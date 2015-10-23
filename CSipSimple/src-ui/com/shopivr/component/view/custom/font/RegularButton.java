package com.shopivr.component.view.custom.font;

import com.shopivr.component.base.TAMainApplication;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class RegularButton extends Button {

	public RegularButton(Context context) {
		super(context);
		init();
	}

	public RegularButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RegularButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		try {
			//setTypeface(TAMainApplication.getInstance().getRegular());
		} catch (Exception ex) {
		}
	}
}