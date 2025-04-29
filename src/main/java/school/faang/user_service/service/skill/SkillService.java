package school.faang.user_service.service.skill;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillCreateDto;
import school.faang.user_service.dto.skill.SkillViewDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Data
@RequiredArgsConstructor
@Service
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    public SkillViewDto create(SkillCreateDto skillCreateDto) {
        validateSkill(skillCreateDto);
        if (skillRepository.existsByTitle(skillCreateDto.getTitle())) {
        throw new DataValidationException("skill not found");

        }
        Skill skill = skillMapper.toEntity(skillCreateDto);
         skill = skillRepository.save(skill);


        return skillMapper.ToDto(skill);
    }
public List<SkillViewDto> getUserSkills(long userId) {
        List<Skill>skills = skillRepository.findAllByUserId(userId);
        return skills.stream().map(skillMapper::ToDto).toList();
}
public List <SkillCandidateDto> getOfferedSkills (long userId) {
        List <Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        return skillMapper.toSkillCandidateDtoList(skills);
}
private void validateSkill (SkillCreateDto skill){
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {

        }

}
}
