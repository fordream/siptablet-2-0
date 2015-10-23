package com.app.knock.db;

import org.json.JSONObject;

import z.lib.base.CommonAndroid;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class TAShareTel extends SkypeTable {

	public TAShareTel(Context context) {
		super(context);
		addColumns("sharetel_id");
		addColumns("last_name");
		addColumns("fast_name");
		addColumns("last_name_kana");
		addColumns("fast_name_kana");
		addColumns("exten");
		addColumns("description");
		addColumns("update_date");
		addColumns("update_user");
		addColumns("user_id");
		addColumns("last_name_kana_fast_name_kana");
	}

	public void add(JSONObject group) {
		ContentValues values = new ContentValues();
		values.put("sharetel_id", CommonAndroid.getString(group, "sharetel_id"));
		values.put("last_name", CommonAndroid.getString(group, "last_name"));
		values.put("fast_name", CommonAndroid.getString(group, "fast_name"));
		values.put("last_name_kana", CommonAndroid.getString(group, "last_name_kana"));
		values.put("fast_name_kana", CommonAndroid.getString(group, "fast_name_kana"));
		
		String last_name_kana_fast_name_kana = CommonAndroid.getString(group, "last_name_kana") + CommonAndroid.getString(group, "fast_name_kana");
		values.put("last_name_kana_fast_name_kana", last_name_kana_fast_name_kana);

		values.put("exten", CommonAndroid.getString(group, "exten"));
		values.put("description", CommonAndroid.getString(group, "description"));
		values.put("update_date", CommonAndroid.getString(group, "update_date"));
		values.put("update_user", CommonAndroid.getString(group, "update_user"));

		String where = String.format("%s = '%s'", "sharetel_id", CommonAndroid.getString(group, "sharetel_id"));
		if (has(where)) {
			getContext().getContentResolver().update(getContentUri(), values, where, null);
		} else {
			getContext().getContentResolver().insert(getContentUri(), values);
		}
	}

	public void delete() {
		getContext().getContentResolver().delete(getContentUri(), null, null);
	}

	@Override
	public Cursor search(String search) {
		StringBuilder querry = new StringBuilder();
		querry.append(" last_name like '%").append(search).append("%' ");
		querry.append(" and last_name like '%").append(search).append("%' ");
		querry.append(" and last_name_kana like '%").append(search).append("%' ");
		querry.append(" and fast_name_kana like '%").append(search).append("%' ");
		return querry(querry.toString(), "last_name_kana");
	}
}