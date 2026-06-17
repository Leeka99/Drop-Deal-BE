package com.dropdeal.api.profile;

import com.dropdeal.api.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ApiResponse<ShippingAddressResponse> getDefaultShippingAddress() {
        return ApiResponse.ok(profileService.getDefaultShippingAddress());
    }

    @PutMapping
    public ApiResponse<ShippingAddressResponse> saveDefaultShippingAddress(@Valid @RequestBody ShippingAddressRequest request) {
        return ApiResponse.ok(profileService.saveDefaultShippingAddress(request));
    }
}
