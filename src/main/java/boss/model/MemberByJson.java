package boss.model;

import java.util.Random;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

public class MemberByJson {

	
	BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	//model 폴더는 스프링 컨테이너의 스캔을 받지 않음. 따라서 어노테이션을 이용한 주입은 불가
	
	public Member makeMember(JSONObject json) {
		
		Member member = new Member();	//메소드 내부의 변수로 만들어 둬야 기존 객체가 재활용되는 걸 방지 가능
		
		member.setmEmail((String)json.get("email"));
		member.setmName((String)json.get("name"));
		member.setmPhone(((String)json.get("mobile")).replace("-", ""));	// 010-0000-0000 을 01000000000으로 바꿔서 저장
		
		Random random = new Random();
		int test = random.nextInt(888888) + 111111;
		String pwd = Integer.toString(test);
		member.setmPwd(passwordEncoder.encode(pwd));		//무작위 난수값을 인코딩해 비밀번호에 저장
		
		return member;
		
	}
	
}
