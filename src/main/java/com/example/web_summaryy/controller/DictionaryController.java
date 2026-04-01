package com.example.web_summaryy.controller;

import com.example.web_summaryy.config.mapper.DictionaryMapper;
import com.example.web_summaryy.dto.dictionary.PositionDetailDtoResponse;
import com.example.web_summaryy.dto.dictionary.PositionSimpleDto;
import com.example.web_summaryy.dto.dictionary.DirectionSimpleDto;
import com.example.web_summaryy.dto.dictionary.IncidentTypeSimpleDto;
import com.example.web_summaryy.dto.dictionary.IncidentCategorySimpleDto;
import com.example.web_summaryy.dto.dictionary.NetworkTypeSimpleDto;
import com.example.web_summaryy.dto.role.RoleDtoRequest;
import com.example.web_summaryy.dto.role.RoleDtoResponse;
import com.example.web_summaryy.model.*;
import com.example.web_summaryy.repository.*;
import com.example.web_summaryy.service.PositionLookupService;
import com.example.web_summaryy.service.RoleService;
import com.example.web_summaryy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dictionaries")
@RequiredArgsConstructor
public class DictionaryController {

    private final IncidentTypeRepository incidentTypeRepository;
    private final IncidentCategoryRepository incidentCategoryRepository;
    private final PositionRepository positionRepository;
    private final RoleRepository roleRepository;
    private final DirectionRepository directionRepository;
    private final NetworkTypeRepository networkTypeRepository;
    private final DictionaryMapper dictionaryMapper;
    private final UserService userService;
    private final RoleService roleService;
    private final PositionLookupService positionLookupService;


