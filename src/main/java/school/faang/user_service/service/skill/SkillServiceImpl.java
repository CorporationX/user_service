package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;

import java.util.List;

@Slf4j
@Setter
@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;

    @Value("${skill.minimal.offers}")
    private int minimalSkillOffers;

    @Override
    @Transactional
    public SkillDto create(CreateSkillDto skillDto) {
        log.info("Получили новый объект по RestAPI: {} ", skillDto);
        Skill skill = skillMapper.toSkill(skillDto);

        log.info("Проверяем, нет ли уже такого заголовка в базе");
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("Данный заголовок уже существует: " + skill.getTitle());
        }

        log.info("Начинаем писать в базу: {} ", skill);
        skill = skillRepository.save(skill);
        log.info("В базу сохранен новый объект: {} ", skill);

        return skillMapper.toSkillDto(skill);
    }

    @Override
    public List<SkillDto> getByUserId(Long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        validateListNotEmpty(skills, "skill");
        if (skills.isEmpty()) {
            throw new EntityNotFoundException("No skills found for user with id " + userId);
        }
        return skills.stream()
                .map(skill -> skillMapper.toSkillDto(skill))
                .toList();
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        validateListNotEmpty(skills, "skill");
        return skills.stream()
                .map(skill -> skillMapper.toSkillDto(skill))
                .map(skill -> {
                    int offersAmount = skillOfferRepository.countAllOffersOfSkill(skill.id(), userId);
                    return new SkillCandidateDto(skill, offersAmount);
                })
                .toList();
    }

    @Override
    @Transactional
    public void acquireSkillFromOffers(long skillId, long userId) {
        log.info("Пользователь с id {} хочет приобрести скилл с id {}", userId, skillId);
        if (skillRepository.existsById(skillId)) {
            throw new EntityNotFoundException("Skill with id " + skillId + " not found");
        }
        if (skillOfferRepository.countAllOffersOfSkill(skillId, userId) >= minimalSkillOffers) {
            log.info("Добавляем скилл с id {} пользователю с id {}", skillId, userId);
            skillRepository.assignSkillToUser(skillId, userId);
        } else {
            throw new IllegalStateException("Требуется не менее трех предложений скилла для его приобретения");
        }
    }

    private void validateListNotEmpty(List<Skill> value, String paramName) {
        if (value.isEmpty()) {
            throw new DataValidationException(paramName + " лист объектов не найден");
        }
    }
}