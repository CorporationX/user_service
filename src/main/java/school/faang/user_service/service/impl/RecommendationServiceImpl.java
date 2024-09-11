package school.faang.user_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.RecommendationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final static String AUTHOR_NOT_FOUND_MESSAGE = "Author not found";
    private final static String RECEIVER_NOT_FOUND_MESSAGE = "Receiver not found";
    private final static String SKILL_NOT_FOUND_MESSAGE = "Skill not found";
    private final static String RECOMMENDATION_NOT_FOUND_MESSAGE = "Recommendation not found";

    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;

    @Override
    @Transactional
    public RecommendationDto createRecommendation(RecommendationDto recommendationDto) {
        User author = userRepository.findById(recommendationDto.authorId())
                .orElseThrow(() -> new DataValidationException(AUTHOR_NOT_FOUND_MESSAGE));
        User receiver = userRepository.findById(recommendationDto.receiverId())
                .orElseThrow(() -> new DataValidationException(RECEIVER_NOT_FOUND_MESSAGE));

        Recommendation recommendation = recommendationMapper.toRecommendation(recommendationDto);
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation.setCreatedAt(LocalDateTime.now());

        recommendationRepository.save(recommendation);
        handleSkillOffers(recommendation, recommendationDto.skillOffers());

        return recommendationMapper.toRecommendationDto(recommendation);
    }

    @Override
    public List<RecommendationDto> getAllUserRecommendations(long receiverId, Pageable pageable) {
        return recommendationRepository.findAllByReceiverId(receiverId, pageable)
                .stream()
                .map(recommendationMapper::toRecommendationDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecommendationDto> getAllGivenRecommendations(long authorId, Pageable pageable) {
        return recommendationRepository.findAllByAuthorId(authorId, pageable)
                .stream()
                .map(recommendationMapper::toRecommendationDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RecommendationDto updateRecommendation(long id, RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(RECEIVER_NOT_FOUND_MESSAGE));

        recommendationMapper.updateFromDto(recommendationDto, recommendation);
        recommendation.setUpdatedAt(LocalDateTime.now());

        recommendationRepository.save(recommendation);
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        handleSkillOffers(recommendation, recommendationDto.skillOffers());

        return recommendationMapper.toRecommendationDto(recommendation);
    }

    @Override
    @Transactional
    public void deleteRecommendation(long id) {
        Recommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(RECOMMENDATION_NOT_FOUND_MESSAGE));
        recommendationRepository.delete(recommendation);
    }

    private void handleSkillOffers(Recommendation recommendation, List<SkillOfferDto> skillOffers) {
        for (SkillOfferDto skillOfferDto : skillOffers) {
            Skill skill = skillRepository.findById(skillOfferDto.skillId())
                    .orElseThrow(() -> new DataValidationException(SKILL_NOT_FOUND_MESSAGE));

            SkillOffer skillOffer = SkillOffer.builder()
                    .skill(skill)
                    .recommendation(recommendation)
                    .build();
            skillOfferRepository.save(skillOffer);
        }
    }
}