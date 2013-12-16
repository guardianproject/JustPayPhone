package info.guardianproject.justpayphone.utils;

import java.io.File;
import java.util.List;

import org.witness.informacam.InformaCam;
import org.witness.informacam.intake.Intake;
import org.witness.informacam.models.j3m.IDCIMEntry;
import org.witness.informacam.models.j3m.IExif;
import org.witness.informacam.utils.BackgroundProcessor;
import org.witness.informacam.utils.Constants.Codes;
import org.witness.informacam.utils.Constants.Logger;
import org.witness.informacam.utils.Constants.Models;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;

public class SelfieIntake extends Intake {
	public SelfieIntake() {
		super();
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Logger.d(LOG, "onHandleIntent called");

		queue = new BackgroundProcessor();
		new Thread(queue).start();
		
		long timeOffset = intent.getLongExtra(Codes.Extras.TIME_OFFSET, 0L);
		String[] cacheFiles = intent.getStringArrayExtra(Codes.Extras.INFORMA_CACHE);
		String logParent = intent.getStringExtra(Codes.Extras.MEDIA_PARENT);
		IDCIMEntry entry = (IDCIMEntry) intent.getSerializableExtra(Codes.Extras.RETURNED_MEDIA);
		
		queue.add(new SelfieEntryJob(queue, entry, logParent, cacheFiles, timeOffset));
		queue.numProcessing++;
		queue.stop();
		
		
	}
	
	public static void processFile(String filePath, String parent)
	{
		InformaCam informaCam;
		File tempFile = new File(filePath);
				
		informaCam = InformaCam.getInstance();
		long timeOffset = informaCam.informaService.getTimeOffset();
			
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
		selfieIntake.putExtra(Codes.Extras.MEDIA_PARENT, parent);
		selfieIntake.putExtra(Codes.Extras.RETURNED_MEDIA, entry);

		informaCam.startService(selfieIntake);
	}
}
