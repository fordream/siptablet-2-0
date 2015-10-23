package z.lib.base;

import android.os.Handler;
import android.os.Message;

public class OnChangLanguage extends Handler {
	public OnChangLanguage(BaseFragment baseFragment) {
		super();

		this.fragment = baseFragment;
	}

	@Override
	public final void dispatchMessage(Message msg) {
		super.dispatchMessage(msg);
		if (fragment != null && fragment.getActivity() != null) {
			fragment.onChangeLanguage();
		}
	}

	private BaseFragment fragment;

}
