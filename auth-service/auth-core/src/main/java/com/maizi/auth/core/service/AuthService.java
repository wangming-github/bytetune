package com.maizi.auth.core.service;

import com.maizi.auth.domain.dto.R;

public interface AuthService {

    /**
     *
     * @param username
     * @param password
     * @return
     */
    R<String> login(String username, String password);

}