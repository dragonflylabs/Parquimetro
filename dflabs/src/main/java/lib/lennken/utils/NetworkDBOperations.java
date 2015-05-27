package lib.lennken.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import lib.lennken.R;

@SuppressWarnings("deprecation")
public class NetworkDBOperations {

	static OnDBDownload static_callback_download;
	static OnDBUpload static_callback_upload;
	
	public static boolean download(final Context context, final String url, final String paramJson, final String path, final OnDBDownload callback) {
		static_callback_download = callback;
		new AsyncTask<Void, Void, Boolean>() {
			ProgressDialog dialog;
			protected void onPreExecute() {
				dialog = new ProgressDialog(context);
				dialog.setMessage(context.getString(R.string.__dialog_loading));
				dialog.setIndeterminate(true);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
			};
			
			@Override
			protected Boolean doInBackground(Void... params) {
				@SuppressWarnings("resource")
				HttpClient httpclient = new DefaultHttpClient();
			    HttpPost httppost = new HttpPost(url);
			    try {
					httppost.setEntity(new StringEntity(paramJson));
					httppost.setHeader("Content-type", "application/json");
			        HttpResponse response = httpclient.execute(httppost);
			        InputStream is = response.getEntity().getContent();
			        File file = new File (path);
			        file.mkdirs();
			        if (file.exists ()) file.delete ();
			        FileOutputStream output = new FileOutputStream(file); 
			        int bufferSize = 1024;
			        byte[] buffer = new byte[bufferSize];
			        int len = 0;
			        while ((len = is.read(buffer)) != -1) {
			            output.write(buffer, 0, len);
			        }
			        output.flush();
			        output.close();
			        is.close();
			        return true;
			    } catch (Exception e){
			    	e.printStackTrace();
			    	return false;
			    }
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				if(dialog != null && dialog.isShowing()){
					dialog.dismiss();
					if(result){
						callback.onSucessDBDownload();
					}else{
						callback.onErrorDBDownload();
					}
				}
			};
		}.execute();
		return true;
	}
	
	public static String loadUrl(String urlr,String method, HttpEntity entity) throws Exception {
		try {
	        URL url = new URL(urlr);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(10000);
	        conn.setConnectTimeout(300000);
	        conn.setRequestMethod("POST");
	        conn.setUseCaches(false);
	        conn.setDoInput(true);
	        conn.setDoOutput(true);
	        conn.setRequestProperty("Connection", "Keep-Alive");
	        conn.addRequestProperty("Content-length", entity.getContentLength()+"");
	        conn.addRequestProperty(entity.getContentType().getName(), entity.getContentType().getValue());
	        OutputStream os = conn.getOutputStream();
	        entity.writeTo(conn.getOutputStream());
	        os.close();
	        conn.connect();
	        System.out.println(conn.getResponseCode());
	        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
	        	BufferedReader reader = null;
			    StringBuilder builder = new StringBuilder();
		        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		        String line = "";
		        while ((line = reader.readLine()) != null) {
		            builder.append(line);
		        }
		        if (reader != null) {
		            try {
		                reader.close();
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		        }
			    return builder.toString();
	        }
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return null;
	}
}

