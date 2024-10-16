package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import school.faang.user_service.model.dto.ProjectDto;

@FeignClient(value = "project-service", url = "${project-service.host}:${project-service.port}")
public interface ProjectServiceClient {

    @GetMapping("/project/{projectId}")
    ProjectDto getProject(@PathVariable("projectId")  long projectId);
}
