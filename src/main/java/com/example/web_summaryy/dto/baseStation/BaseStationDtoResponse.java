package com.example.web_summaryy.dto.baseStation;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseStationDtoResponse {
    private String baseStationUUID;
    private String baseStationAdName;
    private String baseStationDispTitle;
    private String baseStationAddressDoc;


}
