package com.example.workshopapp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.List;

public class myAdapterPostaroLice extends RecyclerView.Adapter<myAdapterPostaroLice.ViewHolder> {

    private List<Baranje> myList;
    private int rowLayout;
    private Context mContext;

    private User volonter;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtAktivnost, txtOpisAktivnost, txtVreme, txtAdresa, txtStatus, txtDelete, txtVolonter;
        public ImageView Pic;

        public ViewHolder(View itemView) {
            super(itemView);
            txtAktivnost = (TextView) itemView.findViewById(R.id.rwAktivnost);
            txtOpisAktivnost = (TextView) itemView.findViewById(R.id.rwOpisAktivnost);
            txtVreme = (TextView) itemView.findViewById(R.id.rwVreme);
            txtAdresa = (TextView) itemView.findViewById(R.id.rwAdresa);
            txtStatus = (TextView) itemView.findViewById(R.id.rwStatus);
            txtDelete = (TextView) itemView.findViewById(R.id.rwDelete);
            txtVolonter = (TextView) itemView.findViewById(R.id.rwVolonter);
            Pic = (ImageView) itemView.findViewById(R.id.picture);
        }
    }


    public myAdapterPostaroLice(List<Baranje> myList, int rowLayout, Context context) {
        this.myList = myList;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
       Baranje baranje = myList.get(i);
        viewHolder.txtAktivnost.setText(baranje.getAktivnost());
        viewHolder.txtOpisAktivnost.setText("Опис: "+baranje.getOpisAktivnost());
        if(baranje.getDatum().equals("")) {
            viewHolder.txtVreme.setText("Време: " + baranje.getVremeOd() + " - " + baranje.getVremeDo() + "       Секој " + baranje.getDen());
        } else {
            viewHolder.txtVreme.setText("Време: " + baranje.getVremeOd() + " - " + baranje.getVremeDo() + "       Датум: " + baranje.getDatum());
        }
        viewHolder.txtAdresa.setText("Адреса: " + baranje.getAdresa());
        viewHolder.txtStatus.setText(baranje.getStatus());

        viewHolder.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Потврда");
                builder.setMessage("Дали сте сигурни дека сакате да ја избришете оваа активност?");

                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Baranja");
                        databaseReference.child(baranje.getAktivnostId()).removeValue();
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
            }
        });

        if(baranje.getStatus().equals("На чекање")) {
            viewHolder.txtStatus.setTextColor(Color.rgb(55,0,179));
            viewHolder.txtStatus.setPaintFlags(viewHolder.txtStatus.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        System.out.println(baranje.getVolonterUId());

        viewHolder.txtStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(baranje.getStatus().equals("На чекање")) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                            .child(baranje.getVolonterUId());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                           volonter = snapshot.getValue(User.class);
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                            builder.setTitle("Потврда");
                            DecimalFormat decimalFormat = new DecimalFormat("##.0");
                            if(volonter.getVkupnoOceni() != 0) {
                                double ocena = (double) volonter.getZbirOceni() / volonter.getVkupnoOceni();
                                builder.setMessage(volonter.getName() + "    Оцена: " + decimalFormat.format(ocena) + "\n"
                                + "Е-маил: " +volonter.getEmail() + "\n" + "Телефонски број: " + volonter.getPhone());
                            } else {
                                builder.setMessage(volonter.getName() + "    Оцена: / \n"
                                        + "Е-маил: " +volonter.getEmail() + "\n" + "Телефонски број: " + volonter.getPhone());
                            }

                            builder.setPositiveButton("Прифати", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase.getInstance().getReference("Baranja").child(baranje.getAktivnostId())
                                            .child("status").setValue("Закажано").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                FirebaseDatabase.getInstance().getReference("Baranja").child(baranje.getAktivnostId())
                                                        .child("emailVolonter").setValue(volonter.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task1) {
                                                        if(task1.isSuccessful()) {
                                                            FirebaseDatabase.getInstance().getReference("Baranja").child(baranje.getAktivnostId())
                                                                    .child("telefonVolonter").setValue(volonter.getPhone()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task2) {
                                                                    if(task2.isSuccessful()) {
                                                                        viewHolder.txtStatus.setTextColor(Color.rgb(0,0,0));
                                                                        viewHolder.txtStatus.setPaintFlags(0);
                                                                        Toast.makeText(mContext, "Успешно е доделен волонтер за вашата активност!", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();

                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
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
                                    FirebaseDatabase.getInstance().getReference("Baranja").child(baranje.getAktivnostId())
                                            .child("status").setValue("Активно").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                FirebaseDatabase.getInstance().getReference("Baranja").child(baranje.getAktivnostId())
                                                        .child("volonterUId").setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task1) {
                                                        if(task1.isSuccessful()) {
                                                            viewHolder.txtStatus.setTextColor(Color.rgb(0,0,0));
                                                            viewHolder.txtStatus.setPaintFlags(0);
                                                            Toast.makeText(mContext, "Волонтерот е одбиен.", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });

                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
        if(!baranje.getEmailVolonter().equals("")) {
            viewHolder.txtVolonter.setText("Волонтер: " + baranje.getEmailVolonter() + "       " + baranje.getTelefonVolonter());
        }
        viewHolder.Pic.setImageResource(R.drawable.pozadina);
    }

    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }
}
