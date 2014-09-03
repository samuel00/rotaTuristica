package com.example.utils;

import android.os.Environment;

public class EnderecoImagem {
	
	private String endereco;
	
	public EnderecoImagem(){
		this.endereco = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Rotaturistica/";
	}
	
	public String getEndereco(){
		return this.endereco;
	}
}
