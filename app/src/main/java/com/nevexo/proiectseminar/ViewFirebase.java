package com.nevexo.proiectseminar;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nevexo.proiectseminar.Model.Masina;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewFirebase extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView listView = new ListView(this);
        List<Masina> masinaList = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("bv-seminar-default-rtdb");
        myRef.keepSynced(true);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Masina masina = dataSnapshot.getValue(Masina.class);
                        masinaList.add(masina);
                    }
                }
                ArrayAdapter<Masina> arrayAdapter = new ArrayAdapter<Masina>(ViewFirebase.this, android.R.layout.simple_list_item_1, masinaList);
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        myRef.child("bv-seminar-default-rtdb").addValueEventListener(valueEventListener);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Masina masina = masinaList.get(i);
                masinaList.remove(masina);

                myRef.child("bv-seminar-default-rtdb").child(masina.getUid()).removeValue();

                ArrayAdapter<Masina> arrayAdapter = new ArrayAdapter<Masina>(ViewFirebase.this, android.R.layout.simple_list_item_1, masinaList);
                listView.setAdapter(arrayAdapter);

                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Masina masina = masinaList.get(i);

                HashMap map = new HashMap();
                map.put("marca", "Tesla");
                map.put("pret", 100000);

                myRef.child("bv-seminar-default-rtdb").child(masina.getUid()).updateChildren(map);
            }
        });

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(this);
        textView.setText("Lista masini din Firebase\n");

        linearLayout.addView(textView);
        linearLayout.addView(listView);
        horizontalScrollView.addView(linearLayout);

        setContentView(horizontalScrollView);
    }
}
