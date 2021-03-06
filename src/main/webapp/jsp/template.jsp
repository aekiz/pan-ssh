<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<!DOCTYPE html>
<html lang="es">
  <head>
    <meta charset="utf-8">
    <title><s:text name="pan.title"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Portal agregador de noticias">
    <meta name="author" content="Borja Bares">
    <!—[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]—>
    <link href=<s:url value="/bootstrap/css/bootstrap.min.css"/> rel="stylesheet">
    <style type="text/css">
      body {
        padding-top: 60px;
        padding-bottom: 20px;
      }
    </style>
    <link href=<s:url value="/bootstrap/css/bootstrap-responsive.min.css"/> rel="stylesheet">
    <link href=<s:url value="/css/doc.css"/> rel="stylesheet">
    <link href=<s:url value="/img/favicon.ico"/> rel="shortcut icon">
  </head>
  <body>
    <div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container-fluid">
          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
	        <span class="icon-bar"></span>
	        <span class="icon-bar"></span>
	        <span class="icon-bar"></span>
	      </a>
          <a class="brand" href="/index/"><s:text name="pan.title"/></a>
          <div class="nav-collapse collapse">
          	<tiles:insertAttribute name="menu" />
          	<tiles:insertAttribute name="usermenu" />
          </div>
        </div>
      </div>
    </div><!-- End NavBar -->
    <div class="container content">
      <div class="container-fluid">
        <div class="row-fluid">
          <div class="span12">
            <tiles:insertAttribute name="title" />
            <tiles:insertAttribute name="body" />
            <tiles:insertAttribute name="pagination" />
          </div>
        </div>
     </div>
    </div><!-- End Container -->
    <div class="container-fluid upline">
	    <div class="container footer">
	      <tiles:insertAttribute name="footer" />
	    </div>
    </div>
    <script src=<s:url value="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"/>></script>
    <script src=<s:url value="/bootstrap/js/bootstrap.min.js"/>></script>
    <script src=<s:url value="/js/functions.js"/>></script>
  </body>
</html>