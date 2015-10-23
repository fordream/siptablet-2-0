package com.shopivr.component.base;



import com.shopivr.component.model.TADataStore;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public abstract class BaseFragment extends Fragment {
	public void execute() {
		new AsyncTask<String, String, Object>() {
			@Override
			protected void onPostExecute(Object result) {
				super.onPostExecute(result);
				updateUi(result);
			}

			@Override
			protected Object doInBackground(String... params) {
				return loadData();
			}

		}.execute("");
	}

	@Override
	public void onResume() {
		super.onResume();
		execute();
	}
	public void updateUi(Object result) {

	}

	public Object loadData() {
		return null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = new TextView(getActivity());
		if (getResourceLayout() != 0)
			v = inflater.inflate(getResourceLayout(), container, false);
		//getActivity().getActionBar().setTitle(getTitleBar());
//		getActivity().getActionBar().setDisplayShowHomeEnabled(false);
//		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
//		getActivity().getActionBar().setIcon(
//				   new ColorDrawable(getResources().getColor(android.R.color.transparent)));    
		
		// getActivity().getActionBar().setTitle(
		long id = TADataStore.getActiveId(getActivity());

		if (id != -1) {
			Cursor cursor = TADataStore.getActiveIdCursor(v.getContext());
			if (cursor != null) {
				String userNumber = cursor.getString(cursor
						.getColumnIndex("username"));
				//getActivity().getActionBar().setTitle(userNumber);
			}
		}
		onInitCreateView(v);
		return v;
	}

	public void onInitCreateView(View v) {

	}

	public View getView(int res) {
		return getView().findViewById(res);
	}

	public EditText getEditText(int res) {
		return (EditText) getView().findViewById(res);
	}

	public CheckBox getCheckBox(int res) {
		return (CheckBox) getView().findViewById(res);
	}

	public ListView getListView(int res) {
		return (ListView) getView().findViewById(res);
	}

	public void makeText(String text) {
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}

	public abstract int getResourceLayout();

	public abstract String getTitleBar();
	
	public TAMainApplication getApplication(){
		return ((TAMainApplication) getActivity().getApplication());
	}

}