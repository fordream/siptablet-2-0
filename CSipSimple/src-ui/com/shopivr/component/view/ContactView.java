package com.shopivr.component.view;

import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.callback.TACallBack;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.knock.db.TAGroup;
import com.app.knock.db.TAShareTel;
import com.app.knock.db.TAUser;
import com.shopivrtablet.R;
import com.csipsimple.api.SipCallSession;
import com.csipsimple.api.SipCallSession.InvState;
import com.csipsimple.api.SipManager;
import com.csipsimple.api.SipUri;
import com.csipsimple.api.SipUri.ParsedSipContactInfos;
import com.shopivr.component.TAMainActivity;
import com.shopivr.component.base.TAMainApplication;
import com.shopivr.component.base.TAMainApplication.ClientPresentCallState;
import com.shopivr.service.utils.TAUtils.TAAddressBook;

//ta.com.component.view.ContactView
public class ContactView extends LinearLayout implements View.OnClickListener, OnItemClickListener {
	private ContactAdapter contactAdapter;
	private com.shopivr.component.view.AlphbelListView list_alphabel;
	private TextWatcher watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (contactAdapter != null) {
				contactAdapter.getFilter().filter(s.toString());
			}
		}
	};
	private ListView contact_list_listview;
	private EditText contact_list_edt;

	// private int callState = InvState.INVALID;

	public ContactView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ContactView(Context context) {
		super(context);
		init();
	}

	private void init() {
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.contact, this);
		list_alphabel = CommonAndroid.getView(this, R.id.list_alphabel);
		contact_list_listview = CommonAndroid.getView(this, R.id.contact_list_listview);

		contact_list_edt = CommonAndroid.getView(this, R.id.contact_list_edt);

		try {
			contact_list_listview.setOnItemClickListener(this);
			findViewById(R.id.contact_main).setOnClickListener(this);
			contact_list_edt.addTextChangedListener(watcher);
			findViewById(R.id.contact_list_x).setOnClickListener(this);
			setOnClickListener(this);

			findViewById(R.id.contact_detail_back).setOnClickListener(this);
			findViewById(R.id.contact_detail_x).setOnClickListener(this);
			contactAdapter = new ContactAdapter(getContext());
			contact_list_listview.setAdapter(contactAdapter);

			list_alphabel.setOnItemClickListener(alphabelOnItemClick);
		} catch (Exception exception) {
		}
	}

	private OnItemClickListener alphabelOnItemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String str = parent.getItemAtPosition(position).toString();
			int index = 0;
			if ("#".equals(str)) {
				index = 0;
			} else {
				index = contactAdapter.findIndex(str);
			}

			if (index >= 0) {
				contact_list_listview.setSelection(index);
			}
		}
	};

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.contact_list_x || v.getId() == R.id.contact_detail_x) {
			if ("".equals(mexten)) {
				setVisibility(View.GONE);
				TAMainActivity.getInstance().contactClose1(mContactType);
			}
		} else if (v.getId() == R.id.contact_detail_back) {
			findViewById(R.id.contact_list).setVisibility(View.VISIBLE);
			findViewById(R.id.contact_detail).setVisibility(View.GONE);
		} else if (v == this) {
			if (mContactType == ContactType.TRANSFER2 && !CommonAndroid.isBlank(mexten)) {
				return;
			}
			setVisibility(View.GONE);
			// TAMainActivity.getInstance().contactClose(mContactType);
			TAMainActivity.getInstance().closeContactTransferMenu(mContactType);

		} else if (v.getId() == R.id.contact_detail_call_mn) {

		}
	}

	private ContactType mContactType = ContactType.TRANSFER2;
	private TAAddressBook contactType = TAAddressBook.address_book_type_business;

	/**
	 * 
	 * @param mContactType
	 *            2 is menu 2, 5 is menu 5
	 * @param contactType
	 */
	public enum ContactType {
		CONTACT5, TRANSFER2
	}

	public void show(ContactType type, int intContactType) {
		this.mContactType = type;
		String title = getContext().getString(R.string.menu_contact_1);
		if (intContactType == 1) {
			title = getContext().getString(R.string.menu_contact_1);
			this.contactType = TAAddressBook.address_book_type_business;
		} else if (intContactType == 2) {
			title = getContext().getString(R.string.menu_contact_2);
			this.contactType = TAAddressBook.address_book_type_sip_personal;
		} else if (intContactType == 3) {
			title = getContext().getString(R.string.menu_contact_3);
			this.contactType = TAAddressBook.address_book_type_group;
		} else if (intContactType == 4) {
			title = getContext().getString(R.string.menu_contact_4);
			this.contactType = TAAddressBook.address_book_type_local;
		}

		((TextView) CommonAndroid.getView(this, R.id.contact_detail_list_title)).setText(title);
		setVisibility(View.VISIBLE);
		findViewById(R.id.contact_list).setVisibility(View.VISIBLE);
		findViewById(R.id.contact_detail).setVisibility(View.GONE);
		// adapter
		contact_list_edt.setText("");
		contactAdapter.changeCursor(null);
		if (contactType == TAAddressBook.address_book_type_local) {
			contactAdapter.getFilter().filter("");
		} else {
			TAMainApplication.getInstance().getAddressBook(contactType, new TACallBack() {
				@Override
				public void onStart() {
					super.onStart();
					TAMainActivity.getInstance().showLoading(true, true);
				}

				@Override
				public void onSuccess(String status, String errorMessage, String response) {
					super.onSuccess(status, errorMessage, response);
					TAMainActivity.getInstance().showLoading(false, true);
					contactAdapter.getFilter().filter("");
				}
			});
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}

	private void showDetail(final String sharetel_id, final String group_id, final String user_id, final String idContact, final String et) {

		final View contact_detail_call_mn = findViewById(R.id.contact_detail_call_mn);
		contact_detail_call_mn.setEnabled(true);
		findViewById(R.id.contact_detail__call_1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mContactType == ContactType.TRANSFER2) {
					if (callId != SipCallSession.INVALID_CALL_ID) {
						((TAMainApplication) getContext().getApplicationContext()).hangup(callId, 0);
					} else {
						((TAMainApplication) getContext().getApplicationContext()).cancelTransfer();
					}
				} else {
					((TAMainApplication) getContext().getApplicationContext()).finishTransferToCall2(callId);
				}
			}
		});

		findViewById(R.id.contact_detail__call_2).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mContactType == ContactType.TRANSFER2) {
					TAMainActivity.getInstance().finishTransferToCall(callId);
				} else {
					((TAMainApplication) getContext().getApplicationContext()).finishTransferToCall2(callId);
				}
			}
		});

		findViewById(R.id.contact_list).setVisibility(View.GONE);
		findViewById(R.id.contact_detail).setVisibility(View.VISIBLE);

		TextView contact_detail_name = CommonAndroid.getView(this, R.id.contact_detail_name);
		TextView contact_deail_name_kana = CommonAndroid.getView(this, R.id.contact_deail_name_kana);
		TextView contact_detail_number = CommonAndroid.getView(this, R.id.contact_detail_number);
		TextView contact_detail_comment1 = CommonAndroid.getView(this, R.id.contact_detail_comment1);
		TextView contact_detail_comment2 = CommonAndroid.getView(this, R.id.contact_detail_comment2);

		contact_detail_name.setText("");
		contact_deail_name_kana.setText("");
		contact_detail_number.setText("");
		contact_detail_comment1.setText("");
		contact_detail_comment2.setText("");

		findViewById(R.id.contact_detail__call).setEnabled(true);

		showControlDetail(et);

		contact_detail_comment2.setVisibility(View.GONE);
		String phone = "";
		String exten = "";

		if (contactType == TAAddressBook.address_book_type_business) {
			TAShareTel taUser = new TAShareTel(getContext());
			Cursor cursor = taUser.querry(String.format("%s = '%s'", "sharetel_id", sharetel_id));
			if (cursor != null) {
				if (cursor.moveToNext()) {
					exten = CommonAndroid.getString(cursor, "exten");
					contact_detail_name.setText(String.format("%s %s", CommonAndroid.getString(cursor, "last_name"), CommonAndroid.getString(cursor, "fast_name")));
					contact_deail_name_kana.setText(String.format("%s %s", CommonAndroid.getString(cursor, "last_name_kana"), CommonAndroid.getString(cursor, "fast_name_kana")));
					contact_detail_number.setText(CommonAndroid.getString(cursor, "exten"));
					contact_detail_comment1.setText(CommonAndroid.getString(cursor, "description"));

					if ((!"".equals(mexten) && et.equals(mexten)) || ((TAMainApplication.getInstance().getClientCallState() != ClientPresentCallState.FREE) && mContactType == ContactType.CONTACT5)) {

						findViewById(R.id.contact_detail__call).setVisibility(View.GONE);
					} else {
						if ((TAMainApplication.getInstance().getClientCallState() == ClientPresentCallState.FREE)) {
							findViewById(R.id.contact_detail__call).setVisibility(View.VISIBLE);

						} else {
							// contact type is transfer
							if (CommonAndroid.isBlank(mexten)) {
								findViewById(R.id.contact_detail__call).setVisibility(View.VISIBLE);
								findViewById(R.id.contact_detail__call).setEnabled(true);
							} else {
								findViewById(R.id.contact_detail__call).setVisibility(View.VISIBLE);
								findViewById(R.id.contact_detail__call).setEnabled(false);
								findViewById(R.id.contact_detail__call).setAlpha(0.5f);
								contact_detail_call_mn.setEnabled(false);
							}
						}
					}
				}
				cursor.close();
			}
		} else if (contactType == TAAddressBook.address_book_type_sip_personal) {
			TAUser taUser = new TAUser(getContext());
			Cursor cursor = taUser.querry(String.format("%s = '%s'", "user_id", user_id));
			if (cursor != null) {
				if (cursor.moveToNext()) {
					exten = CommonAndroid.getString(cursor, "exten");
					contact_detail_name.setText(String.format("%s %s", CommonAndroid.getString(cursor, "last_name"), CommonAndroid.getString(cursor, "fast_name")));
					contact_deail_name_kana.setText(String.format("%s %s", CommonAndroid.getString(cursor, "last_name_kana"), CommonAndroid.getString(cursor, "fast_name_kana")));
					contact_detail_number.setText(CommonAndroid.getString(cursor, "exten"));
					contact_detail_comment1.setText(CommonAndroid.getString(cursor, "description"));
					String status = CommonAndroid.getString(cursor, "status");

					// LogUtils.e("TRANSFER", "MEXTEN:" + mexten +
					// "  CONTACT TYPE:" + mContactType + " CLIENT STATE:" +
					// TAMainApplication.getInstance().getClientCallState());

					if ((!"".equals(mexten) && et.equals(mexten)) || ((TAMainApplication.getInstance().getClientCallState() != ClientPresentCallState.FREE) && mContactType == ContactType.CONTACT5)) {

						findViewById(R.id.contact_detail__call).setVisibility(View.GONE);
					} else {
						if ("1".equals(status)) {
							findViewById(R.id.contact_detail__call).setVisibility(View.VISIBLE);
						} else {
							// findViewById(R.id.contact_detail__call).setVisibility(View.GONE);
							findViewById(R.id.contact_detail__call).setVisibility(View.VISIBLE);

							findViewById(R.id.contact_detail__call).setEnabled(false);
							findViewById(R.id.contact_detail__call).setAlpha(0.5f);
							contact_detail_call_mn.setEnabled(false);
						}
					}
				}
				cursor.close();
			}
		} else if (contactType == TAAddressBook.address_book_type_group) {
			TAGroup taUser = new TAGroup(getContext());
			Cursor cursor = taUser.querry(String.format("%s = '%s'", "group_id", group_id));
			if (cursor != null) {
				if (cursor.moveToNext()) {
					exten = CommonAndroid.getString(cursor, "exten");
					contact_detail_name.setText(String.format("%s", CommonAndroid.getString(cursor, "group_name")));
					contact_deail_name_kana.setText(String.format("%s", CommonAndroid.getString(cursor, "group_name")));
					contact_detail_number.setText(CommonAndroid.getString(cursor, "exten"));
					contact_detail_comment1.setText(CommonAndroid.getString(cursor, "description"));

					if ((!"".equals(mexten) && et.equals(mexten)) || ((TAMainApplication.getInstance().getClientCallState() != ClientPresentCallState.FREE) && mContactType == ContactType.CONTACT5)) {

						findViewById(R.id.contact_detail__call).setVisibility(View.GONE);
					} else {
						if ((TAMainApplication.getInstance().getClientCallState() == ClientPresentCallState.FREE)) {
							findViewById(R.id.contact_detail__call).setVisibility(View.VISIBLE);

						} else {
							// contact type is transfer
							if (CommonAndroid.isBlank(mexten)) {
								findViewById(R.id.contact_detail__call).setVisibility(View.VISIBLE);
								findViewById(R.id.contact_detail__call).setEnabled(true);
							} else {
								findViewById(R.id.contact_detail__call).setVisibility(View.VISIBLE);
								findViewById(R.id.contact_detail__call).setEnabled(false);
								findViewById(R.id.contact_detail__call).setAlpha(0.5f);
								contact_detail_call_mn.setEnabled(false);
							}
						}
					}
				}
				cursor.close();
			}
		} else {

			String where = String.format("%s = '%s'", ContactsContract.CommonDataKinds.Phone._ID, idContact);
			Cursor cursor = getContext().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, where, null, null);
			if (cursor != null) {
				exten = CommonAndroid.getString(cursor, "exten");
				if (cursor.moveToNext()) {
					String name = CommonAndroid.getString(cursor, ContactsContract.Contacts.DISPLAY_NAME);
					contact_detail_name.setText(name);
					contact_deail_name_kana.setText("");

					if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
						Cursor pCur = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
								new String[] { idContact }, null);
						if (pCur.moveToNext()) {
							phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						}
						pCur.close();
					}

					if ((!"".equals(mexten) && et.equals(mexten)) || ((TAMainApplication.getInstance().getClientCallState() != ClientPresentCallState.FREE) && mContactType == ContactType.CONTACT5)) {

						findViewById(R.id.contact_detail__call).setVisibility(View.GONE);
					} else {
						if ((TAMainApplication.getInstance().getClientCallState() == ClientPresentCallState.FREE)) {
							findViewById(R.id.contact_detail__call).setVisibility(View.VISIBLE);

						} else {
							// contact type is transfer
							if (CommonAndroid.isBlank(mexten)) {
								findViewById(R.id.contact_detail__call).setVisibility(View.VISIBLE);
								findViewById(R.id.contact_detail__call).setEnabled(true);
							} else {
								findViewById(R.id.contact_detail__call).setVisibility(View.VISIBLE);
								findViewById(R.id.contact_detail__call).setEnabled(false);
								findViewById(R.id.contact_detail__call).setAlpha(0.5f);
								contact_detail_call_mn.setEnabled(false);
							}
						}
					}

					contact_detail_number.setText(phone);
				}

				cursor.close();
			}

		}
		final String xPhone = phone;
		final String xxtent = exten;
		findViewById(R.id.contact_detail__call).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
