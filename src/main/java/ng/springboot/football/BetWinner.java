package ng.springboot.football;

public class BetWinner {

    private String name;
    private String email;

    private BetWinner() {

    }

    public BetWinner(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "ng.springboot.football.BetWinner { name: \"" + name + "\""
            + ", email: \"" + email + "\" }";
    }

}
