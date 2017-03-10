package ng.springboot.football;

public class EventResult {

    private int resultA;
    private int resultB;
    private String result;
    private String owner;

    private EventResult() {

    }

    public EventResult(int resultA, int resultB, String result, String owner) {
        this.resultA = resultA;
        this.resultB = resultB;
        this.result = result;
        this.owner = owner;
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

    public String getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return "ng.springboot.football.EventResult { resultA: \"" + resultA + "\""
            + ", resultB: \"" + resultB + "\""
            + ", result: \"" + result + "\""
            + ", owner: \"" + owner + "\" }";
    }

}
