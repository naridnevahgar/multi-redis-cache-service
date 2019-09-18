package com.rs.multirediscacheservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataController {

    private final DataService dataService;

    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping(value = "/policies/{prodCd}/{policyNumber}", produces = "application/json")
    public ResponseEntity<String> getPolicy(@PathVariable("prodCd") String productCode, @PathVariable("policyNumber") String policyNumber) {
        String policyRepresentation = dataService.getPolicyRepresentation(productCode, policyNumber);

        if (policyRepresentation != null) {
            return ResponseEntity.ok(policyRepresentation);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/policies/cache")
    public ResponseEntity<?> updateCache() {
        if (dataService.updateCache()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
