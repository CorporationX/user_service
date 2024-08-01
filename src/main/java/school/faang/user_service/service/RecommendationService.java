package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationValidator recommendationValidator;
    private final SkillOfferMapper skillOfferMapper;
    private final UserRepository userRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Transactional
    public RecommendationDto createRecommendational(RecommendationDto recommendationDto) {
        recommendationValidator.validateLastUpdate(recommendationDto);
        recommendationValidator.validateOfferSkillsIsExisting(recommendationDto.getSkillOffers());

        saveSkillOffers(recommendationDto);
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        Recommendation savedRecommendation = recommendationRepository.save(recommendation);
        return recommendationMapper.toDto(savedRecommendation);
    }

    private void saveSkillOffers(RecommendationDto recommendationDto) {
        long authorId = recommendationDto.getAuthorId();
        long receiverId = recommendationDto.getReceiverId();

        List<UserSkillGuarantee> userSkillGuaranteeListToSave = new ArrayList<>();
        List<SkillOfferDto> skillOfferDtosToSave = new ArrayList<>();

        List<SkillOfferDto> skillOfferDtoList = recommendationDto.getSkillOffers();
        List<Long> receiverSkillIds = getUserSkillsByUserId(receiverId);

        for (SkillOfferDto skillOffer : skillOfferDtoList) {
            long skillId = skillOffer.getSkillId();
            if (receiverSkillIds.contains(skillId) && !guaranteeExist(receiverId, skillId, authorId)) {
                userSkillGuaranteeListToSave.add(getSkillGuaranteeEntity(receiverId, skillId, authorId));
            } else {
                skillOfferDtosToSave.add(skillOffer);
            }
        }
        userSkillGuaranteeRepository.saveAll(userSkillGuaranteeListToSave);
        skillOfferRepository.saveAll(skillOfferMapper.toSkillOfferEntityList(skillOfferDtosToSave));
    }

    private List<Long> getUserSkillsByUserId(long receiverId) {
        User user = userRepository.findById(receiverId).orElseThrow(
                () -> new EntityNotFoundException("Couldn't find user with ID= " + receiverId));
        return user.getSkills().stream().map(Skill::getId).toList();
    }

    private boolean guaranteeExist(long receiverId, long skillId, long authorId) {
        boolean receiverIdExist = userSkillGuaranteeRepository.existsById(receiverId);
        boolean skillIdExist = userSkillGuaranteeRepository.existsById(skillId);
        boolean authorIdIdExist = userSkillGuaranteeRepository.existsById(authorId);

        return (receiverIdExist && skillIdExist && authorIdIdExist);
    }

    @Transactional
    public RecommendationDto updateRecommendation(long recommendationId, RecommendationDto recommendationDto) {
        recommendationValidator.recommendationExist(recommendationId);
        recommendationValidator.validateLastUpdate(recommendationDto);
        recommendationValidator.validateOfferSkillsIsExisting(recommendationDto.getSkillOffers());

        skillOfferRepository.deleteAllByRecommendationId(recommendationId);
        saveSkillOffers(recommendationDto);
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);

        recommendationRepository.update(recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId(),
                recommendationDto.getContent());

        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public void delete(long id) {
        recommendationValidator.recommendationExist(id);
        recommendationRepository.deleteById(id);
    }

    @Transactional
    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        User user = userRepository.findById(receiverId).orElseThrow(()->
                new EntityNotFoundException("Couldn't Find user with ID = " + receiverId + " in the systmem"));
        List<Recommendation> userRecommendations = user.getRecommendationsReceived();

        return userRecommendations.stream().map(recommendationMapper::toDto).toList();
    }

    @Transactional
    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        User user = userRepository.findById(authorId).orElseThrow(()->
                new EntityNotFoundException("Couldn't Find user with ID = " + authorId + " in the systmem"));
        List<Recommendation> recommendationsGiven = user.getRecommendationsGiven();

        return recommendationsGiven.stream().map(recommendationMapper::toDto).toList();
    }

    private UserSkillGuarantee getSkillGuaranteeEntity(long receiverId, long skillId, long authorId) {
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

        return userSkillGuarantee;
    }
}
