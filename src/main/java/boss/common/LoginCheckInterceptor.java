package boss.common;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import boss.model.Member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginCheckInterceptor implements HandlerInterceptor {
	
	//자바에서는 default 예약어를 통해 디폴트 메소드를 선언 가능
	//디폴트 메소드는 기본 구현 메소드가 존재하기에, 구현을 위한 구현메소드를 작성하지 않아도 상속 클래스를 생성 가능
	//해당 예약어를 통해 인터페이스에도 메소드의 내용을 추가하는 것이 가능해졌음
	//HandlerInterceptor 인터페이스의 메소드들도 디폴트 메소드. 따라서 사용자가 필요한 메소드만 구현하면 됨

    @Override
    public boolean preHandle(HttpServletRequest request, 		//요청에 반응하기 위함
    						 HttpServletResponse response, 		//응답을 생성하기 위함
    						 Object handler						//요청을 처리할 핸들러(컨트롤러의 메서드)의 정보 처리를 위함
    						 									//인터셉터가 어떤 핸들러가 요청을 처리할 것인지를 알 수 있게 해줌
    						 									//보다 세밀한 조정을 위해 사용하는 변수로, 현재는 사용되지 않지만 preHandle 메소드는 매개변수 세 개가 필수이므로 포함하는 것
    						 
    						 ) throws Exception {				//throws : 이 메서드는 어떤 예외를 던질 수 있다는 것을 의미. 즉, 이 메서드를 호출하는 측에서는 예외를 처리하거나 다시 던져야 함
    															//Exception은 최상위의 예외 클래스. 이 블록 내부에서 발생하는 모든 예외는 Exception 객체로 가공되어 외부로 전파
    															//따라서 이 인터셉터를 호출하는 곳에서는 Exception을 처리할 구문이 필수적
    	
    	System.out.println("인터셉터!");
    	
        // 세션에 member 객체가 없다면 로그인되지 않은 상태로 간주
        if (request.getSession().getAttribute("member") == null) {
            // 로그인 페이지로 리다이렉트
            response.sendRedirect("loginCheck.do");
            return false; // 위에서 새로운 요청(리다이렉트)이 생성됐으니 현재 요청 처리 중지
        }
        	
        return true;	// 로그인되어 있으면 요청을 계속 진행
    } 
}
