package com.bill.accounty.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class HgbKategorisierungService {

    // Aktiva-Konten nach SKR03/SKR04
    private static final Map<String, String[]> AKTIVA_MAP = new HashMap<>();
    // Passiva-Konten nach SKR03/SKR04
    private static final Map<String, String[]> PASSIVA_MAP = new HashMap<>();

    static {
        // === AKTIVA ===
        // A. Anlagevermögen
        // I. Immaterielle Vermögensgegenstände
        AKTIVA_MAP.put("0100", new String[]{"Anlagevermögen", "Immaterielle Vermögensgegenstände", "AKTIVA"});
        AKTIVA_MAP.put("0120", new String[]{"Anlagevermögen", "Immaterielle Vermögensgegenstände", "AKTIVA"});
        AKTIVA_MAP.put("0130", new String[]{"Anlagevermögen", "Immaterielle Vermögensgegenstände", "AKTIVA"});

        // II. Sachanlagen
        AKTIVA_MAP.put("0200", new String[]{"Anlagevermögen", "Sachanlagen", "AKTIVA"});
        AKTIVA_MAP.put("0210", new String[]{"Anlagevermögen", "Sachanlagen", "AKTIVA"});
        AKTIVA_MAP.put("0220", new String[]{"Anlagevermögen", "Sachanlagen", "AKTIVA"});
        AKTIVA_MAP.put("0240", new String[]{"Anlagevermögen", "Sachanlagen", "AKTIVA"});
        AKTIVA_MAP.put("0260", new String[]{"Anlagevermögen", "Sachanlagen", "AKTIVA"});
        AKTIVA_MAP.put("0300", new String[]{"Anlagevermögen", "Sachanlagen", "AKTIVA"});

        // III. Finanzanlagen
        AKTIVA_MAP.put("0500", new String[]{"Anlagevermögen", "Finanzanlagen", "AKTIVA"});
        AKTIVA_MAP.put("0510", new String[]{"Anlagevermögen", "Finanzanlagen", "AKTIVA"});
        AKTIVA_MAP.put("0520", new String[]{"Anlagevermögen", "Finanzanlagen", "AKTIVA"});

        // B. Umlaufvermögen
        // I. Vorräte
        AKTIVA_MAP.put("1000", new String[]{"Umlaufvermögen", "Vorräte", "AKTIVA"});
        AKTIVA_MAP.put("1100", new String[]{"Umlaufvermögen", "Vorräte", "AKTIVA"});
        AKTIVA_MAP.put("1200", new String[]{"Umlaufvermögen", "Vorräte", "AKTIVA"});

        // II. Forderungen
        AKTIVA_MAP.put("1400", new String[]{"Umlaufvermögen", "Forderungen und sonstige Vermögensgegenstände", "AKTIVA"});
        AKTIVA_MAP.put("1410", new String[]{"Umlaufvermögen", "Forderungen und sonstige Vermögensgegenstände", "AKTIVA"});
        AKTIVA_MAP.put("1500", new String[]{"Umlaufvermögen", "Forderungen und sonstige Vermögensgegenstände", "AKTIVA"});
        AKTIVA_MAP.put("1510", new String[]{"Umlaufvermögen", "Forderungen und sonstige Vermögensgegenstände", "AKTIVA"});

        // III. Wertpapiere
        AKTIVA_MAP.put("1600", new String[]{"Umlaufvermögen", "Wertpapiere", "AKTIVA"});

        // IV. Kassenbestand / Bank
        AKTIVA_MAP.put("1700", new String[]{"Umlaufvermögen", "Kassenbestand, Bankguthaben", "AKTIVA"});
        AKTIVA_MAP.put("1710", new String[]{"Umlaufvermögen", "Kassenbestand, Bankguthaben", "AKTIVA"});
        AKTIVA_MAP.put("1800", new String[]{"Umlaufvermögen", "Kassenbestand, Bankguthaben", "AKTIVA"});

        // C. Rechnungsabgrenzungsposten Aktiva
        AKTIVA_MAP.put("1900", new String[]{"Rechnungsabgrenzungsposten", "Aktiver RAP", "AKTIVA"});

        // === PASSIVA ===
        // A. Eigenkapital
        PASSIVA_MAP.put("2000", new String[]{"Eigenkapital", "Gezeichnetes Kapital", "PASSIVA"});
        PASSIVA_MAP.put("2010", new String[]{"Eigenkapital", "Gezeichnetes Kapital", "PASSIVA"});
        PASSIVA_MAP.put("2100", new String[]{"Eigenkapital", "Kapitalrücklage", "PASSIVA"});
        PASSIVA_MAP.put("2200", new String[]{"Eigenkapital", "Gewinnrücklagen", "PASSIVA"});
        PASSIVA_MAP.put("2300", new String[]{"Eigenkapital", "Gewinnvortrag/Verlustvortrag", "PASSIVA"});
        PASSIVA_MAP.put("2400", new String[]{"Eigenkapital", "Jahresüberschuss/Jahresfehlbetrag", "PASSIVA"});

        // B. Rückstellungen
        PASSIVA_MAP.put("3000", new String[]{"Rückstellungen", "Steuerrückstellungen", "PASSIVA"});
        PASSIVA_MAP.put("3100", new String[]{"Rückstellungen", "Sonstige Rückstellungen", "PASSIVA"});
        PASSIVA_MAP.put("3200", new String[]{"Rückstellungen", "Pensionsrückstellungen", "PASSIVA"});

        // C. Verbindlichkeiten
        PASSIVA_MAP.put("3300", new String[]{"Verbindlichkeiten", "Verbindlichkeiten ggü. Kreditinstituten", "PASSIVA"});
        PASSIVA_MAP.put("3400", new String[]{"Verbindlichkeiten", "Verbindlichkeiten ggü. Kreditinstituten", "PASSIVA"});
        PASSIVA_MAP.put("3500", new String[]{"Verbindlichkeiten", "Verbindlichkeiten aus Lieferungen", "PASSIVA"});
        PASSIVA_MAP.put("3600", new String[]{"Verbindlichkeiten", "Verbindlichkeiten aus Lieferungen", "PASSIVA"});
        PASSIVA_MAP.put("3700", new String[]{"Verbindlichkeiten", "Sonstige Verbindlichkeiten", "PASSIVA"});
        PASSIVA_MAP.put("3800", new String[]{"Verbindlichkeiten", "Sonstige Verbindlichkeiten", "PASSIVA"});

        // D. Rechnungsabgrenzungsposten Passiva
        PASSIVA_MAP.put("3900", new String[]{"Rechnungsabgrenzungsposten", "Passiver RAP", "PASSIVA"});
    }

    /**
     * Kategorisiert eine Position anhand ihrer Kontonummer (HGB §266)
     */
    public String[] kategorisiere(String kontonummer, String bezeichnung) {
        if (kontonummer == null || kontonummer.isBlank()) {
            return kategorisiereNachBezeichnung(bezeichnung);
        }

        // Exakter Match
        String[] result = AKTIVA_MAP.get(kontonummer);
        if (result != null) return result;

        result = PASSIVA_MAP.get(kontonummer);
        if (result != null) return result;

        // Prefix-Match (erste 2 Stellen)
        String prefix2 = kontonummer.length() >= 2 ? kontonummer.substring(0, 2) : kontonummer;
        for (Map.Entry<String, String[]> entry : AKTIVA_MAP.entrySet()) {
            if (entry.getKey().startsWith(prefix2)) return entry.getValue();
        }
        for (Map.Entry<String, String[]> entry : PASSIVA_MAP.entrySet()) {
            if (entry.getKey().startsWith(prefix2)) return entry.getValue();
        }

        // Fallback: nach Bezeichnung
        return kategorisiereNachBezeichnung(bezeichnung);
    }

    private String[] kategorisiereNachBezeichnung(String bezeichnung) {
        if (bezeichnung == null) return new String[]{"Sonstige", "Nicht kategorisiert", "AKTIVA"};
        String bez = bezeichnung.toLowerCase();

        // Aktiva Keywords
        if (bez.contains("anlage") || bez.contains("maschine") || bez.contains("gebäude") || bez.contains("fuhrpark"))
            return new String[]{"Anlagevermögen", "Sachanlagen", "AKTIVA"};
        if (bez.contains("software") || bez.contains("lizenz") || bez.contains("patent") || bez.contains("goodwill"))
            return new String[]{"Anlagevermögen", "Immaterielle Vermögensgegenstände", "AKTIVA"};
        if (bez.contains("kasse") || bez.contains("bank") || bez.contains("guthaben"))
            return new String[]{"Umlaufvermögen", "Kassenbestand, Bankguthaben", "AKTIVA"};
        if (bez.contains("forderung") || bez.contains("debitor"))
            return new String[]{"Umlaufvermögen", "Forderungen und sonstige Vermögensgegenstände", "AKTIVA"};
        if (bez.contains("vorrat") || bez.contains("ware") || bez.contains("lager") || bez.contains("rohstoff"))
            return new String[]{"Umlaufvermögen", "Vorräte", "AKTIVA"};

        // Passiva Keywords
        if (bez.contains("kapital") || bez.contains("eigenkapital"))
            return new String[]{"Eigenkapital", "Gezeichnetes Kapital", "PASSIVA"};
        if (bez.contains("rückstellung"))
            return new String[]{"Rückstellungen", "Sonstige Rückstellungen", "PASSIVA"};
        if (bez.contains("verbindlichkeit") || bez.contains("kreditor") || bez.contains("darlehen"))
            return new String[]{"Verbindlichkeiten", "Verbindlichkeiten aus Lieferungen", "PASSIVA"};
        if (bez.contains("rücklage"))
            return new String[]{"Eigenkapital", "Gewinnrücklagen", "PASSIVA"};
        if (bez.contains("gewinn") || bez.contains("überschuss"))
            return new String[]{"Eigenkapital", "Jahresüberschuss/Jahresfehlbetrag", "PASSIVA"};

        return new String[]{"Sonstige", "Nicht kategorisiert", "AKTIVA"};
    }
}

