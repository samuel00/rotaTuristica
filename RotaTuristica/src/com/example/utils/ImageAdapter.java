//package com.example.utils;
//
//import com.example.rotaturistica.R;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//
//public class ImageAdapter  extends BaseAdapter
//{
//    private Context context;
//    private int itemBackground;
//     public ImageAdapter(Context c)
//     {
//        context = c;
//        //---setting the style---
//        TypedArray a = obtainStyledAttributes(R.styleable.Gallery1);
//        itemBackground = a.getResourceId(  R.styleable.Gallery1_android_galleryItemBackground, 0);
//        a.recycle();
//     }
//     //---returns the number of images---
//     public int getCount()
//     {
//         return imageIDs.length;
//     }
//         //---returns the ID of an item---
//     public Object getItem(int position)
//     {
//         return position;
//     }
//     //---returns the ID of an item---
//     public long getItemId(int position)
//     {
//         return position;
//     }
// @Override   //---returns an ImageView view---
//  public View getView(int position, View convertView, ViewGroup parent)
//  {
//  ImageView imageView = new ImageView(context);
//  imageView.setImageResource(imageIDs[position]);
//  imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//  imageView.setLayoutParams(new Gallery.LayoutParams(150, 120));
//  imageView.setBackgroundResource(itemBackground);
//
//  return imageView;
//  }
//}