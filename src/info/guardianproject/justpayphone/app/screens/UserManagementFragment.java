package info.guardianproject.justpayphone.app.screens;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.app.HomeActivity;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

public class UserManagementFragment extends Fragment implements TabHost.OnTabChangeListener {
	View rootView;
	Activity a;
		
	TabHost tabHost;
	Fragment workplacesFragment, contactFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(li, container, savedInstanceState);
		
		rootView = li.inflate(R.layout.fragment_home_user_management, null);
		
		tabHost = (TabHost) rootView.findViewById(android.R.id.tabhost);
		tabHost.setup();

		TabHost.TabSpec tab = tabHost.newTabSpec(MyWorkspacesFragment.class.getName()).setIndicator(HomeActivity.generateTab(li, R.layout.tabs_user_management, a.getString(R.string.where_i_work)));
		li.inflate(R.layout.fragment_user_management_my_workspaces, tabHost.getTabContentView(), true);
		tab.setContent(R.id.user_management_root_view);
		tabHost.addTab(tab); 
		
		tab = tabHost.newTabSpec(ContactFragment.class.getName()).setIndicator(HomeActivity.generateTab(li, R.layout.tabs_user_management, a.getString(R.string.my_attorney)));
		li.inflate(R.layout.fragment_user_management_contact, tabHost.getTabContentView(), true);
		tab.setContent(R.id.user_management_root_view);
		tabHost.addTab(tab);
		
		tabHost.setOnTabChangedListener(this);
		
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
	}

	@Override
	public void onTabChanged(String tabId) {
		
		
	}
		
}
