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
public interface KgMenuDao {
    //SubMenu
    @Insert("INSERT INTO kg_sys_sub_menu " +
            "(name, sort, status, create_at, init_flag) " +
            "VALUES " +
            "(#{kgSubMenu.name}, #{kgSubMenu.sort}, #{kgSubMenu.status}, #{kgSubMenu.createAt}, 0)")
    @Options(useGeneratedKeys = true,keyColumn = "oid",keyProperty = "kgSubMenu.oid")
    void createSubMenu(@Param("kgSubMenu") KgSubMenu kgSubMenu);

    @Update("UPDATE kg_sys_sub_menu SET " +
            "delete_at = #{deleteAt} " +
            "WHERE oid = #{subMenuOID} LIMIT 1")
    void deleteSubMenu(@Param("subMenuOID") String subMenuOID,@Param("deleteAt") long deleteAt);

    @Update("UPDATE kg_sys_sub_menu SET " +
            "name = #{kgSubMenu.name}, " +
            "update_at = #{kgSubMenu.updateAt} " +
            "WHERE oid = #{kgSubMenu.oid} LIMIT 1")
    void updateSubMenu(@Param("kgSubMenu") KgSubMenu kgSubMenu);

    @Update("<script>" +
            "UPDATE kg_sys_sub_menu SET " +
            "status = #{status} " +
            "WHERE oid IN (" +
            "<foreach collection='subMenuOIDList' item='subMenuOID' index='index' separator=','>" +
            "#{subMenuOID} " +
            "</foreach>" +
            ") LIMIT 1" +
            "</script>")
    void updateSubMenuStatus(@Param("subMenuOIDList") List<String> subMenuOIDList,@Param("status") int status);

    @Update("UPDATE kg_sys_sub_menu SET sort = #{sort} WHERE oid = #{subMenuOID} LIMIT 1")
    void updateSubMenuSort(@Param("subMenuOID")String subMenuOID,@Param("sort")int sort);

    @Select("SELECT " +
            "oid, name, sort, status, create_at AS createAt, update_at AS updateAt, delete_at AS deleteAt, init_flag AS initFlag " +
            "FROM kg_sys_sub_menu WHERE oid = #{subMenuOID} LIMIT 1")
    KgSubMenu getSubMenuByOID(@Param("subMenuOID")String subMenuOID);

    @Select ("SELECT oid FROM kg_sys_sub_menu WHERE delete_at IS NULL ORDER BY SORT")
    List<String> getSubMenuOIDs();

    @Select("SELECT MAX(sort) FROM kg_sys_sub_menu WHERE delete_at IS NULL")
    String getMaxSortOfSubMenu();

    @Select("<script>" +
            "SELECT " +
            "DISTINCT a.oid AS oid, a.name AS name, a.sort AS sort, a.status AS status, " +
            "a.create_at AS createAt, a.update_at AS updateAt, a.delete_at AS deleteAt " +
            "FROM kg_sys_sub_menu AS a " +
            "INNER JOIN kg_sys_menu AS b ON a.oid = b.sub_menu_oid WHERE b.oid IN (" +
            "<foreach collection='menuOIDList' item='menuOID' index='index' separator=','>" +
            "#{menuOID} " +
            "</foreach>" +
            ") AND  a.delete_at IS NULL ORDER BY a.sort" +
            "</script>")
    List<KgSubMenu> getSubMenuListByMenuOIDs(@Param("menuOIDList")List<String> menuOIDList);

    @Update("<script>" +
            "<foreach collection='subMenuOIDList' item='subMenuOID' index='index' separator=';'>" +
            "UPDATE kg_sys_sub_menu SET " +
            "sort = (#{index}*10+10)" +
            "WHERE oid = #{subMenuOID} LIMIT 1" +
            "</foreach>" +
            "</script>")
    void resetSubMenuSort(@Param("subMenuOIDList")List<String> subMenuOIDList);

    @Select("SELECT COUNT(oid) FROM kg_sys_sub_menu " +
        "WHERE name = #{subMenuName} AND delete_at IS NULL LIMIT 1")
    int checkSubMenuNameExist(@Param("subMenuName") String subMenuName);

