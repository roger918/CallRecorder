package com.kimi.android.callrecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PhoneReceiver extends BroadcastReceiver {

	private static final String TAG = "PhoneReceiver";
	public static final String  EXTRA_OUTCOMING_PHONE_NUMBER = "com.kimi.android.phonereceiver.outcoming_phone_number";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "boot completed received");
		Intent service = new Intent(context, PhoneRecordService.class);

		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			service.putExtra(EXTRA_OUTCOMING_PHONE_NUMBER, phoneNumber);			
		} else {
			service.putExtra(EXTRA_OUTCOMING_PHONE_NUMBER, "");
		}

		context.startService(service);
	}
}
