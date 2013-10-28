package info.guardianproject.justpayphone.app.screens;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.app.adapters.ILogGallery;
import info.guardianproject.justpayphone.utils.Constants;
import info.guardianproject.justpayphone.utils.Constants.HomeActivityListener;

import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.witness.informacam.InformaCam;
import org.witness.informacam.models.media.ILog;
import org.witness.informacam.models.media.IMedia;
import org.witness.informacam.utils.Constants.Codes;
import org.witness.informacam.utils.Constants.Models;
import org.witness.informacam.utils.Constants.Models.IMedia.MimeType;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GalleryFragment extends Fragment implements OnClickListener, OnScrollListener {
	View rootView;
	Activity a;
	InformaCam informaCam = InformaCam.getInstance();

	ListView iLogList;

	Handler h = new Handler();
	private TextView tvHeaderDate;

	private final static String LOG = Constants.App.Home.LOG;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(li, container, savedInstanceState);

		rootView = li.inflate(R.layout.fragment_user_management_my_workspaces, null);

		iLogList = (ListView) rootView.findViewById(R.id.my_workplaces_list_holder);
		iLogList.setOnScrollListener(this);
		
		tvHeaderDate = (TextView) rootView.findViewById(R.id.tvTimeDate);
		
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
		h.post(new Runnable()
		{
			@Override
			public void run() {
				updateWorkspaces();
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void updateWorkspaces() {
		List<ILog> iLogs = informaCam.mediaManifest.getAllByType(MimeType.LOG);

		if(iLogs == null) {
			return;
		}

		iLogList.setAdapter(new ILogGallery(iLogs, a));
	}

//	private void initData() {
//		updateWorkspaces();
//
//
//	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK) {
			switch(requestCode) {
			case Codes.Routes.IMAGE_CAPTURE:
				Log.d(LOG, "THIS RETURNS:\n" + data.getStringExtra(Codes.Extras.RETURNED_MEDIA));
				try {
					JSONArray returnedMedia = ((JSONObject) new JSONTokener(data.getStringExtra(Codes.Extras.RETURNED_MEDIA)).nextValue()).getJSONArray("dcimEntries");

					// add to current log's attached media
					IMedia m = new IMedia();
					m.inflate(returnedMedia.getJSONObject(0));
					
					((HomeActivityListener) a).getCurrentLog().attachedMedia.add(m._id);
					((HomeActivityListener) a).persistLog();
					
					// update UI
					updateWorkspaces();
					
				} catch(JSONException e) {
					Log.e(LOG, e.toString());
					e.printStackTrace();
				}
				break;
			}
		}
	}


	@Override
	public void onClick(View v) {
		if(((HomeActivityListener) a).getCurrentLog() == null || ((HomeActivityListener) a).getCurrentLog().has(Models.IMedia.ILog.IS_CLOSED)) {
			Toast.makeText(a, getString(R.string.you_cannot_take_a), Toast.LENGTH_LONG).show();
			return;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		ILogGallery list = (ILogGallery) view.getAdapter();
		if (list == null || firstVisibleItem >= totalItemCount)
			return;
		
		ILog first = (ILog) list.getItem(firstVisibleItem);
		
		if (first == null)
			return;
		
		ILog last = first;
		if (visibleItemCount > 1)
			last = (ILog) list.getItem(firstVisibleItem + visibleItemCount - 1);
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(first.startTime);

		int firstMonth = cal.get(Calendar.MONTH);
		int firstYear = cal.get(Calendar.YEAR);

		cal.setTimeInMillis(last.startTime);
		int lastMonth = cal.get(Calendar.MONTH);
		int lastYear = cal.get(Calendar.YEAR);
		
		if (firstYear == lastYear && firstMonth == lastMonth)
		{
			tvHeaderDate.setText(this.getString(R.string.time_span_same_month, String.valueOf(firstMonth)));
		}
		else
		{
			tvHeaderDate.setText(this.getString(R.string.time_span_months, String.valueOf(firstMonth), String.valueOf(lastMonth)));
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

}
