package com.shopivr.component.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csipsimple.api.ISipService;
import com.shopivr.component.base.TAMainApplication;

@SuppressLint("NewApi")
public class BaseView extends LinearLayout implements View.OnClickListener {

	public BaseView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setWillNotDraw(false);
		initLayout();
	}

	public BaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
		initLayout();
	}

	public BaseView(Context context) {
		super(context);
		setWillNotDraw(false);
		initLayout();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		onDrawOnChild(canvas);
		invalidate();
	}

	private int sessionCallId;

	public final int getSessionCallId() {
		return sessionCallId;
	}

	public void setSessionCallId(long sessionCallId) {
		this.sessionCallId = (int) sessionCallId;
	}

	public final ISipService getService() {
		return ((TAMainApplication) getContext().getApplicationContext())
				.getService();
	}

	public void onDrawOnChild(Canvas canvas) {

	}

	private void initLayout() {
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		init(li);

	}

	public void init(LayoutInflater li) {

	}

	public void addonClick(int[] res) {
		for (int _res : res)
			findViewById(_res).setOnClickListener(this);
	}

	public void setText(int res, String txt) {
		((TextView) findViewById(res)).setText(txt);
	}

	public void setEndable(int res, boolean isEnable) {
		((TextView) findViewById(res)).setEnabled(isEnable);
	}

	public TextView getTextView(int res) {
		return (TextView) findViewById(res);
	}

	public EditText getEditText(int res) {
		return (EditText) findViewById(res);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}