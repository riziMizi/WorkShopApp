package com.example.workshopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class VolonterActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private myAdapterVolonterAktivniBaranja mAdapter;

    private List<Baranje> list = new ArrayList<>();

    FusedLocationProviderClient fusedLocationProviderClient;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private Boolean mLocationPermissionsGranted = false;

    private Location myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volonter);

        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getLocationPermission();
        getDeviceLocation();

        CreateList();

        mRecyclerView = (RecyclerView) findViewById(R.id.list1);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new myAdapterVolonterAktivniBaranja(list, R.layout.aktivni_baranja_volonter, this);
        mRecyclerView.setAdapter(mAdapter);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.actionbar_postaro_lice_baranja, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_baranja:
                Intent intent = new Intent(this, VolonterSvojZadaciActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_signout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Одјави се");
                builder.setMessage("Дали сте сигурни дека сакате да се одјавите?");
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(VolonterActivity.this, MainActivity.class));
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Не", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void CreateList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Baranja");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                double rastojanie = 0;
                double rastojanieKm = 0;

                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Baranje baranje = dataSnapshot.getValue(Baranje.class);
                    if(myLocation != null) {
                        LatLng Start = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                        LatLng End = new LatLng(baranje.getLatitude(), baranje.getLongitude());
                        rastojanie = PresmetajRastojanie(Start, End);
                    }
                    rastojanieKm = rastojanie / 1000;
                    baranje.setRastojanie((double) Math.round(rastojanieKm * 100) / 100);
                    baranje.setAktivnostId(dataSnapshot.getKey());
                    if(baranje.getStatus().equals("Активно")) {
                        list.add(baranje);
                    }
                }

                Collections.sort(list, new Comparator<Baranje>() {
                    @Override
                    public int compare(Baranje o1, Baranje o2) {
                        return Double.valueOf(o1.getRastojanie()).compareTo(o2.getRastojanie());
                    }
                });

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(VolonterActivity.this, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                }
            }
        }
    }

    private void getDeviceLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mLocationPermissionsGranted){

                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                           myLocation = (Location) task.getResult();
                        }else{
                            Toast.makeText(VolonterActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
           e.printStackTrace();
        }
    }

    public float PresmetajRastojanie(LatLng Start, LatLng End) {
        float[] results = new float[1];
        Location.distanceBetween(Start.latitude, Start.longitude,
                End.latitude, End.longitude,
                results);
        return results[0];
    }

}