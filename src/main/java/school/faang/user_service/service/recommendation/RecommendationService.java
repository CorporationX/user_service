package school.faang.user_service.service.recommendation;

import com.google.common.collect.Lists;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.dto.recommendation.UserSkillGuaranteeDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.mapper.recommendation.SkillOfferMapper;
import school.faang.user_service.mapper.recommendation.UserSkillGuaranteeMapper;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.recommendation.RecommendationValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    private final UserService userService;
    private final RecommendationValidator recommendationValidator;
    private final RecommendationMapper recommendationMapper;
    private final SkillOfferMapper skillOfferMapper;
    private final UserSkillGuaranteeMapper userSkillGuaranteeMapper;
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Transactional
    public RecommendationDto createRecommendation(RecommendationDto recommendationDto) {
        log.info("Start create new recommendation");
        recommendationValidator.validateDateOfLastRecommendation(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        recommendationValidator.validateSkillOffers(recommendationDto);

        Recommendation recommendationReadyToSave = recommendationMapper.toEntity(recommendationDto);
        Recommendation savedRecommendation = recommendationRepository.save(recommendationReadyToSave);

        updateSkillGuarantee(recommendationDto.getSkillOffers(), recommendationDto.getAuthorId(), recommendationDto.getReceiverId());

        List<SkillOffer> savedSkillOffers = saveSkillOffers(recommendationDto.getSkillOffers(), savedRecommendation.getId());
        savedRecommendation.setSkillOffers(savedSkillOffers);

        log.info("New recommendation was created");
        return recommendationMapper.toDto(savedRecommendation);
    }

    @Transactional
    public RecommendationDto updateRecommendation(long recommendationId, RecommendationDto updateRecommendationDto) {
        log.info("Start update the recommendation ID = {}", recommendationId);

        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("The recommendation (ID : %d) doesn't exists in the system", recommendationId);
                    log.error(errorMessage);
                    return new EntityNotFoundException(errorMessage);
                });

        List<SkillOffer> skillOffers = recommendation.getSkillOffers();
        RecommendationDto recommendationDto = recommendationMapper.toDto(recommendation);

        recommendationValidator.validateDateOfLastRecommendation(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        recommendationValidator.validateSkillOffers(updateRecommendationDto);

        List<SkillOfferDto> skillOfferDtosToUpdate = newSkillOffersToUpdate(recommendationDto.getSkillOffers(), updateRecommendationDto.getSkillOffers());
        updateSkillGuarantee(skillOfferDtosToUpdate, recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        List<SkillOffer> savedSkillOffersToUpdate = saveSkillOffers(skillOfferDtosToUpdate, recommendationId);

        recommendationDto.setContent(updateRecommendationDto.getContent());
        Recommendation updatedRecommendation = recommendationRepository.save(recommendationMapper.toEntity(recommendationDto));

        skillOffers.addAll(savedSkillOffersToUpdate);
        updatedRecommendation.setSkillOffers(skillOffers);
        log.info("Recommendation ID = {} was updated", recommendationId);
        return recommendationMapper.toDto(updatedRecommendation);
    }

    @Transactional
    public RecommendationDto getRecommendationById(long id) {
        log.info("Start get the recommendation ID = {}", id);
        Recommendation recommendation = recommendationRepository.findById(id).orElseThrow(() -> {
            String errorMessage = String.format("Couldn't find recommendation (ID : %d) in the system", id);
            log.error(errorMessage);
            return new EntityNotFoundException(errorMessage);
        });
        log.info("Recommendation ID = {} was provided", id);
        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public List<RecommendationDto> getAllUserRecommendation(long userId) {
        log.info("Get all user ID = {} recommendation ", userId);
        return recommendationMapper.toListDto(userService.getUserById(userId).getRecommendationsReceived());
    }

    @Transactional
    public List<RecommendationDto> getAllGivenRecommendations(long userId) {
        log.info("Get all user ID = {}  given recommendations", userId);
        return recommendationMapper.toListDto(userService.getUserById(userId).getRecommendationsGiven());
    }

    @Transactional
    public void deleteRecommendation(long id) {
        log.info("Delete the recommendation ID = {}", id);
        recommendationRepository.deleteById(id);
    }

    public List<SkillOffer> saveSkillOffers(List<SkillOfferDto> skillOfferDtoList, long recommendationId) {
        if (skillOfferDtoList == null || skillOfferDtoList.isEmpty()) {
            return Collections.emptyList();
        }
        skillOfferDtoList.forEach(skillOfferDto -> skillOfferDto.setRecommendationId(recommendationId));
        List<SkillOffer> skillOffers = skillOfferMapper.toListOffersEntity(skillOfferDtoList);

        return skillOfferRepository.saveAll(skillOffers);
    }

    private void updateSkillGuarantee(List<SkillOfferDto> skillOfferDtoList, long authorId, long receiverId) {
        if (skillOfferDtoList == null || skillOfferDtoList.isEmpty()) {
            return;
        }

        List<Long> skillOfferIds = skillOfferDtoList.stream()
                .map(SkillOfferDto::getSkillId).toList();
        List<Long> userSkillIds = userService.getUserSkillsId(receiverId);
        List<UserSkillGuaranteeDto> skillGuaranteeDtoList = new ArrayList<>();

        for (var skillOfferId : skillOfferIds) {
            if (userSkillIds.contains(skillOfferId) &&
                    !userSkillGuaranteeRepository.existsUserSkillGuaranteeByUserIdAndGuarantorIdAndSkillId(receiverId, authorId, skillOfferId)) {
                skillGuaranteeDtoList.add(UserSkillGuaranteeDto.builder()
                        .userId(receiverId)
                        .guarantorId(authorId)
                        .skillId(skillOfferId).build()
                );
            }
        }
        userSkillGuaranteeRepository.saveAll(userSkillGuaranteeMapper.toEntityList(skillGuaranteeDtoList));
    }

    private List<SkillOfferDto> newSkillOffersToUpdate(List<SkillOfferDto> skillOfferDto, List<SkillOfferDto> updateSkillOffer) {
        List<Long> skillOfferIds = skillOfferDto.stream().map(SkillOfferDto::getSkillId).toList();
        List<SkillOfferDto> result = new ArrayList<>();

        for (var skillOffer : updateSkillOffer) {
            if (!skillOfferIds.contains(skillOffer.getSkillId())) {
                result.add(SkillOfferDto.builder().skillId(skillOffer.getSkillId()).build());
            }
        }
        return result;
    }
}