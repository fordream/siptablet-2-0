package com.shopivr.component.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RadioButton;
//ta.com.component.view.RadioButtonContactView
public class RadioButtonContactView extends RadioButton {

	public RadioButtonContactView(Context context) {
		super(context);
	}

	public RadioButtonContactView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RadioButtonContactView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setChecked(boolean checked) {
		super.setChecked(checked);
		if (checked) {
			setTextColor(Color.WHITE);
		} else {
			setTextColor(Color.GRAY);
		}
	}

}
