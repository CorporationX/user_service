package school.faang.user_service.service.skill.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.dto.skill.SkillDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.skill.interfaces.SkillService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillMapper skillMapper;
    private static final int MIN_SKILL_OFFERS = 3;

    @Override
    public SkillDto create(SkillDto skillDto) {

        validateSkill(skillDto);

        String title = skillDto.getTitle();

        if (skillRepository.existsByTitle(title)) {
            log.error("Attempt to create a duplicate skill: {}", title);
            throw new DataValidationException("Skill with title '" + title + "' already exists.");
        }
        Skill skill = skillMapper.toEntity(skillDto);
        Skill savedSkill = skillRepository.save(skill);

        return skillMapper.toDto(savedSkill);
    }

    @Override
    public List<SkillDto> getUserSkills(Long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skills.stream()
                .map(skillMapper::toDto)
                .toList();
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);

        Map<Skill, Long> skillCountMap = skills.stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()));

        return skillCountMap.entrySet().stream()
                .map(entry -> skillMapper.toSkillCandidateDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    public SkillDto acquireSkillFromOffers(Long skillId, Long userId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new DataValidationException("Skill not found"));

        Optional<Skill> acquiredSkill = skillRepository.findUserSkill(skillId, userId);
        if (acquiredSkill.isPresent()) {
            log.warn("Skill with id: {} already acquired by user with id: {}", skillId, userId);
            return skillMapper.toDto(acquiredSkill.get());
        }

        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (offers.size() < MIN_SKILL_OFFERS) {
            throw new DataValidationException("Not enough skill offers to acquire this skill.");
        }

        skillRepository.assignSkillToUser(skillId, userId);

        List<UserSkillGuarantee> guarantees = offers.stream()
                .map(offer -> UserSkillGuarantee.builder()
                        .user(offer.getRecommendation().getReceiver())
                        .skill(offer.getSkill())
                        .guarantor(offer.getRecommendation().getAuthor())
                        .build())
                .collect(Collectors.toList());

        userSkillGuaranteeRepository.saveAll(guarantees);

        return skillMapper.toDto(skill);
    }

    @Override
    public List<Skill> findAllSkillsById(List<Long> skillIds) {
        return skillRepository.findAllById(skillIds);
    }

    @Override
    public List<Skill> findSkillsByUserId(Long userId) {
        return skillRepository.findAllByUserId(userId);
    }

    @Override
    public List<Skill> findSkillsByGoalId(Long goalId) {
        return skillRepository.findSkillsByGoalId(goalId);
    }

    @Override
    public void saveAllSkills(List<Skill> skills) {
        skillRepository.saveAll(skills);
    }

    private void validateSkill(SkillDto skillDto) {
        if (Objects.isNull(skillDto)) {
            throw new DataValidationException("Skill data is null");
        }
        String title = skillDto.getTitle();
        if (Objects.isNull(title) || title.isBlank()) {
            throw new DataValidationException("Skill title is empty");
        }
    }
}
