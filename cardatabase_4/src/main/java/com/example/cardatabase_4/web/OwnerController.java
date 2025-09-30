package com.example.cardatabase_4.web;

import com.example.cardatabase_4.domain.Owner;
import com.example.cardatabase_4.service.OwnerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OwnerController {
    private final OwnerService ownerService;
    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService; }

    // 1. 모든 Owner 조회
    @GetMapping("/owners")
    public List<Owner> getOwners() {
        return ownerService.getOwners();
    }

    // 2. id 별 조회
    @GetMapping("/owners/{ownerId}")
    public ResponseEntity<Owner> getOwnerById(@PathVariable Long ownerId) {
        return ownerService.getOwnerById(ownerId)
                .map(owner -> ResponseEntity.ok().body(owner))
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Owner 객체 추가 (POST / api/owners)
    @PostMapping("/owners")
    public ResponseEntity<Owner> addOwner(@RequestBody Owner owner) {
        Owner savedOwner = ownerService.addOwner(owner);

        return new ResponseEntity<>(savedOwner, HttpStatus.CREATED);
    }

    // 4. Owner 객체 삭제
    @DeleteMapping("/owners/{ownerId}")
    public ResponseEntity<Void> deleteOwner(@PathVariable Long ownerId) {
        if(ownerService.deleteOwner(ownerId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 5. Owner 객체 수정
    @PutMapping("/owners/{ownerId}")
    public ResponseEntity<Owner> updateOwner(@PathVariable Long ownerId, @RequestBody Owner ownerDetails) {
        return ownerService.updateOwner(ownerId, ownerDetails)
                .map(updateOwner -> ResponseEntity.ok().body(updateOwner))
                .orElse(ResponseEntity.notFound().build());
    }
}
