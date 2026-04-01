package com.example.web_summaryy.dto.shift;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndShiftResponse {

    private ShiftDtoResponse shift;

    /**
     * URL для скачивания Excel отчета (если был сгенерирован)
     */
    private String downloadUrl;

    /**
     * Количество закрытых аварий за смену
     */
    private Integer closedIncidentsCount;

    /**
     * Количество открытых аварий (переданных следующей смене)
     */
    private Integer openIncidentsCount;
}