    @Select("<script>" +
            "SELECT COUNT(oid) FROM kg_sys_sub_menu " +
            "WHERE name = #{subMenuName} AND delete_at IS NULL AND oid NOT IN (" +
            "<foreach collection='exceptOIDList' item='oid' index='index' separator=','>" +
            "#{oid} " +
            "</foreach>" +
            ") LIMIT 1 " +
            "</script>")
    int checkSubMenuNameExistExcept(@Param("subMenuName") String subMenuName,@Param("exceptOIDList")List<String> exceptOIDList);

    //Menu
    @Insert("INSERT INTO kg_sys_menu " +
            "(name, sort, status, sub_menu_oid, create_at, init_flag) " +
            "VALUES " +
            "(#{kgMenu.name}, #{kgMenu.sort}, " +
            "#{kgMenu.status}, #{kgMenu.subMenuOid}, #{kgMenu.createAt}, 0)")
    @Options(useGeneratedKeys = true,keyColumn = "oid",keyProperty = "kgMenu.oid")
    void createMenu(@Param("kgMenu") KgMenu kgMenu);

    @Update("UPDATE kg_sys_menu SET " +
            "delete_at = #{deleteAt} " +
            "WHERE oid = #{menuOID} LIMIT 1")
    void deleteMenu(@Param("menuOID") String menuOID,@Param("deleteAt") long deleteAt);

    @Update("UPDATE kg_sys_menu SET " +
            "name = #{kgMenu.name}, " +
            "update_at = #{kgMenu.updateAt} " +
            "WHERE oid = #{kgMenu.oid} LIMIT 1")
    void updateMenu(@Param("kgMenu") KgMenu kgMenu);

    @Update("<script>" +
            "UPDATE kg_sys_menu SET " +
            "status = #{status} " +
            "WHERE oid IN (" +
            "<foreach collection='menuOIDList' item='menuOID' index='index' separator=','>" +
            "#{menuOID} " +
            "</foreach>" +
            ")" +
            "</script>")
    void updateMenuStatus(@Param("menuOIDList") List<String> menuOIDList,@Param("status") int status);

    @Update("UPDATE kg_sys_menu SET sort = #{sort} WHERE oid = #{menuOID} LIMIT 1")
    void updateMenuSort(@Param("menuOID")String menuOID,@Param("sort")int sort);

    @Update("UPDATE kg_sys_menu SET sub_menu_oid = #{subMenuOID} WHERE oid = #{menuOID} LIMIT 1")
    void updateMenuParentOID(@Param("menuOID")String menuOID,@Param("subMenuOID")String subMenuOID);

    @Select("SELECT " +
            "oid, name, sort, status, sub_menu_oid AS subMenuOid, " +
            "create_at AS createAt, update_at AS updateAt, delete_at AS deleteAt, init_flag AS initFlag " +
            "FROM kg_sys_menu WHERE oid = #{menuOID} LIMIT 1")
    KgMenu getMenuByOID(@Param("menuOID")String menuOID);

    @Select ("SELECT oid FROM kg_sys_menu WHERE sub_menu_oid=#{subMenuOID} AND delete_at IS NULL ORDER BY sort")
    List<String> getMenuOIDsBySubMenuOID(@Param("subMenuOID")String subMenuOID);

    @Select("SELECT MAX(sort) FROM kg_sys_menu WHERE delete_at IS NULL AND sub_menu_oid = #{subMenuOID} ")
    String getMaxSortOfMenuBySubMenuOID(@Param("subMenuOID")String subMenuOID);

    @Select("SELECT COUNT(*) FROM kg_sys_menu WHERE sub_menu_oid = #{subMenuOID} AND delete_at IS NULL")
    int getMenuCountBySubMenuOID(@Param("subMenuOID")String subMenuOID);

    @Select("<script>" +
            "SELECT " +
            "DISTINCT a.oid AS oid, a.name AS name, a.sort AS sort, a.status AS status, " +
            "a.sub_menu_oid AS subMenuOid, a.create_at as createAt, " +
            "a.update_at AS updateAt, a.delete_at as deleteAt " +
            "FROM kg_sys_menu AS a INNER JOIN kg_sys_command AS b ON a.oid = b.menu_oid WHERE b.oid IN (" +
            "<foreach collection='commandOIDList' item='commandOID' index='index' separator=','>" +
            "#{commandOID} " +
            "</foreach>" +
            ") AND a.delete_at IS NULL ORDER BY a.sort" +
            "</script>")
    List<KgMenu> getMenuListByCommandOIDs(@Param("commandOIDList")List<String> commandOIDList);

