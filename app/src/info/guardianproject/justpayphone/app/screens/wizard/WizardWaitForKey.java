package info.guardianproject.justpayphone.app.screens.wizard;
import info.guardianproject.justpayphone.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class WizardWaitForKey extends WizardFragmentBase
{
	private ProgressBar progress;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getLayout() {
		return R.layout.fragment_wizard_wait_for_key;
	}
	
	@Override
	public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(li, container, savedInstanceState);
		
		progress = (ProgressBar) rootView.findViewById(R.id.progressBarWait);
		
		return rootView;
	}

	public void setProgress(Integer value) {
		if (progress != null)
			progress.setProgress(value);
	}
}
