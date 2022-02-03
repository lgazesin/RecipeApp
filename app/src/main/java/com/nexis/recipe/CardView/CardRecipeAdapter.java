package com.nexis.recipe.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nexis.recipe.Model.Recipe;
import com.nexis.recipe.R;

import java.util.List;

public class CardRecipeAdapter extends RecyclerView.Adapter<CardRecipeAdapter.CardRecipeViewHolder> {

    private List<CardRecipe> mRecipes;
    public void setData(List<CardRecipe> list){
        this.mRecipes = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CardRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe,parent,false);
        return new CardRecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardRecipeViewHolder holder, int position) {
        CardRecipe cardRecipe = mRecipes.get(position);
        if (cardRecipe == null){
            return;
        }
        holder.imgRecipe.setImageResource(cardRecipe.getResourceId());
        holder.tvTitle.setText(cardRecipe.getTitle());
    }

    @Override
    public int getItemCount() {
        if (mRecipes != null){
            return mRecipes.size();
        }
        return 0;
    }

    public class CardRecipeViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgRecipe;
        private TextView tvTitle;

        public CardRecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRecipe = itemView.findViewById(R.id.img_recipe);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }
}
