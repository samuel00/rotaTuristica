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
import com.example.controller.RotaController;
import com.example.entidade.TipoRota;
import com.example.utils.Dialog;
import com.example.utils.EnderecoServidor;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("NewApi")
public class RotaListActivity extends ListActivity implements OnItemClickListener{ 
	
	private List<Map<String, Object>> rotas;
	private Dialog dialog;
	private long idMunicipio;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		idMunicipio = intent.getLongExtra("idMunicipio",0);
		
		ModeTrasmition mt = new ModeTrasmition();
		
		dialog = new Dialog("Aguarde","Carregando dados");
		dialog.iniciaDialog(RotaListActivity.this);
		
		if(mt.getModoTransmissao(this) == 1){
			if(isOnline()){
				new TipoRotaTask().execute();
			}
		}
		else{
			if(montaView()){
				String[] from = {"icone", "rota", "id", "idTipoRota"};
				int[] to = {R.id.tipoRota, R.id.rota};
				SimpleAdapter adapter =	new SimpleAdapter(RotaListActivity.this, rotas,R.layout.lista_rota, from, to);
				setListAdapter(adapter);
				getListView().setOnItemClickListener(RotaListActivity.this);
				dialog.encerraDialog();
			}else
				dialog.encerraDialog();
				Toast.makeText(getApplicationContext(), "Não há tipo de rotas turísticas.", Toast.LENGTH_LONG).show();
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
		RotaController controller = new RotaController();
		List<TipoRota> listaDeTipoRota = new ArrayList<TipoRota>();
		
		listaDeTipoRota = controller.getTipoDeRota(this);
		
		rotas = new ArrayList<Map<String,Object>>();
		
		
		if(listaDeTipoRota != null){
			for(int i=0; i < listaDeTipoRota.size(); i++){
				
				Map<String, Object> item = new HashMap<String, Object>();

				
				item.put("icone", R.drawable.direction_uturn);

				item.put("rota", listaDeTipoRota.get(i).getNome());
				item.put("idTipoRota", listaDeTipoRota.get(i).getId());
				item.put("id", listaDeTipoRota.get(i).getId());
				rotas.add(item);
			}
			return true;
		}else{
			return false;
		}
		
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressLint("NewApi")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Map<String, Object> map = rotas.get(position);
		long idRota = (Long) map.get("id");
		long idTipoRota =  (Long) map.get("idTipoRota");
		int icone = (Integer) map.get("icone");
		String rota = (String) map.get("rota");
		Intent i=new Intent(this, RotaSelecionadoActivity.class);
		i.putExtra("idRota",idRota);
		i.putExtra("idTipo",idTipoRota);
		i.putExtra("icone",icone);
		i.putExtra("rota", rota);
		i.putExtra("idMunicipio", idMunicipio);
		ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight());
		startActivity(i, options.toBundle());

	}
	
	private class TipoRotaTask extends AsyncTask<String, Void, String[]>{

		@Override
		protected String[] doInBackground(String... params) {
			String rotaSelecionadaJson = rotaSelecionadaJson(); 
			try {
				JSONObject jsonObject = new JSONObject(rotaSelecionadaJson);
				JSONObject jsonObjectTipo = new JSONObject(jsonObject.getString("tipo"));
				JSONArray jsonArraylistaDeTipoAtracao = new JSONArray(jsonObjectTipo.getString("listaDeTipoRota"));
				JSONObject jsonObject3 = jsonArraylistaDeTipoAtracao.getJSONObject(0);
				if(!jsonObject3.getString("tipo_rota").startsWith("[")){
					JSONObject objectRota = new JSONObject(jsonObject3.getString("tipo_rota"));
					
					String [] json = new String[1];
					
					String nome = objectRota.getString("nome");
					String id = objectRota.getString("id");
					json[0] = nome + "---" + id;
					
					return json;
					
				}else{
					JSONArray jsonReal = new JSONArray(jsonObject3.getString("tipo_rota"));
					String [] json = new String[jsonReal.length()];
					for (int i = 0; i < jsonReal.length(); i++) {

						JSONObject jsonObject4 = jsonReal.getJSONObject(i);
						String nome = jsonObject4.getString("nome");
						String id = jsonObject4.getString("id");
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

		private String rotaSelecionadaJson() {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			EnderecoServidor es = new EnderecoServidor();
			HttpGet httpGet = new HttpGet(es.getEndereco()+"tipo_rotas");
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
		
		@Override
		protected void onPostExecute(String[] result) {
			if(result != null){
				String[] from = {"icone", "rota", "id", "idTipoRota"};
				int[] to = {R.id.tipoRota, R.id.rota};
				
				SimpleAdapter adapter =	new SimpleAdapter(RotaListActivity.this, montaView(result),R.layout.lista_rota, from, to);
				setListAdapter(adapter);
				getListView().setOnItemClickListener(RotaListActivity.this);
				dialog.encerraDialog();
			}else{
				Toast.makeText(getApplicationContext(), "Não há tipo de rotas turísticas.", Toast.LENGTH_LONG).show();
			}
			
		}

		private List<? extends Map<String, ?>> montaView(String[] result) {
			rotas = new ArrayList<Map<String,Object>>();
			for(int i=0; i < result.length; i++){
				String[] parts = result[i].split("---");
				String nome = parts[0];
				long id = Long.parseLong(parts[1]);
				Log.w("", "id" + id);
				Map<String, Object> item = new HashMap<String, Object>();

				
				item.put("icone", R.drawable.direction_uturn);

				item.put("rota", nome);
				item.put("idTipoRota", id);
				item.put("id", id);
				rotas.add(item);
			}
			return rotas;
		}
		
	}

}
