package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.dto.RestaurantSimpleDTO;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {SettingsMapper.class, MenuMapper.class})
public interface RestaurantMapper {

    RestaurantDTO toDTO(Restaurant restaurant);

    Restaurant toRestaurant(RestaurantDTO restaurantDTO);

    @Mapping(target = "menus", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDTO(RestaurantDTO restaurantDTO, @MappingTarget Restaurant restaurant);

    RestaurantSimpleDTO toSimpleDTO(Restaurant restaurant);

}
