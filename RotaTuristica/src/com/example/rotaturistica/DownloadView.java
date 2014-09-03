package com.example.rotaturistica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.controller.AtracaoController;
import com.example.controller.ModeTrasmition;
import com.example.controller.ServicoController;
import com.example.dao.ServicoTuristicoDao;
import com.example.download.DownloadAtracao;
import com.example.download.DownloadServico;
import com.example.utils.Dialog;
import com.example.utils.EnderecoServidor;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadView extends FragmentActivity {
	private Dialog dialog;
	private String item;
	private long idMunicipio;
	private long idPolo;
	private String polo;
	private String municipio;
	private long dataUltimaAtualizacao;
	
	private TextView descricao;
	private TextView local;
	private TextView mcp;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		
		Intent intent = getIntent();
		idPolo = intent.getLongExtra("idPolo",0);
		polo = intent.getStringExtra("polo");
		idMunicipio = intent.getLongExtra("idMunicipio",0);
		municipio = intent.getStringExtra("municipio");
		item = intent.getStringExtra("item");
		
		ModeTrasmition mt = new ModeTrasmition();
		if(mt.getModoTransmissao(this) == 1){
			if(isOnline()){
				dialog = new Dialog("Aguarde","Carregando dados");
				dialog.iniciaDialog(DownloadView.this);
				new ComparaDados().execute();
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
	
	public void atualizar(View view){
		if(item.equalsIgnoreCase("service")){
			DownloadServico down = new DownloadServico();
			down.salvaServico(DownloadView.this,dataUltimaAtualizacao,idPolo,polo,municipio,idMunicipio);
		}else
			if(item.equalsIgnoreCase("attractive")){
				DownloadAtracao down = new DownloadAtracao();
				down.salvaAtracao(DownloadView.this,dataUltimaAtualizacao,idPolo,polo,municipio,idMunicipio);
			}else
				if(item.equalsIgnoreCase("route")){
					
				}
	}
	
	private class ComparaDados extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String contador = null;
			try {
				if(item.equalsIgnoreCase("service")){
					String item = "servicos";
					dataUltimaAtualizacao = dataUltimaAtualizacao(DownloadView.this);
					String contadorJson = contadorJson(dataUltimaAtualizacao,item);
					JSONObject jsonObject = new JSONObject(contadorJson);
					jsonObject = new JSONObject(jsonObject.getString("servico"));
					contador = jsonObject.getString("contador");
				}else
					if(item.equalsIgnoreCase("attractive")){
						String item = "atracoes";
						dataUltimaAtualizacao = dataUltimaAtualizacao(DownloadView.this);
						String contadorJson = contadorJson(dataUltimaAtualizacao,item);
						JSONObject jsonObject = new JSONObject(contadorJson);
						jsonObject = new JSONObject(jsonObject.getString("atracao"));
						contador = jsonObject.getString("contador");
					}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return contador;
		}

		private String contadorJson(long dataUltimaAtualizacao, String item) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			EnderecoServidor es = new EnderecoServidor();
			HttpGet httpGet = new HttpGet(es.getEndereco()+item+"/contador/"+idMunicipio+"/"+dataUltimaAtualizacao);
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

		private long dataUltimaAtualizacao(DownloadView downloadView) {
			if(item.equalsIgnoreCase("service")){
				ServicoController controller = new ServicoController();
				long data = controller.dataUltimaAtualizacao(downloadView,idMunicipio);
				return data;
			}else
				if(item.equalsIgnoreCase("attractive")){
					AtracaoController controller = new AtracaoController();
					long data = controller.dataUltimaAtualizacao(downloadView,idMunicipio);
					return data;
				}
			return 0;
		}
		
		protected void onPostExecute(String result) {
			if(result != null){
				setContentView(R.layout.download_view);
				dialog.encerraDialog();
				long contadorServidor = Long.parseLong(result);
				long contadorLocal = 0;
				
				if(item.equalsIgnoreCase("service")){
					ServicoController controller = new ServicoController();
					contadorLocal = controller.contadorDeServico(DownloadView.this,idMunicipio,dataUltimaAtualizacao);
				}else
					if(item.equalsIgnoreCase("attractive")){
						AtracaoController controller = new AtracaoController();
						contadorLocal = controller.contadorDeAtracao(DownloadView.this,idMunicipio,dataUltimaAtualizacao);
					}
				
				
				long a = contadorServidor + contadorLocal;
				
				long resultado = (contadorLocal*100)/a;
				
				resultado = 100 - resultado;
				
				local = (TextView) findViewById(R.id.local);
				descricao = (TextView) findViewById(R.id.descricao);
				mcp = (TextView) findViewById(R.id.municipio);
				
				if(resultado > 0){
					mcp.setText(municipio);
					descricao.setText("Você precisa atualizar essa cidade no aplicativo");
					local.setText("A cidade está - " + resultado + "% desatualizada");
				}else{
					mcp.setText(municipio);
					descricao.setText("Você não precisa  atualizar essa cidade no aplicativo");
					local.setText("A cidade está atualizada");
				}
				
				
				
			}
		}
		
	}

}
