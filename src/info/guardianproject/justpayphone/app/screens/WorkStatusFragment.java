package info.guardianproject.justpayphone.app.screens;

import java.util.Timer;
import java.util.TimerTask;

import org.witness.informacam.utils.Constants.App;
import org.witness.informacam.utils.TimeUtility;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.app.popups.KeypadPopup;
import info.guardianproject.justpayphone.app.popups.TextareaPopup;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WorkStatusFragment extends Fragment implements OnClickListener {
	View rootView;
	Activity a;

	Button workStatusToggle, lunchQuestionnaireCommit;
	LinearLayout clockHolder, lunchQuestionnaire;
	EditText lunchMinutes;

	Timer t;
	TimerTask tt;
	Handler h = new Handler();

	TextView timeAtWork;
	long timeWorked = 0;
	boolean isAtWork = false;

	private final static String LOG = App.LOG;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(li, container, savedInstanceState);

		rootView = li.inflate(R.layout.fragment_home_work_status, null);
		workStatusToggle = (Button) rootView.findViewById(R.id.work_status_toggle);
		workStatusToggle.setOnClickListener(this);

		clockHolder = (LinearLayout) rootView.findViewById(R.id.work_status_clock_holder);
		timeAtWork = (TextView) rootView.findViewById(R.id.work_status_time_at_work);
		
		lunchQuestionnaire = (LinearLayout) rootView.findViewById(R.id.work_status_lunch_questionnaire);
		lunchQuestionnaireCommit = (Button) rootView.findViewById(R.id.odk_commit);
		lunchQuestionnaireCommit.setOnClickListener(this);
		
		lunchMinutes = (EditText) rootView.findViewById(R.id.lunch_minutes_answerHolder);
		lunchMinutes.setText(a.getString(R.string.x_minutes, 0));
		lunchMinutes.setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);
		this.a = a;		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initLayout();
	}

	private void initLayout() {
		toggleWorkStatus(true);
	}

	private void toggleWorkStatus() {
		toggleWorkStatus(false);
	}

	private void toggleWorkStatus(boolean isStarting) {
		clockHolder.setVisibility(isAtWork ? View.VISIBLE : View.GONE);
		workStatusToggle.setText(isAtWork ? a.getString(R.string.clock_out) : a.getString(R.string.check_in));
		lunchQuestionnaire.setVisibility((!isAtWork && !isStarting) ? View.VISIBLE : View.GONE);
		
		if(isAtWork) {
			workStatusToggle.setVisibility(View.VISIBLE);
			
			timeWorked = 0;
			lunchMinutes.setText(a.getString(R.string.x_minutes, 0));
			
			t = new Timer();
			t.schedule(new TimerTask() {
				@Override
				public void run() {
					if(isAtWork) {
						timeWorked += 1000;
						h.post(new Runnable() {
							@Override
							public void run() {
								timeAtWork.setText(WorkStatusFragment.this.a.getString(R.string.at_work_x, TimeUtility.millisecondsToTimestamp(timeWorked)));
							}
						});
					}
				}
			}, 0L, 1000);
		} else {
			if(!isStarting) {
				t.cancel();
				t = null;
				
				workStatusToggle.setVisibility(View.GONE);
			} else {
				workStatusToggle.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if(v == workStatusToggle) {
			isAtWork = !isAtWork;
			toggleWorkStatus();
		} else if(v == lunchQuestionnaireCommit) {
			isAtWork = false;
			toggleWorkStatus(true);
		} else if(v == lunchMinutes) {
			new KeypadPopup(a, null, R.string.x_minutes) {
				@Override
				public void cancel() {
					lunchMinutes.setText(a.getString(R.string.x_minutes, Integer.parseInt(currentNum)));
					super.cancel();
				}
			};
		}

	}	
}
