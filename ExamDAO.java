package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** All SQL lives here; shared by the AWT client and the Servlet/JSP web client. */
public class ExamDAO {

    public List<Question> getQuestions() throws SQLException {
        List<Question> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM question ORDER BY id")) {
            while (rs.next()) {
                list.add(new Question(rs.getInt("id"), rs.getString("text"),
                        rs.getString("opt1"), rs.getString("opt2"),
                        rs.getString("opt3"), rs.getString("opt4"), rs.getInt("correct")));
            }
        }
        return list;
    }

    public void addQuestion(Question q) throws SQLException {
        String sql = "INSERT INTO question(text,opt1,opt2,opt3,opt4,correct) VALUES(?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, q.text);
            for (int i = 0; i < 4; i++) ps.setString(i + 2, q.options[i]);
            ps.setInt(6, q.correct);
            ps.executeUpdate();
        }
    }

    public void saveResult(String student, int score, int total) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO result(student,score,total) VALUES(?,?,?)")) {
            ps.setString(1, student);
            ps.setInt(2, score);
            ps.setInt(3, total);
            ps.executeUpdate();
        }
    }

    public List<Result> getResults() throws SQLException {
        List<Result> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM result ORDER BY id DESC")) {
            while (rs.next()) {
                list.add(new Result(rs.getString("student"), rs.getInt("score"),
                        rs.getInt("total"), rs.getString("taken_on")));
            }
        }
        return list;
    }
}
