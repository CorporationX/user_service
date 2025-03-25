package school.faang.user_service.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferService skillOfferService;
    private final SkillService skillService;
    private final UserSkillGuaranteeService userSkillGuaranteeService;
    private final RecommendationMapper recommendationMapper;
    private final SkillOfferMapper skillOfferMapper;

    @Value("${recommendation-min-distance-months}")
    private int recommendationMinDistanceMonths;

    @Value("${page-size}")
    private int pageSize;

    @Transactional
    public RecommendationDto create(@NotNull RecommendationDto recommendationDto) {
        validateRecommendation(recommendationDto);

        recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());

        var createdRecommendation = recommendationRepository.findByAuthorIdAndReceiverId(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId());

        if (createdRecommendation.isEmpty()) {
            throw new DataValidationException(
                    "Recommendation is not created (author id: %d, receiver id: %d)".formatted(
                            recommendationDto.getAuthorId(),
                            recommendationDto.getReceiverId()));
        }

        var createdRecommendationDto = recommendationMapper.toDto(createdRecommendation.get());
        createdRecommendationDto.setSkillOffers(recommendationDto.getSkillOffers());

        if (recommendationDto.getSkillOffers() != null) {
            var receiverSkills = skillService.findSkillsByUserId(createdRecommendationDto.getReceiverId());
            recommendationDto.getSkillOffers()
                    .forEach(dto -> saveSkillOffer(createdRecommendationDto, dto, receiverSkills));
        }

        return createdRecommendationDto;
    }

    @Transactional
    public RecommendationDto update(@NotNull RecommendationDto recommendationDto) {
        validateRecommendation(recommendationDto);

        var updatedRecommendation = updatedRecommendation(recommendationDto);

        skillOfferService.deleteSkillOffersByRecommendationId(updatedRecommendation.getId());

        var updatedRecommendationDto = recommendationMapper.toDto(updatedRecommendation);

        if (recommendationDto.getSkillOffers() != null) {
            var savedSkillOfferDtos = saveSkillOffers(
                    recommendationDto.getReceiverId(),
                    recommendationDto.getSkillOffers(),
                    updatedRecommendationDto);

            updatedRecommendationDto.setSkillOffers(savedSkillOfferDtos);
        }

        return updatedRecommendationDto;
    }

    private Recommendation updatedRecommendation(RecommendationDto recommendationDto) {
        recommendationRepository.update(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());

        var updatedRecommendation = recommendationRepository.findByAuthorIdAndReceiverId(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId());
        if (updatedRecommendation.isEmpty()) {
            throw new DataValidationException(
                    "Source recommendationDto is not found (author id: %d, receiver id: %d)".formatted(
                            recommendationDto.getAuthorId(),
                            recommendationDto.getReceiverId()));
        }

        return updatedRecommendation.get();
    }

    public void deleteRecommendationById(long recommendationId) {
        recommendationRepository.deleteById(recommendationId);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        return getRecommendationDtos(
                pageRequest -> recommendationRepository.findAllByReceiverId(receiverId, pageRequest));
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return getRecommendationDtos(
                pageRequest -> recommendationRepository.findAllByAuthorId(authorId, pageRequest));
    }

    private List<SkillOfferDto> saveSkillOffers(
            long receiverId,
            List<SkillOfferDto> skillOffers,
            RecommendationDto updatedRecommendationDto) {
        var receiverSkills = skillService.findSkillsByUserId(receiverId);

        return skillOffers.stream()
                .map(dto -> saveSkillOffer(updatedRecommendationDto, dto, receiverSkills))
                .toList();
    }

    private List<RecommendationDto> getRecommendationDtos(Function<Pageable, Page<Recommendation>> pageFetcher) {
        List<RecommendationDto> recommendationDtos = new ArrayList<>();

        var page = pageFetcher.apply(getInitialPageRequest());
        if (page.isEmpty()) {
            return recommendationDtos;
        }

        recommendationDtos.addAll(page.map(this::toRecommendationDto).toList());
        IntStream.range(1, page.getTotalPages())
                .forEach(pageIndex -> processRecommendationsPage(pageIndex, recommendationDtos, pageFetcher));

        return recommendationDtos;
    }

    private void processRecommendationsPage(
            int pageIndex,
            List<RecommendationDto> recommendationDtos,
            Function<Pageable, Page<Recommendation>> pageFetcher) {
        var pageRequest = PageRequest.of(pageIndex, pageSize);
        var page = pageFetcher.apply(pageRequest);
        recommendationDtos.addAll(page.map(this::toRecommendationDto).toList());
    }

    @NotNull
    private RecommendationDto toRecommendationDto(Recommendation recommendation) {
        var recommendationDto = recommendationMapper.toDto(recommendation);
        var skillOfferDtos = skillOfferService.getSkillRequestsByRecommendationId(recommendation.getId())
                .stream()
                .map(skillOfferMapper::toDto)
                .toList();
        recommendationDto.setSkillOffers(skillOfferDtos);

        return recommendationDto;
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        var lastAuthorRecommendation = recommendationRepository.findLastRecommendationByAuthorId(
                recommendation.getAuthorId());

        if (lastAuthorRecommendation.isPresent()
                && LocalDateTime.now()
                .minusMonths(recommendationMinDistanceMonths)
                .isBefore(lastAuthorRecommendation.get().getUpdatedAt())) {
            throw new DataValidationException(
                    "Less than %d months have passed since the last recommendation".formatted(
                            recommendationMinDistanceMonths));
        }

        validateSkillOffers(recommendation);
    }

    private void validateSkillOffers(RecommendationDto recommendation) {
        if (recommendation.getSkillOffers() == null) {
            return;
        }

        var requestedSkillIds = recommendation.getSkillOffers()
                .stream()
                .map(SkillOfferDto::skillId)
                .collect(Collectors.toSet());

        var missingSkillIds = requestedSkillIds.stream()
                .filter(skillId -> !skillService.doesSkillExists(skillId))
                .toList();

        if (!missingSkillIds.isEmpty()) {
            var missingSkills = missingSkillIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            throw new DataValidationException("Skills %s are not registered".formatted(missingSkills));
        }
    }

    private SkillOfferDto saveSkillOffer(
            RecommendationDto recommendationDto,
            SkillOfferDto skillOfferDto,
            List<Skill> receiverSkills) {
        var existedSkill = receiverSkills.stream()
                .filter(x -> x.getId() == skillOfferDto.skillId())
                .findFirst();
        if (existedSkill.isPresent()
                && userSkillGuaranteeService.findUserSkillGuaranteeByGuarantorId(
                recommendationDto.getAuthorId()).isEmpty()) {
            userSkillGuaranteeService.createUserSkillGuarantee(
                    recommendationDto.getReceiverId(),
                    skillOfferDto.skillId(),
                    recommendationDto.getAuthorId());
        }

        skillOfferService.createSkillOffer(skillOfferDto.skillId(), recommendationDto.getId());
        var skillOffer = skillOfferService.findSkillOfferBySkillAndRecommendationIds(
                skillOfferDto.skillId(),
                recommendationDto.getId());
        if (skillOffer.isEmpty()) {
            throw new DataValidationException(
                    "Skill offer is not created (skill id: %d, recommendationDto id: %d)".formatted(
                            skillOfferDto.skillId(),
                            recommendationDto.getId()));
        }

        return skillOfferMapper.toDto(skillOffer.get());
    }

    private Pageable getInitialPageRequest() {
        return PageRequest.of(0, pageSize);
    }

    public Recommendation findRecommendationById(long recommendationId) {
        return recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new DataRetrievalFailureException(
                        "Recommendation with id %d is not found".formatted(recommendationId)));
    }
}
