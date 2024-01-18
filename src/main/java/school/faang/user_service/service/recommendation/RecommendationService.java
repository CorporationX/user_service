package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.recommendation.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;


    public RecommendationDto create(RecommendationDto recommendationDto) {
        LocalDateTime now = LocalDateTime.now();
        long monthsDifference = recommendationDto.getCreatedAt().until(now, ChronoUnit.MONTHS);
        if (monthsDifference <= 6) {
            System.out.println("Нельзя дать рекомендацию, так как не прошло 6 месяцев с последней рекомендации.");
        } else {
            recommendationRepository.save(recommendationMapper.toEntity(recommendationDto));
        }
        List<SkillOfferDto> skillOffers = recommendationDto.getSkillOffers();
        for (SkillOfferDto skillOfferDto: skillOffers) {
            skillOfferRepository.create(skillOfferDto.getIdSkill() , recommendationDto.getId());
        }
        Optional<User> userById = userRepository.findById(recommendationDto.getReceiverId());
        if (userById.isPresent()) {
            List<Skill> skills = userById.get().getSkills();
            for (Skill skill: skills) {
                for (SkillOfferDto skillOffer : skillOffers) {
                    Skill byId = skillRepository.findById(skillOffer.getIdSkill()).orElseThrow();
                    if (skill.equals(byId)) {
                        userSkillGuaranteeRepository.create(userById.get().getId(), skill.getId(), recommendationDto.getAuthorId());
                    }
                }
            }
        }
        return recommendationDto;
    }

    public List<RecommendationDto> getAllUserRecommendations(long recieverId) {
        return recommendationRepository.findAllByReceiverId(recieverId).stream()
                .map(recommendation -> recommendationMapper.toDto(recommendation))
                .toList();
    }

// BJS2-1457
    public void deleteRecommendation(long id) {
        if (!userRepository.existsById(id)) {
            throw new DataValidationException("id not found / id не найден");
        }
        recommendationRepository.deleteById(id);
    }
}