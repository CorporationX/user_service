package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.redis.RedisService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserRepository userRepository;
    private final RedisService redisService;

    public RecommendationDto create(RecommendationDto recommendationDto) {


        validateCooldown(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        validateSkillOffers(recommendationDto.getSkillOffers());

        Recommendation recommendation = toRecommendation(recommendationDto);

        Long createdRecommendationId = recommendationRepository.create(
                recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId(),
                recommendation.getContent()
        );

        recommendation.setId(createdRecommendationId);

        addGuarantee(recommendation);

        RecommendationDto createdRecommendationDto = recommendationRepository.findById(createdRecommendationId)
                .map(recommendationMapper::toDto)
                .orElseThrow(EntityNotFoundException::new);

        redisService.publish(recommendationMapper.toEvent(createdRecommendationDto));

        return createdRecommendationDto;
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {

        validateCooldown(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        validateSkillOffers(recommendationDto.getSkillOffers());

        Recommendation recommendation = toRecommendation(recommendationDto);

        recommendationRepository.update(
                recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId(),
                recommendation.getContent()
        );

        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());

        addGuarantee(recommendation);

        return recommendationRepository.findById(recommendation.getId())
                .map(recommendationMapper::toDto)
                .orElseThrow(EntityNotFoundException::new);
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        return recommendationMapper.toDto(
                recommendationRepository.findAllByReceiverId(receiverId, Pageable.unpaged()).getContent()
        );
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return recommendationMapper.toDto(
                recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged()).getContent()
        );
    }

    private Recommendation toRecommendation(RecommendationDto dto) {
        Recommendation recommendation = recommendationMapper.toEntity(dto);
        recommendation.setAuthor(getUser(dto.getAuthorId()));
        recommendation.setReceiver(getUser(dto.getReceiverId()));
        recommendation.setSkillOffers(mapSkillOffers(dto.getSkillOffers(), recommendation));
        return recommendation;
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User %d not found".formatted(userId)));
    }

    private void validateCooldown(Long authorId, Long receiverId) {
        recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                authorId,
                receiverId
        ).ifPresent(previous -> {
            if (previous.getCreatedAt().plusMonths(6).isAfter(LocalDateTime.now())) {
                throw new DataValidationException(
                        String.format("the author %d gives a recommendation %d earlier " +
                                "than 6 months after his last recommendation to user %d",
                            authorId,
                            previous.getId(),
                            receiverId
                        )
                );
            }
        });
    }

    private void validateSkillOffers(List<SkillOfferDto> skillOffers) {
        if (skillOffers == null || skillOffers.isEmpty()) {
            throw new DataValidationException("Skill offers list is empty");
        }

        for (SkillOfferDto offer : skillOffers) {
            Long skillId = offer.getSkillId();
            skillRepository.findById(skillId)
                    .orElseThrow(
                            () -> new DataValidationException(
                                    String.format("Skill %d does not exist", (skillId)
                                    )
                            )
                    );
        }
    }

    private List<SkillOffer> mapSkillOffers(List<SkillOfferDto> dtos, Recommendation recommendation) {
        return dtos.stream()
                .map(dto -> {
                    Skill skill = skillRepository.findById(dto.getSkillId())
                            .orElseThrow(
                                    () -> {
                                        log.info("Skill {} not found", dto.getSkillId());
                                        return new DataValidationException(
                                                String.format("Skill %d not found", dto.getSkillId())
                                        );
                                    });
                    return SkillOffer.builder()
                            .id(dto.getId())
                            .skill(skill)
                            .recommendation(recommendation)
                            .build();
                })
                .toList();
    }

    private void addGuarantee(Recommendation recommendation) {
        User receiver = recommendation.getReceiver();
        User author = recommendation.getAuthor();

        for (SkillOffer offer : recommendation.getSkillOffers()) {
            Skill skill = offer.getSkill();

            boolean receiverHasSkill = receiver.getSkills().contains(skill);
            if (receiverHasSkill) {
                UserSkillGuarantee guarantee = userSkillGuaranteeRepository
                        .findByUserAndSkill(receiver, skill)
                        .orElse(new UserSkillGuarantee());

                guarantee.setUser(receiver);
                guarantee.setSkill(skill);
                guarantee.setGuarantor(author);
                userSkillGuaranteeRepository.save(guarantee);
            } else {
                skillOfferRepository.create(skill.getId(), recommendation.getId());
            }
        }
    }
}