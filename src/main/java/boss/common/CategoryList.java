package boss.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import boss.service.ProductService;

@ControllerAdvice			// 모든 요청에 자동으로 호출되는 어노테이션. 따라서 @ModelAttribute에 설정된 내용을 전역적으로 참조가 가능
public class CategoryList {
	
	@Autowired
	ProductService ps;

    @ModelAttribute("categoryList")			//공유를 위한 어노테이션. model과 역할이 동일
    public List<String> category() {
    	
        // ProductService를 통해 카테고리 목록을 가져옴
        return ps.selectCategory();
    }
}
