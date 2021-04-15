/*
Joseph Salazar
salazjos@oregonstate.edu
CS 370 - Programming Project 1
WeakCollision.java
*/

/* Citations
Judicael, Gupta, L., Rajavel, M., Taylor, M., Clovis, Tom, . . . Pradeep. (2020, May 15).
Java Secure Hashing - MD5, SHA256, SHA512, PBKDF2, BCrypt, SCrypt. Retrieved October 10, 2020,
from https://howtodoinjava.com/java/java-security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/

Seralahthan. (2019, July 21). Java Cryptographic Hash Functions.
Retrieved October 10, 2020, from https://medium.com/@TechExpertise/java-cryptographic-hash-functions-a7ae28f3fa42
*/

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.util.Random;

public class WeakCollision {

    public static String byteArrayAsHexString(byte[] encryptedArr) {
        StringBuilder sb = new StringBuilder();
        for(byte bt : encryptedArr)
            sb.append(String.format("%02x", bt));

        return sb.toString();
    }

    public static void getFirstThreeBytes(byte[] firstThreeArr, byte[] hashArray) {
        for(int i = 0; i < 3; i++)
            firstThreeArr[i] = 0;
        System.arraycopy(hashArray, 0,firstThreeArr, 0, 3);
    }

    public static byte[] hashSha256(String randomStr) {
        byte[] hashValue = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] stringToBytes = randomStr.getBytes(StandardCharsets.UTF_8);
            hashValue = messageDigest.digest(stringToBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashValue;
    }

    public static boolean isMatchFound(String a, String b) {
        return a.equals(b);
    }

    public static ArrayList<String> getListOfWords(String fileName){
        String word;
        ArrayList<String> strList = new ArrayList<String>();;
        FileInputStream fstream = null;
        BufferedReader bufferedReader = null;
        try {
            fstream = new FileInputStream(fileName);
            bufferedReader = new BufferedReader(new InputStreamReader(fstream));

            while((word = bufferedReader.readLine()) != null) {
                strList.add(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strList;
    }

    public static String getRandomWord(ArrayList<String> strList){
        Random rand = new Random();
        int randomIndex = rand.nextInt(strList.size());
        return strList.get(randomIndex);
    }

    public static String getRandomString(String randomWord, ArrayList<Character> charList) {
        for(int i = 0; i < randomWord.length(); i++)
            charList.add(randomWord.charAt(i));

        Collections.shuffle(charList);
        StringBuilder sb = new StringBuilder();
        for (Character character : charList)
            sb.append(character);

        return sb.toString();
    }

    public static int bruteForceWeakCollision(ArrayList<String> wordList) {

        ArrayList<Character> randomCharList = new ArrayList<Character>();;
        boolean isCollisionFound = false;
        String randomStr = null;
        String byteArrayAsHexStrA = null, byteArrayAsHexStrB = null;
        byte[] hashArray1 = null, hashArray2 = null;
        byte[] firstThreeTempA = new byte[3], firstThreeTempB = new byte[3];
        int loopCount = 0;

        String randomWordA = getRandomWord(wordList);
        hashArray1 = hashSha256(randomWordA);
        getFirstThreeBytes(firstThreeTempA, hashArray1);
        byteArrayAsHexStrA = byteArrayAsHexString(firstThreeTempA);
        String headerString = "Random Word: " + randomWordA + ", Looking for collision with: " + byteArrayAsHexStrA;
        System.out.println(headerString);
        while(!isCollisionFound) {
            loopCount++;
            randomCharList.clear();
            String randomWordB = getRandomWord(wordList);
            randomStr = getRandomString(randomWordB, randomCharList);
            hashArray2 = hashSha256(randomStr);
            getFirstThreeBytes(firstThreeTempB, hashArray2);
            byteArrayAsHexStrB = byteArrayAsHexString(firstThreeTempB);
            if(loopCount % 50000 == 0) System.out.println("Still looking....." + loopCount);
            if(isMatchFound(byteArrayAsHexStrA, byteArrayAsHexStrB))
                isCollisionFound = true;
        }
        System.out.println(byteArrayAsHexString(hashArray1));
        System.out.println(byteArrayAsHexString(hashArray2));
        return loopCount;
    }

    public static double findLoopAverage(int[] loopAmountArr) {
        int sum = 0;
        for(int i : loopAmountArr) {
            sum += i;
        }
        return (int)(sum/10);
    }

    public static void main(String[] args) {
        ArrayList<String> wordList = getListOfWords("english_words.txt");
        int[] loopAmountArr = new int[10];
        for(int i = 0; i < 10; i++) {
            loopAmountArr[i] =  bruteForceWeakCollision(wordList);
            System.out.println("Weak Collision found!!!");
            System.out.println("Loops to break: " + loopAmountArr[i] + "\n");
        }
        for(int i : loopAmountArr) {
            System.out.println("Loop amount: " + i);
        }
        System.out.println("Average was: " + String.format("%.2f", findLoopAverage(loopAmountArr)));

    }

}
