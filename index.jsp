<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html><head><title>Online Examination</title><link rel="stylesheet" href="style.css"></head>
<body>
<h2>Online Examination System</h2>
<form method="post" action="exam">
  <input type="hidden" name="action" value="start">
  <p><label>Student name: <input name="student" required></label></p>
  <p><button type="submit">Start Exam</button></p>
</form>
<p><a href="exam?action=results">View previous results</a></p>
<%-- JSP implicit object: 'application' (ServletContext), 'session', 'request', 'out' ... --%>
<p>Server time: <%= new java.util.Date() %></p>
</body></html>
