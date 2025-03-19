package ir.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.dto.offer.OfferRequestDto;
import ir.dto.offer.OfferResponseDto;
import ir.service.expert.ExpertService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/experts")
public class ExpertController {

    private final ExpertService expertService;

    @PostMapping("/create-offer")
    @Operation(summary = "(Expert only)")
    public ResponseEntity<OfferResponseDto> saveExpertOffer(@Valid @RequestBody OfferRequestDto offerRequestDto,
                                                              @RequestParam String email,
                                                              @RequestParam String password) {
        OfferResponseDto offerResponseDto = expertService.saveOffer(offerRequestDto, email, password);
        return ResponseEntity.ok(offerResponseDto);
    }
}
