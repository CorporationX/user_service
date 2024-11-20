package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final UserService userService;
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillService skillService;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationValidator recommendationValidator;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto dto) {
        recommendationValidator.validateData(dto);
        recommendationValidator.checkDate(dto);
        Long recommendationId = recommendationRepository.create(
                dto.getAuthorId(),
                dto.getReceiverId(),
                dto.getContent()
        );
        dto.setId(recommendationId);
        handleSkillOffers(dto);
        return dto;
    }

    @Transactional
    public RecommendationDto update(RecommendationDto dto) {
        recommendationValidator.validateData(dto);
        skillOfferRepository.deleteAllByRecommendationId(dto.getId());
        recommendationRepository.update(dto.getAuthorId(),dto.getReceiverId(), dto.getContent());
        handleSkillOffers(dto);
        return dto;
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(
            long receiverId,
            int page,
            int size
    ) {
        checkPageIndexAndSize(page, size);
        List<Recommendation> recommendations = recommendationRepository.findAllByReceiverId(
                receiverId,
                PageRequest.of(page - 1, size, Sort.by("createdAt").descending())
        ).stream().toList();
        return recommendationMapper.toListDto(recommendations);
    }

    public List<RecommendationDto> getAllGivenRecommendations(
            long authorId,
            int page,
            int size
    ) {
        checkPageIndexAndSize(page, size);
        List<Recommendation> recommendations = recommendationRepository.findAllByAuthorId(
                authorId,
                PageRequest.of(page - 1, size, Sort.by("createdAt").descending())
        ).stream().toList();
        return recommendationMapper.toListDto(recommendations);
    }

    private void handleSkillOffers(RecommendationDto dto) {
        User receiver = userService.findById(dto.getReceiverId());
        User author = userService.findById(dto.getAuthorId());
        List<Long> receiverSkillsId = receiver.getSkills().stream()
                .map(Skill::getId)
                .toList();

        dto.getSkillOffers().forEach(skillOfferDto -> {
                    Long skillId = skillOfferDto.getSkillId();
                    skillOfferRepository.create(skillId, dto.getId());
                    int indexOfSkill = receiverSkillsId.indexOf(skillId);
                    boolean receiverHasThisSkill = indexOfSkill >= 0;

                    if (receiverHasThisSkill) {
                        addGuaranteeIfAbsent(author, receiver, indexOfSkill);
                    } else {
                        addGuarantee(author, receiver, skillId);
                    }
                }
        );
    }

    private void addGuaranteeIfAbsent(User author, User receiver, int indexOfSkill) {
        Skill skill = receiver.getSkills().get(indexOfSkill);
        Set<User> guarantors = skill.getGuarantees().stream()
                .map(UserSkillGuarantee::getGuarantor)
                .collect(Collectors.toSet());

        if (!guarantors.contains(author)) {
            userSkillGuaranteeRepository.save(
                    UserSkillGuarantee.builder()
                            .user(receiver)
                            .guarantor(author)
                            .skill(receiver.getSkills().get(indexOfSkill))
                            .build()
            );
        }
    }

    private void addGuarantee(User author, User receiver, long skillId) {
        Skill skill = skillService.findById(skillId);
        userSkillGuaranteeRepository.save(
                UserSkillGuarantee.builder()
                        .user(receiver)
                        .guarantor(author)
                        .skill(skill)
                        .build()
        );
    }

    private void checkPageIndexAndSize(int page, int size) {
        if (page < 1 || size < 1) {
            throw new DataValidationException("The page number and size must be greater than 1");
        }
    }
}