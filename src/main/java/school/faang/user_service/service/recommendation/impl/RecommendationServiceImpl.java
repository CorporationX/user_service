package school.faang.user_service.service.recommendation.impl;

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
import school.faang.user_service.service.recommendation.RecommendationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;

    @Override
    @Transactional
    public RecommendationDto createRecommendation(RecommendationDto recommendationDto) {
        User author = userRepository.findById(recommendationDto.authorId())
                .orElseThrow(() -> new DataValidationException(
                        "Author with id " + recommendationDto.authorId() + " not found"));

        User receiver = userRepository.findById(recommendationDto.receiverId())
                .orElseThrow(() -> new DataValidationException(
                        "Receiver with id " + recommendationDto.receiverId() + " not found"));

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
                .collect(toList());
    }

    @Override
    public List<RecommendationDto> getAllGivenRecommendations(long authorId, Pageable pageable) {
        return recommendationRepository.findAllByAuthorId(authorId, pageable)
                .stream()
                .map(recommendationMapper::toRecommendationDto)
                .collect(toList());
    }

    @Override
    @Transactional
    public RecommendationDto updateRecommendation(long id, RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Recommendation with id " + id + " not found"));

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
                .orElseThrow(() -> new DataValidationException("Recommendation with id " + id + " not found"));
        recommendationRepository.delete(recommendation);
    }

    private void handleSkillOffers(Recommendation recommendation, List<SkillOfferDto> skillOffers) {
        List<Long> skillIds = skillOffers.stream()
                .map(SkillOfferDto::skillId)
                .collect(toList());

        List<Skill> skills = skillRepository.findAllById(skillIds);

        Map<Long, Skill> skillsMap = skills.stream().collect(Collectors.toMap(Skill::getId, skill -> skill));

        List<SkillOffer> skillOffersToSave = skillOffers.stream().map(skillOfferDto -> {
            Skill skill = skillsMap.get(skillOfferDto.skillId());
            if (skill == null) {
                throw new DataValidationException("Skill with id " + skillOfferDto.skillId() + " not found");
            }

            return SkillOffer.builder()
                    .skill(skill)
                    .recommendation(recommendation)
                    .build();
        }).collect(toList());

        skillOfferRepository.saveAll(skillOffersToSave);
    }
}