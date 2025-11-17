package com.pvq.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pvq.exception.ProductException;
import com.pvq.model.Order;
import com.pvq.model.Product;
import com.pvq.request.CreateProductRequest;
import com.pvq.response.ApiResponse;
import com.pvq.service.OrderService;
import com.pvq.service.ProductService;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private OrderService orderService;

	@PostMapping("/")
	public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest req) {

	    Product product = productService.createProduct(req);
	    return new ResponseEntity<Product>(product, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/{productId}/delete")
	public ResponseEntity<?> deleteProduct(
	        @PathVariable Long productId,
	        @RequestParam(defaultValue = "false") boolean force) {
	    try {
	        // ✅ Nếu có yêu cầu xóa cưỡng chế
	        if (force) {
	            productService.forceDeleteProduct(productId);
	            ApiResponse res = new ApiResponse();
	            res.setMessage("Đã xóa sản phẩm và các dữ liệu liên quan.");
	            res.setStatus(true);
	            return new ResponseEntity<>(res, HttpStatus.OK);
	        }
	
	        // ✅ Xóa bình thường
	        productService.deleteProduct(productId);
	        ApiResponse res = new ApiResponse();
	        res.setMessage("Product deleted successfully");
	        res.setStatus(true);
	        return new ResponseEntity<>(res, HttpStatus.OK);
	
	    } catch (DataIntegrityViolationException ex) {
	        // 🔥 Nếu có lỗi khóa ngoại → trả về danh sách đơn hàng liên quan
	        List<Order> relatedOrders = orderService.findOrdersByProductId(productId);
	
	        Map<String, Object> errorResponse = new HashMap<>();
	        errorResponse.put("message", "Không thể xóa sản phẩm do có đơn hàng liên quan.");
	        errorResponse.put("orders", relatedOrders.stream().map(order -> Map.of(
	                "orderId", order.getId(),
	                "orderStatus", order.getOrderStatus(),
	                "totalPrice", order.getTotalPrice(),
	                "userName", order.getUser() != null
	                        ? order.getUser().getFirstName() + " " + order.getUser().getLastName()
	                        : "N/A"
	        )).toList());
	
	        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	    } catch (Exception e) {
	        ApiResponse res = new ApiResponse();
	        res.setMessage("Lỗi khi xóa sản phẩm: " + e.getMessage());
	        res.setStatus(false);
	        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@DeleteMapping("/{productId}/force-delete")
	public ResponseEntity<ApiResponse> forceDeleteProduct(@PathVariable Long productId) throws ProductException {
	    productService.forceDeleteProduct(productId);
	    ApiResponse res = new ApiResponse();
	    res.setMessage("✅ Đã xóa cưỡng chế sản phẩm và toàn bộ dữ liệu liên quan!");
	    res.setStatus(true);
	    return new ResponseEntity<>(res, HttpStatus.OK);
	}





	
	@GetMapping("/all")
	public ResponseEntity<List<Product>> findAllProduct() {
	    List<Product> products = productService.findAllProducts();
	    return new ResponseEntity<>(products, HttpStatus.OK);
	}

	@PutMapping("/{productId}/update")
	public ResponseEntity<Product> updateProductHandler(
	        @PathVariable Long productId,
	        @RequestBody Product product) throws ProductException {
	    Product updated = productService.updateProduct(productId, product);
	    return new ResponseEntity<>(updated, HttpStatus.OK);
	}
	
	@PostMapping("/creates")
	public ResponseEntity<ApiResponse> createMultipleProduct(
	        @RequestBody CreateProductRequest[] req) {

	    for (CreateProductRequest product : req) {
	        productService.createProduct(product);
	    }

	    ApiResponse res = new ApiResponse();
	    res.setMessage("Products created successfully");
	    res.setStatus(true);

	    return new ResponseEntity<>(res, HttpStatus.CREATED);
	}
	
	
	@GetMapping("/check/{productId}")
    public ResponseEntity<?> checkProductInOrder(@PathVariable Long productId) {
        List<Order> relatedOrders = orderService.findOrdersByProductId(productId);
        Map<String, Object> response = new HashMap<>();
        response.put("exists", !relatedOrders.isEmpty());
        response.put("relatedOrders", relatedOrders.stream().map(order -> Map.of(
                "orderId", order.getId(),
                "orderStatus", order.getOrderStatus(),
                "userName", order.getUser().getFirstName() + " " + order.getUser().getLastName(),
                "totalPrice", order.getTotalPrice()
        )));
        return ResponseEntity.ok(response);
    }


	
	
}
