package school.faang.user_service.dmitrysprint2.part2;

import org.bouncycastle.util.Strings;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Filtrator {
    public static void main(String[] args) {
        List<String > strings = Arrays.asList("apple", "banana", "cherry", "date", "fig", "grape");
        String alphaSource = "abcdefghijklmnopqrstuvwxyz";
        System.out.println(filtrate(strings, alphaSource));

    }
    public static List<String> filtrate(List<String > strings, String alphaSource){
return strings.stream().filter(s -> alphaSource.chars().anyMatch(c -> s.indexOf(c) != -1)).sorted(Comparator.comparingInt(String::length))
        .collect(Collectors.toList());
    }
}
