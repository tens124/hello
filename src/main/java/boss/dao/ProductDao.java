package boss.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import boss.model.Category;
import boss.model.Product;

@Mapper
public interface ProductDao {

	int count(Category c);

	List<Product> list(Category c);

	List<String> selectCategory();

	Product sampleProduct(String cid);

	
}
