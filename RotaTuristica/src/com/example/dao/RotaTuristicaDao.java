package com.example.dao;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.entidade.AtracaoTuristica;
import com.example.entidade.Descricao;
import com.example.entidade.Multimidia;
import com.example.entidade.OrdemPontoRota;
import com.example.entidade.PontoLocalizavel;
import com.example.entidade.RotaTuristica;
import com.example.entidade.TipoRota;
import com.example.entidade.TipoRota;
import com.example.rotaturistica.DownloadListItemActivity;
import com.example.utils.EnderecoImagem;

public class RotaTuristicaDao {
	
	private Conexao conexao;

	public long dataUltimaAtualizacao(long idTipoItem,	DownloadListItemActivity downloadListItemActivity) {
		conexao = new Conexao(downloadListItemActivity);
		SQLiteDatabase db = conexao.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT max(itd.data_atualizacao), itd.id from item_descricao itd inner join rota_turistica rt on rt.id = itd.id"
				+ " inner join tipo_rota tr on tr.id = rt.tipo_rota where rt.tipo_rota = " + idTipoItem,null);
		long data = 0;
		if(cursor.moveToFirst()) {
			data = cursor.getLong(0);
		}
		return data;
	}

	public void salvaRota(DownloadListItemActivity downloadListItemActivity,String[] itemDescricao, String[] multimidia, String[] pontoRota) {
		conexao = new Conexao(downloadListItemActivity);
		SQLiteDatabase db = conexao.getWritableDatabase();
		try{
			salvarItemDescricao(db,itemDescricao);
			salvaTipoRota(db, itemDescricao);
			salvaRota(db,itemDescricao);
			salvaPontoRota(db,pontoRota);
			salvarDescricao(db,itemDescricao);
//			salvaMultimidia(db,multimidia);
		}catch(Exception e){

		}
	}

	private void salvarDescricao(SQLiteDatabase db, String[] itemDescricao) {
		for(int i = 0; i < itemDescricao.length; i++){
			ContentValues values = new ContentValues();
			String[] parts = itemDescricao[i].split("---");
			long id = Long.parseLong(parts[5]);
			if(getDescricaoById(db,id))
				deleteDescricao(db,id);
			String nome = parts[0];
			long itemDescricaoId = Long.parseLong(parts[1]);
			String descricao = parts[6];
			long idiomaId = Long.parseLong(parts[7]);
			values.put("id", id);
			values.put("descricao", descricao);
			values.put("idiomaid", idiomaId);
			values.put("item_descricao_id", itemDescricaoId);
			
			db.insert("descricao", null, values);
		}
		
	}
	
	private void deleteDescricao(SQLiteDatabase db, long id) {
		DescricaoDao dao = new DescricaoDao();
		dao.deleteDescricao(db,id);
		
	}


	private boolean getDescricaoById(SQLiteDatabase db, long id) {
		DescricaoDao dao = new DescricaoDao();
		return dao.getDescricaoById(db,id);
	}


	private void salvaTipoRota(SQLiteDatabase db, String[] itemDescricao) {
		String[] parts = itemDescricao[0].split("---");
		String rota = (parts[8]);
		long tipoRota = Long.parseLong(parts[3]);
		if(!getTipoRotaById(db,tipoRota)){
			ContentValues values = new ContentValues();
			values.put("nome", rota);
			values.put("id", tipoRota);
			
			db.insert("tipo_rota", null, values);
		}
		
		
	}

	private boolean getTipoRotaById(SQLiteDatabase db, long tipoRota) {
		Cursor cursor = db.rawQuery("select * from tipo_rota where id = " + tipoRota,null);
		return cursor.moveToFirst();
	}

	private void salvaMultimidia(SQLiteDatabase db, String[] multimidia) {
		for(int i = 0; i < multimidia.length; i++){
			ContentValues values = new ContentValues();
			String[] parts = multimidia[i].split("---");
			String url  = parts[2];
			long id = Long.parseLong(parts[1]);
			long itemDescricao = Long.parseLong(parts[0]);
			values.put("item_descricao_id", itemDescricao);
			values.put("id", id);
			values.put("url", url);
			
			db.insert("multimidia", null, values);
		}
		
	}

	private void salvaRota(SQLiteDatabase db, String[] itemDescricao) {
		for(int i = 0; i < itemDescricao.length; i++){
			ContentValues values = new ContentValues();
			String[] parts = itemDescricao[i].split("---");
			long id = Long.parseLong(parts[1]);
			if(getRotaById(db,id))
				deleteRota(db,id);
			long tipoRota = Long.parseLong(parts[3]);
			
			values.put("tipo_rota", tipoRota);
			values.put("id", id);
			
			db.insert("rota_turistica", null, values);
		}
		
	}
	
	private void deleteRota(SQLiteDatabase db, long id) {
		db.delete("rota_turistica", "id = " + id, null);
	}


	private boolean getRotaById(SQLiteDatabase db, long id) {
		Cursor cursor = db.rawQuery("select * from rota_turistica where id = " + id,null);
		return cursor.moveToFirst();
	}
	
	private void salvaPontoRota(SQLiteDatabase db, String[] pontoRota) {
		for(int i = 0; i < pontoRota.length; i++){
			ContentValues values = new ContentValues();
			String[] parts = pontoRota[i].split("---");
			long id =  Long.parseLong(parts[0]);
			long posicao = Long.parseLong(parts[1]);
			long rotaTuristicaId = Long.parseLong(parts[2]);
			long pontoLocalizavelId = Long.parseLong(parts[3]);
			
			values.put("id", id);
			values.put("posicao", posicao);
			values.put("rota_turistica_id", rotaTuristicaId);
			values.put("ponto_localizavel_id", pontoLocalizavelId);
			
			db.insert("ordem_ponto_rota", null, values);
		}
		
	}

	private void salvarItemDescricao(SQLiteDatabase db, String[] itemDescricao) {
		for(int i = 0; i < itemDescricao.length; i++){
			ContentValues values = new ContentValues();
			String[] parts = itemDescricao[i].split("---");
			long id = Long.parseLong(parts[1]);
			if(getItemDescricaoById(db,id))
				deleteItemDescricao(db,id);
			String nome = parts[0];
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date data = null;
			try {
				data = df.parse(parts[2]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			long idVersao = 0;
			long municipioId = Long.parseLong(parts[4]);
			values.put("nome", nome);
			values.put("id", id);
			values.put("data_atualizacao", data.getTime());
			values.put("id_versao", idVersao);
			values.put("municipio", municipioId);
			
			db.insert("item_descricao", null, values);
		}
		
	}
	
	private void deleteItemDescricao(SQLiteDatabase db, long id) {
		ItemDescricaoDao dao = new ItemDescricaoDao();
		dao.deleteItemDescricao(db,id);
	}


	private boolean getItemDescricaoById(SQLiteDatabase db, long id) {
		ItemDescricaoDao dao = new ItemDescricaoDao();
		return dao.getItemDescricaoById(db,id);
	}

	public List<TipoRota> getTipoDeRota(ListActivity classe) {
		conexao = new Conexao(classe);
		SQLiteDatabase db = conexao.getReadableDatabase();

		Cursor cursor = db.rawQuery("select * from tipo_rota",null);

		cursor.moveToFirst();

		List<TipoRota> listaDeTipoRota = new ArrayList<TipoRota>();

		for (int i = 0; i < cursor.getCount(); i++) {

			TipoRota tipoRota = new TipoRota();


			tipoRota.setId(cursor.getLong(0));
			tipoRota.setNome(cursor.getString(1));

			listaDeTipoRota.add(tipoRota);
			cursor.moveToNext();
		}
		cursor.close();
		return listaDeTipoRota;
	}

	public List<RotaTuristica> getDeRotas(ListActivity classe, long id) {
		conexao = new Conexao(classe);
		SQLiteDatabase db = conexao.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT itd.id, itd.nome, rt.tipo_rota from item_descricao itd inner join rota_turistica rt on rt.id = itd.id"
				+ " inner join tipo_rota tr on tr.id = rt.tipo_rota where rt.tipo_rota = " + id,null);
		
		List<RotaTuristica> listaDeRotaTuristica = new ArrayList<RotaTuristica>();
		
		if(cursor.moveToFirst()){
			for (int i = 0; i < cursor.getCount(); i++) {
				RotaTuristica rota = new RotaTuristica();
				rota.setId(cursor.getLong(0));
				rota.setNome(cursor.getString(1));
				rota.setTipo_rota(cursor.getLong(2));
				listaDeRotaTuristica.add(rota);
				
				cursor.moveToNext();
			}
			
		}
		
		return listaDeRotaTuristica;
	}

	public List<PontoLocalizavel> getRota(FragmentActivity classe, long idTipo) {
		conexao = new Conexao(classe);
		SQLiteDatabase db = conexao.getReadableDatabase();
		Cursor cursor = db.rawQuery("select pl.id, pl.latitude, pl.longitude from item_descricao it "
				+ "inner join rota_turistica rt On rt.id = it.id "
				+ "inner join ordem_ponto_rota op ON op.rota_turistica_id = rt.id "
				+ "inner join ponto_localizavel pl ON pl.id = op.ponto_localizavel_id "
				+ "where rt.tipo_rota = " + idTipo + " order by op.posicao ",null);

		List<PontoLocalizavel> listaDePontoLocalizavel = new ArrayList<PontoLocalizavel>();

		if(cursor.moveToFirst()){
			for (int i = 0; i < cursor.getCount(); i++) {
				PontoLocalizavel ponto = new PontoLocalizavel();
				ponto.setId(cursor.getLong(0));
				ponto.setNome(getNomePontoLocalizavel(classe,ponto.getId()));
				ponto.setLatitude(cursor.getDouble(1));
				ponto.setLongitude(cursor.getDouble(2));

				listaDePontoLocalizavel.add(ponto);
				cursor.moveToNext();
			}
		}

		return listaDePontoLocalizavel;
	}

	private String getNomePontoLocalizavel(FragmentActivity classe, Long id) {
		PontoLocalizavelDao dao = new PontoLocalizavelDao();
		return dao.getNomePontoLocalizavel(classe,id);
	}

	public void salvaRota(DownloadListItemActivity classe,List<TipoRota> listaTipoRota,List<RotaTuristica> listaRotaTuristica,
			List<Multimidia> listaMultimidia,List<AtracaoTuristica> listaAtracaoTuristica,List<Descricao> listaDescricao, List<OrdemPontoRota> listaOrdemPontoRota) {
		conexao = new Conexao(classe);
		SQLiteDatabase db = conexao.getWritableDatabase();
		
			salvarItemDescricao(db,listaRotaTuristica);
			salvaTipoRota(db, listaTipoRota);
			salvaRota(db,listaRotaTuristica);
			salvaPontoRota(db,listaOrdemPontoRota);
			salvaDescricao(db,listaDescricao);
			salvaAtracaoEmItemDescricao(db,listaAtracaoTuristica);
			salvarPontoLocalizavel(db, listaAtracaoTuristica);
			salvaMultimidia(db,listaMultimidia,classe);
		
		
	}
	
	private void salvaMultimidia(SQLiteDatabase db,List<Multimidia> listaMultimidia, Context classe) {
		for(int i = 0; i < listaMultimidia.size(); i++){
			ContentValues values = new ContentValues();
			String url  = listaMultimidia.get(i).getUrl();
			long id = listaMultimidia.get(i).getId();
			long itemDescricao = listaMultimidia.get(i).getItem_descricao();
			values.put("item_descricao_id", itemDescricao);
			values.put("id", id);
			values.put("url", getNomeImagem(url,classe));
			
			db.insert("multimidia", null, values);
		}
		
	}

	private String getNomeImagem(String url, Context classe) {
		String nome = FilenameUtils.getBaseName(url);
        String extensao = FilenameUtils.getExtension(url);
        String imagemNome = nome+"."+extensao;
        EnderecoImagem enderecoImagem = new EnderecoImagem();
        boolean sucesso = false;
        int contador = 1;
        while(!sucesso){
        	File imagem = new File(enderecoImagem.getEndereco()+imagemNome);
        	if(imagem.isFile()){
        		imagemNome = nome+"_"+contador+"."+extensao;
        	}else{
        		sucesso = true;
        	}
        	contador ++;
        }
    	String folder = enderecoImagem.getEndereco();
        return folder+imagemNome;
	}


	private void salvarPontoLocalizavel(SQLiteDatabase db,	List<AtracaoTuristica> listaAtracaoTuristica) {
		for(int i = 0; i < listaAtracaoTuristica.size(); i++){
			if(getPontoLocalizavelById(db,listaAtracaoTuristica.get(i).getId()))
				deletePontoLocalizavel(db,listaAtracaoTuristica.get(i).getId());
			ContentValues values = new ContentValues();
			values.put("id", listaAtracaoTuristica.get(i).getId());
			values.put("latitude", listaAtracaoTuristica.get(i).getLatitude());
			values.put("longitude", listaAtracaoTuristica.get(i).getLongitude());
			
			db.insert("ponto_localizavel", null, values);
		}
		
	}

	private void deletePontoLocalizavel(SQLiteDatabase db, Long id) {
		PontoLocalizavelDao dao = new PontoLocalizavelDao();
		dao.deletePontoLocalizavel(db,id);
	}

	private boolean getPontoLocalizavelById(SQLiteDatabase db, Long id) {
		PontoLocalizavelDao dao = new PontoLocalizavelDao();
		return dao.getPontoLocalizavelById(db,id);
	}

	private void salvaAtracaoEmItemDescricao(SQLiteDatabase db,List<AtracaoTuristica> listaAtracaoTuristica) {
		for(int i = 0; i < listaAtracaoTuristica.size(); i++){
			ContentValues values = new ContentValues();
			if(!getItemDescricaoById(db,listaAtracaoTuristica.get(i).getId())){
				long idVersao = 0;
				long municipioId = 0;
				values.put("nome", listaAtracaoTuristica.get(i).getNome());
				values.put("id", listaAtracaoTuristica.get(i).getId());
				values.put("data_atualizacao", 0);
				values.put("id_versao", idVersao);
				values.put("municipio", municipioId);
				
				db.insert("item_descricao", null, values);
			}
		}
		
	}

	private void salvaDescricao(SQLiteDatabase db,List<Descricao> listaDescricao) {
		for(int i =0; i < listaDescricao.size(); i ++){
			if(getDescricaoById(db,listaDescricao.get(i).getId()))
				deleteDescricao(db,listaDescricao.get(i).getId());
			ContentValues values = new ContentValues();
			values.put("id", listaDescricao.get(i).getId());
			values.put("descricao", listaDescricao.get(i).getDescricao());
			values.put("idiomaid", listaDescricao.get(i).getIdioma());
			values.put("item_descricao_id", listaDescricao.get(i).getItem_descricao());

			db.insert("descricao", null, values);
		}
	}

	private void salvaPontoRota(SQLiteDatabase db,	List<OrdemPontoRota> listaOrdemPontoRota) {
		for(int i = 0; i < listaOrdemPontoRota.size(); i++){
			ContentValues values = new ContentValues();
			long id =  listaOrdemPontoRota.get(i).getId();
			long posicao = listaOrdemPontoRota.get(i).getPosicao();
			long rotaTuristicaId = listaOrdemPontoRota.get(i).getRotaTuristicaId();
			long pontoLocalizavelId =listaOrdemPontoRota.get(i).getPontoLocalizavelId();
			
			values.put("id", id);
			values.put("posicao", posicao);
			values.put("rota_turistica_id", rotaTuristicaId);
			values.put("ponto_localizavel_id", pontoLocalizavelId);
			
			db.insert("ordem_ponto_rota", null, values);
		}
	}

	private void salvaRota(SQLiteDatabase db,	List<RotaTuristica> listaRotaTuristica) {
		for(int i = 0; i < listaRotaTuristica.size(); i++){
			ContentValues values = new ContentValues();
			long id = listaRotaTuristica.get(i).getId();
			if(getRotaById(db,id))
				deleteRota(db,id);
			long tipoRota = listaRotaTuristica.get(i).getTipo_rota();
			
			values.put("tipo_rota", tipoRota);
			values.put("id", id);
			
			db.insert("rota_turistica", null, values);
		}
		
	}

	private void salvaTipoRota(SQLiteDatabase db, List<TipoRota> listaTipoRota) {
		for(int i = 0; i < listaTipoRota.size(); i ++){
			String rota = listaTipoRota.get(i).getNome();
			long tipoRota = listaTipoRota.get(i).getId();
			if(!getTipoRotaById(db,tipoRota)){
				ContentValues values = new ContentValues();
				values.put("nome", rota);
				values.put("id", tipoRota);
				
				db.insert("tipo_rota", null, values);
			}
		}
		
	}

	private void salvarItemDescricao(SQLiteDatabase db,List<RotaTuristica> listaRotaTuristica) {
		for(int i = 0; i < listaRotaTuristica.size(); i++){
			ContentValues values = new ContentValues();
			if(getItemDescricaoById(db,listaRotaTuristica.get(i).getId()))
				deleteItemDescricao(db,listaRotaTuristica.get(i).getId());
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date data = null;
			long idVersao = 0;
			long municipioId = 0;
			values.put("nome", listaRotaTuristica.get(i).getNome());
			values.put("id", listaRotaTuristica.get(i).getId());
			values.put("data_atualizacao", listaRotaTuristica.get(i).getDataAtualizacao().getTime());
			values.put("id_versao", idVersao);
			values.put("municipio", municipioId);
			
			db.insert("item_descricao", null, values);
		}
	}

}
