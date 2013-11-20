package info.guardianproject.justpayphone.app.screens.wizard;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.utils.Constants.Codes.Extras;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class WizardFragmentBase extends Fragment 
{
	protected View rootView;
	protected Activity a;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	protected abstract int getLayout();
	
	@Override
	public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(li, container, savedInstanceState);
		rootView = li.inflate(getLayout(), null);
		
		TextView tvWizardStep = (TextView) rootView.findViewById(R.id.tvWizardStep);
		if (tvWizardStep != null && getArguments().containsKey(Extras.WIZARD_STEP))
			tvWizardStep.setText(getArguments().getString(Extras.WIZARD_STEP));
		return rootView;
	}

	@Override
	public void onAttach(Activity a)
	{
		super.onAttach(a);
		this.a = a;
	}
}
