package com.example.dao;

import java.util.ArrayList;
import java.util.List;

import com.example.entidade.PoloTuristico;
import com.example.entidade.TipoServico;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PoloDao {
	
	private Conexao conexao;

	public boolean getPoloById(SQLiteDatabase db, long id) {
		Cursor cursor = db.rawQuery("select * from  polo_turistico where id = " + id,null);
		return cursor.moveToFirst();
	}

	public List<PoloTuristico> getPoloTuristico(ListActivity classe) {
		conexao = new Conexao(classe);
		SQLiteDatabase db = conexao.getReadableDatabase();

		Cursor cursor = db.rawQuery("select * from polo_turistico",null);

		if(cursor.moveToFirst()){

			List<PoloTuristico> listaDePolo = new ArrayList<PoloTuristico>();

			for (int i = 0; i < cursor.getCount(); i++) {

				PoloTuristico polo= new PoloTuristico();


				polo.setId(cursor.getLong(0));
				polo.setNome(cursor.getString(1));

				listaDePolo.add(polo);
				cursor.moveToNext();
			}
			cursor.close();
			return listaDePolo;
		}else{
			return null;
		}
	}


}
