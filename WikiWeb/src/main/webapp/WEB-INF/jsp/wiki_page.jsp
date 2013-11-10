<!DOCTYPE html>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="info.vividcode.app.web.wiki.model.PageManager.PageResource" %>
<%@ page import="info.vividcode.web.util.HtmlUtils" %>
<jsp:useBean id="it" scope="request" type="info.vividcode.app.web.wiki.model.PageManager.PageResource" />
<html>
<head>
<title><%= HtmlUtils.h(it.title) %></title>
</head>
<body>
<h1><%= HtmlUtils.h(it.title) %></h1>
<article>
<%= HtmlUtils.h(it.content) %>
</article>
</body>
</html>
