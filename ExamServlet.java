package web;

import db.ExamDAO;
import db.Question;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

/**
 * Controller servlet (HttpServlet = protocol-specific servlet; GenericServlet is the
 * protocol-independent parent).
 * Life cycle: init() once -> service() -> doGet()/doPost() per request -> destroy() once.
 * Uses jakarta.* (Tomcat 10+). For Tomcat 9 or older, change to javax.*
 *
 *   GET  /exam              -> show login page (index.jsp is the welcome page)
 *   POST /exam?action=start -> store name in session, load questions, forward to exam.jsp
 *   POST /exam?action=submit-> evaluate answers, save result, forward to result.jsp
 *   GET  /exam?action=results -> list previous results
 */
@WebServlet("/exam")
public class ExamServlet extends HttpServlet {
    private ExamDAO dao;

    @Override public void init() throws ServletException {
        dao = new ExamDAO();
        log("ExamServlet initialised");
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            if ("results".equals(req.getParameter("action"))) {
                req.setAttribute("results", dao.getResults());
                req.getRequestDispatcher("/results.jsp").forward(req, resp);
            } else {
                resp.sendRedirect("index.jsp");
            }
        } catch (Exception e) { throw new ServletException(e); }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            HttpSession session = req.getSession();
            String action = req.getParameter("action");

            if ("start".equals(action)) {
                session.setAttribute("student", req.getParameter("student"));
                req.setAttribute("questions", dao.getQuestions());
                req.getRequestDispatcher("/exam.jsp").forward(req, resp);

            } else if ("submit".equals(action)) {
                List<Question> qs = dao.getQuestions();
                int score = 0;
                for (Question q : qs) {
                    String a = req.getParameter("q" + q.getId());   // radio group name: q<id>
                    if (a != null && Integer.parseInt(a) == q.getCorrect()) score++;
                }
                String student = (String) session.getAttribute("student");
                dao.saveResult(student, score, qs.size());
                req.setAttribute("score", score);
                req.setAttribute("total", qs.size());
                req.getRequestDispatcher("/result.jsp").forward(req, resp);
            } else {
                resp.sendRedirect("index.jsp");
            }
        } catch (Exception e) { throw new ServletException(e); }
    }

    @Override public void destroy() { log("ExamServlet destroyed"); }
}
