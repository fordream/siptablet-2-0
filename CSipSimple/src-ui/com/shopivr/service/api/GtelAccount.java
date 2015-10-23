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
 *  
 *  This file and this file only is also released under Apache license as an API file
 */

package com.shopivr.service.api;

import android.os.Parcel;
import android.os.Parcelable;

public class GtelAccount implements Parcelable {
	private String user_id;
	private String sip_userid;

	public String getUser_id() {
		return user_id;
	}

	public String getSip_userid() {
		return sip_userid;
	}

	public void update(String user_id, String sip_userid) {
		this.user_id = user_id;
		this.sip_userid = sip_userid;
	}

	public void clear() {
		user_id = "";
		sip_userid = "";
	}

	public GtelAccount() {
	}

	private GtelAccount(Parcel in) {
		user_id = in.readString();
		sip_userid = in.readString();
	}

	public static final Parcelable.Creator<GtelAccount> CREATOR = new Parcelable.Creator<GtelAccount>() {
		public GtelAccount createFromParcel(Parcel in) {
			return new GtelAccount(in);
		}

		public GtelAccount[] newArray(int size) {
			return new GtelAccount[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(user_id);
		dest.writeString(sip_userid);

	}
}