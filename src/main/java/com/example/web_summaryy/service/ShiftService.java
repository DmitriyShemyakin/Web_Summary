package com.example.web_summaryy.service;

import com.example.web_summaryy.dto.shift.EndShiftRequest;
import com.example.web_summaryy.dto.shift.EndShiftResponse;
import com.example.web_summaryy.dto.incident.IncidentDtoResponse;
import com.example.web_summaryy.dto.shift.ShiftDtoResponse;
import com.example.web_summaryy.dto.shift.StartShiftRequest;
import com.example.web_summaryy.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShiftService {


    ShiftDtoResponse startShift(StartShiftRequest request, User currentUser);

    EndShiftResponse endShift(EndShiftRequest request, User currentUser);

    ShiftDtoResponse getCurrentShift(User currentUser);

    ShiftDtoResponse getShiftById(Long shiftId, User currentUser);

    Page<ShiftDtoResponse> getShiftHistory(User currentUser, Pageable pageable);

    Page<ShiftDtoResponse> getAllShifts(Pageable pageable);

    List<IncidentDtoResponse> getShiftIncidents(Long shiftId, User currentUser);

    byte[] exportShiftIncidentsAsXlsx(Long shiftId, User currentUser);
}










