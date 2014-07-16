package info.guardianproject.justpayphone.app.popups;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.utils.Constants.Forms;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.witness.informacam.InformaCam;
import org.witness.informacam.models.forms.IForm;
import org.witness.informacam.models.media.ILog;
import org.witness.informacam.models.organizations.IOrganization;
import org.witness.informacam.utils.Constants.Codes;
import org.witness.informacam.utils.Constants.Logger;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.ProgressBar;

public class ExportAllPopup extends Popup {
	ProgressBar inProgressBar;
	
	private boolean mIsBatchExport = false;
	private ILog mLastLog = null;
	
	Handler h = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			
			if(b.containsKey(Codes.Keys.UI.PROGRESS)) {
			//	Log.d(LOG, "HELLO PROGRES: " + b.getInt(Codes.Keys.UI.PROGRESS) + "/" + inProgressBar.getProgress());
				inProgressBar.setProgress(inProgressBar.getProgress() + b.getInt(Codes.Keys.UI.PROGRESS));
			} else if(b.containsKey(Codes.Keys.BATCH_EXPORT_FINISHED)) {
				ExportAllPopup.this.cancel();
			}
			
			if (!mIsBatchExport)
			{
				String fileExport = null;
				
				if ((fileExport = b.getString("file")) != null)
				{
					ExportAllPopup.this.cancel();
					//this is the callback from the send/share export command
					boolean localShare = b.getBoolean("localShare");
					
					if (localShare)
					{
						Intent intent = new Intent()
						.setAction(Intent.ACTION_SEND)
						.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(fileExport)))
						.setType("message/rfc822");
						
						if (mLastLog != null)
						{
							InformaCam informaCam = InformaCam.getInstance();

							StringBuffer sbLog = new StringBuffer ();
							
							
							
							sbLog.append("From: " ).append(informaCam.user.alias).append(" (").append(informaCam.user.email).append(")").append("\n\n");
							//sbLog.append("Public Key: " ).append(informaCam.user.pgpKeyFingerprint).append("\n\n");
							sbLog.append("Device Unique Id: ").append(mLastLog.genealogy.createdOnDevice).append("\n\n");
							
							if (mLastLog.data != null && mLastLog.intakeData != null)
								sbLog.append("Log Unique Id: ").append(mLastLog.data.intakeData.signature).append("\n\n");
							
							sbLog.append("Log Start: ").append(DateFormat.getDateTimeInstance().format(new Date(mLastLog.startTime))).append("\n\n");
							sbLog.append("Log End: ").append(DateFormat.getDateTimeInstance().format(new Date(mLastLog.endTime))).append("\n\n");
							
							String geo = mLastLog.getSimpleLocationString();
							if (geo != null)
							{
								sbLog.append("GPS Location: ").append(geo).append("\n\n");
							}
							
							float msWorked = mLastLog.endTime - mLastLog.startTime;
							float hWorked = (msWorked / 3600000f);
							
							sbLog.append("Hours Worked: ").append(hWorked).append("\n\n");
							
							try
							{
								List<IForm> forms = mLastLog.getForms(a);
								
								for(IForm form : forms) {
									if(form.namespace.equals(Forms.LUNCH_QUESTIONNAIRE)) {
										
										EditText lunchMinutesProxy = new EditText(a);
										form.associate(lunchMinutesProxy, Forms.LunchQuestionnaire.LUNCH_MINUTES);
										
										
										Integer lunchMinutes = Integer.valueOf(lunchMinutesProxy.getText().toString());
									
										sbLog.append("Lunch Minutes: ").append(lunchMinutes).append("\n\n");
										break;
									}
								}
							}
							catch (Exception e)
							{
								Logger.e(LOG,e);
							}
							
							String message = sbLog.toString();
							
							try
							{
								byte[] sig = informaCam.signatureService.signData(message.getBytes());
							
							
								sbLog = new StringBuffer();
								sbLog.append("-----BEGIN PGP SIGNED MESSAGE-----\n");
								sbLog.append("Hash: SHA1\n\n");
								sbLog.append(message);
								
								String sigString = new String(sig);
								
								sbLog.append(sigString.replace("PGP MESSAGE", "PGP SIGNATURE"));
								
							}
							catch (Exception e)
							{
								Logger.e(LOG,e);
							}
							
							intent.putExtra(Intent.EXTRA_SUBJECT, "JustPay Work Log: " + DateFormat.getDateTimeInstance().format(new Date(mLastLog.startTime)));
							intent.putExtra(Intent.EXTRA_TEXT   , sbLog.toString());
							
						}
					
						Intent intentShare = Intent.createChooser(intent, a.getString(R.string.send));
						a.startActivity(intentShare);
					}
				}
			}
		}
	};
	
	List<ILog> observations;
	
	public ExportAllPopup(Activity a, List<ILog> observations) {
		super(a, R.layout.extras_waiter);
		
		this.observations = observations;
		
		inProgressBar = (ProgressBar) layout.findViewById(R.id.share_in_progress_bar);
		inProgressBar.setMax(observations.size() * 100);
		
		Show();
	}
	
	public void init() {
		init(true);
	}
	
	public void init(boolean localShare) {
		int observationsExported = 0;
		mIsBatchExport = true;
		
		//TODO keep the org null for now, as we don't want encryption on this export
		IOrganization org = null;
	
		boolean includeSensorLogs = true;
		
		if (!localShare)
			org = InformaCam.getInstance().installedOrganizations.getByName("GLSP");
		
		for(ILog iLog : ExportAllPopup.this.observations) {
			
			try {
				iLog.export(a, h, org, includeSensorLogs, localShare,!localShare);
			} catch(Exception e) {
				Logger.e(LOG, e);
			}
			
			observationsExported++;
			
			inProgressBar.setProgress(observationsExported * 100);
			
			if(observationsExported == ExportAllPopup.this.observations.size()) {
				Bundle b = new Bundle();
				b.putBoolean(Codes.Keys.BATCH_EXPORT_FINISHED, true);
				
				Message msg = new Message();
				msg.setData(b);
				
				h.sendMessage(msg);
			}
		}
	}
	
	public void init(boolean localShare, ILog iLog) {
		
		mLastLog = iLog;
		
		inProgressBar.setMax(100);
		
		mIsBatchExport = false;
		boolean includeSensorLogs = true;

		IOrganization org = null;
		
		if (!localShare)
			org = InformaCam.getInstance().installedOrganizations.getByName("GLSP");
		
		try {
			iLog.export(a, h, org, includeSensorLogs, localShare, !localShare);
		} catch(Exception e) {
			Logger.e(LOG, e);
		}
		
		inProgressBar.setProgress(100);
		
		
	}
}
