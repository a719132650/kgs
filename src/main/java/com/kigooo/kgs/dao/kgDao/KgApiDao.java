package com.kigooo.kgs.dao.kgDao;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.util.List;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;
import com.kigooo.kgs.domain.kgDomain.KgApi;

@Component
@Mapper
public interface KgApiDao {
    @Insert("INSERT INTO kg_sys_api " +
            "(api, name, description, business_type, auth_type, create_at, status, init_flag) " +
            "VALUES " +
            "(#{kgApi.api}, #{kgApi.name}, #{kgApi.description}, #{kgApi.businessType}, " +
            "#{kgApi.authType}, #{kgApi.createAt}, #{kgApi.status}, 0)")
    @Options(useGeneratedKeys=true,keyColumn = "oid",keyProperty = "kgApi.oid")
    void createApi(@Param("kgApi") KgApi kgApi);

    @Update("UPDATE kg_sys_api SET delete_at = #{deleteAt} " +
            "WHERE oid = #{apiOID}")
    void deleteApi(@Param("apiOID")String apiOID,@Param("deleteAt")long deleteAt);

    @Update("UPDATE kg_sys_api SET " +
            "api = #{kgApi.api}, " +
            "name = #{kgApi.name}, " +
            "description = #{kgApi.description}, " +
            "business_type = #{kgApi.businessType}, " +
            "auth_type = #{kgApi.authType}, " +
            "update_at = #{kgApi.updateAt} " +
            "WHERE oid = #{kgApi.oid}")
    void updateApi(@Param("kgApi") KgApi kgApi);

    @Update("<script>" +
            "<foreach collection='apiOIDList' item='apiOID' index='index'>" +
            "UPDATE kg_sys_api SET status = #{status} " +
            "WHERE oid = #{apiOID};" +
            "</foreach>" +
            "</script>")
    void updateApiStatus(@Param("apiOIDList")List<String> apiOIDList,@Param("status")int status);

    @Select("SELECT " +
            "oid, api, name,description, status, " +
            "auth_type AS authType, " +
            "business_type AS businessType, " +
            "create_at AS createAt, " +
            "update_at AS updateAt, " +
            "delete_at AS deleteAt, " +
            "init_flag AS initFlag " +
            "FROM kg_sys_api WHERE oid = #{apiOID}")
    KgApi getApiByOID(@Param("apiOID")String apiOID);
    
    @Select("<script>" +
            "SELECT a.oid FROM kg_sys_api AS a " +
            "INNER JOIN kg_sys_command_api AS b " +
            "ON a.oid = b.api_oid " +
            "INNER JOIN kg_sys_command AS c " +
            "ON b.command_oid = c.oid " +
            "INNER JOIN kg_sys_menu AS d " +
            "ON c.menu_oid = d.oid " +
            "INNER JOIN kg_sys_sub_menu AS e " +
            "ON d.sub_menu_oid = e.oid " +
            "WHERE e.oid IN (" +
            "<foreach collection='subMenuOIDList' item='subMenuOID' index='index' separator=','>" +
            "#{subMenuOID} " +
            "</foreach>" +
            ")" +
            "AND a.delete_at IS NULL AND c.delete_at IS NULL AND d.delete_at IS NULL " +
            "</script>")
    List<String> getApiOIDsInCommandBySubMenuOIDs(@Param("subMenuOIDList")List<String> subMenuOIDList);

    @Select("<script>" +
            "SELECT a.oid FROM kg_sys_api AS a " +
            "INNER JOIN kg_sys_operate AS b " +
            "ON a.oid = b.api_oid " +
            "INNER JOIN kg_sys_command AS c " +
            "ON b.command_oid = c.oid " +
            "INNER JOIN kg_sys_menu AS d " +
            "ON c.menu_oid = d.oid " +
            "INNER JOIN kg_sys_sub_menu AS e " +
            "ON d.sub_menu_oid = e.oid " +
            "WHERE e.oid IN (" +
            "<foreach collection='subMenuOIDList' item='subMenuOID' index='index' separator=','>" +
            "#{subMenuOID} " +
            "</foreach>" +
            ") " +
            "AND a.delete_at IS NULL AND b.delete_at IS NULL AND c.delete_at IS NULL AND d.delete_at IS NULL " +
            "</script>")
    List<String> getApiOIDsInOperateBySubMenuOIDs(@Param("subMenuOIDList")List<String> subMenuOIDList);

