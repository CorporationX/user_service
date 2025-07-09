package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.entity.user.UserSkillGuarantee;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserSkillGuaranteeMapper {

    @Mapping(target = "user", source = "recommendation.receiver")
    @Mapping(target = "guarantor", source = "recommendation.author")
    UserSkillGuarantee toUserSkillGuarantee(SkillOffer offer);

    List<UserSkillGuarantee> toUserSkillGuarantees(List<SkillOffer> offers);
}
