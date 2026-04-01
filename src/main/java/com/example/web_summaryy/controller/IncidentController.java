package com.example.web_summaryy.controller;

import com.example.web_summaryy.dto.incident.*;
import com.example.web_summaryy.model.User;
import com.example.web_summaryy.service.IncidentService;
import com.example.web_summaryy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<IncidentDtoResponse> createIncident(@RequestBody CreateIncidentRequest request) {
        User currentUser = userService.getCurrentUser();
        IncidentDtoResponse incident = incidentService.createIncident(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(incident);
    }

    @GetMapping
    public ResponseEntity<List<IncidentDtoResponse>> getOpenIncidents() {
        User currentUser = userService.getCurrentUser();
        List<IncidentDtoResponse> incidents = incidentService.getOpenIncidents(currentUser);
        return ResponseEntity.ok(incidents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentDtoResponse> getIncident(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        IncidentDtoResponse incident = incidentService.getIncidentById(id, currentUser);
        return ResponseEntity.ok(incident);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncidentDtoResponse> updateIncident(
            @PathVariable Long id,
            @RequestBody UpdateIncidentRequest request) {
        User currentUser = userService.getCurrentUser();
        IncidentDtoResponse incident = incidentService.updateIncident(id, request, currentUser);
        return ResponseEntity.ok(incident);
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<IncidentDtoResponse> closeIncident(
            @PathVariable Long id,
            @RequestBody CloseIncidentRequest request) {
        User currentUser = userService.getCurrentUser();
        IncidentDtoResponse incident = incidentService.closeIncident(id, request, currentUser);
        return ResponseEntity.ok(incident);
    }

    @GetMapping("/history")
    public ResponseEntity<List<IncidentDtoResponse>> getClosedIncidents(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        User currentUser = userService.getCurrentUser();
        List<IncidentDtoResponse> incidents = incidentService.getClosedIncidents(from, to, currentUser);
        return ResponseEntity.ok(incidents);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        incidentService.deleteIncident(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
