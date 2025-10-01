package school.faang.user_service.dmitrysprint2.part2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Filtration {

    public static void main(String[] args) {

        List<String> strings = new ArrayList<>();
        strings.add("apple");
        strings.add("banana");
        strings.add("avocado");
        strings.add("apricot");
        char sample = 'a';
        System.out.println(fingAndSort(strings, sample));


    }

    public static List<String> fingAndSort(List<String> strings, char sample) {
        return strings.stream().filter(a -> a.charAt(0) == sample).sorted(Comparator.comparingInt(String::length)).collect(Collectors.toList());
    }
}