package school.faang.user_service.controller.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.json.student.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.userDto.UserDto;
import school.faang.user_service.dto.userDto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.UserPremiumService;
import school.faang.user_service.service.csv.CsvToObjectConverter;
import school.faang.user_service.service.csv.CsvUserService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {


    private final UserPremiumService userPremiumService;
    private CsvToObjectConverter csvToObjectConverter;
    private final CsvUserService csvUserService;
    @GetMapping(value = "/premium")
    public ResponseEntity<List<UserDto>> getListPremiumUsers(@RequestBody UserFilterDto userFilterDto) {
        if (userFilterDto == null) {
            log.error("userFilterDto ничего не содержит");
            throw new IllegalArgumentException("userFilterDto ничего не содержит");
        }
        return ResponseEntity.status(HttpStatus.OK).body(userPremiumService.getPremiumUsers(userFilterDto));
    }

    @GetMapping(value = "/parseUser")
    public void getStudentsParsing(@RequestBody MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            log.error("multipartFile isEmpty");
            throw new IllegalArgumentException("multipartFile isEmpty");
        }else{
            csvUserService.getStudentsParsing(multipartFile);
        }
    }
}
