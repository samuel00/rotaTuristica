package com.example.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TelefoneDao {

	public void deleteTelefone(SQLiteDatabase db, long id) {
		db.delete("telefone", "id = " + id, null);
		
	}

	public boolean getTelefoneById(SQLiteDatabase db, long id) {
		Cursor cursor = db.rawQuery("select * from telefone where id = " + id,null);
		return cursor.moveToFirst();
	}

}
