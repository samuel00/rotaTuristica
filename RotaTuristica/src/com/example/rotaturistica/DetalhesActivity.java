package com.example.rotaturistica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

import com.example.controller.ModeTrasmition;
import com.example.controller.MultimidiaController;
import com.example.entidade.Multimidia;
import com.example.utils.Dialog;
import com.example.utils.EnderecoServidor;
import com.example.utils.GalleryImageAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

@SuppressLint("NewApi")
public class DetalhesActivity extends FragmentActivity {

	private TextView descricaoText;
	private TextView enderecoText;
	ImageView imageView;
	private long id;
	private Dialog dialog;
	String item;
	String [] imagens;
	String [] responsavel;
	String [] telefone;
	List<Multimidia> listaMultimidia;
	
	Gallery gallery;

	private ViewFlipper viewFlipper;
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.detalhes);

		Intent intent = getIntent();
		String descricao = intent.getStringExtra("descricao");
		String endereco = intent.getStringExtra("endereco");
		String atracaoNome = intent.getStringExtra("nome");
		this.item = intent.getStringExtra("item");
		this.id = intent.getLongExtra("idItem",0);
		
		getActionBar().setTitle(atracaoNome +" - Detalhes");

		descricaoText = (TextView) findViewById(R.id.descricao);
		imageView = (ImageView) findViewById(R.id.atracaoImage);
		enderecoText = (TextView) findViewById(R.id.endereco);
		gallery = (Gallery) findViewById(R.id.gallery);

		descricaoText.setText(descricao);
		enderecoText.setText(endereco);

		ModeTrasmition mt = new ModeTrasmition();
		dialog = new Dialog("Aguarde","Carregando dados");
		dialog.iniciaDialog(DetalhesActivity.this);
		if(mt.getModoTransmissao(this) == 1){
			if(isOnline()){
				new MultimidiaTask().execute();
			}
		}
		else{
			MultimidiaController controller= new MultimidiaController();
			
			listaMultimidia = controller.getMultimidiaByItemDescricao(this,this.id);
			
			if(listaMultimidia.size() > 0){
				gallery.setSpacing(1);
				gallery.setAdapter(new GalleryImageAdapter(DetalhesActivity.this,listaMultimidia));
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

	public void verNoMapa(View view){
		if(view.getId() == R.id.mapaButton){
			if(this.item == null){
				Intent intent = getIntent();
				long idServico = intent.getLongExtra("idItem",0);
				long idTipoServico = intent.getLongExtra("idTipo", 0);
				double latitude = intent.getDoubleExtra("latitude", 0);
				double longitude = intent.getDoubleExtra("longitude", 0);
				String atracaoNome = intent.getStringExtra("nome");
				int icone = intent.getIntExtra("icone", 0);

				Intent intent2 = new Intent(this, Mapa.class);
				intent2.putExtra("idItem",idServico);
				intent2.putExtra("latitude",latitude);
				intent2.putExtra("longitude",longitude);
				intent2.putExtra("idTipo", idTipoServico);
				intent2.putExtra("nome", atracaoNome);
				intent2.putExtra("icone", icone);
				startActivity(intent2);
			}else{
				Intent intent = getIntent();
				long idServico = intent.getLongExtra("idItem",0);
				String atracaoNome = intent.getStringExtra("nome");
				int icone = intent.getIntExtra("icone", 0);
				long idTipo = intent.getLongExtra("idTipo",0);
				Intent intent2 = new Intent(this, RouteMapa.class);
				intent2.putExtra("idItem",idServico);
				intent2.putExtra("idTipo",idTipo);
				intent2.putExtra("nome", atracaoNome);
				intent2.putExtra("icone", icone);
				startActivity(intent2);
			}
		}
	}	
	
	private class MultimidiaTask extends AsyncTask<String, Void, String[]>{

		@Override
		protected String[] doInBackground(String... params) {
			String multimidiaJson = multimidiaJson(id);
			String [] url;
			try {
				listaMultimidia = new ArrayList<Multimidia>();
				JSONObject multimidiaObject = new JSONObject(multimidiaJson);
				multimidiaObject = new JSONObject(multimidiaObject.getString("multimidia"));
				JSONArray jsonArraylistaDeMultimidia = new JSONArray(multimidiaObject.getString("listaDeMultimidia"));
				multimidiaObject = jsonArraylistaDeMultimidia.getJSONObject(0);
				if(!multimidiaObject.getString("multimidia").startsWith("[")){
					JSONObject multimidia = new JSONObject(multimidiaObject.getString("multimidia"));
					url = new String[1];
					String multimidiaUrl = multimidia.getString("url");
					url[0] = multimidiaUrl;
					
					Multimidia multimidias = new Multimidia();
					multimidias.setUrl(multimidiaUrl);
					
					listaMultimidia.add(multimidias);
				}else{
					jsonArraylistaDeMultimidia = new JSONArray(multimidiaObject.getString("multimidia"));
					url = new String[jsonArraylistaDeMultimidia.length()];
					for(int j =0; j < jsonArraylistaDeMultimidia.length(); j++){
						JSONObject jsonObject5 = jsonArraylistaDeMultimidia.getJSONObject(j);
						String multimidiaUrl = jsonObject5.getString("url");
						url[j] = multimidiaUrl;
						
						Multimidia multimidias = new Multimidia();
						multimidias.setUrl(multimidiaUrl);
						
						listaMultimidia.add(multimidias);
					}

				}
				return url;

			}catch (Exception e) {
				Log.w(JsonActivity.class.getName(), "Deu erro");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			if(result != null){
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy); 
				gallery.setSpacing(1);
				gallery.setAdapter(new GalleryImageAdapter(DetalhesActivity.this,listaMultimidia));
			}
			dialog.encerraDialog();
		}

		private String multimidiaJson(long id) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			EnderecoServidor es = new EnderecoServidor();
			HttpGet httpGet = new HttpGet(es.getEndereco()+"multimidias/"+id);
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