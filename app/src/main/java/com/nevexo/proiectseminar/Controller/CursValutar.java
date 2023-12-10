package com.nevexo.proiectseminar.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nevexo.proiectseminar.Model.Curs;
import com.nevexo.proiectseminar.R;

import java.util.ArrayList;
import java.util.List;

public class CursValutar extends AppCompatActivity {

    private Intent intent;
    private ListView listView;
    List<Curs> cursuri = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curs_valutar);

        intent = getIntent();

        listView = (ListView) findViewById(R.id.cursValutarListView);

        if(intent.hasExtra(BNRActivity.ADD_CURS)) {
            Curs curs = (Curs) intent.getSerializableExtra(BNRActivity.ADD_CURS);
            cursuri.add(curs);
            ArrayAdapter<Curs> adapter = new ArrayAdapter<Curs>(this, android.R.layout.simple_list_item_1, cursuri);
            listView.setAdapter(adapter);
        }

    }
}