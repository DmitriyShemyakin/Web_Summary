package com.example.web_summaryy.dto.role;

import com.example.web_summaryy.dto.direction.DirectionDtoResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDtoRequest {
    private Long id;
    private String title;
    private List<Long> directionIds;
}

