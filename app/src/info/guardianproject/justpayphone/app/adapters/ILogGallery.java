package info.guardianproject.justpayphone.app.adapters;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.models.JPPWorkSummary;
import info.guardianproject.justpayphone.utils.Constants.App;

import java.util.List;
import java.util.Random;

import org.witness.informacam.InformaCam;
import org.witness.informacam.models.media.ILog;
import org.witness.informacam.models.media.IMedia;
import org.witness.informacam.utils.Constants.App.Storage.Type;
import org.witness.informacam.utils.TimeUtility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ILogGallery extends BaseAdapter {
	InformaCam informaCam;
	List<ILog> iLogs;
	
	private final static String LOG = App.Home.LOG;
	
	public ILogGallery(List<ILog> iLogs) {
		this.iLogs = iLogs;
		informaCam = InformaCam.getInstance();
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
		convertView = LayoutInflater.from(informaCam.a).inflate(R.layout.adapter_ilog_gallery_parent, null);
		ILog iLog = iLogs.get(position);
		
		TextView date = (TextView) convertView.findViewById(R.id.ilog_date);
		date.setText(TimeUtility.millisecondsToDayOnly(iLog.startTime));
		
		LinearLayout imagesAndVideo = (LinearLayout) convertView.findViewById(R.id.ilog_images_and_video);
		for(String l : iLog.attachedMedia) {
			ImageView iv = new ImageView(informaCam.a);
			IMedia m = informaCam.mediaManifest.getById(l);
			byte[] bBytes = informaCam.ioService.getBytes(m.bitmapThumb, Type.IOCIPHER);
			Bitmap b = BitmapFactory.decodeByteArray(bBytes, 0, bBytes.length);
			
			LinearLayout.LayoutParams lp = new LayoutParams(90, 90);
			lp.setMargins(0, 0, 10, 0);
			
			iv.setLayoutParams(lp);
			iv.setImageBitmap(b);
			
			imagesAndVideo.addView(iv);
		}
		
		return convertView;
	}

	
}
