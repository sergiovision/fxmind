package com.fxmind.mapper;

import com.fxmind.domain.NewsEvent;
import com.fxmind.domain.Person;
import com.fxmind.dto.NewsEventDto;
import com.fxmind.dto.PersonDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/** Compile-time generated entity → read-model mapping (replaces the hand-written DTOConverter). */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DomainMapper {

    @Mapping(target = "activated", source = "registration.activated")
    @Mapping(target = "registeredAt", source = "registration.registeredAt")
    @Mapping(target = "registrationIp", source = "registration.registrationIp")
    PersonDto toDto(Person person);

    @Mapping(target = "currency", source = "currency.name")
    NewsEventDto toDto(NewsEvent event);
}
