package com.example.dao;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.controller.AtracaoController;
import com.example.entidade.AtracaoTuristica;
import com.example.entidade.Descricao;
import com.example.entidade.Endereco;
import com.example.entidade.InfoDoResponsavel;
import com.example.entidade.Multimidia;
import com.example.entidade.PontoLocalizavel;
import com.example.entidade.Telefone;
import com.example.entidade.TipoAtracao;
import com.example.rotaturistica.AtracaoListActivity;
import com.example.rotaturistica.AtracaoSelecionadoActivity;
import com.example.rotaturistica.AtracaoListActivity;
import com.example.rotaturistica.DownloadListItemActivity;
import com.example.rotaturistica.NearMap;
import com.example.utils.EnderecoImagem;
import com.example.utils.SalvaImagemNoSD;
import com.google.android.gms.drive.internal.GetMetadataRequest;
import com.google.android.gms.maps.model.LatLng;

@SuppressLint("SimpleDateFormat")
public class AtracaoTuristicaDao {

	private Conexao conexao;

	public List<AtracaoTuristica> getAtracoes(){

		return null;
	}

	public List<TipoAtracao> getTipoDeAtracao(AtracaoListActivity atracaoListActivity) {
		conexao = new Conexao(atracaoListActivity);
		SQLiteDatabase db = conexao.getReadableDatabase();

		Cursor cursor = db.rawQuery("select * from tipo_atracao",null);

		cursor.moveToFirst();

		List<TipoAtracao> listaDeTipoAtracao = new ArrayList<TipoAtracao>();

		for (int i = 0; i < cursor.getCount(); i++) {

			TipoAtracao tipoAtracao = new TipoAtracao();


			tipoAtracao.setId(cursor.getLong(0));
			tipoAtracao.setAtracao(cursor.getString(1));

			listaDeTipoAtracao.add(tipoAtracao);
			cursor.moveToNext();
		}
		cursor.close();
		return listaDeTipoAtracao;
	}

