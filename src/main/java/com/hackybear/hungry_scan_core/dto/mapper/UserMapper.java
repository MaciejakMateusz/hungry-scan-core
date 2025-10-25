package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.*;
import com.hackybear.hungry_scan_core.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = RestaurantMapper.class)
public interface UserMapper {

    RegistrationDTO toRegistrationDTO(User user);

    User toUser(RegistrationDTO registrationDTO);

    UserProfileDTO toUserProfileDTO(User user);

    User toUser(UserProfileDTO userProfileDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "repeatedPassword", ignore = true)
    void updateFromProfileUpdateDTO(UserProfileUpdateDTO userProfileUpdateDTO, @MappingTarget User user);

    UserDTO toDTO(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    void updateFromDTO(UserDTO userDTO, @MappingTarget User user);

    User toUser(UserDTO userDTO);

    UserActivityDTO toUserActivityDTO(User user);

}
