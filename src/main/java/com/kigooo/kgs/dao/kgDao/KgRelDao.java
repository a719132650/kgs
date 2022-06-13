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
public interface KgRelDao {
     //user2role
     @Insert("<script>" +
        "<foreach collection='roleOIDList' item ='roleOID'>" +
        "INSERT INTO kg_sys_user_role " +
        "(user_oid, role_oid, create_at, init_flag) " +
        "VALUES " +
        "(#{userOID}, #{roleOID}, #{createAt}, 0); " +
        "</foreach>" +
        "</script>")
    void createUser2Roles(@Param("userOID")String userOID,@Param("roleOIDList") List<String> roleOIDList,@Param("createAt") long createAt);

    @Insert("<script>" +
        "<foreach collection='userOIDList' item ='userOID' >" +
        "INSERT INTO kg_sys_user_role " +
        "(user_oid, role_oid, create_at, init_flag) " +
        "VALUES " +
        "(#{userOID}, #{roleOID}, #{createAt}, 0); " +
        "</foreach>" +
        "</script>")
    void createUsers2Role(@Param("userOIDList") List<String> userOIDList,@Param("roleOID")String roleOID,@Param("createAt") long createAt);

    @Delete("<script>DELETE FROM kg_sys_user_role WHERE rid in (" +
            "<foreach collection='ridList' item ='rid' separator=','>" +
            "#{rid}" +
            "</foreach>" +
            ")</script>")
    void deleteUser2Role(@Param("ridList")List<String> ridList);

    @Delete("DELETE FROM kg_sys_user_role WHERE user_oid = #{userOID}")
    void deleteUser2RoleByUserOID(@Param("userOID")String userOID);

    @Delete("DELETE FROM kg_sys_user_role WHERE role_oid = #{roleOID}")
    void deleteUser2RoleByRoleOID(@Param("roleOID")String roleOID);

    @Select("SELECT " +
            "a.rid AS rid, " +
            "a.user_oid AS fromOID, " +
            "a.role_oid AS toOID " +
            "FROM kg_sys_user_role AS a " +
            "INNER JOIN kg_sys_role AS b " +
            "ON a.role_oid = b.oid " +
            "WHERE a.user_oid = #{userOID} " +
            "AND b.delete_at IS NULL")
    @Results({
            @Result(column = "fromOID",property = "fromOID"),
            @Result(column = "toOID",property = "toOID"),
            @Result(column = "fromOID",property = "fromObj",javaType = KgUser.class,one = @One(select = "com.kigooo.kgs.dao.kgDao.KgUserDao.getUserByOID")),
            @Result(column = "toOID",property = "toObj",javaType = KgRole.class,one = @One(select = "com.kigooo.kgs.dao.kgDao.KgRoleDao.getRoleByOID"))
    })
    List<KgRelationship> getUser2RoleByUserOID(@Param("userOID") String userOID);

    @Select("<script>" +
            "SELECT " +
            "a.rid AS rid, " +
            "a.role_oid AS fromOID, " +
            "a.user_oid AS toOID " +
            "FROM kg_sys_user_role AS a " +
            "INNER JOIN kg_sys_user AS b " +
            "ON a.user_oid = b.oid " +
            "WHERE a.role_oid = #{roleOID} " +
            "AND b.delete_at IS NULL " +
            "<if test = 'condition!=null and condition!=\"\" '>" +
            "AND (b.u_account like #{condition} " +
            "OR b.u_name like #{condition} ) " +
            "</if>" +
            "ORDER BY b.u_account " +
            "</script>")
    @Results({
            @Result(column = "fromOID",property = "fromOID"),
            @Result(column = "toOID",property = "toOID"),
            @Result(column = "fromOID",property = "fromObj",javaType = KgRole.class,one = @One(select = "com.kigooo.kgs.dao.kgDao.KgRoleDao.getRoleByOID")),
            @Result(column = "toOID",property = "toObj",javaType = KgUser.class,one = @One(select = "com.kigooo.kgs.dao.kgDao.KgUserDao.getUserByOID"))
    })
    List<KgRelationship> getUser2RoleByRoleOID(@Param("roleOID") String roleOID,@Param("condition")String condition);

    @Select("<script>" +
            "select count(rid) from kg_sys_user_role where rid in " + 
            "(<foreach collection='ridList' item ='rid' separator=','>" +
            "#{rid}" +
            "</foreach>) " +
            "and user_oid in (select oid from kg_sys_user where init_flag > #{init}) " + 
            "and role_oid in (select oid from kg_sys_role where init_flag > #{init}) " +
            "</script>")
    int checkUser2RoleDoByRids(@Param("ridList")List<String> ridList,@Param("init")int init);

