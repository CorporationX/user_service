package school.faang.user_service.service.auth;

import school.faang.user_service.dto.auth.AuthRequest;
import school.faang.user_service.dto.auth.AuthResponse;
import school.faang.user_service.dto.user.CreateUserDto;

public interface AuthService {
    public AuthResponse register(CreateUserDto dto);

    public AuthResponse authenticate(AuthRequest dto);
}
