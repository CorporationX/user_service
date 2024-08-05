package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.exception.NotFoundEntityException;
import school.faang.user_service.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public void existUserById(long id) {
        if (!userRepository.existsById(id)) {
            log.error("User with id :{} doesn't exist!", id);
            throw new NotFoundEntityException("User with id :" + id + " doesn't exist!");
        }
    }
}
