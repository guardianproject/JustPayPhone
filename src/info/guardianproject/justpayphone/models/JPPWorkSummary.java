package info.guardianproject.justpayphone.models;

import info.guardianproject.justpayphone.utils.Constants.Forms;

import org.json.JSONException;
import org.witness.informacam.models.Model;
import org.witness.informacam.models.media.ILog;
import org.witness.informacam.utils.TimeUtility;

import android.util.Log;

public class JPPWorkSummary extends Model {
	public ILog iLog = null;

	public int timeForLunch = 0;
	public int totalWorkTime = 0;
	public int weeklyTotal = 0;

	public JPPWorkSummary() {
		super();
	}

	public JPPWorkSummary(ILog iLog) {
		super();

		this.iLog = iLog;
		setTimeForLunch();
		setWeeklyTotal();
		setTotalWorkTime();
	}

	public void setTotalWorkTime() {
		if(iLog != null) {
			if(iLog.endTime != 0) {
				totalWorkTime = TimeUtility.millisecondsToHours(Math.abs(iLog.startTime - iLog.endTime));
			}
		}
	}

	public void setTimeForLunch() {
		if(iLog != null) {
			try {
				timeForLunch = iLog.data.regionData.get(0).metadata.getInt(Forms.LunchQuestionnaire.LUNCH_MINUTES);
			} catch (JSONException e) {
				Log.e(LOG, e.toString());
				e.printStackTrace();
			} catch(NullPointerException e) {
				Log.e(LOG, e.toString());
				e.printStackTrace();
			}
		}
	}

	public void setWeeklyTotal() {
		if(iLog != null) {
			// TODO
			// get the day of the week it is

			// TODO
			// get the logs of the days surrounding
		}
	}
}
