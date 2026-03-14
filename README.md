# BillAccounty HGB

Interaktives Buchhaltungstool zur automatischen Bilanzanalyse nach **HGB §266**.
Unterstützt Excel (.xlsx) und PDF-Dateien. Kategorisiert Bestände nach SKR03/SKR04.

---

## Projektstruktur

```
bilanz-tool/
├── backend/          → Spring Boot (Java 17)
│   ├── pom.xml
│   └── src/main/java/com/bilanz/
│       ├── BilanzApplication.java
│       ├── controller/BilanzController.java
│       ├── service/BilanzService.java
│       ├── service/HgbKategorisierungService.java
│       ├── parser/ExcelParserService.java
│       ├── parser/PdfParserService.java
│       └── model/
│           ├── BilanzPosition.java
│           └── BilanzResponse.java
└── frontend/         → Vue 3 + Vite
    ├── package.json
    ├── vite.config.js
    ├── index.html
    └── src/
        ├── main.js
        ├── App.vue
        ├── stores/bilanzStore.js
        └── components/
            ├── UploadZone.vue
            ├── BilanzSeite.vue
            ├── BilanzChart.vue
            └── StatusBar.vue
```

---

## 🚀 Setup & Start

### Backend (Spring Boot)

**Voraussetzungen:** Java 17+, Maven 3.8+

```bash
cd backend
mvn clean install
mvn spring-boot:run
# → läuft auf http://localhost:8080
```

### Frontend (Vue 3)

**Voraussetzungen:** Node.js 18+

```bash
cd frontend
npm install
npm run dev
# → läuft auf http://localhost:5173
```

Der Vite Dev-Server proxied `/api/*` automatisch zu `localhost:8080`.

---

## 📋 Features

| Feature | Beschreibung |
|---|---|
| Excel-Import | .xlsx Dateien mit automatischer Spaltenerkennung |
| PDF-Import | Regex-basierte Extraktion von Bilanzpositionen |
| HGB §266 | Kategorisierung nach Aktiva/Passiva-Struktur |
| SKR03/SKR04 | Kontennummern-basierte Zuordnung |
| Filterung | Interaktiv nach Kategorien filtern |
| Drill-Down | Aufklappbare Kategorien und Unterkategorien |
| Chart | Doughnut-Charts für Aktiva- & Passiva-Struktur |
| Bilanzprüfung | Automatische Gleichungsprüfung Aktiva = Passiva |

---

## 📄 Excel-Format

Die Eingabedatei sollte folgende Spalten enthalten:

| Kontonummer | Bezeichnung | Betrag |
|---|---|---|
| 1400 | Forderungen aus L+L | 125000,00 |
| 3500 | Verbindlichkeiten L+L | 85000,00 |

Spaltenbezeichnungen werden automatisch erkannt (deutsch/englisch).

---

## API Endpoints

```
POST /api/bilanz/upload    → Datei hochladen & analysieren
GET  /api/bilanz/health    → Health-Check
```

### Response-Struktur

```json
{
  "dateiname": "bilanz_2024.xlsx",
  "aktiva": [
    {
      "kontonummer": "1400",
      "bezeichnung": "Forderungen aus LuL",
      "betrag": 125000.00,
      "kategorie": "Umlaufvermögen",
      "unterkategorie": "Forderungen und sonstige Vermögensgegenstände",
      "seite": "AKTIVA"
    }
  ],
  "passiva": [...],
  "summeAktiva": 500000.00,
  "summePassiva": 500000.00,
  "bilanziert": true,
  "kategorienSummen": {
    "AKTIVA_Umlaufvermögen": 250000.00,
    "PASSIVA_Eigenkapital": 200000.00
  }
}
```

---

## Erweiterungsideen

- PDF-Tabellenerkennung mit Apache Tika
- Export als PDF-Bericht
- Mehrjahresvergleich
- DATEV-Import
- Benutzerauth mit Spring Security
