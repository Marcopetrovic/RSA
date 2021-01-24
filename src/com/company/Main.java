package com.company;


import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.sql.SQLOutput;
import javax.crypto.*;
import java.util.Scanner;


public class Main {
    static Scanner user = new Scanner(System.in);
    static String currentPubKey;
    static String currentPrivKey;
    static int bitLength = 4096;

    public static void saveKey(String fileName, KeyPair key) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(key);
            out.close();
            System.out.println("Saved key as " + fileName);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public static KeyPair readKey(String fileName) {
        KeyPair key = null;
        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            key = (KeyPair) in.readObject();
            in.close();
            System.out.println("Read key from " + fileName);
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
        return key;
    }

    public static void generateKeys(String fileName, int bitLength) {
        SecureRandom rand = new SecureRandom();

        BigInteger p = new BigInteger(bitLength / 2, 100, rand);
        BigInteger q = new BigInteger(bitLength / 2, 100, rand);
        BigInteger n = p.multiply(q);
        BigInteger phiN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger e = new BigInteger("3");
        while (phiN.gcd(e).intValue() > 1) {
            e = e.add(new BigInteger("2"));
        }

        BigInteger d = e.modInverse(phiN);
        KeyPair publicKey = new KeyPair(e, n);
        KeyPair privateKey = new KeyPair(d, n);
        saveKey(fileName + "_pub.key", publicKey);
        saveKey(fileName + "_priv.key", privateKey);

    }

    public static String encrypt(String message, KeyPair key) {
        return (new BigInteger(message.getBytes(StandardCharsets.UTF_8))).modPow(key.getKey(), key.getN()).toString();
    }

    public static String decrypt(String message, KeyPair key) {
        String msg = new String(message.getBytes(StandardCharsets.UTF_8));
        return new String((new BigInteger(msg)).modPow(key.getKey(), key.getN()).toByteArray());
    }

    public static void main(String[] args) {


        System.out.println("Welcome");
        run();

    }

    public static void run(){

        System.out.println("\nWELCOME, PLEASE SELECT ONE OF THE FOLLOWING OPTIONS");
        System.out.println("\n1. Create keypair");
        System.out.println("2. Load saved keypair");
        System.out.println("3. Encrypt message");
        System.out.println("4. Decrypt message");
        System.out.println("5. to exit");
        int userMeny = user.nextInt();
        if(userMeny == 1){
            createKey();
        }
        if(userMeny == 2){
            enterKey();
        }
        if(userMeny == 3){
            encrypt();
        }
        if(userMeny == 4){
            decrypt();
        }
        if(userMeny == 5){

        }
        else{
            exit();
        }
    }

    public static void createKey(){
        Scanner createKey = new Scanner(System.in);
        System.out.println("\n" + "please name key");
        generateKeys(createKey.nextLine(),bitLength);
        run();
    }

    public static void enterKey(){
        Scanner loadKey = new Scanner(System.in);
        System.out.println("\nPlease enter the public key");
        String pubFile = loadKey.nextLine();
        currentPubKey = pubFile+"_pub.key";

        System.out.println("\nPlease enter the private key");
        String privFile = loadKey.nextLine();
        currentPrivKey = privFile+"_priv.key";
        System.out.println(currentPrivKey + " Has been loaded");
        System.out.println(currentPubKey + " Has been loaded");
        run();
    }

    public static void encrypt(){
        System.out.println("\nEncrypt to a string press 1, to a file press 2");
        Scanner encStr = new Scanner(System.in);
        int select = encStr.nextInt();
        if(select == 1){
            Scanner scanString = new Scanner(System.in);
            System.out.println("\nPlease enter message to encrypt");
            String messageToEncrypt = scanString.nextLine();
            System.out.println(encrypt(messageToEncrypt, readKey(currentPubKey)));

        }
        if(select == 2){
            try {
                Scanner encToFile = new Scanner(System.in);
                System.out.println("\nEnter message to encrypt to file");
                FileWriter fw = new FileWriter("EncryptedFile.txt");
                String fileString = encToFile.nextLine();

                fw.write(encrypt(fileString, readKey(currentPubKey)));
                fw.close();
            } catch (Exception e){

            }
        }
        run();
    }

        public static void decrypt(){
            String currentFile = "";
            System.out.println("\nDo you want do Decrypt a string press 1 or to decrypt a file press 2");
            Scanner decryStr = new Scanner(System.in);
            int select = decryStr.nextInt();
            if(select == 1){
                System.out.println("\nPlease enter message to decrypt");
                Scanner scanString = new Scanner(System.in);
                String decryptString = scanString.nextLine();
                System.out.println(decrypt(decryptString, readKey(currentPrivKey)));
            }
            if(select == 2){
                try {
                    Scanner decryFile = new Scanner(System.in);
                    System.out.println("\nSelect file to decrypt from");
                    String whatFile = decryFile.nextLine();
                    FileReader fr = new FileReader(whatFile);
                    BufferedReader br = new BufferedReader(fr);

                    String stringFromFile = br.readLine();
                    System.out.println(decrypt(stringFromFile, readKey(currentPrivKey)));

                    fr.close();
                } catch (Exception e){

                }
            }
            run();
        }

        public static void exit(){
            System.exit(0);
        }
    }