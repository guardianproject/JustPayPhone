package info.guardianproject.justpayphone.app;


import info.guardianproject.justpayphone.JustPayPhone;
import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.app.screens.CallLawyerFragment;
import info.guardianproject.justpayphone.app.screens.GalleryFragment;
import info.guardianproject.justpayphone.app.screens.TakePhotosFragment;
import info.guardianproject.justpayphone.app.screens.UserManagementFragment;
import info.guardianproject.justpayphone.app.screens.WorkStatusFragment;
import info.guardianproject.justpayphone.app.views.DottedProgressView;
import info.guardianproject.justpayphone.utils.Constants;
import info.guardianproject.justpayphone.utils.Constants.App.Home;
import info.guardianproject.justpayphone.utils.Constants.Codes.Extras;
import info.guardianproject.justpayphone.utils.Constants.Forms;
import info.guardianproject.justpayphone.utils.Constants.HomeActivityListener;
import info.guardianproject.justpayphone.utils.Constants.Settings;
import info.guardianproject.odkparser.utils.QD;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import org.json.JSONException;
import org.witness.informacam.InformaCam;
import org.witness.informacam.models.forms.IForm;
import org.witness.informacam.models.media.IAsset;
import org.witness.informacam.models.media.ILog;
import org.witness.informacam.models.media.IRegion;
import org.witness.informacam.models.notifications.INotification;
import org.witness.informacam.models.organizations.IOrganization;
import org.witness.informacam.storage.FormUtility;
import org.witness.informacam.utils.Constants.App.Camera;
import org.witness.informacam.utils.Constants.Codes;
import org.witness.informacam.utils.Constants.InformaCamEventListener;
import org.witness.informacam.utils.Constants.Models;
import org.witness.informacam.utils.Constants.Models.IMedia.MimeType;
import org.witness.informacam.utils.InformaCamBroadcaster.InformaCamStatusListener;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.Toast;

public class HomeActivity extends FragmentActivity implements HomeActivityListener, InformaCamStatusListener, InformaCamEventListener {
	private final static String LOG = Constants.App.Home.LOG;
	private String lastLocale;

	List<Fragment> fragments = new Vector<Fragment>();
	Fragment userManagementFragment, workStatusFragment, cameraFragment, callLawyerFragment;
	GalleryFragment galleryFragment;
	Fragment currentFragment = null;

	LayoutInflater li;
	ViewPager viewPager;
	TabPager pager;

	Handler h;

	InformaCam informaCam;
	ILog currentLog = null;
	
	boolean initFlag = false;
	private DottedProgressView progressDots;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		informaCam = (InformaCam) getApplication();
		
		setContentView(R.layout.activity_home);

		userManagementFragment = Fragment.instantiate(this, UserManagementFragment.class.getName());
		workStatusFragment = Fragment.instantiate(this, WorkStatusFragment.class.getName());
		cameraFragment = Fragment.instantiate(this, TakePhotosFragment.class.getName());
		callLawyerFragment = Fragment.instantiate(this, CallLawyerFragment.class.getName());
		galleryFragment = (GalleryFragment) Fragment.instantiate(this, GalleryFragment.class.getName());

		fragments.add(workStatusFragment);
		fragments.add(cameraFragment);
		fragments.add(callLawyerFragment);
		//fragments.add(userManagementFragment);
		fragments.add(galleryFragment);

