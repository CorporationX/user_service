package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    @Value("${recommendation.service.pageable_page}")
    private int PAGEABLE_PAGE_PARAMETER;
    @Value("${recommendation.service.pageable_size}")
    private int PAGEABLE_SIZE_PARAMETER;
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationValidator recommendationValidator;
    private final UserRepository userRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        recommendationValidator.validateBeforeAction(recommendationDto);
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        Recommendation savedRecommendation = recommendationRepository.save(recommendation);
        saveSkillOffers(recommendation);
        return recommendationMapper.toDto(savedRecommendation);
    }

    private void saveSkillOffers(Recommendation recommendation) {
        long authorId = recommendation.getAuthor().getId();
        long receiverId = recommendation.getReceiver().getId();

        List<SkillOffer> skillOffers = recommendation.getSkillOffers();
        List<Skill> receiverSkills = getReceiverSkills(receiverId);

        for (var skillOffer : skillOffers) {
            long skillId = skillOffer.getSkill().getId();

            if (receiverSkills.contains(skillOffer.getSkill()) && !guaranteeExist(receiverId, skillId, authorId)) {

                updateSkillGuaranteeRepository(receiverId, skillId, authorId);
            } else {
                skillOfferRepository.save(skillOffer);
            }
        }
    }

    private List<Skill> getReceiverSkills(long receiverId) {
        Optional<User> user = userRepository.findById(receiverId);
        return user.map(User::getSkills).orElse(Collections.emptyList());
    }

    private boolean guaranteeExist(long receiverId, long skillId, long authorId) {
        boolean receiverIdExist = userSkillGuaranteeRepository.existsById(receiverId);
        boolean skillIdExist = userSkillGuaranteeRepository.existsById(skillId);
        boolean authorIdIdExist = userSkillGuaranteeRepository.existsById(authorId);

        return (receiverIdExist && skillIdExist && authorIdIdExist);
    }

    @Transactional
    public RecommendationDto update(RecommendationDto recommendationDto) {
        recommendationValidator.validateBeforeAction(recommendationDto);
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendationRepository.update(recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId(),
                recommendationDto.getContent());

        updateSkillOfferRepositoryAndGuaranteeRepository(recommendation);
        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public void delete(long id) {
        recommendationValidator.validateById(id);
        recommendationRepository.deleteById(id);
    }

    @Transactional
    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Pageable pageable = PageRequest.of(PAGEABLE_PAGE_PARAMETER, PAGEABLE_SIZE_PARAMETER);
        Page<Recommendation> pageRecommendations = recommendationRepository.findAllByReceiverId(receiverId, pageable);

        List<Recommendation> recommendations = pageRecommendations.getContent();

        return recommendations.stream().map(recommendationMapper::toDto).toList();
    }

    @Transactional
    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        Pageable pageable = PageRequest.of(PAGEABLE_PAGE_PARAMETER, PAGEABLE_PAGE_PARAMETER);
        Page<Recommendation> pageRecommendations = recommendationRepository.findAllByAuthorId(authorId, pageable);

        List<Recommendation> recommendations = pageRecommendations.getContent();

        return recommendations.stream().map(recommendationMapper::toDto).toList();
    }

    private void updateSkillOfferRepositoryAndGuaranteeRepository(Recommendation recommendation) {
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        for (var skillOffer : recommendation.getSkillOffers()) {
            skillOfferRepository.create(skillOffer.getSkill().getId(), recommendation.getId());
        }

        long authorId = recommendation.getAuthor().getId();
        long receiverId = recommendation.getReceiver().getId();

        List<SkillOffer> skillOffers = recommendation.getSkillOffers();
        List<Skill> receiverSkills = getReceiverSkills(receiverId);

        for (var skillOffer : skillOffers) {
            long skillId = skillOffer.getSkill().getId();
            skillOfferRepository.create(skillId, recommendation.getId());

            if (receiverSkills.contains(skillOffer.getSkill()) && !guaranteeExist(receiverId, skillId, authorId)) {

                updateSkillGuaranteeRepository(receiverId, skillId, authorId);
            }
        }
    }

    private void updateSkillGuaranteeRepository(long receiverId, long skillId, long authorId) {
        User user = userRepository.findById(receiverId)
                .orElseThrow(() -> new DataValidationException("Couldn't find the user in the system"));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new DataValidationException("Couldn't find the skill in the system"));

        User guarantor = userRepository.findById(authorId)
                .orElseThrow(() -> new DataValidationException("Couldn't find the guarantor in the system"));

        UserSkillGuarantee userSkillGuarantee = new UserSkillGuarantee();
        userSkillGuarantee.setUser(user);
        userSkillGuarantee.setGuarantor(guarantor);
        userSkillGuarantee.setSkill(skill);

        userSkillGuaranteeRepository.save(userSkillGuarantee);
    }
}
