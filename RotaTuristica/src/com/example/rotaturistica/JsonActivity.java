package com.example.rotaturistica;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.utils.DialogProgress;
import com.example.utils.GalleryImageAdapter;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Build;

public class JsonActivity extends ActionBarActivity {
	
	
	ImageView selectedImage; 
    private Integer[] mImageIds = {
               R.drawable.igreja_da_se_1,
               R.drawable.igreja_da_se_2,
               R.drawable.igreja_das_merces};
    
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private DialogProgress dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_json);

//		StrictMode.ThreadPolicy policy = new StrictMode.
//				ThreadPolicy.Builder().permitAll().build();
//		StrictMode.setThreadPolicy(policy); 
		
		
		
//		Gallery gallery = (Gallery) findViewById(R.id.gallery1);
//        selectedImage=(ImageView)findViewById(R.id.imageView1);
//        gallery.setSpacing(1);
//        gallery.setAdapter(new GalleryImageAdapter(this));
//
//        gallery.setOnItemClickListener(new OnItemClickListener(){
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
//				Toast.makeText(JsonActivity.this, "Your selected position = " + position, Toast.LENGTH_SHORT).show();
//                // show the selected Image
//                selectedImage.setImageResource(mImageIds[position]);
//				
//			}
//        	
//        });

	}
	
	public void startDownload(View view){
			new JsonTask().execute();
	}
	
	private class JsonTask extends AsyncTask<String,Integer, String>{

		 @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            dialog = new DialogProgress("Aguarde","Baixando Servicos Turísticos de ");
				dialog.iniciaProgressDialog(JsonActivity.this);
	        }

	        @Override
	        protected String doInBackground(String... aurl) {
	            int count;

	            try {
	                URL url = new URL("http://farm1.static.flickr.com/114/298125983_0e4bf66782_b.jpg");
	                URLConnection conexion = url.openConnection();
	                conexion.connect();

	                int lenghtOfFile = conexion.getContentLength();
	                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

	                InputStream input = new BufferedInputStream(url.openStream());
	                OutputStream output = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +"/some_photo_from_gdansk_poland.jpg");

	                byte data[] = new byte[1024];

	                long total = 0;

	                while ((count = input.read(data)) != -1) {
	                    total += count;
	                    publishProgress((int)((total*100)/lenghtOfFile));
	                    output.write(data, 0, count);
	                }

	                output.flush();
	                output.close();
	                input.close();
	            } catch (Exception e) {}
	            return null;

	        }
	        protected void onProgressUpdate(Integer... progress) {
	             dialog.setProgress(progress[0]);
	        }

	        @Override
	        protected void onPostExecute(String unused) {
	            dialog.encerraProgress();
	        }
	}

}
