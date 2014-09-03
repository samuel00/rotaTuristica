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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.entidade.ServicoTuristico;
import com.example.entidade.Descricao;
import com.example.entidade.Endereco;
import com.example.entidade.InfoDoResponsavel;
import com.example.entidade.Multimidia;
import com.example.entidade.PontoLocalizavel;
import com.example.entidade.ServicoTuristico;
import com.example.entidade.Telefone;
import com.example.entidade.TipoServico;
import com.example.entidade.TipoServico;
import com.example.rotaturistica.DownloadListItemActivity;
import com.example.rotaturistica.NearMap;
import com.example.rotaturistica.ServicoListActivity;
import com.example.rotaturistica.ServicoSelecionadoActivity;
import com.example.utils.EnderecoImagem;
import com.example.utils.SalvaImagemNoSD;




public class ServicoTuristicoDao{
	
	private Conexao conexao;
	
	public List<ServicoTuristico> getServico(){
		return null;
	}
	
	
	public List<ServicoTuristico> getServicos(ServicoSelecionadoActivity servicoSelecionadoActivity, long idTipo) {
		conexao = new Conexao(servicoSelecionadoActivity);
		SQLiteDatabase db = conexao.getReadableDatabase();

		Cursor cursor = db.rawQuery("select item_descricao.id, item_descricao.nome, ponto.latitude, ponto.longitude, tipo.servico, "
				+ "tipo.id, end.logradouro, end.numero, end.bairro, end.complemento, end.cep "
				+ "from item_descricao "
				+ "inner join ponto_localizavel ponto ON ponto.id = item_descricao.id "
				+ "inner join servico_turistico servico ON servico.id = ponto.id "
				+ "inner join tipo_servico tipo ON servico.tipo = tipo.id "
				+ "inner join endereco end ON end.ponto_localizavel_id = item_descricao.id "
				+ "WHERE servico.tipo = " + idTipo, null);

		cursor.moveToFirst();

		List<ServicoTuristico> listaDeServico = new ArrayList<ServicoTuristico>();

		for (int i = 0; i < cursor.getCount(); i++) {

			ServicoTuristico servico= new ServicoTuristico();
			Endereco endereco = new Endereco();

			servico.setId(cursor.getLong(0));
			servico.setNome(cursor.getString(1));
			servico.setLatitude(cursor.getDouble(2));
			servico.setLongitude(cursor.getDouble(3));
			servico.setTipoServico(cursor.getLong(5));
			
			endereco.setLogradouro(cursor.getString(6));
			endereco.setNumero(cursor.getString(7));
			endereco.setBairro(cursor.getString(8));
			endereco.setComplemento(cursor.getString(9));
			endereco.setCep(cursor.getString(10));
			
			
			
			servico.setEndereco(endereco);
			listaDeServico.add(servico);
			cursor.moveToNext();
		}
		cursor.close();
		return listaDeServico;
	}
	
	public List<TipoServico> getTipoServico(ListActivity classe) {
		conexao = new Conexao(classe);
		SQLiteDatabase db = conexao.getReadableDatabase();

		Cursor cursor = db.rawQuery("select * from tipo_servico",null);

		if(cursor.moveToFirst()){

			List<TipoServico> listaDeServico = new ArrayList<TipoServico>();

			for (int i = 0; i < cursor.getCount(); i++) {

				TipoServico servico= new TipoServico();


				servico.setId(cursor.getLong(0));
				servico.setServico(cursor.getString(1));

				listaDeServico.add(servico);
				cursor.moveToNext();
			}
			cursor.close();
			return listaDeServico;
		}else{
			return null;
		}
	}


