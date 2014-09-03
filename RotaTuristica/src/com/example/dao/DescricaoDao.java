package com.example.dao;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DescricaoDao {
	
	private Conexao conexao;

	public String getDescricaoByItemDescricao(ListActivity classe,Long id) {
		conexao = new Conexao(classe);
		SQLiteDatabase db = conexao.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("select descricao from descricao where item_descricao_id = " + id ,null);
		
		String descricao = null;
		
		if(cursor.moveToFirst()){
			descricao = cursor.getString(0);
		}
		
		return descricao;
	}

	public void deleteDescricao(SQLiteDatabase db, long id) {
		db.delete("descricao", "id = " + id, null);
	}

	public boolean getDescricaoById(SQLiteDatabase db, long id) {
		Cursor cursor = db.rawQuery("select * from descricao where id = " + id,null);
		return cursor.moveToFirst();
	}
	
	
}
