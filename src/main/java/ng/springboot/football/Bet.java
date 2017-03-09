package ng.springboot.football;

public class Bet {

    private String name;
    private String email;
    private String prediction;
    private int predictionA;
    private int predictionB;

    private Bet() {

    }

    public Bet(String name, String email, String prediction, int predictionA, int predictionB) {
        this.name = name;
        this.email = email;
        this.prediction = prediction;
        this.predictionA = predictionA;
        this.predictionB = predictionB;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPrediction() {
        return prediction;
    }

    public int getPredictionA() {
        return predictionA;
    }

    public int getPredictionB() {
        return predictionB;
    }

    @Override
    public String toString() {
        return "ng.springboot.football.Bet { name: \"" + name + "\"" + ", email: \"" + email + "\"" + ", prediction: \""
                + prediction + "\"" + ", predictionA: \"" + predictionA + "\"" + ", predictionB: \"" + predictionB
                + "\" }";
    }

}
