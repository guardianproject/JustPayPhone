package info.guardianproject.justpayphone.app;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.utils.Constants.Codes;

import org.witness.informacam.ui.SurfaceGrabberActivity;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.widget.TextView;

public class CameraActivity extends SurfaceGrabberActivity {

	public CameraActivity() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getLayout()
	{
		return R.layout.activity_camera;
	}
	
	@Override
	protected int getCameraDirection() {
		return CameraInfo.CAMERA_FACING_BACK;
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO save picture!
		//super.onPictureTaken(data, camera);
		this.setResult(Activity.RESULT_OK);
		finish();
	}

	
}
