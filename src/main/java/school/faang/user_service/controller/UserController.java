package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.pojo.Person;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Validated
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/students/upload")
    public void uploadStudents(@RequestParam("students") MultipartFile students) throws IOException {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(Person.class).withHeader().withColumnReordering(true);

        MappingIterator<Person> persons = mapper.readerFor(Person.class).with(schema).readValues(students.getInputStream());
        List<Person> personList = persons.readAll();
        personList.forEach(System.out::println);
    }
}
