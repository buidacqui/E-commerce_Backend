//package com.pvq.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.pvq.repository.OrderRepository;
//import com.pvq.service.OrderService;
//import com.pvq.service.UserService;
//
//@RestController
//@RequestMapping("/api")
//public class PaymentController {
//
//    @Value("${razorpay.api.key}")
//    String apiKey;
//
//    @Value("${razorpay.api.secret}")
//    String apiSecret;
//
//    @Autowired
//    private OrderService orderService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    public ResponseEntity<PaymentLinkResponse> createPaymentLink(@PathVariable Long orderId,
//            @RequestHeader("Authorization") String jwt) {
//    	
//    	
//    }
//}
