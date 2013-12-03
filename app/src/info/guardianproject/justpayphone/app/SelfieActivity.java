package info.guardianproject.justpayphone.app;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.utils.SelfieIntake;

import org.witness.informacam.InformaCam;
import org.witness.informacam.models.j3m.IDCIMEntry;
import org.witness.informacam.models.j3m.IExif;
import org.witness.informacam.ui.SurfaceGrabberActivity;
import org.witness.informacam.utils.MediaHasher;
import org.witness.informacam.utils.Constants.Codes;
import org.witness.informacam.utils.Constants.Models;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class SelfieActivity extends SurfaceGrabberActivity {

	private String mFilePrefix;

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
		mFilePrefix = "selfie";
		if (getIntent().hasExtra(info.guardianproject.justpayphone.utils.Constants.Codes.Extras.FILE_PREFIX))
			mFilePrefix = getIntent().getStringExtra(info.guardianproject.justpayphone.utils.Constants.Codes.Extras.FILE_PREFIX);
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
		new Thread(new Runnable() {
			InformaCam informaCam;

			@Override
			public void run() {
				try {
					informaCam = InformaCam.getInstance();
					long timeOffset = informaCam.informaService.getTimeOffset();

					File outputDir = getCacheDir(); // context being the Activity pointer
					File tempFile = File.createTempFile(mFilePrefix, ".jpg", outputDir);
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempFile));
					bos.write(data);
					bos.flush();
					bos.close();
					
					IDCIMEntry entry = new IDCIMEntry();
					entry.fileName = tempFile.getAbsolutePath();
					entry.timeCaptured = System.currentTimeMillis()
							+ timeOffset;
					entry.uri = Uri.fromFile(tempFile).toString();
					entry.name = tempFile.getName();
					entry.authority = ContentResolver.SCHEME_FILE;
					entry.mediaType = Models.IMedia.MimeType.IMAGE;
						
					// inflate exif values
					entry.exif = new IExif();

					// make a new entry job
					Intent selfieIntake = new Intent(informaCam, SelfieIntake.class);

					// put extras: timeOffset, caches, logId, entry
					List<String> cacheFiles = informaCam.informaService.getCacheFiles();
					selfieIntake.putExtra(Codes.Extras.INFORMA_CACHE, cacheFiles.toArray(new String[cacheFiles.size()]));
					selfieIntake.putExtra(Codes.Extras.TIME_OFFSET, timeOffset);
					if (getIntent().hasExtra(Codes.Extras.MEDIA_PARENT))
						selfieIntake.putExtra(Codes.Extras.MEDIA_PARENT, getIntent().getStringExtra(Codes.Extras.MEDIA_PARENT));
					selfieIntake.putExtra(Codes.Extras.RETURNED_MEDIA, entry);

					InformaCam.getInstance().startService(selfieIntake);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

		this.setResult(Activity.RESULT_OK);
		finish();
	}

	
}
