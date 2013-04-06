package info.guardianproject.justpayphone.app.screens;

import info.guardianproject.justpayphone.R;

import org.witness.informacam.utils.Constants.App.Camera;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyWorkspacesFragment extends Fragment {
	View rootView;
	Activity a;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(li, container, savedInstanceState);
		
		rootView = li.inflate(R.layout.fragment_user_management_my_workspaces, null);
		
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
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(Camera.TAG, true);

		super.onSaveInstanceState(outState);
	}
}
