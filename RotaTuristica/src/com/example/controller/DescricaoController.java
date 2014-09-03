package com.example.controller;

import android.app.ListActivity;

import com.example.dao.DescricaoDao;

public class DescricaoController {

	public String getDescricaoByItemDescricao(ListActivity classe, Long id) {
		DescricaoDao dao = new DescricaoDao();
		return dao.getDescricaoByItemDescricao(classe, id);
	}

}
