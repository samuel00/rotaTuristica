package com.example.utils;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.io.FilenameUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.example.rotaturistica.DownloadListItemActivity;
import com.example.rotaturistica.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class SalvaImagemNoSD{
	
	private String url;
	private String nomeImagem;
	public SalvaImagemNoSD(){
	}
	
	public String salvaImagem(String url, DownloadListItemActivity downloadListItemActivity){
		
		this.url = url;
		
		Log.w("","Nome Método salvaImagem");
		
		Picasso.with(downloadListItemActivity)
        .load(this.url)
        .error(R.drawable.exclamation);
		
		Picasso.with(downloadListItemActivity)
        .load(this.url)
        .into(target);
		
		return this.nomeImagem;
	}
	
	private String getNomeImagem() {
		String nome = FilenameUtils.getBaseName(this.url);
        String extensao = FilenameUtils.getExtension(this.url);
        nomeImagem = nome+"."+extensao;
        Log.w("","Nome da Imagem - " + nomeImagem);
		return nomeImagem;
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
                			String imagemNome = getNomeImagem();
                			File file = new File(Environment.getExternalStorageDirectory().getPath() +"/imagens_rt/"+imagemNome);
                        	file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(CompressFormat.PNG, 75, ostream);
                            ostream.close();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                	} else {
                		try
                        {
                			Log.w("","Entrou aqui o/");
                			String imagemNome = getNomeImagem();
                			File file = new File(Environment.getExternalStorageDirectory().getPath() +"/imagens_rt/"+imagemNome);
                        	file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(CompressFormat.PNG, 75, ostream);
                            ostream.close();
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
