package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User getUserById(long id){
        return userRepository.findById(id).orElseThrow(() -> {
            log.error("ошибка getUserById");
            return new EntityNotFoundException("ошибка getUserById");
        });
    }

    public List<Long> getUserSkillsId(long id) {
        return userRepository.findById(id).orElseThrow(()-> {
            log.error("нет пользователя по id");
            return new EntityNotFoundException("нет пользователя по id");
        }).getSkills().stream().map(Skill::getId).toList();
    }

    public UserDto getUser(long id){
        return userMapper.toDto(userRepository.findById(id).orElseThrow());
    }
}