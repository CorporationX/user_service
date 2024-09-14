package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.mapper.recommendation.SkillOfferMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validation.recommendation.RecommendationValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationValidator recommendationValidator;
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferMapper skillOfferMapper;

    @Transactional
    public RecommendationDto createRecommendation(RecommendationDto recommendationDto) {
        User author = getUserById(recommendationDto.getAuthorId());
        User receiver = getUserById(recommendationDto.getReceiverId());
        recommendationValidator.recommendationIntervalValidation(author, receiver);


        List<SkillOfferDto> skillOffersDto = Optional.ofNullable(recommendationDto.getSkillOffers())
                .orElseGet(ArrayList::new);
        List<Long> skillOfferDtoIds = getSkillOfferDtoIds(skillOffersDto);
        List<Long> allSkillsIds = getAllSkillsIds(skillOfferDtoIds);
        recommendationValidator.skillOffersValidation(skillOfferDtoIds, allSkillsIds);
//        saveSkillOffers(skillOffersDto);
        String content = recommendationDto.getContent();
        skillOfferDtoIds.forEach(aLong -> recommendationRepository.create2(author.getId(), receiver.getId(), content, aLong));


//        updateSkillsGuarantees(skillOffersDto, author, receiver);
//        saveNewSkills(author, receiver, skillOffersDto);

        return recommendationDto;
    }


    private void updateSkillsGuarantees(List<SkillOfferDto> skillOffersDto, User author, User receiver) {
        List<Skill> allSkillsByUserId = skillRepository.findAllByUserId(receiver.getId());
        List<Long> skillOfferIds = skillOffersDto.stream()
                .map(SkillOfferDto::getSkillId)
                .toList();

        allSkillsByUserId.stream()
                .filter(skill -> skillOfferIds.contains(skill.getId()))
                .forEach(getSkillConsumer(author, receiver));

        skillOffersDto.removeIf(offer -> skillOfferIds.contains(offer.getSkillId()));
    }

    @NotNull
    private Consumer<Skill> getSkillConsumer(User author, User receiver) {
        return matchedSkill -> {
            if (!recommendationValidator.checkIsGuarantor(matchedSkill, author.getId())) {
                addGuarantor(matchedSkill, author, receiver);
            }
        };
    }

    private void addGuarantor(Skill skill, User author, User receiver) {
        UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                .user(receiver)
                .skill(skill)
                .guarantor(author)
                .build();

        List<UserSkillGuarantee> guarantees = Optional.ofNullable(skill.getGuarantees())
                .orElseGet(ArrayList::new);

        guarantees.add(guarantee);
    }

    public void saveNewSkills(User author, User receiver, List<SkillOfferDto> skillOffersDto) {
        List<Skill> newSkills = skillRepository.findAllById(
                skillOffersDto.stream()
                        .map(SkillOfferDto::getSkillId)
                        .toList()
        );
        newSkills.forEach(skill -> addGuarantor(skill, author, receiver));

        receiver.getSkills().addAll(newSkills);
        userRepository.save(receiver);
    }

    public void saveSkillOffers(List<SkillOfferDto> skillOffersDto) {
        skillOffersDto.forEach(skillOfferDto -> skillOfferRepository.create(
                skillOfferDto.getSkillId(),
                skillOfferDto.getRecommendationId())
        );
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new DataValidationException("User with ID: " + userId + " not found")
        );
    }

    @NotNull
    private List<Long> getAllSkillsIds(List<Long> skillOfferDtoIds) {
        return skillRepository.findAllById(skillOfferDtoIds)
                .stream()
                .map(Skill::getId)
                .toList();
    }

    @NotNull
    private List<Long> getSkillOfferDtoIds(List<SkillOfferDto> skillOfferDtoList) {
        return skillOfferDtoList.stream()
                .map(SkillOfferDto::getSkillId)
                .toList();
    }
}
