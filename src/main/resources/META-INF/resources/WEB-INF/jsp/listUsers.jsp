<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
	<head>
		<title> Users List Page</title>
	</head>
	<body>
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
	</body>
</html>