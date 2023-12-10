package com.nevexo.proiectseminar.Network;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.nevexo.proiectseminar.Controller.CursValutar;
import com.nevexo.proiectseminar.Model.Curs;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Network extends AsyncTask<URL, Void, InputStream> {

    InputStream inputStream = null;
    public static String result = null;

    public static Curs cursValutar = null;

    @Override
    protected InputStream doInBackground(URL... urls) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) urls[0].openConnection();
            connection.setRequestMethod("GET");
            inputStream = connection.getInputStream();

            Parsing(inputStream);

//            BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(inputStream));
//            StringBuilder stringBuilder = new StringBuilder();
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                stringBuilder.append(line);
//            }
//            result = stringBuilder.toString();
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return inputStream;
    }

    public static Node getNode(String tagName, Node parent) throws Exception {
        if(parent.getNodeName().equals(tagName)) {
            return parent;
        }
        NodeList list = parent.getChildNodes();
        for(int i = 0; i < list.getLength(); i++) {
            Node node = getNode(tagName, list.item(i));
            if(node != null) {
                return node;
            }
        }
        return null;
    }

    public static String getNodeValue(Node node, String tagName) {
        try {
            return ((Element)node).getAttribute(tagName);
        } catch (Exception e) {
            return "";
        }
    }

    public void Parsing(InputStream inputStream)
    {
//        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//        try {
//            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//            Document document = documentBuilder.parse(inputStream);
//            document.getDocumentElement().normalize();
//
//            cursValutar = new Curs();
//
//            Node cube = getNode("Cube", document.getDocumentElement());
//            if(cube != null)
//            {
//                cursValutar.setDataCurs(new Date());
//
//                NodeList nodeList = cube.getChildNodes();
//                for(int i = 0; i < nodeList.getLength(); i++)
//                {
//                    Node rate = nodeList.item(i);
//                    String currency = getNodeValue(rate, "currency");
//                    if(currency.equals("EUR"))
//                    {
//                        cursValutar.setEUR(Float.parseFloat(rate.getTextContent()));
//                    }
//                    if(currency.equals("USD"))
//                    {
//                        cursValutar.setUSD(Float.parseFloat(rate.getTextContent()));
//                    }
//                    if(currency.equals("GBP"))
//                    {
//                        cursValutar.setGBP(Float.parseFloat(rate.getTextContent()));
//                    }
//                    if(currency.equals("XAU"))
//                    {
//                        cursValutar.setXAU(Float.parseFloat(rate.getTextContent()));
//                    }
//                }
//            }
//
//
//
//        } catch (ParserConfigurationException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (SAXException e) {
//            throw new RuntimeException(e);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        XmlPullParser parser = Xml.newPullParser();

        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();

            cursValutar = new Curs();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG) {
                    String name = parser.getName();

                    if (name.equals("Cube")) {
                        cursValutar.setDataCurs(new Date());
                    } else if (name.equals("Rate")) {
                        String currency = parser.getAttributeValue(null, "currency");
                        if(currency.equals("EUR"))
                        {
                            cursValutar.setEUR(Float.parseFloat(parser.nextText()));
                        }
                        if(currency.equals("USD"))
                        {
                            cursValutar.setUSD(Float.parseFloat(parser.nextText()));
                        }
                        if(currency.equals("GBP"))
                        {
                            cursValutar.setGBP(Float.parseFloat(parser.nextText()));
                        }
                        if(currency.equals("XAU"))
                        {
                            cursValutar.setXAU(Float.parseFloat(parser.nextText()));
                        }
                    }
                }

                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
