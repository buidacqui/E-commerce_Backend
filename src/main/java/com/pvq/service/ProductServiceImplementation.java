package com.pvq.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pvq.exception.ProductException;
import com.pvq.model.Category;
import com.pvq.model.Product;
import com.pvq.repository.CategoryRepository;
import com.pvq.repository.OrderItemRepository;
import com.pvq.repository.ProductRepository;
import com.pvq.request.CreateProductRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ProductServiceImplementation implements ProductService {
	private ProductRepository productRepository;
	private UserService userService;
	private CategoryRepository categoryRepository;
	private OrderItemRepository orderItemRepository;
	 @PersistenceContext
	    private EntityManager entityManager;

	public ProductServiceImplementation(ProductRepository productRepository,
	                                    UserService userService,
	                                    CategoryRepository categoryRepository,
	                                    OrderItemRepository orderItemRepository) {
	    this.productRepository = productRepository;
	    this.userService = userService;
	    this.categoryRepository = categoryRepository;
	    this.orderItemRepository = orderItemRepository;
	}

	@Override
	public Product createProduct(CreateProductRequest req) {
		Category topLevel = categoryRepository.findByName(req.getTopLavelCategory());
		
		if (topLevel == null) {
		    Category topLavelCategory = new Category();
		    topLavelCategory.setName(req.getTopLavelCategory());
		    topLavelCategory.setLevel(1);

		    topLevel = categoryRepository.save(topLavelCategory);
		}

		Category secondLevel = categoryRepository.findByNameAndParant(
		        req.getSecondLavelCategory(), 
		        topLevel.getName()
		);

		if (secondLevel == null) {
		    Category secondLavelCategory = new Category();
		    secondLavelCategory.setName(req.getSecondLavelCategory());
		    secondLavelCategory.setParentCategory(topLevel);
		    secondLavelCategory.setLevel(2);

		    secondLevel = categoryRepository.save(secondLavelCategory);
		}
		
		Category thirdLevel = categoryRepository.findByNameAndParant(
		        req.getThirdLavelCategory(),
		        secondLevel.getName()
		);

		if (thirdLevel == null) {
		    Category thirdLavelCategory = new Category();
		    thirdLavelCategory.setName(req.getThirdLavelCategory());
		    thirdLavelCategory.setParentCategory(secondLevel);
		    thirdLavelCategory.setLevel(3);

		    thirdLevel = categoryRepository.save(thirdLavelCategory);
		}
		
		Product product = new Product();
		product.setTitle(req.getTitle());
		product.setColor(req.getColor());
		product.setDescription(req.getDescription());
		product.setDiscountedPrice(req.getDiscountedPrice());
		product.setDiscountPercent(req.getDiscountPercent());
		product.setImageUrl(req.getImageUrl());
		product.setBrand(req.getBrand());
		product.setPrice(req.getPrice());
		product.setSizes(req.getSize());
		product.setQuantity(req.getQuantity());
		product.setCategory(thirdLevel);
		product.setCreateAt(LocalDateTime.now());
		
		Product savedProduct =productRepository.save(product);
		
		return savedProduct;

	}

	@Override
	public String deleteProduct(Long productId) throws ProductException {
	    Product product = findProductById(productId);
	    product.getSizes().clear();
	    productRepository.delete(product);
	    return "Product deleted Successfully";
	}


	@Transactional
    @Override
    public Product updateProduct(Long productId, Product req) throws ProductException {
        Product product = findProductById(productId);

        if (req.getTitle() != null) product.setTitle(req.getTitle());
        if (req.getDescription() != null) product.setDescription(req.getDescription());
        if (req.getBrand() != null) product.setBrand(req.getBrand());
        if (req.getColor() != null) product.setColor(req.getColor());
        if (req.getImageUrl() != null) product.setImageUrl(req.getImageUrl());
        if (req.getDiscountedPrice() != null) product.setDiscountedPrice(req.getDiscountedPrice());
        if (req.getDiscountPercent() != null) product.setDiscountPercent(req.getDiscountPercent());
        if (req.getPrice() != null) product.setPrice(req.getPrice());
        if (req.getQuantity() != null) product.setQuantity(req.getQuantity());

        // ✅ FIX: Reassign Set thay vì clear/addAll
        if (req.getSizes() != null && !req.getSizes().isEmpty()) {
            product.setSizes(new HashSet<>(req.getSizes()));
        }

        if (req.getCategory() != null) product.setCategory(req.getCategory());

        // ✅ Save và refresh entity
        Product saved = productRepository.save(product);
        entityManager.flush();
        entityManager.refresh(saved); // ✅ tải lại dữ liệu từ DB
        return saved;

    }



	@Override
	public Product findProductById(Long id) throws ProductException {
	    Optional<Product> opt = productRepository.findById(id);

	    if (opt.isPresent()) {
	        return opt.get();
	    }
	    throw new ProductException("Product not found with id - " + id);
	}


	@Override
	public List<Product> findProductByCategory(String category) {
		// TODO Auto-generated method stub
		return null;
	}
	
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

	@Override
	public Page<Product> getAllProduct(String category, List<String> colors, List<String> sizes, Integer minPrice,
			Integer maxPrice, Integer minDiscount, String sort, String stock, Integer pageNumber, Integer pageSize) {
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);

		List<Product> products = productRepository.filterProducts(category, minPrice, maxPrice, minDiscount, sort);

		if (!colors.isEmpty()) {
		    products = products.stream()
		            .filter(p -> colors.stream().anyMatch(c -> c.equalsIgnoreCase(p.getColor())))
		            .collect(Collectors.toList());
		}

		if (stock != null) {
		    if (stock.equals("in_stock")) {
		        products = products.stream()
		                .filter(p -> p.getQuantity() > 0)
		                .collect(Collectors.toList());
		    } else if (stock.equals("out_of_stock")) {
		        products = products.stream()
		                .filter(p -> p.getQuantity() < 1)
		                .collect(Collectors.toList());
		    }
		}

		int startIndex = (int) pageable.getOffset();
		int endIndex = Math.min(startIndex + pageable.getPageSize(), products.size());

		List<Product> pageContent = products.subList(startIndex, endIndex);

		Page<Product> filteredProducts = new PageImpl<>(pageContent, pageable, products.size());

		return filteredProducts;
	}
	
	@Override
	@Transactional
	public void forceDeleteProduct(Long productId) throws ProductException {
	    Product product = productRepository.findById(productId)
	            .orElseThrow(() -> new ProductException("Không tìm thấy sản phẩm với ID: " + productId));

	    // ✅ Xóa tất cả order item liên quan trước
	    orderItemRepository.deleteByProductId(productId);

	    // ✅ Sau đó xóa luôn sản phẩm
	    productRepository.delete(product);
	}






}
