package info.guardianproject.justpayphone.app.adapters;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.app.HomeActivity;
import info.guardianproject.justpayphone.models.JPPWorkSummary;
import info.guardianproject.justpayphone.utils.Constants.App;
import info.guardianproject.justpayphone.utils.Constants.Forms;
import info.guardianproject.justpayphone.utils.Constants.HomeActivityListener;
import info.guardianproject.odkparser.utils.QD;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.witness.informacam.InformaCam;
import org.witness.informacam.models.forms.IForm;
import org.witness.informacam.models.media.ILog;
import org.witness.informacam.models.media.IMedia;
import org.witness.informacam.models.media.IRegion;
import org.witness.informacam.models.notifications.INotification;
import org.witness.informacam.storage.FormUtility;
import org.witness.informacam.utils.Constants.Logger;
import org.witness.informacam.utils.Constants.Models;
import org.witness.informacam.utils.Constants.App.Storage.Type;
import org.witness.informacam.utils.TimeUtility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class ILogGallery extends BaseAdapter {
	InformaCam informaCam;
	List<ILog> iLogs;
	ArrayList<ILog> retrySend;
	Activity a;
	private RadioGroup lunchTakenProxy;
	private RadioButton lunchTakenProxyYes;
	private RadioButton lunchTakenProxyNo;
	private EditText lunchMinutesProxy;
	
	private final static String LOG = App.Home.LOG;
	
	public ILogGallery(List<ILog> iLogs, Activity a) {
		this.iLogs = iLogs;
		this.a = a;
		informaCam = InformaCam.getInstance();
		
		lunchTakenProxy = new RadioGroup(a);
		lunchTakenProxyYes = new RadioButton(a);
		lunchTakenProxyNo = new RadioButton(a);
		lunchTakenProxy.addView(lunchTakenProxyYes);
		lunchTakenProxy.addView(lunchTakenProxyNo);
		lunchMinutesProxy = new EditText(a);
		lunchMinutesProxy.setText(a.getString(R.string.x_minutes, 0));
		
		// TODO - add this to a separate thread!
		retrySend = new ArrayList<ILog>();
		List<INotification> notifications = new ArrayList<INotification>(informaCam.notificationsManifest.sortBy(Models.INotificationManifest.Sort.DATE_DESC));
		HashMap<String, INotification> mediaIdMap = new HashMap<String, INotification>();
		if (notifications != null)
		{
			for (INotification notification : notifications)
			{
				if (!TextUtils.isEmpty(notification.mediaId) && !mediaIdMap.containsKey(notification.mediaId))
					mediaIdMap.put(notification.mediaId, notification);
			}
			
			for (ILog log : iLogs)
			{
				if (mediaIdMap.containsKey(log._id))
				{
					INotification n = mediaIdMap.get(log._id);
					if (n.canRetry)
						retrySend.add(log);
				}
			}
		}
	}

	@Override
	public int getCount() {
		return iLogs.size();
	}



	@Override
	public Object getItem(int position) {
		return iLogs.get(position);
	}



	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		
		if (convertView == null)
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_day_item, parent, false);
		
		ILog iLog = iLogs.get(position);
	
		TextView tv = (TextView) convertView.findViewById(R.id.tvTimeDate);
		
		if (retrySend.contains(iLog))
		{
			tv.setBackgroundColor(a.getResources().getColor(R.color.send_failed_background));
		}
		else
		{
			tv.setBackgroundResource(0);
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(iLog.startTime);
		
		tv.setText(String.valueOf(cal.get(Calendar.DATE)));

		tv = (TextView) convertView.findViewById(R.id.tvTimeWorked);
		tv.setText(getWorkDisplayString(iLog));

		tv = (TextView) convertView.findViewById(R.id.tvTimeLunch);
		tv.setText(getLunchDisplayString(iLog));
		
		return convertView;
	}

	public String getWorkDisplayString(ILog iLog)
	{
		float msWorked = iLog.endTime - iLog.startTime;
		float hWorked = (msWorked / 3600000);
	    if(hWorked == (int) hWorked)
	        return String.format(Locale.getDefault(), "%d", (int)hWorked);
	    else
	        return String.format(Locale.getDefault(), "%.1f", hWorked);
	}
	
	private String getLunchDisplayString(ILog iLog)
	{
		
		String lunchTaken = a.getString(R.string.time_lunch_no_lunch);
		
		try
		{
			List<IForm> forms = iLog.getForms(a);
		
	
			for(IForm form : forms) {
				if(form.namespace.equals(Forms.LUNCH_QUESTIONNAIRE)) {
					form.associate(lunchTakenProxy, Forms.LunchQuestionnaire.LUNCH_TAKEN);
					form.associate(lunchMinutesProxy, Forms.LunchQuestionnaire.LUNCH_MINUTES);
					
					Integer lunchMinutes = Integer.valueOf(lunchMinutesProxy.getText().toString());
					lunchTaken = a.getString(R.string.time_lunch_minutes, lunchMinutes);
				
					
				}
			}
		}
		catch (Exception e)
		{
			Logger.e(LOG, e);
		}
		
		return lunchTaken;
	}
	
}
