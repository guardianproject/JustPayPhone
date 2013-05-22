package info.guardianproject.justpayphone.app.screens;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.app.adapters.ILogGallery;
import info.guardianproject.justpayphone.utils.Constants;
import info.guardianproject.justpayphone.utils.Constants.HomeActivityListener;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.witness.informacam.InformaCam;
import org.witness.informacam.models.media.ILog;
import org.witness.informacam.models.media.IMedia;
import org.witness.informacam.ui.CameraActivity;
import org.witness.informacam.utils.Constants.Codes;
import org.witness.informacam.utils.Constants.App.Camera;
import org.witness.informacam.utils.Constants.Models;
import org.witness.informacam.utils.Constants.Models.IMedia.MimeType;

import android.app.Activity;
import android.content.Intent;
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
	InformaCam informaCam = InformaCam.getInstance();

	ListView iLogList;

	LinearLayout toCamera, toCamcorder;

	private Intent cameraIntent = null;

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

		iLogList = (ListView) rootView.findViewById(R.id.my_workplaces_list_holder);
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

	@SuppressWarnings("unchecked")
	private void updateWorkspaces() {
		List<ILog> iLogs = informaCam.mediaManifest.getAllByType(MimeType.LOG);

		if(iLogs == null) {
			return;
		}

		iLogList.setAdapter(new ILogGallery(iLogs));
	}

	private void initData() {
		updateWorkspaces();


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
				Log.d(LOG, "THIS RETURNS:\n" + data.getStringExtra(Codes.Extras.RETURNED_MEDIA));
				try {
					JSONArray returnedMedia = ((JSONObject) new JSONTokener(data.getStringExtra(Codes.Extras.RETURNED_MEDIA)).nextValue()).getJSONArray("dcimEntries");

					// add to current log's attached media
					IMedia m = new IMedia();
					m.inflate(returnedMedia.getJSONObject(0));
					
					((HomeActivityListener) a).getCurrentLog().attachedMedia.add(m._id);
					((HomeActivityListener) a).persistLog();
					
					// update UI
					updateWorkspaces();
					
				} catch(JSONException e) {
					Log.e(LOG, e.toString());
					e.printStackTrace();
				}
				break;
			}
		}
	}


	@Override
	public void onClick(View v) {
		if(((HomeActivityListener) a).getCurrentLog() == null || ((HomeActivityListener) a).getCurrentLog().has(Models.IMedia.ILog.IS_CLOSED)) {
			Toast.makeText(a, getString(R.string.you_cannot_take_a), Toast.LENGTH_LONG).show();
			return;
		}
		
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
