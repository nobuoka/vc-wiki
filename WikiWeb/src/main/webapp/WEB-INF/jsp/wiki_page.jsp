<!DOCTYPE html>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="info.vividcode.app.web.wiki.model.PageManager.PageHtmlResource" %>
<%@ page import="info.vividcode.web.util.HtmlUtils" %>
<jsp:useBean id="it" scope="request" type="info.vividcode.app.web.wiki.web.controllers.WikiPageController.M" />
<%
    PageHtmlResource pr = it.pr;
%>
<html>
<head>
<title><%= HtmlUtils.h(pr.title) %></title>
</head>
<body>
<%@ include file="./_header.jspf" %>
<h1><%= HtmlUtils.h(pr.title) %></h1>
<article>
<%= pr.contentHtml %>
</article>
</body>
</html>
