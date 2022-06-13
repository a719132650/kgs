package com.kigooo.kgs.dao.kgDao;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.util.List;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;
import com.kigooo.kgs.domain.kgDomain.*;

@Component
@Mapper
public interface KgRoleDao {
    @Insert("INSERT INTO kg_sys_role (name,description,create_at,status,init_flag) " +
            "VALUES " +
            "(#{kgRole.name},#{kgRole.description},#{createAt},1,0)")
    @Options(useGeneratedKeys=true,keyColumn = "oid",keyProperty = "kgRole.oid")
    void createRole(@Param("kgRole")KgRole kgRole,@Param("createAt")long createAt);

    @Update("UPDATE kg_sys_role SET delete_at = #{deleteAt} WHERE oid = #{roleOID}")
    void deleteRole(@Param("roleOID")String roleOID,@Param("deleteAt")long deleteAt);

    @Update("UPDATE kg_sys_role " +
            "SET name = #{kgRole.name}, description = #{kgRole.description}, " +
            "update_at = #{updateAt} " +
            "WHERE oid = #{kgRole.oid}")
    void updateRole(@Param("kgRole")KgRole kgRole,@Param("updateAt")long updateAt);

    @Update("UPDATE kg_sys_role SET status = #{status} WHERE oid = #{roleOID}")
    void updateRoleStatus(@Param("roleOID")String roleOID,@Param("status")int status);

    @Select("SELECT " +
            "oid AS oid, " +
            "name AS name, " +
            "status AS status, " +
            "description AS description, " +
            "create_at AS createAt, " +
            "update_at AS updateAt, " +
            "delete_at AS deleteAt, " +
            "init_flag AS initFlag " +
            "FROM kg_sys_role WHERE oid = #{roleOID}")
    KgRole getRoleByOID(@Param("roleOID")String roleOID);

    @Select("SELECT oid FROM kg_sys_role  "+
            "WHERE status = #{status} " +
            "AND delete_at IS NULL")
    List<String> getRoleOIDs(@Param("status")int status);

    @Select("SELECT a.oid FROM kg_sys_role AS a " +
            "INNER JOIN kg_sys_role_command AS b " +
            "ON a.oid = b.role_oid " +
            "INNER JOIN kg_sys_command AS c " +
            "ON b.command_oid = c.oid " +
            "INNER JOIN kg_sys_command_api AS d " +
            "ON c.oid = d.command_oid " +
            "INNER JOIN kg_sys_api AS e " +
            "ON d.api_oid = e.oid " +
            "INNER JOIN kg_sys_menu AS f " +
            "ON c.menu_oid = f.oid " +
            "INNER JOIN kg_sys_sub_menu AS g " +
            "ON f.sub_menu_oid = g.oid " +
            "WHERE e.oid = #{apiOID} " +
            "AND a.delete_at IS NULL AND a.status = #{status} " +
            "AND c.delete_at IS NULL AND c.status = #{status} " +
            "AND e.delete_at IS NULL AND e.status = #{status} " +
            "AND f.delete_at IS NULL AND f.status = #{status} " +
            "AND g.delete_at IS NULL AND g.status = #{status} ")
    List<String> getRoleOIDsInCommandByApiOID(@Param("apiOID")String apiOID,@Param("status")int status);

    @Select("SELECT a.oid FROM kg_sys_role AS a " +
            "INNER JOIN kg_sys_role_operate AS b " +
            "ON a.oid = b.role_oid " +
            "INNER JOIN kg_sys_operate AS c " +
            "ON b.operate_oid = c.oid " +
            "INNER JOIN kg_sys_api AS d " +
            "ON c.api_oid = d.oid " +
            "INNER JOIN kg_sys_command AS e " +
            "ON c.command_oid = e.oid " +
            "INNER JOIN kg_sys_menu AS f " +
            "ON e.menu_oid = f.oid " +
            "INNER JOIN kg_sys_sub_menu AS g " +
            "ON f.sub_menu_oid = g.oid " +
            "WHERE d.oid = #{apiOID} " +
            "AND a.delete_at IS NULL AND a.status = #{status} " +
            "AND c.delete_at IS NULL AND c.status = #{status} " +
            "AND d.delete_at IS NULL AND d.status = #{status} " +
            "AND e.delete_at IS NULL AND e.status = #{status} " +
            "AND f.delete_at IS NULL AND f.status = #{status} " +
            "AND g.delete_at IS NULL AND g.status = #{status} ")
    List<String> getRoleOIDsInOperateByApiOID(@Param("apiOID")String apiOID,@Param("status")int status);

