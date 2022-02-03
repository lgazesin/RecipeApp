package com.nexis.recipe.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nexis.recipe.Interface.Callback;
import com.nexis.recipe.Model.Recipe;
import com.nexis.recipe.R;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {
    Context context;
    ArrayList<Recipe>arrayList;
    Callback callback;

    public DataAdapter(Context context, ArrayList<Recipe> arrayList, Callback callback) {
        this.context = context;
        this.arrayList = arrayList;
        this.callback = callback;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.item_layout,parent,false);
        return new DataViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, final int position) {
        Recipe model = arrayList.get(position);
        Glide.with(context).load(model.getImageUrl()).into(holder.imageView);
        holder.tvname.setText(model.getName());
        holder.tvMin.setText(model.getMinute());
        holder.tvDesc.setText(model.getDescription());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }



    public class DataViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView tvname;
        TextView tvMin;
        TextView tvDesc;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            tvname = itemView.findViewById(R.id.tvName);
            tvMin = itemView.findViewById(R.id.tvMin);
            tvDesc = itemView.findViewById(R.id.tvDesc);
        }
    }

    public void searchITemName(ArrayList<Recipe> recipeArrayList) {
        arrayList = recipeArrayList;
        notifyDataSetChanged();
    }
}
