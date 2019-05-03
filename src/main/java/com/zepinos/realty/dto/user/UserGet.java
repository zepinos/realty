package com.zepinos.realty.dto.user;

import com.zepinos.realty.jooq.tables.pojos.Users;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserGet extends Users {

    @Override
    public void setPassword(String password) {
        super.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
    }

}
