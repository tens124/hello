package boss.common;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import boss.model.Member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginCheckInterceptor implements HandlerInterceptor {

	// 자바에서는 default 예약어를 통해 디폴트 메소드를 선언 가능
	// 디폴트 메소드는 기본 구현 메소드가 존재하기에, 구현을 위한 구현메소드를 작성하지 않아도 상속 클래스를 생성 가능
	// 해당 예약어를 통해 인터페이스에도 메소드의 내용을 추가하는 것이 가능해졌음
	// HandlerInterceptor 인터페이스의 메소드들도 디폴트 메소드. 따라서 사용자가 필요한 메소드만 구현하면 됨

	@Override
	public boolean preHandle(HttpServletRequest request, // 요청에 반응하기 위함
			HttpServletResponse response, // 응답을 생성하기 위함
			Object handler // 요청을 처리할 핸들러(컨트롤러의 메서드)의 정보 처리를 위함
							// 인터셉터가 어떤 핸들러가 요청을 처리할 것인지를 알 수 있게 해줌
							// 보다 세밀한 조정을 위해 사용하는 변수로, 현재는 사용되지 않지만 preHandle 메소드는 매개변수 세 개가 필수이므로 포함하는 것

	) throws Exception { // throws : 이 메서드는 어떤 예외를 던질 수 있다는 것을 의미. 즉, 이 메서드를 호출하는 측에서는 예외를 처리하거나 다시 던져야 함
							// Exception은 최상위의 예외 클래스. 이 블록 내부에서 발생하는 모든 예외는 Exception 객체로 가공되어 외부로 전파
							// 따라서 이 인터셉터를 호출하는 곳에서는 Exception을 처리할 구문이 필수적

		System.out.println("인터셉터!");

		// 세션에 member 객체가 없다면 로그인되지 않은 상태로 간주
		if (request.getSession().getAttribute("member") == null || request.getRequestURI().replace("/boss", "")=="Logout.do") {
			
			System.out.println(request.getRequestURI());

			// 현재 세션에 현재 페이지의 url 주소를 저장한 후 공유하는 코드. 다음 페이지에서 받아 사용할 수 있다
			HttpSession session = request.getSession();
			String currentUrl = request.getRequestURI().replace("/boss", "");
			String queryString = request.getQueryString();

			//url과 쿼리스트링을 합침
			if (queryString != null) {
				currentUrl += "?" + queryString;
			}
			
			session.setAttribute("afterLogin", currentUrl);
			System.out.println(currentUrl);
			//이 방식을 사용하기 위해서는 컨트롤러와 서비스의 책임 분배를 반드시 동반해야 한다! 
			//현재 형태로는 모든 경우의 수를 커버하기 위한 로직을 모든 컨트롤러에 넣어줘야 함

			// 로그인 페이지로 리다이렉트
			//리다이렉트VS뷰로 포워딩. 뭐가 더 나은 방식일까?
			response.sendRedirect("loginCheck.do");
//            return false; // 위에서 새로운 요청(리다이렉트)이 생성됐으니 현재 요청 처리 중지
			// 중지하면 안된다! 요청은 계속되어야 함. 그래야 매끄럽게 이어짐
			// sendRedirect 메소드가 문제. 해당 메소드는 새로운 요청을 생성하며 기존의 흐름을 차단해버림
		}

		return true; // 로그인되어 있으면 요청을 계속 진행
	}

}
