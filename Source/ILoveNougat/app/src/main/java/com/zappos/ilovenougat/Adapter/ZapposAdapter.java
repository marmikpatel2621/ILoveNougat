package com.zappos.ilovenougat.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zappos.ilovenougat.Activity.ZapposActivity;
import com.zappos.ilovenougat.Model.ZapposProduct;
import com.zappos.ilovenougat.R;

import java.util.ArrayList;

/**
 * Created by user on 10/09/2016.
 */
public class ZapposAdapter extends RecyclerView.Adapter<ZapposAdapter.MyViewHolder>{

    private ArrayList<ZapposProduct> dataSet;
    private Context c;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewVersion;
        ImageView imageViewIcon;
        TextView originalPrice;
        TextView off;
        TextView price;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.textViewVersion = (TextView) itemView.findViewById(R.id.textViewVersion);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);
            this.off = (TextView)itemView.findViewById(R.id.off);
            this.originalPrice = (TextView)itemView.findViewById(R.id.originalPrice);
            this.price = (TextView)itemView.findViewById(R.id.zappoprice);
        }
    }

    public ZapposAdapter(Context c,ArrayList<ZapposProduct> z) {
        this.dataSet=z;
        this.c = c;
    }

        public void setDataSet(ArrayList<ZapposProduct> dataSet)
        {
            this.dataSet = dataSet;
          //  notifyItemRangeChanged(0,dataSet.size());
        }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_layout, parent, false);

        view.setOnClickListener(ZapposActivity.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView textViewName = holder.textViewName;
        TextView textViewVersion = holder.textViewVersion;
        ImageView imageView = holder.imageViewIcon;
        TextView originalPrice = holder.originalPrice;
        TextView off = holder.off;
        TextView price = holder.price;
        textViewName.setText(dataSet.get(listPosition).getProductName());
        textViewVersion.setText(dataSet.get(listPosition).getBrandName());
        Picasso.with(c).load(dataSet.get(listPosition).getImageURL()).into(holder.imageViewIcon);
        originalPrice.setText(dataSet.get(listPosition).getOriginalPrice());
        off.setText(dataSet.get(listPosition).getOff());
        price.setText(dataSet.get(listPosition).getPrice());
    }

    @Override
    public int getItemCount() {

//        Log.d("hiiii","hhii" +dataSet.size());
        return dataSet.size();
    }
}