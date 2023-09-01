package school.faang.user_service.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationEventDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.publisher.recommendation.RecommendationEventPublisher;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    private final SkillOfferService skillOfferService;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationValidator recommendationValidator;
    private final RecommendationEventPublisher publisher;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        recommendationValidator.validateToCreate(recommendationDto);

        long newRecommendationId = recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());
        Recommendation recommendation = getRecommendation(newRecommendationId);

        if (Objects.nonNull(recommendationDto.getSkillOffers())) {
            saveSkillOffers(recommendation, recommendationDto.getSkillOffers());
        }

        publishRecommendationEvent(recommendation);
        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public RecommendationDto update(Long id, RecommendationDto recommendationDto) {
        recommendationValidator.validateToUpdate(recommendationDto);

        recommendationRepository.update(id, recommendationDto.getContent());
        Recommendation recommendation = getRecommendation(id);

        if (Objects.nonNull(recommendationDto.getSkillOffers())) {
            skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
            saveSkillOffers(recommendation, recommendationDto.getSkillOffers());
        }

        return recommendationMapper.toDto(recommendation);
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

    private void saveSkillOffers(Recommendation recommendation, List<SkillOfferDto> skillOfferDtos) {
        List<SkillOffer> skillOffers = skillOfferDtos.stream()
                .map(skillOfferDto -> skillOfferRepository.create(skillOfferDto.getSkill(), recommendation.getId()))
                .map(skillOfferService::getSkillOffer)
                .toList();

        User receiver = recommendation.getReceiver();
        User guarantor = recommendation.getAuthor();

        List<Skill> newSkills = getNewSkillsFromSkillOffers(skillOffers, receiver);
        newSkills.forEach(skill -> addNewSkillToReceiver(recommendation, skill));

        List<Skill> notGuaranteedSkills = getNotGuaranteedSkillsFromSkillOffers(skillOffers, receiver, guarantor);
        notGuaranteedSkills.forEach(skill -> addGuarantorToSkill(recommendation, skill));
    }

    private List<Skill> getNewSkillsFromSkillOffers(List<SkillOffer> skillOffers, User receiver) {
        return skillOffers.stream()
                .map(SkillOffer::getSkill)
                .filter(skill -> !receiver.getSkills().contains(skill))
                .toList();
    }

    private List<Skill> getNotGuaranteedSkillsFromSkillOffers(List<SkillOffer> skillOffers, User receiver, User guarantor) {
        return skillOffers.stream()
                .map(SkillOffer::getSkill)
                .filter(skill -> receiver.getSkills().contains(skill))
                .filter(skill -> !skill.getGuarantees().stream()
                        .map(UserSkillGuarantee::getGuarantor)
                        .toList().contains(guarantor))
                .toList();
    }

    private void addNewSkillToReceiver(Recommendation recommendation, Skill skill) {
        addGuarantorToSkill(recommendation, skill);
        recommendation.getReceiver().getSkills().add(skill);
        skill.getUsers().add(recommendation.getReceiver());
    }

    private void addGuarantorToSkill(Recommendation recommendationEntity, Skill skill) {
        UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                .user(recommendationEntity.getReceiver())
                .guarantor(recommendationEntity.getAuthor())
                .skill(skill)
                .build();
        userSkillGuarantee = userSkillGuaranteeRepository.save(userSkillGuarantee);
        skill.getGuarantees().add(userSkillGuarantee);
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
