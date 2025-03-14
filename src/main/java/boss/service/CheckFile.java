package boss.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
//파일을 검사 및 업로드하기 위한 클래스
public class CheckFile {

	// 파일 크기 & 확장자 검증
	int validateFile(MultipartFile mf) {

		if (mf.getSize() > 1000000)
			return 2; // 1MB 초과

		if (!isValidExtension(mf)) // 확장자 검층 메소드 호출
			return 3; // 잘못된 확장자

		return 0; // 유효한 파일
	}

	// 확장자 검증
	boolean isValidExtension(MultipartFile mf) {
		String extension = mf.getOriginalFilename().toLowerCase(); // 문자열 전체를 소문자로 변환. 대문자 확장자도 소문자로 변환 가능

		return extension.endsWith(".jpg") || extension.endsWith(".jpeg") || extension.endsWith(".png")
				|| extension.endsWith(".gif");
		// String의 enndsWith 메소드는 문자열이 매개변수로 끝날 경우 true를 반환
	}

	// 중복 방지를 위한 랜덤 파일명 생성 (UUID 사용)
	String generateFileName(MultipartFile mf) {

		// mf의 파일 이름을 알아낸 후, substring으로 확장자를 분리
		// substring은 문자열을 앞에서부터 세서, 매개변수 만큼의 문자를 제거
		// lastIndexOf는 해당 문자열에서 마지막에 존재하는 매개변수를 찾아낸 후, 그 위치를 숫자로 반환
		// 즉, 최종적으로 .이 몇 번째 위치에 있는지 계산 후, 그만큼의 문자를 전부 제거해버리는 코드
		String extension = mf.getOriginalFilename().substring(mf.getOriginalFilename().lastIndexOf("."));

		return UUID.randomUUID().toString() + extension;
	}

	// 파일 업로드
	void uploadFile(MultipartFile mf, String fileName, HttpServletRequest request) throws IOException {

		String path = request.getRealPath("images");

		mf.transferTo(new File(path + "/" + fileName)); // 파일 저장
	}
}
