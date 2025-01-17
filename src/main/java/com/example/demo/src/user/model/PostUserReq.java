package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostUserReq {
    private String name;
    private String nickName;
    private String email;
    private String password;
    private String profileImgUrl;
    private String gender;
    private String birth;
}
