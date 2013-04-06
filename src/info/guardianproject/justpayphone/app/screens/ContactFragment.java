package info.guardianproject.justpayphone.app.screens;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.app.adapters.OrganizationsListAdapter;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.witness.informacam.models.IOrganization;
import org.witness.informacam.utils.Constants.App.Camera;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ContactFragment extends Fragment {
	View rootView;
	Activity a;
	
	ListView organizationHolder;
	List<IOrganization> organizations;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(li, container, savedInstanceState);
		
		rootView = li.inflate(R.layout.fragment_user_management_contact, null);
		organizationHolder = (ListView) rootView.findViewById(R.id.user_management_organization_holder);
		
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
		initData();
	}
	
	private void initData() {
		organizations = new Vector<IOrganization>();
		
		// TODO: test data
		IOrganization organization = new IOrganization();
		organization.organizationName = "GLSP (Georgia Legal Services Project)";
		organization.organizationDetails = "{\"address\":\"89 Legal Place, Suite 4\",\"city\": \"Anytown\",\"state\": \"GA\",\"zip\": 19570,\"phone\": 5045552424,\"fax\": 5045552626}";
		
		organizations.add(organization);
		
		organizationHolder.setAdapter(new OrganizationsListAdapter(organizations, a));
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(Camera.TAG, true);

		super.onSaveInstanceState(outState);
	}
}
