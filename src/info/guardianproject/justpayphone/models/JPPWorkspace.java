package info.guardianproject.justpayphone.models;

import java.util.List;

import org.witness.informacam.models.ILocation;
import org.witness.informacam.models.Model;
import org.witness.informacam.models.media.IMedia;
import org.witness.informacam.utils.Constants.Models;

public class JPPWorkspace extends Model {
	public ILocation location = null;
	public List<IMedia> associatedMedia = null;
	
	public int getNumberOfDaysWorkedHere() {
		int days = 0;
		for(IMedia media : associatedMedia) {
			if(media.dcimEntry.mediaType == Models.IMedia.MimeType.LOG) {
				days++;
			}
		}
		
		return days;
	}
}
