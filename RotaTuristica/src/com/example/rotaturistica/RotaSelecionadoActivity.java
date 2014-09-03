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

import com.example.controller.AtracaoController;
import com.example.controller.DescricaoController;
import com.example.controller.ModeTrasmition;
import com.example.controller.RotaController;
import com.example.entidade.AtracaoTuristica;
import com.example.entidade.RotaTuristica;
import com.example.utils.Dialog;
import com.example.utils.EnderecoServidor;

import android.annotation.SuppressLint;
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

public class RotaSelecionadoActivity  extends ListActivity implements OnItemClickListener{

	private List<Map<String, Object>> rotas ;
	private long idTipo;
	private long idMunicipio;
	String [] itemDescricao;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		idTipo = intent.getLongExtra("idTipo",0);
		idMunicipio = intent.getLongExtra("idMunicipio",0);
		
		ModeTrasmition mt = new ModeTrasmition();
		
		dialog = new Dialog("Aguarde","Carregando dados");
		dialog.iniciaDialog(RotaSelecionadoActivity.this);
		
		if(mt.getModoTransmissao(this) == 1){
			if(isOnline()){
				new RotaSelecionadaTask().execute();
			}
		}
		else{
			if(montaView()){
				String[] from = {"rotaNome", "rotaImagem", "descricao", "id", "idTipoRota", "icone", "multimidia"};
				int[] to = {R.id.rota, R.id.rotaImage};
				SimpleAdapter adapter =	new SimpleAdapter(RotaSelecionadoActivity.this, rotas,R.layout.lista_rota_selecionado, from, to);
				setListAdapter(adapter);
				mudaTituloDaTela();
				getListView().setOnItemClickListener(RotaSelecionadoActivity.this);
				dialog.encerraDialog();
		}else{
			dialog.encerraDialog();
			Toast.makeText(getApplicationContext(), "Não há rotas turísticas.", Toast.LENGTH_LONG).show();
		}
		}
	}
	
	@SuppressLint("NewApi")
	private void mudaTituloDaTela() {
		Intent getIntent = getIntent();
		String rota = getIntent.getStringExtra("rota");
		getActionBar().setTitle("Lista de rota - " + rota);
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
		List<RotaTuristica> listaDeRotaTuristica = new ArrayList<RotaTuristica>();
		
		listaDeRotaTuristica = controller.getRotas(this, this.idTipo);
		
		
		rotas = new ArrayList<Map<String,Object>>();
		
		if(listaDeRotaTuristica != null){
			for(int i = 0; i < listaDeRotaTuristica.size(); i++){

				Map<String, Object> item = new HashMap<String, Object>();

				item.put("rotaNome", listaDeRotaTuristica.get(i).getNome());
				item.put("id", listaDeRotaTuristica.get(i).getId());
				item.put("descricao", getDescricaoByItemDescricao(listaDeRotaTuristica.get(i).getId()));
				item.put("idTipoRota", idTipo);
				item.put("icone", R.drawable.direction_uturn);
				item.put("rotaImagem", R.drawable.direction_uturn);
				rotas.add(item);
			}
			return true;
		}else{
			return false;
		}
		
	}

	private String getDescricaoByItemDescricao(Long id) {
		DescricaoController controller = new DescricaoController();
		return controller.getDescricaoByItemDescricao(this,id);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Map<String, Object> map = rotas.get(position);
		long idRota = (Long) map.get("id");
		long idTipoRota = (Long) map.get("idTipoRota");
		String rotaNome = (String) map.get("rotaNome");
		String descricao = (String) map.get("descricao");
		int icone = (Integer) map.get("icone");
		Intent i=new Intent(this, DetalhesActivity.class);
		i.putExtra("idItem",idRota);
		i.putExtra("idTipo", idTipoRota);
		i.putExtra("nome", rotaNome);
		i.putExtra("descricao", descricao);
		i.putExtra("icone", icone);
		i.putExtra("item", "route");
		startActivity(i);

	}

	private class RotaSelecionadaTask extends AsyncTask<String, Void, String[]>{

		@Override
		protected String[] doInBackground(String... params) {
			String rotaJson = rotaJson(idTipo);
			try {
				if(rotaJson != null){
					JSONObject jsonObject = new JSONObject(rotaJson);
					JSONObject jsonObjectTipo = new JSONObject(jsonObject.getString("rota"));
					JSONArray jsonArraylistaDeTipoRota = new JSONArray(jsonObjectTipo.getString("listaDeRotaTuristica"));
					JSONObject jsonObject3 = jsonArraylistaDeTipoRota.getJSONObject(0);
					if(!jsonObject3.getString("rota_turistica").startsWith("[")){
						JSONObject objectRota = new JSONObject(jsonObject3.getString("rota_turistica"));
						itemDescricao = new String[1];

						String nome = objectRota.getString("nome");
						String id = objectRota.getString("id");
						String dataAtualizacao = objectRota.getString("dataAtualizacao");
						String tipoRota = objectRota.getString("tipoRota");
						String municipioId = objectRota.getString("municipio_id");

						JSONArray jsonArrayDescricao = new JSONArray(objectRota.getString("descricao"));
						JSONObject objectDescricao = jsonArrayDescricao.getJSONObject(0);
						jsonArrayDescricao = new JSONArray(objectDescricao.getString("descricao"));
						objectDescricao = jsonArrayDescricao.getJSONObject(0);
						String descricaoId = objectDescricao.getString("id");
						String idiomaId = objectDescricao.getString("idioma_id");
						jsonArrayDescricao = new JSONArray(objectDescricao.getString("descricao"));
						String descricao = (String) jsonArrayDescricao.get(0);

						itemDescricao[0] = nome + "---" + id + "---" + dataAtualizacao + "---" + tipoRota + "---" + descricaoId + "---" + descricao + "---" + idiomaId;

					}else{
						JSONArray jsonReal = new JSONArray(jsonObject3.getString("rota_turistica"));
						itemDescricao = new String[jsonReal.length()];
						for (int i = 0; i < jsonReal.length(); i++) {

							JSONObject jsonObject4 = jsonReal.getJSONObject(i);
							String nome = jsonObject4.getString("nome");
							String id = jsonObject4.getString("id");
							String dataAtualizacao = jsonObject4.getString("dataAtualizacao");
							String tipoRota = jsonObject4.getString("tipoRota");
							String municipioId = jsonObject4.getString("municipio_id");

							JSONArray jsonArrayDescricao = new JSONArray(jsonObject4.getString("descricao"));
							JSONObject objectDescricao = jsonArrayDescricao.getJSONObject(0);
							jsonArrayDescricao = new JSONArray(objectDescricao.getString("descricao"));
							objectDescricao = jsonArrayDescricao.getJSONObject(0);
							String descricaoId = objectDescricao.getString("id");
							String idiomaId = objectDescricao.getString("idioma_id");
							jsonArrayDescricao = new JSONArray(objectDescricao.getString("descricao"));
							String descricao = (String) jsonArrayDescricao.get(0);


							itemDescricao[i] = nome + "---" + id + "---" + dataAtualizacao + "---" + tipoRota  + "---" + descricaoId + "---" + descricao + "---" + idiomaId;
						}
					}

					return itemDescricao;
				} 
			} catch (Exception e) {
				Log.w(JsonActivity.class.getName(), "Deu erro");
				e.printStackTrace();
			}
			return null;
		}

		private String rotaJson(long id) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			EnderecoServidor es = new EnderecoServidor();
			HttpGet httpGet = new HttpGet(es.getEndereco()+"rotas/"+id+"/"+0);
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
				String[] from = {"rotaNome", "rotaImagem", "descricao", "id", "idTipoRota", "icone", "multimidia"};
				int[] to = {R.id.rota, R.id.rotaImage};


				SimpleAdapter adapter =	new SimpleAdapter(RotaSelecionadoActivity.this, listarRotaSelecionada(result),R.layout.lista_rota_selecionado, from, to);
				setListAdapter(adapter);
				mudaTituloDaTela();	
				getListView().setOnItemClickListener(RotaSelecionadoActivity.this);
				dialog.encerraDialog();
			}else{
				dialog.encerraDialog();
				Toast.makeText(getApplicationContext(), "Não há rotas turísticas.", Toast.LENGTH_LONG).show();
			}
			
		}

		private List<? extends Map<String, ?>> listarRotaSelecionada(	String[] result) {
			
			rotas = new ArrayList<Map<String,Object>>();

			for(int i = 0; i < result.length; i++){

				String[] split = result[i].split("---");
				String nome = split[0];
				long id = Long.parseLong(split[1]);
				String descricao = split[5];

				Map<String, Object> item = new HashMap<String, Object>();

				item.put("rotaNome", nome);
				item.put("id", id);
				item.put("descricao", descricao);
				item.put("idTipoRota", id);
				item.put("icone", R.drawable.direction_uturn);
				item.put("rotaImagem", R.drawable.direction_uturn);

				rotas.add(item);
			}
			return rotas;
		}
	}

}

