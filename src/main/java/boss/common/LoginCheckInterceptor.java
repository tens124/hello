package boss.common;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import boss.model.Member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginCheckInterceptor implements HandlerInterceptor {
	
	//Spring에서는 HandlerInterceptorAdapter라는 추상 클래스가 기본 제공됨 
	//이 클래스는 HandlerInterceptor 인터페이스를 구현하고 있으며, 그 안에 preHandle, postHandle, afterCompletion 메소드가 기본적으로 구현되어 있다
	//따라서 이 클래스가 HandlerInterceptor를 상속하면서 preHandle 메소드만을 구현하고 있음에도 오류가 발생하지 않는 것
	//이처럼 스프링은 인터페이스에 대응되는 다양한 기본클래스가 존재하여 인터페이스 사용의 부담을 덜어주고 있다

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
    	
        // 세션에서 "member" 속성을 확인 후 객체 생성
        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute("member");

        // 세션에 member 객체가 없다면 로그인되지 않은 상태로 간주
        if (member == null) {
            // 로그인 페이지로 리다이렉트
            response.sendRedirect("loginCheck.do");
            return false; // 위에서 새로운 요청(리다이렉트)이 생성됐으니 현재 요청 처리 중지
        }
        	
        return true;	// 로그인되어 있으면 요청을 계속 진행
    } 
}