    @Update("<script>" +
            "<foreach collection='menuOIDList' item='menuOID' index='index' separator=';'>" +
            "UPDATE kg_sys_menu SET " +
            "sort = (#{index}*10+10)" +
            "WHERE oid = #{menuOID} LIMIT 1" +
            "</foreach>" +
            "</script>")
    void resetMenuSort(@Param("menuOIDList")List<String> menuOIDList);

    @Select("SELECT COUNT(oid) FROM kg_sys_menu WHERE " +
            "name = #{name} " +
            "AND sub_menu_oid = #{subMenuOID} " +
            "AND delete_at IS NULL LIMIT 1")
    int checkMenuNameExist(@Param("name") String name,@Param("subMenuOID") String subMenuOID);

    @Select("<script>" +
            "SELECT COUNT(oid) FROM kg_sys_menu WHERE " +
            "name = #{name} " +
            "AND sub_menu_oid = #{subMenuOID} " +
            "AND delete_at IS NULL AND oid NOT IN (" +
            "<foreach collection='exceptOIDList' item='oid' index='index' separator=','>" +
            "#{oid} " +
            "</foreach>" +
            ") LIMIT 1 " +
            "</script>")
    int checkMenuNameExistExcept(@Param("name") String name,@Param("subMenuOID") String subMenuOID,@Param("exceptOIDList")List<String> exceptOIDList);

    //Command
    @Insert("INSERT INTO kg_sys_command " +
            "(name, sort, url, status, menu_oid, create_at, init_flag) " +
            "VALUES " +
            "(#{kgCommand.name}, #{kgCommand.sort}, #{kgCommand.url}, " +
            "#{kgCommand.status}, #{kgCommand.menuOid}, #{kgCommand.createAt}, 0)")
    @Options(useGeneratedKeys = true,keyColumn = "oid",keyProperty = "kgCommand.oid")
    void createCommand(@Param("kgCommand") KgCommand kgCommand);

    @Update("UPDATE kg_sys_command SET " +
            "delete_at = #{deleteAt} " +
            "WHERE oid = #{commandOID} LIMIT 1")
    void deleteCommand(@Param("commandOID") String commandOID,@Param("deleteAt") long deleteAt);

    @Update("UPDATE kg_sys_command SET " +
            "name = #{kgCommand.name}, " +
            "url = #{kgCommand.url}, " +
            "update_at = #{kgCommand.updateAt} " +
            "WHERE oid = #{kgCommand.oid} LIMIT 1")
    void updateCommand(@Param("kgCommand") KgCommand kgCommand);

    @Update("<script>" +
            "<foreach collection='commandOIDList' item='commandOID' index='index' separator=';'>" +
            "UPDATE kg_sys_command SET " +
            "status = #{status} " +
            "WHERE oid = #{commandOID} LIMIT 1" +
            "</foreach>" +
            "</script>")
    void updateCommandStatus(@Param("commandOIDList") List<String> commandOIDList,@Param("status") int status);

    @Update("UPDATE kg_sys_command SET sort = #{sort} WHERE oid = #{commandOID} LIMIT 1")
    void updateCommandSort(@Param("commandOID")String commandOID,@Param("sort")int sort);

    @Update("UPDATE kg_sys_command SET menu_oid = #{menuOID} WHERE oid = #{commandOID} LIMIT 1")
    void updateCommandParentOID(@Param("commandOID")String commandOID,@Param("menuOID")String menuOID);

    @Select("SELECT " +
            "oid, name, sort, url, status, " +
            "menu_oid AS menuOid, " +
            "create_at AS createAt, " +
            "update_at AS updateAt, " +
            "delete_at AS deleteAt, " +
            "init_flag AS initFlag " +
            "FROM kg_sys_command WHERE oid = #{commandOID} LIMIT 1")
    KgCommand getCommandByOID(@Param("commandOID")String commandOID);

    @Select ("SELECT oid FROM kg_sys_command WHERE menu_oid = #{menuOID} AND delete_at IS NULL ORDER BY sort")
    List<String> getCommandOIDsByMenuOID(@Param("menuOID")String menuOID);

    @Select("SELECT MAX(sort) FROM kg_sys_command WHERE delete_at IS NULL AND menu_oid = #{menuOID} ")
    String getMaxSortOfCommandByMenuOID(@Param("menuOID")String menuOID);