    @Select("<script>" +
            "SELECT a.oid FROM kg_sys_api AS a " +
            "INNER JOIN kg_sys_command_api AS b " +
            "ON a.oid = b.api_oid " +
            "INNER JOIN kg_sys_command AS c " +
            "ON b.command_oid = c.oid " +
            "INNER JOIN kg_sys_menu AS d " +
            "ON c.menu_oid = d.oid " +
            "WHERE d.oid IN (" +
            "<foreach collection='menuOIDList' item='menuOID' index='index' separator=','>" +
            "#{menuOID} " +
            "</foreach>" +
            ")" +
            "AND a.delete_at IS NULL AND c.delete_at IS NULL " +
            "</script>")
    List<String> getApiOIDsInCommandByMenuOIDs(@Param("menuOIDList")List<String> menuOIDList);

    @Select("<script>" +
            "SELECT a.oid FROM kg_sys_api AS a " +
            "INNER JOIN kg_sys_operate AS b " +
            "ON a.oid = b.api_oid " +
            "INNER JOIN kg_sys_command AS c " +
            "ON b.command_oid = c.oid " +
            "INNER JOIN kg_sys_menu AS d " +
            "ON c.menu_oid = d.oid " +
            "WHERE d.oid IN (" +
            "<foreach collection='menuOIDList' item='menuOID' index='index' separator=','>" +
            "#{menuOID} " +
            "</foreach>" +
            ")" +
            "AND a.delete_at IS NULL AND b.delete_at IS NULL AND c.delete_at IS NULL " +
            "</script>")
    List<String> getApiOIDsInOperateByMenuOIDs(@Param("menuOIDList")List<String> menuOIDList);

    @Select("<script>" +
            "SELECT b.api_oid FROM kg_sys_command AS a " +
            "INNER JOIN kg_sys_command_api AS b " +
            "ON a.oid = b.command_oid " +
            "INNER JOIN kg_sys_api AS c " +
            "ON c.oid = b.api_oid " +
            "WHERE a.oid IN (" +
            "<foreach collection='commandOIDList' item='commandOID' index='index' separator=','>" +
            "#{commandOID} " +
            "</foreach>" +
            ")" +
            "AND c.delete_at IS NULL " +
            "</script>")
    List<String> getApiOIDsByCommandOIDs(@Param("commandOIDList")List<String> commandOIDList);

    @Select("<script>" +
            "SELECT DISTINCT a.oid FROM kg_sys_api AS a " +
            "INNER JOIN kg_sys_operate AS b " +
            "ON a.oid = b.api_oid WHERE b.oid IN (" +
            "<foreach collection='operateOIDList' item='operateOID' index='index' separator=','>" +
            "#{operateOID} " +
            "</foreach>" +
            ")" +
            "AND a.delete_at IS NULL " +
            "</script>")
    List<String> getApiOIDsByOperateOIDs(@Param("operateOIDList")List<String> operateOIDList);

    @Select("SELECT b.api_oid FROM " +
            "kg_sys_role_command AS a " +
            "INNER JOIN kg_sys_command_api AS b " +
            "ON a.command_oid = b.command_oid " +
            "INNER JOIN kg_sys_api AS c " +
            "ON b.api_oid = c.oid " +
            "INNER JOIN kg_sys_command AS d " +
            "ON d.oid = a.command_oid " +
            "INNER JOIN kg_sys_menu AS e " +
            "ON d.menu_oid = e.oid " +
            "INNER JOIN kg_sys_sub_menu AS f " +
            "ON e.sub_menu_oid = f.oid " +
            "WHERE a.role_oid = #{roleOID} " +
            "AND c.delete_at IS NULL AND c.status = #{status} " +
            "AND d.delete_at IS NULL AND d.status = #{status} " +
            "AND e.delete_at IS NULL AND e.status = #{status} " +
            "AND f.delete_at IS NULL AND f.status = #{status} ")
    List<String> getApiOIDsInCommandByRoleOID(@Param("roleOID")String roleOID,@Param("status")int status);

