package com.example.rotaturistica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.example.controller.ModeTrasmition;
import com.example.controller.RotaController;
import com.example.controller.ServicoController;
import com.example.dao.AtracaoTuristicaDao;
import com.example.dao.RotaTuristicaDao;
import com.example.dao.ServicoTuristicoDao;
import com.example.download.DownloadImagem;
import com.example.entidade.AtracaoTuristica;
import com.example.entidade.Descricao;
import com.example.entidade.Multimidia;
import com.example.entidade.OrdemPontoRota;
import com.example.entidade.RotaTuristica;
import com.example.entidade.TipoRota;
import com.example.utils.Dialog;
import com.example.utils.DialogProgress;
import com.example.utils.EnderecoServidor;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.ActivityOptions;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Build;

public class DownloadListItemActivity extends ListActivity implements OnItemClickListener {

	private List<Map<String, Object>> itens;
	private String item;
	private long idTipoItem;
	private String tipoItem;
	private long idPolo;
	private String polo;
	private long idMunicipio;
	private String municipio;
	private Dialog dialog;
	String [] itemDescricao;
	String [] multimidia;
	String [] responsavel;
	String [] telefone;
	String [] pontoRota;
	
	List<RotaTuristica> listaRotaTuristica;
	List<Descricao> listaDescricao;
	List<TipoRota> listaTipoRota;
	List<Multimidia> listaMultimidia;
	List<OrdemPontoRota> listaOrdemPontoRota;
	List<AtracaoTuristica> listaAtracaoTuristica;
	private DialogProgress dialog2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();

		idTipoItem =  intent.getLongExtra("idTipo",0);
		tipoItem = intent.getStringExtra("tipoItem");
		item = intent.getStringExtra("item");
		idPolo = intent.getLongExtra("idPolo",0);
		polo = intent.getStringExtra("polo");
		idMunicipio = intent.getLongExtra("idMunicipio",0);
		municipio = intent.getStringExtra("polo");
		
		ModeTrasmition mt = new ModeTrasmition();
		if(mt.getModoTransmissao(this) == 1){
			if(isOnline()){
				new DownloadData().execute();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.download, menu);
		return true;
	} 

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.download:
			if(this.item.equalsIgnoreCase("route")){
				RotaController controller = new RotaController();
				controller.salvaAtracao(this,listaTipoRota, listaRotaTuristica, listaMultimidia, listaOrdemPontoRota, listaAtracaoTuristica,listaDescricao);
				Toast.makeText(getApplicationContext(), "Download realizado com sucesso", Toast.LENGTH_LONG).show();
			}
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	private List<Map<String, Object>> viewAtracao(String[] result) {
		if(result.length > 0){
			itens = new ArrayList<Map<String,Object>>();
			for(int i=0; i < result.length; i++){
				String[] parts = result[i].split("---");
				String nome = parts[0];
				long id = Long.parseLong(parts[1]);
				Map<String, Object> item = new HashMap<String, Object>();

				
				item.put("itemImagem", R.drawable.theater);

				item.put("itemText", nome);
				item.put("id", id);
				itens.add(item);
			}
			return itens;}
		else{
			return null;
		}

	}

	private List<? extends Map<String, ?>> viewServico(String[] result) {
		if(result.length > 0){
			itens = new ArrayList<Map<String,Object>>();
			for(int i=0; i < result.length; i++){
				String[] parts = result[i].split("---");
				String nome = parts[0];
				long id = Long.parseLong(parts[1]);
				Map<String, Object> item = new HashMap<String, Object>();

				
				item.put("itemImagem", R.drawable.service);

				item.put("itemText", nome);
				item.put("id", id);
				itens.add(item);
			}
			return itens;}
		else{
			return null;
		}

	}
	
	private List<? extends Map<String, ?>> viewRoute(String[] result) {
		if(result.length > 0){
			itens = new ArrayList<Map<String,Object>>();
			for(int i=0; i < result.length; i++){
				String[] parts = result[i].split("---");
				String nome = parts[0];
				long id = Long.parseLong(parts[1]);
				Map<String, Object> item = new HashMap<String, Object>();

				
				item.put("itemImagem", R.drawable.direction_uturn);

				item.put("itemText", nome);
				item.put("id", id);
				itens.add(item);
			}
			return itens;}
		else{
			return null;
		}
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		// TODO Auto-generated method stub

	}

