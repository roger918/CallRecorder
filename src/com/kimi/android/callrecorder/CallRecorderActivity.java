package com.kimi.android.callrecorder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.widget.Toast;



public class CallRecorderActivity extends SingleFragmentActivity {

	private long exitTime = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
				
		super.onCreate(savedInstanceState);	
	}

	@Override
	protected Fragment createFragment() {
		
		return new CallRecorderFragment();
	}

	@Override
	public void onBackPressed() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {  
            Toast.makeText(CallRecorderActivity.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();  
            exitTime = System.currentTimeMillis();  
            return;  
        }  
        finish();  
	}	
}

