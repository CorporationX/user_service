package school.faang.user_service.dmitrrysprint1.reverse;

public class Example {
    private int[] sample;

    public Example(int[] sample) {
        this.sample = sample;
    }

    public int[] getSample() {
        return sample;
    }

    public void setSample(int[] sample) {
        this.sample = sample;
    }

    public Example() {
    }

    public int[] reverse(int[] sample){

        int temp;
        for (int i = 0; i < sample.length / 2; i++) {
            temp = sample[i];
            sample[i] = sample[sample.length - 1 - i];
            sample[sample.length - 1 - i] = temp;
        }
        return sample;
    }
}
