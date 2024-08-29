package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(()-> {
            String errorMessage = String.format("User (ID : %d) doesn't exist in the system");
            log.error(errorMessage);
            return new EntityNotFoundException(errorMessage);
        });
    }
    @Transactional
    public List<Long> getUserSkillsId(long id) {
        return userRepository.findById(id).orElseThrow(()-> {
            String errorMessage = String.format("User (ID : %d) doesn't exist in the system");
            log.error(errorMessage);
            return new EntityNotFoundException(errorMessage);
        }).getSkills().stream().map(Skill::getId).toList();
    }
}
