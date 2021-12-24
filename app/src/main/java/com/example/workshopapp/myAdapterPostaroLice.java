package com.example.workshopapp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class myAdapterPostaroLice extends RecyclerView.Adapter<myAdapterPostaroLice.ViewHolder> {

    private List<Baranje> myList;
    private int rowLayout;
    private Context mContext;

    private User volonter;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtAktivnost, txtOpisAktivnost, txtVreme, txtAdresa, txtStatus,
                txtDelete, txtVolonter, txtInfo;
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
            txtInfo = (TextView) itemView.findViewById(R.id.rwInfo);
            Pic = (ImageView) itemView.findViewById(R.id.picture);

            txtInfo.setVisibility(View.INVISIBLE);
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

                                    Map<String, Object> map = new HashMap();

                                    DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Baranja");
                                    map.put(baranje.getAktivnostId()+"/status", "Закажано");
                                    map.put(baranje.getAktivnostId()+"/emailVolonter", volonter.getEmail());
                                    map.put(baranje.getAktivnostId()+"/telefonVolonter", volonter.getPhone());

                                    firebaseDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                viewHolder.txtStatus.setTextColor(Color.rgb(0,0,0));
                                                viewHolder.txtStatus.setPaintFlags(0);
                                                Toast.makeText(mContext, "Успешно е доделен волонтер за вашата активност!", Toast.LENGTH_SHORT).show();
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
                                    Map<String, Object> map = new HashMap();

                                    DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Baranja");
                                    map.put(baranje.getAktivnostId()+"/status", "Активно");
                                    map.put(baranje.getAktivnostId()+"/volonterUId", "");

                                    firebaseDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                viewHolder.txtStatus.setTextColor(Color.rgb(0,0,0));
                                                viewHolder.txtStatus.setPaintFlags(0);
                                                Toast.makeText(mContext, "Волонтерот е одбиен.", Toast.LENGTH_SHORT).show();
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

                } else if(baranje.getStatus().equals("Завршено") && baranje.getOcenaVolonter() == 0) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                    alert.setTitle("Завршена активност");

                    LinearLayout layout = new LinearLayout(mContext);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final TextView textView = new TextView(mContext);
                    textView.setText("Оцена за волонтерот:");
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

                            Map<String, Object> map = new HashMap();

                            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Baranja");
                            map.put(baranje.getAktivnostId()+"/izvestajPostaroLice", izvestaj.getText().toString().trim());
                            map.put(baranje.getAktivnostId()+"/ocenaVolonter", Ocena);

                            firebaseDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(mContext, "Успешно поднесен извештај за завршената активност!", Toast.LENGTH_SHORT).show();
                                        DodadiOcena(baranje.getVolonterUId(), Ocena);
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
            }
        });

        if(!baranje.getEmailVolonter().equals("")) {
            viewHolder.txtVolonter.setText("Контакт: " + baranje.getEmailVolonter() + "       " + baranje.getTelefonVolonter());
        }

        if(baranje.getOcenaVolonter() == 0) {
            viewHolder.txtInfo.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.txtInfo.setVisibility(View.VISIBLE);
        }

        if(baranje.getOcenaVolonter() == 0 && (baranje.getStatus().equals("Завршено") || baranje.getStatus().equals("На чекање"))) {
            viewHolder.txtStatus.setTextColor(Color.rgb(55,0,179));
            viewHolder.txtStatus.setPaintFlags(viewHolder.txtStatus.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        } else if(baranje.getOcenaVolonter() != 0 && baranje.getStatus().equals("Завршено")) {
            viewHolder.txtStatus.setTextColor(Color.rgb(0,0,0));
            viewHolder.txtStatus.setPaintFlags(0);
        }

        viewHolder.txtInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                alert.setTitle("Извештај за активноста");

                LinearLayout layout = new LinearLayout(mContext);
                layout.setOrientation(LinearLayout.VERTICAL);

                final TextView textView = new TextView(mContext);
                textView.setText("Ваш извештај:");
                textView.setPadding(0,10,0,0);
                textView.setTextSize(20);

                final TextView textView1 = new TextView(mContext);
                textView1.setText(baranje.getIzvestajPostaroLice());
                textView1.setPadding(0,10,0,0);
                textView1.setTextSize(16);

                final TextView textView2 = new TextView(mContext);
                textView2.setText("Оцена: " + String.valueOf(baranje.getOcenaVolonter()));
                textView2.setPadding(0,10,0,0);
                textView2.setTextSize(16);

                layout.addView(textView);
                layout.addView(textView1);
                layout.addView(textView2);

                if(baranje.getOcenaVolonter() != 0) {
                    final TextView textView3 = new TextView(mContext);
                    textView3.setText("Извештај на волонтерот:");
                    textView3.setPadding(0,10,0,0);
                    textView3.setTextSize(20);

                    final TextView textView4 = new TextView(mContext);
                    textView4.setText(baranje.getIzvestajVolonter());
                    textView4.setPadding(0,10,0,0);
                    textView4.setTextSize(16);

                    final TextView textView5 = new TextView(mContext);
                    textView5.setText("Оцена: " + String.valueOf(baranje.getOcenaPostaroLice()));
                    textView5.setPadding(0,10,0,0);
                    textView5.setTextSize(16);

                    layout.addView(textView3);
                    layout.addView(textView4);
                    layout.addView(textView5);
                }

                alert.setView(layout);

                alert.setNegativeButton("Во ред", new DialogInterface.OnClickListener() {
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
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                int VkupnoOceni = user.getVkupnoOceni() + 1;
                int ZbirOceni = user.getZbirOceni() + ocena;
                user.setVkupnoOceni(VkupnoOceni);
                user.setZbirOceni(ZbirOceni);
                snapshot.getRef().setValue(user);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
