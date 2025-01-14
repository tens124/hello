package boss.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import boss.common.PagePgm;
import boss.dao.CategoryDao;
import boss.model.Category;
import boss.model.Product;

@Service
public class CategoryService {

	@Autowired
	CategoryDao dao;
	
	//해당 카테고리의 상품 list 전송
	public List<Product> categoryList(Category c) {
		System.out.println("서비스");
		return dao.categoryList(c);
	}

	public List<Product> categorySearch(Category c) {

		return dao.categorySearch(c);
	}

	public List<String> selectcid() {

		return dao.selectcid();
	}

	public List<Product> sampleList(List<String> clist) {
		
		List<Product> sample = new ArrayList<Product>();
		
		for (String cid : clist) {		
			Product p = dao.samplecategory(cid);
			if(p == null) {
				p = new Product();
				p.setCid(cid);
			}
			
			sample.add(p);
        }
		
		return sample;
	}

	public PagePgm categoryPage(String newCid, int now, int count) {

		int total = dao.categoryCount(newCid);
		//해당 카테고리의 상품 갯수를 검색
		//페이징 중 끝 번호 처리를 위한 수
		
		return new PagePgm(total,now,count);
	}

	public PagePgm categorySearchPage(Category c, int now, int count) {

		int total = dao.categorySearchCount(c);
		//카테고리 내에서 해당 검색어를 가진 상품의 갯수를 파악
		return new PagePgm(total, now, count);
	}

	
}
