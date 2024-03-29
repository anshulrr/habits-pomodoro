<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
	<head>
		<link rel="stylesheet" type="text/css" href="webjars/bootstrap/5.1.3/css/bootstrap.min.css">
		<title> Users List Page</title>
	</head>
	<body>
		<div class="container">
			
			<p>Users List</p>
			<table class="table">
				<thead>
					<tr>
						<th>id</th>
						<th>Name</th>
						<th>Email</th>
					</tr>
				</thead>
				<tbody>		
					<c:forEach items="${users}" var="user">
						<tr>
							<td>${user.id}</td>
							<td>${user.name}</td>
							<td>${user.email}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
			
		<script src="" type="text/javascript"></script>
		<script src="" type="text/javascript"></script>
	</body>
</html>