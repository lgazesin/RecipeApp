package com.nexis.recipe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nexis.recipe.Model.Recipe;

import java.text.DateFormat;
import java.util.Calendar;

public class UpdateRecipeActivity extends AppCompatActivity {
    ImageView imageView;
    EditText edName;
    EditText edDesc;
    EditText edMin;
    Uri uri;
    String imageUrl;
    String key,oldimageUrl;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String recipename,recipeDescription,recipeMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_recipe);

        imageView = findViewById(R.id.imageView);
        edName = findViewById(R.id.tvName);
        edDesc = findViewById(R.id.tvDesc);
        edMin = findViewById(R.id.tvMin);

        Intent intent = getIntent();

        imageUrl = intent.getStringExtra("oldimageUrl");

        Glide.with(this).load(imageUrl).into(imageView);
        String itemName = intent.getStringExtra("recipeNameKey");
        String itemDesc = intent.getStringExtra("descriptionKey");
        String itemMin = intent.getStringExtra("minuteKey");

        edName.setText(itemName);
        edDesc.setText(itemDesc);
        edMin.setText(itemMin);

        key = intent.getStringExtra("key");
        oldimageUrl = intent.getStringExtra("oldimageUrl");

        databaseReference = FirebaseDatabase.getInstance().getReference("RecipeBook").child(key);


    }

    public void pickImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            uri = data.getData();
            imageView.setImageURI(uri);
        }else{
            Toast.makeText(this, "you have not picked image", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateRecipe(View view) {
        recipename = edName.getText().toString().trim();
        recipeDescription = edDesc.getText().toString().trim();
        recipeMinute = edMin.getText().toString();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Image is Uploading");
        progressDialog.show();

        storageReference = FirebaseStorage.getInstance().getReference().child("RecipeBook").child(uri.getLastPathSegment());

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task task = taskSnapshot.getStorage().getDownloadUrl();
                while (!task.isSuccessful());
                Uri uriImage = (Uri) task.getResult();
                imageUrl = uriImage.toString();
                uploadRecipe();
                progressDialog.dismiss();
                Toast.makeText(UpdateRecipeActivity.this, "Image Upload Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UpdateRecipeActivity.this,"Error"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void uploadRecipe(){
        String recipename = edName.getText().toString().trim();
        String recipeDescription = edDesc.getText().toString().trim();
        String recipeMinute = edMin.getText().toString().trim();
        Recipe recipe = new Recipe(recipename,recipeDescription,recipeMinute,imageUrl);

        databaseReference.setValue(recipe).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                StorageReference storageReferenceNew = FirebaseStorage.getInstance().getReference().child("RecipeBook").child(oldimageUrl);
                storageReferenceNew.delete();
                Toast.makeText(UpdateRecipeActivity.this,"Data Updated",Toast.LENGTH_SHORT).show();
            }
        });


    }
}