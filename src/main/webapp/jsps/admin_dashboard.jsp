<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Price Analyzer</title>

<!-- Bootstrap -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap-theme.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js"></script>
</head>
<body>
	<div class="container">
		<a href="/" class="btn">Home</a>
		<h1>AWS Lambda Price Analyzer</h1>
		
		<p>&nbsp;</p>

		<h3> Lambda settings</h3>
		<p><a href="/admin/settings/new" class="btn btn-primary">Create new Settings</a></p>
		<table class="table table-bordered">
			<tr>
				<th>Memory Setting (MB)</th>
				<th>Price per GBs ($)</th>
				<th>Price per million requests ($)</th>
				<th>Free GBs</th>
				<th>Free no. of requests</th>
			</tr>
			<% Iterable<Entity> lambdas = (Iterable<Entity>) request.getAttribute("lambdas"); %>
			<% for(Entity lambda: lambdas) { %>
			 <tr>
				<td><%= lambda.getProperty("memory") %></td>
				<td><%= lambda.getProperty("pricePerGbs") %></td>
				<td><%= lambda.getProperty("pricePerMRequest") %></td>
				<td><%= lambda.getProperty("freeGbs") %></td>
				<td><%= lambda.getProperty("freeRequests") %></td>
			 </tr>
			<% } %>
		</table>
		
		<h3> API gateway settings</h3>
		<p><a href="/admin/api_gateway" class="btn btn-primary">Update Api gateway cost</a></p>
		<table class="table table-bordered">
			<tr>
				<th> Price per million requests ($) </th>
				<th> Free no. of requests </th>
			</tr>
			<tr>
				<% Entity api = (Entity) request.getAttribute("api"); %>
				<td><%= api.getProperty("pricePerMRequest") %></td>
				<td><%= api.getProperty("freeRequests") %></td>
			</tr>
		</table>
		
		<h3> Data transfer settings </h3>
		<p><a href="/admin/data_transfer" class="btn btn-primary">Update Data Transfer cost</a></p>
		<table class="table table-bordered">
			<tr>
				<th> price per GB transfered ($) </th>
			</tr>
			<tr>
				<% Entity dataTransfer = (Entity) request.getAttribute("dataTransfer"); %>
				<td><%= dataTransfer.getProperty("pricePerGb") %></td>
			</tr>
		</table>
		
		
	</div>
</body>
</html>