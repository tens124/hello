<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script src="http://code.jquery.com/jquery-latest.js"></script>
<link rel="stylesheet" href="css/masterCss.css">
<script src="js/master.js"></script>
<title>상품 정보수정</title>


</head>
	<script src="js/masterProductUpdate.js" />
	
<script>
	function goBack() {
		alert('이전 페이지로 돌아갑니다.');
		history.go(-1);
	}
</script>

<body>

	<%@ include file="../common/masterNav.jsp"%>
	<form method="post" action="masterProductUpdate.do" id="myform">

		<div class="container">
			<div class="table-container">
				<h1 class="h1_caption">상품 정보수정</h1>
				<table>
					<tr>
						<th>상품코드</th>
						<th>이미지</th>
						<th>상품명</th>
						<th>카테고리</th>
						<th>색상</th>
						<th>사이즈</th>
						<th>설명</th>
						<th>등록일</th>
						<th>재고수량</th>
						<th>삭제여부[Y/N]</th>
						<th>수정/삭제</th>
					</tr>
					<tr align="center">
						<td><input type="text" value="${product.pid}" id="pid"
							name="pid" readonly></td>
						<td><img src="images/${product.pimage }" width="50"
							height="50"></td>
						<td><input type="text" value="${product.pname}" id="pname"
							name="pname" maxlength="10" class="phone" autofocus></td>
						<td><select name="cid" class="putsub">
								<option value="맨투맨" selected>맨투맨</option>
								<option value="코트">코트</option>
								<option value="상의">상의</option>
								<option value="하의">하의</option>
								<option value="벨트">벨트</option>
								<option value="악세사리">악세사리</option>
								<option value="양말">양말</option>
						</select></td>
						<td><input type="text" value="${product.pcolor}" id="pcolor"
							name="pcolor"></td>
						<td><input type="text" value="${product.psize}" id="psize"
							name="psize"></td>
						<td><input type="text" value="${product.pcontent }"
							id="pcontent" name="pcontent"></td>


						<td><fmt:formatDate pattern="yyyy-MM-dd" var="formattedDate"
								value="${product.preg}" /> <input type="text"
							value="${formattedDate}" readonly /></td>
						<td><input type="text" value="${amount.acount }"
							name="acount" id="acount"></td>
						<td><input type="text" value="${product.pdrop}" id="pdrop"
							name="pdrop"></td>
						<td>
							<button type="submit">수정</button>
							<button type="button" onclick="goBack();">취소</button>
						</td>
					</tr>
				</table>
			</div>
		</div>

		<br>
	</form>
</body>
</html>