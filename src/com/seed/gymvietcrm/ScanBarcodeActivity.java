package com.seed.gymvietcrm;

import com.seed.gymvietcrm.R;
import jim.h.common.android.lib.zxing.config.ZXingLibConfig;
import jim.h.common.android.lib.zxing.integrator.IntentIntegrator;
import jim.h.common.android.lib.zxing.integrator.IntentResult;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ScanBarcodeActivity extends Activity {

	public static final int MSG_USER_INFOR = 1;

	private static ScanHandler handler;
	private TextView txtScanResult;
	public static TextView tvUserName;
	private ZXingLibConfig zxingLibConfig;
	private ScanBarcodeModel mModel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        handler = new ScanHandler();
		
		setContentView(R.layout.activity_scan_barcode);

		txtScanResult = (TextView) findViewById(R.id.scan_result);
		tvUserName = (TextView) findViewById(R.id.tvUserName);
		zxingLibConfig = new ZXingLibConfig();
		zxingLibConfig.useFrontLight = true;

		mModel = new ScanBarcodeModel(this, handler);

		View btnScan = findViewById(R.id.scan_button);
		btnScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				IntentIntegrator.initiateScan(ScanBarcodeActivity.this,
						zxingLibConfig);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case IntentIntegrator.REQUEST_CODE:
			IntentResult scanResult = IntentIntegrator.parseActivityResult(
					requestCode, resultCode, data);
			if (scanResult == null) {
				return;
			}
			final String result = scanResult.getContents();

			Log.v("start", String.valueOf(System.currentTimeMillis()));
			if (result != null) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						txtScanResult.setText(result);
						mModel.getUserName();
					}
				});
			}
			
			break;
		default:
		}
	}

	static class ScanHandler extends Handler {

		public ScanHandler() {
		}

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == MSG_USER_INFOR) {
			    tvUserName.setText((String) msg.obj);
			}
		}
	}
}
