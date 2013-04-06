package info.guardianproject.justpayphone.app.screens;

import java.util.Iterator;
import java.util.List;

import org.witness.informacam.utils.Constants.Actions;
import org.witness.informacam.utils.Constants.Codes;
import org.witness.informacam.utils.Constants.App.Camera;

import info.guardianproject.justpayphone.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class CameraFragment extends Fragment {
	View rootView;
	Activity a;
	
	private Intent cameraIntent = null;
	private ComponentName cameraComponent = null;
	private boolean doInit = false;
	
	private final static String LOG = Camera.LOG;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(li, container, savedInstanceState);
		
		rootView = li.inflate(R.layout.fragment_home_camera_chooser, null);
		
		return rootView;
	}
	
	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);
		this.a = a;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		try {
			Iterator<String> i = savedInstanceState.keySet().iterator();
			while(i.hasNext()) {
				String outState = i.next();
				if(outState.equals(Camera.TAG) && savedInstanceState.getBoolean(Camera.TAG)) {
					doInit = false;
				}
			}
		} catch(NullPointerException e) {}

		if(doInit) {
			init();
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(Camera.TAG, true);

		super.onSaveInstanceState(outState);
	}
	
	private void init() {
		List<ResolveInfo> resolveInfo = a.getPackageManager().queryIntentActivities(new Intent(Actions.CAMERA), 0);

		for(ResolveInfo ri : resolveInfo) {
			String packageName = ri.activityInfo.packageName;
			String name = ri.activityInfo.name;
			
			Log.d(LOG, "found camera app: " + packageName);

			if(Camera.SUPPORTED.indexOf(packageName) >= 0) {
				cameraComponent = new ComponentName(packageName, name);
				break;
			}
		}

		if(resolveInfo.isEmpty() || cameraComponent == null) {
			Toast.makeText(a, getString(R.string.could_not_find_any_camera_activity), Toast.LENGTH_LONG).show();
		}

		cameraIntent = new Intent(Camera.Intents.CAMERA);
		cameraIntent.setComponent(cameraComponent);
		startActivityForResult(cameraIntent, Codes.Routes.IMAGE_CAPTURE);
	}
}
