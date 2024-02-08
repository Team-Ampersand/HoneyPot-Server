package com.grapefruitade.honeypost.domain.auth.service;

import com.grapefruitade.honeypost.domain.auth.dto.LoginRequestDto;
import com.grapefruitade.honeypost.domain.auth.dto.RegisterRequestDto;
import com.grapefruitade.honeypost.domain.auth.dto.TokenDto;
import com.grapefruitade.honeypost.domain.auth.dto.TokenRequestDto;
import com.grapefruitade.honeypost.domain.auth.entity.RefreshToken;
import com.grapefruitade.honeypost.domain.auth.exception.*;
import com.grapefruitade.honeypost.domain.auth.repository.RefreshTokenRepository;
import com.grapefruitade.honeypost.domain.user.dto.UserResponseDto;
import com.grapefruitade.honeypost.domain.user.entity.User;
import com.grapefruitade.honeypost.domain.user.repository.UserRepository;
import com.grapefruitade.honeypost.global.security.jwt.TokenProvider;
import com.grapefruitade.honeypost.global.security.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.MissingResourceException;

@Transactional
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserUtil userUtil;

    public UserResponseDto register(RegisterRequestDto registerRequest){
        if(userRepository.existsByUsername(registerRequest.getUsername())){
            throw new ExistUsernameException();
        }

        User user = registerRequest.user(passwordEncoder);
        return UserResponseDto.userResponseDto(userRepository.save(user));
    }

    public TokenDto login(LoginRequestDto loginRequest){

      User user = userRepository.findByUsername(loginRequest.getUsername())
              .orElseThrow(() -> new UserNotFoundException());

      if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
          throw new MismatchNotPassword();
      }

      TokenDto tokenDto = tokenProvider.generateTokenDto(user.getId(), user.getRole());

      saveRefreshToken(tokenDto, user);

        return tokenDto;
    }

    private void saveRefreshToken(TokenDto tokenDto, User user) {
        RefreshToken token = RefreshToken.builder()
                .token(tokenDto.getRefreshToken())
                .userid(user.getId())
                .expiresAt(tokenDto.getRefreshTokenExpiresIn())
                .build();

        refreshTokenRepository.save(token);
    }

    public TokenDto refresh (String RefreshToken){
        String parseRefreshToken = tokenProvider.parseRefreshToken(RefreshToken);


        RefreshToken validRefreshToken = refreshTokenRepository.findById(parseRefreshToken)
                .orElseThrow(() -> new TokenExpirationException());

        User user = userRepository.findById(validRefreshToken.getUserid())
                .orElseThrow(() -> new UserNotFoundException());


        TokenDto tokenDto = tokenProvider.generateTokenDto(user.getId(), user.getRole());

        saveRefreshToken(tokenDto, user);
        refreshTokenRepository.deleteById(validRefreshToken.getToken());

        return tokenDto;
    }


    public void logout() {

        User user = userUtil.currentUser();
        RefreshToken refreshToken = refreshTokenRepository.findByUserid(user.getId());
        if (refreshToken == null){
            throw new UserNotFoundException();
        }
        refreshTokenRepository.delete(refreshToken);

    }


}
