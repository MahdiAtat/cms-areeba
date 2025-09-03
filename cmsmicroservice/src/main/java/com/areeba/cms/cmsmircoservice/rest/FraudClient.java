package com.areeba.cms.cmsmircoservice.rest;

import com.areeba.cms.cmsmicroservice.type.FraudCheckRequest;
import com.areeba.cms.cmsmicroservice.type.FraudCheckResponse;
import com.areeba.cms.cmsmircoservice.config.FraudFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "fraudmicroservice", url = "${fraud.url}", configuration = FraudFeignConfig.class)
public interface FraudClient {
    @PostMapping("/evaluate")
    FraudCheckResponse evaluate(@RequestBody FraudCheckRequest request);
}
