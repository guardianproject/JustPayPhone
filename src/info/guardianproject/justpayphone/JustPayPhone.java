package  info.guardianproject.justpayphone;

import org.witness.informacam.InformaCam;
import org.witness.informacam.InformaCam.LocalBinder;
import org.witness.informacam.storage.FormUtility;
import org.witness.informacam.ui.WizardActivity;
import org.witness.informacam.utils.InformaCamBroadcaster.InformaCamStatusListener;

import info.guardianproject.justpayphone.app.CameraActivity;
import info.guardianproject.justpayphone.app.HomeActivity;
import info.guardianproject.justpayphone.app.LoginActivity;
import info.guardianproject.justpayphone.utils.Constants;
import info.guardianproject.justpayphone.utils.Constants.Codes;
import info.guardianproject.justpayphone.utils.Constants.App.*;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class JustPayPhone extends Activity implements InformaCamStatusListener {
	Intent init, route;
	int routeCode;
	
	private InformaCam informaCam;
	private ServiceConnection sc;
	
	private final static String LOG = Constants.App.Router.LOG;
	private String packageName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		packageName = getClass().getName();
		
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
			if(route != null) {
				routeByIntent();
				Log.d(LOG, "we have a route! lets go!");
			} else {
				Log.d(LOG, "route is null now, please wait");
				if(informaCam.isAbsolutelyLoggedIn()) {
					route = new Intent(this, HomeActivity.class);
					routeCode = Codes.Routes.HOME;
					routeByIntent();
				} else {
					Log.d(LOG, "no, not logged in");
				}
			}
			
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
		unbindService(sc);
		super.onDestroy();
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_CANCELED) {
			Log.d(LOG, "finishing with request code " + requestCode);
			finish();
		} else if(resultCode == Activity.RESULT_OK) {
			route = new Intent(this, HomeActivity.class);
			routeCode = Codes.Routes.HOME;
			
			switch(requestCode) {
			case Codes.Routes.WIZARD:
				FormUtility.installIncludedForms(this);
				break;
			case Codes.Routes.CAMERA:
				// TODO?
				
				break;
			case Codes.Routes.LOGIN:
				informaCam.startup();
				break;
			}
			
			routeByIntent();
		}
	}
	
	private void routeByIntent() {
		Log.d(LOG, "intent is: " + init.getAction());
				
		if(Intent.ACTION_MAIN.equals(init.getAction())) {
			route = new Intent(this, HomeActivity.class);
			routeCode = Home.ROUTE_CODE;
		} else if("android.media.action.IMAGE_CAPTURE".equals(init.getAction())) {
			route = new Intent(this, CameraActivity.class);
			routeCode = Camera.ROUTE_CODE;
		}
		
		route.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(route, routeCode);
	}
	
	@Override
	public void onInformaCamStart(Intent intent) {
		Log.d(LOG, "STARTING INFORMACAM ON JustPayPhone!");
		
		int code = intent.getBundleExtra(org.witness.informacam.utils.Constants.Codes.Keys.SERVICE).getInt(org.witness.informacam.utils.Constants.Codes.Extras.MESSAGE_CODE);
		
		switch(code) {
		case org.witness.informacam.utils.Constants.Codes.Messages.Wizard.INIT:			
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
	public void onInformaCamStop(Intent intent) {}
	
	@Override
	public void onInformaStop(Intent intent) {}
	
	@Override
	public void onInformaStart(Intent intent) {
		
		
	}	
}
