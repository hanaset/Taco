package com.hanaset.tacoreaper.scheduler;

import com.hanaset.tacocommon.api.probit.ProbitAuthRestClient;
import com.hanaset.tacocommon.cache.probit.ProbitAuth;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReaperProbitAuthScheduler {

    private final ProbitAuthRestClient probitAuthRestClient;

    public ReaperProbitAuthScheduler(ProbitAuthRestClient probitAuthRestClient) {
        this.probitAuthRestClient = probitAuthRestClient;
    }

    @Scheduled(fixedDelay = 1000 * 60)
    public void getAuthToken() {
        ProbitAuth.authToken = probitAuthRestClient.getToken();
    }
}
