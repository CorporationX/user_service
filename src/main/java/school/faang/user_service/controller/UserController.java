package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.pojo.student.Person;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.util.uploader.PersonUploader;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Validated
@Slf4j
public class UserController {
    private final UserService userService;
    private final PersonUploader personUploader;

    @PostMapping("/students/upload")
    public void uploadStudents(@RequestParam("students") MultipartFile students) throws IOException {
        List<Person> studentList = personUploader.upload(students);
        userService.saveStudents(studentList);
    }
}
