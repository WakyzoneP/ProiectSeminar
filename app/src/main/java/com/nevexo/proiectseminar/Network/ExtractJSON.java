package com.nevexo.proiectseminar.Network;

import android.os.AsyncTask;
import android.util.Xml;

import com.nevexo.proiectseminar.Model.Masina;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExtractJSON extends AsyncTask<URL, Void, InputStream> {
    InputStream inputStream = null;
    public List<Masina> masini = null;

    @Override
    protected InputStream doInBackground(URL... urls) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) urls[0].openConnection();
            connection.setRequestMethod("GET");
            inputStream = connection.getInputStream();

            masini = Parsing(inputStream);

        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return inputStream;
    }

    public List<Masina> Parsing(InputStream inputStream) {
        List<Masina> masiniList = new ArrayList<>();
        JSONObject jsonObject = null;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String jsonString = stringBuilder.toString();
            jsonObject = new JSONObject(jsonString);

            JSONArray masiniArray = jsonObject.getJSONArray("masini");
            for (int i = 0; i < masiniArray.length(); i++) {
                JSONObject masinaObject = masiniArray.getJSONObject(i);
                String marca = masinaObject.getString("Marca");
                Date dataFabricatie = new Date(masinaObject.getString("DataFabricatiei"));
                float pret = (float) masinaObject.getDouble("Pret");
                String culoare = masinaObject.getString("Culoare");
                String motorizare = masinaObject.getString("Motorizare");
                motorizare = motorizare.toLowerCase();
                motorizare = motorizare.substring(0, 1).toUpperCase() + motorizare.substring(1);

                Masina masina = new Masina(marca, dataFabricatie, pret, culoare, motorizare);
                masiniList.add(masina);
            }



        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return masiniList;
    }
}
