package  info.guardianproject.justpayphone;

import info.guardianproject.justpayphone.app.CameraActivity;
import info.guardianproject.justpayphone.app.HomeActivity;
import info.guardianproject.justpayphone.utils.Constants;
import info.guardianproject.justpayphone.utils.Constants.App.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class JustPayPhone extends Activity {
	Intent init, route;
	int routeCode;
	
	private final static String LOG = Constants.App.Router.LOG;
	private String packageName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		packageName = getClass().getName();
		
		Log.d(LOG, "hello " + packageName);
		
		init = getIntent();
		routeByIntent();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_CANCELED) {
			// TODO: LOG OUT IF PREFERENCES SAY SO
			finish();
		}
	}
	
	private void routeByIntent() {
		Log.d(LOG, "intent is: " + init.getAction());
		
		// TODO: AUTHENTICATION
		
		if(Intent.ACTION_MAIN.equals(init.getAction())) {
			route = new Intent(this, HomeActivity.class);
			routeCode = Home.ROUTE_CODE;
		} else if("android.media.action.IMAGE_CAPTURE".equals(init.getAction())) {
			route = new Intent(this, CameraActivity.class);
			routeCode = Camera.ROUTE_CODE;
		}
		
		this.startActivityForResult(route, routeCode);
	}
}
