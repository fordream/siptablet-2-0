package com.shopivr.component.view;

import z.lib.base.CommonAndroid;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shopivrtablet.R;
import com.shopivr.component.TAMainActivity;

//ta.com.component.view.MenuNotificationView
public class MenuNotificationView extends LinearLayout implements View.OnClickListener {

	public MenuNotificationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MenuNotificationView(Context context) {
		super(context);
		init();
	}

	private void init() {
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.menunotification, this);
		setVisibility(View.GONE);
		if (((ListView) findViewById(R.id.notification_list)) != null)
			((ListView) findViewById(R.id.notification_list)).setAdapter(new NotificationAdapter());
		this.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		setVisibility(View.GONE);
		TAMainActivity.getInstance().closeMenu(v);
	}

	private class NotificationAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = CommonAndroid.initView(parent.getContext(), R.layout.notification_item, null);
			}

			TextView notification_item_time = CommonAndroid.getView(convertView, R.id.notification_item_time);
			TextView notification_item_status = CommonAndroid.getView(convertView, R.id.notification_item_status);
			TextView notification_item_title = CommonAndroid.getView(convertView, R.id.notification_item_title);
			final TextView notification_item_content = CommonAndroid.getView(convertView, R.id.notification_item_content);
			View notification_item_header = CommonAndroid.getView(convertView, R.id.notification_item_header);
			notification_item_content.setVisibility(View.GONE);
			notification_item_header.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (notification_item_content.getVisibility() == View.VISIBLE) {
						notification_item_content.setVisibility(View.GONE);
					} else {
						notification_item_content.setVisibility(View.VISIBLE);
					}
				}
			});
			return convertView;
		}

	}
}