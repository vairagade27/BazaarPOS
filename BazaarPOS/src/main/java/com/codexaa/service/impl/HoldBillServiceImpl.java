
package com.codexaa.service.impl;

import com.codexaa.dto.HoldBillDto;
import com.codexaa.exception.UserExceptions;
import com.codexaa.service.HoldBillService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class HoldBillServiceImpl implements HoldBillService {

    // key = "storeId:cashierId:holdId"
    private final Map<String, HoldBillDto> store = new ConcurrentHashMap<>();

    @Override
    public HoldBillDto holdBill(Long storeId, Long cashierId, HoldBillDto.Request req)
            throws UserExceptions {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new UserExceptions("Cannot hold an empty cart");
        }
        String holdId = UUID.randomUUID().toString();
        HoldBillDto dto = HoldBillDto.builder()
                .holdId(holdId)
                .storeId(storeId)
                .cashierId(cashierId)
                .label(req.getLabel())
                .discount(req.getDiscount())
                .discountType(req.getDiscountType())
                .notes(req.getNotes())
                .heldAt(LocalDateTime.now())
                .items(req.getItems())
                .build();
        store.put(key(storeId, cashierId, holdId), dto);
        return dto;
    }

    @Override
    public List<HoldBillDto> getHeldBills(Long storeId, Long cashierId)
            throws UserExceptions {
        String prefix = storeId + ":" + cashierId + ":";
        return store.entrySet().stream()
                .filter(e -> e.getKey().startsWith(prefix))
                .map(Map.Entry::getValue)
                .sorted(Comparator.comparing(HoldBillDto::getHeldAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public void discardHeldBill(Long storeId, Long cashierId, String holdId)
            throws UserExceptions {
        String k = key(storeId, cashierId, holdId);
        if (store.remove(k) == null) {
            throw new UserExceptions("Held bill not found: " + holdId);
        }
    }

    private String key(Long storeId, Long cashierId, String holdId) {
        return storeId + ":" + cashierId + ":" + holdId;
    }
}