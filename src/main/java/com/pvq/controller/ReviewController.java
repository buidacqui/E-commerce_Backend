package com.pvq.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pvq.exception.ProductException;
import com.pvq.exception.UserException;
import com.pvq.model.Review;
import com.pvq.model.User;
import com.pvq.request.ReviewRequest;
import com.pvq.service.ReviewService;
import com.pvq.service.UserService;

@RestController
@RequestMapping("api/reviews")
public class ReviewController {
	@Autowired
	private ReviewService reviewService;

	@Autowired
	private UserService userService;

	@PostMapping("/create")
	public ResponseEntity<Review> createReview(
	        @RequestBody ReviewRequest req,
	        @RequestHeader("Authorization") String jwt
	) throws UserException, ProductException {

	    User user = userService.findUserProfileByJwt(jwt);

	    Review review = reviewService.createreview(req, user);

	    return new ResponseEntity<>(review, HttpStatus.CREATED);
	}
	@GetMapping("/product/{productId}")
	public ResponseEntity<List<Review>> getProductsReview(
	    @PathVariable Long productId
	) throws UserException, ProductException {
	    List<Review> reviews = reviewService.getAllReview(productId);
	    return new ResponseEntity<>(reviews, HttpStatus.ACCEPTED);
	}


}
