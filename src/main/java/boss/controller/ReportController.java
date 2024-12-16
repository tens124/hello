package boss.controller;

import java.io.File;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import boss.model.Report;
import boss.service.ReportService;

@Controller
public class ReportController {

	@Autowired
	ReportService rs;

	// 신고 작성 폼 이동
	@RequestMapping("reportWriteForm.do")
	public String reportWriteForm(String pid, Report report, Model model) {
		System.out.println("reportWriteForm");
		String reporttype = report.getReporttype();	//신고 타입을 저장
		String reportnum = "" + report.getReportnum();	//신고 번호를 문자형으로 변환

		System.out.println("type : " + reporttype);
		System.out.println("reportnum : " + reportnum);

		if (reporttype.equals("review")) {	//리뷰 게시판에서 온 신고라면? 신고 내용과 함께 상품번호를 같이 저장
			model.addAttribute("Report", report);
			model.addAttribute("pid", pid);

		} else if (reporttype.equals("freeBoard")) {	//자유게시판에서 온 신고라면? 리포트 객체만 저장
			model.addAttribute("Report", report);
		}
		return "report/reportWriteForm";
	}

	// 신고 작성값 받음
	@RequestMapping("reportWrite.do")
	public String reportWrite(String pid, Report report, Model model, HttpServletRequest request,
			@RequestParam(value = "image1", required = false) MultipartFile mfile) throws Exception {
		System.out.println("reportWrite");
		int result = 0;
		String realFileName = mfile.getOriginalFilename();	//파일 이름 저장
		int size = (int) mfile.getSize();	//파일 용량 저장
		if (realFileName != null && realFileName != "") { // 파일이 있는경우
			String extension = realFileName.substring(realFileName.lastIndexOf("."), realFileName.length());	//.을 포함한 확장자만 추출
			System.out.println("파일이있음. : " + extension);
			
			
			//잘못된 이미지 사용으로 인해 신고글 작성이 불가
			if (size > 5000000) { // 이미지의 용량이 약 4MB를 초과한 경우.
				System.out.println("용량초과");
				model.addAttribute("result", 2);
				model.addAttribute("msg", "용량이 3MB 이상입니다.");
				return "report/reportWriteResult";
			} else if (!extension.equals(".jpg") && !extension.equals(".png") && !extension.equals(".jpeg")
					&& !extension.equals(".gif")) { // 이미지 형식이 올바르지 않은경우.
				System.out.println("이미지 형식 다름");
				model.addAttribute("result", 3);
				model.addAttribute("msg", "올바른 파일 형식이 아닙니다.");
				return "report/reportWriteResult";
			}
			
			
			
			UUID uuid = UUID.randomUUID();	//해당 코드는 128비트 크기의 새로운 이름을 생성해준다. 따라서 해당 결과값과 원본 파일 이름을 조합하면 중복 방지에 매우 효율적
			String newFileName = uuid + extension;	//새로운 파일 이름을 생성
			report.setReportimage(newFileName);		//세터 메소드를 이용해 새로운 파일 이름을 적용
			//String path = "C:\\gitBoss\\boss\\src\\main\\webapp\\uploadReport";
			String path = request.getRealPath("images");	//파일의 경로를 추적하는 코드. 구버전이니 개선할 수 있을 것
			mfile.transferTo(new File(path + "/" + newFileName));	//파일 업로드. 업로드된 파일을 지정한 경로(path + 파일명)로 실제로 저장
		}

		result = rs.insert(report);	//insert문을 호출. 성공 시 1 반환
		if (result == 1) { // 글 작성 true
			if (report.getReporttype().equals("review")) { // 리뷰에 글을썼을경우. report DTO의 reporttype 프로퍼티값을 비교
				System.out.println("리뷰 글작성 성공");
				model.addAttribute("msg", "신고가 접수되었습니다.");
				model.addAttribute("resultType", "review_true");
				model.addAttribute("pid", pid);
				System.out.println("pid : " + pid);
				model.addAttribute("report", report);
			} else if (report.getReporttype().equals("freeBoard")) { // 프리보드에 글을썼을경우
				System.out.println("프리보드 글작성 성공");
				model.addAttribute("msg", "신고가 접수되었습니다.");
				model.addAttribute("resultType", "freeBoard_true");
				model.addAttribute("report", report);
			}
		} else { // 글 작성 false
			model.addAttribute("msg", "글 작성에 실패하였습니다.");
			model.addAttribute("resultType", "report_false");
		}

		return "report/reportWriteResult";
	}
}
