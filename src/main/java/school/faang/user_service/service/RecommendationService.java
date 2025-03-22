package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.utils.validationUtils.RecommendationValidation;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto update(RecommendationDto recommendationDto) {
        RecommendationValidation.validateRecommendationContent(recommendationDto.getContent());
        RecommendationValidation.validateSkills(recommendationDto, skillOfferRepository.findAllSkillOffers());
        LocalDateTime lastRecommendation = recommendationRepository.
                findLastRecommendationDate(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        RecommendationValidation.validateRecommendationDate(lastRecommendation);

        Recommendation recommendation = recommendationMapper.toRecommendation(recommendationDto);

        updateRecommendation(recommendation);
        deleteAllAndCreate(recommendation);
        return recommendationMapper.toRecommendationDto(recommendation);
    }

    public void delete(Long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllGivenRecommendation(Long authorId) {
        if (authorId == null) {
            throw new DataValidationException("Id автора не может быть null");
        }
        Pageable pageable = PageRequest.of(0 , 10);
        Page<Recommendation> recommendationPage = recommendationRepository.findAllByAuthorId(authorId, pageable);
        List<Recommendation> recommendationList = recommendationPage.getContent();
        return recommendationMapper.toRecommendationDtoList(recommendationList);
    }

    public List<RecommendationDto> getAllUserRecommendations(Long receiverId) {
        if (receiverId == null) {
            throw new DataValidationException("Id пользователя не может быть null");
        }
        Pageable pageable = PageRequest.of(0 , 10);
        Page<Recommendation> recommendationPage = recommendationRepository.findAllByReceiverId(receiverId, pageable);
        List<Recommendation> recommendationList = recommendationPage.getContent();
        return recommendationList.stream()
                .map(recommendationMapper::toRecommendationDto)
                .toList();
    }

    private void deleteAllAndCreate(Recommendation recommendation) {
        List<SkillOffer> skillOffersOfReceiver =
                skillOfferRepository.findAllByUserId(recommendation.getReceiver().getId());

        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        for (SkillOffer skillOffer : recommendation.getSkillOffers()) {
            skillOfferRepository.create(skillOffer.getSkill().getId(), recommendation.getId());
            if (skillOffersOfReceiver.contains(skillOffer)) {
                UserSkillGuarantee userSkillGuarantee = new UserSkillGuarantee();
                userSkillGuarantee.setUser(recommendation.getReceiver());
                userSkillGuarantee.setGuarantor(recommendation.getAuthor());
                userSkillGuarantee.setSkill(skillOffer.getSkill());
                skillOffer.getSkill().setGuarantees(List.of(userSkillGuarantee));
                userSkillGuaranteeRepository.save(userSkillGuarantee);
            }
        }
    }

    private void updateRecommendation(Recommendation recommendation) {
        Long authorId = recommendation.getAuthor().getId();
        Long receiverId = recommendation.getReceiver().getId();
        String content = recommendation.getContent();
        if (authorId == null) {
             throw new DataValidationException("Author id can't be null");
        } else if (receiverId == null) {
            throw new DataValidationException("Author id can't be null");
        }

        recommendationRepository.update(authorId, receiverId, content);
    }
}
