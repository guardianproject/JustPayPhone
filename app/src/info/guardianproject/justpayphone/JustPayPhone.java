package  info.guardianproject.justpayphone;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.spongycastle.openpgp.PGPException;
import org.witness.informacam.InformaCam;
import org.witness.informacam.crypto.KeyUtility;
import org.witness.informacam.models.organizations.IInstalledOrganizations;
import org.witness.informacam.models.organizations.IOrganization;
import org.witness.informacam.models.utils.ILanguageMap;
import org.witness.informacam.storage.FormUtility;
import org.witness.informacam.utils.Constants.Codes.Messages.Login;
import org.witness.informacam.utils.Constants.IManifest;
import org.witness.informacam.utils.Constants.App.Storage.Type;
import org.witness.informacam.utils.InformaCamBroadcaster.InformaCamStatusListener;

import info.guardianproject.justpayphone.app.HomeActivity;
import info.guardianproject.justpayphone.app.KillScreen;
import info.guardianproject.justpayphone.app.LoginActivity;
import info.guardianproject.justpayphone.app.WizardActivity;
import info.guardianproject.justpayphone.utils.Constants;
import info.guardianproject.justpayphone.utils.Constants.App.Home;
import info.guardianproject.justpayphone.utils.Constants.Codes;
import info.guardianproject.justpayphone.utils.Constants.Codes.Extras;
import info.guardianproject.justpayphone.utils.Constants.Settings;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class JustPayPhone extends Activity implements InformaCamStatusListener {
	Intent route;
	int routeCode;
	
	private InformaCam informaCam;
	private Handler mHandler;
	
	private final static String LOG = Constants.App.Router.LOG;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_camera_waiter);
		
		
		informaCam = (InformaCam)getApplication();

		mHandler = new Handler();
		
		if(getIntent().hasExtra(Codes.Extras.CHANGE_LOCALE) && getIntent().getBooleanExtra(Codes.Extras.CHANGE_LOCALE, false)) {
			onInformaCamStart(getIntent());
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		informaCam = (InformaCam)getApplication();
		informaCam.setStatusListener(this);
		
		Log.d(LOG, "AND HELLO onResume()!!");
		
		try {
			if(route != null) {
				routeByIntent();
			}
			else
			{
				Log.d(LOG, "route is null now, please wait");
				Log.d(LOG, "hasCredentialManager? " + String.valueOf(informaCam.hasCredentialManager()));
				
				if(informaCam.hasCredentialManager()) {
					Log.d(LOG, "NOW ASKING FOR CM STATUS...");
					
					switch(informaCam.getCredentialManagerStatus()) {
					case org.witness.informacam.utils.Constants.Codes.Status.UNLOCKED:
						route = new Intent(this, HomeActivity.class);
						routeCode = Codes.Routes.HOME;
						break;
					case org.witness.informacam.utils.Constants.Codes.Status.LOCKED:
						autoLogin();
						//route = new Intent(this, LoginActivity.class);
						//routeCode = Codes.Routes.LOGIN;
						break;
					}

					routeByIntent();
				}
				else
				{
					Log.d(LOG, "no, not logged in");
				}
			}
	
		} catch(NullPointerException e) {
			Log.e(LOG, e.toString());
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
		if (route != null)
		{	
			route.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			// Any flags to forward?
			if (getIntent().hasExtra(Extras.GO_TO_CALL_LAWYER))
			{
				route.putExtra(Extras.GO_TO_CALL_LAWYER, getIntent().getBooleanExtra(Extras.GO_TO_CALL_LAWYER, false));
				getIntent().removeExtra(Extras.GO_TO_CALL_LAWYER);
			}
			
			if(routeCode == Codes.Routes.FINISH_SAFELY) {
				finish();
				startActivity(route);
			} else {
				startActivityForResult(route, routeCode);
			}
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		informaCam = InformaCam.getInstance();
		
		if(resultCode == Activity.RESULT_CANCELED) {			
			// modify route so it does not restart the app
			route = new Intent(this, KillScreen.class);
			routeCode = Codes.Routes.FINISH_SAFELY;
			
		} else if(resultCode == Activity.RESULT_OK) {
			route = new Intent(this, HomeActivity.class);
			routeCode = Codes.Routes.HOME;
			
			switch(requestCode) {
			case Codes.Routes.WIZARD:
				break;
			case Codes.Routes.LOGIN:
				//informaCam.startup();
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
		else if (resultCode == Activity.RESULT_FIRST_USER)
		{
			if (route != null && data.hasExtra(Codes.Extras.CHANGE_LOCALE) && data.getBooleanExtra(Codes.Extras.CHANGE_LOCALE, false))
			{
				route.putExtra(Codes.Extras.CHANGE_LOCALE, true);
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
			
//			ILanguageMap languageMap = new ILanguageMap();
//			for (int l = 0; l < getResources().getStringArray(R.array.languages_).length; l++)
//			{
//				languageMap.add(getResources().getStringArray(R.array.locales)[l], getResources().getStringArray(R.array.languages_)[l]);
//			}
//			route.putExtra(org.witness.informacam.utils.Constants.Codes.Extras.SET_LOCALES, languageMap);
			
			routeCode = Codes.Routes.WIZARD;
			break;
		case org.witness.informacam.utils.Constants.Codes.Messages.Login.DO_LOGIN:
			//route = new Intent(this, LoginActivity.class);
			//routeCode = Codes.Routes.LOGIN;
			autoLogin();
			return;
		case org.witness.informacam.utils.Constants.Codes.Messages.Home.INIT:
			route = new Intent(this, HomeActivity.class);
			routeCode = Codes.Routes.HOME;
			break;
		}
		
		routeByIntent();
	}
	
	@Override
	public void onInformaCamStop(Intent intent) {
	}
	
	@Override
	public void onInformaStop(Intent intent) {}
	
	@Override
	public void onInformaStart(Intent intent) {
	}	
	
	private void autoLogin()
	{
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				String generatedPwd = prefs.getString(Settings.GENERATED_PWD, null);
				if (generatedPwd != null && informaCam.attemptLogin(generatedPwd)) {	
				}
				else {
					Toast.makeText(JustPayPhone.this, getString(R.string.we_could_not_log), Toast.LENGTH_LONG).show();
				}
			}
		});
	}
}
