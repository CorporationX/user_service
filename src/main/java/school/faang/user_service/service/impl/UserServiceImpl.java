package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public boolean existsUserById(Long id) {
        return userRepository.existsById(id);
    }
}
