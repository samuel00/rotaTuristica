package com.example.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.example.controller.ModeTrasmition;
import com.example.entidade.Multimidia;
import com.example.rotaturistica.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class GalleryImageAdapter extends BaseAdapter{
	private Context mContext;

	private List<Multimidia> listaMultimidia;

	public GalleryImageAdapter(Context context, List<Multimidia> listaDeMultimidia)
	{
		mContext = context;
		this.listaMultimidia = listaDeMultimidia;
	}

	public int getCount() {
		return this.listaMultimidia.size();

	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}


	// Override this method according to your need
	public View getView(int index, View view, ViewGroup viewGroup)
	{

		ImageView i = new ImageView(mContext);

		String enderecoImagem = listaMultimidia.get(index).getUrl();


		ModeTrasmition mt = new ModeTrasmition();
		if(mt.getModoTransmissao(mContext) == 1){
			URL url;
			try {
				url = new URL(enderecoImagem);

				Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

				i.setImageBitmap(bmp);
				i.setLayoutParams(new Gallery.LayoutParams(200, 200));

				i.setScaleType(ImageView.ScaleType.FIT_XY);

			} catch (MalformedURLException e) {
				e.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();
			}
			return i;

		}else{
			File f = new File(enderecoImagem);
			Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
			i.setImageBitmap(bmp);
			i.setLayoutParams(new Gallery.LayoutParams(200, 200));

			i.setScaleType(ImageView.ScaleType.FIT_XY);
			return i;
		}

	}
}
