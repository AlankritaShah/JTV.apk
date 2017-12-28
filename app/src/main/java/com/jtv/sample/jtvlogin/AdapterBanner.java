package com.jtv.sample.jtvlogin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AdapterBanner extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<DataFish> data= Collections.emptyList();

    // create constructor to initilize context and data sent from MainActivity
    public AdapterBanner(Context context, List<DataFish> data){
        this.context=context;
        inflater= LayoutInflater.from(context);
        this.data=data;
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.container_banner, parent,false);
        MyHolder holder=new MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        MyHolder myHolder= (MyHolder) holder;
        final DataFish current=data.get(position);

        Picasso.with(context) //Context
                .load(current.fishImage) //URL/FILE
                .into(myHolder.imview2);

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Log.e("url", current.videourl);
                // Get the position
                Intent intent = new Intent(context, SingleItemView.class);
                intent.putExtra("assetid",current.catName);
                intent.putExtra("mname",current.fishName);
                intent.putExtra("videourl",current.videourl);

                // Start SingleItemView Class
                context.startActivity(intent);

            }
        });

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }
    
     class MyHolder extends RecyclerView.ViewHolder{

        ImageView imview2;

        // create constructor to get widget reference
        public MyHolder(final View itemView) {
            super(itemView);
            imview2 = (ImageView) itemView.findViewById(R.id.imview2);

        }

    }

}