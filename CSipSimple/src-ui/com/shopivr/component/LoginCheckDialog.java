package com.shopivr.component;

import z.lib.base.BaseAdialog;
import z.lib.base.CommonAndroid;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.shopivrtablet.R;

public abstract class LoginCheckDialog extends BaseAdialog {

	private String message = "";

	public LoginCheckDialog(Context context, String message) {
		super(context);
		this.message = message;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((TextView) CommonAndroid.getView(this, R.id.popup_loigin_text)).setText(message);
		CommonAndroid.getView(this, R.id.popup_loigin_yes).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				onClickOk();
			}
		});
		CommonAndroid.getView(this, R.id.popup_loigin_no).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		if (!showButtonCancel) {
			CommonAndroid.getView(this, R.id.popup_loigin_no).setVisibility(View.GONE);
		}

		if (!CommonAndroid.isBlank(titleOkie)) {
			((Button) CommonAndroid.getView(this, R.id.popup_loigin_yes)).setText(titleOkie);
		}
	}

	@Override
	public int getLayout() {
		return R.layout.popup_login;
	}

	public abstract void onClickOk();

	private boolean showButtonCancel = true;

	public void setShowButtonCancel(boolean b) {
		showButtonCancel = b;
	}

	private String titleOkie = null;

	public void setTextButtonOkie(String title) {
		titleOkie = title;
	}
}