    @Select("SELECT COUNT(rid) FROM kg_sys_user_role WHERE role_oid = #{roleOID} AND user_oid = #{userOID} LIMIT 1")
    int checkUser2RoleExist(@Param("userOID")String userOID,@Param("roleOID")String roleOID);

    //role2command
    @Insert("<script>" +
            "<foreach collection=\"commandOIDList\" item =\"commandOID\" >" +
            "INSERT INTO kg_sys_role_command " +
            "(role_oid, command_oid, create_at, init_flag) " +
            "VALUES " +
            "(#{roleOID}, #{commandOID}, #{createAt}, 0); " +
            "</foreach>" +
            "</script>")
    void createRole2Commands(@Param("roleOID")String roleOID,@Param("commandOIDList")List<String>commandOIDList,@Param("createAt")long createAt);

    @Insert("<script>" +
            "<foreach collection=\"roleOIDList\" item =\"roleOID\" >" +
            "INSERT INTO kg_sys_role_command " +
            "(role_oid, command_oid, create_at, init_flag) " +
            "VALUES " +
            "(#{roleOID}, #{commandOID}, #{createAt}, 0); " +
            "</foreach>" +
            "</script>")
    void createRoles2Command(@Param("roleOIDList")List<String>roleOIDList,@Param("commandOID")String commandOID,@Param("createAt")long createAt);

    @Delete("<script>" +
            "<foreach collection='ridList' item='rid' index='index'>" +
            "DELETE FROM kg_sys_role_command WHERE rid = #{rid} LIMIT 1; " +
            "</foreach>" +
            "</script>")
    void deleteRole2Command(@Param("ridList")List<String> ridList);

    @Delete("DELETE FROM kg_sys_role_command WHERE role_oid = #{roleOID}")
    void deleteRole2CommandByRoleOID(@Param("roleOID")String roleOID);

    @Delete("DELETE FROM kg_sys_role_command WHERE command_oid = #{commandOID}")
    void deleteRole2CommandByCommandOID(@Param("commandOID")String commandOID);

    @Select("SELECT " +
            "a.role_oid AS fromOID, " +
            "a.command_oid AS toOID, " +
            "a.rid AS rid " +
            "FROM kg_sys_role_command AS a " +
            "INNER JOIN kg_sys_command AS b " +
            "ON a.command_oid = b.oid " +
            "WHERE a.role_oid = #{roleOID} AND b.delete_at IS NULL;")
    @Results({
            @Result(column = "fromOID",property = "fromOID"),
            @Result(column = "toOID",property = "toOID"),
            @Result(column = "fromOID",property = "fromObj",javaType = KgRole.class,one = @One(select = "com.kigooo.kgs.dao.kgDao.KgRoleDao.getRoleByOID")),
            @Result(column = "toOID",property = "toObj",javaType = KgCommand.class,one = @One(select = "com.kigooo.kgs.dao.kgDao.KgMenuDao.getCommandByOID"))
    })
    List<KgRelationship> getRole2CommandByRoleOID(@Param("roleOID") String roleOID);

    @Select("SELECT " +
            "rid, " +
            "command_oid AS fromOID, " +
            "role_oid AS toOID " +
            "FROM kg_sys_role_command " +
            "WHERE command_oid = #{commandOID} ")
    @Results({
            @Result(column = "fromOID",property = "fromOID"),
            @Result(column = "toOID",property = "toOID"),
            @Result(column = "fromOID",property = "fromObj",javaType = KgCommand.class,one = @One(select = "com.kigooo.kgs.dao.kgDao.KgMenuDao.getCommandByOID")),
            @Result(column = "toOID",property = "toObj",javaType = KgRole.class,one = @One(select = "com.kigooo.kgs.dao.kgDao.KgRoleDao.getRoleByOID"))
    })
    List<KgRelationship> getRole2CommandByCommandOID(@Param("commandOID")String commandOID);

    @Select("SELECT COUNT(rid) FROM kg_sys_role_command " +
            "WHERE role_oid = #{roleOID} AND command_oid = #{commandOID} " +
            "AND delete_at IS NULL LIMIT 1")
    int checkRole2CommandExist(@Param("roleOID")String roleOID,@Param("commandOID")String commandOID);

