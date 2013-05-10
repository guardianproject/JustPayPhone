package  info.guardianproject.justpayphone;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.witness.informacam.InformaCam;
import org.witness.informacam.InformaCam.LocalBinder;
import org.witness.informacam.informa.PersistentService;
import org.witness.informacam.models.organizations.IInstalledOrganizations;
import org.witness.informacam.models.organizations.IOrganization;
import org.witness.informacam.storage.FormUtility;
import org.witness.informacam.ui.WizardActivity;
import org.witness.informacam.utils.Constants.IManifest;
import org.witness.informacam.utils.Constants.App.Storage.Type;
import org.witness.informacam.utils.InformaCamBroadcaster.InformaCamStatusListener;

import info.guardianproject.justpayphone.app.HomeActivity;
import info.guardianproject.justpayphone.app.KillScreen;
import info.guardianproject.justpayphone.app.LoginActivity;
import info.guardianproject.justpayphone.utils.Constants;
import info.guardianproject.justpayphone.utils.Constants.Codes;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class JustPayPhone extends Activity implements InformaCamStatusListener {
	Intent route;
	int routeCode;
	
	private InformaCam informaCam;
	private ServiceConnection sc;
	private PersistentService ps;
	
	private final static String LOG = Constants.App.Router.LOG;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_camera_waiter);
		sc = new ServiceConnection() {

			@SuppressWarnings("static-access")
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				informaCam = ((LocalBinder) service).getService().getInstance(JustPayPhone.this); 
				
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				informaCam = null;
			}
			
		};
		bindService(new Intent(this, InformaCam.class), sc, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		try {
			informaCam = InformaCam.getInstance(this);
		} catch(NullPointerException e) {
			Log.e(LOG, "informacam has not started again yet");
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private void routeByIntent() {
		route.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		if(routeCode == Codes.Routes.FINISH_SAFELY) {
			finish();
			startActivity(route);
		} else {
			startActivityForResult(route, routeCode);
		}
	}
	
	private void installOrganization() {
		Log.d(LOG, "OK WIZARD COMPLETED!");
		try {
			if(getAssets().list("includedOrganizations").length > 0) {
				List<IOrganization> includedOrganizations = new ArrayList<IOrganization>();
				for(String organizationManifest : getAssets().list("includedOrganizations")) {
					IOrganization organization = new IOrganization();
					organization.inflate((JSONObject) new JSONTokener(
							new String(
									informaCam.ioService.getBytes(
											("includedOrganizations/" + organizationManifest), 
											Type.APPLICATION_ASSET)
									)
							).nextValue());
					
					InputStream is = getResources().openRawResource(R.raw.glsp);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					int size = 0;
					byte[] buf = new byte[1024];
					while((size = is.read(buf, 0, buf.length)) >= 0) {
						baos.write(buf, 0, size);
					}
					is.close();
					
					info.guardianproject.iocipher.File publicKey = new info.guardianproject.iocipher.File("glsp.asc");
					informaCam.ioService.saveBlob(baos.toByteArray(), publicKey);
					
					organization.publicKeyPath = publicKey.getAbsolutePath();
					includedOrganizations.add(organization);
				}

				IInstalledOrganizations installedOrganizations = new IInstalledOrganizations();
				installedOrganizations.organizations = includedOrganizations;
				informaCam.saveState(installedOrganizations, new info.guardianproject.iocipher.File(IManifest.ORGS));
			}
		} catch(IOException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		informaCam = InformaCam.getInstance(this);
		
		if(resultCode == Activity.RESULT_CANCELED) {			
			// modify route so it does not restart the app
			route = new Intent(this, KillScreen.class);
			routeCode = Codes.Routes.FINISH_SAFELY;
			
		} else if(resultCode == Activity.RESULT_OK) {
			route = new Intent(this, HomeActivity.class);
			routeCode = Codes.Routes.HOME;
			
			switch(requestCode) {
			case Codes.Routes.WIZARD:
				installOrganization();
				FormUtility.installIncludedForms(this);
				
				break;
			case Codes.Routes.LOGIN:
				informaCam.startup();
				break;
			case Codes.Routes.HOME:
				Log.d(LOG, "HEY I AM RETURNING HOME");
				
				try {
					if(data != null && data.hasExtra(Codes.Extras.CHANGE_LOCALE)) {
						route.putExtra(Codes.Extras.CHANGE_LOCALE, true);
						break;
					}
					
					if(data.hasExtra(Codes.Extras.LOGOUT_USER) && data.getBooleanExtra(Codes.Extras.LOGOUT_USER, false)) {
						informaCam.attemptLogout();
						break;
					}
				} catch(NullPointerException e) {
					Log.e(LOG, e.toString());
					e.printStackTrace();
				}
				
				route = new Intent(this, KillScreen.class);
				routeCode = Codes.Routes.FINISH_SAFELY;
				
				break;
			}
			
			routeByIntent();
		}		
	}
	
	@Override
	public void onInformaCamStart(Intent intent) {
		Log.d(LOG, "STARTING INFORMACAM ON JustPayPhone!");
		
		int code = intent.getBundleExtra(org.witness.informacam.utils.Constants.Codes.Keys.SERVICE).getInt(org.witness.informacam.utils.Constants.Codes.Extras.MESSAGE_CODE);
		
		switch(code) {
		case org.witness.informacam.utils.Constants.Codes.Messages.Wizard.INIT:
			informaCam.user.isInOfflineMode = true;
			
			route = new Intent(this, WizardActivity.class);
			routeCode = Codes.Routes.WIZARD;
			break;
		case org.witness.informacam.utils.Constants.Codes.Messages.Login.DO_LOGIN:
			route = new Intent(this, LoginActivity.class);
			routeCode = Codes.Routes.LOGIN;
			break;
		case org.witness.informacam.utils.Constants.Codes.Messages.Home.INIT:
			route = new Intent(this, HomeActivity.class);
			routeCode = Codes.Routes.HOME;
			break;
		}
		
		routeByIntent();
	}
	
	@Override
	public void onInformaCamStop(Intent intent) {
		Log.d(LOG, "INFORMA CAM HAS STOPPED! so calling onDestroy (unbinding service)");
		try {
			unbindService(sc);
		} catch(IllegalArgumentException e) {
			Log.d(LOG, "it appears the service is already unbound");
		}
		
		finish();
	}
	
	@Override
	public void onInformaStop(Intent intent) {}
	
	@Override
	public void onInformaStart(Intent intent) {
		
		
	}	
}
