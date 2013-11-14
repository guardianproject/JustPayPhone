package info.guardianproject.justpayphone.app.screens.wizard;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.utils.Constants.WizardActivityListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class WizardTakePhoto extends WizardFragmentBase implements OnClickListener
{
	private Button commit;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getLayout() {
		return R.layout.fragment_wizard_take_photo;
	}
	
	@Override
	public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(li, container, savedInstanceState);
		commit = (Button) rootView.findViewById(R.id.wizard_commit);
		commit.setOnClickListener(this);
		return rootView;
	}
	
	@Override
	public void onClick(View v)
	{
		if (v == commit)
		{
			if (a instanceof WizardActivityListener)
			{
				((WizardActivityListener) a).onTakePhotoClicked();
			}
		}
	}
}
