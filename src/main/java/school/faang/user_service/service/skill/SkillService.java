package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillMapper skillMapper;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public SkillDto create (SkillDto skill) {
        boolean skillExistsInDB = skillRepository.existsByTitle(skill.getTitle());

        if (skillExistsInDB) {
            throw new DataValidationException("Skill with name " + skill.getTitle() + " already exists in database.");
        }

        Skill skillEntity = skillMapper.toEntity(skill);
        List<User> users = userRepository.findAllById(skill.getUserIds());
        skillEntity.setUsers(users);

        skillEntity = skillRepository.save(skillEntity);

        return skillMapper.toDto(skillEntity);
    }

    public List<SkillDto> getUserSkills (long userId) {

        List<Skill> skillEntityList = skillRepository.findAllByUserId(userId);

        if (skillEntityList == null) {
            throw new DataValidationException("No skills found by user id " + userId);
        }

        for (Skill skill : skillEntityList) {
            SkillDto skillDto = skillMapper.toDto(skill);
            List<User> users = userRepository.findAllById(skillDto.getUserIds());
            skill.setUsers(users);
        }

        return skillMapper.listToDto(skillEntityList);
    }

    public List<SkillCandidateDto> getOfferedSkills (long userId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);
        List<SkillDto> offeredSkillDtos = skillMapper.listToDto(offeredSkills);

        Map<SkillDto, Long> offeredSkillDtosMap = offeredSkillDtos
                .stream()
                .collect(Collectors.groupingBy(skillDto -> skillDto, Collectors.counting()));

        List<SkillCandidateDto> offeredSkillCandidateDtos = offeredSkillDtosMap
                .entrySet()
                .stream()
                .map(item -> new SkillCandidateDto(item.getKey(), item.getValue()))
                .toList();

        return offeredSkillCandidateDtos;
    }
}
