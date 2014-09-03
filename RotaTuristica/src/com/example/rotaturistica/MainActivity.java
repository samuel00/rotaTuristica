package com.example.rotaturistica;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.controller.ServicoController;
import com.example.dao.Conexao;
import com.example.entidade.ItemDescricao;
import com.example.entidade.PontoLocalizavel;
import com.example.entidade.ServicoTuristico;
import com.google.android.gms.internal.fn;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends Activity {

	public static final long MINUTE = 60000;
	public static final long INTERVAL_CAPTURE_IN_MILIS = 2 * MINUTE;
	public static final long INTERVAL_PHYSICAL_SPACE_IN_METERS = 100;
	public static final int progress_bar_type = 0; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		
		Log.w("", "Início! ");
//		new GetCityTask().execute();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.configuracao, menu);
		return true;
	} 

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.setting:
			Intent intent= new Intent(this, ConfiguracaoActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	public void exibirServico(View view){
		if(view.getId() == R.id.servicos){
			Intent intent = new Intent(this, PoloListActivity.class);
			intent.putExtra("item","servico");
			startActivity(intent);

		}
	}

	public void exibirAtracao(View view){
		if(view.getId() == R.id.atracoes){
			Intent intent = new Intent(this, PoloListActivity.class);
			intent.putExtra("item","atracao");
			startActivity(intent);
		}
	}

	public void exibirRota(View view){
		if(view.getId() == R.id.rotas){
			Intent intent = new Intent(this, RotaListActivity.class);
			intent.putExtra("item","rota");
			startActivity(intent);
		}
	}
	
	private class GetCityTask extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			try {
				LatLng VOCE = new LatLng(-1.457816, -48.491655);
				for(int i =0; i < 2; i++){
					Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
				    Address address;
				    List<Address> list;
				    list = geocoder.getFromLocation(VOCE.latitude/ 1E6,VOCE.longitude/ 1E6,1);//33.64600, 72.96115
				    address = list.get(0);
				    String cityname =  address.getLocality();
				    String countryname =   address.getCountryName();        
				    Log.w("cityname",cityname.toString());
				    Log.w("countryname",countryname.toString());
				}
			    return "";
			} catch (IOException e) {
				Log.w("", e.getMessage());
				
			}
			  catch (NullPointerException e) {
				  Log.w("", "erro 2 ");
			  }
			return null;
		}
		
		protected void onPostExecute(String result) {
			Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
		}
		
	}
}

