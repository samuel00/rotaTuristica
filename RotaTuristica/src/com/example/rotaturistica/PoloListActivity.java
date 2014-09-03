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
import com.example.controller.PoloController;
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
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PoloListActivity extends ListActivity implements OnItemClickListener {
	
	private List<Map<String, Object>> polos;
	private Dialog dialog;
	private String item;
	private boolean download;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		item = intent.getStringExtra("item");
		download = intent.getBooleanExtra("download", false);
		ModeTrasmition mt = new ModeTrasmition();
		
		dialog = new Dialog("Aguarde","Carregando dados");
		dialog.iniciaDialog(this);
		
		if(mt.getModoTransmissao(this) == 1){
			if(isOnline()){
				new PoloTask().execute();
			}
		}else{
			if(montaView()){
				String[] from = {"icone", "polo", "idPolo"};
				int[] to = {R.id.tipoPolo, R.id.polo};
				SimpleAdapter adapter =	new SimpleAdapter(this, polos,R.layout.polos, from, to);
				setListAdapter(adapter);
				getListView().setOnItemClickListener(this);
				dialog.encerraDialog();
			}else{
				dialog.encerraDialog();
				Toast.makeText(getApplicationContext(), "Não há polos turísticos.", Toast.LENGTH_LONG).show();
			}
			
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

	private boolean montaView() {
		
		polos = new ArrayList<Map<String,Object>>();
		
		PoloController controller = new PoloController();
		
		List<PoloTuristico> listaDePoloTuristico = new ArrayList<PoloTuristico>();
		listaDePoloTuristico = controller.getPoloTuristico(this);
		if(listaDePoloTuristico != null){
			for(int i=0; i < listaDePoloTuristico.size(); i++){

				Map<String, Object> item = new HashMap<String, Object>();

				item.put("icone", R.drawable.polos);

				item.put("polo", listaDePoloTuristico.get(i).getNome());
				item.put("idPolo", listaDePoloTuristico.get(i).getId());
				
				polos.add(item);
			}
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		Map<String, Object> map = polos.get(position);
		long idPolo = (Long) map.get("idPolo");
		int icone = (Integer) map.get("icone");
		String polo = (String) map.get("polo");
		Intent i=new Intent(this, MunicipioListActivity.class);
		i.putExtra("idPolo",idPolo);
		i.putExtra("icone",icone);
		i.putExtra("polo", polo);
		i.putExtra("item", item);
		i.putExtra("download", download);
		startActivity(i);
	}
	
	private class PoloTask extends AsyncTask<String, Void, String[]>{

		@Override
		protected String[] doInBackground(String... params) {
			String poloJson = poloJson();
			try {
				JSONObject jsonObject = new JSONObject(poloJson);
				JSONObject jsonObjectTipo = new JSONObject(jsonObject.getString("polo"));
				JSONArray jsonArraylistaDeTipoAtracao = new JSONArray(jsonObjectTipo.getString("listaDePolo"));
				JSONObject jsonObject3 = jsonArraylistaDeTipoAtracao.getJSONObject(0);
				if(!jsonObject3.getString("tipo_polo").startsWith("[")){
					JSONObject objectServico = new JSONObject(jsonObject3.getString("tipo_polo"));

					String [] json = new String[1];

					String nome = objectServico.getString("nome");
					int id = Integer.parseInt(objectServico.getString("id"));
					json[0] = nome + "---" + id;

					return json;

				}else{
					JSONArray jsonReal = new JSONArray(jsonObject3.getString("tipo_polo"));
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

		private String poloJson() {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			EnderecoServidor es = new EnderecoServidor();
			HttpGet httpGet = new HttpGet(es.getEndereco()+"polos");
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
				String[] from = {"icone", "polo", "idPolo"};
				int[] to = {R.id.tipoPolo, R.id.polo};
				

				SimpleAdapter adapter =	new SimpleAdapter(PoloListActivity.this, montaView(result),R.layout.polos, from, to);
				setListAdapter(adapter);
				getListView().setOnItemClickListener(PoloListActivity.this);
				dialog.encerraDialog();
			}else{
				Toast.makeText(getApplicationContext(), "Não há polo turísticos.", Toast.LENGTH_LONG).show();
			}
		}

		private List<? extends Map<String, ?>> montaView(String[] result) {
			polos = new ArrayList<Map<String,Object>>();
			for(int i=0; i < result.length; i++){
				String[] parts = result[i].split("---");
				String nome = parts[0];
				long id = Long.parseLong(parts[1]);
				Map<String, Object> item = new HashMap<String, Object>();

				
				item.put("icone", R.drawable.polos);

				item.put("polo", nome);
				item.put("idPolo", id);
				polos.add(item);
			}
			return polos;
		}
		
	}
}
