package z.lib.base;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BaseView extends LinearLayout {
	private Object data;

	public Context getActivity() {
		return getContext();
	}

	public String getTextStr(int forgotpasswordEdit) {
		return ((TextView) findViewById(forgotpasswordEdit)).getText().toString().trim();
	}

	public void showDialogMessage(String message) {
		CommonAndroid.showDialog(getContext(), message, null);
	}

	public void setText(String str, int res) {
		((TextView) findViewById(res)).setText(str);
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public void refresh() {

	}

	public BaseView(Context context) {
		super(context);
	}

	public BaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void init(int res) {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(res, this);
	}

	public void setTextStrHtml(int messagepostFooterText, String string) {
		((TextView) findViewById(messagepostFooterText)).setText(Html.fromHtml(string));
	}

	public void setTextStr(int messagepostFooterText, String string) {
		((TextView) findViewById(messagepostFooterText)).setText(string);
	}

}