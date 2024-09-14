package school.faang.user_service.mapper.goal;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GoalMapperHelper {

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private UserRepository userRepository;

    @Named("mapSkillsToIds")
    public List<Long> mapSkillsToIds(List<Skill> skills) {
        if (skills == null) {
            return null;
        }
        return skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }

    @Named("mapIdsToSkills")
    public List<Skill> mapIdsToSkills(List<Long> skillIds) {
        if (skillIds == null || skillIds.contains(null)) {
            return null;
        }
        return skillRepository.findAllById(skillIds);
    }

    @Named("mapUsersToIds")
    public List<Long> mapUsersToIds(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    @Named("mapIdsToUsers")
    public List<User> mapIdsToUsers(List<Long> userIds) {
        if (userIds == null || userIds.contains(null)) {
            return null;
        }
        return userRepository.findAllById(userIds);
    }

    @Named("mapMentorIdToUser")
    public User mapMentorIdToUser(Long mentorId) {
        if (mentorId == null) {
            return null;
        }
        return userRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Ментор с ID " + mentorId + " не найден"));
    }
}