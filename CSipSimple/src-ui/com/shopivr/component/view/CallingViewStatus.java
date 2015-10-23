package com.shopivr.component.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shopivrtablet.R;

//ta.com.component.view.CallingViewStatus
public class CallingViewStatus extends LinearLayout {

	public CallingViewStatus(Context context) {
		super(context);
		init();
		setWillNotDraw(false);
	}

	public CallingViewStatus(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		setWillNotDraw(false);

	}

	public CallingViewStatus(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		setWillNotDraw(false);
	}

	private Paint paint = new Paint();
	private int start = 40;

	private void init() {

	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		start++;

		// if (start > 50) {
		TextView textView = (TextView) findViewById(R.id.text1);
		if (start <= 10) {
			float x1 = (float) (start) / (float) 10 * (float) textView.getWidth();
			Shader shader = new LinearGradient(0, 0, x1, 0, Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR);
			textView.getPaint().setShader(shader);
		} else if (start <= 20) {
			float x1 = (float) (start - 10) / (float) 10 * (float) textView.getWidth();
			Shader shader = new LinearGradient(0, 0, x1, 0, Color.WHITE, Color.BLACK, Shader.TileMode.MIRROR);
			textView.getPaint().setShader(shader);
		} else {
			// float x1 = (float) (start) / (float) 10 * (float)
			// textView.getWidth();
			// Shader shader = new LinearGradient(0, 0, x1, 0, Color.WHITE,
			// Color.WHITE, Shader.TileMode.CLAMP);
			if (textView != null && textView.getPaint() != null)
				textView.getPaint().setShader(null);
		}

		if (start == 100) {
			start = 0;
		}
		if (textView != null)
			textView.invalidate();
		invalidate();
	}

	public void show(boolean isShow) {
		setVisibility(isShow ? VISIBLE : GONE);
		start = 0;
	}
}