//				LogUtils.e("BUGS", "make call from detail");
				if (mContactType == ContactType.TRANSFER2) {
					mexten = CommonAndroid.isBlank(xxtent) ? xPhone : xxtent;

					invState = InvState.EARLY;
					showControlDetail(mexten);

					contactAdapter.notifyDataSetChanged();
					TAMainActivity.getInstance().callFromContactTransfer(1, mexten);
				} else {
//					LogUtils.e("BUGS", "call to xPhone:" + xPhone + "  xxtent:" + xxtent);
					TAMainActivity.getInstance().callFromContactTransfer(2, CommonAndroid.isBlank(xxtent) ? xPhone : xxtent);
				}
			}
		});

		findViewById(R.id.contact_detail_call_mn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!CommonAndroid.isBlank(mexten) || (mContactType == ContactType.CONTACT5 && (TAMainApplication.getInstance().getClientCallState() != ClientPresentCallState.FREE))) {
					return;
				}

				View contact_detail__call = findViewById(R.id.contact_detail_call_mn);

				if (contact_detail__call.getVisibility() == View.VISIBLE && contact_detail__call.isEnabled()) {
					if (mContactType == ContactType.TRANSFER2) {
						mexten = CommonAndroid.isBlank(xxtent) ? xPhone : xxtent;

						invState = InvState.EARLY;
						showControlDetail(mexten);
						contactAdapter.notifyDataSetChanged();

						TAMainActivity.getInstance().callFromContactTransfer(1, mexten);
					} else {
						TAMainActivity.getInstance().callFromContactTransfer(2, CommonAndroid.isBlank(xxtent) ? xPhone : xxtent);
					}
				}
			}
		});

	}

	private class ContactAdapter extends CursorAdapter {
		// implements SectionIndexer
		public ContactAdapter(Context context) {
			super(context, null, true);
		}

		public int findIndex(String str) {

			String[] alphabel_1 = getContext().getResources().getStringArray(R.array.alphabel_1);
			String[] alphabel_2 = getContext().getResources().getStringArray(R.array.alphabel_2);

			String alphabel_1str = "";
			String alphabel_2str = "";

			for (int i = 0; i < alphabel_1.length; i++) {
				if (alphabel_1[i].contains(str) || alphabel_2[i].contains(str)) {
					alphabel_1str = alphabel_1[i];
					alphabel_2str = alphabel_2[i];
					break;
				}
			}

			Cursor cursor = getCursor();
			int index = -1;
			if (cursor != null && cursor.moveToFirst()) {

			}

			while (cursor != null && cursor.moveToNext()) {
				int mindex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
				if (mindex < 0) {
					mindex = cursor.getColumnIndex("group_name");
				}

				if (mindex < 0) {
					mindex = cursor.getColumnIndex("last_name_kana");
				}

				if (mindex >= 0) {
					String data = cursor.getString(mindex);
					String first = "";
					if (!CommonAndroid.isBlank(data)) {
						first = data.substring(0, 1);
					}
					if (data.startsWith(str)) {
						index = cursor.getPosition();
						break;
					}

					if (!CommonAndroid.isBlank(alphabel_1str) && (alphabel_1str.contains(first) || alphabel_2str.contains(first))) {
						index = cursor.getPosition();
						break;
					}
				}
			}
			return index;
		}

		@Override
		public Filter getFilter() {
			return new Filter() {

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					changeCursor((Cursor) results.values);
					contact_list_listview.setAdapter(contactAdapter);

				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();
					String search = constraint.toString();
					if (contactType == TAAddressBook.address_book_type_business) {
						results.values = new TAShareTel(getContext()).search(search);
					} else if (contactType == TAAddressBook.address_book_type_sip_personal) {
						results.values = new TAUser(getContext()).search(search);
					} else if (contactType == TAAddressBook.address_book_type_group) {
						results.values = new TAGroup(getContext()).search(search);
					} else {
						StringBuilder querry = new StringBuilder();
						// TODO
						querry.append(ContactsContract.Contacts.DISPLAY_NAME).append(" like '%").append(search).append("%'");
						results.values = getContext().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, querry.toString(), null, ContactsContract.Contacts.DISPLAY_NAME);
					}

					return results;
				}
			};
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public void bindView(View convertView, Context context, Cursor cursor) {

			if (convertView == null) {
				convertView = CommonAndroid.initView(context, R.layout.contact_item, null);
			}

			View contact_item_call = convertView.findViewById(R.id.contact_item_call);
			contact_item_call.setVisibility(View.VISIBLE);
			contact_item_call.setBackgroundResource(R.drawable.contact_call);

			View contact_item_infor = convertView.findViewById(R.id.contact_item_infor);
			contact_item_infor.setBackgroundResource(R.drawable.contact_infor);
			contact_item_infor.setEnabled(true);
			contact_item_infor.setVisibility(View.VISIBLE);

			final View callBtn = convertView.findViewById(R.id.contact_item_call);
			final View hangupBtn = convertView.findViewById(R.id.contact_item_call_1);
			final View transferBtn = convertView.findViewById(R.id.contact_item_call_2);

			hangupBtn.setVisibility(View.GONE);
			transferBtn.setVisibility(View.GONE);

			final String exten = CommonAndroid.getString(cursor, "exten");
			final String sharetel_id = CommonAndroid.getString(cursor, "sharetel_id");
			final String group_id = CommonAndroid.getString(cursor, "group_id");
			final String user_id = CommonAndroid.getString(cursor, "user_id");
			final String idContacts = CommonAndroid.getString(cursor, ContactsContract.Contacts._ID);

			String phone = "";
			TextView contact_item_text_name = CommonAndroid.getView(convertView, R.id.contact_item_text_name);
			TextView contact_item_text_number = CommonAndroid.getView(convertView, R.id.contact_item_text_number);

			contact_item_text_name.setTextColor(Color.BLACK);
			contact_item_text_number.setTextColor(Color.BLACK);
			convertView.findViewById(R.id.contact_item_call).setEnabled(true);
			if (contactType == TAAddressBook.address_book_type_business) {
				contact_item_text_name.setText(String.format("%s%s", CommonAndroid.getString(cursor, "last_name"), CommonAndroid.getString(cursor, "fast_name")));
				contact_item_text_number.setText(CommonAndroid.getString(cursor, "exten"));
			} else if (contactType == TAAddressBook.address_book_type_sip_personal) {
				contact_item_text_name.setText(String.format("%s%s", CommonAndroid.getString(cursor, "last_name"), CommonAndroid.getString(cursor, "fast_name")));
				contact_item_text_number.setText(CommonAndroid.getString(cursor, "exten"));
			} else if (contactType == TAAddressBook.address_book_type_group) {
				contact_item_text_name.setText(CommonAndroid.getString(cursor, "group_name"));
				contact_item_text_number.setText(CommonAndroid.getString(cursor, "exten"));
			} else {

				String name = CommonAndroid.getString(cursor, ContactsContract.Contacts.DISPLAY_NAME);
				contact_item_text_name.setText(name);
				if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					Cursor pCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
							new String[] { idContacts }, null);
					if (pCur.moveToNext()) {
						phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					}
					pCur.close();
				}

				contact_item_text_number.setText(phone);

				// have a phone --> show
				if (!CommonAndroid.isBlank(phone)) {
					convertView.findViewById(R.id.contact_item_x_main).setVisibility(View.VISIBLE);
				} else {
					convertView.findViewById(R.id.contact_item_x_main).setVisibility(View.GONE);
				}
			}

			final String xPhone = phone;
			hangupBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mContactType == ContactType.TRANSFER2) {
						LogUtils.i("TRANSFER", "CANCEL:" + callId);
						if (callId != SipCallSession.INVALID_CALL_ID) {
							((TAMainApplication) getContext().getApplicationContext()).hangup(callId, 0);
						} else {
							((TAMainApplication) getContext().getApplicationContext()).cancelTransfer();
						}
					} else {
						((TAMainApplication) getContext().getApplicationContext()).hangup(callId, 0);
					}
				}
			});
			transferBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mContactType == ContactType.TRANSFER2) {
						TAMainActivity.getInstance().finishTransferToCall(callId);
					} else {
						((TAMainApplication) getContext().getApplicationContext()).finishTransferToCall2(callId);
					}
				}
			});

			callBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (mContactType == ContactType.TRANSFER2) {
						LogUtils.e("CALL-LOG", "--- TOUCH ---:" + exten + " xPhone:" + xPhone);

						mexten = CommonAndroid.isBlank(exten) ? xPhone : exten;
						;
						TAMainActivity.getInstance().callFromContactTransfer(1, mexten);

						invState = InvState.EARLY;

						callBtn.setVisibility(View.GONE);
						hangupBtn.setVisibility(View.VISIBLE);
						transferBtn.setVisibility(View.VISIBLE);
						transferBtn.setEnabled(false);

						showControlDetail(exten);
						contactAdapter.notifyDataSetChanged();
					} else {
						TAMainActivity.getInstance().callFromContactTransfer(2, CommonAndroid.isBlank(exten) ? xPhone : exten);
					}
				}
			});

			callClickNumber((RelativeLayout) convertView.findViewById(R.id.notification_item_header), convertView.findViewById(R.id.contact_item_call), exten, xPhone);

			String status = CommonAndroid.getString(cursor, "status");
			if (contactType != TAAddressBook.address_book_type_sip_personal) {
				status = "1";
			}

			if (TAMainApplication.getInstance().getClientCallState() == TAMainApplication.ClientPresentCallState.FREE) {
				if ("1".equals(status)) {

					contact_item_call.setVisibility(View.VISIBLE);
					contact_item_call.setEnabled(true);

					contact_item_infor.setEnabled(true);
					contact_item_infor.setBackgroundResource(R.drawable.contact_infor);

					contact_item_text_name.setTextColor(Color.BLACK);
					contact_item_text_number.setTextColor(Color.BLACK);
				} else {
					contact_item_call.setEnabled(false);
					contact_item_call.setBackgroundResource(R.drawable.t_1);

					contact_item_infor.setEnabled(true);
					contact_item_infor.setBackgroundResource(R.drawable.contact_infor);

					contact_item_text_name.setTextColor(Color.GRAY);
					contact_item_text_number.setTextColor(Color.GRAY);
				}
			} else {

				if (mContactType == ContactType.CONTACT5) {
					contact_item_call.setVisibility(View.GONE);
					contact_item_infor.setEnabled(true);
					contact_item_infor.setBackgroundResource(R.drawable.contact_infor);
					if ("1".equals(status)) {
						contact_item_text_name.setTextColor(Color.BLACK);
						contact_item_text_number.setTextColor(Color.BLACK);
					} else {
						contact_item_text_name.setTextColor(Color.GRAY);
						contact_item_text_number.setTextColor(Color.GRAY);
					}
				} else if (mContactType == ContactType.TRANSFER2) { // for
					// 1 OKIE
					LogUtils.i("ClientCallState", status);
					if ("1".equals(status)) {
						contact_item_infor.setVisibility(View.VISIBLE);
						contact_item_infor.setBackgroundResource(R.drawable.contact_infor);

						contact_item_call.setBackgroundResource(R.drawable.contact_call);
						contact_item_call.setVisibility(View.VISIBLE);
						contact_item_call.setEnabled(true);

						contact_item_text_name.setTextColor(Color.BLACK);
						contact_item_text_number.setTextColor(Color.BLACK);
					} else {
						contact_item_infor.setEnabled(false);
						contact_item_infor.setBackgroundResource(R.drawable.t_2);

						contact_item_call.setBackgroundResource(R.drawable.t_1);
						contact_item_call.setVisibility(View.VISIBLE);
						contact_item_call.setEnabled(false);

						contact_item_text_name.setTextColor(Color.GRAY);
						contact_item_text_number.setTextColor(Color.GRAY);
					}

					String ex = CommonAndroid.isBlank(exten) ? xPhone : exten;
					if (!CommonAndroid.isBlank(ex)) {
						if (ex.equals(mexten)) {
							callBtn.setVisibility(View.GONE);
							hangupBtn.setVisibility(View.VISIBLE);
							transferBtn.setVisibility(View.VISIBLE);
							if (invState == InvState.CONFIRMED) {
								transferBtn.setEnabled(true);
							} else {
								transferBtn.setEnabled(false);
							}
						} else {
							callBtn.setVisibility(View.VISIBLE);
							transferBtn.setVisibility(View.GONE);
							hangupBtn.setVisibility(View.GONE);

							if (!CommonAndroid.isBlank(mexten)) {
								contact_item_infor.setEnabled(false);
								contact_item_infor.setBackgroundResource(R.drawable.t_2);

								contact_item_call.setBackgroundResource(R.drawable.t_1);
								contact_item_call.setVisibility(View.VISIBLE);
								contact_item_call.setEnabled(false);

								contact_item_text_name.setTextColor(Color.GRAY);
								contact_item_text_number.setTextColor(Color.GRAY);
							}
						}
					}
				}
			}

			convertView.findViewById(R.id.contact_item_infor).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showDetail(sharetel_id, group_id, user_id, idContacts, CommonAndroid.isBlank(exten) ? xPhone : exten);
				}
			});
		}

		private void callClickNumber(RelativeLayout contact_item_text_number, final View contact_item_call, final String exten, final String xPhone) {
			contact_item_text_number.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!CommonAndroid.isBlank(mexten)) {
						return;
					}
					if (contact_item_call.isEnabled()) {
						if (mContactType == ContactType.TRANSFER2) {
							mexten = CommonAndroid.isBlank(exten) ? xPhone : exten;

							invState = InvState.EARLY;
							contactAdapter.notifyDataSetChanged();

							showControlDetail(mexten);

							TAMainActivity.getInstance().callFromContactTransfer(1, mexten);
						} else {
							if (TAMainApplication.getInstance().getClientCallState() != ClientPresentCallState.FREE) {
								return;
							}
							TAMainActivity.getInstance().callFromContactTransfer(2, CommonAndroid.isBlank(exten) ? xPhone : exten);
						}
					}
				}
			});
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup parent) {
			View convertView = CommonAndroid.initView(parent.getContext(), R.layout.contact_item, null);
			bindView(convertView, arg0, arg1);
			return convertView;
		}
	}

	private int callId = SipCallSession.INVALID_CALL_ID;
	private int invState;
	private String mexten = "";

	@Override
	public void setVisibility(int visibility) {
		mexten = "";
		invState = -1;
		showControlDetail("");
		if (contact_list_edt != null) {
			contact_list_edt.setText("");
		}

		super.setVisibility(visibility);

		list_alphabel.clear();
	}

	public void callTranferNumber(Intent intent) {
		SipCallSession callSession = intent.getParcelableExtra(SipManager.EXTRA_CALL_INFO);
		callId = callSession.getCallId();
		ParsedSipContactInfos contactInfo = SipUri.parseSipContact(callSession.getRemoteContact());
		mexten = contactInfo.userName;
		invState = callSession.getCallState();
		switch (invState) {
		case InvState.INCOMING: {

		}
			break;
		case InvState.CONNECTING: {
		}
			break;
		case InvState.EARLY: {
			// den
			// contactAdapter.notifyDataSetChanged();
			showControlDetail(mexten);
		}
			break;
		case InvState.CALLING: {

		}
			break;
		case InvState.CONFIRMED: {
			// dong y
			contactAdapter.notifyDataSetChanged();
			showControlDetail(mexten);
		}
			break;
		case InvState.DISCONNECTED: {
			// ket thuc
			callId = SipCallSession.INVALID_CALL_ID;
			mexten = "";
			contactAdapter.notifyDataSetChanged();
			showControlDetail(mexten);
		}
			break;
		default:
			break;
		}
	}

	private void showControlDetail(String exten) {
		LogUtils.e("TRANSFER", "__EXTEN:" + exten + "__MEXTEN:" + mexten);
		if (!CommonAndroid.isBlank(exten) && exten.equals(mexten)) {
			LogUtils.i("TRANSFER", "ContactView:State:" + invState);
			switch (invState) {
			case InvState.INCOMING: {
			}
				break;
			case InvState.CALLING:
			case InvState.CONNECTING:
			case InvState.EARLY: {
				LogUtils.i("TRANSFER", "UPDATE UI");
				// den
				findViewById(R.id.contact_detail__call).setVisibility(View.GONE);
				findViewById(R.id.contact_detail__call_1).setVisibility(View.VISIBLE);

				findViewById(R.id.contact_detail__call_2).setVisibility(View.VISIBLE);
				findViewById(R.id.contact_detail__call_2).setEnabled(false);
			}
				break;
			case InvState.CONFIRMED: {
				// dong y
				findViewById(R.id.contact_detail__call).setVisibility(View.GONE);
				findViewById(R.id.contact_detail__call_1).setVisibility(View.VISIBLE);

				findViewById(R.id.contact_detail__call_2).setVisibility(View.VISIBLE);
				findViewById(R.id.contact_detail__call_2).setEnabled(true);
			}
				break;
			case InvState.DISCONNECTED: {
				// ket thuc

				findViewById(R.id.contact_detail__call).setVisibility(View.VISIBLE);
				findViewById(R.id.contact_detail__call_1).setVisibility(View.GONE);
				findViewById(R.id.contact_detail__call_2).setVisibility(View.GONE);

			}
				break;
			default:
				findViewById(R.id.contact_detail__call).setVisibility(View.VISIBLE);
				findViewById(R.id.contact_detail__call_1).setVisibility(View.GONE);
				findViewById(R.id.contact_detail__call_2).setVisibility(View.GONE);
				break;
			}
		} else {
			findViewById(R.id.contact_detail__call).setEnabled(true);
			findViewById(R.id.contact_detail__call).setAlpha(1.0f);
			findViewById(R.id.contact_detail__call).setVisibility(View.VISIBLE);

			findViewById(R.id.contact_detail__call_1).setVisibility(View.GONE);
			findViewById(R.id.contact_detail__call_2).setVisibility(View.GONE);

			if ((TAMainApplication.getInstance().getClientCallState() == ClientPresentCallState.IN_CALLING) && mContactType == ContactType.CONTACT5) {
				findViewById(R.id.contact_detail__call).setVisibility(View.GONE);
			}
		}
	}

	public boolean neeOnBackPressed(SipCallSession[] calls) {

		if (getVisibility() == GONE) {
			return true;
		}

		if (findViewById(R.id.contact_detail).getVisibility() == View.VISIBLE) {
			onClick(findViewById(R.id.contact_detail_back));
			return false;
		} else if (calls.length == 2) {
			return false;
		}

		return true;
	}

}
