package com.example.backgroundtask.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.backgroundtask.R;
import com.example.backgroundtask.database.DataHelperCityList;
import com.example.backgroundtask.model.CityDetails;
import com.example.backgroundtask.view.WeatherDetails;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {
    ArrayList<CityDetails> cityList;
    Context ctx;
      public static List<LatLng> deletedLatLong=new ArrayList<>();

    public CityAdapter(ArrayList<CityDetails> cityList, Context ctx) {
        this.ctx = ctx;
        this.cityList = cityList;

    }


    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater recyclerInflater=LayoutInflater.from(ctx);
        @SuppressLint("InflateParams") View recyclerView=recyclerInflater.inflate(R.layout.city_list_layout,null);
        return new CityViewHolder(recyclerView);


    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {


        CityDetails cityTemp=cityList.get(position);
        holder.city.setText(cityTemp.getCityName());
        holder.latitude.setText(String.format("%.2f",cityTemp.getLatitude()));
        holder.longitude.setText(String.format("%.2f",cityTemp.getLongitude()));

    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public class CityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
          TextView city,latitude,longitude;

        public CityViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            city=(TextView) itemView.findViewById(R.id.tvCityName);
            latitude=itemView.findViewById(R.id.tvLatitude);
            longitude=itemView.findViewById(R.id.tvLongitude);

        }

        @Override
        public void onClick(View v) {
            int position=getAdapterPosition();
            Intent i=new Intent(ctx, WeatherDetails.class);
            CityDetails temp=cityList.get(position);
            int id=temp.getId();
            i.putExtra("id",id);
            ctx.startActivity(i);

        }


        @Override
        public boolean onLongClick(View v) {
            int position=getAdapterPosition();
            CityDetails temp=cityList.get(position);
            int id=temp.getId();
            LatLng tempLatLong=new LatLng(temp.getLatitude(),temp.getLongitude());
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx, android.R.style.Theme_Material_Light_Dialog_Alert);
            builder.setTitle(" Delete");
            builder.setMessage("Do you Want  Delete Bookmark City?");
            builder.setPositiveButton("yes", (dialog, which) -> {
                DataHelperCityList deleteCity=new DataHelperCityList(ctx);
                boolean delete=deleteCity.deleteCity(id);
                if(delete) {

                    deletedLatLong.add(tempLatLong);
                    cityList.remove(position);
                    notifyItemRemoved(position);
                }
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

       return true;
        }
    }
}
