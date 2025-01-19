package boss.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import boss.common.PagePgm;
import boss.model.Category;
import boss.model.Product;
import boss.service.ProductService;
import boss.service.SearchService;

@Controller
public class ProductController {

	@Autowired
	ProductService ps;

	//전체 검색 시 요청 발생
	@RequestMapping("product.do")
	public String productList(Category c,
			@RequestParam(value = "nowPage", required = false, defaultValue = "1") String nowPage,
			@RequestParam(value = "cntPerPage", required = false, defaultValue = "15") String cntPerPage, 
			Model model) {

		if (c.getNewCid() == null && c.getKeyword() == null) { // 전달받은 카테고리명이 존재하지 않을 때. 즉, 메뉴에서 카테고리를 눌러서 접속했을 때
			
			List<String> clist = ps.selectCategory(); // 모든 cid를 검색하여 clist에 저장
			List<Product> sample = ps.sampleProduct(clist);
			//이것도 통합시켜보자
			
			model.addAttribute("clist", clist);
			model.addAttribute("sample", sample);
			
			return "./product/sampleList";
		}
		
		//각 페이지마다 달라지는 점은? 1.검색어(검색창에 유지)
		//즉 DB에서 가져올 product리스트만 바뀌면 되는 것
		//Category에는 카테고리명과 keyword 전부 포함되어 있음. sql문을 수정하는 것이 가장 나을 듯
		
		PagePgm page = ps.paging(c, nowPage, cntPerPage);
		System.out.println("컨트롤러:"+page.getNowPage());
		c.setNewStartRow(page.getStartRow());
		c.setNewEndRow(page.getEndRow());
		
		List<Product> list = ps.list(c);
		
		model.addAttribute("category", c);
		model.addAttribute("list", list); //비어있다면 뷰페이지에서 검사
		model.addAttribute("pp", page);
		
		return "./product/product";
	}

}
