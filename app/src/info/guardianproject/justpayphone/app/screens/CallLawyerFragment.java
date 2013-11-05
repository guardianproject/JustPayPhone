package info.guardianproject.justpayphone.app.screens;

import org.witness.informacam.InformaCam;
import org.witness.informacam.utils.Constants.App;
import org.witness.informacam.utils.Constants.Codes;
import org.witness.informacam.utils.InformaCamBroadcaster.InformaCamStatusListener;
import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.app.SelfieActivity;
import info.guardianproject.justpayphone.utils.Constants.Settings;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class CallLawyerFragment extends Fragment implements OnClickListener, InformaCamStatusListener {
	View rootView;
	Activity a;

	InformaCam informaCam = null;
	private View mBtnAdmin;
	private View mBtnCallLawyer;

	private final static String LOG = App.LOG;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(li, container, savedInstanceState);

		rootView = li.inflate(R.layout.fragment_call_lawyer, null);

		mBtnAdmin = rootView.findViewById(R.id.btnAdmin);
		mBtnAdmin.setOnClickListener(this);
		mBtnCallLawyer = rootView.findViewById(R.id.btnCallLawyer);
		mBtnCallLawyer.setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);
		this.a = a;
		informaCam = InformaCam.getInstance();
	}
	
	@Override
	public void onClick(View v) {
		if (v == mBtnCallLawyer) {
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(a.getBaseContext());
			String phoneNumber = prefs.getString(Settings.LAWYER_PHONE, null);
			if (phoneNumber != null)
			{
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
				startActivity(intent);
			}
		} 
	}

	@Override
	public void onInformaCamStart(Intent intent) {}

	@Override
	public void onInformaCamStop(Intent intent) {}

	@Override
	public void onInformaStop(Intent intent) {
	}

	@Override
	public void onInformaStart(Intent intent) {
	}	
}
