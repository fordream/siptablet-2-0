/**
 * 
 */
package z.lib.base;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * @author tvuong1pc
 * 
 */
public abstract class BaseAdapter extends ArrayAdapter<Object> {
	private List<Object> lData;

	public List<Object> getlData() {
		return lData;
	}

	/**
	 * @param context
	 * @param textViewResourceId
	 */
	public BaseAdapter(Context context, List<Object> lData) {
		super(context, 0, lData);
		this.lData = lData;
	}

	public void addAllItems(List<?> list) {
		this.lData.clear();
		this.lData.addAll(list);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Object data = getItem(position);

		if (convertView == null) {
			convertView = getView(getContext(), data);
		}

		((BaseView) convertView).setData(data);
		((BaseView) convertView).refresh();
		return convertView;
	}

	public abstract BaseView getView(Context context, Object data);
}