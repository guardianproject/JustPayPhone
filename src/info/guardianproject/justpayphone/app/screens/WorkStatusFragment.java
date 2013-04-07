package info.guardianproject.justpayphone.app.screens;

import java.util.Timer;
import java.util.TimerTask;

import org.witness.informacam.utils.Constants.App;

import info.guardianproject.justpayphone.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class WorkStatusFragment extends Fragment implements OnClickListener {
	View rootView;
	Activity a;

	Button workStatusToggle;
	LinearLayout activeRoot, inactiveRoot;
	
	Timer t;
	TimerTask tt;
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
		
		activeRoot = (LinearLayout) rootView.findViewById(R.id.work_status_active);
		inactiveRoot = (LinearLayout) rootView.findViewById(R.id.work_status_inactive);
		
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
		toggleWorkStatus();
	}
	
	private void toggleWorkStatus() {
		
	}

	@Override
	public void onClick(View v) {
		if(v == workStatusToggle) {
			toggleWorkStatus();
		}
		
	}	
}
