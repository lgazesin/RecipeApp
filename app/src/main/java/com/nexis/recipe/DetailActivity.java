package com.nexis.recipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ApplicationExitInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class DetailActivity extends AppCompatActivity {
    ImageView imageView;
    TextView tvItemName;
    TextView tvItemDesc;
    TextView tvItemMin;
    String imageUrl;
    String key;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        imageView = findViewById(R.id.image);
        tvItemName = findViewById(R.id.nameitem);
        tvItemDesc = findViewById(R.id.descitem);
        tvItemMin  =findViewById(R.id.minitem);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        imageUrl = intent.getStringExtra("IMAGE");

        Glide.with(this).load(imageUrl).into(imageView);
        String itemName = intent.getStringExtra("NAME");
        String itemDesc = intent.getStringExtra("DESCRIPTION");
        String itemMin = intent.getStringExtra("MINUTE");

        tvItemName.setText(itemName);
        tvItemDesc.setText(itemDesc);
        tvItemMin.setText(itemMin);


        key = intent.getStringExtra("KEY");
    }



    public void deleteItem(View view){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("RecipeBook");
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);

        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                reference.child(key).removeValue();
                Toast.makeText(DetailActivity.this,"Data Deleted Successfully",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DetailActivity.this,MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailActivity.this,"Error"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateItem(View view) {
        startActivity(new Intent(getApplicationContext(),UpdateRecipeActivity.class).putExtra("recipeNameKey",tvItemName.getText().toString())
                .putExtra("descriptionKey",tvItemDesc.getText().toString())
                .putExtra("minuteKey",tvItemMin.getText().toString())
                .putExtra("oldimageUrl",imageUrl)
                .putExtra("key",key));
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Check out this cool Application");
        intent.putExtra(Intent.EXTRA_TEXT,tvItemDesc.getText().toString());
        startActivity(Intent.createChooser(intent,"Share Via"));
        return super.onOptionsItemSelected(item);
    }
}