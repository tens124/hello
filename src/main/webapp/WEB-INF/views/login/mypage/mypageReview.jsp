<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>내가 쓴 리뷰</title>

<link rel="stylesheet" href="css/sidebar.css">
<script src="http://code.jquery.com/jquery-latest.js"></script>
<script>
	function doDetailPage(pid){
		location.href = "productDetail.do?pid=" + pid;
	}
	
	function deleteReview(rid) {
	   var confirmDelete = confirm("진짜 삭제하시겠습니까?");
	   if(confirmDelete){
		   
		$.ajax({
	        type: "POST",
	        url: "mypageDeleteReview.do",
	        data: { rid: rid },
	        success: function (response) {
	            if (response === "Y") {
	                alert("리뷰가 삭제 되었습니다.");
	                location.href = "mypageReview.else";
	            } else {
	                alert("리뷰 삭제에 실패했습니다.");
	                location.href = "mypageReview.else";
	            }
	        },
	        error: function () {
	            alert("서버 오류가 발생했습니다.");
	        }
	    });
	   }
	}
</script>

</head>
<body>
	<%@ include file="/WEB-INF/views/common/header.jsp"%>

	<!-- 세션이 없을때 마이페이지 -->
	<c:if test="${member == null }">
		<script>
			alert("로그인이 되어 있지 않습니다.");
			location.href = "NaverLogin.do";
		</script>
	</c:if>

	<c:if test="${member != null }">
		<div class="main_container">
			<div class="nav1">
				<ul>
					<li><a class="mypage_sidebar" href='#'>메뉴</a></li>
					<li><a href='mypage.else'>내 주문내역</a></li>
					<li><a href='cartFormMove.else'>장바구니</a></li>
					<li><a href='mypageQnA.else'>내가 쓴 QnA</a></li>
					<li><a href='mypageReview.else'>내가 쓴 Review</a></li>
					<li><a href='mypageAskBoard.else'>내가 물어본 상품문의</a></li>
					<li><a href='mypageReport.else'>신고 기록</a></li>
					<li><a href='updateForm.else'>내 정보 수정</a></li>
					<li><a href='deleteForm.do'>회원 탈퇴</a></li>
				</ul>
			</div>
			<c:if test="${not empty rlist}">
				<div class="content">
					<h1>내가 쓴 리뷰</h1>
					<div class="container_review">
						<table border="1" class="reviewtable">
							<tr>
								<th>리뷰 제목</th>
								<th>내용</th>
								<th>이미지</th>
								<th>작성일</th>
								<th>리뷰 삭제</th>
							</tr>

							<c:forEach items="${rlist }" varStatus="loop" var="review">
								<tr>
									<td onclick="doDetailPage(${review.pid})">${review.rtitle }</td>
									<td onclick="doDetailPage(${review.pid})">${review.rcontent }</td>
									<c:if test="${not empty review.rimage }">
										<td style="position: relative;"
											onclick="doDetailPage(${review.pid})"><img
											src="./images/${review.rimage }" width="50" height="50"
											class="toggle-image"> <span class="text-on-image">${o.PTEXT}</span>
										</td>
									</c:if>
									<c:if test="${empty review.rimage }">
										<td onclick="doDetailPage(${review.pid})">x</td>
									</c:if>
									<fmt:formatDate value="${review.rreg }" pattern="yyyy년 MM월 dd일"
										var="formattedDate" />
									<td onclick="doDetailPage(${review.pid})">${formattedDate}</td>
									<td><button class="deleteReview_btn"
											onclick="deleteReview(${review.rid})">리뷰삭제</button></td>
								</tr>
							</c:forEach>
						</table>
					</div>
					<!-- container_review end -->
				</div>

			</c:if>
		</div>
		<c:if test="${empty rlist}">
			<div class="content_noreview">
				<h1>작성한 리뷰글이 없습니다.</h1>
			</div>
		</c:if>
	</c:if>
	<!-- 멤버 세션 조건문 -->
</body>
<%@ include file="/WEB-INF/views/common/footer.jsp"%>
</html>