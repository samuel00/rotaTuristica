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

import com.example.controller.DescricaoController;
import com.example.controller.ModeTrasmition;
import com.example.controller.ServicoController;
import com.example.entidade.ServicoTuristico;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ServicoSelecionadoActivity extends ListActivity implements OnItemClickListener{

	private List<Map<String, Object>> servicos ;
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
		dialog.iniciaDialog(ServicoSelecionadoActivity.this);
		if(mt.getModoTransmissao(this) == 1){
			if(isOnline()){
				new ServicoSelecionadoTask().execute();
			}
		}
		else{
			if(montaView()){
				String[] from = {"servicoNome", "servicoImagem","id", "latitude", "longitude", "descricao","idTipoServico", "icone", "endereco"};
				int[] to = {R.id.servico, R.id.servicoImage};
				SimpleAdapter adapter =	new SimpleAdapter(ServicoSelecionadoActivity.this, servicos ,R.layout.lista_servico_selecionado, from, to);
				mudaTituloDaTela();
				setListAdapter(adapter);
				getListView().setOnItemClickListener(ServicoSelecionadoActivity.this);
				dialog.encerraDialog();
			}else
				dialog.encerraDialog();
				Toast.makeText(getApplicationContext(), "Não há servicos turísticos.", Toast.LENGTH_LONG).show();		
			}
	}
	
	@SuppressLint("NewApi")
	private void mudaTituloDaTela() {
		Intent getIntent = getIntent();
		String servico = getIntent.getStringExtra("servico");
		getActionBar().setTitle("Lista de servico - " + servico);
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
		long idServico = intent.getLongExtra("idServico",0);
		
		ServicoController servico = new ServicoController();
		List<ServicoTuristico> listaDeSevico = new ArrayList<ServicoTuristico>();
		
		servicos = new ArrayList<Map<String,Object>>();
		
		listaDeSevico = servico.getServicos(this, idServico);
		if(listaDeSevico != null){
			for(int i = 0; i < listaDeSevico.size(); i++){

				Map<String, Object> item = new HashMap<String, Object>();

				item.put("servicoNome", listaDeSevico.get(i).getNome());
				item.put("id", listaDeSevico.get(i).getId());
				item.put("latitude", listaDeSevico.get(i).getLatitude());
				item.put("longitude", listaDeSevico.get(i).getLongitude());
				item.put("idTipoServico", listaDeSevico.get(i).getTipoServico());
				item.put("servicoImagem", R.drawable.service);
				item.put("descricao", getDescricaoByItemDescricao(listaDeSevico.get(i).getId()));
				
				String endereco = listaDeSevico.get(i).getEndereco().getLogradouro() + ", " + listaDeSevico.get(i).getEndereco().getNumero() + ", "
						+ listaDeSevico.get(i).getEndereco().getBairro() + ". " + listaDeSevico.get(i).getEndereco().getCep();
				
				item.put("endereco", endereco);
				
				servicos.add(item);
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
			intent.putExtra("item", "servico");
			intent.putExtra("icone", icone);
			intent.putExtra("idMunicipio", idMunicipio);
			startActivity(intent);
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	private List<Map<String, Object>> listarServicoSelecionado(String[] result) {

		servicos = new ArrayList<Map<String,Object>>();
		for(int i = 0; i < result.length; i++){
			
			String[] split = result[i].split("---");
			String nome = split[0];
			long id = Long.parseLong(split[1]);
			double latitude = Double.parseDouble(split[5]);
			double longitude = Double.parseDouble(split[6]);
			long idTipo = Long.parseLong(split[3]);
			String descricao = split[13];
			String endereco = split[7] + ", " + split[8] + ", " + split[9] + ". " + split[10];

			Map<String, Object> item = new HashMap<String, Object>();
			
			

			item.put("servicoNome", nome);
			item.put("id", id);
			item.put("latitude", latitude);
			item.put("longitude", longitude);
			item.put("idTipoServico", idTipo);
			item.put("servicoImagem", R.drawable.service);
			item.put("descricao", descricao);
			item.put("endereco", endereco);
			servicos.add(item);

		}
		return servicos;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		Map<String, Object> map = servicos.get(position);
		long idServico = (Long) map.get("id");
		long idTipoServico = (Long) map.get("idTipoServico");
		double latitude = (Double) map.get("latitude");
		double longitude = (Double) map.get("longitude");
		String servicoNome = (String) map.get("servicoNome");
		String descricao = (String) map.get("descricao");
		String endereco = (String) map.get("endereco");
		int icone = (Integer) map.get("servicoImagem");
		Intent i=new Intent(this, DetalhesActivity.class);
		i.putExtra("idItem",idServico);
		i.putExtra("latitude",latitude);
		i.putExtra("longitude",longitude);
		i.putExtra("idTipo", idTipoServico);
		i.putExtra("nome", servicoNome);
		i.putExtra("icone", icone);
		i.putExtra("descricao", descricao);
		i.putExtra("endereco", endereco);
		startActivity(i);

	}

	private class ServicoSelecionadoTask extends AsyncTask<String, Void, String[]>{

		@Override
		protected String[] doInBackground(String... params) {
			try {
				String servicoJson = servicoJson(id);

				if(servicoJson != null){
					JSONObject jsonObject = new JSONObject(servicoJson);
					jsonObject = new JSONObject(jsonObject.getString("servico"));
					JSONArray jsonArraylistaDeTipoAtracao = new JSONArray(jsonObject.getString("listaDeServicoTuristico"));
					jsonObject = jsonArraylistaDeTipoAtracao.getJSONObject(0);
					if(!jsonObject.getString("servico_turistico").startsWith("[")){
						JSONObject objectServico = new JSONObject(jsonObject.getString("servico_turistico"));
						itemDescricao = new String[1];
						String nome = objectServico.getString("nome");
						String id = objectServico.getString("id");
						String dataAtualizacao = objectServico.getString("dataAtualizacao");
						String tipoAtracao = objectServico.getString("tipoServico");
						String municipioId = objectServico.getString("municipio_id");
						String latitude = objectServico.getString("latitude");
						String longitude = objectServico.getString("longitude");
						
						JSONArray jsonArrayDescricao = new JSONArray( objectServico.getString("descricao"));
						JSONObject objectDescricao = jsonArrayDescricao.getJSONObject(0);
						jsonArrayDescricao = new JSONArray(objectDescricao.getString("descricao"));
						objectDescricao = jsonArrayDescricao.getJSONObject(0);
						String descricaoId = objectDescricao.getString("id");
						String idiomaId = objectDescricao.getString("idioma_id");
						jsonArrayDescricao = new JSONArray(objectDescricao.getString("descricao"));
						String descricao = (String) jsonArrayDescricao.get(0);


						JSONObject objectEndereco = new JSONObject(objectServico.getString("endereco"));
						String logradouro = objectEndereco.getString("logradouro");
						String numero = objectEndereco.getString("numero");
						String bairro = objectEndereco.getString("bairro");
						String cep = objectEndereco.getString("cep");
						String enderecoId = objectEndereco.getString("id");
						Log.w("", "Logradouro - " + logradouro);

						itemDescricao[0] = nome + "---" + id + "---" + dataAtualizacao + "---" + tipoAtracao + "---" + municipioId + "---" + latitude + "---" + longitude
								+ "---" + logradouro + "---" + numero + "---" + bairro + "---" +  cep + "---" + enderecoId + "---" + descricaoId + "---" + descricao;

					}
					else{
						JSONArray jsonReal = new JSONArray(jsonObject.getString("servico_turistico"));
						itemDescricao = new String[jsonReal.length()];
						for (int i = 0; i < jsonReal.length(); i++) {

							JSONObject jsonObject4 = jsonReal.getJSONObject(i);
							String nome = jsonObject4.getString("nome");
							String id = jsonObject4.getString("id");
							String dataAtualizacao = jsonObject4.getString("dataAtualizacao");
							String tipoAtracao = jsonObject4.getString("tipoServico");
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
							Log.w("", "Logradouro - " + logradouro);

							itemDescricao[i] = nome + "---" + id + "---" + dataAtualizacao + "---" + tipoAtracao + "---" + municipioId + "---" + latitude + "---" + longitude
									+ "---" + logradouro + "---" + numero + "---" + bairro + "---" +  cep + "---" + enderecoId + "---" + descricaoId + "---" + descricao;
						}
					}

					return itemDescricao;
				} 
			}catch (Exception e) {
				Log.e(JsonActivity.class.getName(), "Deu erro");
				e.printStackTrace();
			}
			return null;
		}

		private String servicoJson(long idTipoItem) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			EnderecoServidor es = new EnderecoServidor();
			HttpGet httpGet = new HttpGet(es.getEndereco()+"servicos/"+idTipoItem+"/"+idMunicipio);
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
			if(result !=null){
				String[] from = {"servicoNome", "servicoImagem","id", "latitude", "longitude", "descricao","idTipoServico", "icone", "endereco"};
				int[] to = {R.id.servico, R.id.servicoImage};


				SimpleAdapter adapter =	new SimpleAdapter(ServicoSelecionadoActivity.this, listarServicoSelecionado(result),R.layout.lista_servico_selecionado, from, to);
				setListAdapter(adapter);
				mudaTituloDaTela();
				getListView().setOnItemClickListener(ServicoSelecionadoActivity.this);
				dialog.encerraDialog();
			}
			else{
				dialog.encerraDialog();
				Toast.makeText(getApplicationContext(), "Não há servicos turísticos.", Toast.LENGTH_LONG).show();
			}
		}

	}



}
