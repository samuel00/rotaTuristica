package com.example.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.entidade.Multimidia;
import com.example.utils.EnderecoImagem;

public class DownloadImagem {
	
	private List<Multimidia> listaMultimidia;
	
	public DownloadImagem(List<Multimidia> listaMultimidia) {
		this.listaMultimidia = listaMultimidia;
	}

	public void downloadImagem() {
		new DownloadImagemTask().execute();
	}
	private class DownloadImagemTask extends AsyncTask<String,Void, String>{

		@Override
		protected String doInBackground(String... params) {
			int count;
			
			EnderecoImagem enderecoImagem = new EnderecoImagem();
			
			File folder = new File(enderecoImagem.getEndereco());
        	boolean successo = true;
        	if (!folder.exists()) {
        	    successo = folder.mkdir();
        	}
			
			for(int i =0; i < listaMultimidia.size(); i ++){
				try {
	                URL url = new URL(listaMultimidia.get(i).getUrl());
	                URLConnection conexion = url.openConnection();
	                conexion.connect();
	                
	                InputStream input = new BufferedInputStream(url.openStream());
                	String imagemNome = getNomeImagem(listaMultimidia.get(i).getUrl());
	                OutputStream output = new FileOutputStream(enderecoImagem.getEndereco()+imagemNome);
	                
	                byte data[] = new byte[1024];

	                long total = 0;

	                while ((count = input.read(data)) != -1) {
	                    output.write(data, 0, count);
	                }

	                output.flush();
	                output.close();
	                input.close();
	            } catch (Exception e) {}
			}
			return null;
		}

		private String getNomeImagem(String url) {
			String nome = FilenameUtils.getBaseName(url);
	        String extensao = FilenameUtils.getExtension(url);
	        String imagemNome = nome+"."+extensao;
	        boolean sucesso = false;
	        int contador = 1;
	        while(!sucesso){
	        	File imagem = new File(Environment.getExternalStorageDirectory() + "/Rotaturistica/"+imagemNome);
	        	if(imagem.isFile()){
	        		imagemNome = nome+"_"+contador+"."+extensao;
	        	}else{
	        		sucesso = true;
	        	}
	        	contador ++;
	        }
        	
	        return imagemNome;
		}
		
		
	}


}
