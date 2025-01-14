package boss.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import boss.common.PagePgm;
import boss.common.Search;
import boss.model.Category;
import boss.model.Product;
import boss.service.CategoryService;

@Controller		//컨트롤러는 응답을 위한 데이터를 생성해내는 역할. 뷰에 어떤 데이터가 필요한지를 분석 후, model에 공유할 객체들을 선별해내자
public class CategoryController {
	
	@Autowired
	CategoryService cs;
	
		// 상품 리스트로 이동 이동
		@RequestMapping("category.do")
		public String category(Model model, Category c,
				@RequestParam(value = "nowPage", required = false, defaultValue = "1") String nowPage,
				@RequestParam(value = "cntPerPage", required = false, defaultValue = "15") String cntPerPage) throws Exception {
			
			//category.do 요청이 컨트롤러로 도착
			//컨트롤러는 요청 상태에 따라 올바른 이미지 세트를 생성 후, 뷰에 전달해야 한다
			
			if(c.getNewCid() == null) {	//전달받은 카테고리명이 존재하지 않을 때. 즉, 메뉴에서 카테고리를 눌러서 접속했을 때
				
				List<String> clist = cs.selectcid();	//모든 cid를 검색하여 clist에 저장
				List<Product> sample = cs.sampleList(clist);
				
				model.addAttribute("clist",clist);
				model.addAttribute("sample",sample);
				
				return "./category/sampleList";
			}
			
			//카테고리명이 존재할 때
			
			//페이징을 처리할 순서. 응답에 필요한 데이터가 뭘까?
			//cid 별 상품 리스트가 필요
			//1차적으로 해당 데이터를 전부 검색
			//그 후 페이징 객체의 값을 이용해 출력 범위를 조절
			//서비스로 뭘 전송해야 할까? 카테고리명, pp객체
			
			//1.페이징처리를 위한 값들을 전송하여 pp객체를 만들고, 거기서 계산된 변수들을 Category DTO에 저장. 
			PagePgm pp = cs.categoryPage(c.getNewCid(), Integer.parseInt(nowPage), Integer.parseInt(cntPerPage));
			c.setNewStartRow(pp.getStartRow());
			c.setNewEndRow(pp.getEndRow());
			
			//2.완성된 Category DTO를 통해 뷰에 표시할 리스트를 작성
			List<Product> list = cs.categoryList(c);
			
			model.addAttribute("list", list != null ? list : "상품이 없습니다");		//3항 연산자로 축약
			model.addAttribute("pp", pp);  //상품 갯수, 현재페이지번호, 한 페이지에 출력될 번호를 공유
			model.addAttribute("cid",c.getNewCid());
			model.addAttribute("category",c);
			//cid로 찾아낸 해당 카테고리 상품 목록을 페이징 처리하여 공유
			
			return "./category/categoryList";
		}
		
		//검색 리스트로 이동
		@RequestMapping("categorySearch.do")
		public String categorySearch(Model model, Category c,
				@RequestParam(value = "nowPage", required = false, defaultValue = "1") String nowPage,
				@RequestParam(value = "cntPerPage", required = false, defaultValue = "15") String cntPerPage) {
			
			//1.페이징처리를 위한 값들을 전송 후 거기서 파생된 변수들을 Category DTO에 저장. 
			PagePgm pp = cs.categorySearchPage(c, Integer.parseInt(nowPage), Integer.parseInt(cntPerPage));
			c.setNewStartRow(pp.getStartRow());
			c.setNewEndRow(pp.getEndRow());
			
			List<Product> list = cs.categorySearch(c);
			
			model.addAttribute("pp", pp);  //상품 갯수, 현재페이지번호, 한 페이지에 출력될 번호를 공유
			model.addAttribute("list", list);
			model.addAttribute("category",c);
			//cid로 찾아낸 해당 카테고리 상품 목록을 페이징 처리하여 공유
			return "./category/categorySearch";
		}

}
