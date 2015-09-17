package com.kimi.android.callrecorder;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

public class CallLab {
	
	private static final String TAG = "CallLab";
	private static final String FILENAME = "calls.json";
	
	private ArrayList<CallInfo> mCalls;
	private CallInfoJSONSerializer mSerializer;
	
	private static CallLab sCallLab;
	private Context mAppContext;
	
	private CallLab( Context appContext ) {
		mAppContext = appContext;
	//	mCalls = new ArrayList<CallInfo>();
		mSerializer = new CallInfoJSONSerializer(mAppContext, FILENAME);
		
		try {
			mCalls = mSerializer.loadCalls();
		} catch ( Exception e ) {
			mCalls = new ArrayList<CallInfo>();
			Log.e(TAG, "Error loading Calls: ", e);
		}
	}
	
	public static CallLab get( Context c ) {
		if( sCallLab == null ) {
			sCallLab = new CallLab(c.getApplicationContext());
		}
		
		return sCallLab;
	}
	
	public void addCall( CallInfo c ) {
		mCalls.add(0,c);
	}
	
	public boolean saveCalls() {
		try {
			mSerializer.saveCalls(mCalls);
			Log.d(TAG, mCalls.size() + " calls saved to file");
			return true;
		} catch (Exception e) {
			Log.e(TAG, "Error saving calls");
			return false;
		}
	}
	
	public void deleteCall( CallInfo c ) {
		mCalls.remove(c);
	}
	
	public ArrayList<CallInfo> getCalls() {
		return mCalls;
	}

	public CallInfo getCall( UUID id ) {
		for( CallInfo c: mCalls) {
			if( c.getId().equals(id) ) {
				return c;
			}
		}		
		return null;
	}	
	
}
