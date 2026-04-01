package com.example.web_summaryy.controller;

import com.example.web_summaryy.dto.incident.IncidentDtoResponse;
import com.example.web_summaryy.dto.shift.EndShiftRequest;
import com.example.web_summaryy.dto.shift.EndShiftResponse;
import com.example.web_summaryy.dto.shift.ShiftDtoResponse;
import com.example.web_summaryy.dto.shift.StartShiftRequest;
import com.example.web_summaryy.model.User;
import com.example.web_summaryy.service.ShiftService;
import com.example.web_summaryy.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shifts")
public class ShiftController {

    private final ShiftService shiftService;
    private final UserService userService;

    public ShiftController(ShiftService shiftService, UserService userService) {
        this.shiftService = shiftService;
        this.userService = userService;
    }

    /**
     * Начать новую смену
     *
     * POST /api/shifts/start
     */
    @PostMapping("/start")
    public ResponseEntity<ShiftDtoResponse> startShift(
            @RequestBody(required = false) StartShiftRequest request) {

        User currentUser = userService.getCurrentUser();

        if (request == null) {
            request = new StartShiftRequest();
        }

        ShiftDtoResponse shift = shiftService.startShift(request, currentUser);
        return ResponseEntity.ok(shift);
    }

    /**
     * Завершить текущую смену
     *
     * POST /api/shifts/end
     */
    @PostMapping("/end")
    public ResponseEntity<EndShiftResponse> endShift(@RequestBody(required = false) EndShiftRequest request) {
        User currentUser = userService.getCurrentUser();

        if (request == null) {
            request = EndShiftRequest.builder().generateReport(true).build();
        }

        EndShiftResponse response = shiftService.endShift(request, currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * Получить текущую активную смену
     *
     * GET /api/shifts/current
     */
    @GetMapping("/current")
    public ResponseEntity<ShiftDtoResponse> getCurrentShift() {
        User currentUser = userService.getCurrentUser();
        ShiftDtoResponse shift = shiftService.getCurrentShift(currentUser);

        if (shift == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(shift);
    }

    /**
     * Получить смену по ID
     *
     * GET /api/shifts/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShiftDtoResponse> getShiftById(@PathVariable Long id) {

        User currentUser = userService.getCurrentUser();
        ShiftDtoResponse shift = shiftService.getShiftById(id, currentUser);
        return ResponseEntity.ok(shift);
    }

    /**
     * Получить историю смен текущего пользователя
     *
     * GET /api/shifts/history
     */
    @GetMapping("/history")
    public ResponseEntity<Page<ShiftDtoResponse>> getShiftHistory(@PageableDefault(size = 20) Pageable pageable) {
        User currentUser = userService.getCurrentUser();

        Page<ShiftDtoResponse> shifts = shiftService.getShiftHistory(currentUser, pageable);
        return ResponseEntity.ok(shifts);
    }

    /**
     * Получить все смены (для админа/старшего дежурного)
     *
     * GET /api/shifts/all
     */
    @GetMapping("/all")
    public ResponseEntity<Page<ShiftDtoResponse>> getAllShifts(@PageableDefault(size = 20) Pageable pageable) {

        Page<ShiftDtoResponse> shifts = shiftService.getAllShifts(pageable);
        return ResponseEntity.ok(shifts);
    }

    /**
     * Получить аварии за смену
     *
     * GET /api/shifts/{id}/incidents
     */
    @GetMapping("/{id}/incidents")
    public ResponseEntity<List<IncidentDtoResponse>> getShiftIncidents(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();

        List<IncidentDtoResponse> incidents = shiftService.getShiftIncidents(id, currentUser);
        return ResponseEntity.ok(incidents);
    }

    /**
     * Скачать все аварии смены в XLSX (видимые текущему пользователю).
     *
     * GET /api/shifts/{id}/export.xlsx
     */
    @GetMapping("/{id}/export.xlsx")
    public ResponseEntity<byte[]> exportShiftIncidentsXlsx(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        byte[] bytes = shiftService.exportShiftIncidentsAsXlsx(id, currentUser);
        String filename = "shift-" + id + "-incidents.xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }
}










