package boss.common;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AfterLoginFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession(false); // 기존 세션만 가져오기
		System.out.println("필터");

		String filterUrl = "";
		
		if (session.getAttribute("afterLogin") != null) {
			filterUrl = ((String) session.getAttribute("afterLogin")).replace("/", "");
			
			session.removeAttribute("afterLogin"); // 재사용 방지를 위해 세션에서 해당 항목 삭제
			String queryString = httpRequest.getQueryString();
			//url과 쿼리스트링을 합침
			if (queryString != null) {
				filterUrl += "?" + queryString;
			}
		}else {
			filterUrl="main.do";
		}
		

		System.out.println(filterUrl);
		httpResponse.sendRedirect(filterUrl);

	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
