package com.csye6225.springapi.springmvcrest.Security;

import org.springframework.security.crypto.bcrypt.BCrypt;


public class Crypt {

    public Crypt() {

    }
    public String hashPassword(String password) {
        return BCrypt.hashpw(password,BCrypt.gensalt(10));
    }

    public Boolean checkPassword(String password,String hash) {
        return BCrypt.checkpw(password,hash);
    }


}
