package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationReceivedEvent;
import school.faang.user_service.dto.recommendation.RecommendationUpdateDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.dto.recommendation.SkillOfferUpdateDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.invalidFieldException.DataValidationException;
import school.faang.user_service.exception.notFoundExceptions.SkillNotFoundException;
import school.faang.user_service.exception.notFoundExceptions.recommendation.RecommendationNotFoundException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.messaging.recommendation.RecommendationReceivedEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillService skillService;
    private final UserService userService;
    private final RecommendationReceivedEventPublisher recommendationReceivedEventPublisher;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        validatePreviousRecommendation(recommendationDto);
        checkSkillsInRepository(recommendationDto);

        Long recommendationId = recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());

        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new RecommendationNotFoundException("Recommendation not found"));

        saveSkillOffers(recommendation, recommendationDto.getSkillOffers());
        checkGuarantee(recommendation);

        recommendationRepository.save(recommendation);

        RecommendationReceivedEvent event = new RecommendationReceivedEvent(
                recommendationDto.getId(),
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getCreatedAt());
        recommendationReceivedEventPublisher.publish(event);
        return recommendationMapper.toDto(recommendation);
    }

    public RecommendationUpdateDto update(RecommendationUpdateDto recommendationUpdateDto) {
        validatePreviousForUpdateRecommendation(recommendationUpdateDto);
        checkSkillsInRepositoryUpdate(recommendationUpdateDto);

        Recommendation recommendation = recommendationRepository.findById(recommendationUpdateDto.getId())
                .orElseThrow(()-> new RecommendationNotFoundException("Recommendation not found"));

        recommendation.setContent(recommendationUpdateDto.getContent());

        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());

        for (SkillOfferUpdateDto skillOffer : recommendationUpdateDto.getSkillOffers()) {
            skillOfferRepository.create(skillOffer.getSkillId(), recommendationUpdateDto.getId());
        }

        saveSkillOffersUpdate(recommendation, recommendationUpdateDto.getSkillOffers());

        checkGuarantee(recommendation);
        recommendationRepository.save(recommendation);
        return recommendationMapper.toUpdateDto(recommendation);
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> recommendations = recommendationRepository
                .findAllByReceiverId(receiverId, Pageable.unpaged());
        if (recommendations == null) {
            return new ArrayList<>();
        }
        return recommendations.getContent()
                .stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        Page<Recommendation> recommendations = recommendationRepository
                .findAllByReceiverId(authorId, Pageable.unpaged());
        if (recommendations == null) {
            return new ArrayList<>();
        }
        return recommendations.getContent()
                .stream()
                .map(recommendationMapper::toDto)
                .toList();
    }
    private void validatePreviousRecommendation(RecommendationDto recommendationDto) {
        Optional<Recommendation> recommendationByAuthorIdAndReceiverId = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc
                        (recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        if (recommendationByAuthorIdAndReceiverId.isEmpty()) {
            return;
        }
        LocalDateTime createdAt = recommendationByAuthorIdAndReceiverId.get().getCreatedAt();
        if (createdAt.isAfter(LocalDateTime.now().minusMonths(6))) {
            throw new DataValidationException("Recommendation duration has not expired");
        }
    }

    private void validatePreviousForUpdateRecommendation(RecommendationUpdateDto recommendationUpdateDto) {
        Optional<Recommendation> recommendationByAuthorIdAndReceiverId = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc
                        (recommendationUpdateDto.getAuthorId(), recommendationUpdateDto.getReceiverId());
        if (recommendationByAuthorIdAndReceiverId.isEmpty()) {
            return;
        }
        LocalDateTime createdAt = recommendationByAuthorIdAndReceiverId.get().getUpdatedAt();
        if (createdAt.isAfter(LocalDateTime.now().minusMonths(6))) {
            throw new DataValidationException("Recommendation duration has not expired");
        }
    }

    private void checkSkillsInRepository(RecommendationDto recommendationDto) {
        for (SkillOfferDto skillOfferDto : recommendationDto.getSkillOffers()) {
            skillRepository.findById(skillOfferDto.getSkillId())
                    .orElseThrow(() -> new SkillNotFoundException("Skill not found in data base"));
        }
        Set<SkillOfferDto> duplicates = findDuplicates(recommendationDto.getSkillOffers());
        if (!duplicates.isEmpty()) {
            throw new DataValidationException("The recommendation contains the following duplicate skills: " + duplicates);
        }
    }

    private void checkSkillsInRepositoryUpdate(RecommendationUpdateDto recommendationUpdateDto) {
        for (SkillOfferUpdateDto skillOfferDto : recommendationUpdateDto.getSkillOffers()) {
            skillRepository.findById(skillOfferDto.getSkillId())
                    .orElseThrow(() -> new SkillNotFoundException("Skill not found in data base"));
        }
        Set<SkillOfferUpdateDto> duplicates = findDuplicatesUpdate(recommendationUpdateDto.getSkillOffers());
        if (!duplicates.isEmpty()) {
            throw new DataValidationException("The recommendation contains the following duplicate skills: " + duplicates);
        }
    }

    private void saveSkillOffers(Recommendation recommendation, List<SkillOfferDto> skillOffers) {
        skillOffers.forEach(offer -> {
            long OfferId = skillOfferRepository.create(offer.getSkillId(), recommendation.getId());
            recommendation.addSkillOffer(skillOfferRepository.findById(OfferId).orElse(null));
        });
    }

    private void saveSkillOffersUpdate(Recommendation recommendation, List<SkillOfferUpdateDto> skillOffers) {
        skillOffers.forEach(offer -> {
            long OfferId = skillOfferRepository.create(offer.getSkillId(), recommendation.getId());
            recommendation.addSkillOffer(skillOfferRepository.findById(OfferId).orElse(null));
        });
    }

    private void checkGuarantee(Recommendation recommendation) {
        User receiver = userService.getUserFromRepository(recommendation.getReceiver().getId()); //получатель рекомендации
        User author = userService.getUserFromRepository(recommendation.getAuthor().getId()); //автор рекомендации
        for (SkillOffer skillOffer : recommendation.getSkillOffers()) {
            Optional<Skill> optionalSkill = skillRepository.findUserSkill(skillOffer.getSkill().getId(), receiver.getId());
            if (optionalSkill.isPresent()) {
                userSkillGuaranteeRepository
                        .create(receiver.getId(), skillOffer.getSkill().getId(), author.getId());
            }
        }
    }

    private static Set<SkillOfferDto> findDuplicates(List<SkillOfferDto> list) {
        Set<SkillOfferDto> seen = new HashSet<>();
        return list.stream()
                .filter(e -> !seen.add(e))
                .collect(Collectors.toSet());
    }

    private static Set<SkillOfferUpdateDto> findDuplicatesUpdate(List<SkillOfferUpdateDto> list) {
        Set<SkillOfferUpdateDto> seen = new HashSet<>();
        return list.stream()
                .filter(e -> !seen.add(e))
                .collect(Collectors.toSet());
    }
}

