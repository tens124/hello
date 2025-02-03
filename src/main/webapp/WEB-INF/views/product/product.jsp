<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!--
	Phantom by HTML5 UP
	html5up.net | @ajlkn
	Free for personal and commercial use under the CCA 3.0 license (html5up.net/license)
-->
<html>
<head>
<title>Phantom by HTML5 UP</title>
<meta charset="utf-8" />
<meta name="viewport"
	content="width=device-width, initial-scale=1, user-scalable=no" />
<link rel="stylesheet" href="assets/css/main.css" />
<noscript>
	<link rel="stylesheet" href="assets/css/noscript.css" />
</noscript>


<script>
	//카테고리와 검색어 중 하나라도 잘못 전달되면(비어있으면) sql문이 제대로 작동하지 않음... 해결한다면 전부 하나로 통합할 수 있을텐데

	//카테고리 검색용
	function enterkey() {
		if (window.event.keyCode == 13) {
			// 엔터키가 눌렸을 때
			var s = document.getElementById("search").value;
			if (s == "") {
				alert("검색어를 입력해주세요");
				return false;
			}
			location.href = "product.do?newCid=${category.newCid}&keyword=" + s;
		}
	}

	//전체 검색용
	function allSearch() {
		if (window.event.keyCode == 13) {
			// 엔터키가 눌렸을 때
			var s = document.getElementById("search").value;
			var sel = document.getElementById('cntPerPage').value;
			if (s == "") {
				alert("검색어를 입력해주세요");
				return false;
			}
			location.href = "product.do?keyword=" + s + "&cntPerPage="
			+ sel;
		}
	}

	//특정 카테고리 리스트용
	function selChange() {

		var sel = document.getElementById('cntPerPage').value;
		location.href = "product.do?keyword=${category.keyword}&nowPage=${pp.nowPage}&newCid=${category.newCid}&cntPerPage="
				+ sel;
	}
	
	//전체 상품 리스트용
	function selChange2() {

		var sel = document.getElementById('cntPerPage').value;
		location.href = "product.do?keyword=${category.keyword}&nowPage=${pp.nowPage}&cntPerPage="
				+ sel;
	}
</script>

<style type="text/css">
.center-div {
	text-align: center;
}
</style>
</head>



