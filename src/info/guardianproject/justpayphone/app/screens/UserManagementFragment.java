package info.guardianproject.justpayphone.app.screens;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.witness.informacam.models.IDCIMEntry;
import org.witness.informacam.models.ILocation;
import org.witness.informacam.models.IOrganization;
import org.witness.informacam.models.media.IMedia;
import org.witness.informacam.storage.IOUtility;
import org.witness.informacam.utils.Constants.Models;
import org.witness.informacam.utils.Constants.App.Storage.Type;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.app.HomeActivity;
import info.guardianproject.justpayphone.app.adapters.MyWorkspacesListAdapter;
import info.guardianproject.justpayphone.app.adapters.OrganizationsListAdapter;
import info.guardianproject.justpayphone.app.popups.TextareaPopup;
import info.guardianproject.justpayphone.models.JPPWorkspace;
import info.guardianproject.justpayphone.utils.Constants.App.Home.Tabs;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;

public class UserManagementFragment extends Fragment implements OnClickListener {
	View rootView;
	Activity a;
		
	TabHost tabHost;
	
	ListView organizationHolder, workplacesHolder;
	List<IOrganization> organizations;
	List<JPPWorkspace> workspaces;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(li, container, savedInstanceState);
		
		View v = null;
		rootView = li.inflate(R.layout.fragment_home_user_management, null);
		
		tabHost = (TabHost) rootView.findViewById(android.R.id.tabhost);
		tabHost.setup();

		TabHost.TabSpec tab = tabHost.newTabSpec(Tabs.UserManagement.MyWorkplaces.TAG).setIndicator(HomeActivity.generateTab(li, R.layout.tabs_user_management, a.getString(R.string.where_i_work)));
		v = li.inflate(R.layout.fragment_user_management_my_workspaces, tabHost.getTabContentView(), true);
		tab.setContent(R.id.user_management_my_workplaces_root);
		tabHost.addTab(tab);
		
		workplacesHolder = (ListView) v.findViewById(R.id.my_workplaces_list_holder);
		
		tab = tabHost.newTabSpec(Tabs.UserManagement.MyLawyer.TAG).setIndicator(HomeActivity.generateTab(li, R.layout.tabs_user_management, a.getString(R.string.my_attorney)));
		v = li.inflate(R.layout.fragment_user_management_contact, tabHost.getTabContentView(), true);
		tab.setContent(R.id.user_management_my_lawyer_root);
		tabHost.addTab(tab);
		
		organizationHolder = (ListView) v.findViewById(R.id.user_management_organization_holder);
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
		
		tabHost.setCurrentTab(0);
	}
	
	private void initData() {
		organizations = new Vector<IOrganization>();
		
		// XXX: test data
		IOrganization organization = new IOrganization();
		organization.organizationName = "GLSP (Georgia Legal Services Project)";
		organization.organizationDetails = "{\"address\":\"89 Legal Place, Suite 4\",\"city\": \"Anytown\",\"state\": \"GA\",\"zip\": 19570,\"phone\": 5045552424,\"fax\": 5045552626}";
		
		organizations.add(organization);
		organizationHolder.setAdapter(new OrganizationsListAdapter(organizations, a));
		
		ILocation location = new ILocation();
		location.geoCoordinates = new float[] {32.2175f, 82.4136f};
		
		List<IMedia> associatedMedia = new ArrayList<IMedia>();
		IMedia media = new IMedia();
		media.bitmapThumb = "images/sample_1.png";
		media.dcimEntry = new IDCIMEntry();
		media.dcimEntry.mediaType = Models.IMedia.MimeType.IMAGE;
		associatedMedia.add(media);
		
		media = new IMedia();
		media.bitmapThumb = "images/sample_2.png";
		media.dcimEntry = new IDCIMEntry();
		media.dcimEntry.mediaType = Models.IMedia.MimeType.IMAGE;
		associatedMedia.add(media);
		
		media = new IMedia();
		media.dcimEntry = new IDCIMEntry();
		media.dcimEntry.mediaType = Models.IMedia.MimeType.LOG;
		associatedMedia.add(media);
		
		
		JPPWorkspace workspace = new JPPWorkspace();
		workspace.location = location;
		workspace.associatedMedia = (ArrayList<IMedia>) associatedMedia;
		
		workspaces = new Vector<JPPWorkspace>();
		workspaces.add(workspace);
		
		workplacesHolder.setAdapter(new MyWorkspacesListAdapter(workspaces, a));
		
		
	}

	@Override
	public void onClick(View v) {
		
	}	
}
