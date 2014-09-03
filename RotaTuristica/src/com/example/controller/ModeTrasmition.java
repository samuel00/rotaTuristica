package com.example.controller;

import android.app.ListActivity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.example.dao.ModeTrasmitionDao;
import com.example.rotaturistica.AtracaoListActivity;
import com.example.rotaturistica.ModeTrasmitionActivity;

public class ModeTrasmition {

	public int alteraModoDeTrasmissaoOff(ModeTrasmitionActivity modeTrasmitionActivity) {
		ModeTrasmitionDao dao = new ModeTrasmitionDao();
		return dao.alteraModoDeTrasmissaoOff(modeTrasmitionActivity);
	}

	public int alteraModoDeTrasmissaoOn(ModeTrasmitionActivity modeTrasmitionActivity) {
		ModeTrasmitionDao dao = new ModeTrasmitionDao();
		return dao.alteraModoDeTrasmissaoOn(modeTrasmitionActivity);
	}

	public int getModoTransmissao(Context classe) {
		ModeTrasmitionDao dao = new ModeTrasmitionDao();
		return dao.getModoTransmissao(classe);
	}

//	public int getModoTransmissao(Context classe) {
//		ModeTrasmitionDao dao = new ModeTrasmitionDao();
//		return dao.getModoTransmissao(classe);
//	}
//
//	public int getModoTransmissao(Context mContext) {
//		// TODO Auto-generated method stub
//		return 0;
//	}

}
