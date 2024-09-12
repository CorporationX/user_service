package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validation.recommendation.RecommendationValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Transactional
    public RecommendationDto createRecommendation(RecommendationDto recommendationDto) {
        User author = userService.findUserById(recommendationDto.getAuthorId());
        User receiver = userService.findUserById(recommendationDto.getReceiverId());

        recommendationValidator.recommendationIntervalValidation(author, receiver);
        recommendationValidator.skillOffersValidation(recommendationDto);

        Long authorId = author.getId();
        Long receiverId = receiver.getId();
        String content = recommendationDto.getContent();
        recommendationRepository.create(authorId, receiverId, content);

//        processSkillsAndGuarantees(author, receiver, recommendationDto);

        return recommendationDto;
    }


    @Transactional
    public void processSkillsAndGuarantees(User author, User receiver, RecommendationDto recommendationDto) {
        //TODO Murzin34 Вместо прямого вызова skillRepository задействовать SkillService.
        List<Skill> skillList = skillRepository.findAllByUserId(receiver.getId());
        List<SkillOfferDto> skillOffersDto = recommendationDto.getSkillOffers();

        if (skillOffersDto == null) {
            skillOffersDto = new ArrayList<>();
        }

        updateSkillsGuarantees(skillOffersDto, skillList, author, receiver);

        saveNewSkills(author, receiver, skillOffersDto);

        saveSkillOffers(skillOffersDto);
    }

    private void updateSkillsGuarantees(List<SkillOfferDto> skillOffersDto, List<Skill> receiverSkills, User author, User receiver) {
        List<Long> skillOfferIds = skillOffersDto.stream()
                .map(SkillOfferDto::getSkillId)
                .toList();

        receiverSkills.stream()
                .filter(skill -> skillOfferIds.contains(skill.getId()))
                .forEach(matchedSkill -> {
                    if (!recommendationValidator.checkIsGuarantor(matchedSkill, author.getId())) {
                        addGuarantor(matchedSkill, author, receiver);
                    }
                });

        skillOffersDto.removeIf(offer -> skillOfferIds.contains(offer.getSkillId()));
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
}
