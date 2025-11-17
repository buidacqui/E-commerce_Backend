package com.pvq.controller;

import com.pvq.model.Address;
import com.pvq.model.User;
import com.pvq.service.AddressService;
import com.pvq.service.UserService;
import com.pvq.exception.UserException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@CrossOrigin(origins = "*")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    // ✅ Lấy danh sách địa chỉ của 1 user
    @GetMapping("/{userId}")
    public ResponseEntity<List<Address>> getAddressesByUser(@PathVariable Long userId) throws UserException {
        User user = userService.findUserById(userId);
        List<Address> addresses = addressService.getAddressesByUser(user);
        return ResponseEntity.ok(addresses);
    }

    // ✅ Tạo mới địa chỉ cho user
    @PostMapping("/{userId}")
    public ResponseEntity<Address> addAddress(@PathVariable Long userId, @RequestBody Address address)
            throws UserException {
        User user = userService.findUserById(userId);
        Address created = addressService.addAddress(user, address);
        return ResponseEntity.ok(created);
    }

    // ✅ Cập nhật địa chỉ
    @PutMapping("/{addressId}")
    public ResponseEntity<Address> updateAddress(@PathVariable Long addressId, @RequestBody Address address)
            throws UserException {
        Address updated = addressService.updateAddress(addressId, address);
        return ResponseEntity.ok(updated);
    }

    // ✅ Xoá địa chỉ
    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok("Address deleted successfully!");
    }
}
