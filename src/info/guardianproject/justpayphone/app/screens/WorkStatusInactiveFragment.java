package info.guardianproject.justpayphone.app.screens;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.utils.Constants.HomeActivityListener;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class WorkStatusInactiveFragment extends Fragment implements OnClickListener {
	View rootView;
	Activity a;
	
	Button workStatusToggle;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(li, container, savedInstanceState);
		
		rootView = li.inflate(R.layout.layout_work_status_inactive, null);
		
		workStatusToggle = (Button) rootView.findViewById(R.id.work_status_clock_in_toggle);
		workStatusToggle.setOnClickListener(this);
		
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
	public void onClick(View v) {
		if(v == workStatusToggle) {
			((HomeActivityListener) a).setWorkStatus(true);
		}
		
	}
		
}
