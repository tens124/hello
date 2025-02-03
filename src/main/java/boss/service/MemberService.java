package boss.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;

import boss.controller.NaverLoginBO;
import boss.dao.MemberDao;
import boss.model.Member;
import boss.model.MemberByJson;

@Service
public class MemberService {

	@Autowired
	private MemberDao dao;

	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	private NaverLoginBO naverLoginBO;

	@Autowired
	private void setNaverLoginBO(NaverLoginBO naverLoginBO) {
		this.naverLoginBO = naverLoginBO;
	}
	
	@Autowired
	private JavaMailSender mailSender;

	// 회원 가입
	public int insertMember(Member member) {
		return dao.insertMember(member);
		// 인터페이스의 메소드를 호출 후 결과 반환
	}

	// 회원 1명의 정보 가져오기
	public Member selectOne(String mEmail) {
		return dao.selectOne(mEmail);
	}

	// 네이버 회원가입
//	public int insertNMember(Map<String, Object> map) {
//		return dao.insertNMember(member);
//	}

	public int updateMember(Member member) {
		
		int result = 0;
		
		String mEmail = member.getmEmail();

		//기존의 회원 정보를 이용하여 데이터 조회
		Member dbmember = dao.selectOne(mEmail);

		// 비번 비교
		//일치 시 update sql문이 작동할 것이고, 성공한다면 해당 메소드는 1을 반환하게 돼있음
		if (passwordEncoder.matches(member.getmPwd(), dbmember.getmPwd())) {
			
			member.setmPwd(passwordEncoder.encode(member.getmPwd()));	//비밀번호는 다시 암호화해야 함
			
			result = dao.updateMember(member);
		}
		
		return result;
	}

	public int deleteMember(String mEmail) {
		return dao.deleteMember(mEmail);
	}

	public int insertKMember(Map<String, Object> map) {
		return dao.insertKMember(map);
	}

	public int newMember(Member member) {

		// member 폼에서 넘어온 값을 암호화
		String encpassword = passwordEncoder.encode(member.getmPwd()); // 게터메소드를 통해 비밀번호 값을 가져온 후 암호화

		member.setmPwd(encpassword); // db에 넣을 member password 를 암호화 한걸로 넣기

		return dao.insertMember(member);

	}

	/* 네이버아이디로 인증 URL을 생성하기 위하여 naverLoginBO클래스의 getAuthorizationUrl메소드 호출 */
	public String naverAuth(HttpSession session) {

		// https://nid.naver.com/oauth2.0/authorize라는 요청에 변수들을 같이 보내서 네이버 로그인을 요청
		// response_type=code 인증 과정에 대한 내부 구분값. code 고정
		// &client_id=mo1HJXGXT3n7A3XV59QM 클라이언트 아이디. 네이버에서 발급받은 값
		// &redirect_uri=http%3A%2F%2F211.63.89.90%3A8090%2Flogin_project%2Fcallback 로그인
		// 시 넘어갈 주소값
		// &state=e68c269c-5ba9-4c31-85da-54c16c658125 애플리케이션에서 생성한 상태 토큰값. URL 인코딩을 적용한
		// 값을 사용. 요청 위조 공격 방지용
		return naverLoginBO.getAuthorizationUrl(session);
	}

	public OAuth2AccessToken makeToken(HttpSession session, String code, String state) throws Exception {

		// 접근 토큰 변수. OAuth2.0 형식
		// 전달된 code와 state, 현재 세션을 조합해 인증토큰을 생성
		return naverLoginBO.getAccessToken(session, code, state);
	}

	public String makeProfile(OAuth2AccessToken navarToken) throws Exception {

		return naverLoginBO.getUserProfile(navarToken);
	}

	public void makeMember(String profile, HttpSession session) throws ParseException {
		// TODO Auto-generated method stub

		// 1. String형식인 profile을 json형태로 바꿈 ( pom.xml 에 해당 의존성 등록되어 있음 )
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(profile);
		JSONObject jsonObj = (JSONObject) obj; // json 형태로 변환됨. json은 기본적으로 문자열! String보다 파싱을 쉽게 만들어둔 것. 즉, 라이브러리 간 데이터
												// 호환 가능
		
		System.out.println(jsonObj);

		/**
		 * profile json 구조 
		 * { "resultcode":"00", 
		 * 	 "message":"success", 
		 * 	 "response":{ //네이버 api에서 어떤 항목을 저장할지 지정할 수 있음!
		 *  			 "id":"33666449", 
		 *  			 "nickname":"shinn****",
		 * 				 "age":"20-29", 
		 * 				 "gender":"M", 
		 * 				 "email":"sh@naver.com",
		 * 				 "name":"\uc2e0\ubc94\ud638" 
		 * 				} 
		 * 	}
		 **/

		// 2. 데이터 파싱
		// Member DTO와 해당 DTO를 구성해주는 객체를 따로 장만하여 활용하는 것이 낫지 않을까?
		// 맵 VS DTO. 각자의 장단은?

		// response 파싱. response 부분의 id, nickname,age 등의 정보를 가져와 저장
		JSONObject response_obj = parseResponse(jsonObj);
		System.out.println(response_obj);
		
		MemberByJson json = new MemberByJson();
		Member member = json.makeMember(response_obj);

		// response의 name, email, mobile
		String mName = member.getmName();
		String mEmail = member.getmEmail();
		String nPhone = member.getmPhone();

		// 여기서 이메일과 데이터베이스를 비교해보자
		Member checkmember = dao.selectOne(mEmail);

		//DB에 해당 회원 정보가 있다면
		if (checkmember != null) {
			session.setAttribute("member", checkmember);
		} else {

			// 네이버 회원 가입
			int loginresult = dao.insertNMember(member);

			if (loginresult == 1) {
				// 네이버 회원 member 불러오기
				member = dao.selectOne(mEmail);
				// 네이버 세션 올리기
				session.setAttribute("member", member);
			}

		}

	}

