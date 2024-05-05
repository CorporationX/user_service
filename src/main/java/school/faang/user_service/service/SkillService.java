package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mappers.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;


@Component
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SkillMapper skillMapper;
    private static final int MIN_SKILL_OFFERS = 3;

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
        checkUserInDB(userId);
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skills.stream().map(skillMapper::skillToDto).toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        checkUserInDB(userId);
        List<Skill> offeredSkillList = skillRepository.findSkillsOfferedToUser(userId);
        return offeredSkillList.stream().map(skill ->
                skillMapper.skillToSkillCandidateDto(skill, getSkillOffersNumber(userId, skill.getId()))).toList();
    }

    private void checkUserInDB(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException("Не найден пользователь с данным id: " + userId);
        }
    }

    private long getSkillOffersNumber(long userId, long skillId) {
        return skillRepository.countOffersByUserIdAndSkillId(userId, skillId);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        checkNotNullSkillIdOrUserId(skillId, userId);
        if (hasAlreadyAcquiredSkill(skillId, userId) || !hasEnoughSkillOffers(skillId, userId)) {
            throw new IllegalStateException("Пользователь уже приобрел этот навык или у него недостаточно предложений по навыкам.");
        }

        skillRepository.assignSkillToUser(skillId, userId);

        return skillRepository.findById(skillId)
                .map(skillMapper::skillToDto)
                .orElseThrow(() -> new IllegalStateException("Не удалось найти приобретенный навык с помощью id:" + skillId));
    }

    private boolean hasAlreadyAcquiredSkill(long skillId, long userId) {
        return skillRepository.findUserSkill(skillId, userId).isPresent();
    }

     private boolean hasEnoughSkillOffers(long skillId, long userId) {
        return getSkillOffersNumber(skillId, userId) >= MIN_SKILL_OFFERS;
    }

    private void checkNotNullSkillIdOrUserId(long skillId, long userId) {
        if (!userRepository.existsById(userId) || !skillRepository.existsById(skillId)) {
            throw new DataValidationException("Переданы пустые параметры skillId или userId");
        }
    }
}