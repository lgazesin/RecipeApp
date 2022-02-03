package com.nexis.recipe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nexis.recipe.Model.Recipe;

import java.text.DateFormat;
import java.util.Calendar;

public class UploadActivity extends AppCompatActivity {
    ImageView imageView;
    EditText edName;
    EditText edDesc;
    EditText edMin;
    Uri uri;
    String imageUrl;

    LinearLayout mainLayout;

    Button uploadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        imageView = findViewById(R.id.imageView);
        edName = findViewById(R.id.tvName);
        edDesc = findViewById(R.id.tvDesc);
        edMin = findViewById(R.id.tvMin);

        uploadBtn = findViewById(R.id.upload_btn);


        mainLayout = findViewById(R.id.mainLayout);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
        }else {
            Snackbar.make(mainLayout,"Permission Allowed", Snackbar.LENGTH_LONG).setAction("Close", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            })
                    .setActionTextColor(getResources().getColor(R.color.purple_200)).show();
        }
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

    public void uploadRecipe(View view) {
        uploadImage();
    }

    public void uploadImage(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Image is Uploading");
        progressDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("RecipeBook").child(uri.getLastPathSegment());

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task task = taskSnapshot.getStorage().getDownloadUrl();
                while (!task.isSuccessful());
                Uri uriImage = (Uri) task.getResult();
                imageUrl = uriImage.toString();
                uploadRecipe();
                progressDialog.dismiss();
                Toast.makeText(UploadActivity.this, "Image Upload Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UploadActivity.this,"Error"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void uploadRecipe(){
        String name = edName.getText().toString().trim();
        String description = edDesc.getText().toString().trim();
        String minute = edMin.getText().toString().trim();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My Notification","My Notification",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        String timeStamp = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        Recipe recipe = new Recipe(name,description,minute,imageUrl);
        FirebaseDatabase.getInstance().getReference("RecipeBook").child(timeStamp).setValue(recipe).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(UploadActivity.this,"My Notification");
                builder.setContentTitle("Recipe");
                builder.setContentText("Hello from the recipe, you have added a new recipe");
                builder.setSmallIcon(R.drawable.ic_launcher_background);
                builder.setAutoCancel(true);
                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(UploadActivity.this);
                managerCompat.notify(1,builder.build());
                Toast.makeText(UploadActivity.this,"Success",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadActivity.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}