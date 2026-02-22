package com.musicshop.service.user;

import com.musicshop.dto.user.UpdateUserRequest;
import com.musicshop.dto.user.UserDTO;
import com.musicshop.model.address.Address;
import com.musicshop.exception.ResourceNotFoundException;
import com.musicshop.mapper.UserMapper;
import com.musicshop.model.user.User;
import com.musicshop.model.user.UserAddress;
import com.musicshop.repository.address.AddressRepository;
import com.musicshop.repository.user.UserAddressRepository;
import com.musicshop.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final AddressRepository addressRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository,
            UserAddressRepository userAddressRepository,
            AddressRepository addressRepository,
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userAddressRepository = userAddressRepository;
        this.addressRepository = addressRepository;
        this.userMapper = userMapper;
    }

    public Optional<UserDTO> getUser(Long userId) {
        return userRepository.findById(userId).map(this::buildUserView);
    }

    public UserDTO updateUser(Long userId, UpdateUserRequest request) {
        return userRepository.findById(userId).map(user -> {
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setPhoneNumber(request.getPhoneNumber());
            User savedUser = userRepository.save(user);
            upsertUserAddress(savedUser, request);
            return buildUserView(savedUser);
        }).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public Long findUserIdByEmail(String email) {
        return findByEmail(email).getId();
    }

    private UserDTO buildUserView(User user) {
        UserAddress userAddress = userAddressRepository.findFirstByUserId(user.getId()).orElse(null);
        Address address = userAddress != null ? userAddress.getAddress() : null;
        return userMapper.toUserDTO(user, address);
    }

    private void upsertUserAddress(User user, UpdateUserRequest request) {
        UserAddress relation = userAddressRepository.findFirstByUserId(user.getId()).orElse(null);
        Address address = relation != null ? relation.getAddress() : new Address();
        address.setStreet(request.getStreet());
        address.setNumber(request.getNumber());
        address.setPostalCode(request.getPostalCode());
        address.setCity(request.getCity());
        address.setCountry(request.getCountry());
        Address savedAddress = addressRepository.save(address);

        if (relation == null) {
            UserAddress newRelation = new UserAddress();
            newRelation.setUser(user);
            newRelation.setAddress(savedAddress);
            userAddressRepository.save(newRelation);
        }
    }
}
