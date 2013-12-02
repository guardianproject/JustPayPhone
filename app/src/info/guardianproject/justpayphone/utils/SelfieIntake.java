package info.guardianproject.justpayphone.utils;

import org.witness.informacam.intake.Intake;
import org.witness.informacam.models.j3m.IDCIMEntry;
import org.witness.informacam.utils.BackgroundProcessor;
import org.witness.informacam.utils.Constants.Codes;
import org.witness.informacam.utils.Constants.Logger;

import android.content.Intent;

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
}
