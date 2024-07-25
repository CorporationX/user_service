package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void extracted(SkillDto skillDto, Skill skill) {
        List<User> users = userRepository.findAllById(skillDto.getUserIds())
                .stream()
                .toList();
        skill.setUsers(users);
    }
}
