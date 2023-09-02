package school.faang.user_service.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;

import school.faang.user_service.dto.recommendation.RecommendationEventDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.dto.skill.UserSkillGuaranteeDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.mapper.skill.UserSkillGuaranteeMapper;
import school.faang.user_service.publisher.recommendation.RecommendationEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    private final SkillOfferService skillOfferService;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final RecommendationMapper recommendationMapper;
    private final UserSkillGuaranteeMapper userSkillGuaranteeMapper;
    private final RecommendationValidator recommendationValidator;
    private final RecommendationEventPublisher publisher;


    @Transactional
    public RecommendationDto create(RecommendationDto recommendation) {
        recommendationValidator.validateToCreate(recommendation);

        long newRecommendationId = recommendationRepository.create(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent()
        );

        Recommendation recommendationEntity = getRecommendation(newRecommendationId);

        if (Objects.nonNull(recommendation.getSkillOffers())) {
            saveSkillOffers(recommendationEntity, recommendation.getSkillOffers());
        }

        publishRecommendationEvent(recommendationEntity);

        return recommendationMapper.toDto(recommendationEntity);
    }

    @Transactional
    public RecommendationDto update(RecommendationDto recommendationUpdate) {
        recommendationValidator.validateToUpdate(recommendationUpdate);

        Recommendation recommendationEntity = recommendationRepository.update(
                recommendationUpdate.getAuthorId(),
                recommendationUpdate.getReceiverId(),
                recommendationUpdate.getContent()
        );

        skillOfferRepository.deleteAllByRecommendationId(recommendationEntity.getId());
        saveSkillOffers(recommendationEntity, recommendationUpdate.getSkillOffers());
        return recommendationMapper.toDto(recommendationEntity);
    }

    @Transactional
    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        List<Recommendation> recommendations = recommendationRepository.findAllByReceiverId(receiverId)
                .orElseGet(Collections::emptyList);

        return recommendationMapper.toRecommendationDtos(recommendations);
    }

    @Transactional(readOnly = true)
    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        List<Recommendation> recommendations = recommendationRepository.findAllByAuthorId(authorId)
                .orElseGet(Collections::emptyList);

        return recommendationMapper.toRecommendationDtos(recommendations);
    }

    private void saveSkillOffers(Recommendation recommendationEntity, List<SkillOfferDto> skillOffers) {
        for (SkillOfferDto skillOfferDto : skillOffers) {
            long newSkillOfferId = skillOfferRepository.create(
                    skillOfferDto.getSkill(),
                    recommendationEntity.getId()
            );
            recommendationEntity.addSkillOffer(skillOfferService.getSkillOffer(newSkillOfferId));

            List<SkillDto> skillsInUserDtos = getUserSkillsAndConvertToDtos(recommendationEntity.getAuthor().getId());

            UserSkillGuaranteeDto userSkillGuaranteeDto = createUserSkillGuaranteeDto(recommendationEntity);

            for (SkillOffer skillOffer : recommendationEntity.getSkillOffers()) {
                skillsInUserDtos.stream()
                        .filter(userSkill -> userSkill.getId().equals(skillOffer.getSkill().getId()))
                        .forEach(skill -> provideGuarantee(
                                skill,
                                recommendationEntity.getAuthor().getId(),
                                userSkillGuaranteeDto));
            }
        }
    }

    private List<SkillDto> getUserSkillsAndConvertToDtos(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        List<SkillDto> skillDtos = new ArrayList<>();

        if (!skills.isEmpty()) {
            skillDtos = skills.stream()
                    .map(skillMapper::toDto)
                    .collect(Collectors.toList());
        }
        return skillDtos;
    }

    private UserSkillGuaranteeDto createUserSkillGuaranteeDto(Recommendation recommendationEntity) {
        return UserSkillGuaranteeDto.builder()
                .userId(recommendationEntity.getReceiver().getId())
                .guarantorId(recommendationEntity.getAuthor().getId())
                .build();
    }

    private void provideGuarantee(SkillDto skill, long authorId, UserSkillGuaranteeDto userSkillGuaranteeDto) {
        if (skill.getGuarantees()
                .stream()
                .noneMatch(guarantee -> guarantee.getGuarantorId() == authorId)) {
            userSkillGuaranteeDto.setSkillId(skill.getId());
            skill.getGuarantees().add(userSkillGuaranteeDto);
            userSkillGuaranteeRepository.save(userSkillGuaranteeMapper.toEntity(userSkillGuaranteeDto));
        }
    }

    private void publishRecommendationEvent(Recommendation recommendation) {
        RecommendationEventDto recommendationEvent = RecommendationEventDto.builder()
                .id(recommendation.getId())
                .authorId(recommendation.getAuthor().getId())
                .recipientId(recommendation.getReceiver().getId())
                .date(LocalDateTime.now())
                .build();
        publisher.publish(recommendationEvent);
        log.info("Published new recommendation event: {}", recommendationEvent);
    }

    private Recommendation getRecommendation(long id) {
        return recommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation not found"));
    }
}
