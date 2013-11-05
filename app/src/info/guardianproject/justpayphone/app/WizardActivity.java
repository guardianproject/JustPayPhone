package info.guardianproject.justpayphone.app;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.app.screens.wizard.WizardCreateDB;
import info.guardianproject.justpayphone.app.screens.wizard.WizardLawyerInformation;
import info.guardianproject.justpayphone.app.screens.wizard.WizardSelectLanguage;
import info.guardianproject.justpayphone.app.screens.wizard.WizardTakePhoto;
import info.guardianproject.justpayphone.app.screens.wizard.WizardWaitForKey;
import info.guardianproject.justpayphone.utils.Constants;
import info.guardianproject.justpayphone.utils.Constants.Settings;
import info.guardianproject.justpayphone.utils.Constants.WizardActivityListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.spongycastle.openpgp.PGPException;
import org.witness.informacam.InformaCam;
import org.witness.informacam.crypto.KeyUtility;
import org.witness.informacam.models.organizations.IInstalledOrganizations;
import org.witness.informacam.models.organizations.IOrganization;
import org.witness.informacam.storage.FormUtility;
import org.witness.informacam.ui.SurfaceGrabberActivity;
import org.witness.informacam.utils.Constants.Codes;
import org.witness.informacam.utils.Constants.InformaCamEventListener;
import org.witness.informacam.utils.Constants.App.Storage.Type;
import org.witness.informacam.utils.Constants.Models.IUser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class WizardActivity extends SherlockFragmentActivity implements WizardActivityListener, InformaCamEventListener
{
	private InformaCam informaCam;
	private WizardWaitForKey mWaitForKeyFragment;
	private Handler mHandler;
	
	private final static String LOG = Constants.App.Wizard.LOG;
	
	public WizardActivity()
	{
		super();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mHandler = new Handler();

		informaCam =  InformaCam.getInstance();
		informaCam.setEventListener(this);
		
		setContentView(R.layout.activity_wizard);

		Fragment step1 = Fragment.instantiate(this, WizardSelectLanguage.class.getName());

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.wizard_holder, step1);
		//ft.addToBackStack(null);
		ft.commit();
	}

	@Override
	public void onBackPressed()
	{
		if (getSupportFragmentManager().getBackStackEntryCount() == 0)
		{
			this.setResult(RESULT_CANCELED);
			finish();
		}
		else
		{
			super.onBackPressed();
		}
	}

	@Override
	public void onLanguageSelected(String languageCode)
	{
		SharedPreferences.Editor sp = PreferenceManager.getDefaultSharedPreferences(this).edit();
		sp.putString(Codes.Extras.LOCALE_PREF_KEY, languageCode).commit();

		Configuration configuration = new Configuration();
		configuration.locale = new Locale(languageCode);

		getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

		setResult(Activity.RESULT_FIRST_USER, new Intent().putExtra(Codes.Extras.CHANGE_LOCALE, true));
		finish();
	}

	@Override
	public void onLanguageConfirmed()
	{
		Fragment step2 = Fragment.instantiate(this, WizardCreateDB.class.getName());

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_left, R.anim.slide_out_to_right);
		ft.replace(R.id.wizard_holder, step2);
		ft.addToBackStack(null);
		ft.setTransitionStyle(2);
		ft.commit();
	}

	@Override
	public void onUsernameCreated(String username, String email, String password)
	{
		try {
			informaCam.user.put(IUser.ALIAS, username);
			informaCam.user.put(IUser.EMAIL, email);
			informaCam.user.put(IUser.PASSWORD, password);
			
			Fragment step3 = Fragment.instantiate(this, WizardTakePhoto.class.getName());

			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_left, R.anim.slide_out_to_right);
			ft.replace(R.id.wizard_holder, step3);
			ft.addToBackStack(null);
			ft.commit();

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTakePhotoClicked()
	{
		Intent surfaceGrabberIntent = new Intent(this, SurfaceGrabberActivity.class);
		startActivityForResult(surfaceGrabberIntent, Codes.Routes.IMAGE_CAPTURE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK) {
			switch(requestCode) {
			case Codes.Routes.IMAGE_CAPTURE:
				
				Fragment step4 = Fragment.instantiate(this, WizardLawyerInformation.class.getName());

				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_left, R.anim.slide_out_to_right);
				ft.replace(R.id.wizard_holder, step4);
				ft.addToBackStack(null);
				ft.commit();
				break;
			}
		}
	}

	@Override
	public void onLawyerInfoSet(String phoneNumber) {
		
		// Save number in prefs
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		prefs.edit().putString(Settings.LAWYER_PHONE, phoneNumber).commit();
		
		mWaitForKeyFragment = (WizardWaitForKey) Fragment.instantiate(this, WizardWaitForKey.class.getName());

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_left, R.anim.slide_out_to_right);
		ft.replace(R.id.wizard_holder, mWaitForKeyFragment);
		ft.addToBackStack(null);
		ft.commit();	
		
		generateKey();
	}

	@Override
	public void onUpdate(Message message) {
		int code = message.getData().getInt(Codes.Extras.MESSAGE_CODE);

		switch (code)
		{
		// TODO - handle error case!
//		case org.witness.informacam.utils.Constants.Codes.Messages.Transport.GENERAL_FAILURE:
//			mHandlerUI.post(new Runnable()
//			{
//				@Override
//				public void run()
//				{
//					Toast.makeText(HomeActivity.this, message.getData().getString(Codes.Extras.GENERAL_FAILURE), Toast.LENGTH_LONG).show();
//				}
//			});
//			break;
		
		case Codes.Messages.UI.UPDATE:
			if (mWaitForKeyFragment != null)
				mWaitForKeyFragment.setProgress((Integer) message.getData().get(Codes.Keys.UI.PROGRESS));
			break;
		
		case org.witness.informacam.utils.Constants.Codes.Messages.UI.REPLACE:
			// Ok, done!
			setResult(RESULT_OK);
			finish();
			break;
		}
	}
	
	private void generateKey()
	{
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(KeyUtility.initDevice()) {
					
					mHandler.post(new Runnable()
					{
						@Override
						public void run()
						{
							// save everything
							InformaCam informaCam = (InformaCam)getApplication();
							
							informaCam.user.hasCompletedWizard = true;
							informaCam.user.lastLogIn = System.currentTimeMillis();
							informaCam.user.isLoggedIn = true;
							
							informaCam.saveState(informaCam.user);
							informaCam.saveState(informaCam.languageMap);
							
							try {
								informaCam.initData();
							} catch (PGPException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							installOrganization();
							FormUtility.installIncludedForms(WizardActivity.this);
							
							// Tell others we are done!
							Bundle data = new Bundle();
							data.putInt(org.witness.informacam.utils.Constants.Codes.Extras.MESSAGE_CODE, org.witness.informacam.utils.Constants.Codes.Messages.UI.REPLACE);
							
							Message message = new Message();
							message.setData(data);
							
							informaCam.update(data);
						}
					});
				}
			}
		}).start();
	}
	
	private void installOrganization() {
		Log.d(LOG, "OK WIZARD COMPLETED!");
		try {
			if(getAssets().list("includedOrganizations").length > 0) {
				List<IOrganization> includedOrganizations = new ArrayList<IOrganization>();
				for(String organizationManifest : getAssets().list("includedOrganizations")) {
					IOrganization organization = new IOrganization();
					organization.inflate((JSONObject) new JSONTokener(
							new String(
									informaCam.ioService.getBytes(
											("includedOrganizations/" + organizationManifest), 
											Type.APPLICATION_ASSET)
									)
							).nextValue());
					
					InputStream is = getResources().openRawResource(R.raw.glsp);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					int size = 0;
					byte[] buf = new byte[1024];
					while((size = is.read(buf, 0, buf.length)) >= 0) {
						baos.write(buf, 0, size);
					}
					is.close();
					
					info.guardianproject.iocipher.File publicKey = new info.guardianproject.iocipher.File("glsp.asc");
					informaCam.ioService.saveBlob(baos.toByteArray(), publicKey);
					
					organization.publicKey = publicKey.getAbsolutePath();
					includedOrganizations.add(organization);
				}

				IInstalledOrganizations installedOrganizations = new IInstalledOrganizations();
				installedOrganizations.organizations = includedOrganizations;
				informaCam.saveState(installedOrganizations);
			}
		} catch(IOException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		}
	}
}
