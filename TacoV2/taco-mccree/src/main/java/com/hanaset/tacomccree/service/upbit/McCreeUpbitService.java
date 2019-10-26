package com.hanaset.tacomccree.service.upbit;

import com.hanaset.tacocommon.entity.mccree.McCreeAssetEntity;
import com.hanaset.tacocommon.repository.mccree.McCreeAssetRepository;
import com.hanaset.tacomccree.api.upbit.UpbitMcCreeWebSocketService;
import com.hanaset.tacomccree.config.PairConfig;
import com.hanaset.tacomccree.scheduler.upbit.McCreeUpbitTradeScheduler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class McCreeUpbitService {

    private final McCreeAssetRepository mcCreeAssetRepository;
    private final UpbitMcCreeWebSocketService upbitMcCreeWebSocketService;
    private final McCreeUpbitTradeService mcCreeUpbitTradeService;
    private List<McCreeUpbitTradeScheduler> mcCreeUpbitTradeSchedulers;

    public McCreeUpbitService(McCreeAssetRepository mcCreeAssetRepository,
                              UpbitMcCreeWebSocketService upbitMcCreeWebSocketService,
                              McCreeUpbitTradeService mcCreeUpbitTradeService) {
        this.mcCreeAssetRepository = mcCreeAssetRepository;
        this.upbitMcCreeWebSocketService = upbitMcCreeWebSocketService;
        this.mcCreeUpbitTradeService = mcCreeUpbitTradeService;
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
                    .interval(mcCreeAssetEntity.getInterval())
                    .volume(mcCreeAssetEntity.getVolume())
                    .build()
        ).collect(Collectors.toList());

        return pairConfigs;
    }

    public void startService() {

        List<PairConfig> pairConfigs = setting();
        List<String> pairs = pairConfigs.stream().map(pairConfig -> pairConfig.getBaseAsset() + "-" + pairConfig.getAsset()).collect(Collectors.toList());
        upbitMcCreeWebSocketService.orderbookConnect(pairs);

        mcCreeUpbitTradeSchedulers =  pairConfigs.stream().map(pairConfig -> {
            McCreeUpbitTradeScheduler scheduler = new McCreeUpbitTradeScheduler(mcCreeUpbitTradeService);
            scheduler.startScheduler(pairConfig);
            return scheduler;
        }).collect(Collectors.toList());
    }

    public void finishService() {
        upbitMcCreeWebSocketService.orderbookDisconnect();
        mcCreeUpbitTradeSchedulers.stream().forEach(McCreeUpbitTradeScheduler::stopScheduler);
    }
}