<%@include file="/WEB-INF/views/common/chatbot.jsp"%>
<body class="is-preload">

	<!-- Wrapper -->
	<div id="wrapper">

		<!-- Header -->
		<header id="header">
			<div class="inner">

				<!-- 쇼핑몰 로고 & 상단 아이콘 불러오기 -->
				<%@include file="../common/header.jsp"%>

				<!--1. 회원 or 비회원 페이지 -->
				<%@include file="../common/list.jsp"%>
				<div align="center" width="30px" height="100px">
					<c:if test="${not empty category.newCid}">
						<input type="text" maxlength="30"
							placeholder="${category.newCid } 검색" id="search"
							onkeyup="enterkey()">
					</c:if>
					<c:if test="${empty category.newCid}">
						<input type="text" maxlength="30" placeholder="검색어를 입력하세요"
							id="search" onkeyup="allSearch()">
					</c:if>
				</div>

				<c:if test="${not empty list && not empty category.newCid}">
					<div style="float: right;">
						<select id="cntPerPage" name="sel" onchange="selChange()"
							class="selected-five">
							<option value="15"
								<c:if test="${pp.cntPerPage == 15}">selected</c:if>>15개
								보기</option>
							<option value="30"
								<c:if test="${pp.cntPerPage == 30}">selected</c:if>>30개
								보기</option>
							<option value="45"
								<c:if test="${pp.cntPerPage == 45}">selected</c:if>>45개
								보기</option>
						</select>
					</div>
				</c:if>
				<c:if test="${not empty list && empty category.newCid}">
					<div style="float: right;">
						<select id="cntPerPage" name="sel" onchange="selChange2()"
							class="selected-five">
							<option value="15"
								<c:if test="${pp.cntPerPage == 15}">selected</c:if>>15개
								보기</option>
							<option value="30"
								<c:if test="${pp.cntPerPage == 30}">selected</c:if>>30개
								보기</option>
							<option value="45"
								<c:if test="${pp.cntPerPage == 45}">selected</c:if>>45개
								보기</option>
						</select>
					</div>
				</c:if>
				<br>

				<!---------------------- Nav --------------------->
				<nav>
					<ul>
						<li><a href="#menu">Menu</a></li>
					</ul>
				</nav>
			</div>
		</header>
		<!-- Menu -->
		<nav id="menu">
			<h2>Menu</h2>
			<ul>
				<li><a href="product.do" style="text-decoration: none">카테고리</a></li>
				<br>
				<li><a href="freeBoardList.do" style="text-decoration: none">커뮤니티</a></li>
				<br>
				<li><a href="masterNotice.do" style="text-decoration: none">공지사항</a></li>
				<br>
			</ul>
		</nav>
		<!---------------------- Nav ------------------->

		<!-- Main -->
		<div id="main">
			<div class="inner">
				<header align="left">
					<c:if test="${not empty category.newCid}">
						<h1>${category.newCid}</h1>
					</c:if>
				</header>

				<c:if test="${not empty list}">
					<!-- if문을 넣어 해당 카테고리의 상품이 없을 경우 화면 중앙에 '등록된 상품이 없습니다' 출력 -->
					<section class="tiles">
						<c:forEach var="L" items="${list }" varStatus="loop">
							<article class="style1">
								<span class="image"> <img src="images/${L.pimage}" alt=""
									height="450">
								</span> <a href="productDetail.do?pid=${L.pid }">
									<h2>${L.pname }</h2>
									<div class="content">
										<p>${L.pcontent }</p>
									</div>
								</a>

							</article>
						</c:forEach>
					</section>
				</c:if>

				<c:if test="${empty list}">
					<br>
					<br>
					<br>
					<div class="center-div">
						<h1>등록된 상품이 없습니다</h1>
					</div>
				</c:if>


			</div>
			<!-- 다른 페이지로 넘어가기 위한 숫자들 자리 -->
			<div align="center">

				<c:if test="${not empty category.newCid }">
					<c:if test="${pp.startPage != 1 }">
						<a style="text-decoration: none; color: deeppink"
							href="./product.do?newCid=${category.newCid }&keyword=${category.keyword }&nowPage=${pp.startPage - 1 }&cntPerPage=${pp.cntPerPage}">
							<- </a>
					</c:if>
					<c:forEach begin="${pp.startPage }" end="${pp.endPage }" var="p">
						<c:choose>
							<c:when test="${p == pp.nowPage }">
								<b>${p }</b>
							</c:when>
							<c:when test="${p != pp.nowPage }">
								<a style="text-decoration: none; color: deeppink"
									href="./product.do?newCid=${category.newCid }&keyword=${category.keyword }&nowPage=${p }&cntPerPage=${pp.cntPerPage}">${p }</a>
							</c:when>
						</c:choose>
					</c:forEach>
					<c:if test="${pp.endPage != pp.lastPage}">
						<a style="text-decoration: none; color: deeppink"
							href="./product.do?newCid=${category.newCid }&keyword=${category.keyword }&nowPage=${pp.endPage+1 }&cntPerPage=${pp.cntPerPage}">
							-> </a>
					</c:if>
				</c:if>
				<c:if test="${empty category.newCid }">
					<c:if test="${pp.startPage != 1 }">
						<a style="text-decoration: none; color: deeppink"
							href="./product.do?keyword=${category.keyword }&nowPage=${pp.startPage - 1 }&cntPerPage=${pp.cntPerPage}">
							<- </a>
					</c:if>
					<c:forEach begin="${pp.startPage }" end="${pp.endPage }" var="p">
						<c:choose>
							<c:when test="${p == pp.nowPage }">
								<b>${p }</b>
							</c:when>
							<c:when test="${p != pp.nowPage }">
								<a style="text-decoration: none; color: deeppink"
									href="./product.do?keyword=${category.keyword }&nowPage=${p }&cntPerPage=${pp.cntPerPage}">${p }</a>
							</c:when>
						</c:choose>
					</c:forEach>
					<c:if test="${pp.endPage != pp.lastPage}">
						<a style="text-decoration: none; color: deeppink"
							href="./product.do?keyword=${category.keyword }&nowPage=${pp.endPage+1 }&cntPerPage=${pp.cntPerPage}">
							-> </a>
					</c:if>
				</c:if>

			</div>

		</div>




		<!-- Footer -->
		<footer id="footer">
			<div class="inner">
				<section>
					<h2>Get in touch</h2>
					<form method="post" action="#">
						<div class="fields">
							<div class="field half">
								<input type="text" name="name" id="name" placeholder="Name" />
							</div>
							<div class="field half">
								<input type="email" name="email" id="email" placeholder="Email" />
							</div>
							<div class="field">
								<textarea name="message" id="message" placeholder="Message"></textarea>
							</div>
						</div>
						<ul class="actions">
							<li><input type="submit" value="Send" class="primary" /></li>
						</ul>
					</form>
				</section>
				<section>
					<h2>Follow</h2>
					<ul class="icons">
						<li><a href="#" class="icon brands style2 fa-twitter"><span
								class="label">Twitter</span></a></li>
						<li><a href="#" class="icon brands style2 fa-facebook-f"><span
								class="label">Facebook</span></a></li>
						<li><a href="#" class="icon brands style2 fa-instagram"><span
								class="label">Instagram</span></a></li>
						<li><a href="#" class="icon brands style2 fa-dribbble"><span
								class="label">Dribbble</span></a></li>
						<li><a href="#" class="icon brands style2 fa-github"><span
								class="label">GitHub</span></a></li>
						<li><a href="#" class="icon brands style2 fa-500px"><span
								class="label">500px</span></a></li>
						<li><a href="#" class="icon solid style2 fa-phone"><span
								class="label">Phone</span></a></li>
						<li><a href="#" class="icon solid style2 fa-envelope"><span
								class="label">Email</span></a></li>
					</ul>
				</section>
				<ul class="copyright">
					<li>&copy; Untitled. All rights reserved</li>
					<li>Design: <a href="http://html5up.net">HTML5 UP</a></li>
				</ul>
			</div>
		</footer>

	</div>
	<%@ include file="../common/footer.jsp"%>
	<!-- Scripts -->
	<script src="assets/js/jquery.min.js"></script>
	<script src="assets/js/browser.min.js"></script>
	<script src="assets/js/breakpoints.min.js"></script>
	<script src="assets/js/util.js"></script>
	<script src="assets/js/main.js"></script>

</body>
</html>