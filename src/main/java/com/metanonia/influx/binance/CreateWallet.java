package com.metanonia.influx.binance;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Random;

// UTC--2021-05-11T11-35-00.64731000Z--cc096f00f623510498f4dfa1f25a4bc3187a2824.json : fx`)y8a*5F
public class CreateWallet {
    public static void main(String[] args) throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException, IOException {
        String password = new Random().ints(10, 33, 122).collect(StringBuilder::new,
                StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        String f = WalletUtils.generateFullNewWalletFile(password, new File("."));
        System.out.println(f + " : " + password);

    }
}
