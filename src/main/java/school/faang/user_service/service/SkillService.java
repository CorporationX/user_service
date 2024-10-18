package school.faang.user_service.service;

import school.faang.user_service.model.dto.SkillCandidateDto;
import school.faang.user_service.model.dto.SkillDto;
import school.faang.user_service.model.entity.User;

import java.util.List;

public interface SkillService {

    SkillDto create(SkillDto skillDto);

    List<SkillDto> getUserSkills(long userId);

    List<SkillCandidateDto> getOfferedSkills(long userId);

    SkillDto acquireSkillFromOffers(long skillId, long userId);

    void addSkillToUsers(List<User> users, Long goalId);
}
