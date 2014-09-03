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
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.controller.AtracaoController;
import com.example.controller.DescricaoController;
import com.example.controller.ModeTrasmition;
import com.example.entidade.AtracaoTuristica;
import com.example.entidade.Descricao;
import com.example.utils.Dialog;
import com.example.utils.EnderecoServidor;

@SuppressLint("NewApi")
public class AtracaoSelecionadoActivity extends ListActivity implements OnItemClickListener{

	private List<Map<String, Object>> atracoes ;
	private long id;
	private long idMunicipio;
	String [] itemDescricao;
	private Dialog dialog;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		id = intent.getLongExtra("idTipo",0);
		idMunicipio = intent.getLongExtra("idMunicipio",0);
		
		ModeTrasmition mt = new ModeTrasmition();
		
		dialog = new Dialog("Aguarde","Carregando dados");
		dialog.iniciaDialog(AtracaoSelecionadoActivity.this);
		
		if(mt.getModoTransmissao(this) == 1){
			if(isOnline()){
				new AtracaoSelecionadaTask().execute();
			}
		}else{
			if(montaView()){
					String[] from = {"atracaoNome", "atracaoImagem", "descricao", "id", "latitude", "longitude", "idTipoAtracao", "icone", "endereco"};
					int[] to = {R.id.atracao, R.id.atracaoImage};
					SimpleAdapter adapter =	new SimpleAdapter(AtracaoSelecionadoActivity.this, atracoes,R.layout.lista_atracao_selecionado, from, to);
					mudaTituloDaTela();
					setListAdapter(adapter);
					getListView().setOnItemClickListener(AtracaoSelecionadoActivity.this);
					dialog.encerraDialog();
			}else
				dialog.encerraDialog();
				Toast.makeText(getApplicationContext(), "Não há atrações turísticas.", Toast.LENGTH_LONG).show();
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
		Intent intent = getIntent();
		long idAtracao = intent.getLongExtra("idAtracao",0);
		int icone = intent.getIntExtra("icone", 0);

		atracoes = new ArrayList<Map<String,Object>>();
		AtracaoController atracao = new AtracaoController();
		List<AtracaoTuristica> listaDeAtracao = new ArrayList<AtracaoTuristica>();
		
		listaDeAtracao = atracao.getAtracoes(this, idAtracao);
		
		if(listaDeAtracao != null){
			for(int i = 0; i < listaDeAtracao.size(); i++){

				Map<String, Object> item = new HashMap<String, Object>();

				item.put("atracaoNome", listaDeAtracao.get(i).getNome());
				item.put("id", listaDeAtracao.get(i).getId());
				item.put("latitude", listaDeAtracao.get(i).getLatitude());
				item.put("longitude", listaDeAtracao.get(i).getLongitude());
				item.put("descricao", getDescricaoByItemDescricao(listaDeAtracao.get(i).getId()));
				item.put("idTipoAtracao", listaDeAtracao.get(i).getId());
				item.put("icone", icone);
				item.put("atracaoImagem", icone);
				
				String endereco = listaDeAtracao.get(i).getEndereco().getLogradouro() + ", " + listaDeAtracao.get(i).getEndereco().getNumero() + ", "
						+ listaDeAtracao.get(i).getEndereco().getBairro() + ". " + listaDeAtracao.get(i).getEndereco().getCep();
				
				item.put("endereco", endereco);
				
				atracoes.add(item);
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

	@SuppressLint("NewApi")
	private void mudaTituloDaTela() {
		Intent getIntent = getIntent();
		String atracao = getIntent.getStringExtra("atracao");
		getActionBar().setTitle("Lista de atrativo - " + atracao);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.atracao_proxima, menu);
		return true;
	} 

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.near:
			Intent getIntent = getIntent();
			long idTipo = getIntent.getLongExtra("idTipo",0);
			int icone = getIntent.getIntExtra("icone", 0);
			Intent intent= new Intent(this, NearMap.class);
			intent.putExtra("idTipo", idTipo);
			intent.putExtra("item", "atracao");
			intent.putExtra("icone", icone);
			intent.putExtra("idMunicipio", idMunicipio);
			startActivity(intent);
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}


	private List<Map<String, Object>> listarAtracaoSelecionada(String[] result) {

		Intent intent = getIntent();
		long idAtracao = intent.getLongExtra("idAtracao",0);
		int icone = intent.getIntExtra("icone", 0);

		atracoes = new ArrayList<Map<String,Object>>();
		AtracaoController atracao = new AtracaoController();
		List<AtracaoTuristica> listaDeAtracao = new ArrayList<AtracaoTuristica>();


//		listaDeAtracao = atracao.getAtracoes(this, idAtracao);

		for(int i = 0; i < result.length; i++){

			String[] split = result[i].split("---");
			String nome = split[0];
			long id = Long.parseLong(split[1]);
			double latitude = Double.parseDouble(split[5]);
			double longitude = Double.parseDouble(split[6]);
			String descricao = split[13];
			String endereco = split[7] + ", " + split[8] + ", " + split[9] + ". " + split[10];
			
			Map<String, Object> item = new HashMap<String, Object>();

			item.put("atracaoNome", nome);
			item.put("id", id);
			item.put("latitude", latitude);
			item.put("longitude", longitude);
			item.put("descricao", descricao);
			item.put("idTipoAtracao", this.id);
			item.put("icone", icone);
			item.put("atracaoImagem", icone);
			item.put("endereco", endereco);

			atracoes.add(item);
		}
		return atracoes;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		Map<String, Object> map = atracoes.get(position);
		long idAtracao = (Long) map.get("id");
		long idTipoAtracao = (Long) map.get("idTipoAtracao");
		double latitude = (Double) map.get("latitude");
		double longitude = (Double) map.get("longitude");
		String atracaoNome = (String) map.get("atracaoNome");
		String descricao = (String) map.get("descricao");
		String endereco = (String) map.get("endereco");
		int icone = (Integer) map.get("icone");
		Intent i=new Intent(this, DetalhesActivity.class);
		i.putExtra("idItem",idAtracao);
		i.putExtra("latitude",latitude);
		i.putExtra("longitude",longitude);
		i.putExtra("idTipo", idTipoAtracao);
		i.putExtra("nome", atracaoNome);
		i.putExtra("descricao", descricao);
		i.putExtra("icone", icone);
		i.putExtra("endereco", endereco);
		startActivity(i);

	}

	private class AtracaoSelecionadaTask extends AsyncTask<String, Void, String[]>{

		@Override
		protected String[] doInBackground(String... params) {
			String atracaoJson = atracaoJson(id);
			try {
				if(atracaoJson != null){
					JSONObject jsonObject = new JSONObject(atracaoJson);
					JSONObject jsonObjectTipo = new JSONObject(jsonObject.getString("atracao"));
					JSONArray jsonArraylistaDeTipoAtracao = new JSONArray(jsonObjectTipo.getString("listaDeAtracaoTuristica"));
					JSONObject jsonObject3 = jsonArraylistaDeTipoAtracao.getJSONObject(0);
					if(!jsonObject3.getString("atracao_turistica").startsWith("[")){
						JSONObject objectAtracao = new JSONObject(jsonObject3.getString("atracao_turistica"));
						itemDescricao = new String[1];

						String nome = objectAtracao.getString("nome");
						String id = objectAtracao.getString("id");
						String dataAtualizacao = objectAtracao.getString("dataAtualizacao");
						String tipoAtracao = objectAtracao.getString("tipoAtracao");
						String municipioId = objectAtracao.getString("municipio_id");
						String latitude = objectAtracao.getString("latitude");
						String longitude = objectAtracao.getString("longitude");
						
						JSONArray jsonArrayDescricao = new JSONArray(objectAtracao.getString("descricao"));
						JSONObject objectDescricao = jsonArrayDescricao.getJSONObject(0);
						jsonArrayDescricao = new JSONArray(objectDescricao.getString("descricao"));
						objectDescricao = jsonArrayDescricao.getJSONObject(0);
						String descricaoId = objectDescricao.getString("id");
						String idiomaId = objectDescricao.getString("idioma_id");
						jsonArrayDescricao = new JSONArray(objectDescricao.getString("descricao"));
						String descricao = (String) jsonArrayDescricao.get(0);
						
						JSONObject objectEndereco = new JSONObject(objectAtracao.getString("endereco"));
						String logradouro = objectEndereco.getString("logradouro");
						String numero = objectEndereco.getString("numero");
						String bairro = objectEndereco.getString("bairro");
						String cep = objectEndereco.getString("cep");
						String enderecoId = objectEndereco.getString("id");

						itemDescricao[0] = nome + "---" + id + "---" + dataAtualizacao + "---" + tipoAtracao + "---" + municipioId + "---" + latitude + "---" + longitude
								+ "---" + logradouro + "---" + numero + "---" + bairro + "---" +  cep + "---" + enderecoId + "---" + descricaoId + "---" + descricao + "---" + idiomaId;

					}else{
						JSONArray jsonReal = new JSONArray(jsonObject3.getString("atracao_turistica"));
						itemDescricao = new String[jsonReal.length()];
						for (int i = 0; i < jsonReal.length(); i++) {

							JSONObject jsonObject4 = jsonReal.getJSONObject(i);
							String nome = jsonObject4.getString("nome");
							String id = jsonObject4.getString("id");
							String dataAtualizacao = jsonObject4.getString("dataAtualizacao");
							String tipoAtracao = jsonObject4.getString("tipoAtracao");
							String municipioId = jsonObject4.getString("municipio_id");
							String latitude = jsonObject4.getString("latitude");
							String longitude = jsonObject4.getString("longitude");
							
							JSONArray jsonArrayDescricao = new JSONArray(jsonObject4.getString("descricao"));
							JSONObject objectDescricao = jsonArrayDescricao.getJSONObject(0);
							jsonArrayDescricao = new JSONArray(objectDescricao.getString("descricao"));
							objectDescricao = jsonArrayDescricao.getJSONObject(0);
							String descricaoId = objectDescricao.getString("id");
							String idiomaId = objectDescricao.getString("idioma_id");
							jsonArrayDescricao = new JSONArray(objectDescricao.getString("descricao"));
							String descricao = (String) jsonArrayDescricao.get(0);


							JSONObject objectEndereco = new JSONObject(jsonObject4.getString("endereco"));
							String logradouro = objectEndereco.getString("logradouro");
							String numero = objectEndereco.getString("numero");
							String bairro = objectEndereco.getString("bairro");
							String cep = objectEndereco.getString("cep");
							String enderecoId = objectEndereco.getString("id");

							itemDescricao[i] = nome + "---" + id + "---" + dataAtualizacao + "---" + tipoAtracao + "---" + municipioId + "---" + latitude + "---" + longitude
									+ "---" + logradouro + "---" + numero + "---" + bairro + "---" +  cep + "---" + enderecoId + "---" + descricaoId + "---" + descricao + "---" + idiomaId;
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

		

		@Override
		protected void onPostExecute(String[] result) {
			if(result != null){
				String[] from = {"atracaoNome", "atracaoImagem", "descricao", "id", "latitude", "longitude", "idTipoAtracao", "icone", "endereco"};
				int[] to = {R.id.atracao, R.id.atracaoImage};


				SimpleAdapter adapter =	new SimpleAdapter(AtracaoSelecionadoActivity.this, listarAtracaoSelecionada(result),R.layout.lista_atracao_selecionado, from, to);

				mudaTituloDaTela();	
				setListAdapter(adapter);
				getListView().setOnItemClickListener(AtracaoSelecionadoActivity.this);
				dialog.encerraDialog();
			}else{
				dialog.encerraDialog();
				Toast.makeText(getApplicationContext(), "Não há atrações turísticas.", Toast.LENGTH_LONG).show();
			}
		}


		


		private String atracaoJson(long id) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			EnderecoServidor es = new EnderecoServidor();
			HttpGet httpGet = new HttpGet(es.getEndereco()+"atracoes/"+id+"/"+idMunicipio);
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
