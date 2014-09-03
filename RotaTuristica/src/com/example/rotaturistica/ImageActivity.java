package com.example.rotaturistica;

import java.io.File;
import java.io.FileOutputStream;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ImageActivity extends Activity{
	
	ImageView imageView;
    ProgressBar progressBar;
 
    String currentUrl = "http://assets.naointendo.com.br/assets/logo-fcf1dbe1263a76926593b3e079b67b63.png";
 
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_detail);
		imageView = (ImageView) findViewById(R.id.image);
		 
        progressBar = (ProgressBar) findViewById(R.id.loading);
 
//        Picasso.with(this)
//        .load(currentUrl)
//        .error(R.drawable.exclamation)
//        .into(imageView, new Callback() {
//        	
//            public void onSuccess() {
//                progressBar.setVisibility(View.GONE);
//            }            
//            
//            public void onError() {
//                progressBar.setVisibility(View.GONE);
//            }            
//        });
 
        Picasso.with(this)
        .load(currentUrl)
        .into(target);
	}
    
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                	
                	File folder = new File(Environment.getExternalStorageDirectory() + "/imagens_rt");
                	boolean success = true;
                	if (!folder.exists()) {
                	    success = folder.mkdir();
                	}
                	if (success) {
                		try
                        {
                			String imagemNome = "actress_wallpaper.png";
                			File file = new File(Environment.getExternalStorageDirectory().getPath() +"/imagens_rt/"+imagemNome);
                        	file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(CompressFormat.PNG, 75, ostream);
                            ostream.close();
                            Toast.makeText(getApplicationContext(), "Concluido", Toast.LENGTH_LONG).show();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                	} else {
                		try
                        {
                			String imagemNome = "actress_wallpaper.png";
                			File file = new File(Environment.getExternalStorageDirectory().getPath() +"/imagens_rt/"+imagemNome);
                        	 file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(CompressFormat.PNG, 75, ostream);
                            ostream.close();
                            Toast.makeText(getApplicationContext(), "Concluido", Toast.LENGTH_LONG).show();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                	}
 
                   
                    
 
                }
            }).start();
        }

		@Override
		public void onBitmapFailed(Drawable arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPrepareLoad(Drawable arg0) {
			if (arg0 != null) {
            }
			
		}
       
    };

}
