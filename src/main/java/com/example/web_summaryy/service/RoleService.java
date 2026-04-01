package com.example.web_summaryy.service;

import com.example.web_summaryy.dto.dictionary.RoleSimpleDto;
import com.example.web_summaryy.dto.role.RoleDtoRequest;
import com.example.web_summaryy.dto.role.RoleDtoResponse;
import com.example.web_summaryy.model.User;

public interface RoleService {

    RoleDtoResponse createRole(RoleDtoRequest request, User currentUser);

    RoleDtoResponse editRole(RoleDtoRequest request, User currentUser);
}
