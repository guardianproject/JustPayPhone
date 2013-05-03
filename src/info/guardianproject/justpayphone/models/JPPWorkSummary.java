package info.guardianproject.justpayphone.models;

import org.witness.informacam.models.Model;
import org.witness.informacam.models.media.ILog;

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
			// TODO
		}
	}

	public void setTimeForLunch() {
		if(iLog != null) {
			// TODO
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
