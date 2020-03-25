package kr.ac.postech.sslab.openbanking.chaincode.invocation;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.postech.sslab.openbanking.client.CAClient;
import kr.ac.postech.sslab.openbanking.client.ChannelClient;
import kr.ac.postech.sslab.openbanking.client.FabricClient;
import kr.ac.postech.sslab.openbanking.config.Config;
import kr.ac.postech.sslab.openbanking.user.UserContext;
import kr.ac.postech.sslab.openbanking.util.Util;
import org.hyperledger.fabric.sdk.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
public class BankManagement {
    private static final ObjectMapper obectMapper = new ObjectMapper();

    private static final String REGISTER_BANK_FUNCTION_NAME = "registerBank";

    private static final String RETRIEVE_REGISTERED_BANKS_FUNCTION_NAME = "retrieveRegisteredBanks";

    public boolean registerBank(String bank) throws Exception {
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
        request.setFcn(REGISTER_BANK_FUNCTION_NAME);
        String[] arguments = { bank };
        request.setArgs(arguments);
        request.setProposalWaitTime(1000);

        boolean result = false;
        Collection<ProposalResponse> responses = channelClient.sendTransactionProposal(request);
        for (ProposalResponse res: responses) {
            ChaincodeResponse.Status status = res.getStatus();
            Logger.getLogger(BankManagement.class.getName()).log(Level.INFO,"Invoked " + REGISTER_BANK_FUNCTION_NAME + " on "+Config.CHAINCODE_1_NAME + ". Status - " + status);
            result = Boolean.parseBoolean(res.getMessage());
        }

        return result;
    }

    public String retrieveRegisteredBanks() throws Exception {
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

        Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, "Querying for all cars ...");
        Collection<ProposalResponse>  responsesQuery = channelClient.queryByChainCode(Config.CHAINCODE_1_NAME, RETRIEVE_REGISTERED_BANKS_FUNCTION_NAME, new String[] {});
        String result = "";
        for (ProposalResponse pres : responsesQuery) {
            String stringResponse = new String(pres.getChaincodeActionResponsePayload());
            Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, stringResponse);
            result = pres.getMessage();
        }


        return result;
        //return new ArrayList<>(toList(stringResponse));
    }

    private static List<String> toList(String value) {
        return Arrays.asList(value.substring(1, value.length() - 1).split(", "));
    }
}
