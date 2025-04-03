package ir.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.dto.offer.OfferRequestDto;
import ir.dto.offer.OfferResponseDto;
import ir.service.expert.ExpertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/v1/experts")
public class ExpertController {

    private final ExpertService expertService;

    @PostMapping("/create-offer")
    @Operation(summary = "(Expert only)")
    public ResponseEntity<OfferResponseDto> saveExpertOffer(@Valid @RequestBody OfferRequestDto offerRequestDto,
                                                              @RequestParam String email,
                                                              @RequestParam String password) {
        log.info("Received request to create an offer for expert with email: {}", email);
        log.debug("Offer details: {}", offerRequestDto);

        log.info("Offer successfully created for expert with email: {}", email);
        OfferResponseDto offerResponseDto = expertService.saveOffer(offerRequestDto, email, password);
        return ResponseEntity.ok(offerResponseDto);
    }

    @PutMapping("/update-offer")
    @Operation(summary = "(Expert only) Update an existing offer")
    public ResponseEntity<OfferResponseDto> updateExpertOffer(@Valid @RequestBody OfferRequestDto offerRequestDto,
                                                              @RequestParam String email,
                                                              @RequestParam String password,
                                                              @RequestParam Long offerNumber) {
        log.info("Received request to update offer with number: {} for expert with email: {}", offerNumber, email);
        log.debug("Updated offer details: {}", offerRequestDto);

        log.info("Offer successfully updated for expert with email: {} and offer number: {}", email, offerNumber);
        OfferResponseDto offerResponseDto = expertService.updateOffer(offerNumber, offerRequestDto, email, password);
        return ResponseEntity.ok(offerResponseDto);
    }

    @DeleteMapping("/delete-offer")
    @Operation(summary = "(Expert only)")
    public ResponseEntity<String> deleteExpertOffer(@Valid @RequestParam String email,
                                                    @RequestParam String password,
                                                    @RequestParam Long offerNumber) {
        log.info("Received request to delete offer with number: {} for expert with email: {}", offerNumber, email);

        log.info("Offer with number: {} successfully deleted for expert with email: {}", offerNumber, email);
        expertService.deleteOffer(offerNumber, email, password);
        return ResponseEntity.ok("Offer with number " + offerNumber + " has been successfully deleted.");
    }
}