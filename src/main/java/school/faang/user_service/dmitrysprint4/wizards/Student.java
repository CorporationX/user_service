package school.faang.user_service.dmitrysprint4.wizards;

public class Student {

    private String name;

    private int year;

    private int points;

    public Student(String name, int year, int points) {
        this.name = name;
        this.year = year;
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }


}
