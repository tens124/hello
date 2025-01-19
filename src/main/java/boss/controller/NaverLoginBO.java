package boss.controller;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

//빈으로 등록해야 @Value 어노테이션을 통한 값 읽어오기가 가능!
@Component
//@PropertySource("classpath:properties/oauth.properties")	//properties 파일이 어디 있는지 경로를 직접적으로 지정하는 어노테이션
															//web파일에 context:property-placeholder를 설정하는 것과는 어떤 차이가 있을까?
public class NaverLoginBO {

	/* 인증 요청문을 구성하는 파라미터 */
	// client_id: 애플리케이션 등록 후 발급받은 클라이언트 아이디
	// response_type: 인증 과정에 대한 구분값. code로 값이 고정돼 있습니다.
	// redirect_uri: 네이버 로그인 인증의 결과를 전달받을 콜백 URL(URL 인코딩). 애플리케이션을 등록할 때 Callback
	// URL에 설정한 정보입니다.
	// state: 애플리케이션이 생성한 상태 토큰
	
	//앱에서 발생하는 이벤트(로그)를 로깅(기록)하기 위한 객체
	
	//LoggerFactory.getLogger(클래스 인스턴스) 메소드로 지정된 클래스에 대한 Logger 객체를 반환
	//매개변수로 사용되는 클래스에 발생하는 로그를 전부 기록하는 객체가 생성됨. NaverLoginBO의 이벤트가 기록된 객체
	Logger logger = LoggerFactory.getLogger(NaverLoginBO.class);
	
	@Autowired
	NaverLoginApi naver;

	//@Value를 통해 따로 작성한 텍스트 파일에서 값들을 가져옴
	//해당 파일을 암호화하면 보안에 도움이 될 것
	@Value("${CLIENT_ID}")
	private String id; // 클라이언트 아이디
	@Value("${CLIENT_SECRET}")
	private String password; // 클라이언트 비밀번호
	@Value("${REDIRECT_URI}")
	private String uri; // 콜백URL. 현재 서버는 내 컴퓨터이니, ip주소는 localhost
						// 톰캣 설정을 바꿔 내 ip로 연결되게 설정하고, 외부 접근도 허가할 수 있다. 참고
	private final static String SESSION_STATE = "oauth_state";		//모델에 사용할 이름값으로 쓰고 있긴 한데... 굳이 필요한가?

	/* 프로필 조회 API URL */
	private final static String PROFILE_API_URL = "https://openapi.naver.com/v1/nid/me";	//네이버 프로필 조회 요청 URL

	/* 네이버 아이디로 인증 URL 생성 Method */
	public String getAuthorizationUrl(HttpSession session) {
		
		/* 세션 유효성 검증을 위하여 난수를 생성 */
		String state = generateRandomString();		//state는 요청 위조 방지를 위한 값. 여기서 생성한 값을 다음 페이지에서 비교
		
		/* 생성한 난수 값을 session에 저장 */
		setSession(session, state);		//아래 부분에 따로 작성된 메소드가 있음. HttpSession의 메소드가 아님. SESSION_STATE에 state값 저장
		
		/* Scribe에서 제공하는 인증 URL 생성 기능을 이용하여 네이버 아이디 로그인 인증 URL 생성 */
		OAuth20Service oauthService = new ServiceBuilder()		//OAuth 2.0 표준을 따르는 API들은 대부분 동일한 형태의 객체를 사용
				.apiKey(id)								//apikey(client_id 변수) 세팅
				.apiSecret(password)						//apiSecret(client_secret 변수) 세팅
				.callback(uri)							//callback(redirect_uri 변수) 세팅
				.state(state) 									// 앞서 생성한 난수값을 인증 URL생성시 사용. state(state 변수) 세팅
				.build(naver);				//네이버 아이디 로그인에 적합한 객체로 변환. 매개변수로는 늘 같은 값을 참조할 수 있는, default20을 상속한 객체를 포함시켜야 함. 싱글톤 등
		
		System.out.println(id);
		
		//oauthService 객체를 공유할 수 있도록 만들어두면 코드 축약에 도움이 될 것
		return oauthService.getAuthorizationUrl();
	}

	/* 네이버아이디로 Callback 처리 및 AccessToken 획득 Method */
	public OAuth2AccessToken getAccessToken(HttpSession session, String code, String state) throws IOException {
		/* Callback으로 전달받은 세선검증용 난수값과 세션에 저장되어있는 값이 일치하는지 확인 */
		//String sessionState = getSession(session);
		
		// 로그 찍기
		System.out.println("state확인하기 : " + state);
		System.out.println("code확인하기 : " + code);
		
			OAuth20Service oauthService = new ServiceBuilder()		
					.apiKey(id)
					.apiSecret(password)
					.callback(uri)
					.state(state)
					.build(naver);
			/* Scribe에서 제공하는 AccessToken 획득 기능으로 네아로 Access Token을 획득 */
			OAuth2AccessToken accessToken = oauthService.getAccessToken(code);
			return accessToken;
	}

	/* 세션 유효성 검증을 위한 난수 생성기 */
	private String generateRandomString() {
		return UUID.randomUUID().toString();
	}

	/* http session에 데이터 저장 */
	private void setSession(HttpSession session, String state) {
		session.setAttribute(SESSION_STATE, state);
	}

	/* http session에서 데이터 가져오기 */
	private String getSession(HttpSession session) {
		return (String) session.getAttribute(SESSION_STATE);
	}

	/* Access Token을 이용하여 네이버 사용자 프로필 API를 호출 */
	public String getUserProfile(OAuth2AccessToken oauthToken) throws IOException {
		
		OAuth20Service oauthService = new ServiceBuilder()
				.apiKey(id)
				.apiSecret(password)
				.callback(uri)
				.build(naver);
		
		OAuthRequest request = new OAuthRequest(Verb.GET, PROFILE_API_URL, oauthService);
		
		oauthService.signRequest(oauthToken, request);
		Response response = request.send();
		return response.getBody();
	}
	
	
}
