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

    private static SkillMapper sm;
    private final SkillMapper skillMapper;
    private final SkillRepository skillRepository;

    public SkillDto create (SkillDto skill) {
        boolean skillExistsInDB = skillRepository.existsByTitle(skill.getTitle());

        if (skillExistsInDB) {
            throw new DataValidationException("Skill with name " + skill.getTitle() + " already exists in database.");
        }

        Skill skillEntity = skillMapper.toEntity(skill);
        skillRepository.save(skillEntity);

        return skillMapper.toDto(skillEntity);
    }

    public static void main (String[] args) {
        SkillDto dto = new SkillDto(1L, "asdasd");

        Skill skk = sm.toEntity(dto);
        SkillDto newDto = sm.toDto(skk);

        System.out.println(skk);
        System.out.println(newDto);
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skillEntityList = skillRepository.findAllByUserId(userId);
        List<SkillDto> skillDtoList = skillEntityList.stream().map(skillMapper::toDto).toList();

        return skillDtoList;
    }
}
