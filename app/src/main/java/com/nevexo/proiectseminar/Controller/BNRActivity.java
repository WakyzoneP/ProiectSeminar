package com.nevexo.proiectseminar.Controller;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nevexo.proiectseminar.Model.Curs;
import com.nevexo.proiectseminar.Network.Network;
import com.nevexo.proiectseminar.R;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;

public class BNRActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etEUR;
    private EditText etUSD;
    private EditText etGBP;
    private EditText etXAU;
    private TextView tvExtract;
    private TextView tvDataCurs;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    public static final String ADD_CURS = "addCusr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bnr);

        etEUR = findViewById(R.id.editTextEUR);
        etUSD = findViewById(R.id.editTextUSD);
        etGBP = findViewById(R.id.editTextGBP);
        etXAU = findViewById(R.id.editTextXAU);
        tvExtract = findViewById(R.id.tvExtract);
        tvDataCurs = findViewById(R.id.tvDataCurs);

        Button btnAfisare = findViewById(R.id.btnShow);
        btnAfisare.setOnClickListener(this);
        Button btnExtract = findViewById(R.id.btnExtract);
        btnExtract.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (R.id.btnShow == view.getId()) {
            etEUR.setText("4.97");
            etUSD.setText("4.55");
            etGBP.setText("5.46");
            etXAU.setText("235.67");
            Intent intent = new Intent(this, CursValutar.class);
            Curs curs = new Curs(
                    new Date(),
                    Float.parseFloat(etEUR.getText().toString()),
                    Float.parseFloat(etUSD.getText().toString()),
                    Float.parseFloat(etGBP.getText().toString()),
                    Float.parseFloat(etXAU.getText().toString()));
            intent.putExtra(ADD_CURS, curs);
            startActivity(intent);
        }
        if (R.id.btnExtract == view.getId()) {
            Network network = new Network()
            {
                @Override
                protected void onPostExecute(InputStream inputStream) {

                    tvDataCurs.setText("Data curs: " + cursValutar.getDataCurs().toString());
                    etEUR.setText(String.valueOf(cursValutar.getEUR()));
                    etUSD.setText(String.valueOf(cursValutar.getUSD()));
                    etGBP.setText(String.valueOf(cursValutar.getGBP()));
                    etXAU.setText(String.valueOf(cursValutar.getXAU()));
                }
            };
            try {
                network.execute(new java.net.URL("https://www.bnr.ro/nbrfxrates.xml"));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("lifecycle", "Apel metoda onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("lifecycle", "Apel metoda onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("lifecycle", "Apel metoda onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("lifecycle", "Apel metoda onStop()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("lifecycle", "Apel metoda onRestart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("lifecycle", "Apel metoda onDestroy()");

    }
}