package com.pvq.service;

import com.pvq.model.Address;
import com.pvq.model.User;
import com.pvq.exception.UserException;

import java.util.List;

public interface AddressService {
    Address addAddress(User user, Address address);
    List<Address> getAddressesByUser(User user);
    Address updateAddress(Long addressId, Address address);
    void deleteAddress(Long addressId);
}