    @Select("<script>" +
            "SELECT " +
            "oid, name, description, status, " +
            "init_flag AS initFlag, create_at AS createAt, update_at AS updateAt, delete_at AS deleteAt " +
            "FROM kg_sys_role " +
            "WHERE delete_at IS NULL AND init_flag &lt;= #{init} " +
            "<if test = 'condition!=null and condition!=\"\" '>" +
            "AND (name LIKE #{condition} OR description LIKE #{condition}) " +
            "</if>" +
            "ORDER BY name" +
            "</script>")
    List<KgRole> getRoleList(@Param("condition")String condition,@Param("init")int init);

    @Select("SELECT " +
            "oid, name, description " +
            "FROM kg_sys_role AS a " +
            "INNER JOIN kg_sys_user_role AS b " +
            "ON a.oid = b.role_oid " +
            "WHERE b.user_oid = #{userOID} AND a.delete_at IS NULL ")
    List<KgRole> getRoleListByUserOID(@Param("userOID") String userOID);

    @Select("<script>" +
            "SELECT " +
            "a.oid AS oid, a.name AS name, a.description AS description, a.status AS status, " +
            "a.create_at AS createAt, a.update_at AS updateAt, a.delete_at AS deleteAt " +
            "FROM kg_sys_role AS a " +
            "INNER JOIN kg_sys_role_command AS b " +
            "ON a.oid = b.role_oid " +
            "WHERE b.command_oid IN (" +
            "<foreach collection='commandOIDList' item='commandOID' index='index' separator=','>" +
            "#{commandOID} " +
            "</foreach>" +
            ") AND a.delete_at IS NULL" +
            "</script>")
    List<KgRole> getRoleListByCommandOIDs(@Param("commandOIDList")List<String> commandOIDList);

    @Select("<script>" +
            "SELECT " +
            "a.oid AS oid, a.name AS name, a.description AS description, a.status AS status, " +
            "a.create_at AS createAt, a.update_at AS updateAt, a.delete_at AS deleteAt " +
            "FROM kg_sys_role AS a " +
            "INNER JOIN kg_sys_role_operate AS b " +
            "ON a.oid = b.role_oid " +
            "WHERE b.operate_oid IN (" +
            "<foreach collection='operateOIDList' item='operateOID' index='index' separator=','>" +
            "#{operateOID} " +
            "</foreach>" +
            ") AND a.delete_at IS NULL" +
            "</script>")
    List<KgRole> getRoleListByOperateOIDs(@Param("operateOIDList")List<String> operateOIDList);

    @Select("SELECT a.oid, a.name, a.description " +
        "FROM kg_sys_role AS a " +
        "INNER JOIN kg_sys_role_api AS b " +
        "ON a.oid = b.role_oid " +
        "INNER JOIN kg_sys_api AS c " +
        "ON b.api_oid = c.oid " +
        "WHERE c.api = #{api} AND a.delete_at IS NULL")
    List<KgRole> getRoleListByApi(@Param("api")String api);

    @Select("SELECT COUNT(oid) FROM kg_sys_role WHERE name = #{name} AND delete_at IS NULL")
    int checkRoleNameExist(@Param("name")String name);

    @Select("<script>" +
            "SELECT COUNT(oid) FROM kg_sys_role " +
            "WHERE name = #{name} AND delete_at IS NULL AND oid NOT IN (" +
            "<foreach collection='exceptOIDList' item='oid' index='index' separator=','>" +
            "#{oid} " +
            "</foreach>" +
            ") " +
            "</script>")
    int checkRoleNameExistExcept(@Param("name")String name,@Param("exceptOIDList")List<String> exceptOIDList);
}
