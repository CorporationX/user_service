package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.dto.event.RecommendationEventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.publisher.RecommendationRequestedEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationRequestedEventPublisher recommendationRequestedEventPublisher;
    private static final int FILTER_MONTH = 6;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        validationData(recommendationDto);
        Recommendation recommendation = fillEntityRecommendation(recommendationDto);

        Long recommendationId = recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());
        saveSkillOffers(recommendationDto, recommendationId);
        sendNotification(recommendationId, recommendationDto);

        return recommendationMapper.toDto(recommendation);
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {
        validationData(recommendationDto);

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        saveSkillOffers(recommendationDto, recommendationDto.getId());

        recommendationRepository.save(recommendation);
        return recommendationMapper.toDto(recommendation);
    }

    public void delete(long id) {
        validationBeforeDelete(id);
        recommendationRepository.deleteById(id);
    }

    public Page<RecommendationDto> getAllUserRecommendations(Long receiverId, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        Page<Recommendation> receiverRecommendation = recommendationRepository.findAllByReceiverId(receiverId, pageable);
        return receiverRecommendation.map(recommendationMapper::toDto);
    }

    public Page<RecommendationDto> getAllGivenRecommendations(long authorId, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        Page<Recommendation> authorRecommendation = recommendationRepository.findAllByAuthorId(authorId, pageable);
        return authorRecommendation.map(recommendationMapper::toDto);
    }

    private void saveSkillOffers(RecommendationDto recommendation, Long recommendationId) {
        long authorId = recommendation.getAuthorId();
        long receiverId = recommendation.getAuthorId();
        User user = getUserById(receiverId);
        User guarantee = getUserById(authorId);

        List<SkillOfferDto> skillOffers = recommendation.getSkillOffers();
        List<Long> userSkills = getUserSkillsById(receiverId);

        skillOffers.forEach(skillOffer -> {
            long skillId = skillOffer.getSkillId();
            if (userSkills.contains(skillId) && !userSkillGuaranteeRepository.existsById(authorId)) {

                addNewGuarantee(user, guarantee, skillId);
            } else {
                skillOfferRepository.create(skillId, recommendationId);
            }
        });
    }

    private void validationData(RecommendationDto recommendation) {
        LocalDate currentDate = LocalDate.now();
        boolean skillInSystem = recommendation.getSkillOffers().stream()
                .allMatch(skill -> skillRepository.existsById(skill.getSkillId()));
        if (!skillInSystem) {
            throw new DataValidationException("Skill was not found");
        }
        recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendation.getAuthorId(), recommendation.getReceiverId()).ifPresent(lastRecommendation -> {
            if (ChronoUnit.MONTHS.between(lastRecommendation.getCreatedAt().toLocalDate(), currentDate) <= FILTER_MONTH) {
                throw new DataValidationException("It has been less than 6 months since the last recommendation");
            }
        });
    }

    private void validationBeforeDelete(long id) {
        if (recommendationRepository.findById(id).isEmpty()) {
            throw new DataValidationException("Recommendation not found");
        }
    }

    private void addNewGuarantee(User user, User guarantee, Long skillId) {
        Skill skill = skillRepository.findById(skillId).orElseThrow(() ->
                new DataValidationException("Skill not found"));
        UserSkillGuarantee guaranteeSkill = new UserSkillGuarantee();

        guaranteeSkill.setSkill(skill);
        guaranteeSkill.setUser(user);
        guaranteeSkill.setGuarantor(guarantee);

        userSkillGuaranteeRepository.save(guaranteeSkill);
    }

    private List<Long> getUserSkillsById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Skills not found"))
                .getSkills().stream()
                .map(Skill::getId)
                .toList();
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("User not found"));
    }

    private Recommendation fillEntityRecommendation(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);

        recommendation.setReceiver(userRepository.findById(recommendationDto.getReceiverId())
                .orElseThrow(() -> new DataValidationException("User not found")));
        recommendation.setAuthor(userRepository.findById(recommendationDto.getAuthorId())
                .orElseThrow(() -> new DataValidationException(("Author not found"))));

        return recommendation;
    }

    private void sendNotification(Long idRecommendation, RecommendationDto recommendationDto) {
        RecommendationEventDto recommendationEventDto = RecommendationEventDto.builder()
                .id(idRecommendation)
                .authorId(recommendationDto.getAuthorId())
                .receiverId(recommendationDto.getReceiverId())
                .createdAt(recommendationDto.getCreateAt())
                .build();

        recommendationRequestedEventPublisher.convertAndSend(recommendationEventDto);
    }
}
