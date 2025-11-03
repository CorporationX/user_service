package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserImportService;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users/import")
@RestController
public class UserImportController {

    private final UserImportService userImportService;

    @Transactional
    @PostMapping(consumes = "multipart/form-data")
    public List<UserDto> uploadCsv(@RequestParam("file") MultipartFile file) throws IOException {
        return userImportService.uploadUsersCsv(file);
    }
}
