package com.grapefruitade.honeypost.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRequestDto {

    private String accessToken;
    private String refreshToken;

}
