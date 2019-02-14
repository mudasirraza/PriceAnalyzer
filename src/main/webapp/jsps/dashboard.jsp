<%@ page import="java.util.ArrayList" %>

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
		<a href="/admin/settings" class="btn">Admin</a>
		<h1>AWS Lambda Price Analyzer</h1>
		
		<p>&nbsp;</p>

		<form role="form" action="/" method="post">
		
			<% ArrayList<Long> memory_sizes = (ArrayList<Long>) request.getAttribute("memory_sizes"); %>
			<div class="form-group">
				<div class="input-group">
					<span class="input-group-addon">Memoy Settings</span>
					<select name="memory">
					  <% for(Long m: memory_sizes) { %>
					  	<option value="<%= m %>"><%= m %></option>
					  <% } %>
					</select>
					<span> MB</span>
				</div>
			</div>
			
			<div class="form-group">
				<div class="input-group">
					<span class="input-group-addon">Free Tier included?</span>
					<input type="checkbox" name="free_tier_check" value="true">
				</div>
			</div>
			
			<div class="form-group">
				<div class="input-group">
					<span class="input-group-addon">Number of Requests</span>
					<input name="num_requests" type="number" class="form-control">
				</div>
			</div>
			
			<div class="form-group">
				<div class="input-group">
					<span class="input-group-addon">Avg. execution time per request (in ms)</span>
					<input name="avg_execution_time" type="number" class="form-control">
				</div>
			</div>
			
			<div class="form-group">
				<div class="input-group">
					<span class="input-group-addon">Include api Gateway costs?</span>
					<input type="checkbox" name="api_gateway_check" value="true">
				</div>
			</div>
			
			<div class="form-group">
				<div class="input-group">
					<span class="input-group-addon">Number of Requests to Api Gateway</span>
					<input name="num_requests_api" type="number" class="form-control">
				</div>
			</div>
			
			<div class="form-group">
				<div class="input-group">
					<span class="input-group-addon">Include Data transfer costs?</span>
					<input type="checkbox" name="data_transfer_check" value="true">
				</div>
			</div>
			
			<div class="form-group">
				<div class="input-group">
					<span class="input-group-addon">Data Transfer Volume (in GB)</span>
					<input name="data_transfer" type="number" step="any" class="form-control">
				</div>
			</div>
			
			<a href="/" class="btn btn-default">Cancel</a>
			<button type="submit" class="btn btn-success">Analyze</button>
		</form>					
		
	</div>
</body>
</html>