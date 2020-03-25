package kr.ac.postech.sslab.openbanking;

import kr.ac.postech.sslab.openbanking.chaincode.invocation.CheckManagement;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class CheckManagementTest {
    private static final String BANK_KEY = "bank";

    private static final String BALANCE_KEY = "balance";

    private static final String PARENT_KEY = "parent";

    private static final String CHECK_TYPE = "check";

    @Autowired
    private CheckManagement checkManagement;

    @Test
    void issueTest() throws Exception {
        String tokenId = "10";
        String issuer = "Alice";
        Map<String, Object> xattr = new HashMap<>();

        String bank = "Woori";
        int balance = 1000;
        xattr.put(BANK_KEY, bank);
        xattr.put(BALANCE_KEY, balance);
        xattr.put(PARENT_KEY, new ArrayList<>());

        checkManagement.issue(tokenId, issuer, xattr);
    }
}
