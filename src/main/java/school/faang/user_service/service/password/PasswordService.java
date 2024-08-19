package school.faang.user_service.service.password;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PasswordService {
    public String generatePassword() {
        return UUID.randomUUID().toString();
    }
}
