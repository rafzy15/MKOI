/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.pw.elka.mkoi.crypto;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Formatter;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jdk.nashorn.internal.codegen.types.BitwiseType;
import org.bouncycastle.jcajce.provider.digest.BCMessageDigest;
import org.bouncycastle.jcajce.provider.digest.MD5;
import org.bouncycastle.jcajce.provider.digest.SHA1;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

/**
 *
 * @author rafal
 */
public class HMAC {

    public static void main(String[] args) {
        HMAC hmac = new HMAC();
        byte[] out = hmac.hmac("key".getBytes(), "The quick brown fox jumps over the lazy dog".getBytes(), new SHA256.Digest(), 64);
        System.out.println(Hex.toHexString(out));
    }

    public byte[] hmac(byte[] key, byte[] message, BCMessageDigest SHA3, int blockSize) {
        String input = "Hello world !";
        if (key.length > blockSize) {
            key = SHA3.digest(key);
        }
        if (key.length < blockSize) {
            key = Arrays.copyOf(key, blockSize);
        }
        byte[] outer_pad = xorWithConstant((byte) 0x5c, key);
        byte[] inner_pad = xorWithConstant((byte) 0x36, key);
        byte[] SHAStep = SHA3.digest(concatenateTwoArrays(inner_pad, message));
        return SHA3.digest(concatenateTwoArrays(outer_pad, SHAStep));
    }

    private byte[] xorWithConstant(byte constant, byte[] key) {
        byte[] out = new byte[key.length];
        for (int i = 0; i < key.length; i++) {
            out[i] = (byte) (constant ^ key[i]);
        }
        return out;
    }

    private byte[] concatenateTwoArrays(byte[] a, byte[] b){
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

}
