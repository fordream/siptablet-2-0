/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  If you own a pjsip commercial license you can also redistribute it
 *  and/or modify it under the terms of the GNU Lesser General Public License
 *  as an android library.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.csipsimple.widgets;

import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AlphabetIndexer;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.shopivrtablet.R;
import com.csipsimple.api.SipProfile;
import com.csipsimple.models.Filter;
import com.csipsimple.utils.Log;
import com.csipsimple.utils.contacts.ContactsAutocompleteAdapter;
import com.csipsimple.utils.contacts.ContactsWrapper;
import com.csipsimple.widgets.AccountChooserButton.OnAccountChangeListener;

import java.util.regex.Pattern;

public class EditSipUri extends LinearLayout implements TextWatcher, OnItemClickListener {

    protected static final String THIS_FILE = "EditSipUri";
    private AutoCompleteTextView dialUser;
    private AccountChooserButton accountChooserButtonText;
    private TextView domainTextHelper;
    private ListView completeList;
    private ContactAdapter contactsAdapter;
    private ContactsAutocompleteAdapter autoCompleteAdapter;

    public EditSipUri(Context context, AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setOrientation(VERTICAL);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.edit_sip_uri, this, true);

        dialUser = (AutoCompleteTextView) findViewById(R.id.dialtxt_user);
        accountChooserButtonText = (AccountChooserButton) findViewById(R.id.accountChooserButtonText);
        domainTextHelper = (TextView) findViewById(R.id.dialtxt_domain_helper);
        completeList = (ListView) findViewById(R.id.autoCompleteList);

        autoCompleteAdapter = new ContactsAutocompleteAdapter(context);

        // Map events
        accountChooserButtonText.setOnAccountChangeListener(new OnAccountChangeListener() {
            @Override
            public void onChooseAccount(SipProfile account) {
                updateDialTextHelper();
                long accId = SipProfile.INVALID_ID;
                if (account != null) {
                    accId = account.id;
                }
                autoCompleteAdapter.setSelectedAccount(accId);
            }
        });
        dialUser.addTextChangedListener(this);
        
        if(isInEditMode()) {
            // Don't bind cursor in this case
            return;
        }
        Cursor c = ContactsWrapper.getInstance().getContactsPhones(context);
        contactsAdapter = new ContactAdapter(context, c);
        completeList.setAdapter(contactsAdapter);
        completeList.setOnItemClickListener(this);

        dialUser.setAdapter(autoCompleteAdapter);
        

    }

    private class ContactAdapter extends SimpleCursorAdapter implements SectionIndexer {

        private AlphabetIndexer alphaIndexer;

        public ContactAdapter(Context context, Cursor c) {
            super(context, R.layout.contact_phone_list_item, c, new String[] {}, new int[] {});
            alphaIndexer = new AlphabetIndexer(c, ContactsWrapper.getInstance()
                    .getContactIndexableColumnIndex(c),
                    " ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);
            ContactsWrapper.getInstance().bindContactPhoneView(view, context, cursor);
        }

        @Override
        public int getPositionForSection(int arg0) {
            return alphaIndexer.getPositionForSection(arg0);
        }

        @Override
        public int getSectionForPosition(int arg0) {
            return alphaIndexer.getSectionForPosition(arg0);
        }

        @Override
        public Object[] getSections() {
            return alphaIndexer.getSections();
        }

    }

    public class ToCall {
        private Long accountId;
        private String callee;

        public ToCall(Long acc, String uri) {
            accountId = acc;
            callee = uri;
        }

        /**
         * @return the pjsipAccountId
         */
        public Long getAccountId() {
            return accountId;
        }

        /**
         * @return the callee
         */
        public String getCallee() {
            return callee;
        }
    };

    private void updateDialTextHelper() {

        String dialUserValue = dialUser.getText().toString();

        accountChooserButtonText.setChangeable(TextUtils.isEmpty(dialUserValue));

        SipProfile acc = accountChooserButtonText.getSelectedAccount();
        if (!Pattern.matches(".*@.*", dialUserValue) && acc != null
                && acc.id > SipProfile.INVALID_ID) {
            domainTextHelper.setText("@" + acc.getDefaultDomain());
        } else {
            domainTextHelper.setText("");
        }

    }

    /**
     * Retrieve the value of the selected sip uri
     * 
     * @return the contact to call as a ToCall object containing account to use
     *         and number to call
     */
    public ToCall getValue() {
        String userName = dialUser.getText().toString();
        String toCall = "";
        Long accountToUse = null;
        if (TextUtils.isEmpty(userName)) {
            return null;
        }
        userName = userName.replaceAll("[ \t]", "");
        SipProfile acc = accountChooserButtonText.getSelectedAccount();
        if (acc != null) {
            accountToUse = acc.id;
            // If this is a sip account
            if (accountToUse > SipProfile.INVALID_ID) {
                if (Pattern.matches(".*@.*", userName)) {
                    toCall = "sip:" + userName + "";
                } else if (!TextUtils.isEmpty(acc.getDefaultDomain())) {
                    toCall = "sip:" + userName + "@" + acc.getDefaultDomain();
                } else {
                    toCall = "sip:" + userName;
                }
            } else {
                toCall = userName;
            }
        } else {
            toCall = userName;
        }

        return new ToCall(accountToUse, toCall);
    }

    public SipProfile getSelectedAccount() {
        return accountChooserButtonText.getSelectedAccount();
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        updateDialTextHelper();
    }

    @Override
    public void afterTextChanged(Editable s) {
        updateDialTextHelper();
    }

    /**
     * Reset content of the field
     * @see Editable#clear()
     */
    public void clear() {
        dialUser.getText().clear();
    }
    
    /**
     * Set the content of the field
     * @param number The new content to set in the field 
     */
    public void setTextValue(String number) {
        clear();
        dialUser.getText().append(number);
    }

    /**
     * Retrieve the underlying text field of this widget to modify it's behavior directly
     * @return the underlying widget
     */
    public EditText getTextField() {
        return dialUser;
    }


    @Override
    public void onItemClick(AdapterView<?> ad, View view, int position, long arg3) {
        String number = (String) view.getTag();
        SipProfile account = accountChooserButtonText.getSelectedAccount();
        String rewritten = Filter.rewritePhoneNumber(getContext(), account.id, number);
        setTextValue(rewritten);
        Log.d(THIS_FILE, "Clicked contact " + number);
    }

    public void setShowExternals(boolean b) {
        accountChooserButtonText.setShowExternals(b);
    }
}
