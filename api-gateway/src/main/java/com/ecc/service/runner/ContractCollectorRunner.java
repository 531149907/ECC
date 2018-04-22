package com.ecc.service.runner;

import com.ecc.api.BlockServiceApi;
import com.ecc.api.ContractServiceApi;
import com.ecc.api.OrdererServiceApi;
import com.ecc.domain.contract.Contract;
import com.ecc.service.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ContractCollectorRunner {
    @Autowired
    ContractServiceApi contractService;
    @Autowired
    BlockService blockService;
    @Autowired
    BlockServiceApi blockServiceApi;
    @Autowired
    OrdererServiceApi ordererService;

    private static final int MAX_CONTRACT_WITHHOLD = 50;
    private static boolean isRunning = false;

    private ContractCollectorRunner() {
    }

    private static class Holder {
        static final ConcurrentHashMap<String, List<Contract>> peerSendBackContracts = new ConcurrentHashMap<>();
        static final ContractCollectorRunner runner = new ContractCollectorRunner();
    }

    public ContractCollectorRunner getRunner() {
        return Holder.runner;
    }

    public ConcurrentHashMap<String, List<Contract>> getSendBackContracts() {
        return Holder.peerSendBackContracts;
    }

    public void addToSendBackContracts(int index, String key, List<Contract> contractList) {
        Holder.peerSendBackContracts.put(key + ":" + index, contractList);
    }

    public void clearSendBackContracts() {
        Holder.peerSendBackContracts.clear();
    }

    public void run() {
        if (!isRunning) {
            new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        System.out.println(this);
                        if (contractService.getContractCount() >= MAX_CONTRACT_WITHHOLD) {
                            List<Contract> contracts = contractService.getTopContracts(MAX_CONTRACT_WITHHOLD);
                            for (Contract contract : contracts) {
                                contractService.deleteContract(contract.getId());
                            }
                            int index = blockServiceApi.getAllBlocks().size() + 1;
                            blockService.broadcastContracts(contracts, index);
                            try {
                                Thread.sleep(30 * 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            verifyContracts(getSendBackContracts());
                            clearSendBackContracts();
                        }
                        try {
                            Thread.sleep(60 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }.run();
        }
    }

    private void verifyContracts(ConcurrentHashMap<String, List<Contract>> concurrentHashMap) {
        HashMap<String, Integer> counter = new HashMap<>();
        Integer index = 0;
        List<Contract> contractList = new ArrayList<>();

        for (Map.Entry<String, List<Contract>> entry : concurrentHashMap.entrySet()) {
            contractList = entry.getValue();
            index = Integer.valueOf(entry.getKey().split(":")[1]);
            for (Contract contract : entry.getValue()) {
                if (contract.getVerifyResult().equals("Y")) {
                    if (!counter.containsKey(contract.getId())) {
                        counter.put(contract.getId(), 1);
                    } else {
                        counter.put(contract.getId(), counter.get(contract.getId()) + 1);
                    }
                }
            }
        }

        /*for(Map.Entry<String,Integer> map:counter.entrySet()){
            if(map.)
        }*/


        //todo: send to orderer-service
        String preHash = blockServiceApi.getBlock(index - 1).hash();
        ordererService.sendImportBlockBroadcast(index, preHash, contractList);
    }
}
