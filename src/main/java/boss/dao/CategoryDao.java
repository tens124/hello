package boss.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import boss.model.Category;
import boss.model.Product;

@Mapper
public interface CategoryDao {

	int categoryCount(String cid);

	List<Product> categoryList(Category c);

	List<Product> categorySearch(Category c);

	int categorySearchCount(Category c);

	List<String> selectcid();

	Product samplecategory(String cid);

	List<Product> categoryList1(Map<String, Object> map);

}
