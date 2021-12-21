package com.example.workshopapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class myAdapterVolonterAktivniBaranja extends RecyclerView.Adapter<myAdapterVolonterAktivniBaranja.ViewHolder> {

    private List<Baranje> myList;
    private int rowLayout;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtAktivnost, txtOpisAktivnost, txtVreme, txtAdresa, txtImePrezime, txtSend;
        public ImageView Pic;

        public ViewHolder(View itemView) {
            super(itemView);
            txtAktivnost = (TextView) itemView.findViewById(R.id.rw1TipAktivnost);
            txtOpisAktivnost = (TextView) itemView.findViewById(R.id.rw1OpisAktivnost);
            txtVreme = (TextView) itemView.findViewById(R.id.rw1DatumVreme);
            txtAdresa = (TextView) itemView.findViewById(R.id.rw1Lokacija);
            txtImePrezime = (TextView) itemView.findViewById(R.id.rw1ImePrezime);
            txtSend = (TextView) itemView.findViewById(R.id.rw1Send);
            Pic = (ImageView) itemView.findViewById(R.id.rw1Picture);
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
        viewHolder.txtAdresa.setText("Адреса: " + baranje.getAdresa() + "       Растојание: " + baranje.getRastojanie());
       // viewHolder.txtImePrezime.setText(baranje.getStatus());

//        viewHolder.txtDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//
//                builder.setTitle("Потврда");
//                builder.setMessage("Дали сте сигурни дека сакате да ја избришете оваа активност?");
//
//                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Baranja");
//                        databaseReference.child(baranje.getAktivnostId()).removeValue();
//                        dialog.dismiss();
//                    }
//                });
//
//                builder.setNegativeButton("Не", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                AlertDialog alert = builder.create();
//                alert.show();
//            }
//        });
//        if(!baranje.getEmailVolonter().equals("")) {
//            viewHolder.txtVolonter.setText("Волонтер: " + baranje.getEmailVolonter() + "       " + baranje.getTelefonVolonter());
//        }
        viewHolder.Pic.setImageResource(R.drawable.pozadina);
    }

    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }
}
