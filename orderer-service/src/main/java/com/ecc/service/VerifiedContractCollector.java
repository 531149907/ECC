package com.ecc.service;

import com.ecc.domain.contract.Contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VerifiedContractCollector {
    private static HashMap<String, List<Contract>> verifiedContractPool = new HashMap<>();
    private static VerifiedContractCollector instance = new VerifiedContractCollector();
    private boolean isRunning = false;

    public static VerifiedContractCollector getInstance() {
        return instance;
    }

    private VerifiedContractCollector() {
    }

    public void collect() {
        if (!isRunning) {
            new Worker().start();
            isRunning = true;
        }
    }

    public void addToPool(Contract contract) {
        List<Contract> temp = new ArrayList<>();
        if (verifiedContractPool.containsKey(contract.getId())) {
            temp = verifiedContractPool.get(contract.getId());
            temp.add(contract);
            verifiedContractPool.replace(contract.getId(), temp);
        } else {
            temp.add(contract);
            verifiedContractPool.put(contract.getId(), temp);
        }
    }

    private class Worker extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(20 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                HashMap<String, List<Contract>> tempList = (HashMap<String, List<Contract>>) verifiedContractPool.clone();
                verifiedContractPool.clear();
                List<Contract> verifiedContracts = new ArrayList<>();
                for (String id : tempList.keySet()) {
                    List<Contract> temp = tempList.get(id);
                    int resultCount = 0;
                    for (Contract contract : temp) {
                        //todo: 验证 verifierSign
                        if (contract.getVerifyResult().equals("Y")) {
                            resultCount++;
                        }
                    }
                    if (resultCount / temp.size() >= 0.75f) {
                        verifiedContracts.add(temp.get(0));
                    }
                }
                //todo: send to Ordering service


                isRunning = false;
            }
        }
    }
}
