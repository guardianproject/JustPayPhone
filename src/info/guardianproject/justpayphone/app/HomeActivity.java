package info.guardianproject.justpayphone.app;

import java.util.List;
import java.util.Vector;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.app.screens.CameraFragment;
import info.guardianproject.justpayphone.app.screens.UserManagementFragment;
import info.guardianproject.justpayphone.app.screens.WorkStatusActiveFragment;
import info.guardianproject.justpayphone.app.screens.WorkStatusInactiveFragment;
import info.guardianproject.justpayphone.app.screens.WorkStatusStubFragment;
import info.guardianproject.justpayphone.utils.Constants;
import info.guardianproject.justpayphone.utils.Constants.HomeActivityListener;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class HomeActivity extends FragmentActivity implements HomeActivityListener {
	Intent init;
	private final static String LOG = Constants.App.Home.LOG;
	private String packageName;
	
	List<Fragment> fragments = new Vector<Fragment>();
	Fragment userManagementFragment, workStatusFragment, cameraFragment;
	
	LayoutInflater li;
	TabHost tabHost;
	ViewPager viewPager;
	TabPager pager;
	
	boolean isAtWork = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		packageName = getClass().getName();

		Log.d(LOG, "hello " + packageName);
		setContentView(R.layout.activity_home);
		
		userManagementFragment = Fragment.instantiate(this, UserManagementFragment.class.getName());
		workStatusFragment = isAtWork ? Fragment.instantiate(this, WorkStatusActiveFragment.class.getName()) : Fragment.instantiate(this, WorkStatusInactiveFragment.class.getName());
		cameraFragment = Fragment.instantiate(this, CameraFragment.class.getName());
		
		fragments.add(workStatusFragment);
		fragments.add(userManagementFragment);
		fragments.add(cameraFragment);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		initLayout();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@SuppressWarnings("deprecation")
	public int[] getDimensions() {
		Display display = getWindowManager().getDefaultDisplay();
		return new int[] {display.getWidth(),display.getHeight()};
	}

	@Override
	public void onBackPressed() {
		setResult(Activity.RESULT_CANCELED);
		finish();
	}
	
	private void initLayout() {
		pager = new TabPager(getSupportFragmentManager());

		viewPager = (ViewPager) findViewById(R.id.view_pager_root);
		viewPager.setAdapter(pager);
		viewPager.setOnPageChangeListener(pager);

		li = LayoutInflater.from(this);

		int[] dims = getDimensions();

		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();

		TabHost.TabSpec tab = tabHost.newTabSpec(workStatusFragment.getClass().getName()).setIndicator(generateTab(li, R.layout.tabs_jpp_main));
		li.inflate(R.layout.fragment_home_work_status, tabHost.getTabContentView(), true);
		tab.setContent(R.id.work_status_root_view);
		tabHost.addTab(tab); 
		
		tab = tabHost.newTabSpec(UserManagementFragment.class.getName()).setIndicator(generateTab(li, R.layout.tabs_jpp_header, getResources().getDrawable(R.drawable.jpp_briefcase)));
		li.inflate(R.layout.fragment_home_user_management, tabHost.getTabContentView(), true);
		tab.setContent(R.id.user_management_root_view);
		tabHost.addTab(tab);		

		tab = tabHost.newTabSpec(CameraFragment.class.getName()).setIndicator(generateTab(li, R.layout.tabs_jpp_header, getResources().getDrawable(R.drawable.jpp_camera_icon)));
		li.inflate(R.layout.fragment_home_camera_chooser, tabHost.getTabContentView(), true);
		tab.setContent(R.id.camera_chooser_root_view);
		tabHost.addTab(tab);

		tabHost.setOnTabChangedListener(pager);

		for(int i=0; i<tabHost.getTabWidget().getChildCount(); i++) {
			View tab_ = tabHost.getTabWidget().getChildAt(i);
			if(i == 0) {
				tab_.setLayoutParams(new LinearLayout.LayoutParams((int) (dims[0] * 0.6), LayoutParams.MATCH_PARENT));
			} else {
				tab_.setLayoutParams(new LinearLayout.LayoutParams((int) (dims[0] * 0.2), LayoutParams.MATCH_PARENT));
			}
		}

		viewPager.setCurrentItem(0);
		setWorkStatus(isAtWork);
	}
	
	private void launchCamera() {
		
	}
	
	public static View generateTab(final LayoutInflater li, final int resource) {
		return generateTab(li, resource, null, null);
	}
	
	public static View generateTab(final LayoutInflater li, final int resource, final String tabLabel) {
		return generateTab(li, resource, null, tabLabel);
	}
	
	public static View generateTab(final LayoutInflater li, final int resource, final Drawable iconResource) {
		return generateTab(li, resource, iconResource, null);
	}
	
	public static View generateTab(final LayoutInflater li, final int resource, final Drawable iconResource, final String tabLabel) {
		View v = li.inflate(resource, null);
		if(iconResource != null) {
			((ImageView) v.findViewById(R.id.tab_icon)).setImageDrawable(iconResource);
		}
		
		if(tabLabel != null) {
			((TextView) v.findViewById(R.id.tab_label)).setText(tabLabel);
		}
		
		return v;
	}
	
	public void swapLayout(Fragment fragment) {
		swapLayout(fragment, R.id.work_status_fragment_root);
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
		public void onPageScrollStateChanged(int state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {}

		@Override
		public void onPageSelected(int page) {
			tabHost.setCurrentTab(page);
			Log.d(LOG, "setting current page as " + page);
			if(page == 2) {
				launchCamera();
			}
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
	public void setWorkStatus(boolean isAtWork) {
		this.isAtWork = isAtWork;
		
		workStatusFragment = isAtWork ? Fragment.instantiate(this, WorkStatusActiveFragment.class.getName()) : Fragment.instantiate(this, WorkStatusInactiveFragment.class.getName());
		swapLayout(workStatusFragment);
	}
	
	@Override
	public boolean getWorkStatus() {
		return isAtWork;
	}
}
