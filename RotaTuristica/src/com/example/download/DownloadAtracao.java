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

import com.example.controller.AtracaoController;
import com.example.controller.ServicoController;
import com.example.entidade.AtracaoTuristica;
import com.example.entidade.Descricao;
import com.example.entidade.Endereco;
import com.example.entidade.InfoDoResponsavel;
import com.example.entidade.Multimidia;
import com.example.entidade.Telefone;
import com.example.entidade.TipoAtracao;
import com.example.rotaturistica.DownloadListItemActivity;
import com.example.rotaturistica.JsonActivity;
import com.example.utils.Dialog;
import com.example.utils.DialogProgress;
import com.example.utils.EnderecoServidor;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

public class DownloadAtracao {

	long dataUltimaAtualizacao;
	private long idPolo;
	private String polo;
	private long idMunicipio;
	private String municipio;
	FragmentActivity classe;
	String [] itemDescricao;
	String [] multimidia;
	String [] responsavel;
	String [] telefone;
	
	private DialogProgress dialog;
	
	List<AtracaoTuristica> listaAtracaoTuristicas;
	List<Descricao> listaDescricao;
	List<Endereco> listaEnderecos;
	List<TipoAtracao> listaDeAtracao;
	List<Multimidia> listaMultimidia;
	List<InfoDoResponsavel> listaDoResponsavel;
	List<Telefone> listaTelefone;
	
	public void salvaAtracao(FragmentActivity classe,	long dataUltimaAtualizacao, long idPolo, String polo, String municipio, long idMunicipio) {
		this.dataUltimaAtualizacao = dataUltimaAtualizacao;
		this.idPolo = idPolo;
		this.polo = polo;
		this.municipio = municipio;
		this.classe = classe;
		this.idMunicipio = idMunicipio;
		
		new DownloadData().execute();
	}

	private class DownloadData extends AsyncTask<String, Integer, String[]>{
		
		
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new DialogProgress("Aguarde","Baixando Atração Turísticas de " + municipio);
			dialog.iniciaProgressDialog(classe);
		}

		protected void onProgressUpdate(Integer... progress) {
			dialog.setProgress(progress[0]);
		}

