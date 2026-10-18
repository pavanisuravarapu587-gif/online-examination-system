<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html><head><title>Result</title><link rel="stylesheet" href="style.css"></head>
<body>
<h2>Result</h2>
<p><b><%= session.getAttribute("student") %></b>, you scored
   <b><%= request.getAttribute("score") %> / <%= request.getAttribute("total") %></b>.</p>
<p><a href="exam?action=results">All results</a> | <a href="index.jsp">Home</a></p>
</body></html>
