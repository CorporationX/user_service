package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import school.faang.user_service.dto.auth.AuthRequest;
import school.faang.user_service.dto.auth.Token;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.RefreshToken;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.RefreshTokenRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.auth.AuthServiceImpl;
import school.faang.user_service.service.auth.JwtService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    private static final String EXISTING_TOKEN = "valid-refresh-token";
    private static final String NEW_ACCESS_TOKEN = "new-access-token";
    private static final long EXPIRATION_MILLIS = 600_000L;

    public static Country country;

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private CountryRepository countryRepository;
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Captor
    private ArgumentCaptor<User> userCaptor;
    @Captor
    private ArgumentCaptor<UsernamePasswordAuthenticationToken> authTokenCaptor;


    @BeforeEach
    void setUp() {
        country = new Country();
        country.setId(1L);
    }

    @Test
    @DisplayName("Регистрация пользователя")
    public void testUserRegister() {
        String encodedPassword = "encoded";
        when(countryRepository.getByIdOrThrow(country.getId())).thenReturn(country);
        when(passwordEncoder.encode(any())).thenReturn(encodedPassword);
        when(jwtService.generateAccessToken(any())).thenReturn(new Token(null, null, 1L));
        when(jwtService.generateRefreshToken(any())).thenReturn(new Token(null, null, 1L));
        CreateUserDto createUserDto = new CreateUserDto("john", "pass", "password", 1L);

        authService.register(createUserDto);

        verify(userMapper).toUser(createUserDto);
        verify(passwordEncoder).encode(createUserDto.password());
        verify(countryRepository).getByIdOrThrow(createUserDto.countryId());
        verify(userRepository).save(userCaptor.capture());
        User user = userCaptor.getValue();
        assertEquals(encodedPassword, user.getPassword());
        assertEquals(country, user.getCountry());
        verify(jwtService).generateAccessToken(any());
        verify(jwtService).generateRefreshToken(any());
        verify(refreshTokenRepository).save(any());
    }

    @Test
    @DisplayName("Аутентификация пользователя")
    public void testUserAuthentication() {
        AuthRequest request = new AuthRequest("test", "password");
        when(userRepository.findByUsername(request.username())).thenReturn(Optional.of(mock(User.class)));
        when(jwtService.generateAccessToken(any())).thenReturn(new Token(null, null, 1L));
        when(jwtService.generateRefreshToken(any())).thenReturn(new Token(null, null, 1L));

        authService.authenticate(request);

        verify(authenticationManager).authenticate(authTokenCaptor.capture());
        UsernamePasswordAuthenticationToken passed = authTokenCaptor.getValue();
        assertEquals(request.username(), passed.getPrincipal());
        assertEquals(request.password(), passed.getCredentials());
        verify(userRepository).findByUsername(request.username());
        verify(jwtService).generateAccessToken(any());
        verify(jwtService).generateRefreshToken(any());
        verify(refreshTokenRepository).save(any());
    }

    @Test
    @DisplayName("Получение access-токена по refresh-токену пользователя")
    public void testUserGetAccessToken() {
        User user = new User();
        user.setId(1L);
        RefreshToken token = new RefreshToken(
                2L,
                user,
                NEW_ACCESS_TOKEN,
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(refreshTokenRepository.getValidToken(EXISTING_TOKEN)).thenReturn(Optional.of(token));

        authService.refreshToken(EXISTING_TOKEN);

        verify(refreshTokenRepository).getValidToken(EXISTING_TOKEN);
        verify(refreshTokenRepository).save(token);
        verify(jwtService).generateAccessToken(token.getUser());
        assertEquals(user, token.getUser());
    }
}
