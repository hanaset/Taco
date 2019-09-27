package com.hanaset.tacogenji.service;

import com.hanaset.tacocommon.repository.TransactionLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CryptoSelectService {

    private final TransactionLogRepository transactionLogRepository;

    public CryptoSelectService(TransactionLogRepository transactionLogRepository) {
        this.transactionLogRepository = transactionLogRepository;
    }

    public String getPair(String start, String end) {
        List<String> pairList = transactionLogRepository.getCryptoOfSumAmountAndCount(start, end);

        for(int i = 0 ; i < pairList.size() ; i++) {
            System.out.println((i+1) + ". " + pairList.get(i));
        }

        return pairList.get(0);
    }
}
