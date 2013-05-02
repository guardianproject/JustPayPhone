package info.guardianproject.justpayphone.app.screens;

import java.util.Timer;
import java.util.TimerTask;

import org.witness.informacam.utils.Constants.App;
import org.witness.informacam.utils.TimeUtility;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.app.popups.KeypadPopup;
import info.guardianproject.justpayphone.app.popups.TextareaPopup;
import android.annotation.SuppressLint;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class WorkStatusFragment extends Fragment implements OnClickListener {
	View rootView;
	Activity a;

	Button workStatusToggle, lunchQuestionnaireCommit;
	LinearLayout clockHolder, lunchQuestionnaire, lunchMinutesChoiceRoot, lunchTakenChoiceRoot;
	RadioGroup lunchTakenProxy;
	EditText lunchMinutesProxy;
	
	Button[] lunchMinutesChoices, lunchTakenChoices;

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
		
		lunchTakenProxy = (RadioGroup) rootView.findViewById(R.id.lunch_taken_proxy);
		lunchTakenChoiceRoot = (LinearLayout) rootView.findViewById(R.id.lunch_taken_choice_root);
		lunchTakenChoices = new Button[lunchTakenChoiceRoot.getChildCount()];
		for(int l=0; l<lunchTakenChoices.length; l++) {
			Button lunchTakenChoice = (Button) lunchTakenChoiceRoot.getChildAt(l);
			final RadioButton rb = (RadioButton) ((RadioGroup) lunchTakenProxy).getChildAt(l);
			lunchTakenChoice.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					setSelectedInGroup(lunchTakenChoices, (Button) v);
					for(int r=0; r< lunchTakenProxy.getChildCount(); r++) {
						((RadioButton) lunchTakenProxy.getChildAt(r)).setChecked(false);
					}
					
					rb.setChecked(true);
					
				}
				
			});
			
			lunchTakenChoices[l] = lunchTakenChoice;
		}
				
		lunchMinutesChoiceRoot = (LinearLayout) rootView.findViewById(R.id.lunch_minutes_choice_root);
		lunchMinutesChoices = new Button[lunchMinutesChoiceRoot.getChildCount()];
		for(int l=0; l<lunchMinutesChoices.length; l++) {
			Button lunchMinutesChoice = (Button) lunchMinutesChoiceRoot.getChildAt(l);
			
			if(l != (lunchMinutesChoices.length - 1)) {
				final String lunchMinutes = getString(R.string.x_minutes, Integer.parseInt(getResources().getStringArray(R.array.lunch_minutes_choices)[l]));
				lunchMinutesChoice.setText(lunchMinutes);
				lunchMinutesChoice.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						setSelectedInGroup(lunchMinutesChoices, (Button) v);
						lunchMinutesProxy.setText(lunchMinutes);
						lunchMinutesChoices[lunchMinutesChoices.length - 1].setText(getString(R.string.other_amount));
					}
					
				});
			} else {
				lunchMinutesChoice.setText(getString(R.string.other_amount));
				lunchMinutesChoice.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(final View v) {
						new KeypadPopup(a, null, R.string.x_minutes) {
							@Override
							public void cancel() {
								setSelectedInGroup(lunchMinutesChoices, (Button) v);
								
								String customMinutes = a.getString(R.string.x_minutes, Integer.parseInt(currentNum));
								lunchMinutesProxy.setText(customMinutes);
								((Button) v).setText(customMinutes + " " + a.getString(R.string.change));
								super.cancel();
							}
						};
						
					}
					
				});
			}
			
			lunchMinutesChoices[l] = lunchMinutesChoice;
		}
		
		
		lunchMinutesProxy = (EditText) rootView.findViewById(R.id.lunch_minutes_proxy);
		lunchMinutesProxy.setText(a.getString(R.string.x_minutes, 0));

		return rootView;
	}
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void setSelectedInGroup(Button[] buttonGroup, Button selectedButton) {
		for(Button b : buttonGroup) {
			if(b != selectedButton) {
				b.setBackgroundDrawable(getResources().getDrawable(R.drawable.extras_button_b_selected));
			} else {
				b.setBackgroundDrawable(getResources().getDrawable(R.drawable.extras_button_b_unselected));
			}
		}
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
			lunchMinutesProxy.setText(a.getString(R.string.x_minutes, 0));
			
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

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		if(v == workStatusToggle) {
			isAtWork = !isAtWork;
			toggleWorkStatus();
		} else if(v == lunchQuestionnaireCommit) {
			isAtWork = false;
			toggleWorkStatus(true);
			
			for(Button b : lunchTakenChoices) {
				b.setBackgroundDrawable(getResources().getDrawable(R.drawable.extras_button_b_unknown));
			}
			
			for(Button b : lunchMinutesChoices) {
				b.setBackgroundDrawable(getResources().getDrawable(R.drawable.extras_button_b_unknown));
			}
		}

	}	
}
