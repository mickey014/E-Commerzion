package com.codewithkirk.sellerService.Service.impl;

import com.codewithkirk.sellerService.Dto.SellerDto;
import com.codewithkirk.sellerService.Exception.SellerException;
import com.codewithkirk.sellerService.Exception.SellerNotFoundException;
import com.codewithkirk.sellerService.Model.Sellers;
import com.codewithkirk.sellerService.Model.Sellers.SellerStatus;
import com.codewithkirk.sellerService.Repository.SellerRepository;
import com.codewithkirk.sellerService.Service.SellerService;
import com.codewithkirk.sellerService.ServiceClient.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SellerServiceImp implements SellerService {

    private final SellerRepository sellerRepository;
    private final UserServiceClient userServiceClient;  // Inject Feign client

    @Override
    public Sellers registerSeller(SellerDto sellerDto) {
        userServiceClient.getUserById(sellerDto.getUserId());

        Long userId = sellerDto.getUserId();
        String sellerName = sellerDto.getSellerName().replaceAll("\\s+", " ").trim();
        String storeName = sellerDto.getStoreName().replaceAll("\\s+", " ").trim();
        String email = sellerDto.getEmail().replaceAll("\\s+", " ").trim();
        String phoneNumber = sellerDto.getPhoneNumber().replaceAll("\\s+", " ").trim();
        String location = sellerDto.getLocation().replaceAll("\\s+", " ").trim();
        String photoUrl = sellerDto.getPhotoUrl();

        if(sellerExists(userId)) {
            throw new SellerException("Seller is currently registered.");
        }

        // Validate required fields
        if (sellerName.isEmpty()) {
            throw new SellerException("Name is required.");
        }

        if(storeName.isEmpty()) {
            throw new SellerException("Username is required.");
        }

        // Validate email format
        if (!isValidEmail(email)) {
            throw new SellerException("Email must be valid ex. (user@gmail.com)");
        }

        if(emailExists(email)) {
            throw new SellerException("Email already exists.");
        }

        if(phoneNumber.length() > 13) {
            throw new SellerException("Phone number must be valid. ex(09123456789) or (+639)");
        }

        if(phoneNumberExists(phoneNumber)) {
            throw new SellerException("Phone number already exists.");
        }

        if(location.isEmpty()) {
            throw new SellerException("Location is required.");
        }

        // Build the new user object using the Builder pattern
        Sellers newSeller  = Sellers.builder()
                .userId(userId)
                .sellerName(sellerName)
                .storeName(storeName)
                .email(email)
                .phoneNumber(phoneNumber)
                .location(location)
                .photoUrl(photoUrl)
                .status(Sellers.SellerStatus.ACTIVE) // Convert string to enum
                .build();
        sellerRepository.save(newSeller);

        return newSeller;
    }

    @Override
    public Optional<Sellers> getSellerByUserIdAndSellerId(Long userId, Long sellerId) {
        return Optional.ofNullable(sellerRepository.findSellerByUserIdAndSellerId(
                userId, sellerId).orElseThrow(() -> new SellerException("Seller not found.")));
    }

    @Override
    public Optional<Sellers> getSellerById(Long id) {
//        userServiceClient.getUserById(null);
        return Optional.ofNullable(sellerRepository.findById(
                id).orElseThrow(() -> new SellerException("Seller not found.")));
    }

    @Override
    public void updateSeller(Long userId, Long sellerId, SellerDto sellerDto) {
        userServiceClient.getUserById(userId);
        Sellers seller = sellerRepository.findSellerByUserIdAndSellerId(userId, sellerId)
                .orElseThrow(() -> new SellerNotFoundException("Seller not found."));

        String sellerName = sellerDto.getSellerName().replaceAll("\\s+", " ").trim();
        String storeName = sellerDto.getStoreName().replaceAll("\\s+", " ").trim();
        String email = sellerDto.getEmail().replaceAll("\\s+", " ").trim();
        String phoneNumber = sellerDto.getPhoneNumber().replaceAll("\\s+", " ").trim();
        String location = sellerDto.getLocation().replaceAll("\\s+", " ").trim();
        String photoUrl = sellerDto.getPhotoUrl();

        if(!seller.getUserId().equals(userId)) {
            if(sellerExists(userId)) {
                throw new SellerException("Seller is currently registered.");
            }
        }


        // Validate required fields
        if (sellerName.isEmpty()) {
            throw new SellerException("Name is required.");
        }

        if(storeName.isEmpty()) {
            throw new SellerException("Username is required.");
        }

        // Validate email format
        if (!isValidEmail(email)) {
            throw new SellerException("Email must be valid ex. (user@gmail.com)");
        }

        if(!seller.getEmail().equals(email)) {
            if (emailExists(sellerDto.getEmail())) {
                throw new SellerException("Email already exists.");
            }
        }

        if(phoneNumber.length() > 13) {
            throw new SellerException("Phone number must be valid. ex(09123456789) or (+639)");
        }

        if(!seller.getPhoneNumber().equals(phoneNumber)) {
            if (phoneNumberExists(sellerDto.getPhoneNumber())) {
                throw new SellerException("Phone number already exists.");
            }
        }

        if(location.isEmpty()) {
            throw new SellerException("Location is required.");
        }

        seller.setSellerName(sellerName);
        seller.setStoreName(storeName);
        seller.setEmail(email);
        seller.setPhoneNumber(phoneNumber);
        seller.setLocation(location);
        seller.setPhotoUrl(photoUrl);
        seller.setLocation(location);
        SellerStatus status = SellerStatus.valueOf(sellerDto.getStatus().toString());
        seller.setStatus(status);

        sellerRepository.save(seller);
    }

    @Override
    public void safeDeleteSeller(Long userId, Long sellerId) {
        userServiceClient.getUserById(userId);
        Sellers existingUser  = sellerRepository.findSellerByUserIdAndSellerId(userId, sellerId)
                .orElseThrow(() -> new SellerException("Seller not found."));
        existingUser.setStatus(SellerStatus.INACTIVE); // Set status to INACTIVE
        sellerRepository.save(existingUser);
    }

    @Override
    public void forceDeleteSeller(Long userId, Long sellerId) {
        userServiceClient.getUserById(userId);
        Sellers existingUser  = sellerRepository.findSellerByUserIdAndSellerId(userId, sellerId)
                .orElseThrow(() -> new SellerException("Seller not found."));
        sellerRepository.delete(existingUser);
    }

    // Check if the phoneNumber exists
    public boolean sellerExists(Long seller) {
        return sellerRepository.findByUserId(seller).isPresent();
    }

    // Check if the phoneNumber exists
    public boolean phoneNumberExists(String phoneNumber) {
        String sanitized_phone_number = phoneNumber.trim(); // Normalize the email
        return sellerRepository.findByPhoneNumber(sanitized_phone_number).isPresent();
    }

    // Check if the email exists
    public boolean emailExists(String email) {
        String sanitized_email = email.trim().toLowerCase(); // Normalize the email
        return sellerRepository.findByEmail(sanitized_email).isPresent();
    }

    // validate email format
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
}
