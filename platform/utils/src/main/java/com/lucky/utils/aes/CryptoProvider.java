package com.lucky.utils.aes;

import java.security.Provider;

/**
 * @author : 杜宗宁
 * @date : 2018/7/31
 * @Description: android 7.1以上的系统 google去掉了Crypto provider 使用此方法替代
 */
class CryptoProvider extends Provider {

    CryptoProvider() {
        super("Crypto", 1.0, "HARMONY (SHA1 digest; SecureRandom; SHA1withDSA signature)");
        put("SecureRandom.SHA1PRNG",
                "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl");
        put("SecureRandom.SHA1PRNG ImplementedIn", "Software");
    }
}