	private class DownloadData extends AsyncTask<String, Integer, String[]>{
		
		 protected void onPreExecute() {
	            super.onPreExecute();
	            dialog2 = new DialogProgress("Aguarde","Baixando Rota Turística");
	            dialog2.iniciaProgressDialog(DownloadListItemActivity.this);
	        }
		 
		 protected void onProgressUpdate(Integer... progress) {
		        dialog2.setProgress(progress[0]);
		    }

		@Override
		protected String[] doInBackground(String... params) {
			try {
				if(item.equalsIgnoreCase("attractive")){
					long dataUltimaAtualizacao = dataUltimaAtualizacao(idTipoItem, DownloadListItemActivity.this);
					String atracaoJson = atracaoJson(idTipoItem, dataUltimaAtualizacao);

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
							Log.w("", "Logradouro - " + logradouro);

							itemDescricao[0] = nome + "---" + id + "---" + dataAtualizacao + "---" + tipoAtracao + "---" + municipioId + "---" + latitude + "---" + longitude
									+ "---" + logradouro + "---" + numero + "---" + bairro + "---" +  cep + "---" + enderecoId + "---" + descricaoId + "---" + descricao + "---" + idiomaId + "---" + tipoItem
									+ "---" + idPolo + "---" + polo + "---" + municipio;
							
							getMultimidia(id);
							getResponsavel(id);

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
										+ "---" + logradouro + "---" + numero + "---" + bairro + "---" +  cep + "---" + enderecoId + "---" + descricaoId + "---" + descricao + "---" + idiomaId+ "---" + tipoItem
										+ "---" + idPolo + "---" + polo + "---" + municipio;
								
								getMultimidia(id);
								getResponsavel(id);
							}
						}

						return itemDescricao;
					} 

				}
				else
					if(item.equalsIgnoreCase("service")){
						long dataUltimaAtualizacao = dataUltimaAtualizacao(idTipoItem, DownloadListItemActivity.this);
						String servicoJson = servicoJson(idTipoItem, dataUltimaAtualizacao);

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
								
								JSONArray jsonArrayDescricao = new JSONArray(objectServico.getString("descricao"));
								JSONObject objectDescricao = jsonArrayDescricao.getJSONObject(0);
								jsonArrayDescricao = new JSONArray(objectDescricao.getString("descricao"));
								objectDescricao = jsonArrayDescricao.getJSONObject(0);
								String descricaoId = objectDescricao.getString("id");
								String idiomaId = objectDescricao.getString("idioma_id");
								jsonArrayDescricao = new JSONArray(objectDescricao.getString("descricao"));
								String descricao = (String) jsonArrayDescricao.get(0);

								getMultimidia(id);
								getResponsavel(id);


								JSONObject objectEndereco = new JSONObject(objectServico.getString("endereco"));
								String logradouro = objectEndereco.getString("logradouro");
								String numero = objectEndereco.getString("numero");
								String bairro = objectEndereco.getString("bairro");
								String cep = objectEndereco.getString("cep");
								String enderecoId = objectEndereco.getString("id");

								itemDescricao[0] = nome + "---" + id + "---" + dataAtualizacao + "---" + tipoAtracao + "---" + municipioId + "---" + latitude + "---" + longitude
										+ "---" + logradouro + "---" + numero + "---" + bairro + "---" +  cep + "---" + enderecoId + "---" + descricaoId + "---" + descricao + "---" + idiomaId+ "---" + tipoItem
										+ "---" + idPolo + "---" + polo + "---" + municipio;
								
							}
							else{
								JSONArray jsonReal = new JSONArray(jsonObject.getString("servico_turistico"));
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

									getMultimidia(id);
									getResponsavel(id);


									JSONObject objectEndereco = new JSONObject(jsonObject4.getString("endereco"));
									String logradouro = objectEndereco.getString("logradouro");
									String numero = objectEndereco.getString("numero");
									String bairro = objectEndereco.getString("bairro");
									String cep = objectEndereco.getString("cep");
									String enderecoId = objectEndereco.getString("id");

									itemDescricao[i] = nome + "---" + id + "---" + dataAtualizacao + "---" + tipoAtracao + "---" + municipioId + "---" + latitude + "---" + longitude
											+ "---" + logradouro + "---" + numero + "---" + bairro + "---" +  cep + "---" + enderecoId + "---" + descricaoId + "---" + descricao + "---" + idiomaId+ "---" + tipoItem
											+ "---" + idPolo + "---" + polo + "---" + municipio;
								}
							}

							return itemDescricao;
						} 

					}else
						if(item.equalsIgnoreCase("route")){
							int tamanhoArquivo;
							long dataUltimaAtualizacao = dataUltimaAtualizacao(idTipoItem, DownloadListItemActivity.this);
							String rotaJson = rotaJson(idTipoItem, dataUltimaAtualizacao);
								if(rotaJson != null){
									listaRotaTuristica = new ArrayList<RotaTuristica>();
									listaDescricao = new ArrayList<Descricao>();
									listaTipoRota = new ArrayList<TipoRota>();
									listaOrdemPontoRota = new ArrayList<OrdemPontoRota>();
									listaAtracaoTuristica = new ArrayList<AtracaoTuristica>();
									listaMultimidia = new ArrayList<Multimidia>();
									
									TipoRota tipoRota = new TipoRota();
									
									tipoRota.setNome(tipoItem);
									tipoRota.setId(idTipoItem);
									
									listaTipoRota.add(tipoRota);
									
									JSONObject jsonObject = new JSONObject(rotaJson);
									JSONObject jsonObjectTipo = new JSONObject(jsonObject.getString("rota"));
									JSONArray jsonArraylistaDeTipoRota = new JSONArray(jsonObjectTipo.getString("listaDeRotaTuristica"));
									JSONObject jsonObject3 = jsonArraylistaDeTipoRota.getJSONObject(0);
									if(!jsonObject3.getString("rota_turistica").startsWith("[")){
										JSONObject objectRota = new JSONObject(jsonObject3.getString("rota_turistica"));
										itemDescricao = new String[1];
										
										tamanhoArquivo = 1;
										
										publishProgress((int)(tamanhoArquivo*100/itemDescricao.length));

										String nome = objectRota.getString("nome");
										String id = objectRota.getString("id");
										String dataAtualizacao = objectRota.getString("dataAtualizacao");
										String tipoRotaId = objectRota.getString("tipoRota");
										String municipioId = objectRota.getString("municipio_id");
										
										RotaTuristica rotaTuristica = new RotaTuristica();
										
										rotaTuristica.setId(Long.parseLong(id));
										rotaTuristica.setNome(nome);
										rotaTuristica.setDataAtualizacao(getData(dataAtualizacao));
										rotaTuristica.setMunicipio_id(0);
										rotaTuristica.setTipo_rota(Long.parseLong(tipoRotaId));
										
										listaRotaTuristica.add(rotaTuristica);
										
										getMultimidia(id);
										getPontoRota(id);
										
										Descricao descricao = new Descricao();
										
										JSONArray jsonArrayDescricao = new JSONArray(objectRota.getString("descricao"));
										JSONObject objectDescricao = jsonArrayDescricao.getJSONObject(0);
										jsonArrayDescricao = new JSONArray(objectDescricao.getString("descricao"));
										objectDescricao = jsonArrayDescricao.getJSONObject(0);
										String descricaoId = objectDescricao.getString("id");
										String idiomaId = objectDescricao.getString("idioma_id");
										jsonArrayDescricao = new JSONArray(objectDescricao.getString("descricao"));
										String descricaoText = (String) jsonArrayDescricao.get(0);

										itemDescricao[0] = nome + "---" + id + "---" + dataAtualizacao + "---" + tipoRota + "---" + municipioId + "---" + descricaoId + "---" + descricaoText + "---" + idiomaId+ "---" + tipoItem;
										
										descricao.setId(Long.parseLong(descricaoId));
										descricao.setDescricao(descricaoText);
										descricao.setIdioma(Long.parseLong(idiomaId));
										descricao.setItem_descricao(Long.parseLong(id));
										listaDescricao.add(descricao);
										
									}else{
										JSONArray jsonReal = new JSONArray(jsonObject3.getString("rota_turistica"));
										itemDescricao = new String[jsonReal.length()];
										for (int i = 0; i < jsonReal.length(); i++) {
											
											tamanhoArquivo = i+1;
											
											publishProgress((int)(tamanhoArquivo*100/jsonReal.length()));

											JSONObject jsonObject4 = jsonReal.getJSONObject(i);
											String nome = jsonObject4.getString("nome");
											String id = jsonObject4.getString("id");
											String dataAtualizacao = jsonObject4.getString("dataAtualizacao");
											String tipoRotaId = jsonObject4.getString("tipoRota");
											String municipioId = jsonObject4.getString("municipio_id");
											
											RotaTuristica rotaTuristica = new RotaTuristica();
											
											rotaTuristica.setId(Long.parseLong(id));
											rotaTuristica.setNome(nome);
											rotaTuristica.setDataAtualizacao(getData(dataAtualizacao));
											rotaTuristica.setMunicipio_id(0);
											rotaTuristica.setTipo_rota(Long.parseLong(tipoRotaId));
											
											listaRotaTuristica.add(rotaTuristica);
											
											getMultimidia(id);
											getPontoRota(id);
											
											Descricao descricao = new Descricao();

											JSONArray jsonArrayDescricao = new JSONArray(jsonObject4.getString("descricao"));
											JSONObject objectDescricao = jsonArrayDescricao.getJSONObject(0);
											jsonArrayDescricao = new JSONArray(objectDescricao.getString("descricao"));
											objectDescricao = jsonArrayDescricao.getJSONObject(0);
											String descricaoId = objectDescricao.getString("id");
											String idiomaId = objectDescricao.getString("idioma_id");
											jsonArrayDescricao = new JSONArray(objectDescricao.getString("descricao"));
											String descricaoText = (String) jsonArrayDescricao.get(0);


											itemDescricao[i] = nome + "---" + id + "---" + dataAtualizacao + "---" + tipoRota  + "---" + municipioId + "---" + descricaoId + "---" + descricaoText + "---" + idiomaId+ "---" + tipoItem;
											
											descricao.setId(Long.parseLong(descricaoId));
											descricao.setDescricao(descricaoText);
											descricao.setIdioma(Long.parseLong(idiomaId));
											descricao.setItem_descricao(Long.parseLong(id));
											listaDescricao.add(descricao);
										}
									}

									return itemDescricao;
						}

			}}catch (Exception e) {
				Log.e(JsonActivity.class.getName(), "Deu erro");
				e.printStackTrace();
			}
			return null;
		}

		

		private Date getData(String dataAtualizacao) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date data = null;
			try {
				data = df.parse(dataAtualizacao);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return data;
		}



		private String rotaJson(long id, long dataUltimaAtualizacao) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			EnderecoServidor es = new EnderecoServidor();
			HttpGet httpGet = new HttpGet(es.getEndereco()+"rotas/download/"+id+"/"+dataUltimaAtualizacao);
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
		
		private String atracaoJson(long id, long dataUltimaAtualizacao) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			EnderecoServidor es = new EnderecoServidor();
			HttpGet httpGet = new HttpGet(es.getEndereco()+"atracoes/download/"+id+"/"+idMunicipio+"/"+dataUltimaAtualizacao);
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

		private String servicoJson(long idTipoItem, long dataUltimaAtualizacao) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			EnderecoServidor es = new EnderecoServidor();
			HttpGet httpGet = new HttpGet(es.getEndereco()+"servicos/download/"+idTipoItem+"/"+idMunicipio+"/"+dataUltimaAtualizacao);
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

		private void getResponsavel(String id) {
			int itemId = Integer.parseInt(id);
			String responsavelJson = responsavelJson(itemId);
			try {
				JSONObject responsavelObject = new JSONObject(responsavelJson);
				responsavelObject  = new JSONObject(responsavelObject.getString("responsavel"));
				JSONArray jsonArraylistaDeResponsavel = new JSONArray(responsavelObject .getString("listaDeInfoDoResponsavel"));
				responsavelObject  = jsonArraylistaDeResponsavel.getJSONObject(0);
				if(!responsavelObject.getString("informacoes").startsWith("[")){
					JSONObject objectResponsavel= new JSONObject(responsavelObject.getString("informacoes"));
					responsavel = new String[1];
					String nome  = objectResponsavel.getString("nome");
					String responsavelId  = objectResponsavel.getString("id");
					String email  = objectResponsavel.getString("email");
					responsavel[0] = itemId + "---" + responsavelId + "---" + nome + "---" + email;
					Log.w("","DownloadListI - " + itemId + "---" + responsavelId + "---" + nome + "---" + email);
					
					JSONArray jsonArraylistaDeTelefone = new JSONArray(objectResponsavel.getString("telefone"));
					JSONObject objectTelefone = jsonArraylistaDeTelefone.getJSONObject(0);
					if(!objectTelefone.getString("telefone").startsWith("[")){
						objectTelefone = new JSONObject(objectTelefone.getString("telefone"));
						telefone = new String [1];
						String telefoneId = objectTelefone.getString("id");
						String codArea= objectTelefone.getString("codArea");
						String numero= objectTelefone.getString("numero");
						telefone[0] = responsavelId + "---" + telefoneId + "---" + codArea + "---" + numero;
						Log.w("","DownloadListI - " + responsavelId + "---" + telefoneId + "---" + codArea + "---" + numero);
						
					}else{
						jsonArraylistaDeTelefone = new JSONArray(objectTelefone.getString("telefone"));
						telefone = new String[jsonArraylistaDeTelefone.length()];
						for(int i = 0; i < jsonArraylistaDeTelefone.length(); i ++){
							JSONObject jsonObject5 = jsonArraylistaDeTelefone.getJSONObject(i);
							String telefoneId = jsonObject5.getString("id");
							String codArea= jsonObject5.getString("codArea");
							String numero= jsonObject5.getString("numero");
							telefone[i] = responsavelId + "---" + telefoneId + "---" + codArea + "---" + numero;
							Log.w("","DownloadListI - " + responsavelId + "---" + telefoneId + "---" + codArea + "---" + numero);
						}
					}
				}else{
					jsonArraylistaDeResponsavel = new JSONArray(responsavelObject.getString("informacoes"));
					responsavel = new String[jsonArraylistaDeResponsavel.length()];
					for(int j =0; j < jsonArraylistaDeResponsavel.length(); j++){
						JSONObject jsonObject5 = jsonArraylistaDeResponsavel.getJSONObject(j);
						String nome  = jsonObject5.getString("nome");
						String responsavelId  = jsonObject5.getString("id");
						String email  = jsonObject5.getString("email");
						responsavel[j] = itemId + "---" + responsavelId + "---" + nome + "---" + email;
						
						JSONArray jsonArraylistaDeTelefone = new JSONArray(jsonObject5.getString("telefone"));
						JSONObject objectTelefone = jsonArraylistaDeTelefone.getJSONObject(0);
						if(!objectTelefone.getString("telefone").startsWith("[")){
							objectTelefone= new JSONObject(objectTelefone.getString("telefone"));
							telefone = new String [1];
							String telefoneId = objectTelefone.getString("id");
							String codArea= objectTelefone.getString("codArea");
							String numero= objectTelefone.getString("numero");
							telefone[0] = responsavelId + "---" +  telefoneId + "---" + codArea + "---" + numero;
							
						}else{
							jsonArraylistaDeTelefone = new JSONArray(objectTelefone.getString("telefone"));
							telefone = new String[jsonArraylistaDeTelefone.length()];
							for(int i = 0; i < jsonArraylistaDeTelefone.length(); i ++){
								JSONObject jsonObject6 = jsonArraylistaDeTelefone.getJSONObject(i);
								String telefoneId = jsonObject6.getString("id");
								String codArea= jsonObject6.getString("codArea");
								String numero= jsonObject6.getString("numero");
								telefone[i] = responsavelId + "---" + telefoneId + "---" + codArea + "---" + numero;
								
							}
						}

					}
				}

			}catch (Exception e) {
				Log.w(JsonActivity.class.getName(), "Deu erro");
				e.printStackTrace();
			}
		}

		private String responsavelJson(int responsavelId) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			EnderecoServidor es = new EnderecoServidor();
			HttpGet httpGet = new HttpGet(es.getEndereco()+"responsaveis/"+responsavelId);
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
		
		
		private void getPontoRota(String id) {
			long itemId = Long.parseLong(id);
			String pontoRotaJson = pontoRotaJson(itemId);
			try {
				JSONObject jsonObject = new JSONObject(pontoRotaJson);
				jsonObject = new JSONObject(jsonObject.getString("rota"));
				JSONArray jsonArray = new JSONArray(jsonObject.getString("listaDePontoRota"));
				jsonObject = jsonArray.getJSONObject(0);
				jsonArray = new JSONArray(jsonObject.getString("ponto_rota"));
				pontoRota = new String[jsonArray.length()];
				for (int i = 0; i < jsonArray.length(); i++) {
					
					JSONObject objectPontoRota = jsonArray.getJSONObject(i);
					String ordemPontoRotaId = objectPontoRota.getString("id");
					String posicao = objectPontoRota.getString("posicao");
					
					JSONObject objectPontoLocalizavel = new JSONObject(objectPontoRota.getString("pontoLocalizavel"));
					String pontoLocalizavelId = objectPontoLocalizavel.getString("id");
					String nome = objectPontoLocalizavel.getString("nome");
					String latitude = objectPontoLocalizavel.getString("latitude");
					String longitude = objectPontoLocalizavel.getString("longitude");
					
					OrdemPontoRota ordemPontoRota = new OrdemPontoRota();
					
					ordemPontoRota.setId(Long.parseLong(ordemPontoRotaId));
					ordemPontoRota.setPosicao(Integer.parseInt(posicao));
					ordemPontoRota.setPontoLocalizavelId(Long.parseLong(pontoLocalizavelId));
					ordemPontoRota.setRotaTuristicaId(itemId);
					
					AtracaoTuristica atracaoTuristica = new AtracaoTuristica();
					
					atracaoTuristica.setNome(nome);
					atracaoTuristica.setId(Long.parseLong(pontoLocalizavelId));
					atracaoTuristica.setLatitude(Double.parseDouble(latitude));
					atracaoTuristica.setLongitude(Double.parseDouble(longitude));
					
					listaAtracaoTuristica.add(atracaoTuristica);
					listaOrdemPontoRota.add(ordemPontoRota);
					
					pontoRota [i] = ordemPontoRotaId + "---" + posicao + "---" + itemId + "---" + pontoLocalizavelId;
				}
			} catch (Exception e) {
				Log.w(JsonActivity.class.getName(), "Deu erro");
				e.printStackTrace();
			}
			
		}

		private String pontoRotaJson(Long id) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			EnderecoServidor es = new EnderecoServidor();
			HttpGet httpGet = new HttpGet(es.getEndereco()+"ponto_rotas/"+id);
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



		private void getMultimidia(String id) {
			int itemId = Integer.parseInt(id);
			String multimidiaJson = multimidiaJson(itemId);
			try {
				JSONObject multimidiaObject = new JSONObject(multimidiaJson);
				multimidiaObject = new JSONObject(multimidiaObject.getString("multimidia"));
				JSONArray jsonArraylistaDeMultimidia = new JSONArray(multimidiaObject.getString("listaDeMultimidia"));
				multimidiaObject = jsonArraylistaDeMultimidia.getJSONObject(0);
				if(!multimidiaObject.getString("multimidia").startsWith("[")){
					
					JSONObject objectMultimidia = new JSONObject(multimidiaObject.getString("multimidia"));
					
					
					String url = objectMultimidia.getString("url");
					String multimidiaId = objectMultimidia.getString("id");
					
					Multimidia multimidia = new Multimidia();
					
					multimidia.setId(Long.parseLong(multimidiaId));
					multimidia.setUrl(url);
					
					listaMultimidia.add(multimidia);
					
					
				}else{
					jsonArraylistaDeMultimidia = new JSONArray(multimidiaObject.getString("multimidia"));
					
					for(int j =0; j < jsonArraylistaDeMultimidia.length(); j++){
						JSONObject jsonObject5 = jsonArraylistaDeMultimidia.getJSONObject(j);
						
						String url = jsonObject5.getString("url");
						String multimidiaId = jsonObject5.getString("id");
						
						Multimidia multimidia = new Multimidia();
						
						multimidia.setId(Long.parseLong(multimidiaId));
						multimidia.setUrl(url);
						
						listaMultimidia.add(multimidia);
					}

				}

			}catch (Exception e) {
				Log.w(JsonActivity.class.getName(), "Deu erro");
				e.printStackTrace();
			}
			if(listaMultimidia.size() > 0){
				DownloadImagem downloadImagem = new DownloadImagem(listaMultimidia);
				downloadImagem.downloadImagem();
			}
		}

		private String multimidiaJson(int multimidiaId) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			EnderecoServidor es = new EnderecoServidor();
			HttpGet httpGet = new HttpGet(es.getEndereco()+"multimidias/"+multimidiaId);
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

		private long dataUltimaAtualizacao(long idTipoItem, DownloadListItemActivity downloadListItemActivity) {
			if(item.equalsIgnoreCase("attractive")){
				AtracaoTuristicaDao dao = new AtracaoTuristicaDao();
				long data = dao.dataUltimaAtualizacao(idTipoItem, downloadListItemActivity);
				return data;
			}else
				if(item.equalsIgnoreCase("service")){
					ServicoTuristicoDao dao = new ServicoTuristicoDao();
					long data = dao.dataUltimaAtualizacao(idTipoItem, downloadListItemActivity);
					return data;
				}
				else
					if(item.equalsIgnoreCase("route")){
						RotaTuristicaDao dao = new RotaTuristicaDao();
						long data = dao.dataUltimaAtualizacao(idTipoItem, downloadListItemActivity);
						return data;
					}
			return 0;

		}

		@Override
		protected void onPostExecute(String[] result) {
			String[] from = {"itemText", "itemImagem", "itemStatus","action"};
			int[] to = {R.id.itemText, R.id.itemImage};

			if(result !=null){
				if(item.equalsIgnoreCase("attractive")){
					SimpleAdapter adapter =	new SimpleAdapter(DownloadListItemActivity.this, viewAtracao(result),R.layout.download_list_item, from, to);
					setListAdapter(adapter);
					getListView().setOnItemClickListener(DownloadListItemActivity.this);
					dialog.encerraDialog();
				}else
					if(item.equalsIgnoreCase("service")){
						SimpleAdapter adapter =	new SimpleAdapter(DownloadListItemActivity.this, viewServico(result),R.layout.download_list_item, from, to);
						setListAdapter(adapter);
						getListView().setOnItemClickListener(DownloadListItemActivity.this);
						dialog.encerraDialog();
					}
					else
						if(item.equalsIgnoreCase("route")){
							SimpleAdapter adapter =	new SimpleAdapter(DownloadListItemActivity.this, viewRoute(result),R.layout.download_list_item, from, to);
							setListAdapter(adapter);
							getListView().setOnItemClickListener(DownloadListItemActivity.this);
							dialog.encerraDialog();
							dialog2.encerraProgress();
						}
			}else{
				dialog.encerraDialog();
				Toast.makeText(getApplicationContext(), "Não há dados novos para download", Toast.LENGTH_LONG).show();
			}
		}

		






		
	}

}

