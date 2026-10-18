package db;

public class Result {
    public String student, takenOn;
    public int score, total;

    public Result(String student, int score, int total, String takenOn) {
        this.student = student; this.score = score; this.total = total; this.takenOn = takenOn;
    }
    public String getStudent() { return student; }
    public int getScore() { return score; }
    public int getTotal() { return total; }
    public String getTakenOn() { return takenOn; }

    @Override public String toString() {
        return student + "  -  " + score + "/" + total + "  (" + takenOn + ")";
    }
}
