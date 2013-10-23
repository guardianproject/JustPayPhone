package info.guardianproject.justpayphone.app;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.json.JSONException;
import org.witness.informacam.InformaCam;
import org.witness.informacam.models.media.ILog;
import org.witness.informacam.models.media.IMedia;
import org.witness.informacam.utils.Constants.Codes;
import org.witness.informacam.utils.Constants.Models;
import org.witness.informacam.utils.Constants.App.Camera;
import org.witness.informacam.utils.Constants.Models.IMedia.MimeType;
import org.witness.informacam.utils.InformaCamBroadcaster.InformaCamStatusListener;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.app.screens.GalleryFragment;
import info.guardianproject.justpayphone.app.screens.UserManagementFragment;
import info.guardianproject.justpayphone.app.screens.WorkStatusFragment;
import info.guardianproject.justpayphone.app.views.DottedProgressView;
import info.guardianproject.justpayphone.utils.Constants;
import info.guardianproject.justpayphone.utils.Constants.HomeActivityListener;
import info.guardianproject.justpayphone.utils.Constants.App.Home;
import info.guardianproject.justpayphone.utils.Constants.Settings;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class HomeActivity extends FragmentActivity implements HomeActivityListener, InformaCamStatusListener {
	private final static String LOG = Constants.App.Home.LOG;
	private String lastLocale;

	List<Fragment> fragments = new Vector<Fragment>();
	Fragment userManagementFragment, workStatusFragment, galleryFragment;
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

		setContentView(R.layout.activity_home);

		userManagementFragment = Fragment.instantiate(this, UserManagementFragment.class.getName());
		workStatusFragment = Fragment.instantiate(this, WorkStatusFragment.class.getName());
		galleryFragment = Fragment.instantiate(this, GalleryFragment.class.getName());

		fragments.add(workStatusFragment);
		fragments.add(userManagementFragment);
		fragments.add(galleryFragment);

		h = new Handler();

		lastLocale = PreferenceManager.getDefaultSharedPreferences(this).getString(Settings.LANGUAGE, "0");

		// XXX HEY! XXX!
		//InformaCam.getInstance().mediaManifest.media.clear();
		//InformaCam.getInstance().mediaManifest.save();
	}

	@Override
	public void onResume() {
		super.onResume();
		
		informaCam = InformaCam.getInstance(this);
		
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
			long currentTime = informaCam.informaService.getCurrentTime();

			try {
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
	public void showLogView() {
		viewPager.setCurrentItem(fragments.indexOf(galleryFragment));
	}
}
