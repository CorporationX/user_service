package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validator.SkillValidator;
import school.faang.user_service.validator.UserValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillValidator skillValidator;
    private final SkillMapper skillMapper;
    private final UserValidator userValidator;
    private static final int MIN_SKILL_OFFERS = 3;

    @Transactional
    public SkillDto create(SkillDto skillDto) {
        skillValidator.validateTitleRepetition(skillDto.getTitle());

        Skill skill = skillMapper.toEntity(skillDto);
        Skill skillSaved = skillRepository.save(skill);
        return skillMapper.toDto(skillSaved);
    }

    @Transactional(readOnly = true)
    public void assignSkillToUser(long skillId, long userId) {
        skillRepository.assignSkillToUser(skillId, userId);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long skillId) {
        return skillRepository.existsById(skillId);
    }

    @Transactional(readOnly = true)
    public List<SkillDto> getUserSkills(Long userId) {
        userValidator.validateUserExists(userId);
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skills.stream().map(skillMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Skill getSkillById(Long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new DataValidationException("Навыка с id " + skillId + " не существует"));
    }

    @Transactional(readOnly = true)
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        userValidator.validateUserExists(userId);
        List<Skill> offeredSkillList = skillRepository.findSkillsOfferedToUser(userId);
        return offeredSkillList.stream().map(skill ->
                skillMapper.skillToSkillCandidateDto(skill, getSkillOffersNumber(userId, skill.getId()))).toList();
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        skillValidator.checkSkillIdAndUserIdInDB(skillId, userId);
        if (hasAlreadyAcquiredSkill(skillId, userId) || !hasEnoughSkillOffers(skillId, userId)) {
            throw new IllegalStateException("Пользователь уже приобрел этот навык или у него недостаточно предложений по навыкам.");
        }

        skillRepository.assignSkillToUser(skillId, userId);

        return skillRepository.findById(skillId)
                .map(skillMapper::toDto)
                .orElseThrow(() -> new IllegalStateException("Не удалось найти приобретенный навык с помощью id:" + skillId));
    }

    private boolean hasAlreadyAcquiredSkill(long skillId, long userId) {
        return skillRepository.findUserSkill(skillId, userId).isPresent();
    }

    private boolean hasEnoughSkillOffers(long skillId, long userId) {
        return getSkillOffersNumber(skillId, userId) >= MIN_SKILL_OFFERS;
    }

    private long getSkillOffersNumber(long userId, long skillId) {
        return skillRepository.countOffersByUserIdAndSkillId(userId, skillId);
    }
}