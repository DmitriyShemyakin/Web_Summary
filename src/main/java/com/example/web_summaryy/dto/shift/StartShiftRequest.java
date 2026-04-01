package com.example.web_summaryy.dto.shift;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartShiftRequest {

    /**
     * ID направления (опционально, для дежурных БС)
     * Если null - дежурный работает по всем направлениям
     */
    private Long directionId;
}



