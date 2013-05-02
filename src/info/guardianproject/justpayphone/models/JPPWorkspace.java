package info.guardianproject.justpayphone.models;

import java.util.ArrayList;

import org.witness.informacam.models.Model;
import org.witness.informacam.models.j3m.ILocation;
import org.witness.informacam.models.media.IMedia;
import org.witness.informacam.utils.Constants.Models;

public class JPPWorkspace extends Model {
	public ILocation location = null;
	public ArrayList<IMedia> associatedMedia = null;
	
	public int getNumberOfDaysWorkedHere() {
		int days = 0;
		for(IMedia media : associatedMedia) {
			if(media.dcimEntry.mediaType == Models.IMedia.MimeType.LOG) {
				days++;
			}
		}
		
		return days;
	}
	
	public ArrayList<IMedia> getImagesAndVideo() {
		ArrayList<IMedia> imagesAndVideo = new ArrayList<IMedia>();
		for(IMedia media : associatedMedia) {
			if(media.dcimEntry.mediaType != Models.IMedia.MimeType.LOG) {
				imagesAndVideo.add(media);
			}
		}
		
		return imagesAndVideo;
	}
}
