package com.example.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ItemDescricaoDao {

	public boolean getItemDescricaoById(SQLiteDatabase db, long id) {
		Cursor cursor = db.rawQuery("select * from item_descricao where id = " + id,null);
		return cursor.moveToFirst();
	}

	public void deleteItemDescricao(SQLiteDatabase db, long id) {
		db.delete("item_descricao", "id = " + id, null);
	}
	
	
	
}
