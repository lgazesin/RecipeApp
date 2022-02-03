package com.nexis.recipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nexis.recipe.Adapter.DataAdapter;
import com.nexis.recipe.CardView.CardRecipe;
import com.nexis.recipe.Category.Category;
import com.nexis.recipe.Category.CategoryAdapter;
import com.nexis.recipe.Fragments.HomeFragment;
import com.nexis.recipe.Fragments.InfoFragment;
import com.nexis.recipe.Fragments.ProfileFragment;
import com.nexis.recipe.Model.Recipe;
import com.nexis.recipe.Interface.Callback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Callback,NavigationView.OnNavigationItemSelectedListener{
    RecyclerView recyclerView;
    ArrayList<Recipe> arrayList;
    DataAdapter adapter;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    EditText editSearch;

    private DrawerLayout drawer;

    private RecyclerView rcvCategory;
    private CategoryAdapter categoryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        editSearch = findViewById(R.id.editSearch);


        rcvCategory = findViewById(R.id.rcv_category);
        categoryAdapter = new CategoryAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        rcvCategory.setLayoutManager(linearLayoutManager);
        categoryAdapter.setData(getListCategory());
        rcvCategory.setAdapter(categoryAdapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        recyclerView.setHasFixedSize(true);
        arrayList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Items Loading...");
        databaseReference = FirebaseDatabase.getInstance().getReference("RecipeBook");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                   Recipe recipe = ds.getValue(Recipe.class);
                   recipe.setKey(ds.getKey());
                   arrayList.add(recipe);
                }
                adapter = new DataAdapter(MainActivity.this,arrayList,MainActivity.this);
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error"+error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String itemName = s.toString();
                ArrayList<Recipe>recipeArrayList = new ArrayList<>();
                for (Recipe r:arrayList) {
                    if (r.getName().toLowerCase().contains(itemName)){
                        recipeArrayList.add(r);
                    }
                    adapter.searchITemName(recipeArrayList);
                }
            }
        });
    }

    private List<Category> getListCategory(){
        List<Category> listCategory = new ArrayList<>();
        List<CardRecipe> listRecipe = new ArrayList<>();
        listRecipe.add(new CardRecipe(R.drawable.pizza,"Pizza"));
        listRecipe.add(new CardRecipe(R.drawable.hamburger,"Hamburger"));
        listRecipe.add(new CardRecipe(R.drawable.fires,"Fires"));
        listRecipe.add(new CardRecipe(R.drawable.icecream,"Ice Cream"));
        listRecipe.add(new CardRecipe(R.drawable.sandwich,"Sandwich"));


        listCategory.add(new Category("CATEGORIES",listRecipe));


        return listCategory;
    }


    public void uploadClick(View view) {
        startActivity(new Intent(MainActivity.this,UploadActivity.class));
    }

    @Override
    public void onClick(int i) {
        Intent intent = new Intent(MainActivity.this,DetailActivity.class);
        intent.putExtra("IMAGE",arrayList.get(i).getImageUrl());
        intent.putExtra("NAME",arrayList.get(i).getName());
        intent.putExtra("KEY",arrayList.get(i).getKey());
        intent.putExtra("DESCRIPTION",arrayList.get(i).getDescription());
        intent.putExtra("MINUTE",arrayList.get(i).getMinute());
        startActivity(intent);
    }

    public void onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;
            case R.id.nav_acc:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;
            case R.id.nav_info:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new InfoFragment()).commit();
                break;
            case R.id.nav_share:
                Toast.makeText(this,"Share",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_send:
                Toast.makeText(this,"Send",Toast.LENGTH_SHORT).show();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
       switch (item.getItemId()){
           case R.id.item1:
               startService(new Intent(this,MyAndroidServiceTutorial.class));
               break;
           case R.id.item2:
               stopService(new Intent(this,MyAndroidServiceTutorial.class));
               break;
       }
       return true;
    }


}