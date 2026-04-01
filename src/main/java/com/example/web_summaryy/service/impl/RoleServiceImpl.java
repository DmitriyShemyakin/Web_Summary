package com.example.web_summaryy.service.impl;

import com.example.web_summaryy.config.mapper.DictionaryMapper;
import com.example.web_summaryy.dto.dictionary.RoleSimpleDto;
import com.example.web_summaryy.dto.direction.DirectionDtoResponse;
import com.example.web_summaryy.dto.role.RoleDtoRequest;
import com.example.web_summaryy.dto.role.RoleDtoResponse;
import com.example.web_summaryy.model.Direction;
import com.example.web_summaryy.model.Role;
import com.example.web_summaryy.model.User;
import com.example.web_summaryy.repository.DirectionRepository;
import com.example.web_summaryy.repository.RoleRepository;
import com.example.web_summaryy.service.RoleService;
import com.example.web_summaryy.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final UserService userService;

    private final DictionaryMapper dictionaryMapper;
    private final DirectionRepository directionRepository;

    private final RoleRepository roleRepository;

    public RoleServiceImpl(UserService userService, DictionaryMapper dictionaryMapper, DirectionRepository directionRepository, RoleRepository roleRepository) {
        this.userService = userService;
        this.dictionaryMapper = dictionaryMapper;
        this.directionRepository = directionRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleDtoResponse createRole(RoleDtoRequest request, User currentUser) {
        if (!userService.hasAdministratorRole(currentUser)) {
            throw new RuntimeException("Текущий пользователь не является администратором");
        }

        Role role = Role.builder()
                .title(request.getTitle())
                .build();

        if (request.getDirectionIds() != null && !request.getDirectionIds().isEmpty()) {
            role.setDirections(new HashSet<>(directionRepository.findAllById(request.getDirectionIds())));
        }

        Role saved = roleRepository.save(role);
        return dictionaryMapper.toRoleDto(saved);
    }

    @Override
    public RoleDtoResponse editRole(RoleDtoRequest request, User currentUser) {

        if (!userService.hasAdministratorRole(currentUser)) {
            throw new RuntimeException("Текущий пользователь не является администратором");
        }

        Role role = roleRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Роль не найдена"));

        Set<Direction> directions = new HashSet<>();

        if (request.getDirectionIds() != null && !request.getDirectionIds().isEmpty()) {
            directions = new HashSet<>(
                    directionRepository.findAllById(request.getDirectionIds())
            );
        }

        role.setDirections(directions);

        Role saved = roleRepository.save(role);
        return dictionaryMapper.toRoleDto(saved);
    }


}