	public List<AtracaoTuristica> getAtracoes( AtracaoSelecionadoActivity atracaoSelecionadoActivity, long idTipo) {
		conexao = new Conexao(atracaoSelecionadoActivity);
		SQLiteDatabase db = conexao.getReadableDatabase();

		Cursor cursor = db.rawQuery("select item_descricao.id, item_descricao.nome, ponto.latitude, ponto.longitude, tipo.atracao, "
				+ "tipo.id, end.logradouro, end.numero, end.bairro, end.complemento, end.cep "
				+ "from item_descricao "
				+ "inner join ponto_localizavel ponto ON ponto.id = item_descricao.id "
				+ "inner join atracao_turistica atracao ON atracao.id = ponto.id "
				+ "inner join tipo_atracao tipo ON atracao.tipo = tipo.id "
				+ "inner join endereco end ON end.ponto_localizavel_id = item_descricao.id "
				+ "WHERE atracao.tipo = " + idTipo, null);

		cursor.moveToFirst();

		List<AtracaoTuristica> listaDeAtracao = new ArrayList<AtracaoTuristica>();

		for (int i = 0; i < cursor.getCount(); i++) {
			AtracaoTuristica atracao = new AtracaoTuristica();
			Endereco endereco = new Endereco();

			atracao.setId(cursor.getLong(0));
			atracao.setNome(cursor.getString(1));
			atracao.setLatitude(cursor.getDouble(2));
			atracao.setLongitude(cursor.getDouble(3));
			atracao.setTipoAtracao(cursor.getLong(5));
			
			endereco.setLogradouro(cursor.getString(6));
			endereco.setNumero(cursor.getString(7));
			endereco.setBairro(cursor.getString(8));
			endereco.setComplemento(cursor.getString(9));
			endereco.setCep(cursor.getString(10));
			
			
			
			atracao.setEndereco(endereco);

			listaDeAtracao.add(atracao);
			cursor.moveToNext();


		}
		return listaDeAtracao;
	}
	
	
	private List<Descricao> getDescricao(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	


	public List<PontoLocalizavel> getAtracoesNear(	NearMap nearMap, long idTipo, Location location) {
		conexao = new Conexao(nearMap);
		SQLiteDatabase db = conexao.getReadableDatabase();

		Cursor cursor = db.rawQuery("SELECT item_descricao.nome, ponto.latitude, ponto.longitude, SQRT(POW(69.1 * (latitude - " + location.getLatitude() + "), 2) + POW(69.1 * (" + location.getLongitude() + " - longitude) * COS(latitude / 57.3), 2)) AS distance "
				+ "FROM ponto_localizavel ponto inner join item_descricao item_descricao ON ponto.item_descricao_id = item_descricao.id "
				+ "inner join atracao_turistica atracao ON atracao.ponto_localizavel_id = ponto.id"
				+ "inner join tipo_atracao tipo ON tipo.id = atracao.tipo where tipo_id = " + idTipo + " "
				+ "HAVING distance < 25 ORDER BY distance", null);

		cursor.moveToFirst();

		List<PontoLocalizavel> listaDePontosLocalizavel = new ArrayList<PontoLocalizavel>();

		for (int i = 0; i < cursor.getCount(); i++) {

			PontoLocalizavel ponto = new PontoLocalizavel();

			ponto.setNome(cursor.getString(0));
			ponto.setLatitude(cursor.getDouble(1));
			ponto.setLongitude(cursor.getDouble(1));

			listaDePontosLocalizavel.add(ponto);
			cursor.moveToNext();
		}


		return listaDePontosLocalizavel;
	}

	public List<PontoLocalizavel> getAtracoesNear(NearMap nearMap, long idTipo, double d, double e) {
		conexao = new Conexao(nearMap);
		SQLiteDatabase db = conexao.getReadableDatabase();

		int cuadras = (int)(-0.01 * 1E6) ; //10 blocks aprox, I guess

		Cursor cursor = db.rawQuery("SELECT item_descricao.nome, ponto.latitude, ponto.longitude "
				+ "FROM ponto_localizavel ponto "
				+ "inner join item_descricao item_descricao ON ponto.item_descricao_id = item_descricao.id "
				+ "inner join atracao_turistica atracao ON atracao.ponto_localizavel_id = ponto.id "
				+ "inner join tipo_atracao tipo ON tipo.id = atracao.tipo where tipo.id = " + idTipo + " ORDER by abs(ponto.latitude - ("+ d +")) + abs( ponto.longitude - ("+ e + ")) LIMIT 65", null);



		cursor.moveToFirst();

		List<PontoLocalizavel> listaDePontosLocalizavel = new ArrayList<PontoLocalizavel>();

		for (int i = 0; i < cursor.getCount(); i++) {

			PontoLocalizavel ponto = new PontoLocalizavel();

			ponto.setNome(cursor.getString(0));
			ponto.setLatitude(cursor.getDouble(1));
			ponto.setLongitude(cursor.getDouble(2));

			listaDePontosLocalizavel.add(ponto);
			cursor.moveToNext();
		}


		return listaDePontosLocalizavel;
	}

	public List<PontoLocalizavel> getPontosLocalizaveis(NearMap nearMap, long idTipo) {
		conexao = new Conexao(nearMap);
		SQLiteDatabase db = conexao.getReadableDatabase();

		Cursor cursor = db.rawQuery("SELECT item_descricao.nome, ponto.latitude, ponto.longitude FROM ponto_localizavel ponto "
				+ "inner join item_descricao item_descricao ON ponto.id = item_descricao.id "
				+ "inner join atracao_turistica atracao ON atracao.id = ponto.id where atracao.tipo = " + idTipo, null);



		cursor.moveToFirst();

		List<PontoLocalizavel> listaDePontosLocalizavel = new ArrayList<PontoLocalizavel>();

		for (int i = 0; i < cursor.getCount(); i++) {

			PontoLocalizavel ponto = new PontoLocalizavel();

			ponto.setNome(cursor.getString(0));
			ponto.setLatitude(cursor.getDouble(1));
			ponto.setLongitude(cursor.getDouble(2));

			listaDePontosLocalizavel.add(ponto);
			cursor.moveToNext();

		}
		
		return listaDePontosLocalizavel;
	}

	public Long dataUltimaAtualizacao(long idTipoAtracao, DownloadListItemActivity downloadListItemActivity) {
		conexao = new Conexao(downloadListItemActivity);
		SQLiteDatabase db = conexao.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT max(itd.data_atualizacao), itd.id from item_descricao itd inner join ponto_localizavel pl on pl.id = itd.id"
				+ " inner join atracao_turistica at on at.id = pl.id where at.tipo = " + idTipoAtracao,null);
		long data = 0;
		if(cursor.moveToFirst()) {
			data = cursor.getLong(0);
			long id = cursor.getLong(1);
			Log.w("","Data 1 - " + data + " id - " + id);
		}
		return data;
	}

