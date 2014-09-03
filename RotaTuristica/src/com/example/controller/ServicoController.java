package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;

import com.example.dao.AtracaoTuristicaDao;
import com.example.dao.Conexao;
import com.example.dao.ServicoTuristicoDao;
import com.example.entidade.Descricao;
import com.example.entidade.Endereco;
import com.example.entidade.InfoDoResponsavel;
import com.example.entidade.ItemDescricao;
import com.example.entidade.Multimidia;
import com.example.entidade.PontoLocalizavel;
import com.example.entidade.ServicoTuristico;
import com.example.entidade.Telefone;
import com.example.entidade.TipoServico;
import com.example.rotaturistica.DownloadListItemActivity;
import com.example.rotaturistica.DownloadView;
import com.example.rotaturistica.MainActivity;
import com.example.rotaturistica.NearMap;
import com.example.rotaturistica.ServicoListActivity;
import com.example.rotaturistica.ServicoSelecionadoActivity;

public class ServicoController {

	

	public List<TipoServico> getTipoServico(ListActivity classe) {
		ServicoTuristicoDao dao = new ServicoTuristicoDao();
		List<TipoServico> listaDeServico = new ArrayList<TipoServico>();
		listaDeServico = dao.getTipoServico(classe);
				
		return listaDeServico;
	}

	public List<ServicoTuristico> getServicos(ServicoSelecionadoActivity servicoSelecionadoActivity,long idServico) {
		ServicoTuristicoDao dao = new ServicoTuristicoDao();
		List<ServicoTuristico> listaDeServico = new ArrayList<ServicoTuristico>();
		listaDeServico = dao.getServicos(servicoSelecionadoActivity,idServico);
				
		return listaDeServico;
	}

	public void salvaServico(DownloadListItemActivity downloadListItemActivity, String[] itemDescricao, String[] multimidia, String[] responsavel, String[] telefone) {
		ServicoTuristicoDao dao = new ServicoTuristicoDao();
		dao.salvaServico(downloadListItemActivity, itemDescricao, multimidia, responsavel, telefone);
	}
	
	public void salvaServico(FragmentActivity classe, String[] itemDescricao, String[] multimidia, String[] responsavel, String[] telefone) {
		ServicoTuristicoDao dao = new ServicoTuristicoDao();
		dao.salvaServico(classe, itemDescricao, multimidia, responsavel, telefone);
	}

	public List<PontoLocalizavel> getPontosLocalizaveis(NearMap nearMap,long idTipo) {
		ServicoTuristicoDao dao = new ServicoTuristicoDao();
		List<PontoLocalizavel> listaDePontoLocalizavel = new ArrayList<PontoLocalizavel>();
		listaDePontoLocalizavel = dao.getPontosLocalizaveis(nearMap, idTipo);
		
		return listaDePontoLocalizavel;
	}

	public long dataUltimaAtualizacao(FragmentActivity classe, long idMunicipio) {
		ServicoTuristicoDao dao = new ServicoTuristicoDao();
		return dao.dataUltimaAtualizacao(classe,idMunicipio);
	}

	public long contadorDeServico(FragmentActivity classe, long idMunicipio, long dataUltimaAtualizacao) {
		ServicoTuristicoDao dao = new ServicoTuristicoDao();
		return dao.contadorDeServico(classe,idMunicipio,dataUltimaAtualizacao);
	}

	public void salvaAtracao(FragmentActivity classe,List<ServicoTuristico> listaServicoTuristico,	List<TipoServico> listaTipoServico,
			List<InfoDoResponsavel> listaDoResponsavel,	List<Multimidia> listaMultimidia, List<Endereco> listaEnderecos,List<Telefone> listaTelefone, List<Descricao> listaDescricao,
			long idPolo, String polo, long idMunicipio, String municipio) {
		ServicoTuristicoDao dao = new ServicoTuristicoDao();
		dao.salvaAtracao(classe, listaServicoTuristico, listaTipoServico, listaDoResponsavel, 
				listaMultimidia, listaEnderecos, listaTelefone,listaDescricao,idPolo,polo,idMunicipio,municipio);
		
	} 

	
	
	



}
