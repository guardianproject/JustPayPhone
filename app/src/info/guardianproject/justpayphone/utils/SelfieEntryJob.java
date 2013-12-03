package info.guardianproject.justpayphone.utils;

import java.io.File;

import org.witness.informacam.intake.EntryJob;
import org.witness.informacam.models.j3m.IDCIMEntry;
import org.witness.informacam.utils.BackgroundProcessor;

public class SelfieEntryJob extends EntryJob {

	private String mTempFile;

	public SelfieEntryJob(BackgroundProcessor backgroundProcessor, IDCIMEntry entry, String parentId, String[] informaCache, long timeOffset) {
		super(backgroundProcessor, entry, parentId, informaCache, timeOffset);
		mTempFile = entry.fileName;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		// Remove the temp file
		File file = new File(mTempFile);
		file.delete();
	}

	@Override
	protected void commit() {
		// Need to null out uri, it's not a real source uri and can't be deleted by ContentResolver
		entry.uri = null;
		super.commit();
	}
	
	
}
