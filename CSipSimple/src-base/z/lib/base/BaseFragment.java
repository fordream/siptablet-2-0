package z.lib.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public abstract class BaseFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

	public BaseFragment() {
		super();
	}

	private OnChangLanguage changLanguage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		changLanguage = new OnChangLanguage(this);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(getLayout(), null);
		view.setOnClickListener(null);
		init(view);
		return view;
	}

	public abstract int getLayout();

	public abstract void init(View view);

	public boolean onBackPressed(Bundle extras) {
		if (getActivity() instanceof BaseActivity) {
			((BaseActivity) getActivity()).onBackPressed(extras);
		}
		return true;
	}

	public final void startFragment(BaseFragment baseFragment, Bundle extras) {
		if (getActivity() instanceof BaseActivity) {
			((BaseActivity) getActivity()).startFragment(baseFragment, extras);
		}
	}

	public void onFragmentBackPress(Bundle extras) {

	}

	public abstract void onChangeLanguage();

	public boolean isFinish() {
		if (getActivity() == null) {
			return true;
		}

		return getActivity().isFinishing();
	}

	public void finish() {
		if (getActivity() != null) {
			getActivity().finish();
		}
	}

	public void reload() {

	}
}