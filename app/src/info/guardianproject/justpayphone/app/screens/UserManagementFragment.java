package info.guardianproject.justpayphone.app.screens;

import java.util.List;
import java.util.Vector;

import org.witness.informacam.models.organizations.IOrganization;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.app.adapters.OrganizationsListAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

public class UserManagementFragment extends Fragment {
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
		
		// XXX: test data
		IOrganization organization = new IOrganization();
		organization.organizationName = a.getString(R.string.glsp);
		organization.organizationDetails = "{\"address\":\"104 Marietta Street, Suite 250\",\"city\": \"Atlanta\",\"state\": \"GA\",\"zip\": 30303,\"phone\": 404-463-1633,\"fax\": 404-463-1623}";
		
		organizations.add(organization);
		organizationHolder.setAdapter(new OrganizationsListAdapter(organizations, a));
	}
}
