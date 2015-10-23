package com.shopivr.component.view;

import z.lib.base.CommonAndroid;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shopivrtablet.R;
import com.shopivr.component.TAMainActivity;

//ta.com.component.view.Menu3View
public class Menu3View extends LinearLayout implements View.OnClickListener {

	public Menu3View(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Menu3View(Context context) {
		super(context);
		init();
	}

	private ImageView rb1;
	private ImageView rb2;
	private ImageView rb3;
	private ImageView rb4;

	private void init() {
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.menu3, this);
		setVisibility(View.GONE);

		rb1 = CommonAndroid.getView(this, R.id.radio0);
		rb2 = CommonAndroid.getView(this, R.id.radio1);
		rb3 = CommonAndroid.getView(this, R.id.radio2);
		rb4 = CommonAndroid.getView(this, R.id.radio3);
		if (rb1 != null)
			rb1.setOnClickListener(onParkClick);
		if (rb2 != null)
			rb2.setOnClickListener(onParkClick);
		if (rb3 != null)
			rb3.setOnClickListener(onParkClick);
		if (rb4 != null)
			rb4.setOnClickListener(onParkClick);

		setOnClickListener(this);
		if (findViewById(R.id.menu3_main) != null)
			findViewById(R.id.menu3_main).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});
	}

	OnClickListener onParkClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			setVisibility(View.GONE);
			int index = 0;
			if (v.getId() == R.id.radio0) {
				index = 1;
			} else if (v.getId() == R.id.radio1) {
				index = 2;
			} else if (v.getId() == R.id.radio2) {
				index = 3;
			} else if (v.getId() == R.id.radio3) {
				index = 4;
			}

			TAMainActivity.getInstance().menu3Check(index);
		}
	};

	@Override
	public void onClick(View v) {
		setVisibility(View.GONE);
		TAMainActivity.getInstance().closeMenu(v);

	}

	public void parkBtnFlash(int parkNum, boolean state, boolean enable) {
		// int image;

		ImageView btnPark = rb1;
		int res = R.drawable.xml_menu3_1;
		boolean _enable = true;
		if (enable) {
			// btnPark.setChecked(state);
			_enable = state;
		} else {
			_enable = false;
			// btnPark.setChecked(false);
		}

		switch (parkNum) {
		case 1: {
			btnPark = rb1;
			res = _enable ? R.drawable.xml_menu3_1 : R.drawable.menu3_1_inactive;
			// if (enable) {
			// rb1.setChecked(state);
			// } else {
			// rb1.setChecked(false);
			// }
		}
			break;
		case 2: {
			btnPark = rb2;
			res = _enable ? R.drawable.xml_menu3_2 : R.drawable.menu3_2_inactive;
			// if (enable) {
			// rb2.setChecked(state);
			// } else {
			// rb2.setChecked(false);
			// }
		}
			break;
		case 3: {
			btnPark = rb3;
			res = _enable ? R.drawable.xml_menu3_3 : R.drawable.menu3_3_inactive;
			// if (enable) {
			// rb3.setChecked(state);
			// } else {
			// rb3.setChecked(false);
			// }
		}
			break;
		case 4: {
			btnPark = rb4;
			res = _enable ? R.drawable.xml_menu3_4 : R.drawable.menu3_4_inactive;
			// if (enable) {
			// rb4.setChecked(state);
			// } else {
			// rb4.setChecked(false);
			// }
		}
			break;
		default:
			break;
		}
		btnPark.setEnabled(_enable);
		btnPark.setBackgroundResource(res);
		// btnPark.setChecked(_enable);
	}
}