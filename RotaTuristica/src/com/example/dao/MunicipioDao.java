package com.example.dao;

import java.util.ArrayList;
import java.util.List;

import com.example.entidade.Municipio;
import com.example.entidade.PoloTuristico;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MunicipioDao {
	
	private Conexao conexao;

	public boolean getMunicipioById(SQLiteDatabase db, long id) {
		Cursor cursor = db.rawQuery("select * from municipio where id = " + id,null);
		return cursor.moveToFirst();
	}

	public List<Municipio> getMunicipioByPolo(ListActivity classe, long idPolo) {
		conexao = new Conexao(classe);
		SQLiteDatabase db = conexao.getReadableDatabase();

		Cursor cursor = db.rawQuery("select * from municipio",null);

		if(cursor.moveToFirst()){

			List<Municipio> listaDeMunicipio = new ArrayList<Municipio>();

			for (int i = 0; i < cursor.getCount(); i++) {

				Municipio municipio = new Municipio();


				municipio.setId(cursor.getLong(0));
				municipio.setNome(cursor.getString(1));

				listaDeMunicipio.add(municipio);
				cursor.moveToNext();
			}
			cursor.close();
			return listaDeMunicipio;
		}else{
			return null;
		}
	}
}
