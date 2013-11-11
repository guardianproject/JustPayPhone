package info.guardianproject.justpayphone.app.screens;

import org.witness.informacam.InformaCam;
import org.witness.informacam.utils.Constants.App;
import org.witness.informacam.utils.Constants.Codes;
import org.witness.informacam.utils.Constants.Models;
import org.witness.informacam.utils.InformaCamBroadcaster.InformaCamStatusListener;
import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.app.CameraActivity;
import info.guardianproject.justpayphone.app.SelfieActivity;
import info.guardianproject.justpayphone.utils.Constants.HomeActivityListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

public class TakePhotosFragment extends Fragment implements OnClickListener, InformaCamStatusListener {
	View rootView;
	Activity a;

	InformaCam informaCam = null;
	private View mBtnTakePhotos;

	private final static String LOG = App.LOG;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(li, container, savedInstanceState);

		rootView = li.inflate(R.layout.fragment_take_photos, null);

		mBtnTakePhotos = rootView.findViewById(R.id.btnTakePhotos);
		mBtnTakePhotos.setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);
		this.a = a;
		informaCam = InformaCam.getInstance();
	}
	
	@Override
	public void onClick(View v) {
		if (v == mBtnTakePhotos) {
			
			if(((HomeActivityListener) a).getCurrentLog() == null || ((HomeActivityListener) a).getCurrentLog().has(Models.IMedia.ILog.IS_CLOSED)) {
				Toast.makeText(a, getString(R.string.you_cannot_take_a), Toast.LENGTH_LONG).show();
				return;
			}
			
			Intent surfaceGrabberIntent = new Intent(a, CameraActivity.class);
			startActivityForResult(surfaceGrabberIntent, Codes.Routes.IMAGE_CAPTURE);
		} 
	}

	@Override
	public void onInformaCamStart(Intent intent) {}

	@Override
	public void onInformaCamStop(Intent intent) {}

	@Override
	public void onInformaStop(Intent intent) {
	}

	@Override
	public void onInformaStart(Intent intent) {
	}	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK) {
			switch(requestCode) {
			case Codes.Routes.IMAGE_CAPTURE:
				break;
			}
		}
	}
}
