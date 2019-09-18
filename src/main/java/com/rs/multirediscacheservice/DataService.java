package com.rs.multirediscacheservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import com.rs.multirediscacheservice.cache.CacheManager;

import com.rs.multirediscacheservice.model.Policy;
import com.rs.multirediscacheservice.model.PolicyKey;
import com.rs.multirediscacheservice.model.PolicyRepo;
import org.javatuples.Quartet;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DataService {

    private final PolicyRepo policyRepo;
    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DataService(PolicyRepo policyRepo, CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.policyRepo = policyRepo;
    }

    @Cacheable()
    public String getPolicyRepresentation(String productCode, String policyNumber) {
        log.info("Cache miss - Fetching from DB");
        Policy policy = this.policyRepo.findById(new PolicyKey(productCode, policyNumber)).orElse(null);

        if (policy != null) {
            try {
                return objectMapper.writeValueAsString(policy);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public boolean updateCache() {
        log.debug("Started Cache Update");
        try {
            // Quartet<RedisCacheManager, RedisCacheManager, RedisTemplate, RedisTemplate>
            // P, B, P, B
            // P-Primary, B-Backup
            Quartet<RedisCacheManager, RedisCacheManager, RedisTemplate, RedisTemplate> currentStateQuartet = this.cacheManager.currentState();


            // Dump latest data into Backup first
            Map<String, String> data = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            policyRepo.findAll().forEach(entry -> {
                try {
                    data.put("SCVPOL::" + entry.getPolicyKey().getProductCode() + ":" + entry.getPolicyKey().getPolicyNumber(), mapper.writeValueAsString(entry));
                } catch (Exception e) { }
            });
            currentStateQuartet.getValue3().opsForValue().multiSet(data);

            // Make it Primary
            currentStateQuartet.getValue3().opsForValue().set("primary", "yes");

            // Make current primary as backup
            currentStateQuartet.getValue2().opsForValue().set("primary", "no");

        } catch (Exception e) {
            log.error("Error", e);
            return false;
        }

        return true;
    }
}
