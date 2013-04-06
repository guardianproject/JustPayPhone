package info.guardianproject.justpayphone.app.screens;

import org.witness.informacam.utils.Constants.App;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.utils.Constants.HomeActivityListener;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class WorkStatusActiveFragment extends Fragment implements OnClickListener {
	View rootView;
	Activity a;
	
	Button workStatusToggle;
	
	private final static String LOG = App.LOG; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(li, container, savedInstanceState);
		
		rootView = li.inflate(R.layout.layout_work_status_active, null);
		
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
		Log.d(LOG, "HEY");
	}

	@Override
	public void onClick(View v) {
		if(v == workStatusToggle) {
			((HomeActivityListener) a).setWorkStatus(false);
		}
		
		
	}
		
}
