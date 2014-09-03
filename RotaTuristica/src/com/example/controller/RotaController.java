package com.example.controller;

import java.util.List;

import android.app.ListActivity;
import android.support.v4.app.FragmentActivity;

import com.example.dao.RotaTuristicaDao;
import com.example.entidade.AtracaoTuristica;
import com.example.entidade.Descricao;
import com.example.entidade.Multimidia;
import com.example.entidade.OrdemPontoRota;
import com.example.entidade.PontoLocalizavel;
import com.example.entidade.RotaTuristica;
import com.example.entidade.TipoRota;
import com.example.rotaturistica.DownloadListItemActivity;

public class RotaController {

	public void salvaAtracao(DownloadListItemActivity downloadListItemActivity,String[] itemDescricao, String[] multimidia, String[] pontoRota) {
		RotaTuristicaDao dao = new RotaTuristicaDao();
		dao.salvaRota(downloadListItemActivity,itemDescricao,multimidia,pontoRota);
	}

	public List<TipoRota> getTipoDeRota(ListActivity classe) {
		RotaTuristicaDao dao = new RotaTuristicaDao();
		return dao.getTipoDeRota(classe);
	}

	public List<RotaTuristica> getRotas(ListActivity classe, long id) {
		RotaTuristicaDao dao = new RotaTuristicaDao();
		return dao.getDeRotas(classe,id);
	}

	public List<PontoLocalizavel> getRota(FragmentActivity classe, long idTipo) {
		RotaTuristicaDao dao = new RotaTuristicaDao();
		return dao.getRota(classe,idTipo);
	}

	public void salvaAtracao(DownloadListItemActivity downloadListItemActivity,List<TipoRota> listaTipoRota,List<RotaTuristica> listaRotaTuristica,
			List<Multimidia> listaMultimidia,List<OrdemPontoRota> listaOrdemPontoRota,List<AtracaoTuristica> listaAtracaoTuristica, List<Descricao> listaDescricao) {
		RotaTuristicaDao dao = new RotaTuristicaDao();
		dao.salvaRota(downloadListItemActivity, listaTipoRota, listaRotaTuristica, listaMultimidia, listaAtracaoTuristica, listaDescricao, listaOrdemPontoRota);
		
	}

}
