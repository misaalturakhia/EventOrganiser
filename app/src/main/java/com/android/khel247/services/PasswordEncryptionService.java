package com.android.khel247.services;

import com.google.api.client.util.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Created by Jerry Orr at http://www.javacodegeeks.com/2012/05/secure-password-storage-donts-dos-and.html
 * on 07/11/2014.
 */
public class PasswordEncryptionService {

    public boolean authenticate(String attemptedPassword, String encryptedPasswordStr, String saltStr)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        //decode the input salt string to byte array
        byte[] salt = decodeBase64String(saltStr);

        // decode passwordstr to byte array
        byte[] encryptedPassword = decodeBase64String(encryptedPasswordStr);

        // Encrypt the clear-text password using the same salt that was used to
        // encrypt the original password
        byte[] encryptedAttemptedPassword = getEncryptedPassword(attemptedPassword, salt);

        // Authentication succeeds if encrypted password that the user entered
        // is equal to the stored hash
        return Arrays.equals(encryptedPassword, encryptedAttemptedPassword);
    }

    /**
     * Takes an input plain text password and a salt (previously generated) and returns a Base64
     * encoded string representation of the encrypted password
     * @param password : plain text password
     * @param saltStr : a previously generated salt - byte[]
     * @return : Base64 encoded string of the encrypted password
     * @throws java.security.spec.InvalidKeySpecException
     * @throws java.security.NoSuchAlgorithmException
     */
    public String getEncryptedPasswordString(String password, String saltStr)
                throws InvalidKeySpecException, NoSuchAlgorithmException {
        // decode saltStr to byte array
        byte[] salt = Base64.decodeBase64(saltStr);
        byte[] encPassword = getEncryptedPassword(password, salt);
        return Base64.encodeBase64String(encPassword);
    }

    /**
     * Generates a random salt and returns a Base64 encoded string
     * @return : Base64 encoded string salt
     * @throws java.security.NoSuchAlgorithmException
     */
    public String getGeneratedSaltString() throws NoSuchAlgorithmException {
        byte[] salt = generateSalt();
        return encodeByteArray(salt);
    }

    /**
     * Encrypts the input plain text password.
     * @param password
     * @param salt
     * @return
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.spec.InvalidKeySpecException
     */
    public byte[] getEncryptedPassword(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // PBKDF2 with SHA-1 as the hashing algorithm. Note that the NIST
        // specifically names SHA-1 as an acceptable hashing algorithm for PBKDF2
        String algorithm = "PBKDF2WithHmacSHA1";

        // SHA-1 generates 160 bit hashes, so that's what makes sense here
        int derivedKeyLength = 160;

        // Pick an iteration count that works for you. The NIST recommends at
        // least 1,000 iterations:
        // http://csrc.nist.gov/publications/nistpubs/800-132/nist-sp800-132.pdf
        // iOS 4.x reportedly uses 10,000:
        // http://blog.crackpassword.com/2010/09/smartphone-forensics-cracking-blackberry-backup-passwords/
        int iterations = 11983;

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);

        SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

        return f.generateSecret(spec).getEncoded();
    }

    public byte[] generateSalt() throws NoSuchAlgorithmException {
        // VERY important to use SecureRandom instead of just Random
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

        // Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
        byte[] salt = new byte[8];
        random.nextBytes(salt);

        return salt;
    }

    /**Decodes the input string into a byte array using Base64
     *
     * @param str : previously encoded string
     * @return : decoded byte[]
     */
    private byte[] decodeBase64String(String str){
        return Base64.decodeBase64(str);
    }

    /**
     * Encodes the input byte[] using Base64
     * @param array : input byte[]
     * @return : encoded string
     */
    private String encodeByteArray(byte[] array){
        return Base64.encodeBase64String(array);
    }
}

//TODO: REMOVE THIS TEXT
//  When adding a new user, call generateSalt(), then getPassword(), and store both the encrypted
//  password and the salt. Do not store the clear-text password. Don’t worry about keeping the salt in a
//  separate table or location from the encrypted password; as discussed above, the salt is non-secret.
//
//  When authenticating a user, retrieve the previously encrypted password and salt from the database, then
//  send those and the clear-text password they entered to authenticate(). If it returns true, authentication
//  succeeded.
//
//  When a user changes their password, it’s safe to reuse their old salt; you can just call
//  getPassword() with the old salt.
