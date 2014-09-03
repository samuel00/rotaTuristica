package com.example.rotaturistica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.example.entidade.ServicoTuristico;
import com.example.entidade.TipoServico;
import com.example.utils.Dialog;
import com.example.utils.EnderecoServidor;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ServicoListActivity extends ListActivity implements OnItemClickListener{

	private List<Map<String, Object>> servicos ;
	private Dialog dialog;
	private long idMunicipio;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		idMunicipio = intent.getLongExtra("idMunicipio",0);
		
		ModeTrasmition mt = new ModeTrasmition();
		
		dialog = new Dialog("Aguarde","Carregando dados");
		dialog.iniciaDialog(ServicoListActivity.this);
		
		if(mt.getModoTransmissao(this) == 1){
			if(isOnline()){
				new TipoServicoTask().execute();
			}
		}
		else{
			if(montaView()){
				String[] from = {"icone", "servico", "id", "idTipoServico"};
				int[] to = {R.id.tipoServico, R.id.servico};
				SimpleAdapter adapter =	new SimpleAdapter(ServicoListActivity.this, servicos,R.layout.lista_servico, from, to);
				setListAdapter(adapter);
				getListView().setOnItemClickListener(ServicoListActivity.this);
				dialog.encerraDialog();
			}else
				dialog.encerraDialog();
				Toast.makeText(getApplicationContext(), "Não há tipo de servicos turísticos.", Toast.LENGTH_LONG).show();
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
		ServicoController controller = new ServicoController();
		
		List<TipoServico> listaDeServico = new ArrayList<TipoServico>();
		
		listaDeServico = controller.getTipoServico(this);
		
		servicos = new ArrayList<Map<String,Object>>();
		
		if(listaDeServico != null){
			for(int i=0; i < listaDeServico.size(); i++){
				
				Map<String, Object> item = new HashMap<String, Object>();
				
				item.put("icone", R.drawable.service);

				item.put("servico", listaDeServico.get(i).getServico());
				item.put("idTipoServico", listaDeServico.get(i).getId());
				item.put("id", listaDeServico.get(i).getId());
				servicos.add(item);
			}
			return true;
		}else{
			return false;
		}
		
	}




	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		Map<String, Object> map = servicos.get(position);
		long idServico = (Long) map.get("id");
		long idTipoServico =  (Long) map.get("idTipoServico");
		int icone = (Integer) map.get("icone");
		String servico = (String) map.get("servico");
		Intent i=new Intent(this, ServicoSelecionadoActivity.class);
		i.putExtra("idServico",idServico);
		i.putExtra("idTipo",idTipoServico);
		i.putExtra("icone",icone);
		i.putExtra("servico", servico);
		i.putExtra("idMunicipio", idMunicipio);
		startActivity(i);


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
				if(!jsonObject3.getString("tipo_servico").startsWith("[")){
					JSONObject objectServico = new JSONObject(jsonObject3.getString("tipo_servico"));

					String [] json = new String[1];

					String nome = objectServico.getString("nome");
					int id = Integer.parseInt(objectServico.getString("id"));
					json[0] = nome + "---" + id;

					return json;

				}else{
					JSONArray jsonReal = new JSONArray(jsonObject3.getString("tipo_servico"));
					String [] json = new String[jsonReal.length()];
					for (int i = 0; i < jsonReal.length(); i++) {

						JSONObject jsonObject4 = jsonReal.getJSONObject(i);
						String nome = jsonObject4.getString("nome");
						int id = Integer.parseInt(jsonObject4.getString("id"));
						json[i] = nome + "---" + id;

					}
					return json;
				}
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
			HttpGet httpGet = new HttpGet(es.getEndereco()+"tipo_servicos/"+idMunicipio);
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
				

				SimpleAdapter adapter =	new SimpleAdapter(ServicoListActivity.this, montaView(result),R.layout.lista_servico, from, to);
				setListAdapter(adapter);
				getListView().setOnItemClickListener(ServicoListActivity.this);
				dialog.encerraDialog();
			}else{
				Toast.makeText(getApplicationContext(), "Não há tipo de servicos turísticos.", Toast.LENGTH_LONG).show();
			}
		}

		private List<? extends Map<String, ?>> montaView(String[] result) {
			servicos = new ArrayList<Map<String,Object>>();
			for(int i=0; i < result.length; i++){
				String[] parts = result[i].split("---");
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
