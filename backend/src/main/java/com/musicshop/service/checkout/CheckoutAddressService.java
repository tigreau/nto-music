package com.musicshop.service.checkout;

import com.musicshop.dto.checkout.CheckoutRequest;
import com.musicshop.model.address.Address;
import com.musicshop.repository.address.AddressRepository;
import org.springframework.stereotype.Service;

@Service
public class CheckoutAddressService {

    private final AddressRepository addressRepository;

    public CheckoutAddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address createShippingAddress(CheckoutRequest request) {
        Address address = new Address();
        address.setStreet(request.getStreet());
        address.setNumber(request.getNumber());
        address.setPostalCode(request.getPostalCode());
        address.setCity(request.getCity());
        address.setCountry(request.getCountry());
        return addressRepository.save(address);
    }
}
