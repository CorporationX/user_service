package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillDto create(SkillDto skill){
        if (!skillRepository.existsByTitle(skill.getTitle())){
            Skill newSkill = Skill.builder()
                            .id(skill.getId())
                            .title(skill.getTitle())
                            .build();
            skillRepository.save(newSkill);

            return SkillMapper.INSTANCE.skillToDto(newSkill);
        }

        throw new DataValidationException("Skill " + skill.getTitle() + " already exists");
    }

    public List<SkillDto> getUserSkills(Long userId){
        return skillRepository.findAllByUserId(userId)
                .stream()
                .map(SkillMapper.INSTANCE::skillToDto)
                .toList();
    }
}
