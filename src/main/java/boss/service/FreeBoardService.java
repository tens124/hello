package boss.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import boss.dao.FreeBoardDao;
import boss.model.FreeBoard;

@Service
public class FreeBoardService {

	@Autowired
	private FreeBoardDao fdao;
	
	@Autowired
	private CheckFile cf;
	
	@Autowired
	private UploadFile uf;

	public int insert(FreeBoard board) {
		return fdao.insert(board);
	}

	public int freeBoardListCount(FreeBoard board) {
		return fdao.listcount(board);
	}

	public int setLike(FreeBoard board) {
		return fdao.setLike(board);
	}

	public List<FreeBoard> freeBoardList(FreeBoard board) {
		return fdao.selectList(board);
	}

	public void readcount(int fId) {
		fdao.readcount(fId);
	}

	public FreeBoard getDetail(int fId) {
		return fdao.getDetail(fId);
	}

	public int update(FreeBoard board) {
		return fdao.update(board);

	}

	public int delete(int fId) {
		return fdao.delete(fId);
	}

	// 메소드는 최소한의 기능만 수행할 수 있도록 하자!
	public int insert(FreeBoard board, MultipartFile mf, HttpServletRequest request)
			throws IllegalStateException, IOException {

		if (!mf.isEmpty()) { // 첨부파일이 존재하는 경우. 파일 검증 후 해당 파일 이름을 board 객체에 등록
			
			//result 0 : 올바른 파일  1 : 게시글 등록 성공  2 : 크기 초과  3 : 확장자 오류
			
			int result = cf.validateFile(mf);	//파일 크기 및 확장자를 확인하여 결과코드를 저장
			
			if (result > 1)
				return result; // 파일이 유효하지 않으면 즉시 결과코드

			board.setfImage(uf.uploadFile(mf, request)); // 검증 통과한 파일 업로드 및 이름 저장
		}

		return fdao.insert(board); // DB에 board 삽입. 성공 시 1, 실패 시 0 or -1 반환
	}

}
