package boss.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.scribejava.core.model.OAuth2AccessToken;

import boss.model.Member;
import boss.service.MemberService;

@Controller
public class MemberController {

	@Autowired
	private MemberService service;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Autowired
	Random random;

	/* NaverLoginBO */
	private NaverLoginBO naverLoginBO;
//	private String apiResult = null;

	@Autowired
	private void setNaverLoginBO(NaverLoginBO naverLoginBO) {
		this.naverLoginBO = naverLoginBO;
	}

	// 회원가입 진행하고 ajax 콜백하기
	// 그냥 비밀번호만 암호화 한 후 insert문 작동시키면 끝
	@RequestMapping("insertMember.do")
	public String loginform(Member member, Model model) { // member 클래스(DTO)에는 회원가입 폼에 존재하는 모든 요소들이 프로퍼티로 지정되어 있음. 요청을
															// 받으면 자동으로 매핑되어 객체 생성
															// 스프링에는 자동 바인딩 기능이 존재하여 클라이언트에서 전송한 폼 데이터를 Member 객체의 속성에
															// 자동으로 매핑하는 것이 가능
															// 폼 데이터의 name 속성 값과 객체의 필드명을 기반으로 자동으로 매핑을 수행

		int result = service.newMember(member); // insert 메소드는 성공 시 1을 반환

		model.addAttribute("result", result); // result의 값을 모델객체에 저장. 뷰와 공유

		return "login/insertMemberCheck";
	}

	// 이메일 중복 검사
	// end포인트라는 어노테이션
	@PostMapping("dbCheckEmail.do") // @RequestMapping(method = RequestMethod.POST, path = "dbCheckEmail.do") 와 동일
	@ResponseBody // 해당 메소드가 반환하는 값을 뷰로 해석하지 않고, HTTP 응답 바디(Response Body)에 직접 포함. 즉, 실제 내용 데이터를
					// 리턴하겠다는 뜻
					// JSON 또는 문자열 데이터를 반환할 때 필요
					// 컨트롤러의 메소드에 해당 어노테이션 없이 기본적인 상태 그대로라면 리턴값으로 뷰 파일 경로를 사용. 뷰 리졸버가 자동적으로 매칭해줌
	public String dbCheckEmail(String mEmail) {
		Member member = service.selectOne(mEmail);

		// 사용 불가능(이미 존재하는 이메일)한 이메일이라면 N을, 가능한 메일이라면 Y를 리턴
		// join.js의 js코드에서 이 문자를 통해 조건식을 수행 중
		return member != null ? "N" : "Y";
	}

	// 로그인 첫 화면 요청 메소드 ( 기본 로그인 폼으로 이동 할때 꼭 써야 함 )
	@RequestMapping(value = "NaverLogin.do", method = { RequestMethod.GET, RequestMethod.POST })
	public String login(Model model, HttpSession session, HttpServletRequest request) { // 세션은 서버에 접속한 특정 클라이언트의 정보. 로그인한 유저에 국한된 내용이 아님

		System.out.println(request.getRequestURI());
		String naverAuthUrl = service.naverAuth(session);

		System.out.println("네이버:" + naverAuthUrl);

//		System.out.println(session.getAttribute("afterLogin"));

		model.addAttribute("url", naverAuthUrl); // 생성된 url을 url이라는 이름으로 공유

		return "login/LoginForm";
	}

	// 기본 로그인 기능
	// ajax에서 활용할 값들을 리턴하는 역할!
	@RequestMapping("login.do")
	public ResponseEntity<Map<String, String>> Login(String mEmail, String mPwd, HttpSession session) {
		// responseEntity는 HTTP 응답 데이터와 상태를 명시적으로 설정할 수 있는 객체. 클라이언트에 반환되는 응답의 본문(body),
		// HTTP 상태 코드(status), 헤더(headers)를 자유롭게 제어 가능

		Map<String, String> response = service.login(mEmail, mPwd, session); // responseEntity에 사용될 맵 객체

		return new ResponseEntity<>(response, HttpStatus.OK);
		// body는 맵, Http 상태는 OK
		// 해당 맵엔 result 키와 Y 밸류가 저장되어 있음
		// 상태는 해당 요청이 성공적으로 처리됐다는 걸 의미
		// body는 응답 내용을 의미. result : Y
	}

