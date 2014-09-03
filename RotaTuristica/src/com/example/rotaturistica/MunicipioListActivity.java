package com.example.rotaturistica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.controller.ModeTrasmition;
import com.example.controller.MunicipioController;
import com.example.controller.PoloController;
import com.example.entidade.Municipio;
import com.example.entidade.PoloTuristico;
import com.example.utils.Dialog;
import com.example.utils.EnderecoServidor;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MunicipioListActivity extends ListActivity implements OnItemClickListener{
	
	private Dialog dialog;
	private String item;
	private List<Map<String, Object>> municipios;
	private long idPolo;
	private String polo;
	private boolean download;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		idPolo = intent.getLongExtra("idPolo",0);
		polo = intent.getStringExtra("polo");
		item = intent.getStringExtra("item");
		download = intent.getBooleanExtra("download", false);
		
		ModeTrasmition mt = new ModeTrasmition();
		dialog = new Dialog("Aguarde","Carregando dados");
		dialog.iniciaDialog(this);
		if(mt.getModoTransmissao(this) == 1){
			if(isOnline()){
				new MunicipioTask().execute();
			}
			}else{
				if(montaView()){
					String[] from = {"icone", "municipio", "idMunicipio"};
					int[] to = {R.id.tipoMunicipio, R.id.municipio};
					SimpleAdapter adapter =	new SimpleAdapter(this, municipios,R.layout.municipios, from, to);
					setListAdapter(adapter);
					getListView().setOnItemClickListener(this);
					dialog.encerraDialog();
				}else{
					dialog.encerraDialog();
					Toast.makeText(getApplicationContext(), "Não há municípios.", Toast.LENGTH_LONG).show();
				}
			}
	}
	
	private boolean montaView() {
		municipios = new ArrayList<Map<String,Object>>();

		MunicipioController controller = new MunicipioController();

		List<Municipio> listaDeMunicipio = new ArrayList<Municipio>();
		listaDeMunicipio = controller.getMunicipioByPolo(this,idPolo);
		if(listaDeMunicipio != null){
			for(int i=0; i < listaDeMunicipio.size(); i++){

				Map<String, Object> item = new HashMap<String, Object>();

				item.put("icone", R.drawable.city);

				item.put("municipio", listaDeMunicipio.get(i).getNome());
				item.put("idMunicipio", listaDeMunicipio.get(i).getId());

				municipios.add(item);
			}
			return true;
		}else{
			return false;
		}
	}

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();
		    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
		        return true;
		    }
		    Toast.makeText(getApplicationContext(), "Não há conexao com à internet", Toast.LENGTH_LONG).show();
		    return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		if(!download){
			if(item.equalsIgnoreCase("servico")){
				Map<String, Object> map = municipios.get(position);
				long idMunicipio = (Long) map.get("idMunicipio");
				String municipio = (String) map.get("municipio");
				Intent i=new Intent(this, ServicoListActivity.class);
				i.putExtra("idMunicipio", idMunicipio);
				i.putExtra("municipio", municipio);
				startActivity(i);
			}else
				if(item.equalsIgnoreCase("atracao")){
					Map<String, Object> map = municipios.get(position);
					long idMunicipio = (Long) map.get("idMunicipio");
					String municipio = (String) map.get("municipio");
					Intent i=new Intent(this, AtracaoListActivity.class);
					i.putExtra("idMunicipio", idMunicipio);
					i.putExtra("municipio", municipio);
					startActivity(i);
				}else
					if(item.equalsIgnoreCase("rota")){
						Map<String, Object> map = municipios.get(position);
						long idMunicipio = (Long) map.get("idMunicipio");
						String municipio = (String) map.get("municipio");
						Intent i=new Intent(this, RotaListActivity.class);
						i.putExtra("idMunicipio", idMunicipio);
						i.putExtra("municipio", municipio);
						startActivity(i);
					}
		}else{
			if(item.equalsIgnoreCase("service")){
				Map<String, Object> map = municipios.get(position);
				long idMunicipio = (Long) map.get("idMunicipio");
				String municipio = (String) map.get("municipio");
				Intent i=new Intent(this, DownloadView.class);
				i.putExtra("idPolo", idPolo);
				i.putExtra("polo", polo);
				i.putExtra("idMunicipio", idMunicipio);
				i.putExtra("municipio", municipio);
				i.putExtra("item", "service");
				startActivity(i);
			}else
				if(item.equalsIgnoreCase("attractive")){
					Map<String, Object> map = municipios.get(position);
					long idMunicipio = (Long) map.get("idMunicipio");
					String municipio = (String) map.get("municipio");
					Intent i=new Intent(this, DownloadView.class);
					i.putExtra("idPolo", idPolo);
					i.putExtra("polo", polo);
					i.putExtra("idMunicipio", idMunicipio);
					i.putExtra("municipio", municipio);
					i.putExtra("item", "attractive");
					startActivity(i);
				}else
					if(item.equalsIgnoreCase("rota")){
						
					}
		}
	}
	
	private class MunicipioTask extends AsyncTask<String, Void, String[]>{

		@Override
		protected String[] doInBackground(String... params) {
			String municipioJson = municipioJson(idPolo);
			try {
				JSONObject jsonObject = new JSONObject(municipioJson);
				JSONObject jsonObjectTipo = new JSONObject(jsonObject.getString("municipio"));
				JSONArray jsonArraylistaDeTipoAtracao = new JSONArray(jsonObjectTipo.getString("listaDeMunicipio"));
				JSONObject jsonObject3 = jsonArraylistaDeTipoAtracao.getJSONObject(0);
				if(!jsonObject3.getString("municipio").startsWith("[")){
					JSONObject objectServico = new JSONObject(jsonObject3.getString("municipio"));

					String [] json = new String[1];

					String nome = objectServico.getString("nome");
					int id = Integer.parseInt(objectServico.getString("id"));
					json[0] = nome + "---" + id;

					return json;

				}else{
					JSONArray jsonReal = new JSONArray(jsonObject3.getString("municipio"));
					String [] json = new String[jsonReal.length()];
					for (int i = 0; i < jsonReal.length(); i++) {

						JSONObject jsonObject4 = jsonReal.getJSONObject(i);
						String nome = jsonObject4.getString("nome");
						int id = Integer.parseInt(jsonObject4.getString("id"));
						json[i] = nome + "---" + id;

					}
					return json;
				}
			} catch (Exception e) {
				Log.w(JsonActivity.class.getName(), "Deu erro");
				e.printStackTrace();
			}
			return null;
		}

		private String municipioJson(long idPolo) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			EnderecoServidor es = new EnderecoServidor();
			HttpGet httpGet = new HttpGet(es.getEndereco()+"municipios/"+idPolo);
			try {
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
				} else {
					Log.e(JsonActivity.class.toString(), "Failed to download file");
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return builder.toString();
		}
		
		protected void onPostExecute(String[] result) {
			if(result != null){
				String[] from = {"icone", "municipio", "idMunicipio"};
				int[] to = {R.id.tipoMunicipio, R.id.municipio};
				

				SimpleAdapter adapter =	new SimpleAdapter(MunicipioListActivity.this, montaView(result),R.layout.municipios, from, to);
				setListAdapter(adapter);
				getListView().setOnItemClickListener(MunicipioListActivity.this);
				dialog.encerraDialog();
			}else{
				Toast.makeText(getApplicationContext(), "Não há municípios.", Toast.LENGTH_LONG).show();
			}
		}

		private List<? extends Map<String, ?>> montaView(String[] result) {
			municipios = new ArrayList<Map<String,Object>>();
			for(int i=0; i < result.length; i++){
				String[] parts = result[i].split("---");
				String nome = parts[0];
				long id = Long.parseLong(parts[1]);
				Map<String, Object> item = new HashMap<String, Object>();

				
				item.put("icone", R.drawable.city);

				item.put("municipio", nome);
				item.put("idMunicipio", id);
				municipios.add(item);
			}
			return municipios;
		}
		
	}


}
