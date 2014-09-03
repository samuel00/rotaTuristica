package com.example.utils;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;

import com.example.rotaturistica.DownloadAtracaoActivity;

public class Dialog {
	
	private String texto;
	private String aviso;
	private ProgressDialog dialog;
	
	public Dialog(String texto, String aviso){
		this.texto = texto;
		this.aviso = aviso;
	}
	
	public void iniciaDialog(ListActivity classe) {
		dialog = new ProgressDialog(classe);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setTitle(this.texto);
		dialog.setMessage(this.aviso);
		dialog.setCancelable(false);
		dialog.setMax(800);
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	public void encerraDialog(){
		dialog.dismiss();
	}

	public void iniciaDialog(FragmentActivity classe) {
		dialog = new ProgressDialog(classe);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setTitle(this.texto);
		dialog.setMessage(this.aviso);
		dialog.setCancelable(false);
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.show();
		
	}
	
	public void progressDialog(FragmentActivity classe) {
		ProgressDialog dialog = new ProgressDialog(classe);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setTitle("Aguarde");
		dialog.setMessage("Processo demorado");
		dialog.setCancelable(true);
		dialog.setMax(100);
		
		dialog.show();
	}

}
