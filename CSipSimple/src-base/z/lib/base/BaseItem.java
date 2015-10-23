package z.lib.base;

import org.json.JSONObject;

public class BaseItem {
	public JSONObject jsonObject;

	public BaseItem(JSONObject jsonObject) {
		this.jsonObject = jsonObject;

		if (this.jsonObject == null) {
			jsonObject = new JSONObject();
		}
	}

	public BaseItem(String response) {
		try {
			jsonObject = new JSONObject(response);
		} catch (Exception e) {
		}
	}

	public String getString(String key) {
		try {
			return jsonObject.getString(key);
		} catch (Exception e) {
			return null;
		}
	}

	public Object getObject(String key) {
		try {
			return jsonObject.get(key);
		} catch (Exception e) {
			return null;
		}
	}
	
	public JSONObject getObject() {
		try {
			return jsonObject;
		} catch (Exception e) {
			return null;
		}
	}
}