package gui;

import db.ExamDAO;
import db.Question;
import db.Result;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * AWT desktop exam client.
 * Components : Frame, MenuBar/Menu/MenuItem, Label, Button, TextField, TextArea, Checkbox,
 *              CheckboxGroup, Choice, List, Scrollbar, Panel, ScrollPane, Dialog.
 * Layouts    : CardLayout (4 screens), BorderLayout, GridLayout, FlowLayout.
 * Events     : ActionListener, ItemListener, AdjustmentListener, MouseListener, KeyListener,
 *              WindowListener.
 */
public class ExamApp extends Frame
        implements ActionListener, ItemListener, AdjustmentListener, MouseListener, KeyListener {

    private final ExamDAO dao = new ExamDAO();
    private final CardLayout cards = new CardLayout();
    private final Panel cardPanel = new Panel(cards);
    private final Label status = new Label("Welcome");

    // ---- login card ----
    private final TextField tfStudent = new TextField(20);
    private final Scrollbar sbMinutes = new Scrollbar(Scrollbar.HORIZONTAL, 5, 1, 1, 31); // 1..30
    private final Label lblMinutes = new Label("Duration: 5 minutes");
    private final Checkbox cbRules = new Checkbox("I agree to the exam rules");
    private final Button btnStart = new Button("Start Exam");

    // ---- exam card ----
    private final Label lblTimer = new Label("Time left: --:--", Label.RIGHT);
    private final Label lblProgress = new Label("Question 0 of 0");
    private final TextArea taQuestion = new TextArea("", 4, 40, TextArea.SCROLLBARS_VERTICAL_ONLY);
    private final CheckboxGroup group = new CheckboxGroup();
    private final Checkbox[] opts = new Checkbox[4];
    private final Button btnPrev = new Button("< Previous");
    private final Button btnNext = new Button("Next >");
    private final Button btnSubmit = new Button("Submit Exam");

    // ---- result card ----
    private final Label lblScore = new Label("", Label.CENTER);
    private final java.awt.List listResults = new java.awt.List(8);
    private final Button btnHome = new Button("Back to Start");

    // ---- admin card ----
    private final TextArea taNewQ = new TextArea(3, 30);
    private final TextField[] tfOpt = new TextField[4];
    private final Choice chCorrect = new Choice();
    private final Button btnAddQ = new Button("Add Question");

    // ---- exam state ----
    private List<Question> questions;
    private int[] answers;
    private int idx;
    private String student;
    private int secondsLeft;
    private Timer timer;

    public ExamApp() {
        super("Online Examination System");
        setLayout(new BorderLayout(5, 5));
        setMenuBar(buildMenu());

        Label title = new Label("Online Examination System", Label.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        cardPanel.add(buildLoginCard(), "login");
        cardPanel.add(buildExamCard(), "exam");
        cardPanel.add(buildResultCard(), "result");
        cardPanel.add(buildAdminCard(), "admin");
        add(cardPanel, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        for (Button b : new Button[]{btnStart, btnPrev, btnNext, btnSubmit, btnHome, btnAddQ})
            b.addActionListener(this);
        for (Checkbox c : opts) { c.addItemListener(this); c.addMouseListener(this); c.addKeyListener(this); }
        sbMinutes.addAdjustmentListener(this);
        btnStart.addMouseListener(this);
        btnSubmit.addMouseListener(this);
        addKeyListener(this);
        tfStudent.addKeyListener(this);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { stopTimer(); dispose(); System.exit(0); }
        });

        applyTheme();
        setSize(560, 460);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // =============== UI builders ===============
    private MenuBar buildMenu() {
        MenuBar mb = new MenuBar();
        Menu exam = new Menu("Exam");
        Menu admin = new Menu("Admin");
        Menu help = new Menu("Help");
        exam.add(new MenuItem("Start Page"));
        exam.add(new MenuItem("View Results"));
        exam.addSeparator();
        exam.add(new MenuItem("Exit"));
        admin.add(new MenuItem("Add Question"));
        help.add(new MenuItem("About"));
        for (Menu m : new Menu[]{exam, admin, help}) {
            for (int i = 0; i < m.getItemCount(); i++)
                if (!(m.getItem(i).getLabel().equals("-"))) m.getItem(i).addActionListener(this);
            mb.add(m);
        }
        return mb;
    }

    private Panel buildLoginCard() {
        Panel grid = new Panel(new GridLayout(0, 2, 8, 14));
        grid.add(new Label("Student name:")); grid.add(tfStudent);
        grid.add(lblMinutes);                  grid.add(sbMinutes);
        grid.add(new Label("Rules:"));         grid.add(cbRules);

        Panel flow = new Panel(new FlowLayout());
        flow.add(btnStart);

        Panel p = new Panel(new BorderLayout(10, 20));
        p.add(new Label("Enter your details to begin", Label.CENTER), BorderLayout.NORTH);
        sbMinutes.setPreferredSize(new Dimension(200, 18));
        Panel wrap = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        wrap.add(grid);
        p.add(wrap, BorderLayout.CENTER);
        p.add(flow, BorderLayout.SOUTH);
        return p;
    }

    private Panel buildExamCard() {
        taQuestion.setEditable(false);
        Panel top = new Panel(new GridLayout(1, 2));
        top.add(lblProgress);
        top.add(lblTimer);

        Panel optPanel = new Panel(new GridLayout(4, 1));
        for (int i = 0; i < 4; i++) {
            opts[i] = new Checkbox("", group, false);
            optPanel.add(opts[i]);
        }
        ScrollPane sp = new ScrollPane();              // long questions/options can scroll
        Panel body = new Panel(new BorderLayout(5, 5));
        body.add(taQuestion, BorderLayout.NORTH);
        body.add(optPanel, BorderLayout.CENTER);
        sp.add(body);

        Panel nav = new Panel(new FlowLayout());
        nav.add(btnPrev); nav.add(btnNext); nav.add(btnSubmit);

        Panel p = new Panel(new BorderLayout(5, 5));
        p.add(top, BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);
        p.add(nav, BorderLayout.SOUTH);
        return p;
    }

    private Panel buildResultCard() {
        lblScore.setFont(new Font("SansSerif", Font.BOLD, 16));
        Panel p = new Panel(new BorderLayout(5, 5));
        p.add(lblScore, BorderLayout.NORTH);
        p.add(listResults, BorderLayout.CENTER);
        Panel f = new Panel(new FlowLayout());
        f.add(btnHome);
        p.add(f, BorderLayout.SOUTH);
        return p;
    }

    private Panel buildAdminCard() {
        for (int i = 1; i <= 4; i++) { tfOpt[i - 1] = new TextField(25); chCorrect.add("Option " + i); }
        Panel grid = new Panel(new GridLayout(0, 2, 6, 8));
        grid.add(new Label("Question:"));  grid.add(taNewQ);
        for (int i = 0; i < 4; i++) { grid.add(new Label("Option " + (i + 1) + ":")); grid.add(tfOpt[i]); }
        grid.add(new Label("Correct answer:")); grid.add(chCorrect);
        Panel f = new Panel(new FlowLayout());
        f.add(btnAddQ);
        Panel p = new Panel(new BorderLayout(5, 5));
        p.add(new Label("Add a new question (admin)", Label.CENTER), BorderLayout.NORTH);
        p.add(grid, BorderLayout.CENTER);
        p.add(f, BorderLayout.SOUTH);
        return p;
    }

    // =============== Event handling ===============
    @Override public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        String cmd = e.getActionCommand();

        if (src == btnStart) startExam();
        else if (src == btnNext) { if (idx < questions.size() - 1) showQuestion(idx + 1); }
        else if (src == btnPrev) { if (idx > 0) showQuestion(idx - 1); }
        else if (src == btnSubmit) confirmSubmit();
        else if (src == btnHome) cards.show(cardPanel, "login");
        else if (src == btnAddQ) addQuestion();
        else if ("Start Page".equals(cmd)) cards.show(cardPanel, "login");
        else if ("View Results".equals(cmd)) { loadResults(); cards.show(cardPanel, "result"); }
        else if ("Add Question".equals(cmd)) cards.show(cardPanel, "admin");
        else if ("About".equals(cmd)) message("About", "Online Examination System - AWT + JDBC");
        else if ("Exit".equals(cmd)) { stopTimer(); dispose(); System.exit(0); }
    }

    /** Fired when a radio option is chosen: remember the answer. */
    @Override public void itemStateChanged(ItemEvent e) {
        if (questions == null || e.getStateChange() != ItemEvent.SELECTED) return;
        for (int i = 0; i < 4; i++) if (e.getSource() == opts[i]) answers[idx] = i + 1;
        status.setText("Answer saved for question " + (idx + 1));
    }

    @Override public void adjustmentValueChanged(AdjustmentEvent e) {
        lblMinutes.setText("Duration: " + sbMinutes.getValue() + " minutes");
    }

    // Mouse events
    @Override public void mouseEntered(MouseEvent e) {
        Object s = e.getSource();
        if (s == btnStart) status.setText("Fill in your name and accept the rules to start");
        else if (s == btnSubmit) status.setText("Submit ends the exam and shows your score");
        else status.setText("Click to choose this option (or press 1-4)");
    }
    @Override public void mouseExited(MouseEvent e) { status.setText("Ready"); }
    @Override public void mouseClicked(MouseEvent e) { }
    @Override public void mousePressed(MouseEvent e) { }
    @Override public void mouseReleased(MouseEvent e) { }

    // Keyboard events: 1-4 pick an option, N / P navigate, Enter starts exam from the name box
    @Override public void keyPressed(KeyEvent e) {
        if (e.getSource() == tfStudent) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) startExam();
            return;
        }
        if (questions == null) return;
        int k = e.getKeyCode();
        if (k >= KeyEvent.VK_1 && k <= KeyEvent.VK_4) {
            opts[k - KeyEvent.VK_1].setState(true);
            answers[idx] = k - KeyEvent.VK_1 + 1;
        } else if (k == KeyEvent.VK_N && idx < questions.size() - 1) showQuestion(idx + 1);
        else if (k == KeyEvent.VK_P && idx > 0) showQuestion(idx - 1);
    }
    @Override public void keyTyped(KeyEvent e) { }
    @Override public void keyReleased(KeyEvent e) { }

    // =============== Actions ===============
    private void startExam() {
        student = tfStudent.getText().trim();
        if (student.isEmpty()) { message("Error", "Please enter your name"); return; }
        if (!cbRules.getState()) { message("Error", "You must agree to the rules"); return; }
        try {
            questions = dao.getQuestions();
        } catch (Exception ex) { message("Database error", String.valueOf(ex.getMessage())); return; }
        if (questions.isEmpty()) { message("Info", "No questions in the database"); return; }

        answers = new int[questions.size()];                // 0 = unanswered
        secondsLeft = sbMinutes.getValue() * 60;
        cards.show(cardPanel, "exam");
        showQuestion(0);
        startTimer();
        requestFocus();
    }

    private void showQuestion(int i) {
        idx = i;
        Question q = questions.get(i);
        lblProgress.setText("Question " + (i + 1) + " of " + questions.size());
        taQuestion.setText(q.text);
        for (int k = 0; k < 4; k++) opts[k].setLabel((k + 1) + ".  " + q.options[k]);
        if (answers[i] == 0) group.setSelectedCheckbox(null);
        else group.setSelectedCheckbox(opts[answers[i] - 1]);
        btnPrev.setEnabled(i > 0);
        btnNext.setEnabled(i < questions.size() - 1);
    }

    private void startTimer() {
        stopTimer();
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override public void run() {
                secondsLeft--;
                EventQueue.invokeLater(() -> {
                    lblTimer.setText(String.format("Time left: %02d:%02d", secondsLeft / 60, secondsLeft % 60));
                    if (secondsLeft <= 0) finishExam();
                });
            }
        }, 0, 1000);
    }

    private void stopTimer() { if (timer != null) { timer.cancel(); timer = null; } }

    /** Modal confirm dialog before submitting. */
    private void confirmSubmit() {
        int unanswered = 0;
        for (int a : answers) if (a == 0) unanswered++;
        final Dialog d = new Dialog(this, "Confirm Submit", true);
        d.setLayout(new BorderLayout(10, 10));
        d.add(new Label("You have " + unanswered + " unanswered question(s). Submit now?", Label.CENTER),
                BorderLayout.CENTER);
        Panel p = new Panel(new FlowLayout());
        Button yes = new Button("Yes, submit");
        Button no = new Button("Go back");
        yes.addActionListener(ev -> { d.dispose(); finishExam(); });
        no.addActionListener(ev -> d.dispose());
        p.add(yes); p.add(no);
        d.add(p, BorderLayout.SOUTH);
        d.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { d.dispose(); }
        });
        d.setSize(380, 130);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    private void finishExam() {
        if (timer == null) return;                  // already finished
        stopTimer();
        int score = 0;
        for (int i = 0; i < questions.size(); i++)
            if (answers[i] == questions.get(i).correct) score++;
        try {
            dao.saveResult(student, score, questions.size());
        } catch (Exception ex) { message("Database error", String.valueOf(ex.getMessage())); }
        lblScore.setText(student + ", your score: " + score + " / " + questions.size());
        loadResults();
        cards.show(cardPanel, "result");
    }

    private void loadResults() {
        try {
            listResults.removeAll();
            for (Result r : dao.getResults()) listResults.add(r.toString());
        } catch (Exception ex) { message("Database error", String.valueOf(ex.getMessage())); }
    }

    private void addQuestion() {
        String text = taNewQ.getText().trim();
        for (TextField t : tfOpt) if (t.getText().trim().isEmpty()) { message("Error", "Fill all four options"); return; }
        if (text.isEmpty()) { message("Error", "Question text is required"); return; }
        try {
            dao.addQuestion(new Question(0, text, tfOpt[0].getText().trim(), tfOpt[1].getText().trim(),
                    tfOpt[2].getText().trim(), tfOpt[3].getText().trim(), chCorrect.getSelectedIndex() + 1));
            taNewQ.setText("");
            for (TextField t : tfOpt) t.setText("");
            status.setText("Question added");
        } catch (Exception ex) { message("Database error", String.valueOf(ex.getMessage())); }
    }

    private void message(String title, String msg) {
        final Dialog d = new Dialog(this, title, true);
        d.setLayout(new BorderLayout(10, 10));
        d.add(new Label(msg, Label.CENTER), BorderLayout.CENTER);
        Button ok = new Button("OK");
        ok.addActionListener(ev -> d.dispose());
        Panel p = new Panel();
        p.add(ok);
        d.add(p, BorderLayout.SOUTH);
        d.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { d.dispose(); }
        });
        d.setSize(380, 130);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    // =============== Theme (soft green, light mode) ===============
    private void applyTheme() {
        Color bg = new Color(240, 249, 242);        // very light green background
        Color primary = new Color(34, 139, 94);     // green buttons and headings
        Font normal = new Font("SansSerif", Font.PLAIN, 14);

        setBackground(bg);
        paintAll(this, bg, normal);

        for (Button b : new Button[]{btnStart, btnPrev, btnNext, btnSubmit, btnHome, btnAddQ}) {
            b.setBackground(primary);
            b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif", Font.BOLD, 14));
        }
        lblTimer.setForeground(new Color(200, 50, 40));
        lblScore.setForeground(primary);
        lblProgress.setForeground(primary);
        status.setForeground(Color.DARK_GRAY);
    }

    private void paintAll(Container c, Color bg, Font f) {
        for (Component k : c.getComponents()) {
            if (k instanceof Panel || k instanceof Label || k instanceof Checkbox) k.setBackground(bg);
            Font cur = k.getFont();
            if (!(k instanceof Button) && (cur == null || !cur.isBold())) k.setFont(f);
            if (k instanceof Container) paintAll((Container) k, bg, f);
        }
    }

    public static void main(String[] args) { new ExamApp(); }
}
