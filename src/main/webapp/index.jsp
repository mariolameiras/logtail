<!DOCTYPE html>
<%@page import="com.skzo.logtail.file.LogFileHandler"%>
<html>
<head>
<meta charset="utf-8">
<title>Log Tail</title>
<script type="text/javascript" src="jquery/jquery-1.9.0.js"></script>
<script type="text/javascript" src="jquery/jquery.atmosphere.js"></script>
<script type="text/javascript" src="jquery/application.js"></script>
<link href="css/main.css" media="all" rel="stylesheet" type="text/css" />
</head>
<body>
	<%
		
	%>

	<div id="header">
		<label> <select name="logs" id="logSelect">
				<option selected>Select Box</option>
				<%
				
				%>
				<option value="path">test.log</option>
				<option>This Is A Longer Option</option>
		</select>
		</label>
		<br><br>
		<img id="control" width="24" height="24"  src="images/play.png" title="Pause" alt="Pause"">
	</div>
	<div id="logArea"></div>
	<div>
		<span id="status">Connecting...</span>
	</div>
</body>
</html>