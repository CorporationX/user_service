package school.faang.user_service.util.file;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class CsvUtil {

    private final CsvMapper csvMapper;

    public <T> List<T> parseCsvMultipartFile(MultipartFile multipartFile, Class<T> clazz) {
        log.info("Start parseCsvMultipartFile() - {}", multipartFile.getOriginalFilename());

        List<CompletableFuture<T>> completableFutures = new ArrayList<>();
        int linesReaded = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(multipartFile.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            CsvSchema csvSchema = generateCsvSchema(headerLine);

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                linesReaded++;
                String finalLine = line;
                completableFutures.add(CompletableFuture.supplyAsync(() ->
                        parseCsvLineToPojo(finalLine, clazz, csvSchema)));
            }
        } catch (IOException e) {
            log.error("Exception while parsing - {}. {}", multipartFile.getOriginalFilename(), e.getMessage());
            throw new ParseException("Error reading multipart file " + multipartFile.getName());
        }

        List<T> result = completableFutures.stream()
                .map(CompletableFuture::join)
                .toList();

        if (linesReaded == result.size()) {
            log.info("Finish parseCsvMultipartFile() - {}", multipartFile.getOriginalFilename());
            return result;
        } else {
            log.error("Expected objects: {}, but parsed - {}", linesReaded, result.size());
            throw new ParseException("Discrepancy between expected parsed lines and obtained lines");
        }
    }

    private CsvSchema generateCsvSchema(String headerLine) {
        CsvSchema.Builder schemaBuilder = CsvSchema.builder();

        for (String header : headerLine.split(",")) {
            schemaBuilder.addColumn(header.trim());
        }

        return schemaBuilder.build().withoutHeader();
    }

    private <T> T parseCsvLineToPojo(String line, Class<T> clazz, CsvSchema csvSchema) {
        try {
            return csvMapper.readerFor(clazz).with(csvSchema).readValue(line);
        } catch (IOException e) {
            throw new ParseException(e.getMessage());
        }
    }

    public <T> List<T> parseCsvToPojo(MultipartFile multipartFile, Class<T> clazz) {
        try {
            return new CsvMapper()
                    .readerWithSchemaFor(clazz)
                    .with(CsvSchema.emptySchema().withHeader())
                    .<T>readValues(multipartFile.getBytes())
                    .readAll();
        } catch (IOException e) {
            throw new ParseException(e.getMessage());
        }
    }
}