	public long dataUltimaAtualizacao(long idTipoServico,	DownloadListItemActivity downloadListItemActivity) {
		conexao = new Conexao(downloadListItemActivity);
		SQLiteDatabase db = conexao.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT max(itd.data_atualizacao), itd.id from item_descricao itd inner join ponto_localizavel pl on pl.id = itd.id"
				+ " inner join servico_turistico st on st.id = pl.id where st.tipo = " + idTipoServico,null);
		long data = 0;
		if(cursor.moveToFirst()) {
			data = cursor.getLong(0);
			long id = cursor.getLong(1);
		}
		return data;
	}


	public void salvaServico(DownloadListItemActivity downloadListItemActivity,String[] itemDescricao, String[] multimidia, String[] responsavel,	String[] telefone) {
		conexao = new Conexao(downloadListItemActivity);
		SQLiteDatabase db = conexao.getWritableDatabase();
		try{
			salvarItemDescricao(db,itemDescricao);
			salvarDescricao(db,itemDescricao);
			salvarTipoServico(db,itemDescricao);
			salvarPontoLocalizavel(db,itemDescricao);
			salvaServico(db,itemDescricao);
			salvaEndereco(db,itemDescricao);
//			salvaMultimidia(db,multimidia,downloadListItemActivity);
			salvaResponsavel(db,responsavel);
			salvaTelefone(db,telefone);
			salvaMunicipio(db, itemDescricao);
			salvaPolo(db, itemDescricao);
		}catch(Exception e){
			
		}
			
		
	}
	
	public void salvaServico(FragmentActivity classe,String[] itemDescricao, String[] multimidia, String[] responsavel,	String[] telefone) {
		conexao = new Conexao(classe);
		SQLiteDatabase db = conexao.getWritableDatabase();
		try{
			salvarItemDescricao(db,itemDescricao);
			salvarDescricao(db,itemDescricao);
			salvarTipoServico(db,itemDescricao);
			salvarPontoLocalizavel(db,itemDescricao);
			salvaServico(db,itemDescricao);
			salvaEndereco(db,itemDescricao);
//			salvaMultimidia(db,multimidia,downloadListItemActivity);
			salvaResponsavel(db,responsavel);
			salvaTelefone(db,telefone);
			salvaMunicipio(db, itemDescricao);
			salvaPolo(db, itemDescricao);
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

	private void salvarTipoServico(SQLiteDatabase db, String[] itemDescricao) {
		String[] parts = itemDescricao[0].split("---");
		String servico = (parts[15]);
		long TipoServico = Long.parseLong(parts[3]);
		if(!getTipoServicoById(db,TipoServico)){
			ContentValues values = new ContentValues();
			values.put("servico", servico);
			values.put("id", TipoServico);
			values.put("pictograma", 0);
			
			db.insert("tipo_servico", null, values);
		}
		
	}


	private boolean getTipoServicoById(SQLiteDatabase db, long TipoServico) {
		Cursor cursor = db.rawQuery("select * from tipo_servico where id = " + TipoServico,null);
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
			String descricao = parts[13];
			long itemDescricaoId = Long.parseLong(parts[1]);
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


	private void salvaMultimidia(SQLiteDatabase db, String[] multimidia, DownloadListItemActivity downloadListItemActivity) {
		for(int i = 0; i < multimidia.length; i++){
			ContentValues values = new ContentValues();
			String[] parts = multimidia[i].split("---");
			String url  = parts[2];
			long id = Long.parseLong(parts[1]);
			long itemDescricao = Long.parseLong(parts[0]);
			values.put("item_descricao_id", itemDescricao);
			values.put("id", id);
			values.put("url", getNomeImagem("http://www.estacaodasdocas.com.br/upload/arq_arquivo/7407.jpg",downloadListItemActivity));
			
			db.insert("multimidia", null, values);
		}
		
	}

	private String getNomeImagem(String url, DownloadListItemActivity downloadListItemActivity) {
		SalvaImagemNoSD sd = new SalvaImagemNoSD();
		return sd.salvaImagem(url,downloadListItemActivity);
		
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


	private void salvaServico(SQLiteDatabase db, String[] itemDescricao) {
		for(int i = 0; i < itemDescricao.length; i++){
			ContentValues values = new ContentValues();
			String[] parts = itemDescricao[i].split("---");
			long id = Long.parseLong(parts[1]);
			if(getServicoById(db,id))
				deleteServico(db,id);
			long tipoAtracao = Long.parseLong(parts[3]);
			values.put("tipo", tipoAtracao);
			values.put("id", id);
			
			db.insert("servico_turistico", null, values);
		}
		
	}

	private void deleteServico(SQLiteDatabase db, long id) {
		db.delete("servico_turistico", "id = " + id, null);
	}


	private boolean getServicoById(SQLiteDatabase db, long id) {
		Cursor cursor = db.rawQuery("select * from servico_turistico where id = " + id,null);
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


	public List<PontoLocalizavel> getPontosLocalizaveis(NearMap nearMap,long idTipo) {
		conexao = new Conexao(nearMap);
		SQLiteDatabase db = conexao.getReadableDatabase();

		Cursor cursor = db.rawQuery("SELECT item_descricao.nome, ponto.latitude, ponto.longitude FROM ponto_localizavel ponto "
				+ "inner join item_descricao item_descricao ON ponto.id = item_descricao.id "
				+ "inner join servico_turistico servico ON servico.id = ponto.id where servico.tipo = " + idTipo, null);



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


	public long dataUltimaAtualizacao(FragmentActivity classe, long idMunicipio) {
		conexao = new Conexao(classe);
		SQLiteDatabase db = conexao.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT max(itd.data_atualizacao), itd.id from item_descricao itd inner join ponto_localizavel pl on pl.id = itd.id"
				+ " inner join servico_turistico st on st.id = pl.id where itd.municipio = " + idMunicipio,null);
		long data = 0;
		if(cursor.moveToFirst()) {
			data = cursor.getLong(0);
		}
		return data;
	}


	public long contadorDeServico(FragmentActivity classe, long idMunicipio, long dataUltimaAtualizacao) {
		conexao = new Conexao(classe);
		SQLiteDatabase db = conexao.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT count(itd.id), itd.id from item_descricao itd inner join ponto_localizavel pl on pl.id = itd.id"
				+ " inner join servico_turistico st on st.id = pl.id where itd.municipio = " + idMunicipio + " and itd.data_atualizacao <= " + dataUltimaAtualizacao,null);
		long contador = 0;
		if(cursor.moveToFirst()) {
			contador = cursor.getLong(0);
		}
		return contador;
	}


	public void salvaAtracao(FragmentActivity classe,List<ServicoTuristico> listaServicoTuristico,	List<TipoServico> listaTipoServico,
			List<InfoDoResponsavel> listaDoResponsavel,	List<Multimidia> listaMultimidia, List<Endereco> listaEnderecos,
			List<Telefone> listaTelefone, List<Descricao> listaDescricao,long idPolo, String polo, long idMunicipio, String municipio) {
		conexao = new Conexao(classe);
		SQLiteDatabase db = conexao.getWritableDatabase();
		
		salvarTipoServico(db,listaTipoServico);
		salvarDescricao(db, listaDescricao);
		salvarPontoLocalizavel(db,listaServicoTuristico);
		salvaServico(db,listaServicoTuristico);
		salvaEndereco(db,listaEnderecos);
		salvaMultimidia(db,listaMultimidia,classe);
		salvaResponsavel(db,listaDoResponsavel);
		salvaTelefone(db,listaTelefone);
		salvaMunicipio(db, idPolo, idMunicipio,municipio);
		salvaPolo(db, idPolo, polo);
		salvarItemDescricao(db,listaServicoTuristico);
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

	private void salvarItemDescricao(SQLiteDatabase db,List<ServicoTuristico> listaServicoTuristicos) {
		for(int i = 0; i < listaServicoTuristicos.size(); i++){
			ContentValues values = new ContentValues();
			if(getItemDescricaoById(db,listaServicoTuristicos.get(i).getId()))
				deleteItemDescricao(db,listaServicoTuristicos.get(i).getId());
			
			values.put("nome", listaServicoTuristicos.get(i).getNome());
			values.put("id", listaServicoTuristicos.get(i).getId());
			values.put("data_atualizacao", listaServicoTuristicos.get(i).getDataAtualizacao().getTime());
			values.put("id_versao", 0);
			values.put("municipio", listaServicoTuristicos.get(i).getMunicipio_id());
			
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

	private void salvaServico(SQLiteDatabase db,List<ServicoTuristico> listaServicoTuristico) {
		for(int i = 0; i < listaServicoTuristico.size(); i++){
			ContentValues values = new ContentValues();
			if(getServicoById(db,listaServicoTuristico.get(i).getId()))
				deleteServico(db,listaServicoTuristico.get(i).getId());
			values.put("tipo", listaServicoTuristico.get(i).getTipoServico());
			values.put("id", listaServicoTuristico.get(i).getId());
			
			db.insert("servico_turistico", null, values);
		}
		
	}

	private void salvarPontoLocalizavel(SQLiteDatabase db,	List<ServicoTuristico> listaServicoTuristico) {
		for(int i = 0; i < listaServicoTuristico.size(); i++){
			if(getPontoLocalizavelById(db,listaServicoTuristico.get(i).getId()))
				deletePontoLocalizavel(db,listaServicoTuristico.get(i).getId());
			ContentValues values = new ContentValues();
			values.put("id", listaServicoTuristico.get(i).getId());
			values.put("latitude", listaServicoTuristico.get(i).getLatitude());
			values.put("longitude", listaServicoTuristico.get(i).getLongitude());
			
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

	private void salvarTipoServico(SQLiteDatabase db, List<TipoServico> listaTipoServico) {
		for(int i = 0; i <listaTipoServico.size(); i++){
			if(!getTipoServicoById(db,listaTipoServico.get(i).getId())){
				ContentValues values = new ContentValues();
				values.put("servico", listaTipoServico.get(i).getServico());
				values.put("id", listaTipoServico.get(i).getId());
				values.put("pictograma", 0);
				
				db.insert("tipo_servico", null, values);
			}
		}
		
	}


	
	
	
}
