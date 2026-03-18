package com.codexaa.controller;

import com.codexaa.dto.PlanDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/super-admin/plans")
public class PlanController {

    private final PlanService planService;

    @GetMapping
    public ResponseEntity<List<PlanDto>> getAllPlans() {
        return ResponseEntity.ok(planService.getAllPlans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanDto> getPlanById(@PathVariable Long id) throws UserExceptions {
        return ResponseEntity.ok(planService.getPlanById(id));
    }

    @PostMapping
    public ResponseEntity<PlanDto> createPlan(@RequestBody PlanDto planDto) {
        return ResponseEntity.ok(planService.createPlan(planDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanDto> updatePlan(
            @PathVariable Long id,
            @RequestBody PlanDto planDto
    ) throws UserExceptions {
        return ResponseEntity.ok(planService.updatePlan(id, planDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlan(@PathVariable Long id) throws UserExceptions {
        planService.deletePlan(id);
        return ResponseEntity.ok("Plan deleted successfully");
    }
}