package info.guardianproject.justpayphone.app.screens;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.witness.informacam.InformaCam;
import org.witness.informacam.models.forms.IForm;
import org.witness.informacam.models.j3m.IData;
import org.witness.informacam.models.j3m.IRegionData;
import org.witness.informacam.models.media.ILog;
import org.witness.informacam.storage.FormUtility;
import org.witness.informacam.utils.Constants.App;
import org.witness.informacam.utils.Constants.Models;
import org.witness.informacam.utils.InformaCamBroadcaster.InformaCamStatusListener;
import org.witness.informacam.utils.TimeUtility;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.app.popups.KeypadPopup;
import info.guardianproject.justpayphone.utils.Constants.Forms;
import info.guardianproject.justpayphone.utils.Constants.HomeActivityListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class WorkStatusFragment extends Fragment implements OnClickListener, InformaCamStatusListener {
	View rootView;
	Activity a;

	ProgressBar waiter;
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
	boolean timerIsRunning = false;

	InformaCam informaCam = null;

	private final static String LOG = App.LOG;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(li, container, savedInstanceState);

		rootView = li.inflate(R.layout.fragment_home_work_status, null);
		waiter = (ProgressBar) rootView.findViewById(R.id.work_status_waiter);

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
					((HomeActivityListener) a).getCurrentLog().attachedForm.answer(Forms.LunchQuestionnaire.LUNCH_TAKEN);
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
				final int currentNum = Integer.parseInt(getResources().getStringArray(R.array.lunch_minutes_choices)[l]);

				lunchMinutesChoice.setText(lunchMinutes);
				lunchMinutesChoice.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						setSelectedInGroup(lunchMinutesChoices, (Button) v);
						lunchMinutesProxy.setText(String.valueOf(currentNum));
						lunchMinutesChoices[lunchMinutesChoices.length - 1].setText(getString(R.string.other_amount));

						((HomeActivityListener) a).getCurrentLog().attachedForm.answer(Forms.LunchQuestionnaire.LUNCH_MINUTES);
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
								lunchMinutesProxy.setText(currentNum);
								((Button) v).setText(customMinutes + " " + a.getString(R.string.change));

								((HomeActivityListener) a).getCurrentLog().attachedForm.answer(Forms.LunchQuestionnaire.LUNCH_MINUTES);

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
		informaCam = InformaCam.getInstance();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if(((HomeActivityListener) a).getInitFlag()) {
			init();
		}
	}

	private void init() {
		waiter.setVisibility(View.GONE);
		workStatusToggle.setVisibility(View.VISIBLE);
		if(((HomeActivityListener) a).getCurrentLog() != null && !((HomeActivityListener) a).getCurrentLog().has(Models.IMedia.ILog.IS_CLOSED)) {
			isAtWork = true;
		} else {
			isAtWork = false;
		}

		toggleWorkStatus();
	}

	private void initLog() {
		ILog iLog = new ILog();

		iLog.startTime = informaCam.informaService.getCurrentTime();
		iLog._id = iLog.generateId("log_" + System.currentTimeMillis());

		info.guardianproject.iocipher.File rootFolder = new info.guardianproject.iocipher.File(iLog._id);
		if(!rootFolder.exists()) {
			rootFolder.mkdir();
		}

		iLog.rootFolder = rootFolder.getAbsolutePath();
		informaCam.mediaManifest.media.add(iLog);
		informaCam.mediaManifest.save();
		
		((HomeActivityListener) a).setCurrentLog(iLog);

		informaCam.informaService.associateMedia(((HomeActivityListener) a).getCurrentLog());
	}
	
	private void toggleWorkStatus() {
		clockHolder.setVisibility(isAtWork ? View.VISIBLE : View.GONE);
		workStatusToggle.setText(isAtWork ? a.getString(R.string.clock_out) : a.getString(R.string.check_in));

		if(isAtWork) {
			workStatusToggle.setVisibility(View.VISIBLE);

			long currentTime = informaCam.informaService.getCurrentTime();

			timeWorked = (currentTime - ((HomeActivityListener) a).getCurrentLog().startTime);
			
			if(!timerIsRunning) {
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
				timerIsRunning = true;
			}
			
		} else {
			if(timerIsRunning) {
				t.cancel();
				t = null;

				timerIsRunning = false;
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
			
			if(isAtWork) {
				if(((HomeActivityListener) a).getCurrentLog() == null) {
					initLog();
					
				} else {
					try {
						if(!((HomeActivityListener) a).getCurrentLog().getBoolean(Models.IMedia.ILog.IS_CLOSED)) {
							initLog();
							return;
						}
					} catch(JSONException e) {}
					Toast.makeText(a, getString(R.string.you_have_already_logged), Toast.LENGTH_LONG).show();
				}
			} else {
				((HomeActivityListener) a).getCurrentLog().endTime = informaCam.informaService.getCurrentTime();
				
				for(IForm form : FormUtility.getAvailableForms()) {
					if(form.namespace.equals(Forms.LUNCH_QUESTIONNAIRE)) {
						info.guardianproject.iocipher.File formContent = new info.guardianproject.iocipher.File(((HomeActivityListener) a).getCurrentLog().rootFolder, "form");

						((HomeActivityListener) a).getCurrentLog().formPath = formContent.getAbsolutePath();
						((HomeActivityListener) a).getCurrentLog().attachedForm = new IForm(form, a);

						// attach elements to form
						((HomeActivityListener) a).getCurrentLog().attachedForm.associate(lunchTakenProxy, Forms.LunchQuestionnaire.LUNCH_TAKEN);
						((HomeActivityListener) a).getCurrentLog().attachedForm.associate(lunchMinutesProxy, Forms.LunchQuestionnaire.LUNCH_MINUTES);

						break;
					}
				}
				
				lunchQuestionnaire.setVisibility(View.VISIBLE);
			}
			
			toggleWorkStatus();
		} else if(v == lunchQuestionnaireCommit) {
			try {
				info.guardianproject.iocipher.FileOutputStream fos = new info.guardianproject.iocipher.FileOutputStream(((HomeActivityListener) a).getCurrentLog().formPath);			
				((HomeActivityListener) a).getCurrentLog().attachedForm.save(fos);
				
				if(((HomeActivityListener) a).getCurrentLog().data == null) {
					((HomeActivityListener) a).getCurrentLog().data = new IData();
					((HomeActivityListener) a).getCurrentLog().data.regionData = new ArrayList<IRegionData>();
				}
				
				IRegionData regionData = new IRegionData(((HomeActivityListener) a).getCurrentLog().attachedForm, ((HomeActivityListener) a).getCurrentLog().formPath);
				regionData.timestamp = informaCam.informaService.getCurrentTime();
				((HomeActivityListener) a).getCurrentLog().data.regionData.add(regionData);

				((HomeActivityListener) a).persistLog();
				
				informaCam.stopInforma();

			} catch (FileNotFoundException e) {
				Log.e(LOG, e.toString());
				e.printStackTrace();
			}

			isAtWork = false;
			lunchQuestionnaire.setVisibility(View.GONE);
			toggleWorkStatus();

			for(Button b : lunchTakenChoices) {
				b.setBackgroundDrawable(getResources().getDrawable(R.drawable.extras_button_b_unknown));
			}

			for(Button b : lunchMinutesChoices) {
				b.setBackgroundDrawable(getResources().getDrawable(R.drawable.extras_button_b_unknown));
			}
		}

	}

	@Override
	public void onInformaCamStart(Intent intent) {}

	@Override
	public void onInformaCamStop(Intent intent) {}

	@Override
	public void onInformaStop(Intent intent) {
		Log.d(LOG, "LOG IS NOW: " + ((HomeActivityListener) a).getCurrentLog().asJson().toString());
		((HomeActivityListener) a).getCurrentLog().export(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.d(LOG, "MSG DATA: " + msg.getData().toString());
			}
		}, null, true);
		
		((HomeActivityListener) a).setCurrentLog(null);
	}

	@Override
	public void onInformaStart(Intent intent) {
		init();
	}	
}
