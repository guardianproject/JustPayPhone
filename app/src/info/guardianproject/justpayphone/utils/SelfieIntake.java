package info.guardianproject.justpayphone.utils;

import org.witness.informacam.intake.BatchCompleteJob;
import org.witness.informacam.intake.Intake;
import org.witness.informacam.models.j3m.IDCIMEntry;
import org.witness.informacam.utils.BackgroundProcessor;
import org.witness.informacam.utils.Constants.Logger;

import android.content.Intent;

public class SelfieIntake extends Intake {
	long timeOffset;
	String[] cacheFiles;
	String logParent;
	IDCIMEntry entry;
	
	public SelfieIntake() {
		super();
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Logger.d(LOG, "onHandleIntent called");

		queue = new BackgroundProcessor();
		queue.setOnBatchComplete(new BatchCompleteJob(queue));
		new Thread(queue).start();
		
		queue.add(new SelfieEntryJob(queue, entry, logParent, cacheFiles, timeOffset));
		queue.numProcessing++;
		queue.stop();
		
		
	}
}
