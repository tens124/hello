package boss.controller;

import java.io.File;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import boss.common.PagingPgmHyesun;
import boss.model.FreeBoard;
import boss.model.Likes;
import boss.model.Member;
import boss.service.FreeBoardService;
import boss.service.LikeService;
import boss.service.MemberService;

@Controller
public class FreeBoardController {

	@Autowired
	private FreeBoardService fservice;
	
	@Autowired
	private LikeService lservice;
	
	// 멤버 서비스
	@Autowired
	private MemberService service;
	//패스워드 암호화 
	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	// 자유게시판 글등록폼 이동
	@RequestMapping("freeBoardInsertform.do")
	public String freeBoardInsertform() {
		return "freeboard/freeBoardInsertform";
	}

	// 자유게시판 글등록 ok. 글 등록 폼에서 '등록' 버튼을 누르면
	@RequestMapping("freeBoardInsertok.do")
	public String freeBoardInsertok(@ModelAttribute FreeBoard board, 	//@ModelAttribute은 html 폼에서 전달된 데이터를 DTO 객체에 자동으로 바인딩 할 때 사용. 필수는 아님
			                        @RequestParam("freeImage") MultipartFile mf,	//freeImage라는 데이터를 추가로 바인딩
			                        HttpServletRequest request,		//HTTP 요청을 처리하는 객체. 클라이언트가 서버에 보낸 HTTP 요청에 대한 정보를 제공	
			                        								//필수는 아님. 특별한 요구 사항(URL 정보나 헤더 정보, 세션 관리 등)이 있을 때만 사용하는 것이 일반적
			                         Model model) throws Exception {
		// @ModelAttribute 을 이용해서 form에서 넘어온 값을 dto객체로 값을 받고
		// name값이 일치되면 set메소드로 값이 저장됨

		//첨부파일 저장 (중복문제 난수발생으로 파일명 다르게함)
		String filename = mf.getOriginalFilename();		// 첨부파일명
		int size = (int) mf.getSize(); 	
		// 첨부파일의 크기 (단위:Byte) getSize():long형 ->int형 자료형변환 

		//배포전에 업로드한 이미지는 aws의 realpath에 직접 업로드를 시켜야함. (내폴더에만 있어서)
		//배포후에 업로드한 이미지는 aws에 업로드되어있어서 직접 업로드 하지않아도됨.(주석처리한경로는 절대경로라서 배포후에는 불러올수없음)
		String path = request.getRealPath("images");	//파일 실제 경로를 불러오는 데 사용
		//String path = "C:\\Users\\haham\\Downloads\\프로젝트\\상단이미지\\bossproject\\boss\\src\\main\\webapp\\images";
		int result=0;
		
//		String file[] = new String[2];
//		file = filename.split(".");
//		System.out.println(file.length);
//		System.out.println("file0="+file[0]);
//		System.out.println("file1="+file[1]);
		
		String newfilename = "";
	
	if(size > 0){	 	// 첨부파일이 전송된 경우	
		
		// 파일 중복문제 해결
		//.을기준으로 파일길이만큼 파일명과 확장자를 분리 
		String extension = filename.substring(filename.lastIndexOf("."), filename.length());
		System.out.println("extension:"+extension);
		
		UUID uuid = UUID.randomUUID();//난수발생 
		
		newfilename = uuid.toString() + extension;
		
//		StringTokenizer st = new StringTokenizer(filename, ".");
//		file[0] = st.nextToken();		// 파일명		
//		file[1] = st.nextToken();		// 확장자	    jpg 등
		
		if(size > 1000000){				// 1000KB
			result=2;  
			model.addAttribute("result", result);
			return "freeboard/freeBoardInsertform";
			
		}else if(!extension.equals(".jpg")  &&
				 !extension.equals(".jpeg") &&
				 !extension.equals(".gif")  &&
				 !extension.equals(".png") ){
			
			result=3;
			model.addAttribute("result", result);
			return "freeboard/freeBoardInsertform";
		}
	}	

	//	if(filename != ""){	 // 첨부파일이 전송된 경우	
		if (size > 0) { 	// 첨부파일이 전송된 경우
			mf.transferTo(new File(path + "/" + newfilename)); //실제로 첨부파일을 업로드하는 코드 
		}
		board.setfImage(newfilename);
		
		result=fservice.insert(board); // 글 insert
		model.addAttribute("result", result);
		return "freeboard/freeBoardInsertform";
	}