    @Select("SELECT COUNT(*) FROM kg_sys_command WHERE menu_oid = #{menuOID} AND delete_at IS NULL")
    int getCommandCountByMenuOID(@Param("menuOID")String menuOID);

    @Select("<script>SELECT COUNT(a.rid) FROM kg_sys_command_api AS a " +
            "INNER JOIN kg_sys_command AS b " +
            "ON a.command_oid = b.oid " +
            "WHERE a.api_oid = #{apiOID} " +
            "AND b.status IN (" +
            "<foreach collection='statusList' item='status' index='index' separator=','>" +
            "#{status} " +
            "</foreach>" +
            ") AND b.delete_at IS NULL</script>")
    int getCommandCountByApiOID(@Param("apiOID")String apiOID,@Param("statusList")List<Integer> statusList);

    @Select("<script>" +
            "SELECT " +
            "DISTINCT a.oid AS oid, a.name AS name, a.sort AS sort, " +
            "a.url AS url, a.status AS status, " +
            "a.menu_oid AS menuOid, a.create_at AS createAt, " +
            "a.update_at AS updateAt, a.delete_at AS deleteAt " +
            " FROM kg_sys_command AS a " +
            "INNER JOIN kg_sys_operate AS b ON a.oid = b.command_oid WHERE b.oid IN (" +
            "<foreach collection='operateOIDList' item='operateOID' index='index' separator=','>" +
            "#{operateOID} " +
            "</foreach>" +
            ") AND a.delete_at IS NULL ORDER BY a.sort" +
            "</script>")
    List<KgCommand> getCommandListByOperateOIDs(@Param("operateOIDList")List<String> operateOIDList);

    @Update("<script>" +
            "<foreach collection='commandOIDList' item='commandOID' index='index' separator=';'>" +
            "UPDATE kg_sys_command SET " +
            "sort = (#{index}*10+10)" +
            "WHERE oid = #{commandOID} LIMIT 1" +
            "</foreach>" +
            "</script>")
    void resetCommandSort(@Param("commandOIDList")List<String> commandOIDList);

    @Select("SELECT COUNT(*) FROM kg_sys_command " +
            "WHERE name = #{name} " +
            "AND menu_oid = #{menuOID} " +
            "AND delete_at IS null " +
            "LIMIT 1")
    int checkCommandNameExist(@Param("name") String name,@Param("menuOID") String menuOID);

    @Select("<script>" +
            "SELECT COUNT(*) FROM kg_sys_command " +
            "WHERE name = #{name} " +
            "AND menu_oid = #{menuOID} " +
            "AND delete_at IS null AND oid NOT IN (" +
            "<foreach collection='exceptOIDList' item='oid' index='index' separator=','>" +
            "#{oid} " +
            "</foreach>" +
            ")" +
            "LIMIT 1 " +
            "</script>")
    int checkCommandNameExistExcept(@Param("name") String name,@Param("menuOID") String menuOID,@Param("exceptOIDList")List<String> exceptOIDList);

    @Select("SELECT COUNT(*) FROM kg_sys_command " +
            "WHERE url = #{url} " +
            "AND menu_oid = #{menuOID} " +
            "AND delete_at IS null " +
            "LIMIT 1")
    int checkCommandUrlExist(@Param("url") String url,@Param("menuOID") String menuOID);

    @Select("<script>" +
            "SELECT COUNT(*) FROM kg_sys_command " +
            "WHERE url = #{url} " +
            "AND menu_oid = #{menuOID} " +
            "AND delete_at IS null AND oid NOT IN (" +
            "<foreach collection='exceptOIDList' item='oid' index='index' separator=','>" +
            "#{oid} " +
            "</foreach>" +
            ") " +
            "LIMIT 1 " +
            "</script>")
    int checkCommandUrlExistExcept(@Param("url") String url,@Param("menuOID") String menuOID,@Param("exceptOIDList")List<String> exceptOIDList);

    //Operate
    @Insert("<script>" +
            "INSERT INTO kg_sys_operate " +
            "(name, command_oid, api_oid, status, description, create_at, init_flag) " +
            "VALUES " +
            "(#{kgOperate.name}, #{kgOperate.commandOid}, " +
            "<choose>" +
            "<when test='kgOperate.apiOid!=null and kgOperate.apiOid!=\"\" '>" +
            "#{kgOperate.apiOid}, " +
            "</when>" +
            "<otherwise>" +
            "NULL, " +
            "</otherwise>" +
            "</choose>" +
            "1, #{kgOperate.description}, #{kgOperate.createAt}, 0)" +
            "</script>")
    @Options(useGeneratedKeys = true,keyColumn = "oid",keyProperty = "kgOperate.oid")
    void createOperate(@Param("kgOperate") KgOperate kgOperate);

