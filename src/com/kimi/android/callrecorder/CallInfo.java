package com.kimi.android.callrecorder;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class CallInfo {
	
	private static final String JSON_ID = "id";
	private static final String JSON_NAME = "name";
	private static final String JSON_PHONENUMBER = "phonenumber";
	private static final String JSON_TIME = "time";
	private static final String JSON_DATE = "date";
	private static final String JSON_RECORDNAME = "recordname";
	private static final String JSON_IS_INCOMING = "isincoming";
	
	private UUID mId;
	private String mName;
	private String mPhoneNumber;
	private String mTime;
	private String mDate;
	private String mRecordFileName;	
	private boolean mIsIncoming;
	

	public CallInfo() {
		mId = UUID.randomUUID();
	}
	
	public CallInfo( JSONObject json ) throws JSONException {
		mId = UUID.fromString(json.getString(JSON_ID));
		mName = json.getString(JSON_NAME);
		mPhoneNumber = json.getString(JSON_PHONENUMBER);
		mTime = json.getString(JSON_TIME);
		mDate = json.getString(JSON_DATE);
		mRecordFileName = json.getString(JSON_RECORDNAME);
		mIsIncoming = json.getBoolean(JSON_IS_INCOMING);
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_ID, mId);
		json.put(JSON_NAME, mName);
		json.put(JSON_PHONENUMBER, mPhoneNumber);
		json.put(JSON_TIME, mTime);
		json.put(JSON_DATE, mDate);
		json.put(JSON_RECORDNAME, mRecordFileName);
		json.put(JSON_IS_INCOMING, mIsIncoming);
		return json;
	}
	
	public UUID getId() {
		return mId;
	}

	public void setId(UUID mId) {
		this.mId = mId;
	}

	public boolean getIsIncoming() {
		return mIsIncoming;
	}

	public void setIsIncoming(boolean mIsIncoming) {
		this.mIsIncoming = mIsIncoming;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public String getPhoneNumber() {
		return mPhoneNumber;
	}

	public void setPhoneNumber(String mPhoneNumber) {
		this.mPhoneNumber = mPhoneNumber;
	}

	public String getTime() {
		return mTime;
	}

	public void setTime(String mTime) {
		this.mTime = mTime;
	}

	public String getDate() {
		return mDate;
	}

	public void setDate(String mDate) {
		this.mDate = mDate;
	}

	@Override
	public String toString() {
		return mName;
	}
	
	public String getRecordFileName() {
		return mRecordFileName;
	}

	public void setRecordFileName(String mRecordFileName) {
		this.mRecordFileName = mRecordFileName;
	}
	
	
	
}
