package school.faang.user_service.dmitrysprint2;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MathStaticMethods {

    public static void main(String[] args) {
        List<Integer> sample = new ArrayList<>();
        List<String> strings = new ArrayList<>();
        strings.add("1ggfg");
        strings.add("grrddf");
        strings.add("wweeww");
        strings.add("zccdfd");
        strings.add("ggrgfdd");
        strings.add("ggtyuuu");
        sample.add(5);
        sample.add(4);
        sample.add(2);
        sample.add(1);


        Predicate<Integer> predicateInt = integer -> integer < 4;
        Predicate<Integer> secondPredicate = integer -> integer < 3;
        System.out.println(fourNumbersSum(sample));
        System.out.println(maxValueFinder(sample));
        System.out.println(averageValueFinder(sample));
        System.out.println(stringCounter(strings, 'g'));
        List<String> stringsWithSubstring = sustringSearcher(strings, "gg");
        System.out.println(stringsWithSubstring.size());
        System.out.println(stringSizeSorter(strings).size());
        System.out.println(checkPredicate(sample, predicateInt));
        System.out.println("Min value is " + findSomeElement(sample, 3));
        System.out.println("Strings length  is " + listOfStringsLength(strings));
    }


    public static int fourNumbersSum(List<Integer> ints) {

        return ints.stream().reduce(0, Integer::sum);

    }

    public static int maxValueFinder(List<Integer> ints) {
        int maxint = ints.stream().mapToInt(value -> value).max().orElseThrow(NoSuchElementException::new);

        return maxint;

    }

    public static double averageValueFinder(List<Integer> ints) {
        double middle = ints.stream().mapToInt(value -> value).average().orElseThrow(NoSuchElementException::new);

        return middle;

    }

    public static long stringCounter(List<String> strings, char sample) {
        long number = strings.stream().filter(str -> str.charAt(0) == sample).count();


        return number;
    }

    public static List<String> sustringSearcher(List<String> strings, String substring) {
        List<String> results = strings.stream().filter(s -> s.contains(substring)).toList();
        return results;
    }

    public static List<String> stringSizeSorter(List<String> strings) {

        List<String> sortedStrings = strings.stream()
                .sorted(String::compareTo)
                .collect(Collectors.toList());
        return sortedStrings;
    }

    public static boolean checkPredicate(List<Integer> ints, Predicate<Integer> predicate) {

        long result = ints.stream().filter(predicate).count();
        if (result == ints.size()) {
            return true;
        } else return false;

    }

    public static int findSomeElement(List<Integer> ints, int sample) {
        Predicate<Integer> predicate = integer -> integer > sample;
        Optional<Integer> minValue = ints.stream().filter(predicate).min((Comparator.naturalOrder()));
        return minValue.get();
    }

    public static List<Integer> listOfStringsLength(List<String> strings) {
        List<Integer> ints = strings.stream().map(string -> string.length()).collect(Collectors.toList());
        return ints;
    }

}
