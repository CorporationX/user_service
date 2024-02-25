package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.student.Person;
import school.faang.user_service.mapper.UserPersonMapper;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class CsvPersonParser {
    private final ObjectReader csvOpenReader;
    private final UserPersonMapper userPersonMapper;

    /*public List<User> parse(MultipartFile csvFile) throws IOException {
        List<User> users = new ArrayList<>();

        long csvFileSize = csvFile.getSize();
        if (csvFileSize > maxFileChunkSize) {
            log.debug("Csv file size is {}. parallel parse started", csvFileSize);
            parallelParse(csvInputStreamBuilder, users);
        } else {
            log.debug("Csv file size is {}. linealParse parse started", csvFileSize);
            linealParse(csvInputStreamBuilder, users);
        }
        csvInputStreamBuilder.close();
        log.debug("Csv file parse finished. Parsed users count {}", users.size());
        return users;
    }

    private void parallelParse(InputStream inputStream, List<User> users) throws IOException {
        List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        StringBuilder csvStrBuilderFile = new StringBuilder();

        String line;
        int count = 0;
        String header = reader.readLine();
        while ((line = reader.readLine()) != null) {
            if (count == 0) {
                csvStrBuilderFile.append(header).append("\n");
            } else {
                csvStrBuilderFile.append(line).append("\n");
            }
            count++;
            if (count == maxFileChunkSize / expectedChunkSize) {
                log.debug("chunk with amount of lines {} of csv file is ready to parse", count);
                runAsync(users, completableFutures, csvStrBuilderFile);
                count = 0;
                csvStrBuilderFile = new StringBuilder();
            }
        }
        if (count > 0) {
            runAsync(users, completableFutures, csvStrBuilderFile);
        }

        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
        log.debug("parallel parsing finished");

        executor.shutdown();
        inputStreamReader.close();
        reader.close();
    }

    @Async
    private void runAsync(List<User> users, List<CompletableFuture<Void>> completableFutures, StringBuilder csvStrBuilderFile) {
        completableFutures.add(CompletableFuture.runAsync(
                () -> {
                    try {
                        parseCsvToPeopleAndMapToUser(csvStrBuilderFile.toString().getBytes(), users);
                        log.debug("started thread to parse chunk of csv file");
                    } catch (IOException e) {
                        log.debug("IOException trying to parallel parsing");
                        throw new RuntimeException(e);
                    }
                }
                , executor));
    }

    private void linealParse(InputStream inputStream, List<User> users) throws IOException {
        parseCsvToPeopleAndMapToUser(inputStream.readAllBytes(), users);
    }*/

    public List<Person> parse(MultipartFile csvFile) throws IOException {
        MappingIterator<Person> mappingIterator = csvOpenReader.readValues(csvFile.getInputStream());

        return mappingIterator.readAll();
    }
}