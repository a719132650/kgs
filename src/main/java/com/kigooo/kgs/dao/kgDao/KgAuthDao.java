package com.kigooo.kgs.dao.kgDao;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import com.kigooo.kgs.domain.kgDomain.KgUser;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;
import java.util.List;
import com.kigooo.kgs.domain.kgDomain.*;

@Component
@Mapper
public interface KgAuthDao {
    @Select("SELECT COUNT(oid) FROM kg_sys_api " +
            "WHERE api = #{api} AND auth_type= #{authType} AND status = 1 AND delete_at IS NULL LIMIT 1")
    int isInState(@Param("api")String api, @Param("authType")int authType);

    @Insert("INSERT INTO kg_sys_token " +
            "(access_token,expires_at,create_at,user_oid) " +
            "VALUES " +
            "(#{token},#{expiresAt},#{createAt},#{oid})")
    void saveToken(@Param("token") String token,@Param("expiresAt")String expiresAt,@Param("createAt")String createAt,@Param("oid")String oid);

    @Update("UPDATE kg_sys_user SET last_login_at = #{lastLoginAt} WHERE oid = #{userOID} LIMIT 1")
    void updateLastLoginAt(@Param("userOID")String userOID,@Param("lastLoginAt")long lastLoginAt);

    @Update("UPDATE kg_sys_user SET " +
            "u_name = #{kgUser.uName}, " +
            "phone = #{kgUser.phone}, " +
            "sex = #{kgUser.sex}," +
            "update_at = #{kgUser.updateAt} " +
            "WHERE oid = #{kgUser.oid} ")
    void updateUserMy(@Param("kgUser")KgUser kgUser);

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
        "init_flag AS initFlag, " +
        "create_at AS createAt, " +
        "update_at AS updateAt, " +
        "delete_at AS deleteAt " +
        "FROM kg_sys_user WHERE oid = #{userOID}")
    KgUser getUserByOIDMy(@Param("userOID")String userOID);

    @Update("UPDATE kg_sys_user SET " +
            "u_password = #{password} " +
            "WHERE oid = #{userOID}")
    void updateUserPasswordMy(@Param("userOID") String userOID,@Param("password") String password);

    @Update("UPDATE kg_sys_user SET head_url = #{headUrl} WHERE oid = #{userOID}")
    void updateUserHeadUrlMy(@Param("userOID")String userOID,@Param("headUrl")String headUrl);

    @Select("SELECT " +
            "oid, name, sort, status, " +
            "create_at AS createAt, update_at AS updateAt, delete_at AS deleteAt,#{userOID} AS userOID " +
            "FROM kg_sys_sub_menu WHERE status = 1 AND delete_at IS NULL ORDER BY sort")
    @Results({
            @Result(column ="oid" ,property ="oid"),
            @Result(column = "{menuSubOID=oid,userOID=userOID}",property = "children",javaType = java.util.List.class,
                    many = @Many(select = "getMenuListBySubMenuOIDMy"))
    })
    List<KgSubMenu> getSubMenuTreeMy(@Param("userOID")String userOID);

    @Select("SELECT " +
            "oid, name, sort, status, " +
            "sub_menu_oid AS subMenuOid, create_at as createAt, " +
            "update_at AS updateAt, delete_at as deleteAt,#{userOID} AS userOID " +
            "FROM kg_sys_menu " +
            "WHERE sub_menu_oid = #{menuSubOID} AND status = 1 AND delete_at IS NULL " +
            "ORDER BY sort")
    @Results({
            @Result(column ="oid" ,property ="oid"),
            @Result(column = "{menuOID=oid,userOID=userOID}",property = "children",javaType = java.util.List.class,
                    many = @Many(select = "getCommandListByMenuOIDMy"))
    })
    List<KgMenu> getMenuListBySubMenuOIDMy(@Param("menuSubOID")String menuSubOID,@Param("userOID")String userOID);

    @Select("SELECT DISTINCT " +
            "a.oid AS oid, a.name AS name, a.sort AS sort, a.url AS url, a.status AS status, " +
            "a.menu_oid AS menuOid, a.create_at AS createAt, " +
            "a.update_at AS updateAt, a.delete_at AS deleteAt " +
            "FROM kg_sys_command AS a " +
            "INNER JOIN kg_sys_role_command AS b " +
            "ON a.oid = b.command_oid " +
            "INNER JOIN kg_sys_role AS c " +
            "ON b.role_oid = c.oid " +
            "INNER JOIN kg_sys_user_role AS d " +
            "ON c.oid = d.role_oid " +
            "WHERE a.menu_oid = #{menuOID} AND d.user_oid = #{userOID} " +
            "AND a.delete_at IS NULL AND a.status = 1 " +
            "AND c.status = 1 ")
    List<KgCommand> getCommandListByMenuOIDMy(@Param("menuOID")String menuOID,@Param("userOID")String userOID);

    @Select("SELECT DISTINCT " +
            "a.oid AS oid, a.name AS name, a.status AS status, a.description AS description, " +
            "a.command_oid AS commandOid, " +
            "a.api_oid AS apiOid, " +
            "a.create_at AS createAt, " +
            "a.update_at AS updateAt, " +
            "a.delete_at AS deleteAt " +
            "FROM kg_sys_operate AS a " +
            "INNER JOIN kg_sys_command AS b " +
            "ON a.command_oid = b.oid " +
            "INNER JOIN kg_sys_role_operate AS c " +
            "ON a.oid = c.operate_oid " +
            "INNER JOIN kg_sys_role AS d " +
            "ON c.role_oid = d.oid " +
            "INNER JOIN kg_sys_user_role AS e " +
            "ON d.oid = e.role_oid " +
            "INNER JOIN kg_sys_user AS f " +
            "ON e.user_oid = f.oid " +
            "WHERE a.delete_at IS NULL AND a.status = 1 " +
            "AND b.delete_at IS NULL AND b.status = 1 AND b.oid = #{commandOID} " +
            "AND d.delete_at IS NULL AND d.status = 1 " +
            "AND f.delete_at IS NULL AND f.status = 1 AND f.oid = #{userOID} ")
    List<KgOperate> getOperateByCommandOIDMy(@Param("commandOID")String commandOID,@Param("userOID")String userOID);

    @Select("<script>" +
        "select t2.id from kg_sys_role_range t1,kg_sys_range t2 where t1.range_oid=t2.oid and t2.range_id=#{rangeId} and t1.role_oid in (" +
        "<foreach collection='roleOIDList' item='roleOID' index='index' separator=','>" +
        "#{roleOID}" +
        "</foreach>" +
        ") " +
        "</script>")
    List<String> getDataIdsByRangeIdMy(@Param("rangeId")String rangeId,@Param("roleOIDList")List<String> roleOIDList);
}
