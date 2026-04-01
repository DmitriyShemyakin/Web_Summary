package com.example.web_summaryy.service;

import com.example.web_summaryy.dto.position.PositionDtoResponse;

public interface ApiService {
    PositionDtoResponse[] fetchPositions();
}
