package info.guardianproject.justpayphone.utils;

import org.witness.informacam.intake.EntryJob;
import org.witness.informacam.models.j3m.IDCIMEntry;
import org.witness.informacam.utils.BackgroundProcessor;

public class SelfieEntryJob extends EntryJob {

	public SelfieEntryJob(BackgroundProcessor backgroundProcessor, IDCIMEntry entry, String parentId, String[] informaCache, long timeOffset) {
		super(backgroundProcessor, entry, parentId, informaCache, timeOffset);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected boolean onStart() {
		analyze();
		return true;
	}
	
	private void analyze() {
		
	}
	
	private void parseExif() {
		
	}
	
	private void parseThumbnails() {
		
	}

}
