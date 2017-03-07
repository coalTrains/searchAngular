<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import = "javax.servlet.RequestDispatcher" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isErrorPage="true" import="java.io.*" contentType="text/html" %>
<%@ page isErrorPage="true" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Error</title>
	</head>
	<body style="background-color:#eeeebb">
	
	<h2> Error Page</h2>
	
	
	<p>Error:  <c:out value="${context}"/></p>
		
	</body>
</html>