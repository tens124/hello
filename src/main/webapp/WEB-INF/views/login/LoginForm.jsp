<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>로그인</title>
<script src="http://code.jquery.com/jquery-latest.js"></script>
<link rel="stylesheet" href="css/LoginForm.css">
<script src="./js/member.js"></script>

<!-- 로그인 ajax로 처리함 -->
<script>
$(document).ready(function () {
	$("#btn").on("click", function () {
		event.preventDefault();		//submit을 통해 form의 데이터가 제출되지 않도록 막는 함수. login.do는 결과값으로 responseEntitiy를 리턴함
									//해당 객체는 응답의 내용을 담고 있다. 즉, result : Y의 값이 리턴되는 것
									//만약 이 코드가 없다면, 해당 문자열이 우선 랜더링된 후, 아래의 ajax가 실행된다. 그 출력을 막기 위한 코드
		var f = $('#insert_frm');
	    var formData = f.serialize();
	
	    $.ajax({
	        type: "POST",
	        url: f.attr('action'),
	        data: formData,
	        dataType: "json",
	        success: function (data) {
	            // 서버 응답에 따른 처리
	            //return new ResponseEntity<>(response, HttpStatus.OK);를 통해 응답이 이 페이지로 넘어온다
	            if (data.result == "Y") {
	                alert("로그인 성공");
	                location.href = "afterLogin.do";		//리다이렉트를 이전 페이지의 url 값으로 유도한다면? 음... 우선 로그인 로직을 통합해야 할 듯
                } else if (data.result == "N") {
	                alert("로그인 실패");
                    location.href = "NaverLogin.do";	//왜? 그냥 alert만 끄고 그대로 머물면 되지 않나?
	            }
	        },
    	    error: function (data) {
 	           alert("에러 발생!");
            	console.log(data);
        	}
    	});
	});
});
</script>

</head>
   <%@ include file="/WEB-INF/views/common/header.jsp"%>
<body>
   <div class="LoginForm">
      <form method="post" action="login.do" id = "insert_frm">
         <input type="text" placeholder="이메일" class="in" id="mEmail" name="mEmail"> 
         <input type="password" placeholder="비밀번호" class="in" id="mPwd" name="mPwd"> 
         <input type="submit" id="btn" name = "btn" value="로그인"><br> <!-- 이 버튼을 누르면 상단의 함수가 작동! -->
         <input type="button" value="회원가입" onClick="location.href='insertForm.do' " name = "btn" id = "btn">
             <br>
      </form>
         <br>
         <br> 
         
         <!-- 네이버 로그인 버튼 -->
         <%-- <a href="${url }"> 
            <img src="./resources/login_image/naver_login.png" width="183" height="45" alt="Naver로그인">
         </a> --%>
   </div>      
   <br>
   <br>
   <br>
   <br>
   <br>
   <%@ include file="/WEB-INF/views/common/footer.jsp"%>
      
</body>
</html>