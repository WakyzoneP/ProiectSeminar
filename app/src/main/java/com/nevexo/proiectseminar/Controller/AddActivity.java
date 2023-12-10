package com.nevexo.proiectseminar.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nevexo.proiectseminar.Model.Masina;
import com.nevexo.proiectseminar.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etMarca;
    private EditText etDataFabricatiei;
    private EditText etPret;
    private RadioGroup radioGroup;
    private Button button;
    private Spinner spinner;
    private RadioButton radioButton;
    private Intent intent;
    public static final String ADD_MASINA = "addMasina";

    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        intent = getIntent();

        database = FirebaseDatabase.getInstance();

        etMarca = (EditText) findViewById(R.id.editTextMarca);
        etDataFabricatiei = (EditText) findViewById(R.id.editTextData);
        etPret = (EditText) findViewById(R.id.editTextPret);
        radioGroup = (RadioGroup) findViewById(R.id.RadioGroupEngine);
        spinner = (Spinner) findViewById(R.id.spinner);

        if(intent.hasExtra(MainActivity.EDIT_MASINA)) {
            Masina masina = (Masina) intent.getSerializableExtra(MainActivity.EDIT_MASINA);
            etMarca.setText(masina.getMarca());
            etDataFabricatiei.setText(new SimpleDateFormat("MM/dd/yyyy", Locale.US)
                    .format(masina.getDataFabricatie()));
            etPret.setText(String.valueOf(masina.getPret()));
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
            for(int i = 0; i < adapter.getCount(); ++i)
                if(adapter.getItem(i).equals(masina.getCuloare()))
                {
                    spinner.setSelection(i);
                    break;
                }
            if(masina.getMotorizare().equals("GASOLINE"))
                radioGroup.check((R.id.GasolineRadioButton));
            else if(masina.getMotorizare().equals(("HYBRID")))
                radioGroup.check(R.id.HibridRadioButton);
            else if(masina.getMotorizare().equals("ELECTRIC"))
                radioGroup.check(R.id.ElectricRadioButton);
            else radioGroup.check(R.id.GasolineRadioButton);

        }

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (etMarca.getText().toString().isEmpty() == true)
            etMarca.setError("Introduceri marca !");
        else if (etDataFabricatiei.getText().toString().isEmpty())
            etMarca.setError("Introduceri data !");
        else if (etPret.getText().toString().isEmpty())
            etMarca.setError("Introduceri pretul !");
        else {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            try {
                sdf.parse(etDataFabricatiei.getText().toString());
                Date dataFabricatiei = new Date(etDataFabricatiei.getText().toString());
                String marca = etMarca.getText().toString();
                float pret = Float.parseFloat(etPret.getText().toString());
                String culoare = spinner.getSelectedItem().toString();
                radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                String motorizare = radioButton.getText().toString();

                Masina masina = new Masina(marca, dataFabricatiei, pret, culoare, motorizare);
                readToFirebase(masina);
//                Toast.makeText(this, masina.toString(), Toast.LENGTH_LONG).show();
                intent.putExtra(ADD_MASINA, masina);
                setResult(RESULT_OK, intent);
                finish();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            } catch (Exception ex) {
                Log.e("AddActivity", "Erori introducere data");
                Toast.makeText(this, "Erori introducere data", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void readToFirebase(Masina masina) {
        DatabaseReference myRef = database.getReference("bv-seminar-default-rtdb");
        myRef.keepSynced(true);

        myRef.child("masini").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                masina.setUid(myRef.child("bv-seminar-default-rtdb").push().getKey());
                myRef.child("bv-seminar-default-rtdb").child(masina.getUid()).setValue(masina);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}