	// 자유게시판 목록 
	@RequestMapping("freeBoardList.do")
	public String freeBoardList(String page, FreeBoard board, Model model) {	
	//page는 출력할 페이지 번호를 의미. 뷰에서 이동 가능

		final int rowPerPage = 10;	// 화면에 출력할 데이터 갯수
		if (page == null || page.equals("")) {	//page가 빈 값일 경우, 즉 초기에 게시판 진입 시 1페이지로 접속되게 설정
			page = "1";
		}
		int currentPage = Integer.parseInt(page); // 현재 페이지 번호. String 값을 int값으로 변환
		
		// 총 데이터 갯수 (검색기능 추가)
		int total = fservice.freeBoardListCount(board); 	//해당 검색어로 검색했을 때 일치하는 값을 가진 행의 갯수를 조사
		
		//startRow: 한 페이지 화면 출력 시작번호, endRow: 한 페이지 화면출력 끝번호
		//한 페이지에서의 페이지 갯수는 rowPerPage, 10개. 즉, 1~10/11~20/21~30 .... 을 표시하도록 해야 함
		int startRow = (currentPage - 1) * rowPerPage + 1; //1,11,21..	1페이지라면 1/5페이지라면 41/7페이지라면 61 ...
		int endRow = startRow + rowPerPage - 1; //10,20,30,...	첫 글 번호가 1이라면 10/41이라면 50/61이라면 70 ...
		
		//PagingPgmf : paging dto. 해당 DTO는 모델이 아닌 common 디렉토리에 존재 
		PagingPgmHyesun pp = new PagingPgmHyesun(total, rowPerPage, currentPage);	//두 개의 DTO 차이점을 알아보자
		
		board.setStartRow(startRow);
		board.setEndRow(endRow);
		//세터를 통한 시작번호/끝번호 설정

		int no = total - startRow + 1;		// 화면 출력 번호. 게시물 갯수 - 시작번호 + 1
		
		List<FreeBoard> list = fservice.freeBoardList(board);	//게시물 목록을 가져와 저장
		
		model.addAttribute("list", list);
		model.addAttribute("no", no);
		model.addAttribute("pp", pp);
		
		// 검색
		model.addAttribute("search", board.getSearch());
		model.addAttribute("keyword", board.getKeyword());
		
		return "freeboard/freeBoardList";
	}

	// 조회수증가, 게시판 상세페이지, 수정폼,삭제폼에 답변폼 내용 출력
	@RequestMapping("freeBoardDetail.do")
	public String freeBoardUpdate(@RequestParam("fId") int fId, @RequestParam("page") String page,
			@RequestParam("state") String state,HttpSession session , Member member, Model model) {

		// state로 설정한곳 판별 //state가 detail과 같다면(목록에서 제목클릭시 상세페이지로 이동)
		if (state.equals("detail")) {
			fservice.readcount(fId); // 조회수 증가 
		}

		FreeBoard board = fservice.getDetail(fId);
		System.out.println("fImage:"+board.getfImage());
		
		// Like 가져오는 부분
		Likes like = new Likes();
		like.setLikeDrop("N");
		
		if(session.getAttribute("member") != null) {
			member = (Member) session.getAttribute("member");
			String mEmail = member.getmEmail();
			like = lservice.findLike(fId, mEmail);
		}
		
		//좋아요 갯수
		int countLike = lservice.countLike(fId);
		board.setfLike(countLike);
		
		model.addAttribute("detail", board);
		model.addAttribute("page", page);
		model.addAttribute("like",like);
		
		// state가 detail과 같다면(목록에서 제목클릭시 상세페이지로 이동) if(state.equals("detail")) {
		if (state.equals("detail")) {
			return "freeboard/freeBoardDetail";
		} else if (state.equals("update")) { // 수정폼
			return "freeboard/freeBoardUpdateform";
		} else if (state.equals("delete")) { // 삭제폼
			return "freeboard/freeBoardDelete";
		} // 답변컬럼추가후 답변폼 이동 추가하기 return
		return null;
	}

