package com.ordertracker.auth.mapper;

import com.ordertracker.auth.dao.entity.User;
import com.ordertracker.auth.dto.request.RegisterRequest;
import com.ordertracker.util.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = Role.class)
public interface AuthMapper {

    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(target = "role", expression = "java(Role.USER)")
    User toUser(RegisterRequest request, String encodedPassword);

}