package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.validator.RecommendationValidator;
import school.faang.user_service.validator.UserValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationValidator recommendationValidator;
    private final UserValidator userValidator;
    private final UserService userService;
    private final SkillOfferService skillOfferService;
    private final SkillService skillService;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        recommendationValidator.validateAuthorAndReceiverId(recommendationDto);
        recommendationValidator.validateSkillAndTimeRequirementsForGuarantee(recommendationDto);

        Recommendation recommendation = recommendationRepository.save(createRecommendationFromDto(recommendationDto));

        saveNewSkillOffers(recommendation);
        skillService.addGuarantee(recommendation);

        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public RecommendationDto update(RecommendationDto recommendationDto) {
        recommendationValidator.validateAuthorAndReceiverId(recommendationDto);
        recommendationValidator.validateSkillAndTimeRequirementsForGuarantee(recommendationDto);

        recommendationRepository.update(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent()
        );
        skillOfferService.deleteAllByRecommendationId(recommendationDto.getId());
        updateSkillOffers(recommendationDto);
        skillService.addGuarantee(getRecommendationById(recommendationDto.getId()));

        return recommendationDto;
    }

    public void delete(long id) {
        recommendationValidator.validateRecommendationExistsById(id);
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        userValidator.validateUserById(receiverId);
        List<Recommendation> allRecommendations = getAllRecommendationsByReceiverId(receiverId);
        return recommendationMapper.toDtoList(allRecommendations);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        userValidator.validateUserById(authorId);
        List<Recommendation> allRecommendations = getAllRecommendationsByAuthorId(authorId);
        return recommendationMapper.toDtoList(allRecommendations);
    }

    public Recommendation getRecommendationById(Long recommendationId) {
        return recommendationRepository.findById(recommendationId).orElseThrow(() ->
                new EntityNotFoundException("Recommendation with id #" + recommendationId + " not found"));
    }

    public Recommendation createRecommendationFromDto(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendation.setAuthor(userService.findUser(recommendationDto.getAuthorId()));
        recommendation.setReceiver(userService.findUser(recommendationDto.getReceiverId()));
        recommendation.setSkillOffers(skillOfferService.findAllByUserId(recommendationDto.getReceiverId()));
        return recommendation;
    }

    private List<Recommendation> getAllRecommendationsByReceiverId(long receiverId) {
        Page<Recommendation> recommendations = recommendationRepository.findAllByReceiverId(receiverId, Pageable.unpaged());
        return recommendations.getContent();
    }

    private List<Recommendation> getAllRecommendationsByAuthorId(long authorId) {
        Page<Recommendation> recommendations = recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged());
        return recommendations.getContent();
    }

    private void saveNewSkillOffers(Recommendation recommendation) {
        recommendationValidator.validateRecommendationExistsById(recommendation.getId());
        recommendation.getSkillOffers().forEach(skillOffer ->
                skillOfferService.create(skillOffer.getSkill().getId(), recommendation.getId())
        );
    }

    private void updateSkillOffers(RecommendationDto recommendationDto) {
        recommendationValidator.validateRecommendationExistsById(recommendationDto.getId());
        recommendationDto.getSkillOffers().forEach(skillOffer ->
                skillOfferService.create(skillOffer.getSkillId(), recommendationDto.getId())
        );
    }
}
