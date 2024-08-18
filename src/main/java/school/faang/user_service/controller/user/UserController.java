package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.controller.ApiPath;
import school.faang.user_service.service.csv.CsvUserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.USERS)
public class UserController {
    private final CsvUserService csvUserService;
    @PostMapping(value = "/parseUser")
    public void getStudentsParsing(@RequestBody MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            log.error("multipartFile isEmpty");
            throw new IllegalArgumentException("multipartFile isEmpty");
        } else {
            csvUserService.getStudentsParsing(multipartFile);
        }
    }
}