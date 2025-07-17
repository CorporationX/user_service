package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor

public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;

    @Override
    @Named("mapSkillIdsToSkillList")
    public List<Skill> getSkillEntityListByIds(List<Long> skillIds) {
        return skillRepository.findAllById(skillIds);
    }
}
