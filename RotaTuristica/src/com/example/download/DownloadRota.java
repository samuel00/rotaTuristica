package com.example.download;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.entidade.AtracaoTuristica;
import com.example.entidade.Descricao;
import com.example.entidade.InfoDoResponsavel;
import com.example.entidade.Multimidia;
import com.example.entidade.OrdemPontoRota;
import com.example.entidade.RotaTuristica;
import com.example.entidade.ServicoTuristico;
import com.example.entidade.Telefone;
import com.example.entidade.TipoRota;
import com.example.entidade.TipoServico;
import com.example.rotaturistica.JsonActivity;
import com.example.rotaturistica.R;
import com.example.utils.DialogProgress;
import com.example.utils.EnderecoServidor;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

public class DownloadRota {
	
	long dataUltimaAtualizacao;
	private ListActivity classe;
	private long idTipoItem;
	private long idTipoRota;
	private String item;
	private String rota;
	
	List<RotaTuristica> listaRotaTuristica;
	List<Descricao> listaDescricao;
	List<TipoRota> listaTipoRota;
	List<Multimidia> listaMultimidia;
	List<OrdemPontoRota> listaOrdemPontoRota;
	List<AtracaoTuristica> listaAtracaoTuristica;
	private DialogProgress dialog;
	
	
	
	public void salvarRota(ListActivity classe, long idTipoRota, String item, String rota) {
		this.classe = classe;
		this.idTipoRota = idTipoRota;
		this.item = item;
		this.rota = rota;
		
		new DownloadData().execute();
	}
	
	private class DownloadData extends AsyncTask<String, Integer, String[]>{
		
		 protected void onPreExecute() {
	            super.onPreExecute();
	            dialog = new DialogProgress("Aguarde","Baixando Rota Turística");
	            dialog.iniciaProgressDialog(classe);
	        }
		 
		 protected void onProgressUpdate(Integer... progress) {
		        dialog.setProgress(progress[0]);
		    }

		@Override
		protected String[] doInBackground(String... params) {
			try {
				String rotaJson = rotaJson();
				if(rotaJson != null){
					listaRotaTuristica = new ArrayList<RotaTuristica>();
					JSONObject jsonObject = new JSONObject(rotaJson);
					JSONObject jsonObjectTipo = new JSONObject(jsonObject.getString("rota"));
					JSONArray jsonArraylistaDeTipoRota = new JSONArray(jsonObjectTipo.getString("listaDeRotaTuristica"));
					JSONObject jsonObject3 = jsonArraylistaDeTipoRota.getJSONObject(0);
					if(!jsonObject3.getString("rota_turistica").startsWith("[")){
						JSONObject objectRota = new JSONObject(jsonObject3.getString("rota_turistica"));
						
						RotaTuristica rotaTuristica = new RotaTuristica();

						String nome = objectRota.getString("nome");
						String id = objectRota.getString("id");
						String dataAtualizacao = objectRota.getString("dataAtualizacao");
						String tipoRota = objectRota.getString("tipoRota");
						String municipioId = objectRota.getString("municipio_id");
						
						rotaTuristica.setId(Long.parseLong(id));
						rotaTuristica.setNome(nome);
						rotaTuristica.setDataAtualizacao(getData(dataAtualizacao));
						rotaTuristica.setTipo_rota(Long.parseLong(tipoRota));
						rotaTuristica.setMunicipio_id(0);
						
						listaRotaTuristica.add(rotaTuristica);
						
						getMultimidia(id);
						getPontoRota(id);
						
						JSONArray jsonArrayDescricao = new JSONArray(objectRota.getString("descricao"));
						JSONObject objectDescricao = jsonArrayDescricao.getJSONObject(0);
						jsonArrayDescricao = new JSONArray(objectDescricao.getString("descricao"));
						objectDescricao = jsonArrayDescricao.getJSONObject(0);
						String descricaoId = objectDescricao.getString("id");
						String idiomaId = objectDescricao.getString("idioma_id");
						jsonArrayDescricao = new JSONArray(objectDescricao.getString("descricao"));
						String descricao = (String) jsonArrayDescricao.get(0);
						
					}else{
						JSONArray jsonReal = new JSONArray(jsonObject3.getString("rota_turistica"));
						for (int i = 0; i < jsonReal.length(); i++) {

							JSONObject jsonObject4 = jsonReal.getJSONObject(i);
							String nome = jsonObject4.getString("nome");
							String id = jsonObject4.getString("id");
							String dataAtualizacao = jsonObject4.getString("dataAtualizacao");
							String tipoRota = jsonObject4.getString("tipoRota");
							String municipioId = jsonObject4.getString("municipio_id");
							
							getMultimidia(id);
							getPontoRota(id);

							JSONArray jsonArrayDescricao = new JSONArray(jsonObject4.getString("descricao"));
							JSONObject objectDescricao = jsonArrayDescricao.getJSONObject(0);
							jsonArrayDescricao = new JSONArray(objectDescricao.getString("descricao"));
							objectDescricao = jsonArrayDescricao.getJSONObject(0);
							String descricaoId = objectDescricao.getString("id");
							String idiomaId = objectDescricao.getString("idioma_id");
							jsonArrayDescricao = new JSONArray(objectDescricao.getString("descricao"));
							String descricao = (String) jsonArrayDescricao.get(0);

						}
					}

					return null;
		}
		
		}catch (Exception e) {
			dialog.encerraProgress();
			Toast.makeText(classe, "Erro de conexao", Toast.LENGTH_LONG).show();
			Log.e(JsonActivity.class.getName(), "Deu erro");
			e.printStackTrace();
		}
		return null;
	}

		private String rotaJson() {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			EnderecoServidor es = new EnderecoServidor();
			HttpGet httpGet = new HttpGet(es.getEndereco()+"rotas/download/"+idTipoRota+"/"+dataUltimaAtualizacao);
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
			int itemId = Integer.parseInt(id);
			String pontoRotaJson = pontoRotaJson(itemId);
			try {
				listaOrdemPontoRota = new ArrayList<OrdemPontoRota>();
				listaAtracaoTuristica = new ArrayList<AtracaoTuristica>();
				JSONObject jsonObject = new JSONObject(pontoRotaJson);
				jsonObject = new JSONObject(jsonObject.getString("rota"));
				JSONArray jsonArray = new JSONArray(jsonObject.getString("listaDePontoRota"));
				jsonObject = jsonArray.getJSONObject(0);
				jsonArray = new JSONArray(jsonObject.getString("ponto_rota"));
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
					
					AtracaoTuristica atracaoTuristica = new AtracaoTuristica();
					
					atracaoTuristica.setNome(nome);
					atracaoTuristica.setLatitude(Double.parseDouble(latitude));
					atracaoTuristica.setLongitude(Double.parseDouble(longitude));
					
					listaAtracaoTuristica.add(atracaoTuristica);
					listaOrdemPontoRota.add(ordemPontoRota);
				}
			} catch (Exception e) {
				Log.w(JsonActivity.class.getName(), "Deu erro");
				e.printStackTrace();
			}
			
		}

		private String pontoRotaJson(int id) {
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
				listaMultimidia = new ArrayList<Multimidia>();
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
		
		protected void onPostExecute(String[] result) {
			String[] from = {"itemText", "itemImagem", "itemStatus","action"};
			int[] to = {R.id.itemText, R.id.itemImage};
			
			
		}
		 
	}

	
	
	
}
