package info.guardianproject.justpayphone.app;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.witness.informacam.utils.Constants.Actions;
import org.witness.informacam.utils.Constants.Codes;
import org.witness.informacam.utils.Constants.App.Camera;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.app.screens.CameraFragment;
import info.guardianproject.justpayphone.app.screens.GalleryFragment;
import info.guardianproject.justpayphone.app.screens.UserManagementFragment;
import info.guardianproject.justpayphone.app.screens.WorkStatusFragment;
import info.guardianproject.justpayphone.utils.Constants;
import info.guardianproject.justpayphone.utils.Constants.HomeActivityListener;
import info.guardianproject.justpayphone.utils.Constants.App.Home;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class HomeActivity extends FragmentActivity implements HomeActivityListener {
	Intent init;
	private final static String LOG = Constants.App.Home.LOG;
	private String packageName;

	List<Fragment> fragments = new Vector<Fragment>();
	Fragment userManagementFragment, workStatusFragment, galleryFragment;

	LayoutInflater li;
	TabHost tabHost;
	ViewPager viewPager;
	TabPager pager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		packageName = getClass().getName();

		Log.d(LOG, "hello " + packageName);
		setContentView(R.layout.activity_home);

		userManagementFragment = Fragment.instantiate(this, UserManagementFragment.class.getName());
		workStatusFragment = Fragment.instantiate(this, WorkStatusFragment.class.getName());
		galleryFragment = Fragment.instantiate(this, GalleryFragment.class.getName());

		fragments.add(workStatusFragment);
		fragments.add(userManagementFragment);
		fragments.add(galleryFragment);

	}

	@Override
	public void onResume() {
		super.onResume();
		initLayout();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(Camera.TAG, true);
		outState.putInt(Home.Tabs.LAST, viewPager.getCurrentItem());
		super.onSaveInstanceState(outState);
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
		li.inflate(R.layout.fragment_user_management_contact, tabHost.getTabContentView(), true);
		tab.setContent(R.id.user_management_my_lawyer_root);
		tabHost.addTab(tab);		

		tab = tabHost.newTabSpec(GalleryFragment.class.getName()).setIndicator(generateTab(li, R.layout.tabs_jpp_header, getResources().getDrawable(R.drawable.jpp_camera_icon)));
		li.inflate(R.layout.fragment_user_management_my_workspaces, tabHost.getTabContentView(), true);
		tab.setContent(R.id.user_management_my_workplaces_root);
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

	public void swapLayout(Fragment fragment, int layoutRoot) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(layoutRoot, fragment);
		ft.addToBackStack(null);
		ft.commit();
		pager.notifyDataSetChanged();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		viewPager.setCurrentItem(0);
		
		super.onActivityResult(requestCode, resultCode, data);
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
}
