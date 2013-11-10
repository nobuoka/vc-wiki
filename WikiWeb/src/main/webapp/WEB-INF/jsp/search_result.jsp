<!DOCTYPE html>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page language="java" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="info.vividcode.app.web.wiki.model.PageManager.PagePathResourcePair" %>
<%@ page import="info.vividcode.web.util.HtmlUtils" %>
<jsp:useBean id="it" scope="request" type="List<PagePathResourcePair>" />
<html>
<head>
<title>検索結果</title>
<style>
.search-result-item {
  margin: 0.5em 0;
}
</style>
</head>
<body>
<% for (PagePathResourcePair pprp : it ) { %>
<div class="search-result-item">
  <div><a href="../<%= HtmlUtils.h(pprp.path) %>"><%= HtmlUtils.h(pprp.resource.title) %></a></div>
  <div><%= HtmlUtils.h(pprp.resource.content) %></div>
</div>
<% } %>
</body>
</html>
