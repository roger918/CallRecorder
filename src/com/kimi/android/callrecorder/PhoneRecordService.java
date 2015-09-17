package com.kimi.android.callrecorder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneRecordService extends Service {

	private static final String TAG = "PhoneListener";
	private String outComingPhoneNumber;
	private String phoneNumber;
	private final static int NOTIFICATION_ID_ICON = 0x10000;


	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		TelephonyManager telManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		telManager.listen(new TelListener(),
				PhoneStateListener.LISTEN_CALL_STATE);
		Log.i(TAG, "service created");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		outComingPhoneNumber = intent.getStringExtra(PhoneReceiver.EXTRA_OUTCOMING_PHONE_NUMBER);
		Log.d(TAG, "拨出电话： " + outComingPhoneNumber);
		
		//return super.onStartCommand(intent, flags, startId);
		return START_NOT_STICKY;
	}

	
	private class TelListener extends PhoneStateListener {

		private MediaRecorder recorder;	
		private File audioFile;
		private boolean record;


		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			try {
		
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE: // 无任何状态时
					if (record) {
						recorder.stop(); // 停止刻录
						recorder.reset();
						recorder.release(); // 释放资源
						recorder = null;
						record = false;
						Log.d(TAG, "end record");
					}
					deleteIconToStatusbar();
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK: // 接起电话时
					addIconToStatusbar(R.drawable.icon_green);
					recorder = new MediaRecorder();
					recorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 从麦克风采集声音
					recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // 内容输出格式
					recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 音频编码方式

					String fisrtName;
					boolean isIncoming;

					if( !outComingPhoneNumber.isEmpty() ) {
						phoneNumber = outComingPhoneNumber;
						fisrtName = "呼出";
						isIncoming = false;
					} else {
						fisrtName = "呼入";
						isIncoming = true;
					}
						
					
					File recordPath = new File(Environment.getExternalStorageDirectory(),"/My record");
					if (!recordPath.exists()) {
						recordPath.mkdirs();
						Log.d("recorder", "创建目录");
					}
					
					String date = new SimpleDateFormat("yy-MM-dd_HH-mm-ss").format(new Date(System.currentTimeMillis()));
					String fileName = fisrtName + "_" + phoneNumber + "_" + date + ".mp3";// 实际是3gp
					File recordName = new File(recordPath, fileName);

					try {
						recordName.createNewFile();
						Log.d("recorder ", "创建文件 " + recordName.getName());
					} catch (IOException e) {
						e.printStackTrace();
					}

					recorder.setOutputFile(recordName.getAbsolutePath());
					recorder.prepare(); // 预期准备
					recorder.start(); // 开始刻录
					record = true;
					Log.d(TAG, "start record");
					
					// add a new record to list
					String contactName = getContactNameFromPhoneNum(getApplicationContext(), phoneNumber);
					if( contactName.equals("") )
						contactName = phoneNumber;
					date = new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
					CallInfo c = new CallInfo();
					c.setId(UUID.randomUUID());
					c.setName(contactName);
					c.setPhoneNumber(phoneNumber);
					c.setDate(date);
					c.setTime("60s");
					c.setRecordFileName(recordPath.getAbsolutePath() + "/" + fileName);
					c.setIsIncoming(isIncoming);	
					
					Log.d(TAG, "电话号码: " + phoneNumber);

					CallLab.get(getApplicationContext()).addCall(c);
								
					break;
				case TelephonyManager.CALL_STATE_RINGING: // 电话进来时

					phoneNumber = incomingNumber;
						
					Log.i(TAG, incomingNumber + " coming");
					addIconToStatusbar(R.drawable.icon_green);
					break;

				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
	
	
    /*
     * 如果没有从状态栏中删除ICON，且继续调用addIconToStatusbar,
     * 则不会有任何变化。除了：
     * 如果，将notification中的resId设置不同的图标，则会显示不同
     * 的图标
     */
    @SuppressWarnings("deprecation")
	private void addIconToStatusbar(int resId){
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification n = new Notification( resId, "通话录音", System.currentTimeMillis());
        
        Intent i = new Intent(this, CallRecorderActivity.class);
       
        n.flags |= Notification.FLAG_AUTO_CANCEL;

        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        n.contentIntent = pi;
        n.setLatestEventInfo(this, "CallRecroder is running", "", pi);
        nm.notify(NOTIFICATION_ID_ICON, n);
    }
    

    private void deleteIconToStatusbar(){
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID_ICON);
    }
    
    public String getContactNameFromPhoneNum(Context context, String phoneNum) {
		String contactName = "";
		ContentResolver cr = context.getContentResolver();
		Cursor pCur = cr.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
				new String[] { phoneNum }, null);
		if (pCur.moveToFirst()) {
			contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			// 找不到返回空
		}
		pCur.close();
		return contactName;
	}
}