	public void salvaAtracao(DownloadListItemActivity downloadListItemActivity, String[] itemDescricao, String[] multimidia, String[] responsavel, String[] telefone) {
		conexao = new Conexao(downloadListItemActivity);
		SQLiteDatabase db = conexao.getWritableDatabase();
		try{
			salvarItemDescricao(db,itemDescricao);
			salvarTipoAtracao(db,itemDescricao);
			salvarDescricao(db, itemDescricao);
			salvarPontoLocalizavel(db,itemDescricao);
			salvaAtracao(db,itemDescricao);
			salvaEndereco(db,itemDescricao);
//			salvaMultimidia(db,multimidia,downloadListItemActivity);
			salvaResponsavel(db,responsavel);
			salvaTelefone(db,telefone);
			salvaMunicipio(db, itemDescricao);
			salvaPolo(db, itemDescricao);
		}catch(Exception e){
			
		}
			
	}
	
	public void salvaAtracao(FragmentActivity classe, String[] itemDescricao, String[] multimidia, String[] responsavel, String[] telefone) {
		conexao = new Conexao(classe);
		SQLiteDatabase db = conexao.getWritableDatabase();
		try{
			salvarTipoAtracao(db,itemDescricao);
			salvarDescricao(db, itemDescricao);
			salvarPontoLocalizavel(db,itemDescricao);
			salvaAtracao(db,itemDescricao);
			salvaEndereco(db,itemDescricao);
//			salvaMultimidia(db,multimidia,downloadListItemActivity);
			salvaResponsavel(db,responsavel);
			salvaTelefone(db,telefone);
			salvaMunicipio(db, itemDescricao);
			salvaPolo(db, itemDescricao);
			salvarItemDescricao(db,itemDescricao);
		}catch(Exception e){
			
		}
			
	}
	
	private void salvaMunicipio(SQLiteDatabase db, String[] itemDescricao) {
		String[] parts = itemDescricao[0].split("---");
		long idMunicipio = Long.parseLong(parts[4]);
		if(!getMunicipioById(db,idMunicipio)){
			String municipio = parts[17];
			long idPolo = Long.parseLong(parts[15]);
			ContentValues values = new ContentValues();
			values.put("municipio", municipio);
			values.put("id", idMunicipio);
			values.put("polo_id", idPolo);
			
			db.insert("municipio", null, values);
		}
	}

	private boolean getMunicipioById(SQLiteDatabase db, long idMunicipio) {
		MunicipioDao dao = new MunicipioDao();
		return dao.getMunicipioById(db,idMunicipio);
	}

	private void salvaPolo(SQLiteDatabase db, String[] itemDescricao) {
		String[] parts = itemDescricao[0].split("---");
		long idPolo = Long.parseLong(parts[15]);
		if(!getPoloById(db,idPolo)){
			String polo = parts[16];
			ContentValues values = new ContentValues();
			values.put("polo", polo);
			values.put("id", idPolo);
			
			db.insert("polo_turistico", null, values);
		}
	}

	private boolean getPoloById(SQLiteDatabase db, long idPolo) {
		PoloDao dao = new PoloDao();
		return dao.getPoloById(db,idPolo);
	}

	private void salvarTipoAtracao(SQLiteDatabase db, String[] itemDescricao) {
		String[] parts = itemDescricao[0].split("---");
		String atracao = (parts[15]);
		long tipoAtracao = Long.parseLong(parts[3]);
		if(!getTipoAtracaoById(db,tipoAtracao)){
			ContentValues values = new ContentValues();
			values.put("atracao", atracao);
			values.put("id", tipoAtracao);
			values.put("pictograma", 0);
			
			db.insert("tipo_atracao", null, values);
		}
		
		
	}

	private boolean getTipoAtracaoById(SQLiteDatabase db, long tipoAtracao) {
		Cursor cursor = db.rawQuery("select * from tipo_atracao where id = " + tipoAtracao,null);
		return cursor.moveToFirst();
	}