	private JSONObject parseResponse(JSONObject jsonObj) {

		JSONObject response = (JSONObject) jsonObj.get("response");

		return response;
	}

	public void sendEmail(int checkNum, String mEmail) {

		/* 이메일 보내기 */
		String setFrom = "holol124@naver.com"; // 이메일을 보내는 사람의 주소 ( 유효해야함 )
		String toMail = mEmail; // 회원가입 폼에서 쓴 email 매개변수로 받았음
		String title = "회원가입 인증 이메일 입니다."; // 제목
		String content = "홈페이지를 방문해주셔서 감사합니다." + "<br><br>" + "인증 번호는 " + checkNum + "입니다." + "<br><br>" // 내용
				+ "해당 인증번호를 인증번호 확인란에 기입하여 주세요.";

		try {
			MimeMessage message = mailSender.createMimeMessage(); // MimeMessage 객체를 통해 MIME 포맷의 메세지를 저장
																	// 메세지 객체를 매개변수로 받아 생성된 헬퍼 객체가 메세지 객체의 편집을 돕는다
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8"); // 논리값은 멀티파트 활성화를 의미. 멀티파트는 여러 종류의
																						// 메시지를 포함할 수 있다는 의미
																						// 즉, 헬퍼 객체에 이미지, html문서 등 여러
																						// 포맷으로 설정된 메세지를 동시에 저장시킨 후,
																						// 수신자의 컴퓨터 환경에 따라 특정한 메세지를 선정하여
																						// 보여줄 수 있게 됨
																						// 이 코드에서는 셋텍스트 메소드가 한 번 사용되어
																						// content가 html 문서임을 명시해뒀음
																						// 따라서 수신자는 html로 랜더링된 content
																						// 텍스트를 받게 될 것임
			helper.setFrom(setFrom); // 출발 주소 설정
			helper.setTo(toMail); // 도착 주소 설정
			helper.setSubject(title); // 제목 설정
			helper.setText(content, true); // false를 입력하면 html 랜더링 대신 content의 내용이 그대로 출력될 것
			mailSender.send(message); // 헬퍼에 의해 편집된 메세지 객체 전송
			System.out.println("전송!");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("실패!");
		}
	}

	public Member getMember(HttpSession session) {

		// 세션에 있는 Member 값 구해오기
		Member dbMember = (Member) session.getAttribute("member");

		String mEmail = dbMember.getmEmail();

		Member member = dao.selectOne(mEmail);
		
		return member;
	}

	public Map<String, String> login(String mEmail, String mPwd, HttpSession session) {

		System.out.println(mEmail);
		
		Map<String, String> response = new HashMap<String, String>();

		Member dbmember = dao.selectOne(mEmail); // 입력된 아이디 값을 통해 member 객체를 불러옴

		if (dbmember != null && passwordEncoder.matches(mPwd, dbmember.getmPwd())) { // 해당 아이디로 된 유저가 존재하고, 비밀번호가 맞다면
			response.put("result", "Y"); // 맵 객체에 result 키와 Y 밸류를 삽입
			session.setAttribute("member", dbmember); // member라는 이름으로 유저 정보를 세션에 저장
		} else {
			response.put("result", "N");
		}
		return response;
	}

	public String checkMember(String mEmail, String mPwd) {
		// TODO Auto-generated method stub
		Member member = dao.selectOne(mEmail);
		int result = 0;
		
		System.out.println(mPwd);
		System.out.println(member.getmPwd());

		if (member != null) {
			if (passwordEncoder.matches(mPwd, member.getmPwd())) {	//왜 인코딩을 안하지? 내가 입력한 건 비밀번호 그대로지만 db에는 암호화된 코드인데
				result = dao.deleteMember(mEmail);
			}
		}

		System.out.println("result : " + result);

		if (result == 1) {
			return "Y";
		} else {
			return "N";
		}
		
	}

}
