package com.pvq.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pvq.exception.ProductException;
import com.pvq.model.Product;
import com.pvq.model.Review;
import com.pvq.model.User;
import com.pvq.repository.ProductRepository;
import com.pvq.repository.ReviewRepository;
import com.pvq.request.ReviewRequest;

@Service
public class ReviewServiceImplementation implements ReviewService {

	private ReviewRepository reviewRepository;
	private ProductService productService;
	private ProductRepository productRepository;

	public ReviewServiceImplementation(ReviewRepository reviewRepository,
	    ProductService productService, ProductRepository productRepository) {
	    this.reviewRepository = reviewRepository;
	    this.productService = productService;
	}

	@Override
	public Review createreview(ReviewRequest req, User user) throws ProductException {
		Product product = productService.findProductById(req.getProductId());

		Review review = new Review();
		review.setUser(user);
		review.setProduct(product);
		review.setReview(req.getReview());
		review.setCreatedAt(LocalDateTime.now());

		return reviewRepository.save(review);

	}

	@Override
	public List<Review> getAllReview(Long productId) {
		// TODO Auto-generated method stub
		return reviewRepository.getAllProductsRevew(productId);
	}

}
