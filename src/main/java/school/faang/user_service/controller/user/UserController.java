package school.faang.user_service.controller.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.json.student.ContactInfo;
import com.json.student.Person;
import com.json.student.PersonSchema;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.converter.csvconverter.ConverterCsvToPerson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/comments")
public class UserController {
    private final ConverterCsvToPerson converterCsvToPerson;

    @PostMapping("/add/csv")
    public List<Person> addUsersFromFile(@RequestParam("file") MultipartFile file) {
        System.out.println("Name file: " + file.getOriginalFilename());
        return converterCsvToPerson.convertCsvToPerson(file);
    }
}
