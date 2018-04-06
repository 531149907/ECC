package com.ecc.service;

import com.ecc.domain.contract.Contract;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ContractScan {
    public void scan() {
        new Worker().run();
    }

    private class Worker extends Thread {
        @Override
        public void run() {
            while (true) {
                List<Contract> contracts = new ArrayList<>();
                if (contracts.size() >= 10) {
                    //todo: send to 50% peers to verify

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
