package com.apress.gerber.gradleweather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clifton
 * Copyright 8/27/2014.
 */
public class TemperatureAdapter extends BaseAdapter {
    private final Context context;
    private final ImageLoader imageLoader;
    List<TemperatureItem>items;

    public TemperatureAdapter(Context context, ImageLoader imageLoader) {
        this.context = context;
        this.imageLoader = imageLoader;
        this.items = new ArrayList<TemperatureItem>();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView != null ? convertView : createView(parent);
        TemperatureItem temperatureItem = items.get(position);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageIcon);
        imageView.setImageDrawable(temperatureItem.getImageDrawable());
        if(temperatureItem.getIconLink()!=null){
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.progress_animation);
            animation.setInterpolator(new LinearInterpolator());
            imageView.startAnimation(animation);
            ((ViewHolder) view.getTag()).setIconLink(temperatureItem.getIconLink());
        }
        ((TextView) view.findViewById(R.id.dayTextView)).setText(temperatureItem.getDay());
        ((TextView) view.findViewById(R.id.briefForecast)).setText(temperatureItem.getForecast());
        ((TextView) view.findViewById(R.id.description)).setText(temperatureItem.getDescription());
        return view;
    }

    class ViewHolder {
        private final View view;
        private String iconLink;
        private AsyncTask<String, Integer, Bitmap> asyncTask;

        public ViewHolder(View view) {
            this.view = view;
        }

        public void setIconLink(String iconLink) {
            final ImageView imageView = (ImageView) view.findViewById(R.id.imageIcon);
            imageLoader.displayImage(iconLink, imageView, new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    imageView.clearAnimation();
                    super.onLoadingComplete(imageUri, view, loadedImage);
                }
            });
        }
    }
    private View createView(ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflatedView = inflater.inflate(R.layout.temperature_summary, parent, false);
        inflatedView.setTag(new ViewHolder(inflatedView));
        return inflatedView;
    }

    public void setTemperatureData(TemperatureData temperatureData) {
        items = temperatureData.getTemperatureItems();
        notifyDataSetChanged();
    }
}
