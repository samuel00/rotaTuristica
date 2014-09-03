package com.example.rotaturistica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import android.os.Build;

public class ConfiguracaoActivity extends ListActivity implements OnItemClickListener {

	private List<Map<String, Object>> configuracoes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String[] from = {"configuracaoText", "configuracaoImagem", "action"};
		int[] to = {R.id.configuracaoText, R.id.configuracaoImage};

		SimpleAdapter adapter =	new SimpleAdapter(this, listarConfiguracao(),R.layout.configuracao, from, to);

		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);
	}

	private List<? extends Map<String, ?>> listarConfiguracao() {
		configuracoes = new ArrayList<Map<String, Object>>();

		Map<String, Object> item = new HashMap<String, Object>();

		String string = getString(R.string.modenet);
		item.put("configuracaoText", string);
		item.put("configuracaoImagem", R.drawable.net);
		item.put("action", "modeTrasmition");
		configuracoes.add(item);
		
		item = new HashMap<String, Object>();
		string = getString(R.string.download_data);
		item.put("configuracaoText", string);
		item.put("configuracaoImagem", R.drawable.download);
		item.put("action", "downloadData");
		configuracoes.add(item);

		return configuracoes;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		Map<String, Object> map = configuracoes.get(position);
		String action = (String) map.get("action");
		if(action.equalsIgnoreCase("modeTrasmition")){
			Intent i = new Intent(this, ModeTrasmitionActivity.class);
			startActivity(i);
		}else
			if(action.equalsIgnoreCase("downloadData")){
				Intent i = new Intent(this,DownloadActivity.class);
				startActivity(i);
			}

	}





}
