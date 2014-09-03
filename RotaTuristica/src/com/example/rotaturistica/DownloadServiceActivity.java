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
import com.example.controller.ServicoController;
import com.example.entidade.TipoServico;
import com.example.utils.Dialog;
import com.example.utils.EnderecoServidor;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
@SuppressLint("NewApi")
public class DownloadServiceActivity extends ListActivity implements OnItemClickListener {
	
	private List<Map<String, Object>> servicos ;
	public static String tipoServicoJson;
	private Dialog dialog;
	private long idPolo;
	private String polo;
	private long idMunicipio;
	private String municipio;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		idPolo = intent.getLongExtra("idPolo",0);
		polo = intent.getStringExtra("polo");
		idMunicipio = intent.getLongExtra("idMunicipio",0);
		municipio = intent.getStringExtra("polo");
		
		ModeTrasmition mt = new ModeTrasmition();
		
		if(mt.getModoTransmissao(this) == 1){
			if(isOnline()){
				dialog = new Dialog("Aguarde","Carregando dados");
				dialog.iniciaDialog(DownloadServiceActivity.this);
				new TipoServicoTask().execute();
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
		Map<String, Object> map = servicos.get(position);
		long idTipoAtracao =  (Long) map.get("idTipoServico");
		String item = "service";
		String servico = (String) map.get("servico");
		Intent i=new Intent(this, DownloadListItemActivity.class);
		i.putExtra("idTipo",idTipoAtracao);
		i.putExtra("tipoItem",servico);
		i.putExtra("item",item);
		i.putExtra("idPolo", idPolo);
		i.putExtra("polo", polo);
		i.putExtra("idMunicipio", idMunicipio);
		i.putExtra("municipio", municipio);
		ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight());
		startActivity(i, options.toBundle());
	}

	private class TipoServicoTask extends AsyncTask<String, Void, String[]>{

		@Override
		protected String[] doInBackground(String... params) {
			String servicoSelecionadoJson = servicoSelecionadoJson(); 
			try {
				JSONObject jsonObject = new JSONObject(servicoSelecionadoJson);
				JSONObject jsonObjectTipo = new JSONObject(jsonObject.getString("tipo"));
				JSONArray jsonArraylistaDeTipoAtracao = new JSONArray(jsonObjectTipo.getString("listaDeTipoServico"));
				JSONObject jsonObject3 = jsonArraylistaDeTipoAtracao.getJSONObject(0);
				JSONArray jsonReal = new JSONArray(jsonObject3.getString("tipo_servico"));
				String [] json = new String[jsonReal.length()];
				for (int i = 0; i < jsonReal.length(); i++) {

					JSONObject jsonObject4 = jsonReal.getJSONObject(i);
					String nome = jsonObject4.getString("nome");
					int id = Integer.parseInt(jsonObject4.getString("id"));
					json[i] = nome + "-" + id;

				}
				return json;
			} catch (Exception e) {
				Log.w(JsonActivity.class.getName(), "Deu erro");
				e.printStackTrace();
			}
			return null;
		}

		private String servicoSelecionadoJson() {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			EnderecoServidor es = new EnderecoServidor();
			HttpGet httpGet = new HttpGet(es.getEndereco()+"tipo_servicos");
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
				String[] from = {"icone", "servico", "id", "idTipoServico"};
				int[] to = {R.id.tipoServico, R.id.servico};
				

				SimpleAdapter adapter =	new SimpleAdapter(DownloadServiceActivity.this, montaView(result),R.layout.lista_servico, from, to);
				setListAdapter(adapter);
				getListView().setOnItemClickListener(DownloadServiceActivity.this);
				dialog.encerraDialog();
			}else{
				Toast.makeText(getApplicationContext(), "Não há tipo de servicos turísticos.", Toast.LENGTH_LONG).show();
			}
			
		}

		private List<? extends Map<String, ?>> montaView(String[] result) {
			servicos = new ArrayList<Map<String,Object>>();
			for(int i=0; i < result.length; i++){
				String[] parts = result[i].split("-");
				String nome = parts[0];
				long id = Long.parseLong(parts[1]);
				Map<String, Object> item = new HashMap<String, Object>();

				
				item.put("icone", R.drawable.service);

				item.put("servico", nome);
				item.put("idTipoServico", id);
				item.put("id", id);
				servicos.add(item);
			}
			return servicos;
		}

	}

}
