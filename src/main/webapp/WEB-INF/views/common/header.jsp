<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, user-scalable=no" />

<!-- lottie -->
<!-- <script
	src="https://unpkg.com/@lottiefiles/lottie-player@latest/dist/lottie-player.js"></script> -->
<script type="text/javascript">
document.addEventListener("DOMContentLoaded", function () {		//DOMContentLoaded : DOM이 완전히 로드된 후를 의미. 리스너가 DOM이 완전히 로드된 후에 실행되도록 설정
																//DOM이 완전히 로드된 후 함수가 작동
    // 로그인 링크 클릭 이벤트 처리
    document.getElementById("login")	//id가 login인 객체를 가져옴
    .addEventListener("click", function(event) {	//클릭 시 실행되는 함수를 작성. 매개변수는 해당 이벤트. 즉, login 클릭
    	
    	console.log("로그인 클릭됨!");
    
        event.preventDefault();  // 해당 객체의 기본 동작을 막음. <a>의 역할은 페이지 이동. 그 동작을 정지시킨다. 뒤의 ajax를 우선 실행시키기 위함
        
        let targetUrl = this.getAttribute("href");  // 클릭한 <a> 태그의 href 값을 가져옴
        
        let currentUrl = window.location.pathname.replace("/boss/","") 	//현재 페이지의 요청명에서 프로젝트 링크를 제거한 값
        				 + window.location.search;	//현재 페이지의 쿼리스트링. 두 값을 더해 온전한 형태를 만든다
        				 
        console.log("현재 URL: " + currentUrl);
        console.log("목표 URL: " + targetUrl);
        
        let xhr = new XMLHttpRequest();	//XMLHttpRequest는 xml 이외의 다양한 자료형태를 전송 가능한 자바스크립트 API
        let message = "afterLogin="+currentUrl
      
        xhr.open('post', 'saveAfterLogin.do', true);	//요청을 보낼 방식, 주소, 비동기여부 설정
     
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');	
        // 요청의 Content-Type을 설정해서 전송하는 데이터 형식을 알려줘야 함
        //application/x-www-form-urlencoded는 인코딩 방식 중 하나. 인코딩 시 데이터를 key=value의 형태로 가공한다
        //즉, 전송할 데이터가 그 결과물인 key=value의 형태라는 것을 명시하도록 설정
        //@requestParam 어노테이션은 key=value 형태의 데이터를 받아 저장하는 방식. 단순히 문자열에 =을 추가하는 방식으로는 인식하지 못함
        //따라서 let message 변수 설정으로 ~=~ 형태의 문자열을 만들고, 컨텐츠타입 설정을 통해 해당 문자열을 key=value 형식으로 인식시켜야 함
        
        xhr.send(message); // messgae 변수를 key=value 형태로 포함시켜 xhr 요청을 전송
        
        xhr.onload = function() {
        //xhr.onload는 서버로부터 응답이 성공적으로 돌아왔을 때 실행되는 이벤트 핸들러
            if (xhr.status === 200) {	// 서버에서 성공적으로 응답 받으면. 즉, http 상태가 200이라면
                
                console.log("세션 저장 완료:", xhr.responseText);
                window.location.href = targetUrl; // 원래 클릭한 페이지로 이동
            } else {
                // 오류 처리
                console.log("세션 저장 실패");
                alert("오류 발생!");
            }
        };
        
    });
});

</script>

</head>

<body class=" ">
	<header id="header">
		<div class="inner">
			<div>
				<a href="main.do" class="logo"> <span class=" "><img
						src="images/logo.png" alt="" style="width: 200px; height: 100px;"></span>
					<!-- 			<iframe --> <!-- 				src="https://lottie.host/embed/22e21116-1727-42ae-b8a2-432c6a04b253/FoDhFmOjkH.json"></iframe> -->
					<!--<div>JY & HB</div>  -->
				</a>
			</div>
			<div style="text-align: right; padding-right: 30px;">
				<!--1. 비회원 페이지 -->
				<c:if test="${empty sessionScope.member}">
					<!-- 로그인되지 않은 상태라면 -->
					<a href="NaverLogin.do" id="login"><img src="./images/login.png"
						style="text-decoration: none"></a>
					<!-- 네이버 로그인 -->
					<a href="insertForm.do"><img src="./images/join.png"
						style="text-decoration: none"></a>
					<!-- 회원가입 -->
					<br>
				</c:if>

				<!--2.관리자페이지  -->
				<c:if
					test="${!empty sessionScope.member && sessionScope.member.mEmail eq 'boss'}">
					<a href="Logout.do" onclick="alert('로그아웃')"><img
						src="./images/logout.png" style="text-decoration: none"></a>
					<a href="mypage.else"><img src="./images/my.png"
						style="text-decoration: none"></a>
					<a href="cartFormMove.else"><img src="./images/cart.png"
						style="text-decoration: none"></a>
					<a href="masterMain.do"><img src="./images/master.png"
						style="text-decoration: none"></a>
					<br>
					${sessionScope.member.mName }님 환영합니다.
		<!--	<a href="productInsertForm.do" onclick="alert('상품등록')"
				style="text-decoration: none"><br>상품등록</a>
			<input type="button" value="관리자페이지"
				onclick="location.href='masterMain.do'"> -->
					<br>
				</c:if>

				<!-- 3.회원페이지 -->
				<c:if
					test="${!empty sessionScope.member && sessionScope.member.mEmail ne 'boss'}">
					<a href="Logout.do" onclick="alert('로그아웃')"><img
						src="./images/logout.png" style="text-decoration: none"></a>
					<a href="mypage.else"><img src="./images/my.png"
						style="text-decoration: none"></a>
					<a href="cartFormMove.else"><img src="./images/cart.png"
						style="text-decoration: none"></a>
					<br>
				${sessionScope.member.mName }님 환영합니다.
				<br>
				</c:if>
			</div>
		</div>
	</header>
	
	


</body>
</html>