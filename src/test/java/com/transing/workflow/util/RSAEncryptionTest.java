package com.transing.workflow.util;

import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import com.jeeframework.util.io.IOUtils;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;

/**
 * Created by byron on 2018/4/9 0009.
 */

public class RSAEncryptionTest {
    public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";

    public static Map<String, String> createKeys(int keySize){
        //为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;
        try{
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        }catch(NoSuchAlgorithmException e){
            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
        }

        //初始化KeyPairGenerator对象,密钥长度
        kpg.initialize(keySize);
        //生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        //得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded());
        //得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());
        Map<String, String> keyPairMap = new HashMap<>();
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);
        return keyPairMap;
    }

    /**
     * 得到公钥
     * @param publicKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }

    /**
     * 得到私钥
     * @param privateKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }

    /**
     * 公钥加密
     * @param data
     * @param publicKey
     * @return
     */
    public static String publicEncrypt(String data, RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥解密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateDecrypt(String data, RSAPrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), privateKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * 私钥加密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateEncrypt(String data, RSAPrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), privateKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 公钥解密
     * @param data
     * @param publicKey
     * @return
     */

    public static String publicDecrypt(String data, RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), publicKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 解析为byte数组
     * @param cipher
     * @param opmode
     * @param datas
     * @param keySize
     * @return
     */
    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize){
        int maxBlock = 0;
        if(opmode == Cipher.DECRYPT_MODE){
            maxBlock = keySize / 8;
        }else{
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try{
            while(datas.length > offSet){
                if(datas.length-offSet > maxBlock){
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                }else{
                    buff = cipher.doFinal(datas, offSet, datas.length-offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        }catch(Exception e){
            throw new RuntimeException("加解密阀值为["+maxBlock+"]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        IOUtils.closeQuietly(out);
        return resultDatas;
    }

    public static void main(String[] args) throws Exception{
//        Map<String, String> keyMap = createKeys(1024);
//        String  publicKey = keyMap.get("publicKey");
//        String  privateKey = keyMap.get("privateKey");
//        System.out.println("公钥: \n\r" + publicKey);
//        System.out.println("私钥： \n\r" + privateKey);
//        System.out.println("公钥加密——私钥解密");

//        String str = "站在大明门前守卫的禁卫军，事先没有接到";
//        System.out.println("\r明文：\r\n" + str);
//        System.out.println("\r明文大小：\r\n" + str.getBytes().length);
//        String encodedData = publicEncrypt(str, getPublicKey(publicKey));
        String encodedData = "Co12AIXgBxwKAhO1EO0-ja4FK2XBQluqJKwpAxNvATdS1yzagMDwZcwxjH9RY1JavzUNzZOV5nlAFCXl_gHSXE-pTmLDAlYC_axI6bqWxVqjvJDynIVgxesntVO_9pn7_TMq-YzyJZgYKEB3br35L3KHzmmEF6S32AqxF9NBsM4";
        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALZhFT0R155DGSPo4tutfY5G4DRZyFnGNl1lK6AtoTBciIcztvloMnicBF6QNtMu_3VrUtzTccOE4O1k2QT3XbcedSGxCeuREkC7t2f3E0YEXgMy6UX-Xj_4D2BNfAC3z9nGAv89OvQSYxRYrlg3r78d22-8hr7OPSyVX3jIY0-TAgMBAAECgYA5u53HiUBiPwdUUMKPm-nlAhazO8Cqeo53HjGjMQ6XipNFiV9gsFVZzLmtXaWdUIFZoDHI6b5XkFbLj2MLqWqmpbgxIDpqfY3rWlWSvdlsGjUEY53LZ7B-DVyOan-gqDgaOSj1Y88jZzh1pXqNf1ofuOfbvvi9_bvrliaD5xhD8QJBAO2b0opyHawFSnz8MooGBUfvX3rvZSpUePuoeIuoEdMjLUd4qIS24BQojTeksAE7pocim_Sh_KB6Mc-rjA0aIxcCQQDEfuXc7jK8QBx4a6D8dkoLd3eoc1AwN5w33zhr2Bb5fp3RshDeUX6z3wF3Q-nJhea_jAao3ZCd1QdVT7Nn8PTlAkEAm9Dge3ukK6d4lofIGK0kD21RPsv4mo60m8t56ZN4xO2_hqwATq-iOHXzTXcJGHXmmZ2_iegkQ5R-T8IeYIUwVwJBAKdsvK0HpOEz-0473rrei27Dx4gXYQ8Egy40G54ATGsfkJdZEvhrFftrG5OkoTIdzqQNSqtdxlhFaG8vbt00uWUCQDEAv9MRHtY3CTGvoZTdOGCYj7UxSNeSU5BV46lOGPZEGr_uPqQ2TIwq-ucatZnE8b3_7iZxKSVOtpoCLYV3Mkk";
        System.out.println("密文：\r\n" + encodedData);
        String decodedData = privateDecrypt("", getPrivateKey(privateKey));
        System.out.println("解密后文字: \r\n" + decodedData);
    }

}
