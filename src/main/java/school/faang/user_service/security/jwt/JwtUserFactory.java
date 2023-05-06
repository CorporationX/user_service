package school.faang.user_service.security.jwt;

import lombok.NoArgsConstructor;
import school.faang.user_service.entity.User;

@NoArgsConstructor
public final class JwtUserFactory {

    public static JwtUser create(User user) {
        return new JwtUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.isActive()
        );
    }
}