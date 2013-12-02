package info.guardianproject.justpayphone.app;

import java.util.List;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.utils.SelfieIntake;

import org.witness.informacam.InformaCam;
import org.witness.informacam.models.j3m.IDCIMEntry;
import org.witness.informacam.models.j3m.IExif;
import org.witness.informacam.models.media.IImage;
import org.witness.informacam.ui.SurfaceGrabberActivity;
import org.witness.informacam.utils.BackgroundProcessor;
import org.witness.informacam.utils.Constants.Codes;

import android.app.Activity;
import android.content.Intent;
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
		if (getIntent().getBooleanExtra(info.guardianproject.justpayphone.utils.Constants.Codes.Extras.IS_SIGNING_OUT, false))
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
	public void onPictureTaken(final byte[] data, Camera camera) {
		// TODO save picture!
		new Thread(new Runnable() {
			InformaCam informaCam;
			
			@Override
			public void run() {
				informaCam = InformaCam.getInstance();
				long timeOffset = informaCam.informaService.getTimeOffset();
				
				// save and hash bytes
				IDCIMEntry entry = new IDCIMEntry();
				entry.originalHash = "hash of bytes";
				entry.fileName = "where this is saved";
				entry.timeCaptured = System.currentTimeMillis() + timeOffset;
				
				// inflate exif values
				entry.exif = new IExif();
				
				// make a new entry job
				Intent selfieIntake = new Intent(informaCam, SelfieIntake.class);

				// put extras: timeOffset, caches, logId, entry
				List<String> cacheFiles = informaCam.informaService.getCacheFiles();
				selfieIntake.putExtra(Codes.Extras.INFORMA_CACHE, cacheFiles.toArray(new String[cacheFiles.size()]));
				selfieIntake.putExtra(Codes.Extras.TIME_OFFSET, timeOffset);
				// TODO: selfieIntake.putExtra(Codes.Extras.MEDIA_PARENT, logId);
				selfieIntake.putExtra(Codes.Extras.RETURNED_MEDIA, entry);
				
				InformaCam.getInstance().startService(selfieIntake);
			}
		}).start();
		
		
		//super.onPictureTaken(data, camera);
		this.setResult(Activity.RESULT_OK);
		finish();
	}

	
}
