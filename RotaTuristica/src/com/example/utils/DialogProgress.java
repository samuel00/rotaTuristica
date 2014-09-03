package com.example.utils;

import android.support.v4.app.FragmentActivity;
import android.app.ListActivity;
import android.app.ProgressDialog;

public class DialogProgress {
	
	private String texto;
	private String aviso;
	private ProgressDialog dialog;
	
	
	public DialogProgress(String texto, String aviso){
		this.texto = texto;
		this.aviso = aviso;
	}
	
	public void iniciaProgressDialog(FragmentActivity classe) {
		dialog = new ProgressDialog(classe);
		dialog.setTitle(this.texto);
        dialog.setMessage(this.aviso);
        dialog.setIndeterminate(false);
        dialog.setMax(100);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.show();
	}
	
	public void iniciaProgressDialog(ListActivity classe) {
		dialog = new ProgressDialog(classe);
		dialog.setTitle(this.texto);
        dialog.setMessage(this.aviso);
        dialog.setIndeterminate(false);
        dialog.setMax(100);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.show();
	}

	public void setProgress(Integer progress) {
		dialog.setProgress(progress);
	}

	public void encerraProgress() {
		dialog.dismiss();
		
	}
}
