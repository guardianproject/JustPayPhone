package info.guardianproject.justpayphone.app.adapters;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.models.JPPWorkspace;

import java.util.List;

import org.witness.informacam.models.Model;
import org.witness.informacam.models.media.IMedia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyWorkspacesListAdapter extends BaseAdapter {
	List<JPPWorkspace> workspaces;
	Context c;
	
	public MyWorkspacesListAdapter(List<JPPWorkspace> workspaces, Context c) {
		this.workspaces = workspaces;
		this.c = c;
		
	}
	
	@Override
	public int getCount() {
		return workspaces.size();
	}

	@Override
	public Object getItem(int position) {
		return workspaces.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(c).inflate(R.layout.adapter_workspace_list_adapter, null);
		JPPWorkspace workspace = workspaces.get(position);
		
		TextView location = (TextView) convertView.findViewById(R.id.workspace_location);
		location.setText(c.getString(R.string.x_location, workspace.location.geoCoordinates[0], workspace.location.geoCoordinates[1]));
		
		TextView daysWorked = (TextView) convertView.findViewById(R.id.workspace_days_worked);
		daysWorked.setText(c.getString(R.plurals.x_days_worked, workspace.getNumberOfDaysWorkedHere()));
		
		LinearLayout mediaHolder = (LinearLayout) convertView.findViewById(R.id.workspace_media_holder);
		for(IMedia media : workspace.associatedMedia) {
			ImageView thumb = new ImageView(c);
			thumb.setImageBitmap(media.getBitmap(media.bitmapThumb));
			mediaHolder.addView(thumb);
		}
		
		return convertView;
	}

}