		@Override
		protected String[] doInBackground(String... params) {
			try {
				String atracaoJson = atracaoJson();
				if(atracaoJson != null){
					int tamanhoArquivo = 0;
					listaAtracaoTuristicas = new ArrayList<AtracaoTuristica>();
					listaEnderecos = new ArrayList<Endereco>();
					listaDescricao = new ArrayList<Descricao>();
					listaMultimidia = new ArrayList<Multimidia>();
					listaDoResponsavel = new  ArrayList<InfoDoResponsavel>();
					listaTelefone = new ArrayList<Telefone>();
					JSONObject jsonObject = new JSONObject(atracaoJson);
					JSONObject jsonObjectTipo = new JSONObject(jsonObject.getString("atracao"));
					JSONArray jsonArraylistaDeTipoAtracao = new JSONArray(jsonObjectTipo.getString("listaDeAtracaoTuristica"));
					JSONObject jsonObject3 = jsonArraylistaDeTipoAtracao.getJSONObject(0);
					if(!jsonObject3.getString("atracao_turistica").startsWith("[")){
						JSONObject objectAtracao = new JSONObject(jsonObject3.getString("atracao_turistica"));
						
						
						AtracaoTuristica atracaoTuristica = new AtracaoTuristica();
						Endereco endereco = new Endereco();
						Descricao descricao = new Descricao();
						
						itemDescricao = new String[1];
						
						tamanhoArquivo = 1;
						
						publishProgress((int)(tamanhoArquivo*100/itemDescricao.length));

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
						String descricaoText = (String) jsonArrayDescricao.get(0);
						
						descricao.setId(Long.parseLong(descricaoId));
						descricao.setDescricao(descricaoText);
						descricao.setIdioma(Long.parseLong(idiomaId));
						descricao.setItem_descricao(Long.parseLong(id));
						listaDescricao.add(descricao);

						JSONObject objectEndereco = new JSONObject(objectAtracao.getString("endereco"));
						String logradouro = objectEndereco.getString("logradouro");
						String numero = objectEndereco.getString("numero");
						String bairro = objectEndereco.getString("bairro");
						String cep = objectEndereco.getString("cep");
						String enderecoId = objectEndereco.getString("id");
						
						atracaoTuristica.setId(Long.parseLong(id));
						atracaoTuristica.setNome(nome);
						atracaoTuristica.setDataAtualizacao(getData(dataAtualizacao));
						atracaoTuristica.setTipoAtracao(Long.parseLong(tipoAtracao));
						atracaoTuristica.setMunicipio_id(Long.parseLong(municipioId));
						atracaoTuristica.setLatitude(Double.parseDouble(latitude));
						atracaoTuristica.setLongitude(Double.parseDouble(longitude));
						listaAtracaoTuristicas.add(atracaoTuristica);
						
						endereco.setLogradouro(logradouro);
						endereco.setNumero(numero);
						endereco.setBairro(bairro);
						endereco.setCep(cep);
						endereco.setId(Long.parseLong(enderecoId));
						endereco.setPontoLocalizavel(atracaoTuristica);
						listaEnderecos.add(endereco);

						itemDescricao[0] = nome + "---" + id + "---" + dataAtualizacao + "---" + tipoAtracao + "---" + municipioId + "---" + latitude + "---" + longitude
								+ "---" + logradouro + "---" + numero + "---" + bairro + "---" +  cep + "---" + enderecoId + "---" + descricaoId + "---" + descricao + "---" + idiomaId 
								+ "---" + idPolo + "---" + polo + "---" + municipio;

						getMultimidia(id);
						getResponsavel(id,atracaoTuristica);

					}else{
						JSONArray jsonReal = new JSONArray(jsonObject3.getString("atracao_turistica"));
						itemDescricao = new String[jsonReal.length()];
						for (int i = 0; i < jsonReal.length(); i++) {
							
							tamanhoArquivo = i+1;
							
							publishProgress((int)(tamanhoArquivo*100/jsonReal.length()));
							
							AtracaoTuristica atracaoTuristica = new AtracaoTuristica();
							Endereco endereco = new Endereco();
							Descricao descricao = new Descricao();

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
							String descricaoText = (String) jsonArrayDescricao.get(0);								
							
							descricao.setId(Long.parseLong(descricaoId));
							descricao.setDescricao(descricaoText);
							descricao.setIdioma(Long.parseLong(idiomaId));
							descricao.setItem_descricao(Long.parseLong(id));
							listaDescricao.add(descricao);

							JSONObject objectEndereco = new JSONObject(jsonObject4.getString("endereco"));
							String logradouro = objectEndereco.getString("logradouro");
							String numero = objectEndereco.getString("numero");
							String bairro = objectEndereco.getString("bairro");
							String cep = objectEndereco.getString("cep");
							String enderecoId = objectEndereco.getString("id");
							
							atracaoTuristica.setId(Long.parseLong(id));
							atracaoTuristica.setNome(nome);
							atracaoTuristica.setDataAtualizacao(getData(dataAtualizacao));
							atracaoTuristica.setTipoAtracao(Long.parseLong(tipoAtracao));
							atracaoTuristica.setMunicipio_id(Long.parseLong(municipioId));
							atracaoTuristica.setLatitude(Double.parseDouble(latitude));
							atracaoTuristica.setLongitude(Double.parseDouble(longitude));
							listaAtracaoTuristicas.add(atracaoTuristica);
							
							endereco.setLogradouro(logradouro);
							endereco.setNumero(numero);
							endereco.setBairro(bairro);
							endereco.setCep(cep);
							endereco.setId(Long.parseLong(enderecoId));
							endereco.setPontoLocalizavel(atracaoTuristica);
							listaEnderecos.add(endereco);

							itemDescricao[i] = nome + "---" + id + "---" + dataAtualizacao + "---" + tipoAtracao + "---" + municipioId + "---" + latitude + "---" + longitude
									+ "---" + logradouro + "---" + numero + "---" + bairro + "---" +  cep + "---" + enderecoId + "---" + descricaoId + "---" + descricao + "---" + idiomaId
									+ "---" + idPolo + "---" + polo + "---" + municipio;

							getMultimidia(id);
							getResponsavel(id,atracaoTuristica);
						}
					}
					getTipoAtracao();
					return itemDescricao;
				} 
				}catch (Exception e) {
					dialog.encerraProgress();
					Toast.makeText(classe, "Erro de conexao", Toast.LENGTH_LONG).show();
					Log.e(JsonActivity.class.getName(), "Deu erro");
					e.printStackTrace();
					return null;
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


		private void getTipoAtracao() {
			listaDeAtracao = new ArrayList<TipoAtracao>();
			String tipoAtracaoJson = tipoAtracaoJson(); 
			try {
				JSONObject jsonObject = new JSONObject(tipoAtracaoJson);
				JSONObject jsonObjectTipo = new JSONObject(jsonObject.getString("tipo"));
				JSONArray jsonArraylistaDeTipoAtracao = new JSONArray(jsonObjectTipo.getString("listaDeTipoAtracao"));
				JSONObject jsonObject3 = jsonArraylistaDeTipoAtracao.getJSONObject(0);
				if(!jsonObject3.getString("tipo_atracao").startsWith("[")){
					JSONObject objectAtracao = new JSONObject(jsonObject3.getString("tipo_atracao"));
					
					
					
					String nome = objectAtracao.getString("nome");
					int id = Integer.parseInt(objectAtracao.getString("id"));
					
					TipoAtracao tipoAtracao = new TipoAtracao();
					tipoAtracao.setId(id);
					tipoAtracao.setAtracao(nome);
					
					listaDeAtracao.add(tipoAtracao);
					
				}else{
					JSONArray jsonReal = new JSONArray(jsonObject3.getString("tipo_atracao"));
					for (int i = 0; i < jsonReal.length(); i++) {

						JSONObject jsonObject4 = jsonReal.getJSONObject(i);
						String nome = jsonObject4.getString("nome");
						int id = Integer.parseInt(jsonObject4.getString("id"));
						
						TipoAtracao tipoAtracao = new TipoAtracao();
						tipoAtracao.setId(id);
						tipoAtracao.setAtracao(nome);
						
						listaDeAtracao.add(tipoAtracao);

					}
			} }catch (Exception e) {
				Log.w(JsonActivity.class.getName(), "Deu erro");
				e.printStackTrace();
			}
		}


		private String tipoAtracaoJson() {
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


		private String atracaoJson() {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			EnderecoServidor es = new EnderecoServidor();
			HttpGet httpGet = new HttpGet(es.getEndereco()+"download/atracoes/"+idMunicipio+"/"+dataUltimaAtualizacao);
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
			if(multimidiaJson != null){
				try {
					JSONObject multimidiaObject = new JSONObject(multimidiaJson);
					multimidiaObject = new JSONObject(multimidiaObject.getString("multimidia"));
					JSONArray jsonArraylistaDeMultimidia = new JSONArray(multimidiaObject.getString("listaDeMultimidia"));
					multimidiaObject = jsonArraylistaDeMultimidia.getJSONObject(0);
					if(!multimidiaObject.getString("multimidia").startsWith("[")){
						JSONObject objectMultimidia = new JSONObject(multimidiaObject.getString("multimidia"));
						
						Multimidia multimidia = new Multimidia();
						
						multimidia.setId(Long.parseLong(objectMultimidia.getString("id")));
						multimidia.setUrl(objectMultimidia.getString("url"));
						multimidia.setItem_descricao(itemId);
						
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
							multimidia.setItem_descricao(itemId);
							
							listaMultimidia.add(multimidia);
						}

					}

				}catch (Exception e) {
					Log.w(JsonActivity.class.getName(), "Deu erro");
					e.printStackTrace();
				}
				Log.w("","DownloadAtracao, 410 - tamanho lista Multimidia - " + listaMultimidia.size());
				if(listaMultimidia.size() > 0){
					DownloadImagem downloadImagem = new DownloadImagem(listaMultimidia);
					downloadImagem.downloadImagem();
				}
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

		private void getResponsavel(String id, AtracaoTuristica atracaoTuristica) {
			int itemId = Integer.parseInt(id);
			String responsavelJson = responsavelJson(itemId);
			if(responsavelJson != null){
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
						
						InfoDoResponsavel responsavel = new InfoDoResponsavel();
						responsavel.setNome(nome);
						responsavel.setId(Long.parseLong(responsavelId));
						responsavel.setEmail(email);
						responsavel.setPontoLocalizavel(atracaoTuristica);
						listaDoResponsavel.add(responsavel);

						JSONArray jsonArraylistaDeTelefone = new JSONArray(objectResponsavel.getString("telefone"));
						JSONObject objectTelefone = jsonArraylistaDeTelefone.getJSONObject(0);
						if(!objectTelefone.getString("telefone").startsWith("[")){
							objectTelefone = new JSONObject(objectTelefone.getString("telefone"));
							telefone = new String [1];
							String telefoneId = objectTelefone.getString("id");
							String codArea= objectTelefone.getString("codArea");
							String numero= objectTelefone.getString("numero");
							telefone[0] = responsavelId + "---" + telefoneId + "---" + codArea + "---" + numero;
							
							Telefone telefone = new Telefone();
							
							telefone.setId(Long.parseLong(telefoneId));
							telefone.setCodArea(codArea);
							telefone.setNumero(numero);
							telefone.setResponsavel(responsavel);
							listaTelefone.add(telefone);

						}else{
							jsonArraylistaDeTelefone = new JSONArray(objectTelefone.getString("telefone"));
							telefone = new String[jsonArraylistaDeTelefone.length()];
							for(int i = 0; i < jsonArraylistaDeTelefone.length(); i ++){
								JSONObject jsonObject5 = jsonArraylistaDeTelefone.getJSONObject(i);
								String telefoneId = jsonObject5.getString("id");
								String codArea= jsonObject5.getString("codArea");
								String numero= jsonObject5.getString("numero");
								telefone[i] = responsavelId + "---" + telefoneId + "---" + codArea + "---" + numero;
								
								Telefone telefone = new Telefone();
								
								telefone.setId(Long.parseLong(telefoneId));
								telefone.setCodArea(codArea);
								telefone.setNumero(numero);
								telefone.setResponsavel(responsavel);
								listaTelefone.add(telefone);
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
							
							InfoDoResponsavel responsavel = new InfoDoResponsavel();
							responsavel.setNome(nome);
							responsavel.setId(Long.parseLong(responsavelId));
							responsavel.setEmail(email);
							responsavel.setPontoLocalizavel(atracaoTuristica);
							listaDoResponsavel.add(responsavel);

							JSONArray jsonArraylistaDeTelefone = new JSONArray(jsonObject5.getString("telefone"));
							JSONObject objectTelefone = jsonArraylistaDeTelefone.getJSONObject(0);
							if(!objectTelefone.getString("telefone").startsWith("[")){
								objectTelefone= new JSONObject(objectTelefone.getString("telefone"));
								telefone = new String [1];
								String telefoneId = objectTelefone.getString("id");
								String codArea= objectTelefone.getString("codArea");
								String numero= objectTelefone.getString("numero");
								telefone[0] = responsavelId + "---" +  telefoneId + "---" + codArea + "---" + numero;
								
								Telefone telefone = new Telefone();
								
								telefone.setId(Long.parseLong(telefoneId));
								telefone.setCodArea(codArea);
								telefone.setNumero(numero);
								telefone.setResponsavel(responsavel);
								listaTelefone.add(telefone);

							}else{
								jsonArraylistaDeTelefone = new JSONArray(objectTelefone.getString("telefone"));
								telefone = new String[jsonArraylistaDeTelefone.length()];
								for(int i = 0; i < jsonArraylistaDeTelefone.length(); i ++){
									JSONObject jsonObject6 = jsonArraylistaDeTelefone.getJSONObject(i);
									String telefoneId = jsonObject6.getString("id");
									String codArea= jsonObject6.getString("codArea");
									String numero= jsonObject6.getString("numero");
									telefone[i] = responsavelId + "---" + telefoneId + "---" + codArea + "---" + numero;
									
									Telefone telefone = new Telefone();
									
									telefone.setId(Long.parseLong(telefoneId));
									telefone.setCodArea(codArea);
									telefone.setNumero(numero);
									telefone.setResponsavel(responsavel);
									listaTelefone.add(telefone);

								}
							}

						}
					}

				}catch (Exception e) {
					Log.w(JsonActivity.class.getName(), "Deu erro");
					e.printStackTrace();
				}

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
		
		@Override
		protected void onPostExecute(String[] result) {
			AtracaoController controller = new AtracaoController();
			controller.salvaAtracao(classe,listaAtracaoTuristicas, listaDeAtracao, listaDoResponsavel, listaMultimidia, listaEnderecos,
					listaTelefone,listaDescricao,idPolo,polo,idMunicipio,municipio);
			dialog.encerraProgress();
			Toast.makeText(classe, "Dados salvos com sucesso!", Toast.LENGTH_LONG).show();
		}
		
		
		

	}
}




