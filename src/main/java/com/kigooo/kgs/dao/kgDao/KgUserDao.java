package com.kigooo.kgs.dao.kgDao;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.util.List;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;
import com.kigooo.kgs.domain.kgDomain.KgUser;

@Component
@Mapper
public interface KgUserDao {
    @Insert("INSERT INTO kg_sys_user " +
            "(u_account, u_password, u_name, phone, head_url, sex, create_at, status, init_flag) " +
            "VALUES " +
            "(#{kgUser.uAccount}, #{kgUser.uPassword}, #{kgUser.uName}, #{kgUser.phone}, #{kgUser.headUrl}, #{kgUser.sex}, #{kgUser.createAt}, #{kgUser.status}, 0)")
    @Options(useGeneratedKeys=true,keyColumn = "oid",keyProperty = "kgUser.oid")
    void createUser(@Param("kgUser") KgUser kgUser);

    @Update("UPDATE kg_sys_user SET " +
            "delete_at = #{deleteAt} " +
            "WHERE oid = #{userOID}")
    void deleteUser(@Param("userOID")String userOID,@Param("deleteAt") long deleteAt);

    @Update("UPDATE kg_sys_user SET " +
            "u_name = #{kgUser.uName}, " +
            "phone = #{kgUser.phone}, " +
            "sex = #{kgUser.sex}," +
            "update_at = #{kgUser.updateAt} " +
            "WHERE oid = #{kgUser.oid} ")
    void updateUser(@Param("kgUser")KgUser kgUser);

    @Update("UPDATE kg_sys_user SET " +
            "status = #{status} " +
            "WHERE oid = #{userOID}")
    void updateUserStatus(@Param("userOID") String userOID,@Param("status") int status);

    @Update("UPDATE kg_sys_user SET " +
            "u_password = #{password} " +
            "WHERE oid = #{userOID}")
    void updateUserPassword(@Param("userOID") String userOID,@Param("password") String password);

    @Update("UPDATE kg_sys_user SET head_url = #{headUrl} WHERE oid = #{userOID}")
    void updateUserHeadUrl(@Param("userOID")String userOID,@Param("headUrl")String headUrl);

    @Select("SELECT " +
        "oid, " +
        "u_account AS uAccount, " +
        "u_password AS uPassword, " +
        "u_name AS uName, " +
        "phone, " +
        "head_url AS headUrl, " +
        "sex, " +
        "last_login_at AS lastLoginAt, " +
        "status, " +
        "create_at AS createAt, " +
        "update_at AS updateAt, " +
        "delete_at AS deleteAt, " +
        "init_flag AS initFlag " +
        "FROM kg_sys_user WHERE oid = #{userOID}")
    KgUser getUserByOID(@Param("userOID")String userOID);

    @Select("SELECT " +
            "oid, u_account as uAccount, u_password as uPassword, " +
            "u_name as uName, phone, head_url as headUrl, sex, last_login_at as lastLoginAt, status ,init_flag AS initFlag " +
            "FROM kg_sys_user " +
            "WHERE u_account = #{account} AND status = #{status} AND delete_at IS NULL ")
    KgUser getUserByAccount(@Param("account")String account,@Param("status")int status);

    @Select("<script>" +
            "SELECT " +
            "oid, " +
            "u_account AS uAccount, " +
            "u_password AS uPassword, " +
            "u_name AS uName, " +
            "phone, " +
            "head_url AS headUrl, " +
            "sex, " +
            "last_login_at AS lastLoginAt, " +
            "status, " +
            "init_flag AS initFlag, " +
            "create_at AS createAt, " +
            "update_at AS updateAt, " +
            "delete_at AS deleteAt " +
            "FROM kg_sys_user WHERE delete_at IS NULL AND init_flag &lt;= #{init} " +
            "<if test = 'condition!=null and condition!=\"\" '>" +
            "AND (u_account like #{condition} " +
            "OR u_name like #{condition} " +
            "OR phone like #{condition} ) " +
            "</if>" +
            "ORDER BY u_account " +
            "</script>")
    List<KgUser> getUserList(@Param("condition")String condition,@Param("init")int init);

    @Select("<script>" +
            "SELECT " +
            "oid, " +
            "u_account AS uAccount, " +
            "u_password AS uPassword, " +
            "u_name AS uName, " +
            "phone, " +
            "head_url AS headUrl, " +
            "sex, " +
            "last_login_at AS lastLoginAt, " +
            "status, " +
            "init_flag AS initFlag, " +
            "create_at AS createAt, " +
            "update_at AS updateAt, " +
            "delete_at AS deleteAt " +
            "FROM kg_sys_user where delete_at IS NULL AND init_flag &lt;= #{init} AND oid not in (select user_oid from kg_sys_user_role where role_oid = #{roleOID}) " +
            "<if test = 'condition!=null and condition!=\"\" '>" +
            "AND (u_account like #{condition} " +
            "OR u_name like #{condition} " +
            "OR phone like #{condition} ) " +
            "</if>" +
            "ORDER BY u_account " +
            "</script>")
    List<KgUser> getUserListNotByRoleOID(@Param("roleOID")String roleOID,@Param("condition")String condition,@Param("init")int init);

    @Select("SELECT COUNT(*) FROM kg_sys_user WHERE u_account = #{account}")
    int checkUserAccountExist(@Param("account")String account);

    @Select("<script>" +
            "SELECT COUNT(*) FROM kg_sys_user WHERE u_account = #{account} AND oid NOT IN (" +
            "<foreach collection='exceptOIDList' item='oid' index='index' separator=','>" +
            "#{oid} " +
            "</foreach>" +
            ") " +
            "</script>")
    int checkUserAccountExistExcept(@Param("account")String account,@Param("exceptOIDList")List<String> exceptOIDList);

    @Select("SELECT COUNT(*) FROM kg_sys_user WHERE u_name = #{name} AND delete_at IS NULL")
    int checkUserNameExist(@Param("name")String name);

    @Select("<script>" +
            "SELECT COUNT(*) FROM kg_sys_user WHERE u_name = #{name} AND delete_at IS NULL AND oid NOT IN (" +
            "<foreach collection='exceptOIDList' item='oid' index='index' separator=','>" +
            "#{oid} " +
            "</foreach>" +
            ") " +
            "</script>")
    int checkUserNameExistExcept(@Param("name")String name,@Param("exceptOIDList")List<String> exceptOIDList);

    @Select("SELECT COUNT(*) FROM kg_sys_user WHERE phone = #{phone}")
    int checkUserPhoneExist(@Param("phone")String phone);

    @Select("<script>" +
            "SELECT COUNT(*) FROM kg_sys_user WHERE phone = #{phone} AND oid NOT IN (" +
            "<foreach collection='exceptOIDList' item='oid' index='index' separator=','>" +
            "#{oid} " +
            "</foreach>" +
            ") " +
            "</script>")
    int checkUserPhoneExistExcept(@Param("phone")String phone,@Param("exceptOIDList")List<String> exceptOIDList);
}
