<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style type="text/css">
a{
	color: blue;
	text-decoration: none; /* 링크 밑줄 제거 */
}
</style>
</head>
<body>
<div class="category-link" align="center">
						<!-- 상단 카테고리 목록 -->
						<!-- 사실 자동화시키는 것이 가장 좋음. cid는 DB에서 찾을 값/cname은 뷰페이지에 표시될 값으로 설정 -->
						<c:forEach var="c" items="${categoryList }" varStatus="loop">
						<a href="product.do?newCid=${c }"
							style="font-size: 20px; font-weight: bold; margin-right: 10px; text-decoration: none;">${c }</a>
						</c:forEach>
					</div>
					<br>
</body>
</html>