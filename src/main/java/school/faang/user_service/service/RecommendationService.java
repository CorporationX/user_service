package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.validator.SkillValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;

    private final SkillRepository skillRepository;

    private final UserRepository userRepository;

//    private final SkillValidator skillValidator;

    private final RecommendationMapper recommendationMapper;


    public RecommendationDto create(RecommendationDto recommendationDto) {
        validate(recommendationDto.getSkillOffers());
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        User author = userRepository.findById(recommendationDto.getAuthorId()).orElseThrow();
        User receiver = userRepository.findById(recommendationDto.getReceiverId()).orElseThrow();
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        List<SkillOffer> skillOffers = addGuarantee(recommendation);
        recommendation.setSkillOffers(skillOffers);
        return recommendationMapper.toDto(recommendationRepository.save(recommendation));
    }

//    private List<SkillOffer> addGuarantee(Recommendation recommendation){
//        List<Skill> userSkills = skillRepository.findAllByUserId(recommendation.getReceiver().getId());
//        List<SkillOffer> skillOffers = recommendation.getSkillOffers();
//        for (SkillOffer o: skillOffers) {
//            Skill skill = skillRepository.findById(o.getId()).orElseThrow();
//            UserSkillGuarantee guarantee = new UserSkillGuarantee();
//            guarantee.setSkill(skill);
//            System.out.println(recommendation.getAuthor());
//            guarantee.setGuarantor(recommendation.getAuthor());
//            guarantee.setUser(recommendation.getReceiver());
//            if(userSkills.contains(skill)){
//                if(!skill.getGuarantees().contains(guarantee)){
//                    skill.getGuarantees().add(guarantee);
//                }
//            } else {
//              skill.getGuarantees().add(guarantee);
//            }
//            o.setSkill(skill);
//        };
//        return skillOffers;
//    }



    private void validate(List<SkillOfferDto> skills) {
        if (skills !=null && !skills.isEmpty()) {
            List<Long> skillIds = skills.stream()
                    .map(SkillOfferDto::getId)
                    .distinct()
                    .collect(Collectors.toList());
            if (skills.size() != skillIds.size()) {
                throw new DataValidationException("list of skills contains not unique skills, please, check this");
            }
            if (!skillIds.isEmpty()) {
                if (skillIds.size() != skillRepository.countExisting(skillIds)) {
                    throw new DataValidationException("list of skills contains not valid skills, please, check this");
                }
            }
        }
    }
}
