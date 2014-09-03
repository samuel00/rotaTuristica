package com.example.rotaturistica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Build;

public class DownloadActivity extends ListActivity implements OnItemClickListener {

	private ProgressDialog dialog;
	private List<Map<String, Object>> configuracoes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String[] from = {"itemText", "itemImagem", "item"};
		int[] to = {R.id.itemText, R.id.itemImage};

		SimpleAdapter adapter =	new SimpleAdapter(this, listarConfiguracao(),R.layout.download, from, to);

		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);

	}

	private List<? extends Map<String, ?>> listarConfiguracao() {
		configuracoes = new ArrayList<Map<String, Object>>();

		Map<String, Object> item = new HashMap<String, Object>();

		String string = getString(R.string.service);
		item.put("itemText", string);
		item.put("itemImagem", R.drawable.service);
		item.put("item", "service");
		configuracoes.add(item);

		item = new HashMap<String, Object>();
		string = getString(R.string.attractive);
		item.put("itemText", string);
		item.put("itemImagem", R.drawable.theater);
		item.put("item", "attractive");
		configuracoes.add(item);
		
		item = new HashMap<String, Object>();
		string = getString(R.string.route);
		item.put("itemText", string);
		item.put("itemImagem", R.drawable.direction_uturn);
		item.put("item", "route");
		configuracoes.add(item);

		return configuracoes;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		Map<String, Object> map = configuracoes.get(position);
		String item = (String) map.get("item");
		if(item.equalsIgnoreCase("attractive")){
			Intent intent = new Intent(this, PoloListActivity.class);
			intent.putExtra("item","attractive");
			intent.putExtra("download",true);
			startActivity(intent);
		}else
			if(item.equalsIgnoreCase("service")){
				Intent intent = new Intent(this, PoloListActivity.class);
				intent.putExtra("item","service");
				intent.putExtra("download",true);
				startActivity(intent);
			}else
				if(item.equalsIgnoreCase("route")){
					Intent intent = new Intent(this, DownloadRouteActivity.class);
					startActivity(intent);
				}
	}

}
