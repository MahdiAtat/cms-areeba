package com.areeba.cms.cmsmircoservice.rest;

import com.areeba.cms.cmsmicroservice.type.FraudCheckRequest;
import com.areeba.cms.cmsmicroservice.type.FraudCheckResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client to the Fraud microservice.
 */
@FeignClient(name = "fraudmicroservice", url = "${fraud.url}")
public interface FraudClient {

    /**
     * Evaluates a transaction for fraud (amount/frequency rules).
     *
     * @param request payload with cardId, amount, and timestamp
     * @return decision with {@code approved} flag and a {@code reason}
     *
     * @throws feign.FeignException on non-2xx responses or I/O errors
     *                              (e.g., 401/403 if missing/invalid auth)
     */
    @PostMapping("/evaluate")
    FraudCheckResponse evaluate(@RequestBody FraudCheckRequest request);
}
