package com.nevexo.proiectseminar.Model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "masini")
public class Masina implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String marca;
    private Date dataFabricatie;
    private float pret;
    private String culoare;
    private String motorizare; // gasoline, hybrid, electric

    @Ignore
    private String uid;

    @Ignore
    public Masina(){}

    public Masina(String marca, Date dataFabricatie, float pret, String culoare, String motorizare) {
        this.marca = marca;
        this.dataFabricatie = dataFabricatie;
        this.pret = pret;
        this.culoare = culoare;
        this.motorizare = motorizare;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Date getDataFabricatie() {
        return dataFabricatie;
    }

    public void setDataFabricatie(Date dataFabricatie) {
        this.dataFabricatie = dataFabricatie;
    }

    public float getPret() {
        return pret;
    }

    public void setPret(float pret) {
        this.pret = pret;
    }

    public String getCuloare() {
        return culoare;
    }

    public void setCuloare(String culoare) {
        this.culoare = culoare;
    }

    public String getMotorizare() {
        return motorizare;
    }

    public void setMotorizare(String motorizare) {
        this.motorizare = motorizare;
    }

    @Override
    public String toString() {
        return "Masina{" +
                "marca='" + marca + '\'' +
                ", dataFabricatie=" + dataFabricatie +
                ", pret=" + pret +
                ", culoare='" + culoare + '\'' +
                ", motorizare='" + motorizare + '\'' +
                '}';
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}