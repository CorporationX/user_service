package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class RecommendationService {
    private static final int NUMBER_OF_MONTHS_AFTER_PREVIOUS_RECOMMENDATION = 6;

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final RecommendationMapper recommendationMapper;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
//FIXME: в п5 написано что skillOffer не обязателен (странно, ведь рекомендация нужна,
// чтобы добавить в свой список скиллов подтвержденный скилл)
        checkIfOfferedSkillsExist(recommendationDto);
        checkIfAcceptableTimeForRecommendation(recommendationDto);

        Long recommendationId = recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());

        recommendationDto.setId(recommendationId);
        addSkillOffersAndGuarantee(recommendationDto);
        Recommendation recommendation = recommendationRepository.findById(recommendationDto.getId()).orElseThrow(() -> new NoSuchElementException(
                String.format("There is no recommendation with id = %d", recommendationDto.getId())));
        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public RecommendationDto update(RecommendationDto recommendationDto) {
        checkIfOfferedSkillsExist(recommendationDto);
        checkIfAcceptableTimeForRecommendation(recommendationDto);

        recommendationRepository.update(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());

        skillOfferRepository.deleteAllByRecommendationId(recommendationDto.getId());
        addSkillOffersAndGuarantee(recommendationDto);
        Recommendation updatedRecommendation = recommendationRepository.findById(recommendationDto.getId()).orElseThrow(() -> new NoSuchElementException(
                String.format("There is no recommendation with id = %d", recommendationDto.getId())));
        return recommendationMapper.toDto(updatedRecommendation);
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> recommendations = recommendationRepository.findAllByReceiverId(receiverId, Pageable.unpaged());
        return recommendationMapper.toDtoList(recommendations.getContent());
    }

    private void addSkillOffersAndGuarantee(RecommendationDto recommendationDto) {
        List<SkillOfferDto> skillOfferDtoList = recommendationDto.getSkillOffers();
//FIXME: по задаче skillOffer не обязательно. Возможно, ошибка в постановке. Или предполагается,
// в рекомендации просто пишут, что человек хороший, но скилы плохие
        if (skillOfferDtoList == null || skillOfferDtoList.isEmpty()) {
            return;
        }

        for (SkillOfferDto skillOfferDto : skillOfferDtoList) {
            skillOfferRepository.create(skillOfferDto.getSkillId(), recommendationDto.getId());
            skillRepository.findUserSkill(skillOfferDto.getSkillId(), skillOfferDto.getReceiverId())
                    .ifPresent(skill -> {
                        boolean isAuthorAlreadyGuarantor = skill.getGuarantees().stream()
                                .map(UserSkillGuarantee::getGuarantor)
                                .map(User::getId)
                                .anyMatch(skillOfferDto.getAuthorId()::equals);

                        if (!isAuthorAlreadyGuarantor) {
                            addGuaranteeToSkill(skillOfferDto, skill);
                        }
                    });
        }
    }

    private void addGuaranteeToSkill(SkillOfferDto skillOfferDto, Skill skill) {
        UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                .user(userRepository.findById(skillOfferDto.getReceiverId())
                        .orElseThrow(() -> new DataValidationException(
                                String.format("There is no user receiver with id = %d",
                                        skillOfferDto.getReceiverId()))))
                .skill(skill)
                .guarantor(userRepository.findById(skillOfferDto.getAuthorId())
                        .orElseThrow(() -> new DataValidationException(
                                String.format("There is no user author of recommendation with id = %d",
                                        skillOfferDto.getAuthorId()))))
                .build();

        skill.getGuarantees().add(guarantee);
        skillRepository.save(skill);
    }

    private void checkIfAcceptableTimeForRecommendation(RecommendationDto recommendationDto) {
        recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId())
                .ifPresent(recommendation -> {
                    if (recommendation.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(NUMBER_OF_MONTHS_AFTER_PREVIOUS_RECOMMENDATION))) {
                        throw new DataValidationException(
                                String.format("Author id = %s did recommendation for user id = %s less than %d months ago",
                                        recommendationDto.getAuthorId(),
                                        recommendationDto.getReceiverId(),
                                        NUMBER_OF_MONTHS_AFTER_PREVIOUS_RECOMMENDATION));
                    }
                });
    }

    private void checkIfOfferedSkillsExist(RecommendationDto recommendationDto) {
        List<SkillOfferDto> skillOfferDtoList = recommendationDto.getSkillOffers();
//FIXME: по задаче skillOffer не обязательно. Возможно, ошибка в постановке. Или предполагается,
// в рекомендации просто пишут, что человек хороший, но скилы плохие
        if (skillOfferDtoList == null || skillOfferDtoList.isEmpty()) {
            return;
        }

        List<Long> skillIdList = skillOfferDtoList.stream()
                .map(SkillOfferDto::getSkillId)
                .toList();

        int crossedSkillAmount = skillRepository.countExisting(skillIdList);
        if (crossedSkillAmount < skillIdList.size()) {
            throw new DataValidationException("Not all skills exist in the system");
        }
    }
}
