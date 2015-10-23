package com.shopivr.component.view;

import z.lib.base.CommonAndroid;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.shopivrtablet.R;
import com.shopivr.component.TAMainActivity;

//ta.com.component.view.Menu4View
public class Menu4View extends LinearLayout implements View.OnClickListener {
	private KeyPadCallView keypaddcallmenu4;
	private View menu4_call;

	public Menu4View(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Menu4View(Context context) {
		super(context);
		init();
	}

	private void init() {
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.menu4, this);
		setVisibility(View.GONE);
		if (findViewById(R.id.menu4_main) != null)
			findViewById(R.id.menu4_main).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});
		menu4_call = CommonAndroid.getView(this, R.id.menu4_call);
		keypaddcallmenu4 = CommonAndroid.getView(this, R.id.keypaddcallmenu4);
		if (menu4_call != null)
			menu4_call.setOnClickListener(this);
		setOnClickListener(this);
		if (findViewById(R.id.menu4_main) != null)
			findViewById(R.id.menu4_main).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});
	}

	@Override
	public void onClick(View v) {
		if (menu4_call == v) {

			String number = keypaddcallmenu4.getNumber();

			if (CommonAndroid.isBlank(number))
				return;

			TAMainActivity.getInstance().menu4Call(number);
			setVisibility(View.GONE);
		} else {

			TAMainActivity.getInstance().closeMenu(v);
			setVisibility(View.GONE);
		}

	}

	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		if (keypaddcallmenu4 != null)
			keypaddcallmenu4.clearNumber();
	}
}