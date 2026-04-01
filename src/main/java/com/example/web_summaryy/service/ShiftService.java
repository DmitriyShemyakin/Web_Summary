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

    /**
     * Начать новую смену
     *
     * @param request запрос на начало смены
     * @param currentUser текущий пользователь
     * @return информация о созданной смене
     * @throws IllegalStateException если у пользователя уже есть активная смена
     */
    ShiftDtoResponse startShift(StartShiftRequest request, User currentUser);

    /**
     * Завершить текущую смену
     *
     * @param request запрос на завершение смены
     * @param currentUser текущий пользователь
     * @return информация о завершенной смене + статистика
     * @throws IllegalStateException если у пользователя нет активной смены
     */
    EndShiftResponse endShift(EndShiftRequest request, User currentUser);

    /**
     * Получить текущую активную смену пользователя
     *
     * @param currentUser текущий пользователь
     * @return информация о смене или null, если активной смены нет
     */
    ShiftDtoResponse getCurrentShift(User currentUser);

    ShiftDtoResponse getShiftById(Long shiftId, User currentUser);

    /**
     * Получить историю смен пользователя (с пагинацией)
     *
     * @param currentUser текущий пользователь
     * @param pageable параметры пагинации
     * @return страница с историей смен
     */
    Page<ShiftDtoResponse> getShiftHistory(User currentUser, Pageable pageable);

    /**
     * Получить все смены (для админа/старшего дежурного)
     *
     * @param pageable параметры пагинации
     * @return страница со всеми сменами
     */
    Page<ShiftDtoResponse> getAllShifts(Pageable pageable);

    /**
     * Получить аварии за смену
     *
     * @param shiftId ID смены
     * @param currentUser текущий пользователь
     * @return список аварий за смену
     */
    List<IncidentDtoResponse> getShiftIncidents(Long shiftId, User currentUser);

    /**
     * XLSX со всеми авариями смены, видимыми пользователю (та же выборка, что у {@link #getShiftIncidents}).
     */
    byte[] exportShiftIncidentsAsXlsx(Long shiftId, User currentUser);
}










