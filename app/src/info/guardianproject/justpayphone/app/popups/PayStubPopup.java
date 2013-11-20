package info.guardianproject.justpayphone.app.popups;

import java.util.Random;

import org.witness.informacam.InformaCam;
import org.witness.informacam.models.media.ILog;
import org.witness.informacam.utils.Constants.Models.IMedia.MimeType;
import org.witness.informacam.utils.TimeUtility;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.models.JPPWorkSummary;
import android.app.Activity;
import android.util.Log;
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
	TextView currentDate, weeklyTotal, noDataForDay;
	
	JPPWorkSummary workSummary;
	InformaCam informaCam;
	
	long currentTime;
		
	public PayStubPopup(Activity a) {
		super(a, R.layout.popup_paystub);
		
		informaCam = InformaCam.getInstance();
		
		noDataForDay = (TextView) layout.findViewById(R.id.paystub_no_data_for_day);
		
		previousDay = (Button) layout.findViewById(R.id.paystub_previous_day);
		previousDay.setOnClickListener(this);
		
		nextDay = (Button) layout.findViewById(R.id.paystub_next_day);
		nextDay.setOnClickListener(this);
		
		currentDate = (TextView) layout.findViewById(R.id.paystub_current_date);		
		
		currentDayStatsHolder = (TableLayout) layout.findViewById(R.id.paystub_current_stats_holder);
		
		currentTime = informaCam.informaService.getCurrentTime();
		if(currentTime == 0) {
			currentTime = System.currentTimeMillis();
		}
		
		try {
			workSummary = new JPPWorkSummary(new ILog(informaCam.mediaManifest.getByDay(currentTime, MimeType.LOG, 1).get(0)));
		
			setData();
		} catch (IndexOutOfBoundsException e) {
			showNoDataForDay();
		} catch(NullPointerException e) {
			showNoDataForDay();
		}
		currentDate.setText(TimeUtility.millisecondsToDayOnly(currentTime));
		
		Show();
	}
	
	private void showNoDataForDay() {
		currentDayStatsHolder.setVisibility(View.GONE);
		noDataForDay.setVisibility(View.VISIBLE);
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
		currentDayStatsHolder.removeAllViewsInLayout();
		
		addTableRow(a.getString(R.string.start_time), TimeUtility.millisecondsToStopwatchTime(workSummary.iLog.startTime));
		addTableRow(a.getString(R.string.end_time), workSummary.iLog.endTime == 0 ? a.getString(R.string.unknown) : TimeUtility.millisecondsToStopwatchTime(workSummary.iLog.endTime));
		addTableRow(a.getString(R.string.time_for_lunch), workSummary.timeForLunch == 0 ? a.getString(R.string.unknown) : a.getString(R.string.x_minutes, workSummary.timeForLunch));
		addTableRow(a.getString(R.string.total_work_time), workSummary.iLog.endTime == 0 ? a.getString(R.string.unknown) : a.getString(R.string.x_hours, workSummary.totalWorkTime));
		addTableRow(a.getString(R.string.weekly_total), a.getString(R.string.x_hours, random(36, 70)));
		
		currentDayStatsHolder.setVisibility(View.VISIBLE);
		noDataForDay.setVisibility(View.GONE);
	}
	
	public int random(int bottom, int top) {
		Random rn = new Random();
		return bottom + rn.nextInt(top - bottom + 1);
	}

	@Override
	public void onClick(View v) {
		if(v == previousDay) {
			long pDay = TimeUtility.minusOneDay(currentTime);
			
			try {
				workSummary = new JPPWorkSummary(new ILog(informaCam.mediaManifest.getByDay(pDay, MimeType.LOG, 1).get(0)));
				
				setData();
			} catch(NullPointerException e) {
				Log.e(LOG, e.toString());
				e.printStackTrace();
				
				showNoDataForDay();
			}
			
			currentTime = pDay;
			currentDate.setText(TimeUtility.millisecondsToDayOnly(currentTime));
		} else if(v == nextDay) {
			long nDay = TimeUtility.plusOneDay(currentTime);
			
			try {
				workSummary = new JPPWorkSummary(new ILog(informaCam.mediaManifest.getByDay(nDay, MimeType.LOG, 1).get(0)));
			
				setData();
			} catch(NullPointerException e) {
				Log.e(LOG, e.toString());
				e.printStackTrace();
				
				showNoDataForDay();
			}
			
			currentTime = nDay;
			currentDate.setText(TimeUtility.millisecondsToDayOnly(currentTime));
		}
		
	}

}