		h = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				Bundle b = msg.getData();
				
			
			}
			
		};

		lastLocale = PreferenceManager.getDefaultSharedPreferences(this).getString(Settings.LANGUAGE, "0");

		// XXX HEY! XXX!
		//InformaCam.getInstance().mediaManifest.media.clear();
		//InformaCam.getInstance().mediaManifest.save();
		
		checkForCrashes();
		checkForUpdates();
		
		checkGoogleAuth ();
	}
	
	private void checkGoogleAuth ()
	{
		
		new AsyncTask <Void, Void, Void>()
		{

			@Override
			protected Void doInBackground(Void... params) {
				AccountManager am = AccountManager.get(HomeActivity.this);
				Account[] accounts = AccountManager.get(HomeActivity.this).getAccountsByType("com.google");
				
				if (accounts.length > 0)
				{
						
						
						am.getAuthToken(accounts[0], Models.ITransportStub.GoogleDrive.SCOPE, null, HomeActivity.this, new AccountManagerCallback<Bundle> () {

							@Override
							public void run(AccountManagerFuture<Bundle> result) {
					            try {
									final String token = result.getResult().getString(AccountManager.KEY_AUTHTOKEN);
								} catch (OperationCanceledException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (AuthenticatorException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
							
							
						}, null);
						
						//String scope = Models.ITransportStub.GoogleDrive.SCOPE;
						//GoogleAuthUtil.getToken(HomeActivity.this, accounts[0].name, scope,new Bundle());
				
				}
				else
				{
					//todo show an error
				}
				return null;
			}
			
		}.execute();
		
		


	}
	
	int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 9999;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR) {
        	 if (resultCode == RESULT_OK) {
        		 checkGoogleAuth ();
                 return;
             }
             if (resultCode == RESULT_CANCELED) {
                 Toast.makeText(this, "User rejected authorization.", Toast.LENGTH_SHORT).show();
                 return;
             }

            
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


	@Override
	public void onResume() {
		super.onResume();
		
		informaCam.setStatusListener(this);
		informaCam.setEventListener(this);
		
		String currentLocale = PreferenceManager.getDefaultSharedPreferences(this).getString(Settings.LANGUAGE, "0");
		if(!lastLocale.equals(currentLocale)) {
			setNewLocale(currentLocale);
			return;
		}

		initLayout();

		if(informaCam.informaService == null) {
			informaCam.startInforma();
		} else {
			if(!getIntent().getBooleanExtra(Constants.Codes.Extras.CHANGE_LOCALE, false)) {
				try {
					((InformaCamStatusListener) currentFragment).onInformaStart(null);
				} catch(ClassCastException e) {}
			} else {
				initFlag = true;
			}
			
			getIntent().putExtra(Constants.Codes.Extras.CHANGE_LOCALE, false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.jpp_about:
			// TODO: we'll have an about page
			break;
		case R.id.jpp_preferences:
			lastLocale = PreferenceManager.getDefaultSharedPreferences(this).getString(Settings.LANGUAGE, "0");
			startActivity(new Intent(this, PreferencesActivity.class));

			break;
		case R.id.jpp_logout:
			informaCam.mediaManifest.save();
			setResult(Activity.RESULT_OK, new Intent().putExtra(Codes.Extras.LOGOUT_USER, true));
			finish();

			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void setNewLocale(String locale_code) {
		Configuration configuration = new Configuration();
		switch(Integer.parseInt(locale_code)) {
		case Settings.Locales.DEFAULT:
			configuration.locale = new Locale(Locale.getDefault().getLanguage());
			break;
		case Settings.Locales.EN:
			configuration.locale = new Locale("en");
			break;
		case Settings.Locales.ES:
			configuration.locale = new Locale("es");
			break;
		}
		getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

		getIntent().putExtra(Constants.Codes.Extras.CHANGE_LOCALE, true);
		setResult(Activity.RESULT_OK, new Intent().putExtra(Constants.Codes.Extras.CHANGE_LOCALE, true));
		finish();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(Constants.Codes.Extras.HAS_SEEN_HOME, true);
		outState.putInt(Home.Tabs.LAST, viewPager.getCurrentItem());

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStop() {

		super.onStop();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if(!getIntent().getBooleanExtra(Constants.Codes.Extras.CHANGE_LOCALE, false)) {
			if(informaCam.informaService != null) {
				informaCam.stopInforma();
			}
		}
		super.onDestroy();
	}

	@SuppressWarnings("deprecation")
	public int[] getDimensions() {
		Display display = getWindowManager().getDefaultDisplay();
		return new int[] {display.getWidth(),display.getHeight()};
	}

	@Override
	public void onBackPressed() {
		// if we have a log going...
		if(currentLog != null && !currentLog.shouldAutoLog) {
			Log.d(LOG, "FUCKING CURRENT LOG: " + currentLog.asJson().toString());
			currentLog.save();
		}

		setResult(Activity.RESULT_OK);
		finish();
	}

	private void initLayout() {
		pager = new TabPager(getSupportFragmentManager());

		progressDots = (DottedProgressView) findViewById(R.id.progress_dots);

		viewPager = (ViewPager) findViewById(R.id.view_pager_root);
		viewPager.setAdapter(pager);
		viewPager.setOnPageChangeListener(pager);


		li = LayoutInflater.from(this);

		// Any flags telling us to go somewhere?
		if (getIntent().hasExtra(Extras.GO_TO_CALL_LAWYER))
		{
			boolean goToLawyer = getIntent().getBooleanExtra(Extras.GO_TO_CALL_LAWYER, false);
			if (goToLawyer)
			{
				Log.d(LOG, "Got instruction to go to call lawyer page.");
				currentFragment = callLawyerFragment;
			}
			getIntent().removeExtra(Extras.GO_TO_CALL_LAWYER);
		}
		
		
		if(currentFragment == null) {
			viewPager.setCurrentItem(0);
			currentFragment = fragments.get(0);
		} else {
			viewPager.setCurrentItem(fragments.indexOf(currentFragment));
		}
		
		progressDots.setNumberOfDots(pager.getCount());
		progressDots.setCurrentDot(viewPager.getCurrentItem());
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		try {
			Iterator<String> i = savedInstanceState.keySet().iterator();
			while(i.hasNext()) {
				String outState = i.next();

				if(outState.equals(Camera.TAG) && savedInstanceState.getBoolean(Camera.TAG)) {
					Log.d(LOG, "we saw camera: " + String.valueOf(outState));
				} else if(outState.equals(Home.Tabs.LAST)){
					Log.d(LOG, "we should move to tab #" + outState);
				}
			}
		} catch(NullPointerException e) {}

		super.onRestoreInstanceState(savedInstanceState);
	}

	public void swapLayout(Fragment fragment, int layoutRoot) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(layoutRoot, fragment);
		ft.addToBackStack(null);
		ft.commit();
		pager.notifyDataSetChanged();
	}

	class TabPager extends FragmentStatePagerAdapter implements TabHost.OnTabChangeListener, OnPageChangeListener {

		public TabPager(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public void onTabChanged(String tabId) {
			int i=0;
			for(Fragment f : fragments) {
				if(f.getClass().getName().equals(tabId)) {
					viewPager.setCurrentItem(i);
					break;
				}

				i++;
			} 
		}

		@Override
		public void onPageScrollStateChanged(int state) {}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {}

		@Override
		public void onPageSelected(int page) {
			currentFragment = fragments.get(page);
			if(currentFragment.equals(workStatusFragment)) {
				((InformaCamStatusListener) currentFragment).onInformaStart(null);
			}

			Log.d(LOG, "setting current page as " + page);
			progressDots.setNumberOfDots(getCount());
			progressDots.setCurrentDot(page);
		}

		@Override
		public Fragment getItem(int which) {
			return fragments.get(which);
		}


		@Override
		public int getCount() {
			return fragments.size();
		}

	}

	@Override
	public void onInformaCamStart(Intent intent) {
		try {
			((InformaCamStatusListener) currentFragment).onInformaCamStart(intent);
		} catch(ClassCastException e) {}
	}

	@Override
	public void onInformaCamStop(Intent intent) {
		try {
			((InformaCamStatusListener) currentFragment).onInformaCamStop(intent);
		} catch(ClassCastException e) {}
	}

	@Override
	public void onInformaStop(Intent intent) {
		try {
			((InformaCamStatusListener) currentFragment).onInformaStop(intent);
		} catch(ClassCastException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public void onInformaStart(Intent intent) {
		try {
			((InformaCamStatusListener) currentFragment).onInformaStart(intent);
		} catch(ClassCastException e) {}
	}

	@Override
	public void persistLog() {
		if(currentLog != null) {
			informaCam.mediaManifest.getById(currentLog._id).inflate(currentLog.asJson());
			informaCam.mediaManifest.save();
		}
	}

	@Override
	public ILog getCurrentLog() {
		if(currentLog == null) {
			try {	
				// Debug code to delete all old logs on startup
//				@SuppressWarnings("unchecked")
//				List<IMedia> media = informaCam.mediaManifest.getAllByType(MimeType.LOG);
//				for (IMedia m : media)
//				{
//					informaCam.mediaManifest.removeMediaItem(m);
//				}
				long currentTime = informaCam.informaService.getCurrentTime();
				currentLog = new ILog(informaCam.mediaManifest.getByDay(currentTime, MimeType.LOG, 1).get(0));
				if(currentLog.endTime != 0) {
					Log.d(LOG, "LOG SHOULD BE CLOSED (endTime: " + currentLog.endTime + ")");
					currentLog.put(Models.IMedia.ILog.IS_CLOSED, true);
				}
			} catch(IndexOutOfBoundsException e) {
				Log.e(LOG, e.toString());
				e.printStackTrace();				
			} catch(NullPointerException e) {
				Log.e(LOG, e.toString());
				e.printStackTrace();
			} catch (JSONException e) {
				Log.e(LOG, e.toString());
				e.printStackTrace();
			}
		}

		return currentLog;
	}

	@Override
	public void setCurrentLog(ILog currentLog) {
		this.currentLog = currentLog;		
	}

	@Override
	public boolean getInitFlag() {
		return initFlag;
	}

	@Override
	public void showLogView(boolean showBubble) {
		galleryFragment.setHighlightFirstLog(showBubble);
		viewPager.setCurrentItem(fragments.indexOf(galleryFragment));
	}

	@Override
	public void showNavigationDots(boolean show) {
		progressDots.setVisibility(show ? View.VISIBLE : View.GONE);
	}
	
	private void checkForCrashes() {
		CrashManager.register(this, JustPayPhone.HOCKEY_APP_ID);
	}

	private void checkForUpdates() {
		// Remove this for store builds!
		UpdateManager.register(this, JustPayPhone.HOCKEY_APP_ID);
	}

	@Override
	public void onUpdate(Message message) {
		int code = message.getData().getInt(Codes.Extras.MESSAGE_CODE);
		if (code == Codes.Messages.DCIM.ADD)
		{
			String logId = message.getData().getString(Codes.Extras.MEDIA_PARENT);
			String filePath = message.getData().getString(info.guardianproject.justpayphone.utils.Constants.Codes.Extras.PATH_TO_FILE);
			if (logId != null && filePath != null)
			{
				ILog log = (ILog) informaCam.mediaManifest.getById(logId);
				if (log != null)
				{
					h.post(new Runnable()
					{
						private ILog log;
						private String filePath;

						public Runnable init(ILog log, String filePath)
						{
							this.log = log;
							this.filePath = filePath;
							return this;
						}
						
						@Override
						public void run() {
							String signInFile = log.optString(info.guardianproject.justpayphone.utils.Constants.Models.IMedia.ILog.SIGN_IN_FILE, null);
							String signOutFile = log.optString(info.guardianproject.justpayphone.utils.Constants.Models.IMedia.ILog.SIGN_OUT_FILE, null);
							
							if (signInFile != null && signInFile.equals(filePath))
								log.remove(info.guardianproject.justpayphone.utils.Constants.Models.IMedia.ILog.SIGN_IN_FILE);
							else if (signOutFile != null && signOutFile.equals(filePath))
								log.remove(info.guardianproject.justpayphone.utils.Constants.Models.IMedia.ILog.SIGN_OUT_FILE);
							log.save();
							checkAndSendLogIfComplete(log);
						}
						
					}.init(log, filePath));
				}
			}
		}
	}
	
	public IForm getLunchForm(ILog iLog, boolean createIfNotFound)
	{
		List<IForm> forms = iLog.getForms(this);
		for(IForm form : forms) {
			if(form.namespace.equals(Forms.LUNCH_QUESTIONNAIRE)) {
				return form;
			}
		}
		
		if (createIfNotFound)
		{
			for(IForm form : FormUtility.getAvailableForms()) {
				if(form.namespace.equals(Forms.LUNCH_QUESTIONNAIRE)) {
					info.guardianproject.iocipher.File formContent = new info.guardianproject.iocipher.File(getCurrentLog().rootFolder, "form");

					IForm lunchForm = new IForm(form, this);
					lunchForm.answerPath = formContent.getAbsolutePath();
					IRegion topRegion = iLog.getTopLevelRegion();
					if (topRegion == null)
						topRegion = iLog.addRegion(this, null);
					topRegion.addForm(lunchForm);
					return lunchForm;
				}
			}
		}
		return null;
	}
	
	public boolean containsLunchInformation(ILog iLog)
	{
		IForm form = getLunchForm(iLog, false);
		if (form != null)
		{
			QD qdLunch = form.getQuestionDefByTitleId(Forms.LunchQuestionnaire.LUNCH_TAKEN);
			if (qdLunch.hasInitialValue)
				return true;
		}
		return false;
	}
	
	public void checkAndSendLogIfComplete(ILog log)
	{
		Log.d(LOG, "Checking log " + (log == null ? "null" : log._id) + " for completion");
		if (log != null && log.startTime != 0 && log.endTime != 0)
		{
			Log.d(LOG, "Log " + log._id + ": start and end set");
			boolean isClosed = log.optBoolean(Models.IMedia.ILog.IS_CLOSED, false);
			String signInFile = log.optString(info.guardianproject.justpayphone.utils.Constants.Models.IMedia.ILog.SIGN_IN_FILE, null);
			String signOutFile = log.optString(info.guardianproject.justpayphone.utils.Constants.Models.IMedia.ILog.SIGN_OUT_FILE, null);
			if (isClosed && signInFile == null && signOutFile == null)
			{
				Log.d(LOG, "Log " + log._id + ": is closed and all files processed");
				if (containsLunchInformation(log))
				{
					Log.d(LOG, "Log " + log._id + ": lunch information stored");
					sendLog(log,false);
				}
			}
		}
	}
	
	public void sendLog(ILog log, final boolean doLocalShare) {
		new Thread(new Runnable() {
			ILog log;

			public Runnable init(ILog log) {
				this.log = log;
				return this;
			}

			@SuppressLint("HandlerLeak")
			@Override
			public void run() {
				Looper.prepare();

				Handler handler = new Handler() {

					private INotification mPreviousTry;
					private INotification mNotification;

					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
						if (msg.what == 0) {
							try {
								mPreviousTry = findNotification();
								
								IOrganization org = informaCam.installedOrganizations.getByName("GLSP");								
								IAsset exportAsset = log.export(HomeActivity.this, h, org, doLocalShare);
								
								if (exportAsset != null) {
									this.sendEmptyMessageDelayed(1, 1000);
									
								}
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
						} else if (msg.what == 1) {
							// Wait for completion.
							INotification n = getNotification();
							if (n == null || (!n.taskComplete && !n.canRetry)) {
								this.sendEmptyMessageDelayed(1, 1000);
							} else {
								// Update the list!
								h.post(new Runnable() {
									@Override
									public void run() {
										galleryFragment.updateList();
									}
								});

								Looper.myLooper().quit();
							}
						}
					}

					private INotification getNotification() {
						if (mNotification == null) {
							// Find the notification to wait on
							List<INotification> notifications = new ArrayList<INotification>(
									informaCam.notificationsManifest
											.sortBy(Models.INotificationManifest.Sort.DATE_DESC));
							if (notifications != null) {
								for (INotification notification : notifications) {
									if (notification.mediaId != null
											&& notification.mediaId
													.equals(log._id)) {
										if (mPreviousTry == null
												|| !mPreviousTry._id
														.equals(notification._id))
											mNotification = notification;
										break;
									}
								}
							}
						}
						return mNotification;
					}

					private INotification findNotification() {
						// Find the notification to wait on
						List<INotification> notifications = new ArrayList<INotification>(
								informaCam.notificationsManifest
										.sortBy(Models.INotificationManifest.Sort.DATE_DESC));
						if (notifications != null) {
							for (INotification notification : notifications) {
								if (notification.mediaId != null
										&& notification.mediaId.equals(log._id)) {
									return notification;
								}
							}
						}
						return null;
					}
				};

				handler.sendEmptyMessage(0);
				Looper.loop();
			}
		}.init(log)).start();
	}
	
	
}
