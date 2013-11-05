package info.guardianproject.justpayphone.app.screens.wizard;
import info.guardianproject.justpayphone.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class WizardWaitForKey extends Fragment
{
	View rootView;
	Activity a;
	private ProgressBar progress;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(li, container, savedInstanceState);
		rootView = li.inflate(R.layout.fragment_wizard_wait_for_key, null);
		
		progress = (ProgressBar) rootView.findViewById(R.id.progressBarWait);
		
		return rootView;
	}

	@Override
	public void onAttach(Activity a)
	{
		super.onAttach(a);
		this.a = a;
	}

	public void setProgress(Integer value) {
		if (progress != null)
			progress.setProgress(value);
	}
}
