package info.guardianproject.justpayphone.app.adapters;

import info.guardianproject.justpayphone.R;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.witness.informacam.models.IOrganization;
import org.witness.informacam.utils.Constants.App;
import org.witness.informacam.utils.Constants.Models;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class OrganizationsListAdapter extends BaseAdapter{
	List<IOrganization> organizations;
	Context c;
	
	private final static String LOG = App.LOG;
	
	public OrganizationsListAdapter(List<IOrganization> organizations, Context c) {
		this.organizations = organizations;
		this.c = c;
	}
	
	@Override
	public int getCount() {
		return organizations.size();
	}

	@Override
	public Object getItem(int position) {
		return organizations.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(c).inflate(R.layout.adapter_organization_list_item, null);
		IOrganization organization = organizations.get(position);
		
		TextView name = (TextView) convertView.findViewById(R.id.organization_name);
		name.setText(organization.organizationName);
		
		try {
			StringBuffer detailsStringBuffer = new StringBuffer();
			JSONObject detailsObject = (JSONObject) organization.inflateContent(organization.organizationDetails);
			detailsStringBuffer.append(detailsObject.getString(Models.IOrganization.ADDRESS)).append(System.getProperty("line.separator"));
			
			detailsStringBuffer.append(detailsObject.getString(Models.IOrganization.CITY)).append(", ");
			detailsStringBuffer.append(detailsObject.getString(Models.IOrganization.STATE)).append(" ");
			detailsStringBuffer.append(detailsObject.getString(Models.IOrganization.ZIP)).append(System.getProperty("line.separator"));
			
			detailsStringBuffer.append(c.getString(R.string.ph)).append(detailsObject.getString(Models.IOrganization.PHONE)).append(System.getProperty("line.separator"));
			detailsStringBuffer.append(c.getString(R.string.fax)).append(detailsObject.getString(Models.IOrganization.FAX)).append(System.getProperty("line.separator"));
			
			TextView details = (TextView) convertView.findViewById(R.id.organization_details);
			details.setText(detailsStringBuffer.toString());
			
			Button launchCall = (Button) convertView.findViewById(R.id.organization_launch_call);
			launchCall.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO: launch call
				}
			});
			
			Button launchMessage = (Button) convertView.findViewById(R.id.organization_launch_message);
			launchMessage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO: launch message
				}
			});
			
			Button launchRecording = (Button) convertView.findViewById(R.id.organization_launch_recording);
			launchRecording.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO: launch recording
				}
			});
			
			
		} catch (JSONException e) {
			Log.e(LOG, e.toString());
			e.printStackTrace();
		}
		
		
		
		return convertView;
	}
}
