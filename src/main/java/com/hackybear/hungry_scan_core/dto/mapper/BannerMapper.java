package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.BannerDTO;
import com.hackybear.hungry_scan_core.entity.Banner;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TranslatableMapper.class})
public interface BannerMapper {

    BannerDTO toDTO(Banner banner);

    Banner toBanner(BannerDTO bannerDTO);

}