    @Update("UPDATE kg_sys_operate SET " +
            "delete_at = #{deleteAt} " +
            "WHERE oid = #{operateOID} LIMIT 1")
    void deleteOperate(@Param("operateOID") String operateOID,@Param("deleteAt") long deleteAt);

    @Update("<script>" +
            "UPDATE kg_sys_operate SET " +
            "name = #{kgOperate.name}, " +
            "api_oid = " +
            "<choose>" +
            "<when test='kgOperate.apiOid!=null and kgOperate.apiOid!=\"\" '>" +
            "#{kgOperate.apiOid}, " +
            "</when>" +
            "<otherwise>" +
            "NULL, " +
            "</otherwise>" +
            "</choose>" +
            "update_at = #{kgOperate.updateAt}, " +
            "description = #{kgOperate.description} " +
            "WHERE oid = #{kgOperate.oid} LIMIT 1" +
            "</script>")
    void updateOperate(@Param("kgOperate") KgOperate kgOperate);

    @Update("<script>" +
            "UPDATE kg_sys_operate SET " +
            "api_oid = NULL WHERE oid IN (" +
            "<foreach collection='operateOIDList' item='oid' index='index' separator=','>" +
            "#{oid} " +
            "</foreach>" +
            ")" +
            "</script>")
    void updateOperatesEmptyApi(@Param("operateOIDList")List<String> operateOIDList);

    @Update("UPDATE kg_sys_operate SET " +
            "api_oid = #{apiOID} " +
            "WHERE oid = #{operateOID} LIMIT 1")
    void updateOperateApi(@Param("operateOID")String operateOID,@Param("apiOID")String apiOID);

    @Update("<script>" +
            "<foreach collection='operateOIDList' item='operateOID' index='index'>" +
            "UPDATE kg_sys_operate SET status = #{status} WHERE oid = #{operateOID} LIMIT 1; " +
            "</foreach>" +
            "</script>")
    void updateOperateStatus(@Param("operateOIDList") List<String> operateOIDList,@Param("status") int status);

    @Select("SELECT " +
            "oid, name, description, " +
            "command_oid AS commandOid, " +
            "api_oid AS apiOid, " +
            "init_flag AS initFlag " +
            "FROM kg_sys_operate " +
            "WHERE oid = #{operateOID} " +
            "AND delete_at IS NULL LIMIT 1")
    KgOperate getOperateByOID(@Param("operateOID") String operateOID);
	
    @Select("<script>" +
            "SELECT oid FROM kg_sys_operate " +
            "WHERE api_oid IN (" +
            "<foreach collection='apiOIDList' item='apiOID' index='index' separator=','>" +
            "#{apiOID}" +
            "</foreach>" +
            ") " +
            "</script>")
    List<String> getOperateOIDsByApiOIDs(@Param("apiOIDList") List<String> apiOIDList);

    @Select("<script>" +
            "SELECT b.oid FROM kg_sys_operate AS a " +
            "INNER JOIN kg_sys_api AS b " +
            "ON a.api_oid = b.oid " +
            "WHERE a.command_oid IN (" +
            "<foreach collection='commandOIDList' item='commandOID' index='index' separator=','>" +
            "#{commandOID} " +
            "</foreach>" +
            ") " +
            "AND a.delete_at IS NULL " +
            "AND b.delete_at IS NULL  " +
            "</script>")
    List<String> getOperateApiOIDsByCommandOIDs(@Param("commandOIDList") List<String> commandOIDList);

    @Select("SELECT COUNT(oid) FROM kg_sys_operate WHERE command_oid = #{commandOID} AND delete_at IS NULL")
    int getOperateCountByCommandOID(@Param("commandOID")String commandOID);

    @Select("<script>" +
            "SELECT COUNT(oid) FROM kg_sys_operate WHERE api_oid = #{apiOID} " +
            "AND status IN (" +
            "<foreach collection='statusList' item='status' index='index' separator=','>" +
            "#{status} " +
            "</foreach>" +
            ") AND delete_at IS NULL" +
            "</script>")
    int getOperateCountByApiOID(@Param("apiOID")String apiOID,@Param("statusList")List<Integer> statusList);

