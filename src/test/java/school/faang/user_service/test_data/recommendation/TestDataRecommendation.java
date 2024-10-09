package school.faang.user_service.test_data.recommendation;

import lombok.Getter;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TestDataRecommendation {
    private Country getCountry() {
        return Country.builder()
                .id(1L)
                .title("Country")
                .build();
    }

    public Skill getSkill1() {
        return Skill.builder()
                .id(1L)
                .title("skill1")
                .build();
    }

    public Skill getSkill2() {
        return Skill.builder()
                .id(2L)
                .title("skill2")
                .build();
    }

    public SkillOfferDto getSkillOfferDto1() {
        return SkillOfferDto.builder()
                .skillId(getSkill1().getId())
                .build();
    }

    public SkillOfferDto getSkillOfferDto2() {
        return SkillOfferDto.builder()
                .skillId(getSkill2().getId())
                .build();
    }

    public User getAuthor() {
        return User.builder()
                .id(1L)
                .username("Author1")
                .email("Author1@mail.com")
                .password("123456789")
                .active(true)
                .country(getCountry())
                .skills(new ArrayList<>(List.of(getSkill1(), getSkill2())))
                .build();
    }

    public User getReceiver() {
        return User.builder()
                .id(2L)
                .username("Receiver1")
                .email("Receiver1@mail.com")
                .password("123456789")
                .active(true)
                .country(getCountry())
                .skills(new ArrayList<>(List.of(getSkill1())))
                .build();
    }

    public Recommendation getRecommendation() {
        return Recommendation.builder()
                .id(1L)
                .author(getAuthor())
                .receiver(getReceiver())
                .content("testContent")
                .build();
    }

    public RecommendationDto getRecommendationDto() {
        return RecommendationDto.builder()
                .id(1L)
                .authorId(1L)
                .receiverId(2L)
                .content("testContent")
                .skillOffers(new ArrayList<>(List.of(getSkillOfferDto1(), getSkillOfferDto2())))
                .build();
    }


    public UserSkillGuarantee getUserSkillGuarantee() {
        return UserSkillGuarantee.builder()
                .id(1L)
                .user(getAuthor())
                .skill(getSkill1())
                .build();
    }
}
