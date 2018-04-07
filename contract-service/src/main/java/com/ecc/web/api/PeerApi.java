package com.ecc.web.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@Component
@FeignClient("peer")
public interface PeerApi {

}
