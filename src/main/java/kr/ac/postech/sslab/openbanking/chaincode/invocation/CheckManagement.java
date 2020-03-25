package kr.ac.postech.sslab.openbanking.chaincode.invocation;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.postech.sslab.openbanking.client.CAClient;
import kr.ac.postech.sslab.openbanking.client.ChannelClient;
import kr.ac.postech.sslab.openbanking.client.FabricClient;
import kr.ac.postech.sslab.openbanking.config.Config;
import kr.ac.postech.sslab.openbanking.user.UserContext;
import kr.ac.postech.sslab.openbanking.util.Util;
import org.hyperledger.fabric.sdk.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;


@Service
public class CheckManagement {
    private static final ObjectMapper obectMapper = new ObjectMapper();

    private static final String ISSUE_FUNCTION_NAME = "issue";

    private static final String MERGE_FUNCTION_NAME = "merge";

    private static final String DIVIDE_FUNCTION_NAME = "divide";


    public boolean issue(String id, String issuer, Map<String, Object> xattr) throws Exception {
       // Util.cleanUp();
        String caUrl = Config.CA_ORG1_URL;
        CAClient caClient = new CAClient(caUrl, null);
        // Enroll Admin to Org1MSP
        UserContext adminUserContext = new UserContext();
        adminUserContext.setName(Config.ADMIN);
        adminUserContext.setAffiliation(Config.ORG1);
        adminUserContext.setMspId(Config.ORG1_MSP);
        caClient.setAdminUserContext(adminUserContext);
        adminUserContext = caClient.enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);

        FabricClient fabClient = new FabricClient(adminUserContext);

        ChannelClient channelClient = fabClient.createChannelClient(Config.CHANNEL_NAME);
        Channel channel = channelClient.getChannel();
        Peer peer = fabClient.getInstance().newPeer(Config.ORG1_PEER_0, Config.ORG1_PEER_0_URL);
        EventHub eventHub = fabClient.getInstance().newEventHub("eventhub01", "grpc://localhost:7053");
        Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
        channel.addPeer(peer);
        channel.addEventHub(eventHub);
        channel.addOrderer(orderer);
        channel.initialize();

        TransactionProposalRequest request = fabClient.getInstance().newTransactionProposalRequest();
        ChaincodeID ccid = ChaincodeID.newBuilder().setName(Config.CHAINCODE_1_NAME).build();
        request.setChaincodeID(ccid);
        request.setFcn(ISSUE_FUNCTION_NAME);
        String[] arguments = { id, issuer, obectMapper.writeValueAsString(xattr) };
        request.setArgs(arguments);
        request.setProposalWaitTime(1000);

        boolean result = false;
        Collection<ProposalResponse> responses = channelClient.sendTransactionProposal(request);
        for (ProposalResponse res: responses) {
            ChaincodeResponse.Status status = res.getStatus();
            Logger.getLogger(CheckManagement.class.getName()).log(Level.INFO,"Invoked " + ISSUE_FUNCTION_NAME + " on "+Config.CHAINCODE_1_NAME + ". Status - " + status);
            result = Boolean.parseBoolean(res.getMessage());
        }

        return result;
    }
}
