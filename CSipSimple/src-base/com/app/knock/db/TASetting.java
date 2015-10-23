package com.app.knock.db;

import z.lib.base.CommonAndroid;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class TASetting extends SkypeTable {

	public TASetting(Context context) {
		super(context);
		addColumns("settingname");
		addColumns("settingvalue");
	}

	public enum SettingType {
		server, user, password, shopcode, shopmode
	}

	public String get(SettingType type) {
		String where = String.format("%s = '%s'", "settingname", type.toString());
		Cursor cursor = querry(where);
		String data = "";
		if (cursor != null) {
			if (cursor.moveToNext()) {
				data = CommonAndroid.getString(cursor, "settingvalue");
			}
			cursor.close();
		}

		if (CommonAndroid.isBlank(data) && type == SettingType.server) {
			data = "192.168.2.6";
		}
		return data;
	}

	public void setSetting(SettingType type, String settingvalue) {
		String where = String.format("%s = '%s'", "settingname", type.toString());
		ContentValues values = new ContentValues();
		values.put("settingname", type.toString());
		values.put("settingvalue", settingvalue);
		if (has(where)) {
			getContext().getContentResolver().update(getContentUri(), values, where, null);
		} else {
			getContext().getContentResolver().insert(getContentUri(), values);
		}
	}
}