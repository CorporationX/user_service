package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mappers.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    public SkillDto create(SkillDto skillDto) {
        validateTitleRepetition(skillDto);

        Skill skill = skillMapper.DtoToSkill(skillDto);
        Skill skillSaved = skillRepository.save(skill);
        return skillMapper.skillToDto(skillSaved);
    }

    private void validateTitleRepetition(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Навык с таким именем уже существует в базе данных");
        }
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skills.stream().map(skillMapper::skillToDto).toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> offeredSkillList = skillRepository.findSkillsOfferedToUser(userId);
        return offeredSkillList.stream().map(skill ->
                skillMapper.skillToSkillCandidateDto(skill, getSkillOffersNumber(userId, skill.getId()))).toList();
    }

    private long getSkillOffersNumber(long userId, long skillId) {
        return skillRepository.countOffersByUserIdAndSkillId(userId, skillId);
    }

    public Optional<SkillDto> acquireSkillFromOffers(long skillId, long userId) {
        if (hasAlreadyAcquiredSkill(skillId, userId) || !hasEnoughSkillOffers(skillId, userId)) {
            return Optional.empty();
        }

        skillRepository.assignSkillToUser(skillId, userId);

        return skillRepository.findById(skillId)
                .map(skill -> {
                    SkillDto acquiredSkillDto = new SkillDto();
                    acquiredSkillDto.setId(skill.getId());
                    acquiredSkillDto.setTitle(skill.getTitle());
                    return acquiredSkillDto;
                });
    }

    private boolean hasAlreadyAcquiredSkill(long skillId, long userId) {
        return skillRepository.findUserSkill(skillId, userId).isPresent();
    }

     private boolean hasEnoughSkillOffers(long skillId, long userId) {
        final int MIN_SKILL_OFFERS = 3;
        return getSkillOffersNumber(skillId, userId) >= MIN_SKILL_OFFERS;
    }
}