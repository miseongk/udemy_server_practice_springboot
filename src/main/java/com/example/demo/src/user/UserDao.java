package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public GetUserInfoRes selectUserInfo(int userIdx){
        String selectUserInfoQuery = "SELECT u.userIdx as userIdx,\n" +
                "       u.nickName as nickName,\n" +
                "       u.name as name,\n" +
                "       u.profileImgUrl as profileImgUrl,\n" +
                "       u.website as website,\n" +
                "       u.introduce as introduction,\n" +
                "       IF(postCount is null, 0, postCount) as postCount,\n" +
                "       IF(followerCount is null, 0, followerCount) as followerCount,\n" +
                "       IF(followingCount is null, 0, followingCount) as followingCount\n" +
                "       FROM User as u\n" +
                "            left join (select userIdx, count(postIdx) as postCount from Post\n" +
                "                        WHERE status = 'ACTIVE' group by userIdx) p on p.userIdx = u.userIdx\n" +
                "            left join (select followerIdx, count(followIdx) as followerCount from Follow\n" +
                "                        WHERE status = 'ACTIVE' group by followerIdx) fc on fc.followerIdx = u.userIdx\n" +
                "            left join (select followeeIdx, count(followIdx) as followingCount from Follow\n" +
                "                        WHERE status = 'ACTIVE' group by followeeIdx) f on f.followeeIdx = u.userIdx\n" +
                "WHERE u.userIdx = ? and u.status = 'ACTIVE'";
        int selectUserInfoParam = userIdx;

        return this.jdbcTemplate.queryForObject(selectUserInfoQuery,
                (rs,rowNum) -> new GetUserInfoRes(
                        rs.getString("nickName"),
                        rs.getString("name"),
                        rs.getString("profileImgUrl"),
                        rs.getString("website"),
                        rs.getString("introduction"),
                        rs.getInt("followerCount"),
                        rs.getInt("followingCount"),
                        rs.getInt("postCount")
                ), selectUserInfoParam);
    }

    public List<GetUserPostsRes> selectUserPosts(int userIdx){
        String selectUserPostsQuery = "SELECT p.postIdx,\n" +
                "       pi.imgUrl as postImgUrl\n" +
                "FROM Post as p\n" +
                "    join User as u on u.userIdx = p.userIdx\n" +
                "    join PostImgUrl as pi on pi.postIdx = p.postIdx and pi.status = 'ACTIVE'\n" +
                "WHERE p.status = 'ACTIVE' and u.userIdx = ?\n" +
                "group by p.postIdx\n" +
                "Having min(pi.postImgUrlIdx)\n" +
                "order by p.postIdx;";
        int selectUserPostsParam = userIdx;

        return this.jdbcTemplate.query(selectUserPostsQuery,
                (rs, rowNum) -> new GetUserPostsRes(
                        rs.getInt("postIdx"),
                        rs.getString("postImgUrl")
                ), selectUserPostsParam);
    }

//    public GetUserFeedRes getUsersByEmail(String email){
//        String getUsersByEmailQuery = "select userIdx,name,nickName,email from User where email=?";
//        String getUsersByEmailParams = email;
//        return this.jdbcTemplate.queryForObject(getUsersByEmailQuery,
//                (rs, rowNum) -> new GetUserFeedRes(
//                        rs.getInt("userIdx"),
//                        rs.getString("name"),
//                        rs.getString("nickName"),
//                        rs.getString("email")),
//                getUsersByEmailParams);
//    }
//
//
//    public GetUserFeedRes getUsersByIdx(int userIdx){
//        String getUsersByIdxQuery = "select userIdx,name,nickName,email from User where userIdx=?";
//        int getUsersByIdxParams = userIdx;
//        return this.jdbcTemplate.queryForObject(getUsersByIdxQuery,
//                (rs, rowNum) -> new GetUserFeedRes(
//                        rs.getInt("userIdx"),
//                        rs.getString("name"),
//                        rs.getString("nickName"),
//                        rs.getString("email")),
//                getUsersByIdxParams);
//    }

    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into User (name, nickName, email, pwd, profileImgUrl, gender, birth) VALUES (?,?,?,?, ?, ?, ?)";
        Object[] createUserParams = new Object[]{postUserReq.getName(), postUserReq.getNickName(), postUserReq.getEmail(),
                postUserReq.getPassword(), postUserReq.getProfileImgUrl(), postUserReq.getGender(), postUserReq.getBirth()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    public int checkUserExist(int userIdx){
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ?)";
        int checkUserExistParams = userIdx;

        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);
    }

    public int modifyUserName(PatchUserReq patchUserReq){
        String modifyUserNameQuery = "update User set nickName = ? where userIdx = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getNickName(), patchUserReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }

    public int modifyUserStatus(int userIdx){
        String modifyUserStatusQuery = "update User set status = 'INACTIVE' where userIdx = ? ";

        return this.jdbcTemplate.update(modifyUserStatusQuery, userIdx);
    }


}
