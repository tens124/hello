<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>로그인 확인</title>
    <script>
        // 모달을 닫고 로그인 페이지로 리다이렉트
        function closeModal() {
            window.location.href = "NaverLogin.do";  // 로그인 페이지로 이동
        }
    </script>
</head>
<body>
    <!-- 모달 창 -->
    <div id="loginModal" style="display:block; background-color: rgba(0, 0, 0, 0.5); position: fixed; top: 0; left: 0; right: 0; bottom: 0; padding-top: 100px; text-align: center;">
        <div style="background: white; padding: 20px; display: inline-block;">
            <h2>${loginMessage}</h2>
            <button onclick="closeModal()">확인</button>
        </div>
    </div>
</body>
</html>