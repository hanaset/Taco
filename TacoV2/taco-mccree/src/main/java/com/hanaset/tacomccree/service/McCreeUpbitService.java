package com.hanaset.tacomccree.service;

import com.hanaset.tacocommon.entity.mccree.McCreeAssetEntity;
import com.hanaset.tacocommon.repository.mccree.McCreeAssetRepository;
import com.hanaset.tacomccree.api.upbit.UpbitMcCreeWebSocketClient;
import com.hanaset.tacomccree.api.upbit.UpbitMcCreeWebSocketService;
import com.hanaset.tacomccree.config.PairConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class McCreeUpbitService {

    private final McCreeAssetRepository mcCreeAssetRepository;
    private final UpbitMcCreeWebSocketService upbitMcCreeWebSocketService;

    public McCreeUpbitService(McCreeAssetRepository mcCreeAssetRepository,
                              UpbitMcCreeWebSocketService upbitMcCreeWebSocketService) {
        this.mcCreeAssetRepository = mcCreeAssetRepository;
        this.upbitMcCreeWebSocketService = upbitMcCreeWebSocketService;
    }

    private List<PairConfig> setting() {

        List<McCreeAssetEntity> mcCreeAssetEntityList = mcCreeAssetRepository.findByEnable(true);

        List<PairConfig> pairConfigs = mcCreeAssetEntityList.stream()
                .filter(mcCreeAssetEntity -> mcCreeAssetEntity.getExchange().equals("upbit"))
                .map(mcCreeAssetEntity ->
            PairConfig.builder()
                    .exchange(mcCreeAssetEntity.getExchange())
                    .asset(mcCreeAssetEntity.getAsset())
                    .baseAsset(mcCreeAssetEntity.getBaseAsset())
                    .askPrice(mcCreeAssetEntity.getAskPrice())
                    .bidPrice(mcCreeAssetEntity.getBidPrice())
                    .fee(mcCreeAssetEntity.getFee())
                    .volume(mcCreeAssetEntity.getVolume())
                    .build()
        ).collect(Collectors.toList());

        return pairConfigs;
    }

    public void startService() {
        List<String> pairs = setting().stream().map(pairConfig -> pairConfig.getBaseAsset() + "-" + pairConfig.getAsset()).collect(Collectors.toList());
        upbitMcCreeWebSocketService.orderbookConnect(pairs);
    }

    public void finishService() {
        upbitMcCreeWebSocketService.orderbookDisconnect();
    }
}
