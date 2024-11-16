package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {

    RestaurantDTO toDTO(Restaurant restaurant);

    Restaurant toStatistics(RestaurantDTO restaurantDTO);

}
