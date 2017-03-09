package ng.springboot.football;

public class EventResult {

    private int resultA;
    private int resultB;
    private String result;

    private EventResult() {

    }

    public EventResult(int resultA, int resultB, String result) {
        this.resultA = resultA;
        this.resultB = resultB;
        this.result = result;
    }

    public int getResultA() {
        return resultA;
    }

    public int getResultB() {
        return resultB;
    }

    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "ng.springboot.football.EventResult { resultA: \"" + resultA + "\""
            + ", resultB: \"" + resultB + "\""
            + ", result: \"" + result + "\" }";
    }

}
