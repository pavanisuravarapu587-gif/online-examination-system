package db;

public class Question {
    public int id, correct;                 // correct = 1..4
    public String text;
    public String[] options = new String[4];

    public Question(int id, String text, String o1, String o2, String o3, String o4, int correct) {
        this.id = id; this.text = text; this.correct = correct;
        options[0] = o1; options[1] = o2; options[2] = o3; options[3] = o4;
    }
    public int getId() { return id; }
    public String getText() { return text; }
    public String[] getOptions() { return options; }
    public int getCorrect() { return correct; }
}
