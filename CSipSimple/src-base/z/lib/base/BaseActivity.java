package z.lib.base;

import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.shopivrtablet.R;

public abstract class BaseActivity extends FragmentActivity {
	private FragmentManager fragmentManager;
	Fragment mContent = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		fragmentManager = getSupportFragmentManager();
		setContentView(getLayout());
	}

	public abstract int getLayout();

	public final void startFragment(BaseFragment baseFragment, Bundle bundle) {
		try {
			android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.setCustomAnimations(R.anim.scale, R.anim.scale, R.anim.scale_to_center, R.anim.scale_to_center);
			// transaction.setCustomAnimations(R.animator.slide_up,
			// R.animator.slide_down,
			// R.animator.slide_up,
			// R.animator.slide_down);
			transaction.add(getResMain(), baseFragment, "" + System.currentTimeMillis());
			transaction.addToBackStack(null);
			transaction.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public abstract int getResMain();

	@SuppressLint("NewApi")
	public void onBackPressed(Bundle extras) {
		int count = fragmentManager.getBackStackEntryCount();
		List<Fragment> fragments = fragmentManager.getFragments();

	}

	@Override
	public void onBackPressed() {
		try {
			onBackPressed(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void switchContent(Fragment fragment) {
		mContent = fragment;
		// getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
		// fragment).commit();
		// getSlidingMenu().showContent();
	}
}
