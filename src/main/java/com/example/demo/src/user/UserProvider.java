package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }


    public GetUserRes getUsersByEmail(String email) throws BaseException{
        try{
            GetUserRes getUsersRes = userDao.getUsersByEmail(email);
            return getUsersRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetUserRes getUsersByIdx(int userIdx) throws BaseException{
        try{
            GetUserRes getUsersRes = userDao.getUsersByIdx(userIdx);
            return getUsersRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public int checkEmail(String email) throws BaseException{
        try{
            return userDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUser(int userIdx) throws BaseException{
        try{
            return userDao.checkUser(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }


//    public List<GetUserRes> getUsers() {
//        try{
//            List<GetUserRes> getUsersRes = userDao.getUsers();
//            return getUsersRes;
//        }
//        catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
}