	// 네이버 로그인 성공시 callback호출 메소드
	// 네이버 로그인 정보를 정보 입력 후 진행된다
	@RequestMapping(value = "/callback.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String callback(Model model, @RequestParam String code, // 네이버 로그인 인증에 성공하면 반환받는 인증 코드. 접근 토큰 발급에 사용
			@RequestParam String state, // 앞에서 생성된 난수값. 비교를 통해 인증 검사
										// 이외에도 error와 error_description 변수가 반환됨. 저장을 따로 할 필요는 없음
			HttpSession session) throws Exception {

		System.out.println("여기는 callback");
		System.out.println(code);

		// 1. 로그인 사용자 정보를 통해 네이버 토큰 생성. 전달된 code와 state, 현재 세션을 조합해 인증토큰을 생성
		OAuth2AccessToken naverToken = service.makeToken(session, code, state);

		String naverProfile = service.makeProfile(naverToken);

		service.makeMember(naverProfile, session);

		// 세션 생성 ( 이건 건들면 안돼 )
//		model.addAttribute("result", apiResult); 왜 있지?
		return "<script>location.href='afterLogin.do';</script>"; // 새 요청을 발생시켜야 뷰리졸버를 거치지 않고 필터에 제대로 걸림
	}
	
	@RequestMapping(value = "saveAfterLogin.do", method = RequestMethod.POST)
	public ResponseEntity<String> saveAfterLogin(@RequestParam("afterLogin") String afterLogin, HttpSession session) {
		
	    // afterLogin에는 클라이언트에서 보낸 currentUrl 값이 들어옴
		// 그 후 해당 url을 세션에 등록, 언제든 사용할 수 있게
	    System.out.println("저장된 URL: " + afterLogin);
	    session.setAttribute("afterLogin", afterLogin);
	    
	    return ResponseEntity.ok("성공");
	    //http 상태는 200번, 매개변수는 응답 body에 들어갈 데이터
	}

	// 이메일 보내는 요청 ( 인증번호 )
	@ResponseBody
	@RequestMapping(value = "emailAuth.do", method = RequestMethod.POST)
	public String emailAuth(String mEmail) {

		// Random random = new Random();
		int checkNum = random.nextInt(888888) + 111111;
		// nextInt(정수)의 경우 0~매개변수 범위 내의 무작위 32비트 정수를 생성하게 됨. 즉, 이 코드는 최소값
		// 111111~999999까지의 랜덤한 정수를 저장

		service.sendEmail(checkNum, mEmail);

		return Integer.toString(checkNum);
	}

	// 회원가입 폼 이동 ( insert )
	@RequestMapping("insertForm.do")
	public String insertMember() {

		return "login/insertForm";
	}

	// 회원수정 폼 이동 (update)
	@RequestMapping("updateForm.else")
	public String updateForm(HttpSession session, Model model) { // 세션, 즉 현재 로그인한 유저의 정보를 가져옴

		Member member = service.getMember(session);

		model.addAttribute("member", member);

		return "login/updateForm";
	}

	// 회원수정
	// 여기의 member 객체는 회원수정 폼에서 입력된 정보가 저장되어 있음! 즉, 아이디 이외의 값들은 수정사항
	@RequestMapping("updateMember.else")
	public String updateForm(Member member, Model model) {

		int result = service.updateMember(member);

		model.addAttribute("result", result);

		return "login/updateMemberCheck";
	}

	// 로그인 상태 확인
	// 인터셉터를 쓰고 있으니 필요 없을지도?
	@RequestMapping("loginCheck.do")
	public String loginCheck(HttpServletRequest request) { // 로그인되지 않은 상태에서 이 URL로 접근하면 모달을 띄우고 로그인 페이지로 리다이렉트
		request.setAttribute("loginMessage", "로그인이 필요합니다!");
		return "login/loginCheck";
	}

	// 로그아웃 세션 종료
	@RequestMapping(value = "Logout.do")
	public String Logout(HttpSession session) {
		session.invalidate(); // 모든 세션을 제거하지만... 싱글톤을 통해 빈을 관리하는 스프링에서도 옳은 방식인가?
		return "redirect:/main.do";
	}

	// 회원 탈퇴 폼 이동
	@RequestMapping("deleteForm.do")
	public String deleteForm() {
		return "login/deleteForm";
	}

	// 회원 탈퇴 ( mPwd 넘어오는데 이미 암호화 된게 넘어옴 )
	@RequestMapping("deleteMember.do")
	@ResponseBody
	public String deleteMember(@RequestParam("mEmail") String mEmail, @RequestParam("mPwd") String mPwd) {
		String result = service.checkMember(mEmail,mPwd);
		
		return result;
	}

	@RequestMapping("afterLogin.do")
	public String afterLogin(HttpServletRequest request, HttpSession session) {

		System.out.println("hello");
		return "redirect:/"+session.getAttribute("afterLogin"); // 로그인 화면 전에 접속하고 있던 페이지로 리다이렉트
	}

}