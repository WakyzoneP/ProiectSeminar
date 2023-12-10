package com.nevexo.proiectseminar.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nevexo.proiectseminar.Model.Masina;
import com.nevexo.proiectseminar.R;

import java.util.List;

public class CustomMasinaAdapter extends ArrayAdapter<Masina> {

    private Context context;
    private int resource;
    private List<Masina> listMasini;
    private LayoutInflater layoutInflater;

    public CustomMasinaAdapter(@NonNull Context context, int resource, @NonNull List<Masina> objects, LayoutInflater layoutInflater) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.listMasini = objects;
        this.layoutInflater = layoutInflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = layoutInflater.inflate(resource, parent, false);
        Masina masina = listMasini.get(position);

        if(masina != null)
        {
            TextView textViewMarca = (TextView) view.findViewById(R.id.TextViewMarca);
            textViewMarca.setText(masina.getMarca());

            TextView textViewData = (TextView) view.findViewById(R.id.TextViewDataFabricatiei);
            textViewData.setText(masina.getDataFabricatie().toString());

            TextView textViewPret = (TextView) view.findViewById(R.id.TextViewPret);
            textViewPret.setText(String.valueOf(masina.getPret()));

            TextView textViewCuloare = (TextView) view.findViewById(R.id.TextViewCuloare);
            textViewCuloare.setText(masina.getCuloare());

            TextView textViewMotorizare = (TextView) view.findViewById(R.id.TextViewMotorizare);
            textViewMotorizare.setText(masina.getMotorizare());
        }

        return view;
    }
}
