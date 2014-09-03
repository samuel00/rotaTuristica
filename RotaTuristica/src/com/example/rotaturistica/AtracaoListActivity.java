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

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.example.controller.AtracaoController;
import com.example.controller.ModeTrasmition;
import com.example.dao.Conexao;
import com.example.entidade.TipoAtracao;
import com.example.utils.Dialog;
import com.example.utils.EnderecoServidor;
@SuppressLint("NewApi")
public class AtracaoListActivity extends ListActivity implements OnItemClickListener{

	private List<Map<String, Object>> atracoes ;
	public static String tipoAtracaoJson;
	private Dialog dialog;
	private long idMunicipio;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		idMunicipio = intent.getLongExtra("idMunicipio",0);
		
		ModeTrasmition mt = new ModeTrasmition();
		
		dialog = new Dialog("Aguarde","Carregando dados");
		dialog.iniciaDialog(AtracaoListActivity.this);
		
		if(mt.getModoTransmissao(this) == 1){
			if(isOnline()){
				new TipoAtracaoTask().execute();
			}
		}else{
			if(montaView()){
				String[] from = {"icone", "atracao", "id", "idTipoAtracao"};
				int[] to = {R.id.tipoAtracao, R.id.atracao};
				
				SimpleAdapter adapter =	new SimpleAdapter(AtracaoListActivity.this, atracoes,R.layout.lista_atracao, from, to);
				setListAdapter(adapter);
				getListView().setOnItemClickListener(AtracaoListActivity.this);
				dialog.encerraDialog();
			}
			else{
				dialog.encerraDialog();
				Toast.makeText(getApplicationContext(), "Não há tipo de atrações turísticas.", Toast.LENGTH_LONG).show();
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
		AtracaoController controller = new AtracaoController();

		List<TipoAtracao> listaDeTipoAtracao = new ArrayList<TipoAtracao>();

		listaDeTipoAtracao = controller.getTipoDeAtracao(this);

		atracoes = new ArrayList<Map<String,Object>>();
		
		if(listaDeTipoAtracao != null){
			for(int i=0; i < listaDeTipoAtracao.size(); i++){

				Map<String, Object> item = new HashMap<String, Object>();
				
				item.put("icone", R.drawable.theater);

				item.put("atracao", listaDeTipoAtracao.get(i).getAtracao());
				item.put("idTipoAtracao", listaDeTipoAtracao.get(i).getId());
				item.put("id", listaDeTipoAtracao.get(i).getId());
				atracoes.add(item);
			}
			return true;
		}else{
			return false;
		}
		
	}



	@SuppressLint("NewApi")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		Map<String, Object> map = atracoes.get(position);
		long idAtracao = (Long) map.get("id");
		long idTipoAtracao =  (Long) map.get("idTipoAtracao");
		int icone = (Integer) map.get("icone");
		Intent i=new Intent(this, AtracaoSelecionadoActivity.class);
		i.putExtra("idAtracao",idAtracao);
		i.putExtra("idTipo",idTipoAtracao);
		i.putExtra("icone",icone);
		i.putExtra("idMunicipio", idMunicipio);
		ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight());
		startActivity(i, options.toBundle());


	}

	private class TipoAtracaoTask extends AsyncTask<String, Void, String[]>{

		@Override
		protected String[] doInBackground(String... params) {
			String atracaoSelecionadaJson = atracaoSelecionadaJson(); 
			try {
				JSONObject jsonObject = new JSONObject(atracaoSelecionadaJson);
				JSONObject jsonObjectTipo = new JSONObject(jsonObject.getString("tipo"));
				JSONArray jsonArraylistaDeTipoAtracao = new JSONArray(jsonObjectTipo.getString("listaDeTipoAtracao"));
				JSONObject jsonObject3 = jsonArraylistaDeTipoAtracao.getJSONObject(0);
				if(!jsonObject3.getString("tipo_atracao").startsWith("[")){
					JSONObject objectAtracao = new JSONObject(jsonObject3.getString("tipo_atracao"));
					
					String [] json = new String[1];
					
					String nome = objectAtracao.getString("nome");
					int id = Integer.parseInt(objectAtracao.getString("id"));
					json[0] = nome + "---" + id;
					
					return json;
					
				}else{
					JSONArray jsonReal = new JSONArray(jsonObject3.getString("tipo_atracao"));
					String [] json = new String[jsonReal.length()];
					for (int i = 0; i < jsonReal.length(); i++) {

						JSONObject jsonObject4 = jsonReal.getJSONObject(i);
						String nome = jsonObject4.getString("nome");
						int id = Integer.parseInt(jsonObject4.getString("id"));
						json[i] = nome + "---" + id;

					}

					return json;
				}} catch (Exception e) {
					Log.w(JsonActivity.class.getName(), "Deu erro");
					e.printStackTrace();
				}

			return null;
		}
		

		@Override
		protected void onPostExecute(String[] result) {
			if(result != null){
				String[] from = {"icone", "atracao", "id", "idTipoAtracao"};
				int[] to = {R.id.tipoAtracao, R.id.atracao};
				
				SimpleAdapter adapter =	new SimpleAdapter(AtracaoListActivity.this, montaView(result),R.layout.lista_atracao, from, to);
				setListAdapter(adapter);
				getListView().setOnItemClickListener(AtracaoListActivity.this);
				dialog.encerraDialog();
			}else{
				Toast.makeText(getApplicationContext(), "Não há tipo de atrações turísticas.", Toast.LENGTH_LONG).show();
			}
		}


		private List<? extends Map<String, ?>> montaView(String[] result) {
			atracoes = new ArrayList<Map<String,Object>>();
			for(int i=0; i < result.length; i++){
				String[] parts = result[i].split("---");
				String nome = parts[0];
				long id = Long.parseLong(parts[1]);
				Map<String, Object> item = new HashMap<String, Object>();

				
				item.put("icone", R.drawable.theater);

				item.put("atracao", nome);
				item.put("idTipoAtracao", id);
				item.put("id", id);
				atracoes.add(item);
			}
			return atracoes;
		}

		
		private String atracaoSelecionadaJson() {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			EnderecoServidor es = new EnderecoServidor();
			HttpGet httpGet = new HttpGet(es.getEndereco()+"tipo_atracoes/"+idMunicipio);
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
	}
}



