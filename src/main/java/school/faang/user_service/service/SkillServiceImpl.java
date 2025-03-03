package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidateException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private static final int MIN_SKILL_OFFERS = 3;

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserRepository userRepository;

    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new IllegalArgumentException("Skill already exists.");
        }
        Skill skill = skillMapper.toEntity(skillDto);
        Skill savedSkill = skillRepository.save(skill);

        return skillMapper.toDto(savedSkill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidateException("User with ID " + userId + " not found.");
        }
        List<Skill> userSkills = skillRepository.findAllByUserId(userId);
        return userSkills.stream()
                .map(skillMapper::toDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skillsOfferedToUser = skillRepository.findSkillsOfferedToUser(userId);
        Map<Skill, Long> skillOffersCount = skillsOfferedToUser.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return skillOffersCount.entrySet().stream()
                .map(entry -> {
                    SkillCandidateDto dto = new SkillCandidateDto();
                    dto.setSkill(skillMapper.toDto(entry.getKey()));
                    dto.setOffersAmount(entry.getValue());
                    return dto;
                })
                .toList();
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Optional<Skill> existingSkill = skillRepository.findUserSkill(skillId, userId);
        if (existingSkill.isPresent()) {
            throw new DataValidateException(String.format("User %d already has the skill %d.", userId, skillId));
        }

        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (offers.size() < MIN_SKILL_OFFERS) {
            throw new IllegalArgumentException(String.format("Not enough offers to acquire skill %d for user %d.",
                    skillId, userId));
        }
        skillRepository.assignSkillToUser(skillId, userId);
        addUserSkillGuarantee(offers);
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new DataValidateException(String.format("Skill with id %d not found.", skillId)));
        return skillMapper.toDto(skill);
    }

    public void addUserSkillGuarantee(List<SkillOffer> offers) {
        userSkillGuaranteeRepository.saveAll(offers.stream()
                .map(offer -> UserSkillGuarantee.builder()
                        .user(offer.getRecommendation().getReceiver())
                        .skill(offer.getSkill())
                        .guarantor(offer.getRecommendation().getAuthor())
                        .build()
                )
                .distinct()
                .toList());
    }
}