package school.faang.user_service.service.skill.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.entity.Skill;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Skill> getSkillListBySkillIds(List<Long> ids) {
        return skillRepository.findAllById(ids);
    }
}
