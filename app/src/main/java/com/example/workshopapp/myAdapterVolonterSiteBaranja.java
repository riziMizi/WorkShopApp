package com.example.workshopapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

public class myAdapterVolonterSiteBaranja extends RecyclerView.Adapter<myAdapterVolonterSiteBaranja.ViewHolder>{

    private List<Baranje> myList;
    private int rowLayout;
    public Context mContext;

    private int VkupnoOceni = 0;
    private int ZbirOceni = 0;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtAktivnost, txtOpisAktivnost, txtVreme, txtAdresa, txtImePrezime, txtSend, txtDone;
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
            Pic = (ImageView) itemView.findViewById(R.id.rw1Picture);

            txtDone.setVisibility(View.INVISIBLE);

            txtSend.setVisibility(View.INVISIBLE);
        }
    }


    public myAdapterVolonterSiteBaranja(List<Baranje> myList, int rowLayout, Context context) {
        this.myList = myList;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public myAdapterVolonterSiteBaranja.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new myAdapterVolonterSiteBaranja.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(myAdapterVolonterSiteBaranja.ViewHolder viewHolder, int i) {
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

        if(baranje.getStatus().equals("Закажано")) {
            viewHolder.txtDone.setVisibility(View.VISIBLE);
        }

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

        viewHolder.txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                alert.setTitle("Завршена активност");

                LinearLayout layout = new LinearLayout(mContext);
                layout.setOrientation(LinearLayout.VERTICAL);

                final TextView textView = new TextView(mContext);
                textView.setText("Оцена за постарото лице:");
                textView.setPadding(0,10,0,0);
                textView.setTextSize(16);

                final EditText izvestaj = new EditText(mContext);
                izvestaj.setHint("Поднесете извештај");
                layout.addView(izvestaj);
                layout.addView(textView);

               final RadioGroup radioGroup = new RadioGroup(mContext);
                RadioButton radioButton;
                for(int i = 1; i < 6; i++) {
                    radioButton = new RadioButton(mContext);
                    radioButton.setText(String.valueOf(i));
                    radioGroup.addView(radioButton);
                }

                RadioButton radioButton1 = (RadioButton) radioGroup.getChildAt(4);
                radioButton1.setChecked(true);

                radioGroup.setOrientation(LinearLayout.HORIZONTAL);

                layout.addView(radioGroup);

                alert.setView(layout);

                alert.setPositiveButton("Поднеси", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        RadioButton radioButton1 = (RadioButton) layout.findViewById(radioGroup.getCheckedRadioButtonId());
                        int Ocena = Integer.parseInt(radioButton1.getText().toString());
                        FirebaseDatabase.getInstance().getReference("Baranja")
                                .child(baranje.getAktivnostId()).child("status").setValue("Завршено").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    FirebaseDatabase.getInstance().getReference("Baranja")
                                            .child(baranje.getAktivnostId()).child("izvestajVolonter").setValue(izvestaj.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task1) {
                                            if(task1.isSuccessful()) {
                                                FirebaseDatabase.getInstance().getReference("Baranja")
                                                        .child(baranje.getAktivnostId()).child("ocenaPostaroLice").setValue(Ocena).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task2) {
                                                        if(task2.isSuccessful()) {
                                                            DodadiOcena(baranje.getUserId(), Ocena);
                                                            Toast.makeText(mContext, "Успешно поднесен извештај за завршената активност!", Toast.LENGTH_SHORT).show();
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

                alert.setNegativeButton("Исклучи", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });
        viewHolder.Pic.setImageResource(R.drawable.pozadina);
    }

    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }

    private void DodadiOcena(String id, int ocena) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
               VkupnoOceni = user.getVkupnoOceni();
               ZbirOceni = user.getZbirOceni();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });
        VkupnoOceni++;
        ZbirOceni += ocena;
        FirebaseDatabase.getInstance().getReference("Users")
                .child(id).child("vkupnoOceni").setValue(VkupnoOceni).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(id).child("zbirOceni").setValue(ZbirOceni).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task1) {
                            if(!task1.isSuccessful()) {
                                Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