    //role2operate
    @Insert("<script>" +
            "<foreach collection='operateOIDList' item='operateOID'>" +
            "INSERT INTO kg_sys_role_operate (role_oid,operate_oid,create_at,init_flag) " +
            "VALUES " +
            "(#{roleOID}, #{operateOID}, #{createAt},0);" +
            "</foreach>" +
            "</script>")
    void createRole2Operates(@Param("roleOID")String roleOID,@Param("operateOIDList")List<String> operateOIDList,@Param("createAt")long createAt);

    @Insert("<script>" +
            "<foreach collection='roleOIDList' item='roleOID'>" +
            "INSERT INTO kg_sys_role_operate (role_oid,operate_oid,create_at,init_flag) " +
            "VALUES " +
            "(#{roleOID}, #{operateOID}, #{createAt},0);" +
            "</foreach>" +
            "</script>")
    void createRoles2Operate(@Param("roleOIDList")List<String> roleOIDList,@Param("operateOID")String operateOID,@Param("createAt")long createAt);

    @Delete("<script>" +
            "<foreach collection='ridList' item='rid' index='index'>" +
            "DELETE FROM kg_sys_role_operate WHERE rid = #{rid} LIMIT 1; " +
            "</foreach>" +
            "</script>")
    void deleteRole2Operate(@Param("ridList")List<String> ridList);

    @Delete("DELETE FROM kg_sys_role_operate WHERE role_oid = #{roleOID} ")
    void deleteRole2OperateByRoleOID(@Param("roleOID")String roleOID);

    @Select("SELECT " +
            "a.rid AS rid, " +
            "a.role_oid AS fromOID, " +
            "a.operate_oid AS toOID " +
            "FROM kg_sys_role_operate AS a " +
            "INNER JOIN kg_sys_operate AS b " +
            "ON a.operate_oid = b.oid " +
            "WHERE a.role_oid = #{roleOID} AND b.delete_at IS NULL;")
    @Results({
            @Result(column = "fromOID",property = "fromOID"),
            @Result(column = "toOID",property = "toOID"),
            @Result(column = "fromOID",property = "fromObj",javaType = KgRole.class,one = @One(select = "com.kigooo.kgs.dao.kgDao.KgRoleDao.getRoleByOID")),
            @Result(column = "toOID",property = "toObj",javaType = KgOperate.class,one = @One(select = "com.kigooo.kgs.dao.kgDao.KgMenuDao.getOperateByOID"))
    })
    List<KgRelationship> getRole2OperateByRoleOID(@Param("roleOID") String roleOID);

    @Select("SELECT " +
            "rid, " +
            "operate_oid AS fromOID, " +
            "role_oid AS toOID " +
            "FROM kg_sys_role_operate " +
            "WHERE operate_oid = #{operateOID} ")
    @Results({
            @Result(column = "fromOID",property = "fromOID"),
            @Result(column = "toOID",property = "toOID"),
            @Result(column = "fromOID",property = "fromObj",javaType = KgOperate.class,one = @One(select = "com.kigooo.kgs.dao.kgDao.KgMenuDao.getOperateByOID")),
            @Result(column = "toOID",property = "toObj",javaType = KgRole.class,one = @One(select = "com.kigooo.kgs.dao.kgDao.KgRoleDao.getRoleByOID"))
    })
    List<KgRelationship> getRole2OperateByOperateOID(@Param("operateOID")String operateOID);

    //role2range
    @Insert("<script>" +
            "<foreach collection=\"rangeOIDList\" item =\"rangeOID\" >" +
            "INSERT INTO kg_sys_role_range " +
            "(role_oid, range_oid, create_at) " +
            "VALUES " +
            "(#{roleOID}, #{rangeOID}, #{createAt}); " +
            "</foreach>" +
            "</script>")
    void createRole2Ranges(@Param("roleOID")String roleOID,@Param("rangeOIDList") List<String> rangeOIDList,@Param("createAt") long createAt);

    @Delete("<script>" +
            "<foreach collection=\"ridList\" item =\"rid\" >" +
            "DELETE FROM kg_sys_role_range WHERE rid = #{rid}; " +
            "</foreach>" +
            "</script>")
    void deleteRole2Range(@Param("ridList")List<String> ridList);

