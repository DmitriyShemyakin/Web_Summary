package com.example.web_summaryy.service;

import com.example.web_summaryy.dto.position.PositionDtoResponse;

public interface UpdateService {
    void syncPositions();
    void syncPositionsFromArray(PositionDtoResponse[] data);
}
