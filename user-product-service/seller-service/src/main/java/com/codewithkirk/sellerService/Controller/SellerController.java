package com.codewithkirk.sellerService.Controller;

import com.codewithkirk.sellerService.Dto.SellerDto;
import com.codewithkirk.sellerService.Exception.SellerException;
import com.codewithkirk.sellerService.Exception.SellerNotFoundException;
import com.codewithkirk.sellerService.Model.Sellers;
import com.codewithkirk.sellerService.Service.SellerService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SellerController {

    private final SellerService sellerService;

    @PostMapping("/sellers/register")
    public ResponseEntity<?> registerSeller(@RequestBody SellerDto sellerDto) {
        try {
            sellerService.registerSeller(sellerDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(null);
        } catch (SellerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }  catch (FeignException.NotFound e) {
            // Specific handling for 404 errors (Seller not found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FeignException e) {
            // Handle other types of Feign errors (500 Internal Server Error, etc.)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error communicating with User Service: " +  e.getMessage());
        } catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }
    }

//    @GetMapping("/sellers/{id}")
    public ResponseEntity<?> getSellerById(
            @PathVariable Long id) {

        try {
            Optional<Sellers> seller = sellerService.getSellerById(id);
            Sellers foundSeller  = seller.get();
            SellerDto responseDTO = new SellerDto(
                    foundSeller.getSellerId(),
                    foundSeller.getUserId(),
                    foundSeller.getSellerName(),
                    foundSeller.getStoreName(),
                    foundSeller.getEmail(),
                    foundSeller.getPhoneNumber(),
                    foundSeller.getLocation(),
                    foundSeller.getStatus(),
                    foundSeller.getPhotoUrl()
            );
            return ResponseEntity.ok(responseDTO);

        }  catch (SellerException e) {
            // Handle SellerException Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FeignException.NotFound e) {
            // Specific handling for 404 errors (Seller not found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FeignException e) {
            // Handle other types of Feign errors (500 Internal Server Error, etc.)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error communicating with User Service: " +  e.getMessage());
        }catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }

    }

    @GetMapping("/sellers/{userId}/{sellerId}")
    public ResponseEntity<?> getSellerByUserIdAndSellerId(
                                    @PathVariable Long userId,
                                    @PathVariable Long sellerId) {

        try {
            Optional<Sellers> seller = sellerService.getSellerByUserIdAndSellerId(userId, sellerId);
            Sellers foundSeller  = seller.get();
            SellerDto responseDTO = new SellerDto(
                    foundSeller.getSellerId(),
                    foundSeller.getUserId(),
                    foundSeller.getSellerName(),
                    foundSeller.getStoreName(),
                    foundSeller.getEmail(),
                    foundSeller.getPhoneNumber(),
                    foundSeller.getLocation(),
                    foundSeller.getStatus(),
                    foundSeller.getPhotoUrl()
            );
            return ResponseEntity.ok(responseDTO);

        }  catch (SellerException e) {
            // Handle SellerException Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }

    }

    @PutMapping("/sellers/{userId}/{sellerId}")
    public ResponseEntity<?> updateSeller(
                                    @PathVariable Long userId,
                                    @PathVariable Long sellerId,
                                    @RequestBody SellerDto sellerDto) {
        try {
            // Call the service to update the seller
            sellerService.updateSeller(userId, sellerId, sellerDto);
            // Return the updated seller as the response
            return ResponseEntity.ok(null);
        } catch (SellerNotFoundException e) {
            // Handle SellerException Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SellerException e) {
            // Handle SellerException Not Found
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }  catch (FeignException.NotFound e) {
            // Specific handling for 404 errors (Seller not found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FeignException e) {
            // Handle other types of Feign errors (500 Internal Server Error, etc.)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error communicating with User Service: " +  e.getMessage());
        }catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }
    }

    @PutMapping("/sellers/safe/delete/user/{userId}/seller/{sellerId}")
    public ResponseEntity<?> safeDeleteSeller(@PathVariable Long userId,
                                              @PathVariable Long sellerId) {
        try {
            sellerService.safeDeleteSeller(userId, sellerId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (SellerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FeignException.NotFound e) {
            // Specific handling for 404 errors (Seller not found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FeignException e) {
            // Handle other types of Feign errors (500 Internal Server Error, etc.)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error communicating with User Service: " +  e.getMessage());
        }catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }
    }

    @DeleteMapping("/sellers/force/delete/user/{userId}/seller/{sellerId}")
    public ResponseEntity<?> forceDeleteSeller(@PathVariable Long userId,
                                              @PathVariable Long sellerId) {
        try {
            sellerService.forceDeleteSeller(userId, sellerId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (SellerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }  catch (FeignException.NotFound e) {
            // Specific handling for 404 errors (Seller not found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FeignException e) {
            // Handle other types of Feign errors (500 Internal Server Error, etc.)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error communicating with User Service: " +  e.getMessage());
        }catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }
    }

}