    @Select("SELECT " +
            "oid, name,status, description, " +
            "command_oid AS commandOid, api_oid AS apiOid,  " +
            "init_flag AS initFlag, create_at AS createAt, update_at AS updateAt, delete_at AS deleteAt " +
            "FROM kg_sys_operate " +
            "WHERE command_oid = #{commandOID} AND delete_at IS NULL " +
            "ORDER BY oid")
    List<KgOperate> getOperateListByCommandOID(@Param("commandOID")String commandOID);

    @Select("SELECT " +
            "oid, name, status, description, " +
            "command_oid AS commandOid, " +
            "api_oid AS apiOid, " +
            "create_at AS createAt, " +
            "update_at AS updateAt, " +
            "delete_at AS deleteAt " +
            "FROM kg_sys_operate WHERE api_oid = #{apiOID} AND delete_at IS NULL")
    List<KgOperate> getOperateListByApiOID(@Param("apiOID")String apiOID);

    @Select("SELECT COUNT(oid) FROM kg_sys_operate " +
            "WHERE name = #{name} AND command_oid = #{commandOID} " +
            "AND delete_at IS NULL LIMIT 1")
    int checkOperateNameExist(@Param("name") String name,@Param("commandOID") String commandOID);

    @Select("<script>" +
            "SELECT COUNT(oid) FROM kg_sys_operate " +
            "WHERE name = #{name} AND command_oid = #{commandOID} " +
            "AND delete_at IS NULL AND oid NOT IN (" +
            "<foreach collection='exceptOIDList' item='oid' index='index' separator=','>" +
            "#{oid} " +
            "</foreach>" +
            ") LIMIT 1 " +
            "</script>")
    int checkOperateNameExistExcept(@Param("name") String name,@Param("commandOID") String commandOID,@Param("exceptOIDList")List<String> exceptOIDList);

    //MenuTree
    @Select("<script>SELECT " +
            "oid, name, sort/10 AS sort, status, " +
            "init_flag AS initFlag, create_at AS createAt, update_at AS updateAt, delete_at AS deleteAt, #{init} AS init " +
            "FROM kg_sys_sub_menu WHERE delete_at IS NULL AND init_flag &lt;= #{init} ORDER BY sort</script>")
    @Results({
            @Result(column ="oid" ,property ="oid"),
            @Result(column = "menuSubOID=oid,init=init",property = "children",javaType = java.util.List.class,
                    many = @Many(select = "getMenuListBySubMenuOID"))
    })
    List<KgSubMenu> getSubMenuTreeAll(@Param("init") int init);

    @Select("<script>SELECT " +
            "oid, name, sort/10 AS sort, status, " +
            "sub_menu_oid AS subMenuOid, init_flag AS initFlag, create_at as createAt, " +
            "update_at AS updateAt, delete_at as deleteAt, #{init} AS init " +
            "FROM kg_sys_menu " +
            "WHERE sub_menu_oid = #{menuSubOID} AND delete_at IS NULL AND init_flag &lt;= #{init} " +
            "ORDER BY sort</script>")
    @Results({
            @Result(column ="oid" ,property ="oid"),
            @Result(column = "menuOID=oid,init=init",property = "children",javaType = java.util.List.class,
                    many = @Many(select = "getCommandListByMenuOID"))
    })
    List<KgMenu> getMenuListBySubMenuOID(@Param("menuSubOID")String menuSubOID,@Param("init") int init);

    @Select("<script>SELECT " +
            "oid, name, sort/10 AS sort, url, status, " +
            "menu_oid AS menuOid, init_flag AS initFlag, create_at AS createAt, " +
            "update_at AS updateAt, delete_at AS deleteAt " +
            "FROM kg_sys_command " +
            "WHERE menu_oid = #{menuOID} AND delete_at IS NULL AND init_flag &lt;= #{init} " +
            "ORDER BY sort</script>")
    @Results({
            @Result(column ="oid" ,property ="oid"),
            @Result(column = "oid",property = "children",javaType = java.util.List.class,
                    many = @Many(select = "getOperateListByCommandOID"))
    })
    List<KgCommand> getCommandListByMenuOID(@Param("menuOID")String menuOID,@Param("init") int init);
}