	// 글 수정
	@RequestMapping("freeBoardUpdateok.do")
	public String freeBoardUpdateok(@RequestParam("fPassword") String fPassword,
			                        @ModelAttribute FreeBoard board, 
			                        @RequestParam("fId") int fId,
			                        @RequestParam("page") String page, 
			                        @RequestParam("freeImage") MultipartFile mf,
			                        HttpServletRequest request,
			                        Model model, Member member,HttpSession session) throws Exception {
		
		
		//첨부파일 저장 (중복문제 난수발생으로 파일명 다르게함)
		String filename = mf.getOriginalFilename();		// 첨부파일명
		int size = (int) mf.getSize(); 	
		// 첨부파일의 크기 (단위:Byte) getSize():long형 ->int형 자료형변환 

		//배포전에 업로드한 이미지는 aws의 realpath에 직접 업로드를 시켜야함. (내폴더에만 있어서)
		//배포후에 업로드한 이미지는 aws에 업로드되어있어서 직접 업로드 하지않아도됨.(주석처리한경로는 절대경로라서 배포후에는 불러올수없음)
		String path = request.getRealPath("images");
		//String path = "C:\\Users\\haham\\Downloads\\프로젝트\\상단이미지\\bossproject\\boss\\src\\main\\webapp\\images";
		System.out.println("mf=" + mf);
		System.out.println("filename=" + filename); 	// filename="Koala.jpg"
		System.out.println("size=" + size);
		System.out.println("Path=" + path);
		int result=0;
		
//		String file[] = new String[2];
//		file = filename.split(".");
//		System.out.println(file.length);
//		System.out.println("file0="+file[0]);
//		System.out.println("file1="+file[1]);
		
		String newfilename = "";
	
	if(size > 0){	 	// 첨부파일이 전송된 경우	
		
		// 파일 중복문제 해결
		//.을기준으로 파일길이만큼 파일명과 확장자를 분리 
		String extension = filename.substring(filename.lastIndexOf("."), filename.length());
		
		UUID uuid = UUID.randomUUID();//난수발생 
		
		newfilename = uuid.toString() + extension;
		
//		StringTokenizer st = new StringTokenizer(filename, ".");
//		file[0] = st.nextToken();		// 파일명		
//		file[1] = st.nextToken();		// 확장자	    jpg 등
		
		if(size > 1000000){				// 1000KB
			result=2; 
			model.addAttribute("result", result);
		return "freeboard/freeBoardUpdateform";
			
		}else if(!extension.equals(".jpg")  &&
				 !extension.equals(".jpeg") &&
				 !extension.equals(".gif")  &&
				 !extension.equals(".png") ){
			
			result=3;
			model.addAttribute("result", result);
		return "freeboard/freeBoardUpdateform";
		}
	}	

	//	if(filename != ""){	 // 첨부파일이 전송된 경우	
		if (size > 0) { 	// 첨부파일이 전송된 경우
			mf.transferTo(new File(path + "/" + newfilename)); //실제로 첨부파일을 업로드하는 코드 
		}
		board.setfImage(newfilename);
		
		
		//fId로 DB에 저장된 FreeBoard정보를 가져옴 
		FreeBoard fboard = fservice.getDetail(fId);
		
		if (size > 0 ) { 			// 첨부 파일이 수정되면
			board.setfImage(newfilename);			
		} else { 					// 첨부파일이 수정되지 않으면
			board.setfImage(fboard.getfImage()); //db에 저장된 image 
		}
		
		//session공유된 member를 불러와서 mEmail가져옴
		member = (Member) session.getAttribute("member");
		String mEmail = member.getmEmail();
		
		//mEmail로 DB에 저장된 member정보를 가져옴 
		Member dbmember = service.selectOne(mEmail);
		
		// update (삭제 여부 변경) 이 성공적으로 되었을때 조건문을 달기 위함
		result = 0;

		//dbmember 값이 있고 , fPassword과 DB에 저장된 pwd(실제비번)과 같으면 delete
		if(dbmember != null && passwordEncoder.matches(fPassword, dbmember.getmPwd())) {
			result = fservice.update(board); // 글 update result=1
		}else {
			result=0;
		}
			model.addAttribute("result", result);
			model.addAttribute("page", page);
			model.addAttribute("fId", fId);

		return "freeboard/freeBoardUpdateform";
	}

	// 글 삭제
	@RequestMapping("freeBoardDeleteok.do")
	public String freeBoardDeleteok(@RequestParam("fPassword") String fPassword,
			                         @RequestParam("fId") int fId,
			                        @RequestParam("page") String page,
			                        @ModelAttribute FreeBoard board, 
			                        Model model, Member member,HttpSession session) throws Exception {
		
		//session공유된 member를 불러와서 mEmail가져옴
		member = (Member) session.getAttribute("member");
		String mEmail = member.getmEmail();
		
		//mEmail로 DB에 저장된 member정보를 가져옴 
		Member dbmember = service.selectOne(mEmail);
		
		// delete (삭제 여부 변경) 이 성공적으로 되었을때 조건문을 달기 위함
		int result = 0;

		//dbmember 값이 있고 , fPassword과 DB에 저장된 pwd(실제비번)과 같으면 delete
		if(dbmember != null && passwordEncoder.matches(fPassword, dbmember.getmPwd())) {
			
			
			//첨부파일 삭제 (Y값으로 놔두는거니까 이미지 데이터 그냥 놔두고 실제 삭제원하면 구현)
//			String path = session.getServletContext().getRealPath("images");
//			String fname = board.getfImage();
//			System.out.println("path:"+path);
//			
//			// 디비에 저장된 첩부파일명을 가져옴
//			if (fname != null) {		// 첨부파일이 있으면ㄴ
//				File file = new File(path +"/"+fname);
//				file.delete();			// 첨부파일 삭제
//			}
			
			result = fservice.delete(fId); //delete이지만 Y값으로 update

		}else {   //비번이 같지 않으면
			result=0;
		}
		model.addAttribute("result", result);
		model.addAttribute("page", page);
		
		return "freeboard/freeBoardDelete";
	}
}

     