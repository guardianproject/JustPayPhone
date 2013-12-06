package info.guardianproject.justpayphone.utils;

import java.io.File;

import org.witness.informacam.intake.EntryJob;
import org.witness.informacam.models.j3m.IDCIMEntry;
import org.witness.informacam.utils.BackgroundProcessor;
import org.witness.informacam.utils.Constants.Codes;
import org.witness.informacam.utils.Constants.InformaCamEventListener;

import android.os.Bundle;
import android.os.Message;

public class SelfieEntryJob extends EntryJob {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1030318969728442436L;
	
	private String mTempFile;
	private String mParentId;

	public SelfieEntryJob(BackgroundProcessor backgroundProcessor, IDCIMEntry entry, String parentId, String[] informaCache, long timeOffset) {
		super(backgroundProcessor, entry, parentId, informaCache, timeOffset);
		mTempFile = entry.fileName;
		mParentId = parentId;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		// Remove the temp file
		File file = new File(mTempFile);
		file.delete();
		
		Bundle data = new Bundle();
		data.putInt(Codes.Extras.MESSAGE_CODE, Codes.Messages.DCIM.ADD);
		data.putString(Codes.Extras.MEDIA_PARENT, mParentId);
		data.putString(info.guardianproject.justpayphone.utils.Constants.Codes.Extras.PATH_TO_FILE, mTempFile);
		Message message = new Message();
		message.setData(data);

		InformaCamEventListener mListener = informaCam.getEventListener();
		if (mListener != null) {
			mListener.onUpdate(message);
		}
	}

	@Override
	protected void commit() {
		// Need to null out uri, it's not a real source uri and can't be deleted by ContentResolver
		entry.uri = null;
		super.commit();
	}
	
	
}
