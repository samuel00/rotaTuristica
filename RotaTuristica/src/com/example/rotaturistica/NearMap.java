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

import com.example.controller.AtracaoController;
import com.example.controller.ModeTrasmition;
import com.example.controller.ServicoController;
import com.example.entidade.PontoLocalizavel;
import com.example.utils.Dialog;
import com.example.utils.EnderecoServidor;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("NewApi")
public class NearMap extends FragmentActivity implements LocationListener{
	
	private long idTipo;
	private String tipoItem;
	private  LatLng VOCE;
	private LatLng DESTINO;
	private int icone;
	private boolean pesquisar = true;

	private List<PontoLocalizavel> listaDePontoLocalizavel;

	private GoogleMap map;
	private LocationManager locationManager;
	private ProgressDialog dialog;
	private Marker voceMarker;
	private long idMunicipio;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapa);
		SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		map = mapFrag.getMap();
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		Intent getIntent = getIntent();
		idTipo = getIntent.getLongExtra("idTipo",0);
		tipoItem = getIntent.getStringExtra("item");
		icone = getIntent.getIntExtra("icone", 0);
		idMunicipio = getIntent.getLongExtra("idMunicipio",0);
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

		getActionBar().setTitle(tipoItem + " Próximo de mim");


		ModeTrasmition mt = new ModeTrasmition();
		if(mt.getModoTransmissao(this) == 1){
			if(isOnline()){
				new AtracaoProximaTask().execute();
			}
		}else{
			if(tipoItem.equalsIgnoreCase("atracao")){
				AtracaoController controller = new AtracaoController();
				listaDePontoLocalizavel = controller.getPontosLocalizaveis(this, idTipo);
			}else
				if(tipoItem.equalsIgnoreCase("servico")){
					ServicoController controller = new ServicoController();
					listaDePontoLocalizavel = controller.getPontosLocalizaveis(this, idTipo);
				}




				dialog = new ProgressDialog(this);
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				dialog.setTitle("Aguarde");
				dialog.setMessage("Pesquisando ponto");
				dialog.setCancelable(false);
				dialog.setMax(800);
				dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						pesquisar = false;
					}
				});
				dialog.show();
				
				

				if(pesquisar){

					locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

					Listener listener = new Listener();

					long tempoAtualizacao = 0;
					float distancia = 0;

					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, tempoAtualizacao, distancia, listener);

					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, tempoAtualizacao, distancia, listener);
				}
			}

	}

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();
		    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
		        return true;
		    }
		    return false;
	}

	private void addMarkers(Listener listener) {
		
		
		Location locationA = new Location("A");
		locationA.setLatitude(VOCE.latitude);
		locationA.setLongitude(VOCE.longitude);

		if (map != null) {
			BitmapDescriptor iconVoce = BitmapDescriptorFactory.fromResource(R.drawable.tourist);
			BitmapDescriptor iconDestino = BitmapDescriptorFactory.fromResource(icone);
			dialog.cancel();
			int pontoProximo = 0;

			if(voceMarker != null)
				voceMarker.remove();

			voceMarker = map.addMarker(new MarkerOptions().position(VOCE).title("Você").icon(iconVoce));
			for(int i=0; i < listaDePontoLocalizavel.size(); i++){
				Location locationB = new Location("B");
				locationB.setLatitude(listaDePontoLocalizavel.get(i).getLatitude());
				locationB.setLongitude(listaDePontoLocalizavel.get(i).getLongitude());
				if(locationA.distanceTo(locationB) <= 1000){
					DESTINO = new LatLng(listaDePontoLocalizavel.get(i).getLatitude(), listaDePontoLocalizavel.get(i).getLongitude());
					map.addMarker(new MarkerOptions().position(DESTINO).title(listaDePontoLocalizavel.get(i).getNome()).icon(iconDestino));
					pontoProximo ++;
				}
			}

			if(pontoProximo == 0){
				Toast.makeText(getApplicationContext(), "Não há " + tipoItem + " próximo (a)", Toast.LENGTH_LONG).show();
			}
			locationManager.removeUpdates(listener);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}
	
	private class AtracaoProximaTask extends AsyncTask<String, Void, String[]>{

		@Override
		protected String[] doInBackground(String... params) {
			try {
				String [] json;
				if(tipoItem.equalsIgnoreCase("atracao")){
					String atracaoJson = atracaoJson(idTipo);

					if(atracaoJson != null){
						JSONObject jsonObject = new JSONObject(atracaoJson);
						JSONObject jsonObjectTipo = new JSONObject(jsonObject.getString("atracao"));
						JSONArray jsonArraylistaDeTipoAtracao = new JSONArray(jsonObjectTipo.getString("listaDeAtracaoTuristica"));
						JSONObject jsonObject3 = jsonArraylistaDeTipoAtracao.getJSONObject(0);
						if(!jsonObject3.getString("atracao_turistica").startsWith("[")){
							JSONObject objectAtracao = new JSONObject(jsonObject3.getString("atracao_turistica"));
							json = new String[1];

							String nome = objectAtracao.getString("nome");
							String id = objectAtracao.getString("id");
							String latitude = objectAtracao.getString("latitude");
							String longitude = objectAtracao.getString("longitude");

							json[0] = nome + "---" + id +  "---" + latitude + "---" + longitude;



						}else{
							JSONArray jsonReal = new JSONArray(jsonObject3.getString("atracao_turistica"));
							json = new String[jsonReal.length()];
							for (int i = 0; i < jsonReal.length(); i++) {

								JSONObject jsonObject4 = jsonReal.getJSONObject(i);
								String nome = jsonObject4.getString("nome");
								String id = jsonObject4.getString("id");
								String latitude = jsonObject4.getString("latitude");
								String longitude = jsonObject4.getString("longitude");

								json[i] = nome + "---" + id +  "---" + latitude + "---" + longitude;
							}
						}

						return json;
					} 

				}
				else
					if(tipoItem.equalsIgnoreCase("servico")){
						String servicoJson = servicoJson(idTipo);

						if(servicoJson != null){
							JSONObject jsonObject = new JSONObject(servicoJson);
							jsonObject = new JSONObject(jsonObject.getString("servico"));
							JSONArray jsonArraylistaDeTipoAtracao = new JSONArray(jsonObject.getString("listaDeServicoTuristico"));
							jsonObject = jsonArraylistaDeTipoAtracao.getJSONObject(0);
							if(!jsonObject.getString("servico_turistico").startsWith("[")){
								JSONObject objectServico = new JSONObject(jsonObject.getString("servico_turistico"));
								json = new String[1];

								String nome = objectServico.getString("nome");
								String id = objectServico.getString("id");
								String latitude = objectServico.getString("latitude");
								String longitude = objectServico.getString("longitude");



								json[0] = nome + "---" + id +  "---" + latitude + "---" + longitude;

							}
							else{
								JSONArray jsonReal = new JSONArray(jsonObject.getString("servico_turistico"));
								json = new String[jsonReal.length()];
								for (int i = 0; i < jsonReal.length(); i++) {

									JSONObject jsonObject4 = jsonReal.getJSONObject(i);
									String nome = jsonObject4.getString("nome");
									String id = jsonObject4.getString("id");
									String latitude = jsonObject4.getString("latitude");
									String longitude = jsonObject4.getString("longitude");

									json[i] = nome + "---" + id +  "---" + latitude + "---" + longitude;
								}
							}
							return json;
						} 

					}}catch (Exception e) {
						Log.e(JsonActivity.class.getName(), "Deu erro");
						e.printStackTrace();
					}
			return null;
		}

		private String servicoJson(long idTipo) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			EnderecoServidor es = new EnderecoServidor();
			HttpGet httpGet = new HttpGet(es.getEndereco()+"servicos/"+idTipo+"/"+idMunicipio);
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

		private String atracaoJson(long idTipo) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			EnderecoServidor es = new EnderecoServidor();
			HttpGet httpGet = new HttpGet(es.getEndereco()+"atracoes/"+idTipo+"/"+ idMunicipio);
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
			
			listaDePontoLocalizavel = new ArrayList<PontoLocalizavel>();
			
			for(int i = 0; i < result.length; i++){
				String[] split = result[i].split("---");
				String nome = split[0];
				long id = Long.parseLong(split[1]);
				double latitude = Double.parseDouble(split[2]);
				double longitude = Double.parseDouble(split[3]);
				PontoLocalizavel ponto = new PontoLocalizavel();
				
				ponto.setNome(nome);
				ponto.setId(id);
				ponto.setLatitude(latitude);
				ponto.setLongitude(longitude);
				
				listaDePontoLocalizavel.add(ponto);
			}
			
			if(!listaDePontoLocalizavel.isEmpty()){
				dialog = new ProgressDialog(NearMap.this);
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				dialog.setTitle("Aguarde");
				dialog.setMessage("Pesquisando ponto");
				dialog.setCancelable(false);
				dialog.setMax(800);
				dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						pesquisar = false;
					}
				});
				
				dialog.show();
				
				

				if(pesquisar){

					locationManager = (LocationManager) NearMap.this.getSystemService(Context.LOCATION_SERVICE);

					Listener listener = new Listener();

					long tempoAtualizacao = 0;
					float distancia = 0;

					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, tempoAtualizacao, distancia, listener);

					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, tempoAtualizacao, distancia, listener);
				}
				
			}else{
				Toast.makeText(getApplicationContext(), "Lista vazia", Toast.LENGTH_LONG).show();
			}
			
}

		
	}

	private class Listener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			VOCE = new LatLng(location.getLatitude(), location.getLongitude());
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(VOCE,14));
			dialog.dismiss();
			addMarkers(this);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

	}

}
