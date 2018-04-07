package com.ecc.service;

import com.ecc.domain.contract.Contract;
import com.ecc.service.contract.ContractService;
import com.ecc.web.api.BlockServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContractScan {
    @Autowired
    ContractService contractService;
    @Autowired
    BlockServiceApi blockServiceApi;

    private static ContractScan instance = new ContractScan();
    private boolean isRunning = false;

    private ContractScan() {
    }

    public static ContractScan getInstance() {
        return instance;
    }

    public void scan() {
        if (!this.isRunning) {
            new Worker().run();
            this.isRunning = true;
        }
    }

    private class Worker extends Thread {
        @Override
        public void run() {
            while (true) {
                int count = contractService.getContractsCount();
                System.out.println("Thread running!");
                if (count >= 10) {
                    //todo: send to 50% peers to verify
                    List<Contract> contracts = contractService.getTop10Contracts();
                    for (Contract contract : contracts) {
                        contractService.deleteContract(contract);
                    }
                    blockServiceApi.sendToBlockService(contracts);
                }
                try {
                    Thread.sleep(1000 * 60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
