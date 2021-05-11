package com.metanonia.influx.binance;

import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

public class Web3Api {
    String from = "0xcc096f00f623510498f4dfa1f25a4bc3187a2824";
    Web3j web3j = null;
    Credentials credentials = null;

    public Web3Api() {

        try {
            credentials = WalletUtils.loadCredentials("fx`)y8a*5F", "./wallet.json");
            String Address = credentials.getAddress();
            HttpService metanonia = new HttpService("https://ropsten.infura.io/v3/bebdf7f9b1fa4b258bf0db97517e0205");
            web3j = Web3j.build(metanonia);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }
    }

    public String sendData(String data) {
        String ret = null;

        try {
        BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.LATEST)
                .send().getTransactionCount();
        BigInteger gasPrice = gasPrice = web3j.ethGasPrice().send().getGasPrice();

        BigInteger gasLimit = BigInteger.valueOf(50000);

        String dataHexa = "0x" + Hex.toHexString(data.getBytes(StandardCharsets.UTF_8));

        RawTransaction rTx = RawTransaction.createTransaction(nonce, gasPrice
                , gasLimit, from, dataHexa);
        byte[] signedMessage = TransactionEncoder.signMessage(rTx, 3, credentials);
        String hexValue = "0x" + Hex.toHexString(signedMessage);
        EthSendTransaction rTxRes = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        String txHash = rTxRes.getTransactionHash();
        if(txHash == null) {
            System.out.println("txError:" + rTxRes.getError().getMessage());
        } else {
            ret = txHash;
        }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ret;
    }
}
