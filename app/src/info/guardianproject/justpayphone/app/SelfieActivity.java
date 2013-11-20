package info.guardianproject.justpayphone.app;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.utils.Constants.Codes;

import org.witness.informacam.ui.SurfaceGrabberActivity;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.widget.TextView;

public class SelfieActivity extends SurfaceGrabberActivity {

	public SelfieActivity() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getIntent().getBooleanExtra(Codes.Extras.IS_SIGNING_OUT, false))
		{
			((TextView)findViewById(R.id.tvTakeYourPhoto)).setText(R.string.take_your_photo_to_clock_out);
		}
	}

	@Override
	protected int getLayout()
	{
		return R.layout.activity_selfie;
	}
	
	@Override
	protected int getCameraDirection() {
		return CameraInfo.CAMERA_FACING_FRONT;
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO save picture!
		//super.onPictureTaken(data, camera);
		this.setResult(Activity.RESULT_OK);
		finish();
	}

	
}
