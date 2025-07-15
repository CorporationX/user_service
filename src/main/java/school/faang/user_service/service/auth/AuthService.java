package school.faang.user_service.service.auth;

import school.faang.user_service.dto.auth.Token;
import school.faang.user_service.dto.auth.AuthRequest;
import school.faang.user_service.dto.auth.JwtTokens;
import school.faang.user_service.dto.user.CreateUserDto;

public interface AuthService {
    public JwtTokens register(CreateUserDto dto);

    public JwtTokens authenticate(AuthRequest dto);

    Token refreshToken(String refreshToken);
}
