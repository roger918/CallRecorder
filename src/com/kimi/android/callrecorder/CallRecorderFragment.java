package com.kimi.android.callrecorder;

import java.io.File;
import java.util.ArrayList;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class CallRecorderFragment extends ListFragment {

	private static final String TAG = "CallRecorderFragment";
	private ArrayList<CallInfo> mCalls;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);		
		
		getActivity().setTitle(R.string.calls_title);
		
		mCalls = CallLab.get(getActivity()).getCalls();

		Log.d(TAG, mCalls.size() + "");

		// ArrayAdapter<CallInfo> adapter = new
		// ArrayAdapter<CallInfo>(getActivity(),
		// android.R.layout.simple_list_item_1, mCalls);
		CallAdapter adapter = new CallAdapter(mCalls);
		setListAdapter(adapter);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = (View) super.onCreateView(inflater, container,
				savedInstanceState);
			
		ListView listView = (ListView) v.findViewById(android.R.id.list); 		
		
		TextView emptyView = new TextView(getActivity());
		emptyView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		emptyView.setText("当前没有通话记录");
		emptyView.setTextSize(20);
		emptyView.setGravity(Gravity.CENTER);
		emptyView.setVisibility(View.GONE);
		((ViewGroup)listView.getParent()).addView(emptyView);
		listView.setEmptyView(emptyView);
		
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			// 创建ActionMode对象后调用
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.call_list_item_context, menu);
				return true;
			}

			// onCreateActionMode之后 当前上下文操作栏需要刷新显示数据时调用
			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

				return false;
			}

			// 退出时调用 ActionMode对象被销毁
			@Override
			public void onDestroyActionMode(ActionMode mode) {

			}

			// 用户选中某个菜单栏操作时调用
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
				case R.id.menu_item_delete_call:
					CallAdapter adapter = (CallAdapter) getListAdapter();
					CallLab callLab = CallLab.get(getActivity());

					for (int i = adapter.getCount() - 1; i >= 0; i--) {
						if (getListView().isItemChecked(i)) {
							
							//File file = new File(Environment.getExternalStorageDirectory() , "/My Record/" + adapter.getItem(i).getRecordFileName());
							File file = new File(adapter.getItem(i).getRecordFileName());
							callLab.deleteCall(adapter.getItem(i));
							
							Log.d(TAG, file.getAbsolutePath());
							if( file.exists() && file.isFile() ) {
								file.delete();
								Log.d(TAG,"delete file");
							}							
						}						
					}

					mode.finish();
					adapter.notifyDataSetChanged();
					return true;

				case R.id.menu_item_select_all:
					CallAdapter adapter2 = (CallAdapter) getListAdapter();
					for (int i = 0; i < adapter2.getCount(); i++)
						getListView().setItemChecked(i, true);
					return true;

				default:
					return false;
				}

			}

			// 发生变化时调用
			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
			}
		});

		return v;
	}

	@Override
	public void onPause() {
		super.onPause();
		CallLab.get(getActivity()).saveCalls();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		CallInfo c = ((CallAdapter) getListAdapter()).getItem(position);
		Log.d(TAG, c.getName() + " was clicked");
		
		
		//play the audio		
		Intent intent = new Intent(Intent.ACTION_VIEW);  
		File sdcard = Environment.getExternalStorageDirectory(); //这个是获取SDCard路径  
		File audioFile = new File(c.getRecordFileName());
		
		if( audioFile.exists() && audioFile.isFile() ) {
			//然后需要获取该文件的Uri  
			Uri audioUri = Uri.fromFile(audioFile); 
			Log.d(TAG, audioUri.toString());
			//然后指定Uri和MIME  
			intent.setDataAndType(audioUri, "audio/mp3");  
			startActivity(intent); 
		}
	}

	private class CallAdapter extends ArrayAdapter<CallInfo> {
		public CallAdapter(ArrayList<CallInfo> calls) {
			super(getActivity(), 0, calls);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.list_item_call, null);
			}				
						
			CallInfo c = getItem(position);

			ImageView imageView = (ImageView) convertView
					.findViewById(R.id.call_list_item_image);
			
			if( c.getIsIncoming() ) {
				imageView.setImageDrawable(getResources().getDrawable(R.drawable.orange));
			} 		

			TextView nameTextView = (TextView) convertView
					.findViewById(R.id.call_list_item_name);
			nameTextView.setText(c.getName());

			TextView phoneNumberTextView = (TextView) convertView
					.findViewById(R.id.call_list_item_phone_number);
			phoneNumberTextView.setText(c.getPhoneNumber());

			TextView dateTextView = (TextView) convertView
					.findViewById(R.id.call_list_item_date);
			dateTextView.setText(c.getDate().toString());
			
			return convertView;
		}

	}

	// 从XML生成菜单的布局
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.call_recorder, menu);		
	}

	// 相应菜单项
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.exit:
			getActivity().finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

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
