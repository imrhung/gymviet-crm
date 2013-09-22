package com.seed.gymvietcrm;

import java.net.URL;

import com.seed.gymvietcrm.ScanBarcodeActivity.ScanHandler;
import com.seed.gymvietcrm.common.UserInfor;
import com.seed.gymvietcrm.connection.ConnectServer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

public class ScanBarcodeModel {

	public Context context;
	public ScanHandler handler;
	public String name;

	public ScanBarcodeModel(Context ctx, ScanHandler hdl){
		context = ctx;
		handler = hdl;
	}

	public String getUserName(){
		new GetUserInforTask().execute(null);
		return name;
	}



	private class GetUserInforTask extends AsyncTask<URL, Integer, Long> {
		protected Long doInBackground(URL... urls) {
			long totalSize = 0;
			UserInfor user;
			ConnectServer conser = new ConnectServer(context);
			user = conser.getUserInfor("1234");
			name = user.name;
			return totalSize;
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(Long result) {
			Message msg = handler.obtainMessage();
			msg.what = ScanBarcodeActivity.MSG_USER_INFOR;
			msg.obj = name;
			handler.sendMessage(msg);

			Log.v("end", String.valueOf(System.currentTimeMillis()));
		}
	}

}
