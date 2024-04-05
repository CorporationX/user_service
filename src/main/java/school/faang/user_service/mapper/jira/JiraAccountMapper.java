package school.faang.user_service.mapper.jira;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.jira.JiraAccountDto;
import school.faang.user_service.entity.jira.JiraAccount;

@Mapper
public interface JiraAccountMapper {

    @Mapping(source = "user.id", target = "userId")
    JiraAccountDto toDto(JiraAccount jiraAccount);

    @Mapping(source = "userId", target = "user.id")
    JiraAccount toEntity(JiraAccountDto jiraAccountDto);
}
