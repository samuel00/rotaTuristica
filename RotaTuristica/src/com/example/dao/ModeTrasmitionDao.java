package com.example.dao;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;

import com.example.rotaturistica.AtracaoListActivity;
import com.example.rotaturistica.ModeTrasmitionActivity;

public class ModeTrasmitionDao {
	
	private Conexao conexao;

	public int alteraModoDeTrasmissaoOff(	ModeTrasmitionActivity modeTrasmitionActivity) {
		conexao = new Conexao(modeTrasmitionActivity);
		SQLiteDatabase db = conexao.getReadableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("online",0);
		
		if(db.update("modo_transmissao", values, "_id ='" + 1 + "'", null) == 1){
			db.close();
			conexao.close();
			return 1;
		}else{
			db.close();
			conexao.close();
			return 0;
		}
		
	}
	
	public int alteraModoDeTrasmissaoOn(	ModeTrasmitionActivity modeTrasmitionActivity) {
		conexao = new Conexao(modeTrasmitionActivity);
		SQLiteDatabase db = conexao.getReadableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("online",1);
		
		if(db.update("modo_transmissao", values, "_id ='" + 1 + "'", null) == 1){
			db.close();
			conexao.close();
			return 1;
		}else{
			db.close();
			conexao.close();
			return 0;
		}
		
		
	}

	public int getModoTransmissao(Context classe) {
		conexao = new Conexao(classe);
		SQLiteDatabase db = conexao.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT online from modo_transmissao where _id = 1",null);
		
		int online = 0;
		
		if(cursor.moveToFirst()){
			online = cursor.getInt(0);
		}
		db.close();
		conexao.close();
		return online;
	}

	public int getModoTransmissao(FragmentActivity classe) {
		conexao = new Conexao(classe);
		SQLiteDatabase db = conexao.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT online from modo_transmissao where _id = 1",null);
		
		int online = 0;
		
		if(cursor.moveToFirst()){
			online = cursor.getInt(0);
		}
		return online;
	}
}
