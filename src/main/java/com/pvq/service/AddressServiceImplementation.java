package com.pvq.service;

import com.pvq.model.Address;
import com.pvq.model.User;
import com.pvq.repository.AddressRepository;
import com.pvq.exception.UserException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImplementation implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public Address addAddress(User user, Address address) {
        address.setUser(user);
        return addressRepository.save(address);
    }

    @Override
    public List<Address> getAddressesByUser(User user) {
        return addressRepository.findByUser(user);
    }

    @Override
    public Address updateAddress(Long addressId, Address newAddress) {
        Address existing = addressRepository.findById(addressId).orElseThrow();
        existing.setFirstName(newAddress.getFirstName());
        existing.setLastName(newAddress.getLastName());
        existing.setStreetAddress(newAddress.getStreetAddress());
        existing.setCity(newAddress.getCity());
        existing.setState(newAddress.getState());
        existing.setZipCode(newAddress.getZipCode());
        existing.setMobile(newAddress.getMobile());
        return addressRepository.save(existing);
    }

    @Override
    public void deleteAddress(Long addressId) {
        addressRepository.deleteById(addressId);
    }
}
