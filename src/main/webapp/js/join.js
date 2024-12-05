/**
 *  회원가입 관련 처리
 */
 


 
 $(function() { 
    
    var email_auth_cd = '';
 
   $('#join').click(function(){
      
      if($('#mEmail').val() == ""){
         alert("이메일을 입력해주세요.");
         return false;
      }
      
      if($('#mPwd').val() == ""){
         alert("비밀번호를 입력해주세요.");
         return false;
      }
      
      if($('#mPwd').val() != $('#password_ck').val()){
         alert("비밀번호가 일치하지 않습니다.");
         return false;
      }
      
      if($('#email_auth_key').val() != email_auth_cd){
         alert("인증번호가 일치하지 않습니다.");
         return false;
      }
   
      fn_join();
   });
   
   function checkEmail(str) {
    const regEmail = /^([0-9a-zA-Z_\.-]+)@([0-9a-zA-Z_-]+)(\.[0-9a-zA-Z_-]+){1,2}$/;

    return regEmail.test(str);
}

$(".email_auth_btn").click(function () {
    var mEmail = $('#mEmail').val();

    if (mEmail === '') {
        alert("이메일을 입력해주세요.");
        return false;
    } else if (!checkEmail(mEmail)) {
        alert("이메일 형식이 잘못되었습니다.");
        $('#mEmail').val('').focus();
        return false;
    } else {
        // 이메일 중복 확인 수행
        $.ajax({
            type: "POST",
            url: "dbCheckEmail.do",
            data: { mEmail: mEmail },
            success: function (response) {
                if (response === "N") {
                    alert("이미 등록된 이메일입니다.");
                    $('#mEmail').val('');
                } else {
                    // 중복되지 않으면 이메일 인증 진행
                    $.ajax({
                        type: "POST",
                        url: "emailAuth.do",
                        data: { mEmail: mEmail },
                        success: function (data) {
                            alert("인증번호가 발송되었습니다.");
                            email_auth_cd = data;
                        },
                        error: function (data) {
                            alert("메일 발송에 실패했습니다.");
                        }
                    });
                }
            },
            error: function (data) {
                alert("이메일 중복 확인에 실패했습니다.");
            }
        });
    }
});
   
   $('#id').focusout(function(){
      var id = $('#id').val();
   
      $.ajax({
         type : "POST",
         url : "idCheck.do",
         data : {id : id},
         success: function(data){
            console.log(data);
            if(data == "Y"){
               $('#id_ck').removeClass("dpn");
            }else{
               $('#id_ck').addClass("dpn");
            }
         },
         error: function(data){
         }
      }); 
   });
   
   
 });