package com.kimi.android.callrecorder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;

public class CallInfoJSONSerializer {
	
	private static final String TAG = "CallInfoJSONSerializer";
	
	private Context mContext;
	private String mFilename;
	
	public CallInfoJSONSerializer(Context c, String f) {
		mContext  = c;
		mFilename = f;
	}
	
	public void saveCalls( ArrayList<CallInfo> calls ) throws JSONException,IOException {
		//Build an array in  JSON
		JSONArray array = new JSONArray();
		for( CallInfo c: calls ) {
			array.put(c.toJSON());
		}
		//Write the file to disk
		Writer writer = null;
		try {
			OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
		} finally {
			if( writer!=null )
				writer.close();
		}
		
	}
	
	public ArrayList<CallInfo> loadCalls() throws IOException, JSONException {
		ArrayList<CallInfo> calls = new ArrayList<CallInfo>();
		BufferedReader reader = null;
		try {
			InputStream in = mContext.openFileInput(mFilename);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			while( (line = reader.readLine()) != null ) {
				jsonString.append(line);
			}
			
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			Log.d(TAG, array.length() + "");
			for( int i = 0; i < array.length(); i++ ) {
				calls.add(new CallInfo(array.getJSONObject(i)));
			}
			
		} catch ( FileNotFoundException e ) {
			
		} finally {
			if( reader!= null ) {
				reader.close();
			}
		}
		
		return calls;
	}
	
}
