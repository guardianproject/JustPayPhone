package info.guardianproject.justpayphone.utils;

public class Constants {
	
	public interface HomeActivityListener {
		public int[] getDimensions();
	}
	
	public static class Settings {
		public static final String LANGUAGE = "jpp_language";
		public static final class Locales {
			public final static int DEFAULT = 0;
			public final static int EN = 1;
			public final static int ES = 2;
		}
	}
	
	public class Codes {
		public class Routes {
			public static final int HOME = 1;
			public static final int CAMERA = 2;
		}
		
		public class Media {
			public static final int TYPE_IMAGE = 400;
			public static final int TYPE_VIDEO = 401;
			public static final int TYPE_JOURNAL = 402;
		}

		public class Extras {
			public final static String MEDIA_ID = "media_id";
		}
	}
	
	public class Utils {
		public final static String LOG = "******************** Just Pay Phone : Utils ********************";
	}
	
	
	public class App {
		public class Camera {
			public final static String LOG = "******************** Just Pay Phone : CameraActivity ********************";
			public static final int ROUTE_CODE = Codes.Routes.CAMERA;
		}
		
		public class Home {
			public final static String LOG = "******************** Just Pay Phone : HomeActivity ********************";
			public static final int ROUTE_CODE = Codes.Routes.HOME;
			
			public class Tabs {
				public class CameraChooser {
					public final static String TAG = App.CameraChooser.TAG;
				}
				
				public class UserManagement {
					public final static String TAG = App.UserManagement.TAG;
					
					public class MyLawyer {
						public final static String TAG = "my_lawyer";
					}
					
					public class MyWorkplaces {
						public final static String TAG = "my_workplaces";
					}
				}
				
				public class WorkStatus {
					public final static String TAG = App.WorkStatus.TAG;
				}

				public static final String LAST = "last_tab";
				
				
			}
		}
		
		public class WorkStatus {
			public final static String TAG = "work_status";
		}
		
		
		public class UserManagement {
			public final static String TAG = "user_management";
		}
		
		
		
		public class CameraChooser {
			public final static String TAG = "camera_chooser";
		}
		
		public class Router {
			public final static String LOG = "******************** Just Pay Phone : Router ********************";
		}
		
		
		public class Wizard {
			public final static String LOG = "******************** Just Pay Phone : WizardActivity ********************";
		}
	}
}
