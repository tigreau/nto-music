package com.musicshop.mapper;

import com.musicshop.dto.auth.AuthResponse;
import com.musicshop.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class)
public interface AuthMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    @Mapping(target = "token", source = "token")
    AuthResponse toAuthResponse(User user, String token);
}
