package info.guardianproject.justpayphone.app;

import org.witness.informacam.ui.SurfaceGrabberActivity;

public class WizardPhotoActivity extends SurfaceGrabberActivity {

	public WizardPhotoActivity() {
		super();
	}

	@Override
	protected boolean canUseOtherDirection() {
		return true;
	}
}
