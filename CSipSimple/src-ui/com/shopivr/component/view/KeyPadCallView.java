package com.shopivr.component.view;

import z.lib.base.CommonAndroid;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shopivrtablet.R;

//ta.com.component.view.KeyPadCallView
public class KeyPadCallView extends LinearLayout implements View.OnClickListener {
	private TextView menu4_edt;
	private View menu4_1;
	private View menu4_2;
	private View menu4_3;
	private View menu4_4;
	private View menu4_5;
	private View menu4_6;
	private View menu4_7;
	private View menu4_8;
	private View menu4_9;
	private View menu4_0;
	private View menu4_star;
	private View menu4_sharp;
	private View menu_delete;
	private View menu4_delete_one;

	public KeyPadCallView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public KeyPadCallView(Context context) {
		super(context);
		init();
	}

	private void init() {
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.keypad_call, this);
		// setVisibility(View.GONE);
		try {
			menu4_edt = CommonAndroid.getView(this, R.id.menu4_edt);
			menu4_1 = CommonAndroid.getView(this, R.id.menu4_1);
			menu4_2 = CommonAndroid.getView(this, R.id.menu4_2);
			menu4_3 = CommonAndroid.getView(this, R.id.menu4_3);
			menu4_4 = CommonAndroid.getView(this, R.id.menu4_4);
			menu4_5 = CommonAndroid.getView(this, R.id.menu4_5);
			menu4_6 = CommonAndroid.getView(this, R.id.menu4_6);
			menu4_7 = CommonAndroid.getView(this, R.id.menu4_7);
			menu4_8 = CommonAndroid.getView(this, R.id.menu4_8);
			menu4_9 = CommonAndroid.getView(this, R.id.menu4_9);
			menu4_0 = CommonAndroid.getView(this, R.id.menu4_0);
			menu4_star = CommonAndroid.getView(this, R.id.menu4_star);
			menu4_sharp = CommonAndroid.getView(this, R.id.menu4_sharp);
			menu_delete = CommonAndroid.getView(this, R.id.menu_delete);
			menu4_delete_one = CommonAndroid.getView(this, R.id.menu4_delete_one);

			menu4_1.setOnClickListener(this);
			menu4_2.setOnClickListener(this);
			menu4_3.setOnClickListener(this);
			menu4_4.setOnClickListener(this);
			menu4_5.setOnClickListener(this);
			menu4_6.setOnClickListener(this);
			menu4_7.setOnClickListener(this);
			menu4_8.setOnClickListener(this);
			menu4_9.setOnClickListener(this);
			menu4_0.setOnClickListener(this);
			menu4_star.setOnClickListener(this);
			menu4_sharp.setOnClickListener(this);
			menu_delete.setOnClickListener(this);
			menu4_delete_one.setOnClickListener(this);
			setOnClickListener(this);
		} catch (Exception ex) {

		}
	}

	@Override
	public void onClick(View v) {
		if (menu_delete == v) {
			menu4_edt.setText("");
		} else if (menu4_delete_one == v) {
			String number = menu4_edt.getText().toString();
			if (number.length() >= 1) {
				number = number.substring(0, number.length() - 1);
			}
			menu4_edt.setText(number);
		} else {
			if (v instanceof Button) {
				String textadd = ((Button) v).getText().toString();
				String number = menu4_edt.getText().toString() + textadd;
				menu4_edt.setText(number);
				if (dtms != null) {
					dtms.onDtms(number);
				}
			}
		}

	}

	public String getNumber() {
		return menu4_edt.getText().toString();
	}

	public void clearNumber() {
		menu4_edt.setText("");
	}

	private IDTMS dtms;

	public void setDtms(IDTMS dtms) {
//		findViewById(R.id.keypadcall_control).setVisibility(View.GONE);
		this.dtms = dtms;
	}

	public interface IDTMS {
		void onDtms(String numder);
	}
}