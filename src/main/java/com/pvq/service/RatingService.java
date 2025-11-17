package com.pvq.service;

import java.util.List;

import com.pvq.exception.ProductException;
import com.pvq.model.Rating;
import com.pvq.model.User;
import com.pvq.request.RatingRequest;

public interface RatingService {
	
	public Rating createRating(RatingRequest req,User user) throws ProductException;
	public List<Rating>getProductsRating(Long productId);
}
