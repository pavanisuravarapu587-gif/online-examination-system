<%@ page contentType="text/html; charset=UTF-8" import="java.util.*, db.Question" %>
<!DOCTYPE html>
<html><head><title>Exam</title><link rel="stylesheet" href="style.css"></head>
<body>
<h2>Exam - <%= session.getAttribute("student") %></h2>
<form method="post" action="exam">
  <input type="hidden" name="action" value="submit">
  <%
     List<Question> questions = (List<Question>) request.getAttribute("questions");
     int n = 1;
     if (questions != null) for (Question q : questions) {          // scriptlet
  %>
  <fieldset>
    <legend>Q<%= n++ %>. <%= q.getText() %></legend>
    <% for (int i = 0; i < 4; i++) { %>
      <label><input type="radio" name="q<%= q.getId() %>" value="<%= i + 1 %>">
             <%= q.getOptions()[i] %></label><br>
    <% } %>
  </fieldset>
  <% } %>
  <button type="submit">Submit Exam</button>
</form>
</body></html>
