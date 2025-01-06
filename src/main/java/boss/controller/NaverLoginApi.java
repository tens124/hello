package boss.controller;

import org.springframework.stereotype.Component;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi20;
//scribejava는 OAuth 1.0a 및 OAuth 2.0 인증을 지원하는 Java 기반의 OAuth 클라이언트 라이브러리
//다양한 API 플랫폼(Google, Facebook, Twitter 등)과의 인증 및 인가 과정을 단순화하기 위해 사용. OAuth 인증 절차를 쉽게 구현할 수 있게 해줌

//DefaultApi20는 ScribeJava 라이브러리에서 OAuth 2.0 프로바이더(API 제공자.토큰을 발급하고 인증 과정을 처리하는 주체)를 구현하기 위한 기본 클래스
//OAuth 2.0에 특화된 메소드를 포함하고 있어 이를 확장하여 Google, Facebook, GitHub 등 다양한 API 제공자에 대한 구체적인 구현을 작성할 수 있음
//변수를 넣어 요청을 완성한 후 .build(NaverLoginApi.instance()) 메소드를 통해서 네이버 API와 어울리는 객체를 생산, 이를 네이버 API로 전송
//NaverLoginApi는 현재 파일. 즉 현재 파일은 네이버 로그인 API에 필요한 요소들을 구현해 둔 것
//DefaultApi20 클래스를 확장하여 네이버 API와의 OAuth 2.0 통신을 위한 구체적인 설정을 제공하고 있다

@Component
public class NaverLoginApi extends DefaultApi20 {
	//이 클래스는 서비스 빌더의 builder 메소드를 통해 OAuth20 객체를 만드는 데 사용된다
	//액세스토큰과 인증url에 사용할 늘 일정한 값을 전달하는 것이 주 목표
	//싱글톤 등의 설계를 사용하거나, 수정한 것처럼 변함 없는 상수를 전달하도록 구현해야 함
	//두 개의 메소드는 DefaultApi20에서 전달된 것
	
	private static final String AccessTokenEndpoint = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code";
	private static final String AuthorizationBaseUrl = "https://nid.naver.com/oauth2.0/authorize";
	

	@Override
	public String getAccessTokenEndpoint() {
		return AccessTokenEndpoint;
		//DefaultApi20 메소드를 오버라이딩
	}

	@Override
	protected String getAuthorizationBaseUrl() {
		return AuthorizationBaseUrl;
		//DefaultApi20 메소드를 오버라이딩
	}
	
	//위의 두 개 메소드를 포함한 클래스를 싱글톤으로 생성하기 위한 파일. 별도로 DefaultApi20의 기능을 사용할 수 있다. 즉,  OAuth2.0 형태에 어울리는 객체 생성 가능
	//DefaultApi20를 상속 받은 클래스는 서비스빌더 빌더 메소드의 매개변수로 사용할 수 있다. 이를 통해 OAuth20 객체를 생성하는 것이 가능
	//그 객체는 싱글톤이어야 하지만... 빈 등록된 클래스를 대용으로 사용할 수는 없는걸까

}
