package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import school.faang.user_service.dto.project.ProjectDto;

@FeignClient(name = "project-service", url = "${project-service.host}:${project-service.port}/api/v1/projects")
public interface ProjectServiceClient {

    @GetMapping("/{id}")
    ProjectDto getProjectById(@PathVariable("id") long projectId);
}
