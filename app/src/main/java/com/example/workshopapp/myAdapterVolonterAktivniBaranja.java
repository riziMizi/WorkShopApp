package com.example.workshopapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class myAdapterVolonterAktivniBaranja extends RecyclerView.Adapter<myAdapterVolonterAktivniBaranja.ViewHolder> {

    private List<Baranje> myList;
    private int rowLayout;
    public Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtAktivnost, txtOpisAktivnost, txtVreme, txtAdresa, txtImePrezime,
                txtSend, txtDone, txtKontakt, txtInfo;
        public ImageView Pic;

        public ViewHolder(View itemView) {
            super(itemView);
            txtAktivnost = (TextView) itemView.findViewById(R.id.rw1TipAktivnost);
            txtOpisAktivnost = (TextView) itemView.findViewById(R.id.rw1OpisAktivnost);
            txtVreme = (TextView) itemView.findViewById(R.id.rw1DatumVreme);
            txtAdresa = (TextView) itemView.findViewById(R.id.rw1Lokacija);
            txtImePrezime = (TextView) itemView.findViewById(R.id.rw1ImePrezime);
            txtSend = (TextView) itemView.findViewById(R.id.rw1Send);
            txtDone = (TextView) itemView.findViewById(R.id.rw1Done);
            txtKontakt = (TextView) itemView.findViewById(R.id.rw1Kontakt);
            txtInfo = (TextView) itemView.findViewById(R.id.rw1Info);
            Pic = (ImageView) itemView.findViewById(R.id.rw1Picture);

            txtDone.setVisibility(View.INVISIBLE);
            txtInfo.setVisibility(View.INVISIBLE);
            txtKontakt.setVisibility(View.INVISIBLE);
        }
    }


    public myAdapterVolonterAktivniBaranja(List<Baranje> myList, int rowLayout, Context context) {
        this.myList = myList;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public myAdapterVolonterAktivniBaranja.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new myAdapterVolonterAktivniBaranja.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(myAdapterVolonterAktivniBaranja.ViewHolder viewHolder, int i) {
        Baranje baranje = myList.get(i);
        viewHolder.txtAktivnost.setText(baranje.getAktivnost());
        viewHolder.txtOpisAktivnost.setText("Опис: "+baranje.getOpisAktivnost());
        if(baranje.getDatum().equals("")) {
            viewHolder.txtVreme.setText("Време: " + baranje.getVremeOd() + " - " + baranje.getVremeDo() + "       Секој " + baranje.getDen());
        } else {
            viewHolder.txtVreme.setText("Време: " + baranje.getVremeOd() + " - " + baranje.getVremeDo() + "       Датум: " + baranje.getDatum());
        }
        viewHolder.txtAdresa.setText("Адреса: " + baranje.getAdresa() + "       Растојание: " + baranje.getRastojanie() + " км");
        viewHolder.txtAdresa.setPaintFlags(viewHolder.txtAdresa.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(baranje.getUserId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    DecimalFormat decimalFormat = new DecimalFormat("##.0");
                    if(user.getVkupnoOceni() != 0) {
                        double ocena = (double) user.getZbirOceni() / user.getVkupnoOceni();
                        viewHolder.txtImePrezime.setText(user.getName() + "    Oцена: " + decimalFormat.format(ocena));
                    } else {
                        viewHolder.txtImePrezime.setText(user.getName() + "    Oцена: /");
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.txtAdresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ShowGoogleMapActivity.class);
                intent.putExtra("lat", baranje.getLatitude());
                intent.putExtra("lon", baranje.getLongitude());
                v.getContext().startActivity(intent);
            }
        });

        viewHolder.txtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Потврда");
                builder.setMessage("Потврдете го вашето волонтирање за оваа активност!");

                builder.setPositiveButton("Потврди", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Map<String, Object> map = new HashMap();

                        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Baranja");
                        map.put(baranje.getAktivnostId()+"/status", "На чекање");
                        map.put(baranje.getAktivnostId()+"/volonterUId", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        firebaseDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(mContext, "Успешно се пријавивте за активноста!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Одбиј", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        viewHolder.Pic.setImageResource(R.drawable.pozadina);
    }

    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }
}
