package com.codexaa.service.impl;

import com.codexaa.dto.PlanDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.mapper.PlanMapper;
import com.codexaa.model.Plan;
import com.codexaa.repository.PlanRepository;
import com.codexaa.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    @Override
    public List<PlanDto> getAllPlans() {
        return planRepository.findAll()
                .stream()
                .map(PlanMapper::toDTO)
                .toList();
    }

    @Override
    public PlanDto getPlanById(Long id) throws UserExceptions {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new UserExceptions("Plan not found with id: " + id));
        return PlanMapper.toDTO(plan);
    }

    @Override
    public PlanDto createPlan(PlanDto planDto) {
        Plan plan = PlanMapper.toEntity(planDto);
        return PlanMapper.toDTO(planRepository.save(plan));
    }

    @Override
    public PlanDto updatePlan(Long id, PlanDto planDto) throws UserExceptions {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new UserExceptions("Plan not found with id: " + id));

        if (planDto.getName() != null && !planDto.getName().isBlank()) {
            plan.setName(planDto.getName());
        }
        if (planDto.getPrice() != null) {
            plan.setPrice(planDto.getPrice());
        }
        if (planDto.getFeatures() != null && !planDto.getFeatures().isEmpty()) {
            plan.setFeatures(planDto.getFeatures());
        }
        if (planDto.getStatus() != null) {
            plan.setStatus(planDto.getStatus());
        }

        return PlanMapper.toDTO(planRepository.save(plan));
    }

    @Override
    public void deletePlan(Long id) throws UserExceptions {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new UserExceptions("Plan not found with id: " + id));
        planRepository.delete(plan);
    }
}