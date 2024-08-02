package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserBanedService {
    private final UserRepository userRepository;

    @Transactional
    public void banedUser(long userId) {
       userRepository.banUserById(userId);
    }
}