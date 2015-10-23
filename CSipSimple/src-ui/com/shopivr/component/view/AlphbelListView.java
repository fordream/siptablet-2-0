package com.shopivr.component.view;

import z.lib.base.CommonAndroid;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.shopivrtablet.R;

//ta.com.component.view.AlphbelListView
public class AlphbelListView extends ListView {

	public AlphbelListView(Context context) {
		super(context);
		setAdapter(adapter);
		adapter.addAlphabel();
	}

	public AlphbelListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setAdapter(adapter);
		adapter.addAlphabel();
	}

	public AlphbelListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setAdapter(adapter);
		adapter.addAlphabel();
	}

	private AlphabelAdapter adapter = new AlphabelAdapter();

	private class AlphabelAdapter extends BaseAdapter {
		private String data = "";

		@Override
		public int getCount() {
			return data.length();
		}

		@Override
		public Object getItem(int position) {
			return data.charAt(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = CommonAndroid.initView(parent.getContext(), R.layout.alpha_item, null);
			}

			TextView textView = (TextView) convertView.findViewById(R.id.alpha_item_txt);
			textView.setText(getItem(position).toString());
			textView.setTextColor(Color.GRAY);
			String data = getItem(position).toString();

			int color = Color.GRAY;
			if (data.equals("-")) {
				textView.setBackgroundResource(R.drawable.tranfer);
			} else {
				if (position == choose) {
					color = Color.WHITE;
					textView.setBackgroundResource(R.drawable.img_cirlce_blue);
				} else {
					textView.setBackgroundResource(R.drawable.tranfer);
				}
			}
			textView.setTextColor(color);
			return convertView;
		}

		public void addAlphabel() {
			data = getContext().getString(R.string.sort);
			notifyDataSetChanged();
		}

		private int choose = -1;

		public void clear() {
			choose = -1;
			adapter.notifyDataSetChanged();
		}

		public void setChooser(int position) {
			choose = position;
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void setOnItemClickListener(final android.widget.AdapterView.OnItemClickListener listener) {
		// super.setOnItemClickListener(listener);
		super.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				adapter.setChooser(position);
				listener.onItemClick(parent, view, position, id);
			}
		});
	}

	public void clear() {
		adapter.clear();
	}
}