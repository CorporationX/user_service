package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.ErrorMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mydto.UserDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.skill.UserSkillGuaranteeDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.invalidFieldException.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.mymappers.User1Mapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillService skillService;
    private final RecommendationRequestService recommendationRequestService;
    private final UserService userService;
    private final User1Mapper user1Mapper;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        validatePreviousRecommendation(recommendationDto);
        checkSkills(recommendationDto);
        checkRecommendations(recommendationDto);

        Long recommendationId = recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());

        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                        .orElseThrow(()-> new EntityNotFoundException("Recommendation not found"));

        saveSkillOffers(recommendation, recommendationDto.getSkillOffers());

        recommendationDto.getSkillOffers()
                .forEach(sod -> skillOfferRepository.create(sod.getSkillId(), sod.getRecommendationId()));

        User receiver = getUser(recommendationDto.getReceiverId());
        User author = getUser(recommendationDto.getAuthorId());

        checkGuarantee(recommendationDto);

        recommendation.setReceiver(receiver);
        recommendation.setAuthor(author);

        recommendationRepository.save(recommendation); //12th point from ticket BC-3502
        return recommendationMapper.toDto(recommendation);
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {
        validatePreviousRecommendation(recommendationDto);
        checkSkills(recommendationDto);
        checkRecommendations(recommendationDto);

        Recommendation updatedRecommendation = recommendationRepository
                .update(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());

        recommendationDto.getSkillOffers()
                .forEach(sod -> skillOfferRepository.deleteAllByRecommendationId(sod.getRecommendationId()));

        recommendationDto.getSkillOffers()
                .forEach(sod -> skillOfferRepository.create(sod.getSkillId(), sod.getRecommendationId()));

        saveSkillOffers(updatedRecommendation, recommendationDto.getSkillOffers());

        User receiver = getUser(recommendationDto.getReceiverId());
        User author = getUser(recommendationDto.getAuthorId());

        recommendationDto.getSkillOffers()
                .forEach(skillOffer -> {
                    Optional<Skill> optionalSkill = skillRepository.findUserSkill(skillOffer.getSkillId(), receiver.getId());
                    if (optionalSkill.isPresent()) {
                        UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                                .skill(optionalSkill.get())
                                .user(receiver)
                                .guarantor(author)
                                .build();
                        userSkillGuaranteeRepository.save(userSkillGuarantee);
                    }
                });

        recommendationRepository.save(updatedRecommendation);
        return recommendationMapper.toDto(updatedRecommendation);
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> recommendations = recommendationRepository
                .findAllByReceiverId(receiverId, Pageable.unpaged());
        if (recommendations == null) {
            return new ArrayList<>();
        }
        return recommendations.getContent()
                .stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        Page<Recommendation> recommendations = recommendationRepository
                .findAllByReceiverId(authorId, Pageable.unpaged());
        if (recommendations == null) {
            return new ArrayList<>();
        }
        return recommendations.getContent()
                .stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    private void validatePreviousRecommendation(RecommendationDto recommendationDto) {
        var recommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc
                        (recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        if (recommendation.isEmpty()) {
            return;
        }
        LocalDateTime recommendationCreate = recommendation.get().getCreatedAt();
        if (!recommendationCreate.isAfter(LocalDateTime.now().minusMonths(6))) {
            throw new DataValidationException("Recommendation duration has not expired");
        }
    }

    private void checkSkills(RecommendationDto recommendationDto) {
        Set<Long> skillIds = recommendationDto.getSkillOffers()
                .stream()
                .map(SkillOfferDto::getSkillId)
                .collect(Collectors.toSet());
        var skills = skillRepository.findAllById(skillIds);
        if (skillIds.size() != skills.size()) {
            throw new DataValidationException("Some skills do not exist");
        }
    }

    private void checkRecommendations(RecommendationDto recommendationDto) {
        Set<Long> recommendationIds = recommendationDto.getSkillOffers()
                .stream()
                .map(SkillOfferDto::getRecommendationId)
                .collect(Collectors.toSet());
        var recommendations = recommendationRepository.findAllById(recommendationIds);
        if (recommendationIds.size() != recommendations.size()) {
            throw new DataValidationException("Some recommendations do not exist");
        }
    }

    private void saveSkillOffers(Recommendation recommendation, List<SkillOfferDto> skillOffers) {
        skillOffers.forEach(offer -> {
            long OfferId = skillOfferRepository.create(offer.getSkillId(), recommendation.getId());
            recommendation.addSkillOffer(skillOfferRepository.findById(OfferId).orElse(null));
//            provideGuaranteesIfSkillExists(recommendation);
        });
    }

    private void checkGuarantee(RecommendationDto recommendationDto) {
        User receiver = getUser(recommendationDto.getReceiverId());
        User author = getUser(recommendationDto.getAuthorId());
        recommendationDto.getSkillOffers()
                .forEach(skillOffer -> {
                    Optional<Skill> optionalSkill = skillRepository.findUserSkill(skillOffer.getSkillId(), receiver.getId());
                    if (optionalSkill.isPresent()) {
                        UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                                .skill(optionalSkill.get())
                                .user(receiver)
                                .guarantor(author)
                                .build();
                        userSkillGuaranteeRepository.save(userSkillGuarantee);
                    }
                });
    }
    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found"));
    }
}


