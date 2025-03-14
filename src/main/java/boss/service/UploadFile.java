package boss.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UploadFile {

	// 파일 업로드
	//파일 이름이 필요한 순간도 있으니 String을 반환하고는 있지만...
	//파일 이름을 리턴하는 메소드를 따로 작성하는 게 좋을까?
	String uploadFile(MultipartFile mf, HttpServletRequest request) throws IOException {

		String fileName = generateFileName(mf);

		String path = request.getRealPath("images");

		mf.transferTo(new File(path + "/" + fileName)); // 파일 저장
		
		return fileName;
	}

	// 중복 방지를 위한 랜덤 파일명 생성 (UUID 사용)
	private String generateFileName(MultipartFile mf) {

		// mf의 파일 이름을 알아낸 후, substring으로 확장자를 분리
		// substring은 문자열을 앞에서부터 세서, 매개변수 만큼의 문자를 제거
		// lastIndexOf는 해당 문자열에서 마지막에 존재하는 매개변수를 찾아낸 후, 그 위치를 숫자로 반환
		// 즉, 최종적으로 .이 몇 번째 위치에 있는지 계산 후, 그만큼의 문자를 전부 제거해버리는 코드
		String extension = mf.getOriginalFilename().substring(mf.getOriginalFilename().lastIndexOf("."));

		return UUID.randomUUID().toString() + extension;
	}
}
