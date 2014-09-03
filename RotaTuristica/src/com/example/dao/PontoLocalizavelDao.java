package com.example.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;

public class PontoLocalizavelDao {
	private Conexao conexao;

	public String getNomePontoLocalizavel(FragmentActivity classe, Long id) {
		conexao = new Conexao(classe);
		SQLiteDatabase db = conexao.getReadableDatabase();

		Cursor cursor = db.rawQuery("select nome from item_descricao where id = " + id,null);
		
		String nome = null;
		
		if(cursor.moveToFirst()){
			nome = cursor.getString(0);
		}
		
		return nome;
	}

	public void deletePontoLocalizavel(SQLiteDatabase db, long id) {
		db.delete("ponto_localizavel", "id = " + id, null);
		
	}

	public boolean getPontoLocalizavelById(SQLiteDatabase db, long id) {
		Cursor cursor = db.rawQuery("select * from ponto_localizavel where id = " + id,null);
		return cursor.moveToFirst();
	}
}
