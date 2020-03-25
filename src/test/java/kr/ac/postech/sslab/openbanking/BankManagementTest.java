package kr.ac.postech.sslab.openbanking;

import kr.ac.postech.sslab.openbanking.chaincode.invocation.BankManagement;
import kr.ac.postech.sslab.openbanking.chaincode.invocation.CheckManagement;
import kr.ac.postech.sslab.openbanking.config.Config;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class BankManagementTest {

    @Autowired
    private BankManagement bankManagment;

    @Test
    public void registerBankTest() throws Exception {
        bankManagment.registerBank("K-Bank");
    }

    @Test
    public void retrieveRegisteredBanksTest() throws Exception {
        //List<String> registeredBanks = bankManagment.retrieveRegisteredBanks();
        Logger.getLogger(BankManagementTest.class.getName()).log(Level.INFO, bankManagment.retrieveRegisteredBanks());
    }
}
