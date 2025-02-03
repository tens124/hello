<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
<meta charset="UTF-8">
<script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
<script src="./js/join.js" charset="utf-8">></script>	<!-- 해당 파일의 js함수들을 사용 가능 -->
<link rel="stylesheet" href="./css/join.css">
<link rel="stylesheet" href="./css/default.css">
   <title>회원가입</title>
   
   <script src="http://dmaps.daum.net/map_js_init/postcode.v2.js"></script>
<script>
   function openDaumPostcode() {
      new daum.Postcode({
         oncomplete : function(data) {   
            document.getElementById('mPost').value = data.zonecode;
            document.getElementById('mAddress').value = data.address;            
         }
      }).open();
   }
</script>
   
   
</head>
<body>
<%@ include file="/WEB-INF/views/common/header.jsp"%>

<div id="container" class="container">
   <div class="content">
      <div class="join_wrap">
         <form id="join_frm" action="insertMember.do" method = "post">
         <!-- form객체의 경우, 내부의 submit 버튼을 누르면 요청이 전송된다 -->
         
            <div class="join_title">회원가입</div>
            
            <div class="join_box" action = "insertMember.do">
               
               <div class="email_auth">
                  <input type="text" placeholder="이메일 (Naver 이메일만 가능)" name="mEmail" id="mEmail" class="email">
                  <button type="button" id="email_auth_btn" class="email_auth_btn">인증번호 받기</button>
                  <!-- email_auth_btn 버튼 클릭 시 join.js 파일의 (".email_auth_btn").click(function () 함수 실행
                  email의 형식 검사 후 ajax 실행. post 형식의 dbCheckEmail.do 요청 전송. 해당 컨트롤러는 N 혹은 Y를 반환 -->
               </div>
               
               <input type="text" placeholder="인증번호 입력" id="email_auth_key">
                  <span id="id_ck" class="dpn"></span>

            <input type="password" placeholder="비밀번호" name="mPwd" id="mPwd">
               <input type="password" placeholder="비밀번호  확인" id="password_ck">   
               
               <input type="text" placeholder="이름" name="mName" id="mName">
               
               <div class="phone_box">
                   <div class="phone_box_item">
                       <input type="text" size="4" id="mPhone" name="mPhone" maxlength="13" placeholder="핸드폰 번호 ( - 없이 숫자만 입력하세요.)">
                   </div>
               </div>
               
               <div class = "post_box">
                        <input type="text" placeholder="우편번호" name="mPost" id="mPost" class="post">
                        <button type=button onClick="openDaumPostcode()" class="post_btn">우편번호 검색</button>
               </div>
                        
                        <input type="text" placeholder="주소" name="mAddress" id="mAddress">
            </div>
            
            <button type="submit" id="join" class="join_btn" >가입하기</button>
            
         </form>
      </div>
   </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp"%>

</body>
</html>