    @Select("SELECT " +
            "a.rid AS rid, " +
            "a.role_oid AS fromOID, " +
            "a.range_oid AS toOID " +
            "FROM kg_sys_role_range AS a " +
            "INNER JOIN kg_sys_range AS b " +
            "ON a.range_oid = b.oid " +
            "WHERE a.role_oid = #{roleOID} " +
            "AND b.delete_at IS NULL")
    @Results({
            @Result(column = "fromOID",property = "fromOID"),
            @Result(column = "toOID",property = "toOID"),
            @Result(column = "fromOID",property = "fromObj",javaType = KgUser.class,one = @One(select = "com.kigooo.kgs.dao.kgDao.KgRoleDao.getRoleByOID")),
            @Result(column = "toOID",property = "toObj",javaType = KgRole.class,one = @One(select = "com.kigooo.kgs.dao.kgDao.KgRangeDao.getRangeByOID"))
    })
    List<KgRelationship> getRole2RangeByRoleOID(@Param("roleOID") String roleOID);

    @Select("SELECT COUNT(rid) FROM kg_sys_role_range " +
            "WHERE role_oid = #{roleOID} AND range_oid = #{rangeOID} LIMIT 1 ")
    int checkRole2RangeExist(@Param("roleOID")String roleOID,@Param("rangeOID")String rangeOID);
    
    //command2api
    @Insert("<script>" +
            "<foreach collection='apiOIDList' item='apiOID'>" +
            "INSERT INTO kg_sys_command_api " +
            "(command_oid, api_oid, create_at, init_flag) " +
            "VALUES " +
            "(#{commandOID}, #{apiOID}, #{createAt}, 0); " +
            "</foreach>" +
            "</script>")
    void createCommand2Apis(@Param("commandOID")String commandOID,@Param("apiOIDList")List<String> apiOIDList,@Param("createAt")long createAt);

    @Insert("<script>" +
            "<foreach collection='commandOIDList' item='commandOID'>" +
            "INSERT INTO kg_sys_command_api " +
            "(command_oid, api_oid, create_at, init_flag) " +
            "VALUES " +
            "(#{commandOID}, #{apiOID}, #{createAt}, 0); " +
            "</foreach>" +
            "</script>")
    void createCommands2Api(@Param("apiOID")String apiOID,@Param("commandOIDList")List<String> commandOIDList,@Param("createAt")long createAt);

    @Delete("<script>" +
            "<foreach collection=\"ridList\" item =\"rid\" >" +
            "DELETE FROM kg_sys_command_api WHERE rid = #{rid}; " +
            "</foreach>" +
            "</script>")
    void deleteCommand2Api(@Param("ridList")List<String> ridList);

    @Delete("DELETE FROM kg_sys_command_api WHERE command_oid=#{commandOID}")
    void deleteCommand2ApiByCommandOID(@Param("commandOID")String commandOID);

    @Select("SELECT " +
            "a.rid AS rid, " +
            "a.command_oid AS fromOID, " +
            "a.api_oid AS toOID " +
            "FROM kg_sys_command_api AS a " +
            "INNER JOIN kg_sys_command AS b " +
            "ON a.command_oid = b.oid " +
            "WHERE b.delete_at IS NULL " +
            "AND a.command_oid = #{commandOID} ")
    @Results({
            @Result(column = "fromOID",property = "fromOID"),
            @Result(column = "toOID",property = "toOID"),
            @Result(column = "fromOID",property = "fromObj",javaType = KgCommand.class,one = @One(select = "com.kigooo.kgs.dao.kgDao.KgMenuDao.getCommandByOID")),
            @Result(column = "toOID",property = "toObj",javaType = KgApi.class,one = @One(select = "com.kigooo.kgs.dao.kgDao.KgApiDao.getApiByOID"))
    })
    List<KgRelationship> getCommand2ApiByCommandOID(@Param("commandOID")String commandOID);

    @Select("SELECT " +
            "a.rid AS rid, " +
            "a.command_oid AS fromOID, " +
            "a.api_oid AS toOID " +
            "FROM kg_sys_command_api AS a " +
            "INNER JOIN kg_sys_command AS b " +
            "ON a.command_oid = b.oid " +
            "WHERE b.delete_at IS NULL " +
            "AND a.api_oid = #{apiOID} ")
    @Results({
            @Result(column = "fromOID",property = "fromOID"),
            @Result(column = "toOID",property = "toOID"),
            @Result(column = "fromOID",property = "fromObj",javaType = KgCommand.class,one = @One(select = "com.kigooo.kgs.dao.kgDao.KgMenuDao.getCommandByOID")),
            @Result(column = "toOID",property = "toObj",javaType = KgApi.class,one = @One(select = "com.kigooo.kgs.dao.kgDao.KgApiDao.getApiByOID"))
    })
    List<KgRelationship> getCommand2ApiByApiOID(@Param("apiOID")String apiOID);

