package com.ecommerce.project.controllers;

import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.service.AddressService;
import com.ecommerce.project.utils.AuthUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name="Address APIs",description="APIs for managing addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createNewAddress(@Valid @RequestBody AddressDTO addressDTO){
        User user = authUtil.loggedInUser();
        return new ResponseEntity<>(addressService.createNewAddress(addressDTO,user), HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses(){
        return new ResponseEntity<>(addressService.getAllAddresses(), HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId){
        return new ResponseEntity<>(addressService.getAddressById(addressId), HttpStatus.OK);
    }

    @GetMapping("/user/addresses")
    public ResponseEntity<List<AddressDTO>> getAddressByUser(){
        User user = authUtil.loggedInUser();
        return new ResponseEntity<>(addressService.getAddressByUser(user), HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddressById(@Valid @RequestBody AddressDTO addressDTO, @PathVariable Long addressId){
        return new ResponseEntity<>(addressService.updateAddressById(addressDTO, addressId), HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddressById(@PathVariable Long addressId){
        addressService.deleteAddressById(addressId);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

}
