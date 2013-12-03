package info.guardianproject.justpayphone.utils;

import org.witness.informacam.models.media.ILog;

public class Constants {
	
	public interface WizardActivityListener {
		public void onLanguageSelected(String language);
		public void onLanguageConfirmed();
		public void onUsernameCreated(String username, String email, String password);
		public void onTakePhotoClicked();
		public void onLawyerInfoSet(String phoneNumber);
	}
	
	public interface HomeActivityListener {
		public int[] getDimensions();
		public ILog getCurrentLog();
		public void setCurrentLog(ILog currentLog);
		public void persistLog();
		public boolean getInitFlag();
		public void showLogView(boolean showBubble);
		public void showNavigationDots(boolean show);
	}
	
	public static class Settings {
		public static final String LANGUAGE = "jpp_language";
		public static final class Locales {
			public final static int DEFAULT = 0;
			public final static int EN = 1;
			public final static int ES = 2;
		}
		public static final String LAWYER_PHONE = "jpp_lawyer_phone";
		public static final String GENERATED_PWD = "jpp_generated_pwd";
	}
	
	public static class Forms {
		public static final String LUNCH_QUESTIONNAIRE = "Just Pay Phone: Clock-Out Questions";
		public static class LunchQuestionnaire {
			public static final String LUNCH_TAKEN = "jpp_did_have_lunch";
			public static final String LUNCH_MINUTES = "jpp_lunch_minutes";
		}
	}
	
	public class Codes {
		public class Routes {
			public static final int HOME = 1;
			public static final int CAMERA = 2;
			public static final int WIZARD = org.witness.informacam.utils.Constants.Codes.Messages.Wizard.INIT;
			public static final int LOGIN = org.witness.informacam.utils.Constants.Codes.Messages.Login.DO_LOGIN;
			public static final int LOGOUT = org.witness.informacam.utils.Constants.Codes.Messages.Login.DO_LOGOUT;
			public static final int FINISH_SAFELY = 3;
		}
		
		public class Media {
			public static final int TYPE_IMAGE = 400;
			public static final int TYPE_VIDEO = 401;
			public static final int TYPE_JOURNAL = 402;
		}

		public class Extras {
			public final static String MEDIA_ID = "media_id";
			public static final String LOGOUT_USER = org.witness.informacam.utils.Constants.Codes.Extras.LOGOUT_USER;
			public static final String CHANGE_LOCALE = "changeLocale";
			public static final String HAS_SEEN_HOME = "hasSeenHome";
			public static final String PERSISTENT_SERVICE = "persistentService";
			public static final String IS_SIGNING_OUT = "isSigningOut";
			public static final String PATH_TO_FILE = "pathToFile";
			public static final String FILE_PREFIX = "filePrefix";
			public static final String WIZARD_STEP = "wizardStep";
			public static final String GO_TO_CALL_LAWYER = "goToCallLawyerScreen";
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
