package ir.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.dto.offer.OfferRequestDto;
import ir.dto.offer.OfferResponseDto;
import ir.dto.order.OrderResponseDto;
import ir.service.expert.ExpertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/v1/experts")
public class ExpertController {

    private final ExpertService expertService;

    @PostMapping("/create-offer")
    @Operation(summary = "(Expert only)")
    public ResponseEntity<OfferResponseDto> saveExpertOffer(@RequestHeader("Authorization") String token,
                                                            @Valid @RequestBody OfferRequestDto offerRequestDto) {
        log.debug("Offer details: {}", offerRequestDto);

        OfferResponseDto offerResponseDto = expertService.saveOffer(offerRequestDto, token);
        return ResponseEntity.ok(offerResponseDto);
    }

    @PutMapping("/update-offer")
    @Operation(summary = "(Expert only) Update an existing offer")
    public ResponseEntity<OfferResponseDto> updateExpertOffer(@RequestHeader("Authorization") String token,
                                                              @Valid @RequestBody OfferRequestDto offerRequestDto,
                                                              @RequestParam Long offerNumber) {
        log.info("Received request to update offer with number: {} for expert with token", offerNumber);
        log.debug("Updated offer details: {}", offerRequestDto);

        log.info("Offer successfully updated for expert with token and offer number: {}", offerNumber);
        OfferResponseDto offerResponseDto = expertService.updateOffer(offerNumber, offerRequestDto, token);
        return ResponseEntity.ok(offerResponseDto);
    }

    @DeleteMapping("/delete-offer")
    @Operation(summary = "(Expert only)")
    public ResponseEntity<String> deleteExpertOffer(@RequestHeader("Authorization") String token,
                                                    @RequestParam Long offerNumber) {
        log.info("Received request to delete offer with number: {} for expert with token", offerNumber);

        log.info("Offer with number: {} successfully deleted for expert with token", offerNumber);
        expertService.deleteOffer(offerNumber, token);
        return ResponseEntity.ok("Offer with number " + offerNumber + " has been successfully deleted.");
    }
}