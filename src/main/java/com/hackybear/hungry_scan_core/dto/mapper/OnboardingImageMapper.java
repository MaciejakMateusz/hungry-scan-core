package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.OnboardingImageDTO;
import com.hackybear.hungry_scan_core.entity.OnboardingImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OnboardingImageMapper {

    OnboardingImageDTO toDTO(OnboardingImage onboardingImage);

    OnboardingImage toOnboardingImage(OnboardingImageDTO onboardingImageDTO);

}
