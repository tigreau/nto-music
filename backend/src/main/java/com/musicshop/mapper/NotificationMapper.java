package com.musicshop.mapper;

import com.musicshop.dto.user.NotificationDTO;
import com.musicshop.model.user.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class)
public interface NotificationMapper {

    @Mapping(target = "type", expression = "java(notification.getType().name())")
    @Mapping(target = "timestamp", expression = "java(notification.getTimestamp().toString())")
    NotificationDTO toNotificationDTO(Notification notification);
}
