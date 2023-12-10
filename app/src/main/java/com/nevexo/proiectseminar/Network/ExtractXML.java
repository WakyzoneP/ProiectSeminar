package com.nevexo.proiectseminar.Network;

import android.os.AsyncTask;
import android.util.Xml;

import com.nevexo.proiectseminar.Model.Masina;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExtractXML extends AsyncTask<URL, Void, InputStream> {
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
        XmlPullParser parser = Xml.newPullParser();
        List<Masina> masiniList = new ArrayList<>();

        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            Masina newMasina = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    if (name.equals("Masina")) {
                        if (newMasina != null)
                            masiniList.add(newMasina);

                        newMasina = new Masina();
                    } else if (name.equals("Tag")) {
                        String attribute = parser.getAttributeValue(null, "atribut");
                        if (attribute.equals("Marca")) {
                            newMasina.setMarca(parser.nextText());
                        } else if (attribute.equals("DataFabricatiei")) {
                            newMasina.setDataFabricatie(new Date(parser.nextText()));
                        } else if (attribute.equals("Pret")) {
                            newMasina.setPret(Float.parseFloat(parser.nextText()));
                        } else if (attribute.equals("Culoare")) {
                            newMasina.setCuloare(parser.nextText());
                        } else if (attribute.equals("Motorizare")) {
                            newMasina.setMotorizare(parser.nextText());
                        }
                    }

                }

                eventType = parser.next();
            }

            if (newMasina != null)
                masiniList.add(newMasina);
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return masiniList;
    }
}