    @Select("SELECT c.oid FROM kg_sys_role_operate AS a " +
            "INNER JOIN kg_sys_operate AS b " +
            "ON a.operate_oid = b.oid " +
            "INNER JOIN kg_sys_api AS c " +
            "ON b.api_oid = c.oid " +
            "INNER JOIN kg_sys_command AS e " +
            "ON b.command_oid = e.oid " +
            "INNER JOIN kg_sys_menu AS f " +
            "ON e.menu_oid = f.oid " +
            "INNER JOIN kg_sys_sub_menu AS g " +
            "ON f.sub_menu_oid = g.oid " +
            "WHERE a.role_oid = #{roleOID} " +
            "AND b.delete_at IS NULL AND b.status = #{status} " +
            "AND c.delete_at IS NULL AND c.status = #{status} " +
            "AND e.delete_at IS NULL AND e.status = #{status} " +
            "AND f.delete_at IS NULL AND f.status = #{status} " +
            "AND g.delete_at IS NULL AND g.status = #{status} ")
    List<String> getApiOIDsInOperateByRoleOID(@Param("roleOID")String roleOID,@Param("status")int status);	

    @Select("<script>" +
            "SELECT " +
            "oid,api,name,description,status," +
            "auth_type AS authType, " +
            "business_type AS businessType, " +
            "init_flag AS initFlag, " +
            "create_at AS createAt, " +
            "update_at AS updateAt, " +
            "delete_at AS deleteAt " +
            "FROM kg_sys_api WHERE delete_at IS NULL AND init_flag &lt;= #{init} " +
            "<if test = 'condition!=null and condition!=\"\" '>" +
            "AND (api LIKE #{condition} " +
            "OR name LIKE #{condition} " +
            "OR description LIKE #{condition}) " +
            "</if>" +
            "<if test = 'businessTypeList!=null'>" +
            "AND business_type IN ( " +
            "<foreach collection='businessTypeList' item='businessType' index='index' separator=','>" +
            "#{businessType} " +
            "</foreach>" +
            ") " +
            "</if>" +
            "ORDER BY business_type,api " +
            "</script>")
    List<KgApi> getApiList(@Param("condition")String condition,@Param("businessTypeList")List<String> businessTypeList,@Param("init")int init);

    @Select("SELECT " +
            "a.oid AS oid, a.name AS name, a.description AS description, " +
            "a.api AS api, a.status AS status, a.business_type AS businessType, " +
            "a.auth_type AS authType, a.create_at AS createAt, " +
            "a.update_at AS updateAt, a.delete_at AS deleteAt " +
            "FROM kg_sys_api AS a " +
            "INNER JOIN kg_sys_command_api AS b " +
            "ON a.oid = b.api_oid " +
            "WHERE b.command_oid = #{commandOID} AND a.delete_at IS NULL")
    List<KgApi> getApiListByCommandOID(@Param("commandOID")String commandOID);

    @Select("SELECT COUNT(oid) FROM kg_sys_api " +
            "WHERE api = #{api} " +
            "AND delete_at IS NULL")
    int checkAPIExist(@Param("api")String api);

    @Select("<script>" +
            "SELECT COUNT(oid) FROM kg_sys_api " +
            "WHERE api = #{api} " +
            "AND delete_at IS NULL AND oid NOT IN ( " +
            "<foreach collection='exceptOIDList' item='oid' index='index' separator=','>" +
            "#{oid} " +
            "</foreach>" +
            ") " +
            "</script>")
    int checkAPIExistExcept(@Param("api")String api,@Param("exceptOIDList")List<String> exceptOIDList);
}
