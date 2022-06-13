package com.kigooo.kgs.service.kgService.kgImpl;

/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.util.List;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kigooo.kgs.component.kgResponseException.KgResponseException;
import com.kigooo.kgs.component.kgResponseJson.KgResponseJson;
import com.kigooo.kgs.config.KgProperties;
import com.kigooo.kgs.dao.kgDao.KgRangeDao;
import com.kigooo.kgs.domain.kgDomain.KgRange;
import com.kigooo.kgs.service.kgService.KgRangeService;
import com.kigooo.kgs.util.KgExcelUtil;
import com.kigooo.kgs.util.KgUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class KgRangeImpl implements KgRangeService {

    @Autowired
    private KgRangeDao kgRangeDao;
    @Autowired
    private KgProperties kgProperties;
    @Autowired
    private Environment env;
    
    private KgResponseJson kgResponseJsonDefault = new KgResponseJson();

    /**
     * 创建选项值
     * @param kgRange 选项值
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2022/1/26 15:00
     */
    @Override
    public KgResponseJson createRange(KgRange kgRange){
        kgRange.setStatus(1).setCreateAt(System.currentTimeMillis());
        kgRangeDao.createRange(kgRange);
        return kgResponseJsonDefault;
    }

    /**
     * 删除选项值
     * @param rangeOIDList 选项值OID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2022/1/26 15:08
     */
    @Override
    public KgResponseJson deleteRange(List<String> rangeOIDList){
        for(String rangeOID : rangeOIDList){
            if(!checkRangeExist(rangeOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
                throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
            }
        }
        kgRangeDao.deleteRange(rangeOIDList,System.currentTimeMillis());
        return kgResponseJsonDefault;
    }

    /**
     * 修改选项值
     * @param kgRange 选项值
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2022/1/26 15:01
     */
    @Override
    public KgResponseJson updateRange(KgRange kgRange){
        if(!checkRangeExist(kgRange.getOid(), Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        kgRange.setUpdateAt(System.currentTimeMillis());
        kgRangeDao.updateRange(kgRange);
        return kgResponseJsonDefault;
    }

    /**
     * 修改选项值状态
     * @param rangeOID 选项值主键
     * @param status 选项值状态
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2022/1/26 15:02
     */
    @Override
    public KgResponseJson updateRangeStatus(String rangeOID,int status){
        if(!checkRangeExist(rangeOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        kgRangeDao.updateRangeStatus(rangeOID,status);
        return kgResponseJsonDefault;
    }

    /**
     * 检索选项值
     * @param page 页码
     * @param pageSize 每页记录数
     * @param condition 条件
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2022/1/26 15:30
     */
    @Override
    public KgResponseJson getRangeList(int page,int pageSize,String condition,String dataMode,String rangeID){
        KgResponseJson kgResponseJson = new KgResponseJson();
        PageHelper.startPage(page,pageSize);
        condition = (KgUtil.isNotEmpty(condition))?("%"+condition+"%"):(null);
        List<KgRange> rangeList = kgRangeDao.getRangeList(condition,dataMode,rangeID);
        kgResponseJson.putListData(rangeList);
        PageInfo<KgRange> pageInfo = new PageInfo<>(rangeList);
        kgResponseJson.putPagingData(page,pageSize,pageInfo.getPages(), (int)pageInfo.getTotal());
        return kgResponseJson;
    }

    /**
     * 获取选项值
     * @param rangeId 选项值ID
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2022/1/26 15:02
     */
    @Override
    public KgResponseJson getRangeListByRangeId(String rangeId,String sortBy){
        KgResponseJson kgResponseJson = new KgResponseJson();
        sortBy = sortBy.equals("id")?"id":"label";
        List<KgRange> kgRangeList = kgRangeDao.getRangeListByRangeId(rangeId,sortBy);
        kgResponseJson.putListData(kgRangeList);
        return kgResponseJson;
    }

    /**
     * 检索指定角色未关联的参数
     * @param page 页码
     * @param pageSize 每页记录数
     * @param condition 条件
     * @param roleOID 角色OID
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2022/5/13 14:02
     */
    @Override
    public KgResponseJson getRangeListNotByRoleOID(int page,int pageSize,String condition,String roleOID){
        KgResponseJson kgResponseJson = new KgResponseJson();
        PageHelper.startPage(page,pageSize);
        condition = (KgUtil.isNotEmpty(condition))?("%"+condition+"%"):(null);
        List<KgRange> rangeList = kgRangeDao.getRangeListNotByRoleOID(condition,roleOID);
        kgResponseJson.putListData(rangeList);
        PageInfo<KgRange> pageInfo = new PageInfo<>(rangeList);
        kgResponseJson.putPagingData(page,pageSize,pageInfo.getPages(), (int)pageInfo.getTotal());
        return kgResponseJson;
    }

    /**
     * 获取RangeID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2022/1/27 14:51
     */
    @Override
    public KgResponseJson getRangeIds(){
        KgResponseJson kgResponseJson = new KgResponseJson();
        List<String> rangeIdList = kgRangeDao.getRangeIds();
        kgResponseJson.putListData(rangeIdList);
        return kgResponseJson;
    }

    @Override
    public KgResponseJson getRangeIdsByDataMode(String dataMode){
        KgResponseJson kgResponseJson = new KgResponseJson();
        List<String> rangeIdList = kgRangeDao.getRangeIdsByDataMode(dataMode);
        kgResponseJson.putListData(rangeIdList);
        return kgResponseJson;
    }

    @Override
    public KgResponseJson getRangeDataModes(){
        KgResponseJson kgResponseJson = new KgResponseJson();
        List<String> dataModeList = kgRangeDao.getRangeDataModes();
        kgResponseJson.putListData(dataModeList);
        return kgResponseJson;
    }

    private Boolean checkRangeExist(String oid,int init){
        KgRange kgRange = kgRangeDao.getRangeByOID(oid);
        if(KgUtil.isNotEmpty(kgRange) && kgRange.getInitFlag()<=init){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public ResponseEntity exportRangeList(String condition,String dataMode,String rangeID){
        condition = (KgUtil.isNotEmpty(condition))?("%"+condition+"%"):(null);
        List<KgRange> rangeList = kgRangeDao.getRangeList(condition,dataMode,rangeID);
        String [] [] attributes = {{"dataMode","dataMode"},{"rangeId","rangeId"},{"id","id"},{"label","label"},{"description","description"},{"ctl1","ctl1"},{"ctl2","ctl2"},{"ctl3","ctl3"},{"status","status"},{"initFlag","initFlag"}};
        return KgExcelUtil.exportToExcelWithDomainList(attributes,rangeList);
    }
}