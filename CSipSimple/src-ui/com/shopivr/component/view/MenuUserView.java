package com.shopivr.component.view;

import z.lib.base.CommonAndroid;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.knock.db.AccountDB;
import com.app.knock.db.TASetting;
import com.app.knock.db.TASetting.SettingType;
import com.shopivrtablet.R;
import com.shopivr.component.TAMainActivity;

//ta.com.component.view.MenuUserView
public class MenuUserView extends LinearLayout implements View.OnClickListener, OnCheckedChangeListener {
	private View menuuser_btn;
	private CheckBox menuuser_checkbox;

	public MenuUserView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		if (menuuser_btn != null) {
			menuuser_btn.setVisibility(View.VISIBLE);
		}

		if (visibility == View.VISIBLE) {
			String userNumber = new AccountDB(getContext()).getUserDisplayNumber();
			TextView textView = CommonAndroid.getView(this, R.id.menuusernumber);
			textView.setText(userNumber);
		}
	}

	public MenuUserView(Context context) {
		super(context);
		init();
	}

	private void init() {
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.menuuser, this);
		setVisibility(View.GONE);

		menuuser_btn = CommonAndroid.getView(this, R.id.menuuser_btn);
		menuuser_checkbox = CommonAndroid.getView(this, R.id.menuuser_checkbox);
		CommonAndroid.setOnClickListener(menuuser_btn, this);

		String checkedStr = new TASetting(getContext()).get(SettingType.shopmode);
		if (menuuser_checkbox != null)
			menuuser_checkbox.setChecked("true".equals(checkedStr));
		if (menuuser_checkbox != null)
			menuuser_checkbox.setOnCheckedChangeListener(this);
		this.setOnClickListener(this);
		if (findViewById(R.id.menuuser_main) != null)
			findViewById(R.id.menuuser_main).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});
	}

	@Override
	public void onClick(View v) {
		setVisibility(View.GONE);
		TAMainActivity.getInstance().closeMenu(v);

		if (v == menuuser_btn) {
			TAMainActivity.getInstance().logout();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		TAMainActivity.getInstance().changeCheckedUser(menuuser_checkbox.isChecked());
	}
}