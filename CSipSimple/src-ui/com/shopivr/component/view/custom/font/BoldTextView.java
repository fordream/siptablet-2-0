package com.shopivr.component.view.custom.font;

import com.shopivr.component.base.TAMainApplication;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
//ta.com.component.view.custom.font.BoldTextView
public class BoldTextView extends TextView {

	public BoldTextView(Context context) {
		super(context);
		init();
	}

	public BoldTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BoldTextView(Context context, AttributeSet attrs, int defStyle) {
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