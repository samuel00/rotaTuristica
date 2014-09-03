package com.example.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EnderecoDao {

	public void deleteEndereco(SQLiteDatabase db, long id) {
		db.delete("endereco", "id = " + id, null);
		
	}

	public boolean getEnderecoById(SQLiteDatabase db, long id) {
		Cursor cursor = db.rawQuery("select * from endereco where id = " + id,null);
		return cursor.moveToFirst();
	}

}
