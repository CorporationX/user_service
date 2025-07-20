package school.faang.user_service.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.auth.AuthRequest;
import school.faang.user_service.dto.auth.JwtTokens;
import school.faang.user_service.dto.auth.Token;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.RefreshToken;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.RefreshTokenRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public JwtTokens register(CreateUserDto dto) {
        User user = userMapper.toUser(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Country country = countryRepository.getByIdOrThrow(dto.countryId());
        user.setCountry(country);
        userRepository.save(user);
        Token accessToken = jwtService.generateAccessToken(user);
        Token refreshToken = jwtService.generateRefreshToken(user);
        createRefreshToken(refreshToken, user);
        logAuthAction("Регистрация пользователя", user);
        return JwtTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional
    public JwtTokens authenticate(AuthRequest dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );
        User user = userRepository.findByUsername(dto.username())
                .orElseThrow();
        Token accessToken = jwtService.generateAccessToken(user);
        Token refreshToken = jwtService.generateRefreshToken(user);
        createRefreshToken(refreshToken, user);
        logAuthAction("Аутентификация пользователя", user);
        return JwtTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void createRefreshToken(Token token, User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .expiredAt(token.expireAt())
                .token(token.value())
                .isRevoked(false)
                .user(user)
                .build();
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public Token refreshToken(String refreshToken) {
        RefreshToken refreshTokenEntity = refreshTokenRepository.getValidToken(refreshToken)
                .orElseThrow(() -> new ForbiddenException("Cant get new access value"));
        User user = refreshTokenEntity.getUser();
        LocalDateTime expiredAt = LocalDateTime.now()
                .plus(jwtService.getRefreshSecretExpiration(), ChronoUnit.MILLIS);

        refreshTokenEntity.setExpiredAt(expiredAt);
        refreshTokenRepository.save(refreshTokenEntity);

        logAuthAction("Генерация access токена через refresh токена", user);
        return jwtService.generateAccessToken(user);
    }

    private void logAuthAction(String msg, User user) {
        log.info("{}. Id: {}, username: {}", msg, user.getId(), user.getUsername());
    }
}
