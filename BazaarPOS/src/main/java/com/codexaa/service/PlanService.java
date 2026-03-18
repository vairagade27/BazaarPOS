package com.codexaa.service;

import com.codexaa.dto.PlanDto;
import com.codexaa.exception.UserExceptions;

import java.util.List;

public interface PlanService {

    List<PlanDto> getAllPlans();

    PlanDto getPlanById(Long id) throws UserExceptions;

    PlanDto createPlan(PlanDto planDto);

    PlanDto updatePlan(Long id, PlanDto planDto) throws UserExceptions;

    void deletePlan(Long id) throws UserExceptions;
}