package com.nevexo.proiectseminar.Model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

public class Curs implements Serializable {
    private Date dataCurs;
    private float EUR;
    private float USD;
    private float GBP;
    private float XAU;

    public Curs() {}

    public Curs(Date dataCurs, float  EUR, float USD, float GBP, float XAU) {
        this.dataCurs = dataCurs;
        this.EUR = EUR;
        this.USD = USD;
        this.GBP = GBP;
        this.XAU = XAU;
    }

    @NonNull
    @Override
    public String toString() {
        return "Curs{" +
                "dataCurs=" + dataCurs.toString() +
                ", EUR=" + EUR +
                ", USD=" + USD +
                ", GBP=" + GBP +
                ", XAU=" + XAU +
                '}';
    }

    public Date getDataCurs() {
        return dataCurs;
    }

    public void setDataCurs(Date dataCurs) {
        this.dataCurs = dataCurs;
    }

    public float getEUR() {
        return EUR;
    }

    public void setEUR(float EUR) {
        this.EUR = EUR;
    }

    public float getUSD() {
        return USD;
    }

    public void setUSD(float USD) {
        this.USD = USD;
    }

    public float getGBP() {
        return GBP;
    }

    public void setGBP(float GBP) {
        this.GBP = GBP;
    }

    public float getXAU() {
        return XAU;
    }

    public void setXAU(float XAU) {
        this.XAU = XAU;
    }
}
