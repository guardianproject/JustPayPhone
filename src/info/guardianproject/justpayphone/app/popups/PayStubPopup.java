package info.guardianproject.justpayphone.app.popups;

import java.util.Random;

import org.witness.informacam.models.media.ILog;
import org.witness.informacam.utils.TimeUtility;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.models.JPPWorkSummary;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class PayStubPopup extends Popup implements OnClickListener {
	Button previousDay, nextDay;
	TableLayout currentDayStatsHolder;
	TextView currentDate, weeklyTotal;
	
	JPPWorkSummary workSummary;
		
	public PayStubPopup(Activity a) {
		super(a, R.layout.popup_paystub);
		
		previousDay = (Button) layout.findViewById(R.id.paystub_previous_day);
		previousDay.setOnClickListener(this);
		
		nextDay = (Button) layout.findViewById(R.id.paystub_next_day);
		nextDay.setOnClickListener(this);
		
		currentDate = (TextView) layout.findViewById(R.id.paystub_current_date);		
		
		currentDayStatsHolder = (TableLayout) layout.findViewById(R.id.paystub_current_stats_holder);
		
		// XXX: THIS IS FAKE DATA
		ILog iLog = new ILog();
		iLog.startTime = System.currentTimeMillis();
		iLog.endTime = iLog.startTime + (60000 * 60 * random(7, 13));
		
		workSummary = new JPPWorkSummary(iLog);
		workSummary.timeForLunch = random(10, 45);
		
		
		setData();
		
		Show();
	}
	
	private void addTableRow(String labelText, String valueText) {
		TableRow tr = (TableRow) LayoutInflater.from(a).inflate(R.layout.extras_paystub_row, null);
		
		TextView label = (TextView) tr.findViewById(R.id.paystub_row_label);
		label.setText(labelText);
		
		TextView value = (TextView) tr.findViewById(R.id.paystub_row_value);
		value.setText(valueText);
		
		currentDayStatsHolder.addView(tr);
	}
	
	private void setData() {
		currentDate.setText(TimeUtility.millisecondsToDayOnly(workSummary.iLog.startTime));
		currentDayStatsHolder.removeAllViewsInLayout();
		
		addTableRow(a.getString(R.string.start_time), TimeUtility.millisecondsToStopwatchTime(workSummary.iLog.startTime));
		addTableRow(a.getString(R.string.end_time), TimeUtility.millisecondsToStopwatchTime(workSummary.iLog.endTime));
		addTableRow(a.getString(R.string.time_for_lunch), a.getString(R.string.x_minutes, workSummary.timeForLunch));
		addTableRow(a.getString(R.string.total_work_time), a.getString(R.string.x_hours, TimeUtility.millisecondsToHours(Math.abs(workSummary.iLog.startTime - workSummary.iLog.endTime))));
		addTableRow(a.getString(R.string.weekly_total), a.getString(R.string.x_hours, random(36, 70)));
	}
	
	public int random(int bottom, int top) {
		Random rn = new Random();
		return bottom + rn.nextInt(top - bottom + 1);
	}

	@Override
	public void onClick(View v) {
		if(v == previousDay) {
			ILog iLog = new ILog();
			iLog.startTime = TimeUtility.minusOneDay(workSummary.iLog.startTime);
			iLog.endTime = iLog.startTime + (1000 * 60 * 60 * random(7, 13));
			
			workSummary = new JPPWorkSummary(iLog);
			workSummary.timeForLunch = random(10, 45);
			setData();
		} else if(v == nextDay) {
			ILog iLog = new ILog();
			iLog.startTime = TimeUtility.plusOneDay(workSummary.iLog.startTime);
			iLog.endTime = iLog.startTime + (1000 * 60 * 60 * random(7, 13));
			
			workSummary = new JPPWorkSummary(iLog);
			workSummary.timeForLunch = random(10, 45);
			setData();
		}
		
	}

}
