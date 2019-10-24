package com.hanaset.tacogenji.service;

import com.hanaset.tacocommon.repository.upbit.UpbitPairRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CryptoSelectService {

    private final UpbitPairRepository upbitPairRepository;

    public CryptoSelectService(UpbitPairRepository upbitPairRepository) {
        this.upbitPairRepository = upbitPairRepository;
    }

    public String getPair(String start, String end) {
        List<String> pairList = upbitPairRepository.getCryptoOfSumAmountAndCount(start, end);
        return pairList.get(0);
    }

    public String getCurrentPair(String date) {
        List<String> pairList = upbitPairRepository.getCurrentCryptoOfSumAmountAndCount(date);
        return pairList.get(0);
    }
}
