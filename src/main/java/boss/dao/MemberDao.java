package boss.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import boss.model.Member;

@Mapper
public interface MemberDao {
	//매퍼 인터페이스의 경우, 루트컨텍스트의 <mybatis-spring:scan base-package="boss.dao" />를 통해 구현 클래스들이 자동으로 등록됨
	//boss.dao가 자동으로 스캔되고, 마이바티스는 내부의 매퍼 인터페이스들을 인식하고 동적 프록시를 생성해줌
	//이를 통해 MyBatis는 매퍼 메서드를 호출할 때마다 해당 SQL을 실행하도록 프록시 객체를 자동으로 생성. 프록시 객체는 실제 구현체가 아니라, SQL 실행을 담당하는 프록시 메서드로 동작
	
	//1.마이바티스는 sql문이 담긴 xml 파일을 읽는다. <mapper namespace="boss.dao.MemberDao">와 같은 형태로 매퍼 인터페이스와 대응되는 매퍼 xml파일을 지정
	//2.마이바티스는 매퍼 인터페이스의 메소드가 호출될 때마다, 해당 메소드와 동일한 이름의 xml 구문을 찾아낸 후 해당 sql문과 동일한 기능을 수행하는 프록시 객체를 생성
	//<insert id="insertMember" parameterType="member">		insertMember 메소드와 동일한 이름의 insert문. 파라미터타입을 통해 받을 객체를 지정
	//INSERT INTO MEMBER VALUES (#{mEmail}, #{mPwd}, #{mName}, #{mPhone}, 
	//	#{mPost}, #{mAddress}, default, sysdate, default)
	//</insert>
	//3.이 프록시 객체가 구현클래스의 역할을 대신 함. 따라서 호출 시 sql문이 정상적으로 작동됨
	
	// 회원가입
	public int insertMember(Member member);
	
	// 1명의 정보 가져오기
	public Member selectOne(String mEmail);
	
	// 네이버 회원 가입
	public int insertNMember(Member member);
	
	// 회원 정보 수정
	public int updateMember(Member member);
	
	// 회원 탈퇴
	public int deleteMember(String mEmail);
	
	// 카카오 회원 가입
	public int insertKMember(Map<String, Object> map);  
	
}
