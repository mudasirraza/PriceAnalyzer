<%@ page import="com.google.appengine.api.datastore.Entity" %>

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
		<h1>AWS Lambda Price Analyzer</h1>
		
		<p>&nbsp;</p>
		
		<h3> Data Transfer </h3>
		
		<% Entity e = (Entity) request.getAttribute("data_transfer"); %>

		<form role="form" action="/admin/data_transfer" method="post">
			<div class="form-group">
				<div class="input-group">
					<span class="input-group-addon">Price per GB ($)</span>
					<input name="price_gb" type="number"  step="any" class="form-control">
				</div>
			</div>
			
			<a href="/admin/settings" class="btn btn-default">Cancel</a>
			<button type="submit" class="btn btn-success">Save</button>
		</form>					
		
	</div>
</body>
</html>