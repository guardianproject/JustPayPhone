package info.guardianproject.justpayphone.app.adapters;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.models.JPPWorkspace;
import info.guardianproject.justpayphone.utils.Constants.App;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.witness.informacam.models.media.IMedia;
import org.witness.informacam.utils.Constants.Models;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyWorkspacesListAdapter extends BaseAdapter {
	List<JPPWorkspace> workspaces;
	Activity c;
	
	private final static String LOG = App.Home.LOG;
	
	public MyWorkspacesListAdapter(List<JPPWorkspace> workspaces, Activity c) {
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
		daysWorked.setText(c.getResources().getQuantityString(R.plurals.x_days_worked, workspace.getNumberOfDaysWorkedHere(), workspace.getNumberOfDaysWorkedHere()));
		
		GridView imagesAndVideo = (GridView) convertView.findViewById(R.id.workspace_images_and_video);
		imagesAndVideo.setAdapter(new GalleryGridAdapter(workspace.getImagesAndVideo()));
		
		return convertView;
	}
	
	class GalleryGridAdapter extends BaseAdapter {
		List<IMedia> media;

		public GalleryGridAdapter(List<IMedia> media) throws NullPointerException {
			this.media = media;
		}

		@Override
		public int getCount() {
			return media.size();
		}

		@Override
		public Object getItem(int position) {
			return media.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressWarnings("deprecation")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = LayoutInflater.from(c).inflate(R.layout.adapter_gallery_list_item, null);

			ImageView iv = (ImageView) view.findViewById(R.id.gallery_thumb);
			try {
				iv.setImageBitmap(BitmapFactory.decodeStream(c.getAssets().open(media.get(position).bitmapThumb)));
			} catch (IOException e) {
				Log.e(LOG, e.toString());
				e.printStackTrace();
			}

			return view;
		}
	}

}
