package com.miqtech.master.client.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactMember implements Parcelable, Comparable<ContactMember> {
	public String contact_phone;
	public String contact_name;
	public String sortKey;
	public int contact_id;
	public int type;
	public static final int TYPE_CHECKED = 1;
	public static final int TYPE_NOCHECKED = 0;
	public int isChecked = 0;
	public int isAddPhone = 0;

	public String getContact_phone() {
		return contact_phone;
	}

	public void setContact_phone(String contact_phone) {
		this.contact_phone = contact_phone;
	}

	public String getContact_name() {
		return contact_name;
	}

	public void setContact_name(String contact_name) {
		this.contact_name = contact_name;
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	public int getContact_id() {
		return contact_id;
	}

	public void setContact_id(int contact_id) {
		this.contact_id = contact_id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getIsChecked() {
		return isChecked;
	}

	public void setIsChecked(int isChecked) {
		this.isChecked = isChecked;
	}

	public int getIsAddPhone() {
		return isAddPhone;
	}

	public void setIsAddPhone(int isAddPhone) {
		this.isAddPhone = isAddPhone;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeString(contact_phone);
		arg0.writeString(contact_name);
		arg0.writeString(sortKey);
		arg0.writeInt(contact_id);
		arg0.writeInt(type);
		arg0.writeInt(isChecked);
		arg0.writeInt(isAddPhone);
	}

	public static final Creator<ContactMember> CREATOR = new Creator<ContactMember>() {
		public ContactMember createFromParcel(Parcel source) {
			ContactMember member = new ContactMember();
			member.contact_phone = source.readString();
			member.contact_name = source.readString();
			member.sortKey = source.readString();
			member.contact_id = source.readInt();
			member.type = source.readInt();
			member.isChecked = source.readInt();
			member.isAddPhone = source.readInt();
			return member;
		}

		public ContactMember[] newArray(int size) {
			return new ContactMember[size];
		}
	};

	@Override
	public int compareTo(ContactMember another) {
		// TODO Auto-generated method stub
		return this.contact_name.compareToIgnoreCase(another.getContact_name());
	}
}
