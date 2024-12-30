<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>내가 쓴 QnA</title>

<link rel="stylesheet" href="css/sidebar.css">

<script>
	function popup() {
		location.href="myPageQnaBoardInsertForm.do?memail=${memail}"
	}
	//qna 상세보기
	function qna(a,b,c) {
		location.href="mypageQnaBoardDetail.do?rnum="+a+"&qid="+b+"&cntPerPage="+c;
	}
	
	//답변글 상세보기
	function reply(a,b,c) {
		location.href="mypageQnaBoardReplyDetail.do?rnum="+a+"&qrid="+b+"&cntPerPage="+c;
	}
	
	function title(){
		var i = document.getElementById("title").value;
		document.write("RE:"+i)
	}
	
	function deleteCheck(abc) {
        if(window.confirm("삭제하시겠습니까?")){
        	location.href="mypageQnaBoardDelete.do?qid="+abc+"&nowPage=${pp.nowPage }&cntPerPage=${pp.cntPerPage }"
        	alert("삭제되었습니다!")		
        }
        event.stopPropagation()
      }
	
</script>

<style type="text/css">
.qna_btn {
	background-color: black;
    color: white;
    padding: 10px;
    display: block;
    margin-left:auto;
    margin-right: 230px;
    width: 150px; /* 버튼을 100%로 설정하여 테이블 셀과 일치시킵니다. */
    white-space: nowrap; /* 텍스트 줄 바꿈 방지 */
}

.qna_btn:hover{
	background-color: #ffffff;
	color: #000000;
}

.container_orders{
width: 100%;
}

.ordertable {
	margin-bottom: 10px !important;
}
</style>
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

		<c:if test="${not empty qlist}">
			<div class="content">
				<h1>내가 쓴 QnA</h1>
					<table border="1" class="ordertable">
						<tr>
							<th>제목</th>
							<th>내용</th>
							<th>첨부파일</th>
							<th>작성일</th>
							<th>관리</th>
						</tr>
						<c:forEach items="${qlist }" varStatus="loop" var="QnaBoard">

							<!-- 문의글 출력 -->
								<tr onclick="javascript:qna(${QnaBoard.rnum },${QnaBoard.qid },${pp.cntPerPage })">
									<td>${QnaBoard.qnatitle }</td>
									<td>${QnaBoard.qnacontent }</td>
									<!-- 첨부파일 -->
									<c:if test="${QnaBoard.qnaorifile != null }">
										<td style="position: relative;">
										<img src="./images/${QnaBoard.qnaorifile }" width="50" height="50"
											class="toggle-image"> <span class="text-on-image">${o.PTEXT}</span>
										</td>
									</c:if>
									<c:if test="${QnaBoard.qnaorifile == null }">
										<td>첨부파일이 없습니다.</td>
									</c:if>

									<td><fmt:formatDate pattern="yyyy-MM-dd hh:mm"
											value="${QnaBoard.qnareg }"></fmt:formatDate></td>
									<td>
										<input type="button" value="문의삭제" class="deleteReview_btn"
										onclick="javascript:deleteCheck(${QnaBoard.qid})">
									</td>
								</tr>

							<!-- 댓글 출력. 댓글의 경우 원본글이 지워지지 않은 상태 + 답변이 완료된 경우에만 출력되어야 한다 -->
							<!-- 우선 풀 아우터 조인을 통해 원본과 댓글 테이블의 데이터를 하나의 컬럼으로 조합한 후, 답변이 완료된 상태라면 원본의 바로 아래에 따라 출력하도록 변경 -->
							<c:if test="${QnaBoard.qnayn =='Y' && QnaBoard.qrdrop != 'Y'}">
								<tr
									onClick="javascript:reply(${QnaBoard.rnum },${QnaBoard.qrid },${pp.cntPerPage })">
									<td colspan="3">ㅤㄴ답변 확인</td>
									<td colspan="2"><fmt:formatDate pattern="yyyy-MM-dd hh:mm"
											value="${QnaBoard.qrreg }"></fmt:formatDate></td>
								</tr>
							</c:if>
						</c:forEach>
					</table>
			</div>
		</c:if>
		

		<c:if test="${empty qlist}">
			<div class="content_noQnA">
				<h1>작성한 QnA글이 없습니다</h1>
			</div>
		</c:if>
		
		<!-- 글 입력 버튼 생성 -->
		<button type="button" class="qna_btn" onclick="javascript:popup()">문의작성</button>
		<c:if test="${empty qlist}">
			<br>
		</c:if>


		<!-- 다른 페이지로 넘어가기 위한 숫자들 자리 -->
		<div align="center">
			<c:if test="${pp.startPage != 1 }">
				<a style="text-decoration: none; color: deeppink"
					href="./mypageQnA.else?nowPage=${pp.startPage - 1 }&cntPerPage=${pp.cntPerPage}">
					<- </a>
			</c:if>
			<c:forEach begin="${pp.startPage }" end="${pp.endPage }" var="p">
				<c:choose>
					<c:when test="${p == pp.nowPage }">
						<b>${p }</b>
					</c:when>
					<c:when test="${p != pp.nowPage }">
						<a style="text-decoration: none; color: deeppink"
							href="./mypageQnA.else?nowPage=${p }&cntPerPage=${pp.cntPerPage}">${p }</a>
					</c:when>
				</c:choose>
			</c:forEach>
			<c:if test="${pp.endPage != pp.lastPage}">
				<a style="text-decoration: none; color: deeppink"
					href="./mypageQnA.else?nowPage=${pp.endPage+1 }&cntPerPage=${pp.cntPerPage}">
					-> </a>
			</c:if>
		</div>


		
	</c:if>
</body>
<%@ include file="/WEB-INF/views/common/footer.jsp"%>
</html>