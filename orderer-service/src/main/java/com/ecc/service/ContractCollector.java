package com.ecc.service;

import com.ecc.domain.contract.Contract;
import com.ecc.web.api.PeerApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class ContractCollector {
    @Autowired
    PeerApi peerApi;

    private static HashMap<String, Contract> contractPool = new HashMap<>();
    private static ContractCollector instance = new ContractCollector();
    private boolean isRunning = false;

    public static ContractCollector getInstance() {
        return instance;
    }

    private ContractCollector() {
    }

    public void collect() {
        if (!isRunning) {
            new Worker().start();
            isRunning = true;
        }
    }

    public void addToPool(Contract contract) {
        contractPool.put(contract.getId(), contract);
    }

    private class Worker extends Thread {
        @Override
        public void run() {
            while (true) {
                if (contractPool.size() >= 10) {
                    List<Contract> contractCollection = new ArrayList<>();
                    int i = 0;
                    for (String id : contractPool.keySet()) {
                        if (i == 10) {
                            break;
                        }
                        contractCollection.add(contractPool.get(id));
                        contractPool.remove(id);
                        i++;
                    }
                    for (Contract contract : contractCollection) {
                        for (int duplication = 0; duplication < 3; duplication++) {
                            //一份 contract 发送给 3个 peer去验证
                            peerApi.sendAndVerify(contract);
                        }
                    }
                }
            }
        }
    }
}
