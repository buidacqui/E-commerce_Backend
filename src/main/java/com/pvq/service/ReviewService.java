package com.pvq.service;

import java.util.List;

import com.pvq.exception.ProductException;
import com.pvq.model.Review;
import com.pvq.model.User;
import com.pvq.request.ReviewRequest;

public interface ReviewService {

	public Review createreview(ReviewRequest req, User user)throws ProductException;
	public List<Review>getAllReview(Long productId);
}
