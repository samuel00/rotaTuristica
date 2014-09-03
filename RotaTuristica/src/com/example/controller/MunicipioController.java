package com.example.controller;

import java.util.List;

import android.app.ListActivity;

import com.example.dao.MunicipioDao;
import com.example.entidade.Municipio;

public class MunicipioController {

	public List<Municipio> getMunicipioByPolo(ListActivity classe, long idPolo) {
		MunicipioDao dao = new MunicipioDao();
		return dao.getMunicipioByPolo(classe,idPolo);
	}

}
