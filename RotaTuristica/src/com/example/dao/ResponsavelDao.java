package com.example.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ResponsavelDao {

	public void deleteResponsavel(SQLiteDatabase db, long id) {
		db.delete("info_do_responsavel", "id = " + id, null);
		
	}

	public boolean getResponsavelById(SQLiteDatabase db, long id) {
		Cursor cursor = db.rawQuery("select * from info_do_responsavel where id = " + id,null);
		return cursor.moveToFirst();
	}

}