    @GetMapping("/incident-types")
    public ResponseEntity<List<IncidentTypeSimpleDto>> getIncidentTypes() {
        List<IncidentType> types = incidentTypeRepository.findByIsActiveTrue();
        List<IncidentTypeSimpleDto> dtos = types.stream()
                .map(dictionaryMapper::toIncidentTypeDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/incident-categories")
    public ResponseEntity<List<IncidentCategorySimpleDto>> getIncidentCategories() {
        List<IncidentCategory> categories = incidentCategoryRepository.findByIsActiveTrue();
        List<IncidentCategorySimpleDto> dtos = categories.stream()
                .map(dictionaryMapper::toIncidentCategoryDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/positions")
    public ResponseEntity<List<PositionSimpleDto>> getPositions() {
        List<Position> positions = positionRepository.findAll();
        List<PositionSimpleDto> dtos = positions.stream()
                .map(dictionaryMapper::toPositionDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /** q короче 2 символов — первые записи по алфавиту ({@link PositionLookupService}). */
    @GetMapping("/positions/search")
    public ResponseEntity<List<PositionSimpleDto>> searchPositions(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "limit", defaultValue = "40") int limit) {
        return ResponseEntity.ok(positionLookupService.searchForIncidentForm(q, limit));
    }

    @GetMapping("/positions/{id}/detail")
    public ResponseEntity<PositionDetailDtoResponse> getPositionDetail(@PathVariable Long id) {
        return positionLookupService.getPositionDetail(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleDtoResponse>> getRoles() {
        List<Role> roles = roleRepository.findAllWithDirections();
        List<RoleDtoResponse> dtos = roles.stream()
                .map(dictionaryMapper::toRoleDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/network-types")
    public ResponseEntity<List<NetworkTypeSimpleDto>> getNetworkTypes() {
        List<NetworkType> types = networkTypeRepository.findAll();
        List<NetworkTypeSimpleDto> dtos = types.stream()
                .map(dictionaryMapper::toNetworkTypeDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/directions")
    public ResponseEntity<List<DirectionSimpleDto>> getDirections() {
        List<Direction> directions = directionRepository.findAll();
        List<DirectionSimpleDto> dtos = directions.stream()
                .map(dictionaryMapper::toDirectionDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


    @PostMapping("/edit-role")
    public ResponseEntity<RoleDtoResponse> editRole(@RequestBody RoleDtoRequest request) {
        User currentUser = userService.getCurrentUser();
        RoleDtoResponse role = roleService.editRole(request, currentUser);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/incident-types/all")
    public ResponseEntity<List<IncidentTypeSimpleDto>> getAllIncidentTypes() {
        return ResponseEntity.ok(incidentTypeRepository.findAll().stream()
                .map(dictionaryMapper::toIncidentTypeDto).collect(Collectors.toList()));
    }

    @GetMapping("/incident-categories/all")
    public ResponseEntity<List<IncidentCategorySimpleDto>> getAllIncidentCategories() {
        return ResponseEntity.ok(incidentCategoryRepository.findAll().stream()
                .map(dictionaryMapper::toIncidentCategoryDto).collect(Collectors.toList()));
    }

    @PostMapping("/network-types")
    public ResponseEntity<?> createNetworkType(@RequestBody NetworkTypeSimpleDto dto) {
        NetworkType entity = NetworkType.builder()
                .title(dto.getTitle())
                .code(dto.getCode())
                .build();
        NetworkType saved = networkTypeRepository.save(entity);
        return ResponseEntity.ok(dictionaryMapper.toNetworkTypeDto(saved));
    }

    @PutMapping("/network-types/{id}")
    public ResponseEntity<?> updateNetworkType(@PathVariable Long id, @RequestBody NetworkTypeSimpleDto dto) {
        NetworkType entity = networkTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NetworkType not found: " + id));
        entity.setTitle(dto.getTitle());
        entity.setCode(dto.getCode());
        NetworkType saved = networkTypeRepository.save(entity);
        return ResponseEntity.ok(dictionaryMapper.toNetworkTypeDto(saved));
    }

    @DeleteMapping("/network-types/{id}")
    public ResponseEntity<?> deleteNetworkType(@PathVariable Long id) {
        networkTypeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/incident-categories")
    public ResponseEntity<?> createIncidentCategory(@RequestBody IncidentCategorySimpleDto dto) {
        IncidentCategory entity = IncidentCategory.builder()
                .categoryName(dto.getCategoryName())
                .description(dto.getDescription())
                .isActive(true)
                .build();
        IncidentCategory saved = incidentCategoryRepository.save(entity);
        return ResponseEntity.ok(dictionaryMapper.toIncidentCategoryDto(saved));
    }

    @PutMapping("/incident-categories/{id}")
    public ResponseEntity<?> updateIncidentCategory(@PathVariable Long id, @RequestBody IncidentCategorySimpleDto dto) {
        IncidentCategory entity = incidentCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("IncidentCategory not found: " + id));
        entity.setCategoryName(dto.getCategoryName());
        entity.setDescription(dto.getDescription());
        if (dto.getIsActive() != null) entity.setIsActive(dto.getIsActive());
        IncidentCategory saved = incidentCategoryRepository.save(entity);
        return ResponseEntity.ok(dictionaryMapper.toIncidentCategoryDto(saved));
    }

    @PostMapping("/incident-types")
    public ResponseEntity<?> createIncidentType(@RequestBody IncidentTypeSimpleDto dto) {
        IncidentType entity = IncidentType.builder()
                .typeCode(dto.getTypeCode())
                .isActive(true)
                .build();
        IncidentType saved = incidentTypeRepository.save(entity);
        return ResponseEntity.ok(dictionaryMapper.toIncidentTypeDto(saved));
    }

    @PutMapping("/incident-types/{id}")
    public ResponseEntity<?> updateIncidentType(@PathVariable Long id, @RequestBody IncidentTypeSimpleDto dto) {
        IncidentType entity = incidentTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("IncidentType not found: " + id));
        entity.setTypeCode(dto.getTypeCode());
        if (dto.getIsActive() != null) entity.setIsActive(dto.getIsActive());
        IncidentType saved = incidentTypeRepository.save(entity);
        return ResponseEntity.ok(dictionaryMapper.toIncidentTypeDto(saved));
    }

    // --- Role CRUD ---

    @PostMapping("/roles")
    public ResponseEntity<?> createRole(@RequestBody RoleDtoRequest request) {
        User currentUser = userService.getCurrentUser();
        RoleDtoResponse role = roleService.createRole(request, currentUser);
        return ResponseEntity.ok(role);
    }
}
