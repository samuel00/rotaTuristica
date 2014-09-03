package com.example.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.entidade.Multimidia;

public class MultimidiaDao {
	
	private Conexao conexao;

	public List<Multimidia> getMultimidiaByItemDescricao(FragmentActivity classe,long id) {
		conexao = new Conexao(classe);
		SQLiteDatabase db = conexao.getReadableDatabase();
		
		List<Multimidia> listaDeMultimidia = new ArrayList<Multimidia>();

		Cursor cursor = db.rawQuery("select * from multimidia where item_descricao_id = " + id ,null);
		
		cursor.moveToFirst();
		
			for (int i = 0; i < cursor.getCount(); i++) {

				Multimidia multimidia = new Multimidia();
				
				multimidia.setId(Integer.parseInt(cursor.getString(0)));
				multimidia.setUrl(cursor.getString(1));
				
				listaDeMultimidia.add(multimidia);
				
				cursor.moveToNext();
			}
		
		return listaDeMultimidia;

	}
	
}
