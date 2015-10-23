package com.shopivr.component.view;

import z.lib.base.CommonAndroid;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.shopivrtablet.R;
import com.shopivr.component.TAMainActivity;

//ta.com.component.view.Menu5View
public class Menu5View extends LinearLayout implements View.OnClickListener {
	private View menu5_1;
	private View menu5_2;
	private View menu5_3;
	private View menu5_4;

	public Menu5View(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Menu5View(Context context) {
		super(context);

		init();
	}

	private void init() {
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.menu5, this);
		menu5_1 = CommonAndroid.getView(this, R.id.menu5_1);
		menu5_2 = CommonAndroid.getView(this, R.id.menu5_2);
		menu5_3 = CommonAndroid.getView(this, R.id.menu5_3);
		menu5_4 = CommonAndroid.getView(this, R.id.menu5_4);
		setOnClickListener(this);
		if (menu5_1 != null)
			menu5_1.setOnClickListener(this);
		if (menu5_2 != null)
			menu5_2.setOnClickListener(this);
		if (menu5_3 != null)
			menu5_3.setOnClickListener(this);
		if (menu5_4 != null)
			menu5_4.setOnClickListener(this);

		setVisibility(View.GONE);
		if (findViewById(R.id.menu5_main) != null)
			findViewById(R.id.menu5_main).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});
	}

	@Override
	public void onClick(View v) {
		setVisibility(View.GONE);
		if (v.getId() == R.id.menu5_1) {
			TAMainActivity.getInstance().menu5(1);
		} else if (v.getId() == R.id.menu5_2) {
			TAMainActivity.getInstance().menu5(2);
		} else if (v.getId() == R.id.menu5_3) {
			TAMainActivity.getInstance().menu5(3);
		} else if (v.getId() == R.id.menu5_4) {
			TAMainActivity.getInstance().menu5(4);
		} else {
			TAMainActivity.getInstance().closeMenu(v);
		}
	}
}