    @Select("SELECT COUNT(rid) FROM kg_sys_command_api WHERE command_oid = #{commandOID} AND api_oid = #{apiOID} LIMIT 1")
    int checkCommand2ApiExist(@Param("commandOID")String commandOID,@Param("apiOID")String apiOID);

    //role2api
    @Insert("<script>" +
            "<foreach collection=\"apiOIDList\" item =\"apiOID\" >" +
            "INSERT INTO kg_sys_role_api " +
            "(api_oid, role_oid, create_at) " +
            "VALUES " +
            "(#{apiOID}, #{roleOID}, #{createAt}); " +
            "</foreach>" +
            "</script>")
    void createRole2Apis(@Param("roleOID")String roleOID,@Param("apiOIDList") List<String> apiOIDList,@Param("createAt") long createAt);

    @Insert("<script>" +
            "<foreach collection=\"roleOIDList\" item =\"roleOID\" >" +
            "INSERT INTO kg_sys_role_api " +
            "(api_oid, role_oid, create_at) " +
            "VALUES " +
            "(#{apiOID}, #{roleOID}, #{createAt}); " +
            "</foreach>" +
            "</script>")
    void createRoles2Api(@Param("roleOIDList") List<String> roleOIDList,@Param("apiOID")String apiOID,@Param("createAt") long createAt);

    @Insert("<script>" +
            "<foreach collection=\"roleOIDList\" item =\"roleOID\" index='index'>" +
            "INSERT INTO kg_sys_role_api " +
            "(api_oid, role_oid, create_at) " +
            "VALUES " +
            "(#{apiOIDList[${index}]}, #{roleOID}, #{createAt}); " +
            "</foreach>" +
            "</script>")
    void createRoles2Apis(@Param("roleOIDList")List<String> roleOIDList,@Param("apiOIDList") List<String> apiOIDList,@Param("createAt") long createAt);

    @Update("delete from kg_sys_role_api")
    void deleteRole2ApiAll();

    @Delete("<script>" +
            "<foreach collection=\"ridList\" item =\"rid\" >" +
            "DELETE FROM kg_sys_role_api WHERE rid = #{rid}; " +
            "</foreach>" +
            "</script>")
    void deleteRole2Api(@Param("ridList")List<String> ridList);

    @Delete("<script>" +
            "DELETE FROM kg_sys_role_api WHERE role_oid IN (" +
            "<foreach collection='roleOIDList' item='roleOID' index='index' separator=','>" +
            "#{roleOID} " +
            "</foreach>" +
            ")" +
            "</script>")
    void deleteRole2ApiByRoleOIDs(@Param("roleOIDList")List<String> roleOIDList);

    @Delete("<script>" +
            "DELETE FROM kg_sys_role_api WHERE api_oid IN (" +
            "<foreach collection='apiOIDList' item='apiOID' index='index' separator=','>" +
            "#{apiOID} " +
            "</foreach>" +
            ")" +
            "</script>")
    void deleteRole2ApiByApiOIDs(@Param("apiOIDList")List<String> apiOIDList);

    @Select("<script>" +
            "SELECT rid FROM kg_sys_role_api WHERE role_oid IN (" +
            "<foreach collection='roleOIDList' item='roleOID' index='index' separator=','>" +
            "#{roleOID} " +
            "</foreach>" +
            ")" +
            "</script>")
    List<String> getRole2ApiRIDsByRoleOIDs(@Param("roleOIDList")List<String> roleOIDList);

    @Select("<script>" +
            "SELECT rid FROM kg_sys_role_api WHERE api_oid IN (" +
            "<foreach collection='apiOIDList' item='apiOID' index='index' separator=','>" +
            "#{apiOID} " +
            "</foreach>" +
            ")" +
            "</script>")
    List<String> getRole2ApiRIDsByApiOIDs(@Param("apiOIDList")List<String> apiOIDList);

    @Select("SELECT COUNT(rid) FROM kg_sys_role_api " +
            "WHERE api_oid = #{apiOID} AND role_oid = #{roleOID} LIMIT 1 ")
    int checkRole2ApiExist(@Param("roleOID")String roleOID,@Param("apiOID")String apiOID);
}
