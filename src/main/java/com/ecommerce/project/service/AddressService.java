package com.ecommerce.project.service;

import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;

import java.util.List;

public interface AddressService {

    AddressDTO createNewAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddressById(Long addressId);

    AddressDTO updateAddressById(AddressDTO addressDTO, Long addressId);

    String deleteAddressById(Long addressId);

    List<AddressDTO> getAddressByUser(User user);
}
