package boss.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import boss.common.PagePgm;
import boss.dao.ProductDao;
import boss.model.Category;
import boss.model.Product;



@Service
public class ProductService {
	
	@Autowired
	ProductDao dao;

	public int count(Category c) {

		return dao.count(c);
	}

	public PagePgm paging(Category c, String nowPage, String cntPerPage) {
		// TODO Auto-generated method stub
		int total = dao.count(c);
		
		return new PagePgm(total, Integer.parseInt(nowPage), Integer.parseInt(cntPerPage));
	}

	public List<Product> list(Category c) {
		// TODO Auto-generated method stub
		return dao.list(c);
	}

	public List<String> selectCategory() {
		// TODO Auto-generated method stub
		return dao.selectCategory();
	}

	public List<Product> sampleProduct(List<String> clist) {
List<Product> sample = new ArrayList<Product>();
		
		for (String cid : clist) {		
			Product p = dao.sampleProduct(cid);
			if(p == null) {
				p = new Product();
				p.setCid(cid);
			}
			
			sample.add(p);
        }
		
		return sample;
	}

}
