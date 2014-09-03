package com.example.controller;

import java.util.List;

import android.app.ListActivity;

import com.example.dao.PoloDao;
import com.example.entidade.PoloTuristico;

public class PoloController {

	public List<PoloTuristico> getPoloTuristico(ListActivity classe) {
		PoloDao dao = new PoloDao();
		return dao.getPoloTuristico(classe);
	}

}
