package com.shopivr.component.view.custom.font;

import com.shopivr.component.base.TAMainApplication;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
//ta.com.component.view.custom.font.RegularTextView
public class RegularTextView extends TextView {

	public RegularTextView(Context context) {
		super(context);
		init();
	}

	public RegularTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RegularTextView(Context context, AttributeSet attrs, int defStyle) {
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