package com.app.knock.db;

import org.json.JSONException;
import org.json.JSONObject;

import z.lib.base.CommonAndroid;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class TAGroup extends SkypeTable {

	public TAGroup(Context context) {
		super(context);
		addColumns("group_id");
		addColumns("group_name");
		addColumns("exten");
		addColumns("description");
		addColumns("user_id");
		addColumns("user_count");
		addColumns("update_date");
		addColumns("update_user");
	}

	@Override
	public Cursor search(String search) {
		StringBuilder querry = new StringBuilder();
		querry.append(" group_name like '%").append(search).append("%' ");
		return querry(querry.toString(), "group_name");
	}

	public void add(JSONObject group) {
		try {
			ContentValues values = new ContentValues();
			values.put("group_id", CommonAndroid.getString(group, "group_id"));
			values.put("group_name", CommonAndroid.getString(group, "group_name"));
			values.put("exten", CommonAndroid.getString(group, "exten"));
			values.put("description", CommonAndroid.getString(group, "description"));
			values.put("user_count", CommonAndroid.getString(group, "user_count"));
			values.put("update_date", CommonAndroid.getString(group, "update_date"));
			values.put("update_user", CommonAndroid.getString(group, "update_user"));
			values.put("user_id", CommonAndroid.getString(group.getJSONObject("user"), "user_id"));

			String where = String.format("%s = '%s'", "group_id", CommonAndroid.getString(group, "group_id"));
			if (has(where)) {
				getContext().getContentResolver().update(getContentUri(), values, where, null);
			} else {
				getContext().getContentResolver().insert(getContentUri(), values);
			}
		} catch (JSONException e) {
		}
	}

	public void delete() {
		getContext().getContentResolver().delete(getContentUri(), null, null);
	}

}