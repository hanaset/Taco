package com.hanaset.tacogenji.service;

import com.hanaset.tacocommon.repository.PairRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CryptoSelectService {

    private final PairRepository pairRepository;

    public CryptoSelectService(PairRepository pairRepository) {
        this.pairRepository = pairRepository;
    }

    public String getPair(String start, String end) {
        List<String> pairList = pairRepository.getCryptoOfSumAmountAndCount(start, end);
        return pairList.get(0);
    }

    public String getCurrentPair(String date) {
        List<String> pairList = pairRepository.getCurrentCryptoOfSumAmountAndCount(date);
        return pairList.get(0);
    }
}
