//package com.example.rotaturistica;
//
//import com.example.controller.AtracaoController;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//
//public class AtracaoNear extends Activity{
//	
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		
//		Intent getIntent = getIntent();
//		long idTipo = getIntent.getLongExtra("idTipo",0);
//		
//		AtracaoController controller = new AtracaoController();
//		listaDePontoLocalizavel = controller.getAtracoesNear(idTipo, location);
//		
//		
//	}
//}
//
//
