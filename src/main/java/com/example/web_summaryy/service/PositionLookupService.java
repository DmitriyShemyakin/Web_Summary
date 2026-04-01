package com.example.web_summaryy.service;

import com.example.web_summaryy.config.mapper.DictionaryMapper;
import com.example.web_summaryy.dto.dictionary.PositionDetailDtoResponse;
import com.example.web_summaryy.dto.dictionary.PositionSimpleDto;
import com.example.web_summaryy.model.Position;
import com.example.web_summaryy.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PositionLookupService {

    private final PositionRepository positionRepository;
    private final DictionaryMapper dictionaryMapper;

    /**
     * Поиск позиций по имени (position_nameforbs). При коротком запросе (&lt; 2 символов) — первые N по алфавиту.
     */
    @Transactional(readOnly = true)
    public List<PositionSimpleDto> searchForIncidentForm(String query, int limit) {
        int cap = Math.min(Math.max(limit, 1), 100);
        Pageable pageable = PageRequest.of(0, cap, Sort.by(Sort.Direction.ASC, "positionNameforbs"));
        Page<Position> page;
        if (query == null || query.trim().length() < 2) {
            page = positionRepository.findAll(pageable);
        } else {
            page = positionRepository.findByPositionNameforbsContainingIgnoreCase(query.trim(), pageable);
        }
        return page.getContent().stream().map(dictionaryMapper::toPositionDto).toList();
    }

    @Transactional(readOnly = true)
    public Optional<PositionDetailDtoResponse> getPositionDetail(Long id) {
        return positionRepository.findById(id).map(dictionaryMapper::toPositionDetailDto);
    }
}
