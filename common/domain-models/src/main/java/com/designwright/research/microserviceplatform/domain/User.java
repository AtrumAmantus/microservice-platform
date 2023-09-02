package com.designwright.research.microserviceplatform.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    public static final long serialVersionUID = 4098058543268126281L;

    private Long id;
    private String username;
    private String password;
    private String email;
    private boolean activated;
    private boolean verified;
    private String accessToken;
    private Long lastLogin;
    private long createdOn;
    private long createdBy;
    private long updatedOn;
    private long updatedBy;
}
