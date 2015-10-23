package com.shopivr.component.view;

import z.lib.base.CommonAndroid;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.shopivrtablet.R;
import com.shopivr.component.TAMainActivity;

//ta.com.component.view.MenuLeftView
public class MenuLeftView extends LinearLayout implements View.OnClickListener {
	private View menuleft_1, menuleft_2, menuleft_3;

	public MenuLeftView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MenuLeftView(Context context) {
		super(context);
		init();
	}

	private void init() {
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.menuleft, this);
		setVisibility(View.GONE);

		menuleft_1 = CommonAndroid.getView(this, R.id.menuleft_1);
		menuleft_2 = CommonAndroid.getView(this, R.id.menuleft_2);
		menuleft_3 = CommonAndroid.getView(this, R.id.menuleft_3);
		setOnClickListener(this);
		if (menuleft_1 != null)
			menuleft_1.setOnClickListener(this);
		if (menuleft_2 != null)
			menuleft_2.setOnClickListener(this);
		if (menuleft_3 != null)
			menuleft_3.setOnClickListener(this);
		if (findViewById(R.id.menuleft_main) != null)
			findViewById(R.id.menuleft_main).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});
	}

	@Override
	public void onClick(View v) {
		setVisibility(View.GONE);
		if (menuleft_1 == v) {
			TAMainActivity.getInstance().menuLeft(1);
		} else if (menuleft_2 == v) {
			TAMainActivity.getInstance().menuLeft(2);
		} else if (menuleft_3 == v) {
			TAMainActivity.getInstance().menuLeft(3);
		} else {
			TAMainActivity.getInstance().closeMenu(v);
		}
	}
}