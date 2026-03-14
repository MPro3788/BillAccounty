package com.bill.accounty.model;

import java.math.BigDecimal;

public class BilanzPosition {

    private String kontonummer;
    private String bezeichnung;
    private BigDecimal betrag;
    private String kategorie;
    private String unterkategorie;
    private String seite; // "AKTIVA" oder "PASSIVA"

    public String getKontonummer() {
        return kontonummer;
    }

    public void setKontonummer(String kontonummer) {
        this.kontonummer = kontonummer;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public BigDecimal getBetrag() {
        return betrag;
    }

    public void setBetrag(BigDecimal betrag) {
        this.betrag = betrag;
    }

    public String getKategorie() {
        return kategorie;
    }

    public void setKategorie(String kategorie) {
        this.kategorie = kategorie;
    }

    public String getUnterkategorie() {
        return unterkategorie;
    }

    public void setUnterkategorie(String unterkategorie) {
        this.unterkategorie = unterkategorie;
    }

    public String getSeite() {
        return seite;
    }

    public void setSeite(String seite) {
        this.seite = seite;
    }
}

