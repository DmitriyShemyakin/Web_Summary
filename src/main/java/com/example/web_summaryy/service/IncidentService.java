package com.example.web_summaryy.service;

import com.example.web_summaryy.dto.incident.*;
import com.example.web_summaryy.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface IncidentService {

    IncidentDtoResponse createIncident(CreateIncidentRequest request, User currentUser);

    IncidentDtoResponse updateIncident(Long id, UpdateIncidentRequest request, User currentUser);

    IncidentDtoResponse closeIncident(Long id, CloseIncidentRequest request, User currentUser);

    IncidentDtoResponse getIncidentById(Long id, User currentUser);

    List<IncidentDtoResponse> getOpenIncidents(User currentUser);

    List<IncidentDtoResponse> getClosedIncidents(LocalDateTime from, LocalDateTime to, User currentUser);

    void deleteIncident(Long id, User currentUser);
}
