package ru.danil.medicine.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.danil.medicine.dto.PolicyDTO;
import ru.danil.medicine.exception.ObjectNotFoundException;
import ru.danil.medicine.mapper.PolicyMapper;
import ru.danil.medicine.model.Policy;
import ru.danil.medicine.repository.PolicyRepository;


import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PolicyService {
    private final PolicyRepository policyRepository;
    private final PolicyMapper policyMapper;

    @Transactional
    public PolicyDTO create(PolicyDTO policyDTO) {
        Optional<Policy> policy = policyRepository.findById(policyDTO.getPersonId());
        if (policy.isPresent()) return policyMapper.toPolicyDTO(policy.get());
        else return policyMapper.toPolicyDTO(policyRepository.save(policyMapper.toPolicy(policyDTO)));
    }

    @Transactional(readOnly = true)
    public PolicyDTO get(UUID id) {
        Policy policy = policyRepository.findById(id).orElseThrow(() -> {
            log.error("Полис с таким айди не найден: {}", id);
            throw  new ObjectNotFoundException("Полис с таким айди не найден: " + id);
        });
           log.debug("Найден полис: {}", policy.toString());
           return policyMapper.toPolicyDTO(policy);
    }
}
