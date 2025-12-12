package com.abc.postpaid.billing.controller;

import com.abc.postpaid.billing.dto.UsageRecordRequest;
import com.abc.postpaid.billing.dto.UsageRecordResponse;
import com.abc.postpaid.billing.service.UsageRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/services")
public class UsageController {

    @Autowired
    private UsageRecordService usageRecordService;

    private boolean isAdmin(Authentication auth) {
        if (auth == null) return false;
        Collection<? extends GrantedAuthority> auths = auth.getAuthorities();
        if (auths == null) return false;
        return auths.stream().anyMatch(a -> String.valueOf(a.getAuthority()).equalsIgnoreCase("ROLE_ADMIN"));
    }

    @GetMapping("/{id}/usage")
    public ResponseEntity<List<UsageRecordResponse>> getUsageRecords(@PathVariable Long id) {
        List<UsageRecordResponse> records = usageRecordService.listUsageRecordsByService(id);
        return ResponseEntity.ok(records);
    }

    @PostMapping("/{id}/usage")
    public ResponseEntity<?> createUsageRecord(@PathVariable Long id, @RequestBody UsageRecordRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAdmin(auth)) {
            return ResponseEntity.status(403).build();
        }

        try {
            Long usageId = usageRecordService.createUsageRecord(id, request);
            return ResponseEntity.status(201).body("{\"usageId\":" + usageId + "}");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).build();
        }
    }
}
