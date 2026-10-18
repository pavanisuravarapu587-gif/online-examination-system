<%@ page contentType="text/html; charset=UTF-8" import="java.util.*, db.Result" %>
<!DOCTYPE html>
<html><head><title>All Results</title><link rel="stylesheet" href="style.css"></head>
<body>
<h2>All Results</h2>
<table>
  <tr><th>Student</th><th>Score</th><th>Date</th></tr>
  <% List<Result> list = (List<Result>) request.getAttribute("results");
     if (list != null) for (Result r : list) { %>
  <tr><td><%= r.getStudent() %></td><td><%= r.getScore() %> / <%= r.getTotal() %></td>
      <td><%= r.getTakenOn() %></td></tr>
  <% } %>
</table>
<p><a href="index.jsp">Home</a></p>
</body></html>
