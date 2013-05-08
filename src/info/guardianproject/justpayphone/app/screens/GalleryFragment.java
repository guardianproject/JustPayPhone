package info.guardianproject.justpayphone.app.screens;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.app.CameraActivity;
import info.guardianproject.justpayphone.app.adapters.MyWorkspacesListAdapter;
import info.guardianproject.justpayphone.models.JPPWorkspace;
import info.guardianproject.justpayphone.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.witness.informacam.models.j3m.IDCIMEntry;
import org.witness.informacam.models.j3m.ILocation;
import org.witness.informacam.models.media.IMedia;
import org.witness.informacam.utils.Constants.Actions;
import org.witness.informacam.utils.Constants.Codes;
import org.witness.informacam.utils.Constants.Models;
import org.witness.informacam.utils.Constants.App.Camera;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class GalleryFragment extends Fragment implements OnClickListener {
	View rootView;
	Activity a;
	
	List<JPPWorkspace> workspaces;
	ListView workplacesHolder;
	
	LinearLayout toCamera, toCamcorder;

	private Intent cameraIntent = null;
	private ComponentName cameraComponent = null;
	
	Handler h = new Handler();
	
	private final static String LOG = Constants.App.Home.LOG;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(li, container, savedInstanceState);
		
		rootView = li.inflate(R.layout.fragment_user_management_my_workspaces, null);
		
		toCamera = (LinearLayout) rootView.findViewById(R.id.gallery_to_camera);
		toCamera.setOnClickListener(this);
		
		toCamcorder = (LinearLayout) rootView.findViewById(R.id.gallery_to_camcorder);
		toCamcorder.setOnClickListener(this);
		
		workplacesHolder = (ListView) rootView.findViewById(R.id.my_workplaces_list_holder);
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
		initData();
		
	}
	
	private void initData() {
		ILocation location = new ILocation();
		location.geoCoordinates = new float[] {32.2175f, 82.4136f};
		
		List<IMedia> associatedMedia = new ArrayList<IMedia>();
		IMedia media = new IMedia();
		media.bitmapThumb = "images/sample_1.png";
		media.dcimEntry = new IDCIMEntry();
		media.dcimEntry.mediaType = Models.IMedia.MimeType.IMAGE;
		associatedMedia.add(media);
		
		media = new IMedia();
		media.bitmapThumb = "images/sample_2.png";
		media.dcimEntry = new IDCIMEntry();
		media.dcimEntry.mediaType = Models.IMedia.MimeType.IMAGE;
		associatedMedia.add(media);
		
		media = new IMedia();
		media.dcimEntry = new IDCIMEntry();
		media.dcimEntry.mediaType = Models.IMedia.MimeType.LOG;
		associatedMedia.add(media);
		
		JPPWorkspace workspace = new JPPWorkspace();
		workspace.location = location;
		workspace.associatedMedia = (ArrayList<IMedia>) associatedMedia;
		
		workspaces = new Vector<JPPWorkspace>();
		workspaces.add(workspace);
		
		workplacesHolder.setAdapter(new MyWorkspacesListAdapter(workspaces, a));
	}
	
	private void launchCamcorder() {
		cameraIntent = new Intent(a, CameraActivity.class).putExtra(Codes.Extras.CAMERA_TYPE, Camera.Type.CAMCORDER);
		startActivityForResult(cameraIntent, Codes.Routes.IMAGE_CAPTURE);
	}
	
	private void launchCamera() {
		cameraIntent = new Intent(a, CameraActivity.class).putExtra(Codes.Extras.CAMERA_TYPE, Camera.Type.CAMERA);
		startActivityForResult(cameraIntent, Codes.Routes.IMAGE_CAPTURE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK) {
			switch(requestCode) {
			case Codes.Routes.IMAGE_CAPTURE:
				
				// get current log
				
				// add as associated media
				
				// push thumbnails to list
				
				break;
			}
		}
	}
	
	
	@Override
	public void onClick(View v) {
		if(v == toCamera) {
			h.postDelayed(new Runnable() {
				@Override
				public void run() {
					launchCamera();
				}
			}, 100);
		} else if(v == toCamcorder) {
			h.postDelayed(new Runnable() {
				@Override
				public void run() {
					launchCamcorder();
				}
			}, 100);
		}
		
	}

}
