package boss.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import boss.common.OpenAI;
import boss.common.Pinecone;
import boss.model.MainImage;
import boss.model.Product;
import boss.service.MasterProductService;

@Controller
public class MainController {

	@Autowired
	private MasterProductService service;
	

	//메인 페이지 이동 메소드. main.do라는 요청이 발생되면 이 메소드가 호출됨
	@RequestMapping(value = "main.do")
	public String main(Model model, @RequestParam(value = "block", required = false, defaultValue = "1") String block)
	//Model 객체는 값을 저장한 객체. 해당 모델을 통해 뷰 파일과 자바 파일 간의 공유가 이루어지기에, 값을 주고받는 메소드라면 반드시 해당 객체가 필요
	//@RequestParam 어노테이션은 클라이언트의 HTTP 요청 파라미터를 메서드 매개변수로 바인딩
	//url에 block이라는 요소가 포함되어 전달되어 오면, 해당 요소의 값을 block 변수에 할당할 값으로 지정. 필수값은 아니며, 만약 block 요소가 존재하지 않는다면 기본값으로 "1"을 할당
	//block은 메인에 걸릴 대표상품들을 위한 변수!
			throws Exception {

		//MainImage는 DTO
		// 초기값 뿌려줌
		//mainImageList 맵은 이름과 DB데이터를 함께 보관하는 듯?
		//mainImageList_db 리스트에는 select문을 통해 db에서 가져온 사진들이 저장됨
		Map<String, MainImage> mainImageList = new HashMap<String, MainImage>();	//메인에 표시될 상품리스트인듯?
		List<MainImage> mainImageList_db = service.selectMainProductList();

		// by hyesun
//		Collections.sort(mainImageList_db, new Comparator<MainImage>() {
//            @Override
//            public int compare(MainImage o1, MainImage o2) {
//                return o2.getPid() - o1.getPid();	//pid 기준으로 내림차순 정렬되게 함
//            }
//        });
		// by hyesun end

		if (mainImageList_db.size() > 0) { // DB 검색 결과 1개라도 구해옴.
			for (int i = 0; i < mainImageList_db.size(); i++) { // list size만큼 put

				// int s = Integer.parseInt(block);

				model.addAttribute("block" + (i + 1), i + 1);	
				model.addAttribute("mainImageList" + (i+1), mainImageList_db.get(i));
				//model(이름,값) 객체에 값을 저장한다
				//block1,1 과 mainImageList1,mainImageList.get(0)	두 가지 요소가 model 내부에 저장됨
			}
		}
		
		
		//뷰 페이지의 상품 출력 방법. 
//		<span class="image"> 
//			<c:if test="${block8 == 8 }">
//				<img src="images/${mainImageList8.mainimage} " alt="" height="450" />
//			</c:if>
//		</span> 
//		<a href="productDetail.do?pid=${mainImageList8.pid }">
		
		
		//model.addAttribute("block", block);

		// by hyesun
//		Pinecone.getInstance();
//		OpenAI.getInstance();
		// by hyesun end
		
		
		

		return "common/main";	//완성된 model 객체를 해당 파일로 전송. 뷰리졸버의 설정 덕분에 가능한 일
								//디스패쳐 서블릿의 설정이 리턴문과 뷰리졸버를 연결해준다. 컨트롤러 메소드의 리턴문은 뷰리졸버로 이어짐
	}

	/*
	 * 메인 이미지 수정 메소드
	 */
	@RequestMapping("masterImageUpdateForm.do")
	public String masterImageUpdateForm(String id, String block, Model model) {

		Product product = service.selectOne(id);

		String mainImage = product.getPimage();
		String mainPname = product.getPname();
		String mainContent = product.getPcontent();

		int bk = Integer.parseInt(block);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("block", bk);
		map.put("id", id);
		map.put("mainimage", mainImage);
		map.put("mainpname", mainPname);
		map.put("maincontent", mainContent);

		int result = service.updateMainImageInsert(map);

		model.addAttribute("block", block);

		return "redirect:/main.do";
	}
}
