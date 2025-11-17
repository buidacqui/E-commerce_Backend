package com.pvq.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.pvq.exception.ProductException;
import com.pvq.model.Product;
import com.pvq.request.CreateProductRequest;

public interface ProductService {
	public Product createProduct(CreateProductRequest req);
	public String deleteProduct(Long productId) throws ProductException;
	
	public Product updateProduct(Long productId, Product req) throws ProductException;

	public Product findProductById(Long id) throws ProductException;

	public List<Product> findProductByCategory(String category);
	
	public List<Product> findAllProducts();


	public Page<Product> getAllProduct(
	        String category,
	        List<String> colors,
	        List<String> sizes,
	        Integer minPrice,
	        Integer maxPrice,
	        Integer minDiscount,
	        String sort,
	        String stock,
	        Integer pageNumber,
	        Integer pageSize
	);
	
	public void forceDeleteProduct(Long productId) throws ProductException; // ✅ thêm dòng này



}
