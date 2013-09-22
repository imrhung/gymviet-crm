package com.seed.gymvietcrm.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.seed.gymvietcrm.common.UserInfor;

import com.seed.gymvietcrm.R;
import android.content.Context;
import android.util.Log;

public class ConnectServer {
	
	private static final int TIME_OUT_CONNECTION = 10000;
	private static final int TIME_OUT_SOCKET = 10000;
	
	private Context context;
	
	public ConnectServer(Context ctx){
		this.context = ctx;
	}
	
	
	public UserInfor getUserInfor(String userStringID){
		UserInfor user = new UserInfor();
		
		getJSONGet(context.getString(R.string.url_get_user_infor));
		user.name = "I got it";
		
		return user;
	}
	
	
	/**
	 * Method send a GET request to address url and get back a string
	 * 
	 * @param url
	 * @return the String result, null otherwise.
	 */
	private JSONObject getJSONGet(String url) {
		JSONObject jsonResult = null;
		Log.v("URL", url);
		// Create an intermediate to connect with the Internet
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				TIME_OUT_CONNECTION);
		HttpConnectionParams.setSoTimeout(httpParameters, TIME_OUT_SOCKET);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpGet httpGet = new HttpGet(url);
		try {

			HttpResponse httpResponse = httpClient.execute(httpGet);

			HttpEntity resultentity = httpResponse.getEntity();

			if (resultentity != null) {
				InputStream inputstream = resultentity.getContent();
				Header contentencoding = httpResponse
						.getFirstHeader("Content-Encoding");
				if (contentencoding != null
						&& contentencoding.getValue().equalsIgnoreCase("gzip")) {
					inputstream = new GZIPInputStream(inputstream);
				}
				String resultString = convertStreamToString(inputstream);
				Log.v("Get back", resultString);
				inputstream.close();
				try {
					jsonResult = new JSONObject(resultString);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (ClientProtocolException cpe) {
			System.out.println("Exception generates caz of httpResponse :"
					+ cpe);
			cpe.printStackTrace();
		} catch (IOException ioe) {
			System.out
					.println("Second exception generates caz of httpResponse :"
							+ ioe);
			ioe.printStackTrace();
		}
		if (jsonResult == null) {
			jsonResult = new JSONObject();
			try {
				// adding some keys
				jsonResult.put("Status", "Error");

			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return jsonResult;
	}
	
	/**
	 * Method to send request with jsonObj to url, then return a JSONObject of
	 * the response.
	 * 
	 * @param url
	 * @param jsonobj
	 * @return
	 */
	private JSONObject getJSONPost(String url, JSONObject jsonobj) {

		JSONObject jsonResult = null;
		// HTTP post
		//Log.v("Send J Obj", jsonobj.toString());
		//Log.v("URL", url);

		String resultString = "";
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					TIME_OUT_CONNECTION);
			HttpConnectionParams.setSoTimeout(httpParameters, TIME_OUT_SOCKET);
			DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
			HttpPost httppost = new HttpPost(url);
			// StringEntity se = new StringEntity(jsonobj.toString());
			// se.setContentType("application/json;charset=UTF-8");
			// se.setContentEncoding((Header) new BasicHeader(HTTP.CONTENT_TYPE,
			// "application/json;charset=UTF-8"));
			httppost.setEntity(new ByteArrayEntity(jsonobj.toString().getBytes(
					"UTF8")));
			httppost.setHeader("json", jsonobj.toString());
			HttpResponse httpresponse = httpclient.execute(httppost);
			HttpEntity resultentity = httpresponse.getEntity();

			if (resultentity != null) {
				InputStream inputstream = resultentity.getContent();
				Header contentencoding = httpresponse
						.getFirstHeader("Content-Encoding");
				if (contentencoding != null
						&& contentencoding.getValue().equalsIgnoreCase("gzip")) {
					inputstream = new GZIPInputStream(inputstream);
				}

				resultString = convertStreamToString(inputstream);
				inputstream.close();
				//Log.v("Get back", resultString);
				try {
					jsonResult = new JSONObject(resultString);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Log.v("Exception", "Timeout");
		}
		if (jsonResult == null) {
			jsonResult = new JSONObject();
			try {
				// adding some keys
				jsonResult.put("Status", "Error");

			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return jsonResult;
	}
	
	/**
	 * Private method convert stream from HTTP response to string
	 * 
	 * @param is
	 * @return Result string
	 */
	private String convertStreamToString(InputStream is) {
		String line = "";
		StringBuilder total = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		try {
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return total.toString();
	}
	
}
