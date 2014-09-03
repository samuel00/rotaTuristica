package com.example.controller;

import java.util.List;

import android.support.v4.app.FragmentActivity;

import com.example.dao.MultimidiaDao;
import com.example.entidade.Multimidia;

public class MultimidiaController {

	public List<Multimidia> getMultimidiaByItemDescricao(FragmentActivity classe, long id) {
		MultimidiaDao dao = new MultimidiaDao();
		return dao.getMultimidiaByItemDescricao(classe, id);
	}

}
