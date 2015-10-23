package com.shopivr.component.view;

import z.lib.base.CommonAndroid;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.shopivrtablet.R;
import com.shopivr.component.TAMainActivity;
import com.shopivr.component.view.KeyPadCallView.IDTMS;

//ta.com.component.view.DTMSView
public class DTMSView extends LinearLayout implements View.OnClickListener {
	private KeyPadCallView keypaddcallmenu4;

	public DTMSView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DTMSView(Context context) {
		super(context);
		init();
	}

	private void init() {
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.dtms, this);
		setVisibility(View.GONE);
		try {
			findViewById(R.id.dtms_cancel).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					setVisibility(View.GONE);
					if (keypaddcallmenu4 != null) {
						keypaddcallmenu4.clearNumber();
					}
				}
			});
			keypaddcallmenu4 = CommonAndroid.getView(this, R.id.keypaddcalldtms);
			keypaddcallmenu4.setDtms(new IDTMS() {

				@Override
				public void onDtms(String numder) {

					if (numder.length() >= 1) {
						numder = numder.substring(numder.length() - 1, numder.length());
					}
					TAMainActivity.getInstance().onDtms(numder);
				}
			});
			setOnClickListener(this);
		} catch (Exception exception) {
		}
	}

	@Override
	public void onClick(View v) {
		{
			// setVisibility(View.GONE);
			// TAMainActivity.getInstance().closeMenu(v);
		}
	}
}