package com.bill.accounty.web;

import com.bill.accounty.model.BilanzAntwort;
import com.bill.accounty.model.BilanzPosition;
import com.bill.accounty.service.HgbKategorisierungService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/bilanz")
public class BilanzController {

    private final HgbKategorisierungService kategorisierungService;

    public BilanzController(HgbKategorisierungService kategorisierungService) {
        this.kategorisierungService = kategorisierungService;
    }

    /**
     * Unterstützte Formate:
     * - CSV: kontonummer;bezeichnung;betrag (erste Zeile optional Header)
     * - Excel (.xls/.xlsx): erste Zeile Header, Spalten "Konto*", "Bezeich*", "Betrag*"
     * - PDF: zeilenweise Export im CSV-ähnlichen Format (kontonummer;bezeichnung;betrag)
     */
    @PostMapping(
            path = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BilanzAntwort> uploadBilanz(@RequestParam("file") MultipartFile file) throws IOException {
        List<BilanzPosition> allePositionen = parseFileToPositionen(file);

        List<BilanzPosition> aktiva = new ArrayList<>();
        List<BilanzPosition> passiva = new ArrayList<>();

        BigDecimal summeAktiva = BigDecimal.ZERO;
        BigDecimal summePassiva = BigDecimal.ZERO;

        for (BilanzPosition pos : allePositionen) {
            String[] kategorisierung = kategorisierungService.kategorisiere(
                    pos.getKontonummer(),
                    pos.getBezeichnung()
            );

            String kategorie = kategorisierung[0];
            String unterkategorie = kategorisierung[1];
            String seite = kategorisierung[2]; // "AKTIVA" oder "PASSIVA"

            pos.setKategorie(kategorie);
            pos.setUnterkategorie(unterkategorie);
            pos.setSeite(seite);

            if ("PASSIVA".equalsIgnoreCase(seite)) {
                passiva.add(pos);
                summePassiva = summePassiva.add(pos.getBetrag());
            } else {
                aktiva.add(pos);
                summeAktiva = summeAktiva.add(pos.getBetrag());
            }
        }

        BilanzAntwort antwort = new BilanzAntwort();
        antwort.setAktiva(aktiva);
        antwort.setPassiva(passiva);
        antwort.setSummeAktiva(summeAktiva);
        antwort.setSummePassiva(summePassiva);
        antwort.setBilanziert(summeAktiva.compareTo(summePassiva) == 0);

        return ResponseEntity.ok(antwort);
    }

    private List<BilanzPosition> parseFileToPositionen(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        String name = filename != null ? filename.toLowerCase() : "";

        if (name.endsWith(".xlsx") || name.endsWith(".xls")) {
            return parseExcel(file.getInputStream());
        }
        if (name.endsWith(".pdf")) {
            return parsePdf(file);
        }
        // Fallback: CSV / Text
        return parseCsv(file.getInputStream());
    }

    private List<BilanzPosition> parseCsv(InputStream in) throws IOException {
        List<BilanzPosition> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8))) {

            String line;
            boolean first = true;

            while ((line = reader.readLine()) != null) {
                if (first) {
                    first = false;
                    // Header überspringen, falls vorhanden
                    if (line.toLowerCase().contains("konto") || line.toLowerCase().contains("betrag")) {
                        continue;
                    }
                }

                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split("[;,\t]");
                if (parts.length < 3) {
                    continue;
                }

                String konto = parts[0].trim();
                String bezeichnung = parts[1].trim();
                String betragStr = parts[2].trim().replace(".", "").replace(",", ".");

                BigDecimal betrag;
                try {
                    betrag = new BigDecimal(betragStr);
                } catch (NumberFormatException ex) {
                    continue;
                }

                BilanzPosition pos = new BilanzPosition();
                pos.setKontonummer(konto);
                pos.setBezeichnung(bezeichnung);
                pos.setBetrag(betrag);

                result.add(pos);
            }
        }

        return result;
    }

    private List<BilanzPosition> parseExcel(InputStream in) throws IOException {
        List<BilanzPosition> result = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(in)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return result;
            }

            DataFormatter formatter = new DataFormatter();
            int kontoCol = 0;
            int bezCol = 1;
            int betragCol = 2;

            // Header auswerten, falls vorhanden
            Row header = sheet.getRow(sheet.getFirstRowNum());
            if (header != null) {
                for (int i = header.getFirstCellNum(); i < header.getLastCellNum(); i++) {
                    String val = formatter.formatCellValue(header.getCell(i)).toLowerCase();
                    if (val.contains("konto")) {
                        kontoCol = i;
                    } else if (val.contains("bezeich")) {
                        bezCol = i;
                    } else if (val.contains("betrag") || val.contains("saldo")) {
                        betragCol = i;
                    }
                }
            }

            int firstDataRow = sheet.getFirstRowNum() + 1;
            for (int r = firstDataRow; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }

                String konto = formatter.formatCellValue(row.getCell(kontoCol)).trim();
                String bezeichnung = formatter.formatCellValue(row.getCell(bezCol)).trim();
                String betragStr = formatter.formatCellValue(row.getCell(betragCol)).trim()
                        .replace(".", "")
                        .replace(",", ".");

                if (bezeichnung.isBlank() && konto.isBlank()) {
                    continue;
                }

                BigDecimal betrag;
                try {
                    betrag = new BigDecimal(betragStr);
                } catch (NumberFormatException ex) {
                    continue;
                }

                BilanzPosition pos = new BilanzPosition();
                pos.setKontonummer(konto);
                pos.setBezeichnung(bezeichnung);
                pos.setBetrag(betrag);

                result.add(pos);
            }
        }

        return result;
    }

    private List<BilanzPosition> parsePdf(MultipartFile file) throws IOException {
        List<BilanzPosition> result = new ArrayList<>();

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            String[] lines = text.split("\\R");
            boolean first = true;

            for (String line : lines) {
                if (first) {
                    first = false;
                    if (line.toLowerCase().contains("konto") || line.toLowerCase().contains("betrag")) {
                        continue;
                    }
                }

                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split("[;,\t]");
                if (parts.length < 3) {
                    continue;
                }

                String konto = parts[0].trim();
                String bezeichnung = parts[1].trim();
                String betragStr = parts[2].trim().replace(".", "").replace(",", ".");

                BigDecimal betrag;
                try {
                    betrag = new BigDecimal(betragStr);
                } catch (NumberFormatException ex) {
                    continue;
                }

                BilanzPosition pos = new BilanzPosition();
                pos.setKontonummer(konto);
                pos.setBezeichnung(bezeichnung);
                pos.setBetrag(betrag);

                result.add(pos);
            }
        }

        return result;
    }
}

