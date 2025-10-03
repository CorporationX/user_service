package school.faang.user_service.dmitrrysprint1.reverse;

public class Main {
    public static void main(String[] args) {
        int[] sample = new int[]{4, 7, 2, 7, 6, 5, 3, 8};
        Example example = new Example();
        int [] reversed = example.reverse(sample);
        System.out.println(reversed[0] + " " + reversed[1]);
    }
}
