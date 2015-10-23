package com.shopivr.component.view;

import z.lib.base.CommonAndroid;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.shopivrtablet.R;
import com.csipsimple.api.SipCallSession;
import com.csipsimple.api.SipCallSession.InvState;
import com.csipsimple.api.SipManager;
import com.shopivr.component.TAMainActivity;
import com.shopivr.component.base.TAMainApplication;
import com.shopivr.component.view.ContactView.ContactType;

//ta.com.component.view.Menu2View
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Menu2View extends LinearLayout implements View.OnClickListener {
	private KeyPadCallView keypaddcallmenu2;
	private View menu2_call;
	private RadioGroup menu_2_radiogroup;

	enum KeypadState {
		KeypadState_None, KeypadState_Transfering
	}

	private KeypadState keypadState = KeypadState.KeypadState_None;

	public Menu2View(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Menu2View(Context context) {
		super(context);
		init();
	}

	private void init() {
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.menu2, this);
		setVisibility(View.GONE);
		try {
			findViewById(R.id.menu2_main).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});
			menu2_call = CommonAndroid.getView(this, R.id.menu2_call);
			keypaddcallmenu2 = CommonAndroid.getView(this, R.id.keypaddcallmenu2);
			menu_2_radiogroup = CommonAndroid.getView(this, R.id.menu_2_radiogroup);
			menu2_call.setOnClickListener(this);
			setOnClickListener(this);

			menu_2_radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {

					if (keypadState == KeypadState.KeypadState_Transfering) {
						return;
					}
					LinearLayout layout = (LinearLayout) findViewById(R.id.menu2_main);
					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
					if (checkedId == R.id.menu_2_radio_1) {
						findViewById(R.id.menu_2_keypad).setVisibility(View.VISIBLE);
						findViewById(R.id.menu_2_contact).setVisibility(View.GONE);
						params.height = (int) getContext().getResources().getDimension(R.dimen.dimen_125dp);
					} else {
						findViewById(R.id.menu_2_keypad).setVisibility(View.GONE);
						findViewById(R.id.menu_2_contact).setVisibility(View.VISIBLE);
						params.height = (int) getContext().getResources().getDimension(R.dimen.dimen_140dp);
					}

					layout.setLayoutParams(params);
				}
			});

			findViewById(R.id.menu2_1).setOnClickListener(this);
			findViewById(R.id.menu2_2).setOnClickListener(this);
			findViewById(R.id.menu2_3).setOnClickListener(this);
			findViewById(R.id.menu2_4).setOnClickListener(this);

			findViewById(R.id.menu2_call_1).setOnClickListener(this);
			findViewById(R.id.menu2_call_2).setOnClickListener(this);
		} catch (Exception exception) {
		}
	}

	private void enableMenu(boolean b) {

		menu_2_radiogroup.findViewById(R.id.menu_2_radio_2).setEnabled(b);
		menu_2_radiogroup.findViewById(R.id.menu_2_radio_1).setEnabled(b);
	}

	@Override
	public void onClick(View v) {
		if (menu2_call == v) {
			String number = keypaddcallmenu2.getNumber();

			// den
			findViewById(R.id.menu2_call_contoll_1).setVisibility(View.VISIBLE);

			findViewById(R.id.menu2_call_2).setEnabled(false);

			TAMainActivity.getInstance().menu2CallNumber(number);
			menu2_call.setVisibility(View.GONE);

			keypadState = KeypadState.KeypadState_Transfering;
			enableMenu(false);

		} else if (R.id.menu2_1 == v.getId()) {

			TAMainActivity.getInstance().showContactView(ContactType.TRANSFER2, 1);
			setVisibility(View.GONE);
		} else if (R.id.menu2_2 == v.getId()) {

			TAMainActivity.getInstance().showContactView(ContactType.TRANSFER2, 2);
			setVisibility(View.GONE);
		} else if (R.id.menu2_3 == v.getId()) {

			TAMainActivity.getInstance().showContactView(ContactType.TRANSFER2, 3);
			setVisibility(View.GONE);
		} else if (R.id.menu2_4 == v.getId()) {

			TAMainActivity.getInstance().showContactView(ContactType.TRANSFER2, 4);
			setVisibility(View.GONE);
		} else if (R.id.menu2_call_1 == v.getId()) {
			if (invState == InvState.CONFIRMED) {
				TAMainApplication.getInstance().hangup(callId, 0);
			} else {
				TAMainApplication.getInstance().cancelTransfer();
			}
		} else if (R.id.menu2_call_2 == v.getId()) {
			if (invState == InvState.CONFIRMED) {
				TAMainActivity.getInstance().finishTransferToCall(callId);
			}
		} else {
			if (findViewById(R.id.menu2_call_contoll_1).getVisibility() == View.GONE) {
				TAMainActivity.getInstance().closeTransferMenu(v);
				setVisibility(View.GONE);
			}
		}
	}

	public void clear() {
		findViewById(R.id.menu2_call_contoll_1).setVisibility(View.GONE);
		menu2_call.setVisibility(View.VISIBLE);
		keypaddcallmenu2.clearNumber();
		((RadioButton) menu_2_radiogroup.findViewById(R.id.menu_2_radio_2)).setChecked(true);
	}

	private int callId;
	private int invState;

	public void callTranferNumber(Intent intent) {

		SipCallSession callSession = intent.getParcelableExtra(SipManager.EXTRA_CALL_INFO);
		callId = callSession.getCallId();
		int callState = callSession.getCallState();

		invState = callSession.getCallState();

		switch (callState) {
		case InvState.INCOMING: {

		}
			break;
		case InvState.CONNECTING: {
		}
			break;
		case InvState.EARLY: {
			// den
			findViewById(R.id.menu2_call_contoll_1).setVisibility(View.VISIBLE);
			menu2_call.setVisibility(View.GONE);

			// findViewById(R.id.menu2_call_2).setAlpha(0.2f);
			findViewById(R.id.menu2_call_2).setEnabled(false);
		}
			break;
		case InvState.CALLING: {

		}
			break;
		case InvState.CONFIRMED: {
			// dong y
			// findViewById(R.id.menu2_call_2).setAlpha(1.0f);
			findViewById(R.id.menu2_call_2).setEnabled(true);
		}
			break;
		case InvState.DISCONNECTED: {
			findViewById(R.id.menu2_call_contoll_1).setVisibility(View.GONE);
			menu2_call.setVisibility(View.VISIBLE);
			keypadState = KeypadState.KeypadState_None;

			enableMenu(true);
		}
			break;
		default:
			break;
		}
	}

	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		keypadState = KeypadState.KeypadState_None;
		if (keypaddcallmenu2 != null) {
			keypaddcallmenu2.clearNumber();
		}

		// clear();
	}
}