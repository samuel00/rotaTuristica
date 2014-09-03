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
import com.example.download.DownloadRota;
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

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
@SuppressLint("NewApi")
public class DownloadRouteActivity extends ListActivity implements OnItemClickListener {

	private List<Map<String, Object>> rotas;
	public static String tipoRotaJson;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ModeTrasmition mt = new ModeTrasmition();
		if(mt.getModoTransmissao(this) == 1){
			if(isOnline()){
				dialog = new Dialog("Aguarde","Carregando dados");
				dialog.iniciaDialog(DownloadRouteActivity.this);
				new TipoRotaTask().execute();
			}
		}
		else{
			Toast.makeText(getApplicationContext(), "Ative para o modo ON-LINE.", Toast.LENGTH_LONG).show();
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

	@SuppressLint("NewApi")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		Map<String, Object> map = rotas.get(position);
		long idTipoRota =  (Long) map.get("idTipoRota");
		String rota = (String) map.get("rota");
		String item = "route";
		Intent i=new Intent(this, DownloadListItemActivity.class);
		i.putExtra("idTipo",idTipoRota);
		i.putExtra("tipoItem",rota);
		i.putExtra("item",item);
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
				JSONArray jsonArraylistaDeTipoRota = new JSONArray(jsonObjectTipo.getString("listaDeTipoRota"));
				JSONObject jsonObject3 = jsonArraylistaDeTipoRota.getJSONObject(0);
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
				
				SimpleAdapter adapter =	new SimpleAdapter(DownloadRouteActivity.this, montaView(result),R.layout.lista_rota, from, to);
				setListAdapter(adapter);
				getListView().setOnItemClickListener(DownloadRouteActivity.this);
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
