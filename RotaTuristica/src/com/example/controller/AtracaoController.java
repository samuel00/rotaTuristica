package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.support.v4.app.FragmentActivity;

import com.example.dao.AtracaoTuristicaDao;
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
import com.example.rotaturistica.DownloadListItemActivity;
import com.example.rotaturistica.NearMap;

public class AtracaoController {
	
	
	public List<TipoAtracao> getTipoDeAtracao(AtracaoListActivity atracaoListActivity){
		AtracaoTuristicaDao dao = new AtracaoTuristicaDao();
		List<TipoAtracao> listaDeAtracao = new ArrayList<TipoAtracao>();
		listaDeAtracao = dao.getTipoDeAtracao(atracaoListActivity);
		
		return listaDeAtracao;
	}

	public List<AtracaoTuristica> getAtracoes(AtracaoSelecionadoActivity atracaoSelecionadoActivity, 	long idAtracao) {
		AtracaoTuristicaDao dao = new AtracaoTuristicaDao();
		List<AtracaoTuristica> listaDeAtracao = new ArrayList<AtracaoTuristica>();
		listaDeAtracao = dao.getAtracoes(atracaoSelecionadoActivity, idAtracao);
		
		
		return listaDeAtracao;
	}
	

	public List<PontoLocalizavel> getAtracoesNear(NearMap nearMap, long idTipo, Location location) {
		AtracaoTuristicaDao dao = new AtracaoTuristicaDao();
		List<PontoLocalizavel> listaDePontoLocalizavel = new ArrayList<PontoLocalizavel>();
		listaDePontoLocalizavel = dao.getAtracoesNear(nearMap, idTipo, location);
		
		return listaDePontoLocalizavel;
	}

	public List<PontoLocalizavel> getAtracoesNear(NearMap nearMap, long idTipo, double d, double e) {
		AtracaoTuristicaDao dao = new AtracaoTuristicaDao();
		List<PontoLocalizavel> listaDePontoLocalizavel = new ArrayList<PontoLocalizavel>();
		listaDePontoLocalizavel = dao.getAtracoesNear(nearMap, idTipo, d, e);
		
		return listaDePontoLocalizavel;
	}

	public List<PontoLocalizavel> getPontosLocalizaveis(NearMap nearMap, long idTipo) {
		AtracaoTuristicaDao dao = new AtracaoTuristicaDao();
		List<PontoLocalizavel> listaDePontoLocalizavel = new ArrayList<PontoLocalizavel>();
		listaDePontoLocalizavel = dao.getPontosLocalizaveis(nearMap, idTipo);
		
		return listaDePontoLocalizavel;
	}

	public void salvaAtracao(DownloadListItemActivity downloadListItemActivity, String[] itemDescricao, String[] multimidia, String[] responsavel, String[] telefone) {
		AtracaoTuristicaDao dao = new AtracaoTuristicaDao();
		dao.salvaAtracao(downloadListItemActivity, itemDescricao, multimidia, responsavel, telefone);
		
	}
	
	public void salvaAtracao(FragmentActivity classe, String[] itemDescricao, String[] multimidia, String[] responsavel, String[] telefone) {
		AtracaoTuristicaDao dao = new AtracaoTuristicaDao();
		dao.salvaAtracao(classe, itemDescricao, multimidia, responsavel, telefone);
		
	}

	public long dataUltimaAtualizacao(FragmentActivity classe,long idMunicipio) {
		AtracaoTuristicaDao dao = new AtracaoTuristicaDao();
		return dao.dataUltimaAtualizacao(classe,idMunicipio);
	}

	public long contadorDeAtracao(FragmentActivity classe, long idMunicipio, 	long dataUltimaAtualizacao) {
		AtracaoTuristicaDao dao = new AtracaoTuristicaDao();
		return dao.contadorDeAtracao(classe,idMunicipio,dataUltimaAtualizacao);
	}

	public void salvaAtracao(FragmentActivity classe,List<AtracaoTuristica> listaAtracaoTuristicas,List<TipoAtracao> listaDeAtracao,
			List<InfoDoResponsavel> listaDoResponsavel,	List<Multimidia> listaMultimidia, List<Endereco> listaEnderecos,List<Telefone> listaTelefone, List<Descricao> listaDescricao,
			long idPolo, String polo, long idMunicipio, String municipio) {
		AtracaoTuristicaDao dao = new AtracaoTuristicaDao();
		dao.salvaAtracao(classe, listaAtracaoTuristicas, listaDeAtracao, listaDoResponsavel, 
				listaMultimidia, listaEnderecos, listaTelefone,listaDescricao,idPolo,polo,idMunicipio,municipio);
	}

	
}
