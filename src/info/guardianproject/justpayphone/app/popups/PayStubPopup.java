package info.guardianproject.justpayphone.app.popups;

import info.guardianproject.justpayphone.R;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

public class PayStubPopup extends Popup implements OnClickListener {
	Button previousDay, nextDay;
	TableLayout currentDayStatsHolder;
	TextView currentDate, weeklyTotal;
	
	public PayStubPopup(Activity a) {
		super(a, R.layout.popup_paystub);
		
		previousDay = (Button) layout.findViewById(R.id.paystub_previous_day);
		previousDay.setOnClickListener(this);
		
		nextDay = (Button) layout.findViewById(R.id.paystub_next_day);
		nextDay.setOnClickListener(this);
		
		Show();
	}

	@Override
	public void onClick(View v) {
		if(v == previousDay) {
			
		} else if(v == nextDay) {
			
		}
		
	}

}
