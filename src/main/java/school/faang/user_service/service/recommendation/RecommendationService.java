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
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.skillOffer.SkillOfferService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.StreamSupport;

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
    private final SkillOfferService skillOfferService;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        checkNotRecommendBeforeSixMonths(recommendationDto);
        skillOfferService.checkForSkills(recommendationDto.getSkillOffers());
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        skillOfferService.saveSkillOffers(recommendation);

        recommendationRepository.save(recommendation);

        return recommendationMapper.toDto(recommendation);
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {

        checkNotRecommendBeforeSixMonths(recommendationDto);
        skillOfferService.checkForSkills(recommendationDto.getSkillOffers());
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        skillOfferService.saveSkillOffers(recommendation);

        recommendationRepository.save(recommendation);

        return recommendationMapper.toDto(recommendation);
    }

    public void delete(long recommendationId) {
        recommendationRepository.deleteById(recommendationId);
    }

    public List<RecommendationDto> getAllUserRecommendations(long recieverId, Pageable pageable) {
        List<Recommendation> recommendations = recommendationRepository
                .findAllByReceiverId(recieverId, pageable)
                .getContent();

        return recommendations.stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId, Pageable pageable) {
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
                    log.error(ExceptionMessages.ARGUMENT_NOT_FOUND);
                    return new NoSuchElementException(ExceptionMessages.ARGUMENT_NOT_FOUND);
                });
        LocalDateTime localDateTime = LocalDateTime.now().minus(COUNT_MONTHS, ChronoUnit.MONTHS);
        if (recommendation.getUpdatedAt().isAfter(localDateTime)) {
            log.error(ExceptionMessages.RECOMMENDATION_FREQUENCY);
            throw new IllegalStateException(ExceptionMessages.RECOMMENDATION_FREQUENCY);
        }
    }

//    private void checkForSkills(List<SkillOfferDto> skillOfferDtos) {
//        List<Long> skillOfferIds = skillOfferDtos.stream()
//                .map(SkillOfferDto::getId)
//                .toList();
//
//        List<SkillOffer> skillOffers = StreamSupport
//                .stream(skillOfferRepository.findAllById(skillOfferIds).spliterator(), false)
//                .toList();
//
//        if (skillOffers.size() != skillOfferIds.size()) {
//            log.error(ExceptionMessages.SKILL_NOT_FOUND);
//            throw new NoSuchElementException(ExceptionMessages.SKILL_NOT_FOUND);
//        }
//    }

//    private void saveSkillOffers(Recommendation recommendation) {
//        User author = recommendation.getAuthor();
//        User receiver = recommendation.getReceiver();
//
//        List<Skill> existingSkills = skillRepository.findAllByUserId(receiver.getId());
//        List<SkillOffer> skillOffers = recommendation.getSkillOffers();
//
//        boolean isAuthorSkillGuarantee = userSkillGuaranteeRepository.existsById(author.getId());
//
//        List<UserSkillGuarantee> listForGuaranteeRepository = new ArrayList<>();
//        List<SkillOffer> listForOfferRepository = new ArrayList<>();
//
//        for (SkillOffer skillOffer : skillOffers) {
//            if (skillOffer.getSkill() != null
//                    && existingSkills.contains(skillOffer.getSkill())
//                    && !isAuthorSkillGuarantee) {
//                listForGuaranteeRepository.add(UserSkillGuarantee.builder()
//                        .user(receiver)
//                        .skill(skillOffer.getSkill())
//                        .guarantor(author)
//                        .build());
//            } else {
//                listForOfferRepository.add(skillOffer);
//            }
//        }
//        userSkillGuaranteeRepository.saveAll(listForGuaranteeRepository);
//        skillOfferRepository.saveAll(listForOfferRepository);
//    }
}