	private void salvarDescricao(SQLiteDatabase db, String[] itemDescricao) {
		for(int i = 0; i < itemDescricao.length; i++){
			ContentValues values = new ContentValues();
			String[] parts = itemDescricao[i].split("---");
			long id = Long.parseLong(parts[12]);
			if(getDescricaoById(db,id))
				deleteDescricao(db,id);
			String nome = parts[0];
			long itemDescricaoId = Long.parseLong(parts[1]);
			String descricao = parts[13];
			long idiomaId = Long.parseLong(parts[14]);
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

	private void salvarPontoLocalizavel(SQLiteDatabase db,	String[] itemDescricao) {
		for(int i = 0; i < itemDescricao.length; i++){
			ContentValues values = new ContentValues();
			String[] parts = itemDescricao[i].split("---");
			long id = Long.parseLong(parts[1]);
			if(getPontoLocalizavelById(db,id))
				deletePontoLocalizavel(db,id);
			double latitude = Double.parseDouble(parts[5]);
			double longitude = Double.parseDouble(parts[6]);
			values.put("id", id);
			values.put("latitude", latitude);
			values.put("longitude", longitude);
			
			db.insert("ponto_localizavel", null, values);
		}
		
	}
	
	private void deletePontoLocalizavel(SQLiteDatabase db, long id) {
		PontoLocalizavelDao dao = new PontoLocalizavelDao();
		dao.deletePontoLocalizavel(db,id);
		
	}


	private boolean getPontoLocalizavelById(SQLiteDatabase db, long id) {
		PontoLocalizavelDao dao = new PontoLocalizavelDao();
		return dao.getPontoLocalizavelById(db,id);
	}

	private void salvaTelefone(SQLiteDatabase db, String[] telefone) {
		for(int i = 0; i < telefone.length; i++){
			ContentValues values = new ContentValues();
			String[] parts = telefone[i].split("---");
			long id = Long.parseLong(parts[1]);
			if(getTelefoneById(db,id))
				deleteTelefone(db,id);
			String codArea  = parts[2];
			String numero = parts[3];
			long responsavelId = Long.parseLong(parts[0]);
			values.put("responsavel_id", responsavelId);
			values.put("id", id);
			values.put("cod_area", codArea);
			values.put("numero", numero);
			
			db.insert("telefone", null, values);
		}
		
		
	}
	
	private void deleteTelefone(SQLiteDatabase db, long id) {
		TelefoneDao dao = new TelefoneDao();
		dao. deleteTelefone(db,id);
		
	}


	private boolean getTelefoneById(SQLiteDatabase db, long id) {
		TelefoneDao dao = new TelefoneDao();
		return dao.getTelefoneById(db,id);
	}

	private void salvaResponsavel(SQLiteDatabase db, String[] responsavel) {
		for(int i = 0; i < responsavel.length; i++){
			ContentValues values = new ContentValues();
			String[] parts = responsavel[i].split("---");
			long id = Long.parseLong(parts[1]);
			if(getResponsavelById(db,id))
				deleteResponsavel(db,id);
			String nome  = parts[2];
			String email = parts[3];
			String facebook;
			String instagram;
			String webSite;
			long pontoLocalizavel = Long.parseLong(parts[1]);
			values.put("ponto_localizavel_id", pontoLocalizavel);
			values.put("id", id);
			values.put("nome", nome);
			values.put("email", email);
			
			db.insert("info_do_responsavel", null, values);
		}
		
	}
	
	private void deleteResponsavel(SQLiteDatabase db, long id) {
		ResponsavelDao dao = new ResponsavelDao();
		dao.deleteResponsavel(db,id);
	}


	private boolean getResponsavelById(SQLiteDatabase db, long id) {
		ResponsavelDao dao = new ResponsavelDao();
		return dao.getResponsavelById(db,id);
	}

	

	

	private void salvaEndereco(SQLiteDatabase db, String[] itemDescricao) {
		for(int i = 0; i < itemDescricao.length; i++){
			ContentValues values = new ContentValues();
			String[] parts = itemDescricao[i].split("---");
			long id = Long.parseLong(parts[11]);
			if(getEnderecoById(db,id))
				deleteEndereco(db,id);
			String logradouro = parts[7];
			String numero = parts[8];
			String bairro = parts[9];
			String complemento = "complemento";
			String cep = parts[10];
			long pontoLocalizavel = Long.parseLong(parts[1]);
			values.put("ponto_localizavel_id", pontoLocalizavel);
			values.put("id", id);
			values.put("logradouro", logradouro);
			values.put("numero", numero);
			values.put("bairro", bairro);
			values.put("complemento", complemento);
			values.put("cep", cep);
			
			db.insert("endereco", null, values);
		}
		
	}
	
	private void deleteEndereco(SQLiteDatabase db, long id) {
		EnderecoDao dao = new EnderecoDao();
		dao.deleteEndereco(db,id);
		
	}


	private boolean getEnderecoById(SQLiteDatabase db, long id) {
		EnderecoDao dao = new EnderecoDao();
		return dao.getEnderecoById(db,id);
	}

	private void salvaAtracao(SQLiteDatabase db, String[] itemDescricao) {
		for(int i = 0; i < itemDescricao.length; i++){
			ContentValues values = new ContentValues();
			String[] parts = itemDescricao[i].split("---");
			long id = Long.parseLong(parts[1]);
			if(getAtracaoById(db,id))
				deleteAtracao(db,id);
			long tipoAtracao = Long.parseLong(parts[3]);
			values.put("tipo", tipoAtracao);
			values.put("id", id);
			
			db.insert("atracao_turistica", null, values);
		}
		
	}

	private void deleteAtracao(SQLiteDatabase db, long id) {
		db.delete("atracao_turistica", "id = " + id, null);
		
	}

	private boolean getAtracaoById(SQLiteDatabase db, long id) {
		Cursor cursor = db.rawQuery("select * from atracao_turistica where id = " + id,null);
		return cursor.moveToFirst();
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

	public long dataUltimaAtualizacao(FragmentActivity classe, long idMunicipio) {
		conexao = new Conexao(classe);
		SQLiteDatabase db = conexao.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT max(itd.data_atualizacao), itd.id from item_descricao itd inner join ponto_localizavel pl on pl.id = itd.id"
				+ " inner join atracao_turistica at on at.id = pl.id where itd.municipio = " + idMunicipio,null);
		long data = 0;
		if(cursor.moveToFirst()) {
			data = cursor.getLong(0);
		}
		return data;
	}

	public long contadorDeAtracao(FragmentActivity classe, long idMunicipio,long dataUltimaAtualizacao) {
		conexao = new Conexao(classe);
		SQLiteDatabase db = conexao.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT count(itd.id), itd.id from item_descricao itd inner join ponto_localizavel pl on pl.id = itd.id"
				+ " inner join atracao_turistica at on at.id = pl.id where itd.municipio = " + idMunicipio + " and itd.data_atualizacao <= " + dataUltimaAtualizacao,null);
		long contador = 0;
		if(cursor.moveToFirst()) {
			contador = cursor.getLong(0);
		}
		return contador;
	}

	public void salvaAtracao(FragmentActivity classe,List<AtracaoTuristica> listaAtracaoTuristicas, List<TipoAtracao> listaDeAtracao,
			List<InfoDoResponsavel> listaDoResponsavel,	List<Multimidia> listaMultimidia, List<Endereco> listaEnderecos,
			List<Telefone> listaTelefone, List<Descricao> listaDescricao, long idPolo, String polo, long idMunicipio, String municipio) {
		conexao = new Conexao(classe);
		SQLiteDatabase db = conexao.getWritableDatabase();
		
		salvarTipoAtracao(db,listaDeAtracao);
		salvarDescricao(db, listaDescricao);
		salvarPontoLocalizavel(db,listaAtracaoTuristicas);
		salvaAtracao(db,listaAtracaoTuristicas);
		salvaEndereco(db,listaEnderecos);
		salvaMultimidia(db,listaMultimidia,classe);
		salvaResponsavel(db,listaDoResponsavel);
		salvaTelefone(db,listaTelefone);
		salvaMunicipio(db, idPolo, idMunicipio,municipio);
		salvaPolo(db, idPolo, polo);
		salvarItemDescricao(db,listaAtracaoTuristicas);
	}

	private void salvaMultimidia(SQLiteDatabase db,List<Multimidia> listaMultimidia, FragmentActivity classe) {
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

	private String getNomeImagem(String url, FragmentActivity classe) {
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

	private void salvarItemDescricao(SQLiteDatabase db,List<AtracaoTuristica> listaAtracaoTuristicas) {
		for(int i = 0; i < listaAtracaoTuristicas.size(); i++){
			ContentValues values = new ContentValues();
			if(getItemDescricaoById(db,listaAtracaoTuristicas.get(i).getId()))
				deleteItemDescricao(db,listaAtracaoTuristicas.get(i).getId());
			
			values.put("nome", listaAtracaoTuristicas.get(i).getNome());
			values.put("id", listaAtracaoTuristicas.get(i).getId());
			values.put("data_atualizacao", listaAtracaoTuristicas.get(i).getDataAtualizacao().getTime());
			values.put("id_versao", 0);
			values.put("municipio", listaAtracaoTuristicas.get(i).getMunicipio_id());
			
			db.insert("item_descricao", null, values);
		}
		
	}

	private void salvaPolo(SQLiteDatabase db, long idPolo, String polo) {
		if(!getPoloById(db,idPolo)){
			ContentValues values = new ContentValues();
			values.put("polo", polo);
			values.put("id", idPolo);
			
			db.insert("polo_turistico", null, values);
		}
		
	}

	private void salvaMunicipio(SQLiteDatabase db, long idPolo,long idMunicipio, String municipio) {
		if(!getMunicipioById(db,idMunicipio)){
			ContentValues values = new ContentValues();
			values.put("municipio", municipio);
			values.put("id", idMunicipio);
			values.put("polo_id", idPolo);
			
			db.insert("municipio", null, values);
		}
		
	}

	private void salvaTelefone(SQLiteDatabase db, List<Telefone> listaTelefone) {
		for(int i = 0; i < listaTelefone.size(); i++){
			ContentValues values = new ContentValues();
			if(getTelefoneById(db,listaTelefone.get(i).getId()))
				deleteTelefone(db,listaTelefone.get(i).getId());
			values.put("responsavel_id", listaTelefone.get(i).getResponsavel().getId());
			values.put("id", listaTelefone.get(i).getId());
			values.put("cod_area", listaTelefone.get(i).getCodArea());
			values.put("numero", listaTelefone.get(i).getNumero());
			
			db.insert("telefone", null, values);
		}
		
	}

	private void salvaResponsavel(SQLiteDatabase db,List<InfoDoResponsavel> listaDoResponsavel) {
		for(int i = 0; i < listaDoResponsavel.size(); i++){
			ContentValues values = new ContentValues();
			if(getResponsavelById(db,listaDoResponsavel.get(i).getId()))
				deleteResponsavel(db,listaDoResponsavel.get(i).getId());
			values.put("ponto_localizavel_id", listaDoResponsavel.get(i).getPontoLocalizavel().getId());
			values.put("id", listaDoResponsavel.get(i).getId());
			values.put("nome", listaDoResponsavel.get(i).getNome());
			values.put("email", listaDoResponsavel.get(i).getEmail());
			
			db.insert("info_do_responsavel", null, values);
		}
		
	}

	private void salvaEndereco(SQLiteDatabase db, List<Endereco> listaEnderecos) {
		for(int i = 0; i < listaEnderecos.size(); i++){
			ContentValues values = new ContentValues();
			if(getEnderecoById(db,listaEnderecos.get(i).getId()))
				deleteEndereco(db,listaEnderecos.get(i).getId());
			values.put("ponto_localizavel_id", listaEnderecos.get(i).getPontoLocalizavel().getId());
			values.put("id", listaEnderecos.get(i).getId());
			values.put("logradouro", listaEnderecos.get(i).getLogradouro());
			values.put("numero", listaEnderecos.get(i).getNumero());
			values.put("bairro", listaEnderecos.get(i).getBairro());
			values.put("complemento", listaEnderecos.get(i).getComplemento());
			values.put("cep", listaEnderecos.get(i).getCep());
			
			db.insert("endereco", null, values);
		}
		
	}

	private void salvaAtracao(SQLiteDatabase db,List<AtracaoTuristica> listaDeAtracaoTuristica) {
		for(int i = 0; i < listaDeAtracaoTuristica.size(); i++){
			ContentValues values = new ContentValues();
			if(getAtracaoById(db,listaDeAtracaoTuristica.get(i).getId()))
				deleteAtracao(db,listaDeAtracaoTuristica.get(i).getId());
			values.put("tipo", listaDeAtracaoTuristica.get(i).getTipoAtracao());
			values.put("id", listaDeAtracaoTuristica.get(i).getId());
			
			db.insert("atracao_turistica", null, values);
		}
		
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

	private void salvarDescricao(SQLiteDatabase db,List<Descricao> listaDescricao) {
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

	private void salvarTipoAtracao(SQLiteDatabase db, List<TipoAtracao> listaDeAtracao) {
		for(int i = 0; i <listaDeAtracao.size(); i++){
			if(!getTipoAtracaoById(db,listaDeAtracao.get(i).getId())){
				ContentValues values = new ContentValues();
				values.put("atracao", listaDeAtracao.get(i).getAtracao());
				values.put("id", listaDeAtracao.get(i).getId());
				values.put("pictograma", 0);
				
				db.insert("tipo_atracao", null, values);
			}
		}
		
	}

}