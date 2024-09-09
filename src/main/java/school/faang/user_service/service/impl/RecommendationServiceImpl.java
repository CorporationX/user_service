package school.faang.user_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;

    @Override
    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        User author = userRepository.findById(recommendationDto.getAuthorId())
                .orElseThrow(() -> new DataValidationException("Author not found"));
        User receiver = userRepository.findById(recommendationDto.getReceiverId())
                .orElseThrow(() -> new DataValidationException("Receiver not found"));

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation.setCreatedAt(LocalDateTime.now());

        recommendationRepository.save(recommendation);
        handleSkillOffers(recommendation, recommendationDto.getSkillOffers());

        return recommendationMapper.toDto(recommendation);
    }

    @Override
    public List<RecommendationDto> getAllUserRecommendations(long receiverId, Pageable pageable) {
        return recommendationRepository.findAllByReceiverId(receiverId, pageable)
                .stream()
                .map(recommendationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecommendationDto> getAllGivenRecommendations(long authorId, Pageable pageable) {
        return recommendationRepository.findAllByAuthorId(authorId, pageable)
                .stream()
                .map(recommendationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RecommendationDto update(long id, RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Recommendation not found"));

        recommendationMapper.updateFromDto(recommendationDto, recommendation);
        recommendation.setUpdatedAt(LocalDateTime.now());

        recommendationRepository.save(recommendation);
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        handleSkillOffers(recommendation, recommendationDto.getSkillOffers());

        return recommendationMapper.toDto(recommendation);
    }

    @Override
    @Transactional
    public void delete(long id) {
        Recommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Recommendation not found"));
        recommendationRepository.delete(recommendation);
    }

    private void handleSkillOffers(Recommendation recommendation, List<SkillOfferDto> skillOffers) {
        for (SkillOfferDto skillOfferDto : skillOffers) {
            Skill skill = skillRepository.findById(skillOfferDto.getSkillId())
                    .orElseThrow(() -> new DataValidationException("Skill not found"));

            SkillOffer skillOffer = SkillOffer.builder()
                    .skill(skill)
                    .recommendation(recommendation)
                    .build();
            skillOfferRepository.save(skillOffer);
        }
    }
}