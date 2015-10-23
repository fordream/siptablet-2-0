package com.shopivr.component.view.custom.font;

import com.shopivr.component.base.TAMainApplication;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
//ta.com.component.view.custom.font.BoldButton
public class BoldButton extends Button {

	public BoldButton(Context context) {
		super(context);
		init();
	}

	public BoldButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BoldButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		try {
			//setTypeface(TAMainApplication.getInstance().getBold());
		} catch (Exception ex) {
		}
	}
}