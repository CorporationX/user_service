package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    private static final int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;

    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Такой навык уже есть");
        }
        Skill skillEntity = skillRepository.save(skillMapper.toEntity(skillDto));
        return skillMapper.toDto(skillEntity);
    }

    public List<SkillDto> getUserSkills(Long userId) {
        return skillRepository.findAllByUserId(userId)
                .stream()
                .map(skill -> skillMapper.toDto(skill))
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillRepository.findSkillsOfferedToUser(userId)
                .stream()
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()))
                .entrySet()
                .stream()
                .filter(skill -> skill.getValue() >= 3)
                .map(entry -> new SkillCandidateDto(
                        skillMapper.toDto(entry.getKey()),
                        entry.getValue()))
                .toList();
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Optional<Skill> skill = skillRepository.findUserSkill(skillId, userId);
        if (skill.isPresent()) {
            throw new IllegalStateException("Скилл уже есть");
        }
        List<SkillOffer> offers = skillOfferRepository
                .findAllOffersOfSkill(skillId, userId);
        if (offers.size() >= MIN_SKILL_OFFERS) {
            skillRepository.assignSkillToUser(skillId, userId);
            List<Recommendation> recommendationList = new ArrayList<>();
            for (SkillOffer offer : offers) {
                recommendationList.add(offer.getRecommendation());
            }
            User user = userRepository.findUser(userId);
            user.setRecommendationsReceived(recommendationList);
            return skillMapper.toDto(offers.get(0).getSkill());
        }
        throw new IllegalArgumentException("Рекомендаций меньше 3");
    }
}
