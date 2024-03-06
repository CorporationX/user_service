package school.faang.user_service.service.csv;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.json.student.PersonSchemaV2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dao.csv.UserDAO;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.ParseFIleException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.service.country.CountryService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class CSVFileParserService {
    private static final int BATCH_SIZE = 1000;
    private final UserMapper userMapper;
    private final Executor executor;
    private final CountryService countryService;
    private final CsvMapper csvMapper;
    private final UserDAO userDAO;

    @Autowired
    public CSVFileParserService(UserMapper userMapper, @Qualifier("taskExecutor") Executor executor, CountryService countryService, CsvMapper csvMapper, UserDAO userDAO) {
        this.userMapper = userMapper;
        this.executor = executor;
        this.countryService = countryService;
        this.csvMapper = csvMapper;
        this.userDAO = userDAO;
    }

    public void parseFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ParseFIleException("csv file is empty");
        }
        List<PersonSchemaV2> persons = parseFileWithCsvMapper(file);
        batchProcess(persons);
    }


    private void batchProcess(List<PersonSchemaV2> persons) {
        List<PersonSchemaV2> batch = new ArrayList<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < persons.size(); i++) {
            batch.add(persons.get(i));
            if ((i + 1) % BATCH_SIZE == 0) {
                futures.add(runBatchProcess(batch));
                batch = new ArrayList<>();
            }
        }
        if (!batch.isEmpty()) {
            futures.add(runBatchProcess(batch));
        }
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
    }


    private CompletableFuture<Void> runBatchProcess(List<PersonSchemaV2> persons) {
        return CompletableFuture.runAsync(() -> {
            List<User> users = persons.stream()
                    .map(person -> {
                        User user = userMapper.personToUser(person);
                        randomUserPayload(user);
                        user.setCountry(countryService.getCountryFromCache(user));
                        return user;
                    }).toList();
            userDAO.saveUsersUsingBatchUpdate(users);
        }, executor);
    }


    private void randomUserPayload(User user) {
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        user.setUsername(RandomStringUtils.randomAlphanumeric(5) + user.getEmail());
        user.setPhone(String.valueOf(1000000000 + threadLocalRandom.nextInt(900000000)));
        user.setEmail(RandomStringUtils.randomAlphanumeric(5) + user.getEmail());
        user.setPassword(generateCommonLangPassword());
    }

    private List<PersonSchemaV2> parseFileWithCsvMapper(MultipartFile file) {
        CsvSchema csvSchema = csvMapper.schemaFor(PersonSchemaV2.class)
                .withHeader()
                .withColumnReordering(true);
        try {
            MappingIterator<PersonSchemaV2> mappingIterator = csvMapper
                    .readerFor(PersonSchemaV2.class)
                    .with(csvSchema)
                    .readValues(file.getInputStream());
            return mappingIterator.readAll();
        } catch (IOException e) {
            throw new ParseFIleException(e.getMessage());
        }
    }

    private String generateCommonLangPassword() {
        String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
        String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
        String numbers = RandomStringUtils.randomNumeric(2);
        String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
        String totalChars = RandomStringUtils.randomAlphanumeric(2);
        String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
                .concat(numbers)
                .concat(specialChar)
                .concat(totalChars);
        List<Character> pwdChars = combinedChars.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(pwdChars);
        String password = pwdChars.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        return password;
    }
}
