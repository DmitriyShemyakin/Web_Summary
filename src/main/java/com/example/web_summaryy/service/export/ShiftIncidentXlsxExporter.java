package com.example.web_summaryy.service.export;

import com.example.web_summaryy.model.Direction;
import com.example.web_summaryy.model.Incident;
import com.example.web_summaryy.model.IncidentStatus;
import com.example.web_summaryy.model.Position;
import com.example.web_summaryy.model.Shift;
import com.example.web_summaryy.model.TechCentre;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Формирует XLSX со списком аварий за смену
 */
@Component
public class ShiftIncidentXlsxExporter {

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final int COLUMN_COUNT = 20;

    public byte[] export(Shift shift, List<Incident> incidents) throws IOException {
        Objects.requireNonNull(shift, "shift");
        Objects.requireNonNull(incidents, "incidents");
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Аварии");

            int r = 0;
            LocalDateTime exportNow = LocalDateTime.now();

            for (Incident inc : incidents) {
                Row row = sheet.createRow(r++);
                int c = 0;
                row.createCell(c++).setCellValue(inc.getId() != null ? inc.getId().doubleValue() : 0);
                setStr(row, c++, inc.getIncidentNumber());
                setStr(row, c++, directionTitles(inc));
                setStr(row, c++, techCentreTitles(inc));
                if (inc.getPositionLevel() != null) {
                    row.createCell(c++).setCellValue(inc.getPositionLevel().doubleValue());
                } else {
                    setStr(row, c++, "");
                }
                setStr(row, c++, inc.getNetworkType() != null ? inc.getNetworkType().getTitle() : "");
                setStr(row, c++, positionNamesMultiline(inc));
                setStr(row, c++, inc.getBaseStationsCount());
                setStr(row, c++, inc.getEquipmentType());
                setStr(row, c++, inc.getIncidentType() != null ? inc.getIncidentType().getTypeCode() : "");
                setStr(row, c++, inc.getIncidentCategory() != null ? inc.getIncidentCategory().getCategoryName() : "");
                setStr(row, c++, formatDt(inc.getStartedAt()));
                setStr(row, c++, formatDt(inc.getEndedAt()));
                setStr(row, c++, inc.getAkbDuration());
                setStr(row, c++, inc.getGuDuration());
                setStr(row, c++, durationCell(inc, exportNow));
                setStr(row, c++, inc.getConnectionDowntime());
                setStr(row, c++, inc.getBsTotalDowntime());
                setStr(row, c++, inc.getNotificationText());
                setStr(row, c, inc.getDescription());
            }

            for (int c = 0; c < COLUMN_COUNT; c++) {
                sheet.autoSizeColumn(c);
                int w = sheet.getColumnWidth(c);
                if (w > 20000) {
                    sheet.setColumnWidth(c, 20000);
                }
            }

            wb.write(out);
            return out.toByteArray();
        }
    }

    private static String durationCell(Incident inc, LocalDateTime exportNow) {
        if (inc.getStatus() == IncidentStatus.OPEN) {
            String live = formatDurationHHMM(inc.getStartedAt(), exportNow);
            return "НВ (" + live + ")";
        }
        return formatDurationHHMM(inc.getStartedAt(), inc.getEndedAt());
    }

    private static String formatDurationHHMM(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return "—";
        }
        long minutes = ChronoUnit.MINUTES.between(start, end);
        if (minutes < 0) {
            return "—";
        }
        long h = minutes / 60;
        long m = minutes % 60;
        return String.format("%02d:%02d", h, m);
    }

    private static void setStr(Row row, int col, String v) {
        Cell cell = row.createCell(col);
        if (v != null) {
            cell.setCellValue(v);
        } else {
            cell.setCellValue("");
        }
    }

    private static String formatDt(LocalDateTime t) {
        return t != null ? t.format(DT) : "";
    }

    private static String positionNamesMultiline(Incident inc) {
        if (inc.getPositions() == null || inc.getPositions().isEmpty()) {
            return "";
        }
        return inc.getPositions().stream()
                .map(p -> {
                    String name = p.getPositionNameforbs();
                    String addr = p.getAddressStr();
                    if (name == null) {
                        return "";
                    }
                    return addr != null && !addr.isBlank() ? name + " - " + addr : name;
                })
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("\n"));
    }

    private static String directionTitles(Incident inc) {
        if (inc.getPositions() == null || inc.getPositions().isEmpty()) {
            return "";
        }
        return inc.getPositions().stream()
                .map(Position::getPositionTechnapr)
                .filter(Objects::nonNull)
                .map(Direction::getTitle)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining("; "));
    }

    private static String techCentreTitles(Incident inc) {
        if (inc.getPositions() == null || inc.getPositions().isEmpty()) {
            return "";
        }
        return inc.getPositions().stream()
                .map(Position::getPositionTechcentre)
                .filter(Objects::nonNull)
                .map(TechCentre::getTitle)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining("; "));
    }
}
