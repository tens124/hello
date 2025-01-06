package boss.controller;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi20;
//scribejava는 OAuth 1.0a 및 OAuth 2.0 인증을 지원하는 Java 기반의 OAuth 클라이언트 라이브러리
//다양한 API 플랫폼(Google, Facebook, Twitter 등)과의 인증 및 인가 과정을 단순화하기 위해 사용. OAuth 인증 절차를 쉽게 구현할 수 있게 해줌

//DefaultApi20는 ScribeJava 라이브러리에서 OAuth 2.0 프로바이더(API 제공자.토큰을 발급하고 인증 과정을 처리하는 주체)를 구현하기 위한 기본 클래스
//OAuth 2.0에 특화된 메소드를 포함하고 있어 이를 확장하여 Google, Facebook, GitHub 등 다양한 API 제공자에 대한 구체적인 구현을 작성할 수 있음
//변수를 넣어 요청을 완성한 후 .build(NaverLoginApi.instance()) 메소드를 통해서 네이버 API와 어울리는 객체를 생산, 이를 네이버 API로 전송
//NaverLoginApi는 현재 파일. 즉 현재 파일은 네이버 로그인 API에 필요한 요소들을 구현해 둔 것
//DefaultApi20 클래스를 확장하여 네이버 API와의 OAuth 2.0 통신을 위한 구체적인 설정을 제공하고 있다

public class NaverLoginApi2 extends DefaultApi20 {

	protected NaverLoginApi2() {
	//싱글톤 패턴을 위해 기본 생성자를 감춰두었음. 외부에서 호출하는 것이 불가능. 이를 통해 객체의 유일함을 보장
		
	//왜 일반적인 싱글톤 생성자의 접근제어자인 private 대신 protected를 사용하는가?
	//protected는 해당 클래스와 같은 패키지에 있는 클래스, 그리고 이 클래스를 상속받는 서브클래스에서 생성자를 호출할 수 있도록 허용
	//따라서 특수한 상황에서 서브클래스 확장을 허용하는 절충안을 제공. 보다 유연한 설계를 위함
	//다양한 API를 사용하여 다양한 프로바이더가 필요하다면 이 클래스를 상속 받아 세부사항을 조금씩 변형하는 것이 가능. ex)네이버 대신 구글 등
		
	//다만... 굳이 이 클래스를 상속받기 보다는 DefaultApi20를 상속받는 별도의 클래스를 새로 설계하는 쪽이 훨씬 직관적이지 않을까?
	//프로젝트 내부에 이 클래스를 상속 받는 또 다른 클래스가 존재하는지 찾아보자
	}

	private static class InstanceHolder {
		private static final NaverLoginApi2 INSTANCE = new NaverLoginApi2();
		//싱글톤 구현. INSTANCE 변수 호출 시 NaverLoginApi 객체를 생성. 해당 객체는 클래스 로딩 시 한 번만 초기화. 또한 변경 불가
		//이 객체는 애플리케이션 전역에서 공유되며, static으로 정의되어 클래스의 인스턴스와 관계없이 접근 가능
		//final을 통해서 해당 이름을 가진 변수의 내용 불변이 보장됨. 즉, INSTANCE라는 변수명에 다른 객체를 할당하는 것이 완전히 불가능해짐
	}

	public static NaverLoginApi2 instance() {
		return InstanceHolder.INSTANCE;
		//싱글톤 호출 메소드. ServiceBuilder().build 메소드에는 싱글톤 객체가 필요
	}

	@Override
	public String getAccessTokenEndpoint() {
		return "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code";
		//DefaultApi20 메소드를 오버라이딩
	}

	@Override
	protected String getAuthorizationBaseUrl() {
		return "https://nid.naver.com/oauth2.0/authorize";
		//DefaultApi20 메소드를 오버라이딩
	}
	
	//위의 두 개 메소드를 포함한 클래스를 싱글톤으로 생성하기 위한 파일. 별도로 DefaultApi20의 기능을 사용할 수 있다. 즉,  OAuth2.0 형태에 어울리는 객체 생성 가능
	//DefaultApi20를 상속 받은 클래스는 서비스빌더 빌더 메소드의 매개변수로 사용할 수 있다. 이를 통해 OAuth20 객체를 생성하는 것이 가능
	//그 객체는 싱글톤이어야 하지만... 빈 등록된 클래스를 대용으로 사용할 수는 없는걸까

}
