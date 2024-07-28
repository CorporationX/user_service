package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private static final int COUNT_MONTHS = 6;
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);

        checkNotRecommendBeforeSixMonths(recommendationDto);
        checkForSkills(recommendationDto.getSkillOffers());
        saveSkillOffers(recommendation);

        recommendationRepository.save(recommendation);

        return recommendationMapper.toDto(recommendation);
    }

    public RecommendationDto update(RecommendationDto recommendationDto){
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);

        checkNotRecommendBeforeSixMonths(recommendationDto);
        checkForSkills(recommendationDto.getSkillOffers());
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        saveSkillOffers(recommendation);

        recommendationRepository.save(recommendation);

        return recommendationMapper.toDto(recommendation);
    }

    public void delete(long recommendationId){
        recommendationRepository.deleteById(recommendationId);
    }

    public List<RecommendationDto> getAllUserRecommendations(long recieverId, Pageable pageable){
        List<Recommendation> recommendations = recommendationRepository
                .findAllByReceiverId(recieverId, pageable)
                .getContent();

        return recommendations.stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId, Pageable pageable){
        List<Recommendation> recommendations = recommendationRepository
                .findAllByAuthorId(authorId, pageable)
                .getContent();

        return recommendations.stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    private void checkNotRecommendBeforeSixMonths(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId())
                .orElseThrow(() -> {
                    log.error("");
                    return new IllegalStateException("аргумент не найден");
                });
        LocalDateTime localDateTime = LocalDateTime.now().minus(COUNT_MONTHS, ChronoUnit.MONTHS);
        if (recommendation.getUpdatedAt().isAfter(localDateTime)) {
            log.error(" рекомендация может быть один раз в 6 месяцев");
            throw new IllegalStateException("рекомендация может быть один раз в 6 месяцев");
        }
    }

    private void checkForSkills(List<SkillOfferDto> skillOfferDtos) {
        for (SkillOfferDto skillOfferDto : skillOfferDtos) {
            if (!skillOfferRepository.existsById(skillOfferDto.getSkillId())) {
                throw new NullPointerException("Навыка нет в системе");
            }
        }
    }

    private void saveSkillOffers(Recommendation recommendation) {
        User author = recommendation.getAuthor();
        User receiver = recommendation.getReceiver();

        List<Skill> existingSkills = skillRepository.findAllByUserId(receiver.getId());
        List<SkillOffer> skillOffers = recommendation.getSkillOffers();

        boolean AuthorSkillGuarantee = userSkillGuaranteeRepository.existsById(author.getId());

        for (SkillOffer skillOffer : skillOffers) {
            if (existingSkills.contains(skillOffer.getSkill()) && !AuthorSkillGuarantee) {
                addAndSaveGuarantee(author, receiver, skillOffer.getSkill());
            } else {
                skillOfferRepository.save(skillOffer);
            }
        }
    }

    private void addAndSaveGuarantee(User author, User receiver, Skill skill) {
        userSkillGuaranteeRepository.save(UserSkillGuarantee.builder()
                .user(receiver)
                .skill(skill)
                .guarantor(author)
                .build());
    }
}
