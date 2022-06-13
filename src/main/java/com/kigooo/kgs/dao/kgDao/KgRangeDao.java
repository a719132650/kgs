package com.kigooo.kgs.dao.kgDao;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.util.List;
import com.kigooo.kgs.domain.kgDomain.KgRange;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface KgRangeDao {
    @Insert("INSERT INTO kg_sys_range " +
            "(data_mode, range_id, id, label, description, ctl1, ctl2, ctl3, status, init_flag, create_at) " +
            "VALUES " +
            "(#{kgRange.dataMode}, #{kgRange.rangeId}, #{kgRange.id}, #{kgRange.label}, #{kgRange.description}, " +
            "#{kgRange.ctl1}, #{kgRange.ctl2}, #{kgRange.ctl3}, #{kgRange.status}, 0, #{kgRange.createAt}) ")
    void createRange(@Param("kgRange") KgRange kgRange);

    @Update("<script>" +
            "UPDATE kg_sys_range SET delete_at = #{deleteAt} WHERE oid IN (" +
            "<foreach collection='oidList' item='oid' index='index' separator=','>" +
            "#{oid} " +
            "</foreach>" +
            ") " +
            "</script>")
    void deleteRange(@Param("oidList")List<String> oidList,@Param("deleteAt")long deleteAt);

    @Update("UPDATE kg_sys_range " +
            "SET data_mode = #{kgRange.dataMode}, range_id = #{kgRange.rangeId}, id = #{kgRange.id}, label = #{kgRange.label}, " +
            "description = #{kgRange.description}, ctl1 = #{kgRange.ctl1}, ctl2 = #{kgRange.ctl2}, " +
            "ctl3 = #{kgRange.ctl3}, update_at = #{kgRange.updateAt} " +
            "WHERE oid = #{kgRange.oid} ")
    void updateRange(@Param("kgRange")KgRange kgRange);

    @Update("UPDATE kg_sys_range SET status = #{status} WHERE oid = #{oid} ")
    void updateRangeStatus(@Param("oid")String oid,@Param("status")int status);

    @Select("SELECT " +
            "oid,data_mode AS dataMode, range_id AS rangeId, id, label, description, ctl1, ctl2, ctl3, status, " +
            "create_at AS createAt, update_at AS updateAt, delete_at AS deleteAt, init_flag AS initFlag  " +
            "FROM kg_sys_range WHERE oid = #{rangeOID} AND delete_at IS NULL ")
    KgRange getRangeByOID(@Param("rangeOID")String rangeOID);

    @Select("<script>" +
            "SELECT " +
            "oid, data_mode AS dataMode, range_id AS rangeId, id, label, description, ctl1, ctl2, ctl3, status, " +
            "init_flag AS initFlag, create_at AS createAt, update_at AS updateAt, delete_at AS deleteAt  " +
            "FROM kg_sys_range " +
            "WHERE delete_at IS NULL " +
            "<if test = 'condition!=null and condition!=\"\" '>" +
            "AND ( range_id LIKE #{condition} OR id LIKE #{condition} OR label LIKE #{condition} " +
            "OR description LIKE #{condition} OR ctl1 LIKE #{condition} OR ctl2 LIKE #{condition} " +
            "OR ctl3 LIKE #{condition} ) " +
            "</if>" +
            "<if test = 'dataMode!=null and dataMode!=\"\" '>" +
            "AND data_mode = #{dataMode} " +
            "</if>" +
            "<if test = 'rangeID!=null and rangeID!=\"\" '>" +
            "AND range_id = #{rangeID} " +
            "</if>" +
            "ORDER BY data_mode, range_id, id " +
            "</script>")
    List<KgRange> getRangeList(@Param("condition")String condition,@Param("dataMode") String dataMode,@Param("rangeID") String rangeID);

    @Select("SELECT " +
            "oid,data_mode AS dataMode, range_id AS rangeId, id, label, description, ctl1, ctl2, ctl3, status, " +
            "create_at AS createAt, update_at AS updateAt, delete_at AS deleteAt  " +
            "FROM kg_sys_range WHERE range_id = #{rangeId} AND delete_at IS NULL " +
            "ORDER BY ${sortBy} ")
    List<KgRange> getRangeListByRangeId(@Param("rangeId")String rangeId ,@Param("sortBy")String sortBy);

    @Select("<script>" +
            "SELECT " +
            "oid, data_mode AS dataMode, range_id AS rangeId, id, label, description, ctl1, ctl2, ctl3, status, " +
            "init_flag AS initFlag, create_at AS createAt, update_at AS updateAt, delete_at AS deleteAt  " +
            "FROM kg_sys_range " +
            "WHERE delete_at IS NULL AND oid not in (select range_oid from kg_sys_role_range where role_oid = #{roleOID})  " +
            "<if test = 'condition!=null and condition!=\"\" '>" +
            "AND ( range_id LIKE #{condition} OR id LIKE #{condition} OR label LIKE #{condition} " +
            "OR description LIKE #{condition} OR ctl1 LIKE #{condition} OR ctl2 LIKE #{condition} " +
            "OR ctl3 LIKE #{condition} ) " +
            "</if>" +
            "ORDER BY data_mode, range_id, id " +
            "</script>")
    List<KgRange> getRangeListNotByRoleOID(@Param("condition")String condition,@Param("roleOID") String roleOID);

    @Select("SELECT DISTINCT range_id FROM kg_sys_range WHERE delete_at IS NULL ORDER BY range_id")
    List<String> getRangeIds();

    @Select("SELECT DISTINCT range_id FROM kg_sys_range WHERE data_mode = #{dataMode} " +
            "AND status = 1 AND delete_at IS NULL ORDER BY range_id")
    List<String> getRangeIdsByDataMode(@Param("dataMode")String dataMode);

    @Select("SELECT DISTINCT data_mode FROM kg_sys_range WHERE status = 1 AND delete_at IS NULL ORDER BY data_mode")
    List<String> getRangeDataModes();
}