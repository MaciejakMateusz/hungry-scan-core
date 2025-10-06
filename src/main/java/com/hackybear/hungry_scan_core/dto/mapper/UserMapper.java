package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.RegistrationDTO;
import com.hackybear.hungry_scan_core.dto.UserProfileDTO;
import com.hackybear.hungry_scan_core.dto.UserProfileUpdateDTO;
import com.hackybear.hungry_scan_core.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    RegistrationDTO toDTO(User user);

    User toUser(RegistrationDTO registrationDTO);

    UserProfileDTO toUserProfileDTO(User user);

    User toUser(UserProfileDTO userProfileDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "repeatedPassword", ignore = true)
    void updateFromProfileUpdateDTO(UserProfileUpdateDTO userProfileUpdateDTO, @MappingTarget User user);

}
