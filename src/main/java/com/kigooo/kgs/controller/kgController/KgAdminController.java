package com.kigooo.kgs.controller.kgController;

import java.util.ArrayList;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.kigooo.kgs.component.kgResponseJson.KgResponseJson;
import com.kigooo.kgs.domain.kgDomain.*;
import com.kigooo.kgs.service.kgService.*;
import com.kigooo.kgs.util.KgUtil;

@Controller
@RequestMapping("/admin")
@Validated
public class KgAdminController {

    @Autowired
    private KgUserService kgUserService;

    @Autowired
    private KgRoleService kgRoleService;

    @Autowired
    private KgApiService kgApiService;	

    @Autowired
    private KgMenuService kgMenuService;

    @Autowired
    private KgRelService kgRelService;

    @Autowired
    private KgRangeService kgRangeService;

    // user 模块
    // user220001
    @RequestMapping("/createUser")
    @ResponseBody
    public KgResponseJson createUser(@Valid @NotBlank String uAccount,@Valid @NotBlank String uPassword,@Valid @NotBlank String uName,String phone,@Valid @NotBlank String sex,@RequestParam(value = "roleOIDList",required = false)List<String> roleOIDList){
        if(roleOIDList == null){roleOIDList = new ArrayList<>();}
        KgUser kgUser = new KgUser()
                .setuAccount(uAccount)
                .setuPassword(uPassword)
                .setuName(uName)
                .setPhone(phone)
                .setSex(sex);
        return kgUserService.createUser(kgUser,roleOIDList);
    }

    // user220002
    @RequestMapping("/deleteUser")
    @ResponseBody
    public KgResponseJson deleteUser(@Valid @NotBlank String userOID) {
        return kgUserService.deleteUser(userOID);
    }

    // user220003
    @RequestMapping("/updateUser")
    @ResponseBody
    public KgResponseJson updateUser(String userOID,String uName,String phone,String sex,@RequestParam(value = "roleOIDList",required = false) List<String> roleOIDList) {
        if(roleOIDList == null){roleOIDList = new ArrayList<>();}
        KgUser kgUser = new KgUser().setuName(uName).setPhone(phone).setSex(sex);
        kgUser.setOid(userOID);
        return kgUserService.updateUser(kgUser,roleOIDList);
    }

    // user220004
    @RequestMapping("/updateUserStatus")
    @ResponseBody
    public KgResponseJson updateUserStatus(@Valid @NotBlank String userOID,@Valid @Pattern(regexp = "0|1") String status) {
        return kgUserService.updateUserStatus(userOID, KgUtil.toInt(status));
    }

    // user220005
    @RequestMapping("/updateUserPassword")
    @ResponseBody
    public KgResponseJson updateUserPassword(@Valid @NotBlank String userOID, @Valid @NotBlank @Size(min = 6,max = 20) String password) {
        return kgUserService.updateUserPassword(userOID, password);
    }

	// user220006
    @RequestMapping("/getUserList")
    @ResponseBody
    public KgResponseJson getUserList(@Valid @Positive String page,@Valid @Positive String pageSize, String condition, String init) {
        int initValue = 0;
        if(KgUtil.isNotEmpty(init) && init.equals("1")){
            initValue = 1;
        }
        if(KgUtil.isNotEmpty(init) && init.equals("2")){
            initValue = 2;
        }
        return kgUserService.getUserList(KgUtil.toInt(page),KgUtil.toInt(pageSize), condition, initValue);
    }

    // user220007
    @RequestMapping("/getUserListNotByRoleOID")
    @ResponseBody
    public KgResponseJson getUserListNotByRoleOID(@Valid @Positive String page,@Valid @Positive String pageSize,@Valid @NotBlank String roleOID, String condition, String init) {
        int initValue = 0;
        if(KgUtil.isNotEmpty(init) && init.equals("1")){
            initValue = 1;
        }
        if(KgUtil.isNotEmpty(init) && init.equals("2")){
            initValue = 2;
        }
        return kgUserService.getUserListNotByRoleOID(KgUtil.toInt(page), KgUtil.toInt(pageSize), roleOID, condition, initValue);
    }

    // user220008
    @RequestMapping("/getUserDetail")
    @ResponseBody
    public KgResponseJson getUserDetail(@Valid @NotBlank String userOID) {
        return kgUserService.getUserDetail(userOID);
    }

    // user220009
    @RequestMapping("/exportUserList")
    @ResponseBody
    public ResponseEntity exportUserList(String condition){
        return kgUserService.exportUserList(condition);
    }

    // Role 模块
    // role220001
    @RequestMapping("/createRole")
    @ResponseBody
    public KgResponseJson createRole(@Valid @NotBlank String name, String description,@RequestParam(value = "menuOIDList",required = false) List<String> menuOIDList, @RequestParam(value = "operateOIDList",required = false) List<String> operateOIDList, @RequestParam(value = "userOIDList",required = false) List<String>userOIDList){
        if(menuOIDList == null){menuOIDList = new ArrayList<>();}
        if(operateOIDList == null){operateOIDList = new ArrayList<>();}
        if(userOIDList == null){userOIDList = new ArrayList<>();}
        return kgRoleService.createRole(name, description, menuOIDList, operateOIDList, userOIDList);
    }

    // role220002
    @RequestMapping("/deleteRole")
    @ResponseBody
    public KgResponseJson deleteRole(@Valid @NotBlank String roleOID) {
        return kgRoleService.deleteRole(roleOID);
    }

    // role220003
    @RequestMapping("/updateRole")
    @ResponseBody
    public KgResponseJson updateRole(String oid,String name, String description,@RequestParam(value = "commandOIDList",required = false)List<String> commandOIDList,@RequestParam(value = "operateOIDList",required = false)List<String> operateOIDList) {
        if(commandOIDList == null){commandOIDList = new ArrayList<>();}
        if(operateOIDList == null){operateOIDList = new ArrayList<>();}
        KgRole kgRole = new KgRole().setName(name).setDescription(description);
        kgRole.setOid(oid);
        return kgRoleService.updateRole(kgRole, commandOIDList, operateOIDList);
    }

    // role220004
    @RequestMapping("/updateRoleStatus")
    @ResponseBody
    public KgResponseJson updateRoleStatus(@Valid @NotBlank String roleOID,@Valid @Pattern(regexp = "0|1") String status) {
        return kgRoleService.updateRoleStatus(roleOID, KgUtil.toInt(status));
    }

    // role220005
    @RequestMapping("/getRoleDetail")
    @ResponseBody
    public KgResponseJson getRoleDetail(@Valid @NotBlank String roleOID){
        return kgRoleService.getRoleDetail(roleOID);
    }

    // role220006
    @RequestMapping("/getRoleList")
    @ResponseBody
    public KgResponseJson getRoleList(@Valid @Positive String page,@Valid @Positive String pageSize,String condition, String init){
        int initValue = 0;
        if(KgUtil.isNotEmpty(init) && init.equals("1")){
            initValue = 1;
        }
        if(KgUtil.isNotEmpty(init) && init.equals("2")){
            initValue = 2;
        }
        return kgRoleService.getRoleList(KgUtil.toInt(page),KgUtil.toInt(pageSize), condition, initValue);
    }

    // role220007
    @RequestMapping("/exportRoleList")
    @ResponseBody
    public ResponseEntity exportRoleList(String condition){
        return kgRoleService.exportRoleList(condition);
    }

    // Api 模块
    // api220001
    @RequestMapping("/createApi")
    @ResponseBody
    public KgResponseJson createApi(String api,String name,String description,String businessType,@Valid @Pattern(regexp = "0|1|2") String authType,@RequestParam(value = "commandOIDList",required = false) List<String> commandOIDList,@RequestParam(value = "operateOIDList",required = false) List<String> operateOIDList) {
        if(commandOIDList == null){commandOIDList = new ArrayList<>();}
        if(operateOIDList == null){operateOIDList = new ArrayList<>();}
        KgApi kgApi = new KgApi().setApi(api).setName(name).setDescription(description).setBusinessType(businessType).setAuthType(KgUtil.toInt(authType));
        return kgApiService.createApi(kgApi,commandOIDList,operateOIDList);
    }

    // api220002
    @RequestMapping("/deleteApi")
    @ResponseBody
    public KgResponseJson deleteApi(@Valid @NotBlank String apiOID) {
        return kgApiService.deleteApi(apiOID);
    }

    // api220003
    @RequestMapping("/updateApi")
    @ResponseBody
    public KgResponseJson updateApi(String oid,String api,String name,String description,String businessType,@Valid @Pattern(regexp = "0|1|2") String authType,@RequestParam(value = "commandOIDList",required = false) List<String> commandOIDList,@RequestParam(value = "operateOIDList",required = false) List<String> operateOIDList) {
        if(commandOIDList == null){commandOIDList = new ArrayList<>();}
        if(operateOIDList == null){operateOIDList = new ArrayList<>();}
        KgApi kgApi = new KgApi().setApi(api).setName(name).setDescription(description).setBusinessType(businessType).setAuthType(KgUtil.toInt(authType));
        kgApi.setOid(oid);
        return kgApiService.updateApi(kgApi,commandOIDList,operateOIDList);
    }

    // api220004
    @RequestMapping("/updateApiStatus")
    @ResponseBody
    public KgResponseJson updateApiStatus(@Valid @NotEmpty @RequestParam("apiOIDList") List<String> apiOIDList,@Valid @Pattern(regexp = "0|1") String status) {
        return kgApiService.updateApiStatus(apiOIDList, KgUtil.toInt(status));
    }

    // api220005
    @RequestMapping("/getApiDetail")
    @ResponseBody
    public KgResponseJson getApiDetail(@Valid @NotBlank String apiOID){
        return kgApiService.getApiDetail(apiOID);
    }

    // api220006
    @RequestMapping("/getApiList")
    @ResponseBody
    public KgResponseJson getApiList(@Valid @Positive String page,@Valid @Positive String pageSize, String condition,@RequestParam(value = "businessTypeList",required = false)List<String> businessTypeList,String init) {
        int initValue = 0;
        if(KgUtil.isNotEmpty(init) && init.equals("1")){
            initValue = 1;
        }
        else if(KgUtil.isNotEmpty(init) && init.equals("2")){
            initValue = 2;
        }
        if(businessTypeList == null){businessTypeList = new ArrayList<>();}
        return kgApiService.getApiList(KgUtil.toInt(page),KgUtil.toInt(pageSize), condition, businessTypeList, initValue);
    }

    // api220007
    @RequestMapping("/exportApiList")
    @ResponseBody
    public ResponseEntity exportApiList(String condition, @RequestParam(value = "businessTypeList",required = false)List<String> businessTypeList){
        return kgApiService.exportApiList(condition,businessTypeList);
    }
	
    // Menu SubMenu模块
    // submenu220001
    @RequestMapping("/createSubMenu")
    @ResponseBody
    public KgResponseJson createSubMenu(String name) {
        KgSubMenu kgSubMenu = new KgSubMenu().setName(name);
        return kgMenuService.createSubMenu(kgSubMenu);
    }

    // submenu220002
    @RequestMapping("/deleteSubMenu")
    @ResponseBody
    public KgResponseJson deleteSubMenu(@Valid @NotBlank String subMenuOID) {
        return kgMenuService.deleteSubMenu(subMenuOID);
    }

    // submenu220003
    @RequestMapping("/updateSubMenu")
    @ResponseBody
    public KgResponseJson updateSubMenu(String oid,String name) {
        KgSubMenu kgSubMenu = new KgSubMenu().setName(name);
        kgSubMenu.setOid(oid);
        return kgMenuService.updateSubMenu(kgSubMenu);
    }

    // submenu220004
    @RequestMapping("/updateSubMenuStatus")
    @ResponseBody
    public KgResponseJson updateSubMenuStatus(@Valid @NotEmpty @RequestParam("subMenuOIDList") List<String> subMenuOIDList,@Valid @Pattern(regexp = "0|1") String status) {
        return kgMenuService.updateSubMenuStatus(subMenuOIDList, KgUtil.toInt(status));
    }
	
    // submenu220005
    @RequestMapping("/updateSubMenuSort")
    @ResponseBody
    public KgResponseJson updateSubMenuSort(@Valid @NotBlank String subMenuOID,@Valid @Positive String sort){
        return kgMenuService.updateSubMenuSort(subMenuOID,KgUtil.toInt(sort));
    }	

    // Menu Menu 模块
    // menu220001
    @RequestMapping("/createMenu")
    @ResponseBody
    public KgResponseJson createMenu(String name,String subMenuOID) {
        KgMenu kgMenu = new KgMenu().setName(name).setSubMenuOid(subMenuOID);
        return kgMenuService.createMenu(kgMenu);
    }

    // menu220002
    @RequestMapping("/deleteMenu")
    @ResponseBody
    public KgResponseJson deleteMenu(@Valid @NotBlank String menuOID) {
        return kgMenuService.deleteMenu(menuOID);
    }

    // menu220003
    @RequestMapping("/updateMenu")
    @ResponseBody
    public KgResponseJson updateMenu(String oid,String name) {
        KgMenu kgMenu = new KgMenu().setName(name);
        kgMenu.setOid(oid);
        return kgMenuService.updateMenu(kgMenu);
    }

    // menu220004
    @RequestMapping("/updateMenuStatus")
    @ResponseBody
    public KgResponseJson updateMenuStatus(@Valid @NotEmpty @RequestParam("menuOIDList") List<String> menuOIDList,@Valid @Pattern(regexp = "0|1") String status) {
        return kgMenuService.updateMenuStatus(menuOIDList, KgUtil.toInt(status));
    }

    // menu220005
    @RequestMapping("/updateMenuSort")
    @ResponseBody
    public KgResponseJson updateMenuSort(String oid,@Valid @Positive String sort,String subMenuOID){
        KgMenu kgMenu = new KgMenu().setSort(KgUtil.toInt(sort)).setSubMenuOid(subMenuOID);
        kgMenu.setOid(oid);
        return kgMenuService.updateMenuSort(kgMenu);
    }

	// Menu Command 模块
    // command220001
    @RequestMapping("/createCommand")
    @ResponseBody
    public KgResponseJson createCommand(String name,String url,String menuOID, @RequestParam(value="roleOIDList",required = false) List<String> roleOIDList,@RequestParam(value = "apiOIDList",required = false)List<String> apiOIDList){
        if(roleOIDList == null){roleOIDList = new ArrayList<>();}
        if(apiOIDList == null){apiOIDList = new ArrayList<>();}
        KgCommand kgCommand = new KgCommand().setName(name).setUrl(url).setMenuOid(menuOID);
        return kgMenuService.createCommand(kgCommand,roleOIDList,apiOIDList);
    }

    // command220002
    @RequestMapping("/deleteCommand")
    @ResponseBody
    public KgResponseJson deleteCommand(@Valid @NotBlank String commandOID) {
        return kgMenuService.deleteCommand(commandOID);
    }

    // command220003
    @RequestMapping("/updateCommand")
    @ResponseBody
    public KgResponseJson updateCommand(String oid,String name,String url, @RequestParam(value = "roleOIDList",required = false) List<String>roleOIDList,@RequestParam(value = "apiOIDList",required = false)List<String> apiOIDList){
        if(roleOIDList == null){roleOIDList = new ArrayList<>();}
        if(apiOIDList == null){apiOIDList = new ArrayList<>();}
        KgCommand kgCommand = new KgCommand().setName(name).setUrl(url);
        kgCommand.setOid(oid);
        return kgMenuService.updateCommand(kgCommand,roleOIDList,apiOIDList);
    }

    // command220004
    @RequestMapping("/updateCommandStatus")
    @ResponseBody
    public KgResponseJson updateCommandStatus(@Valid @NotEmpty @RequestParam("commandOIDList")List<String> commandOIDList,@Valid @Pattern(regexp = "0|1") String status) {
        return kgMenuService.updateCommandStatus(commandOIDList,KgUtil.toInt(status));
    }

    // command220005
    @RequestMapping("/updateCommandSort")
    @ResponseBody
    public KgResponseJson updateCommandSort(String oid,@Valid @Positive String sort,String menuOID){
        KgCommand kgCommand = new KgCommand().setSort(KgUtil.toInt(sort)).setMenuOid(menuOID);
        kgCommand.setOid(oid);
        return kgMenuService.updateCommandSort(kgCommand);
    }

    // command220006
    @RequestMapping("/getCommandDetail")
    @ResponseBody
    public KgResponseJson getCommandDetail(@Valid @NotBlank String commandOID){
        return kgMenuService.getCommandDetail(commandOID);
    }

    // Menu Operate 模块
    // operate220001
    @RequestMapping("/createOperate")
    @ResponseBody
    public KgResponseJson createOperate(String name,String commandOID,String apiOID,String description,@RequestParam(value="roleOIDList",required = false) List<String> roleOIDList){
        if(roleOIDList == null){roleOIDList = new ArrayList<>();}
        KgOperate kgOperate = new KgOperate().setName(name).setCommandOid(commandOID).setApiOid(apiOID).setDescription(description);
        return kgMenuService.createOperate(kgOperate,roleOIDList);
    }

    // operate220002
    @RequestMapping("/deleteOperate")
    @ResponseBody
    public KgResponseJson deleteOperate(@Valid @NotBlank String operateOID) {
        return kgMenuService.deleteOperate(operateOID);
    }

    // operate220003
    @RequestMapping("/updateOperate")
    @ResponseBody
    public KgResponseJson updateOperate(String oid,String name,String apiOID,String description,@RequestParam(value = "roleOIDList",required = false) List<String> roleOIDList){
        if(roleOIDList == null){roleOIDList = new ArrayList<>();}
        KgOperate kgOperate = new KgOperate().setName(name).setApiOid(apiOID).setDescription(description);
        kgOperate.setOid(oid);
        return kgMenuService.updateOperate(kgOperate,roleOIDList);
    }

    // operate220004
    @RequestMapping("/updateOperateStatus")
    @ResponseBody
    public KgResponseJson updateOperateStatus(@Valid @NotEmpty @RequestParam("operateOIDList")List<String> operateOIDList,@Valid @Pattern(regexp = "0|1") String status) {
        return kgMenuService.updateOperateStatus(operateOIDList, KgUtil.toInt(status));
    }

    // operate220005
    @RequestMapping("/getOperateDetail")
    @ResponseBody
    public KgResponseJson getOperateDetail(@Valid @NotBlank String operateOID){
        return kgMenuService.getOperateDetail(operateOID);
    }

    // Menu MenuTree 模块
    // menutree220001
    @RequestMapping("/getSubMenuTreeAll")
    @ResponseBody
    public KgResponseJson getSubMenuTreeAll(String init){
        int initValue = 0;
        if(KgUtil.isNotEmpty(init) && init.equals("1")){
            initValue = 1;
        }
        else if(KgUtil.isNotEmpty(init) && init.equals("2")){
            initValue = 2;
        }
        return kgMenuService.getSubMenuTreeAll(initValue);
    }

    // Rel 模块
    // rel220001
    @RequestMapping("/createUsers2Role")
    @ResponseBody
    public KgResponseJson createUsers2Role(@Valid @NotBlank String roleOID,@Valid @NotEmpty @RequestParam(value = "userOIDList") List<String> userOIDList) {
        return kgRelService.createUsers2Role(userOIDList, roleOID);
    }

    // rel220002
    @RequestMapping("/deleteUser2Role")
    @ResponseBody
    public KgResponseJson deleteUser2Role(@Valid @NotEmpty @RequestParam(value = "ridList") List<String> ridList) {
        return kgRelService.deleteUser2Role(ridList);
    }

    // rel220003
    @RequestMapping("/getUser2RoleByUserOID")
    @ResponseBody
    public KgResponseJson getUser2RoleByUserOIDForReturn(@Valid @NotBlank String userOID) {
        return kgRelService.getUser2RoleByUserOIDForReturn(userOID);
    }

    // rel220004
    @RequestMapping("/getUser2RoleByRoleOID")
    @ResponseBody
    public KgResponseJson getUser2RoleByRoleOIDForReturn(@Valid @NotBlank String roleOID,@Valid @Positive String page,@Valid @Positive String pageSize,String condition){
        return kgRelService.getUser2RoleByRoleOIDForReturn(roleOID, KgUtil.toInt(page),KgUtil.toInt(pageSize), condition);
    }

    // rel220005
    @RequestMapping("/createRole2Ranges")
    @ResponseBody
    public KgResponseJson createRole2Ranges(@Valid @NotBlank String roleOID,@Valid @NotEmpty @RequestParam(value = "rangeOIDList") List<String> rangeOIDList) {
        return kgRelService.createRole2Ranges(roleOID, rangeOIDList);
    }

    // rel220006
    @RequestMapping("/deleteRole2Range")
    @ResponseBody
    public KgResponseJson deleteRole2Range(@Valid @NotEmpty @RequestParam(value = "ridList") List<String> ridList) {
        return kgRelService.deleteRole2Range(ridList);
    }

    // rel220007
    @RequestMapping("/getRole2RangeByRoleOID")
    @ResponseBody
    public KgResponseJson getRole2RangeByRoleOID(@Valid @NotBlank String roleOID) {
        return kgRelService.getRole2RangeByRoleOID(roleOID);
    }

    // Range 模块
    // range220001
    @RequestMapping("/createRange")
    @ResponseBody
    public KgResponseJson createRange(@Valid @NotBlank String dataMode,@Valid @NotBlank String rangeId,@Valid @NotBlank String id,@Valid @NotBlank String label,String description){
        KgRange kgRange = new KgRange().setDataMode(dataMode).setRangeId(rangeId).setId(id).setLabel(label).setDescription(description);
        return kgRangeService.createRange(kgRange);
    }

    // range220002
    @RequestMapping("/deleteRange")
    @ResponseBody
    public KgResponseJson deleteRange(@Valid @NotEmpty @RequestParam("oidList")List<String> oidList){
        return kgRangeService.deleteRange(oidList);
    }

    // range220003
    @RequestMapping("/updateRange")
    @ResponseBody
    public KgResponseJson updateRange(@Valid @NotBlank String oid,@Valid @NotBlank String dataMode,@Valid @NotBlank String rangeId,@Valid @NotBlank String id,@Valid @NotBlank String label,String description){
        KgRange kgRange = new KgRange().setDataMode(dataMode).setRangeId(rangeId).setId(id).setLabel(label).setDescription(description);
        kgRange.setOid(oid);
        return kgRangeService.updateRange(kgRange);
    }

    // range220004
    @RequestMapping("/updateRangeStatus")
    @ResponseBody
    public KgResponseJson updateRangeStatus(@Valid @NotBlank String oid,@Valid @Pattern(regexp = "0|1") String status){
        return kgRangeService.updateRangeStatus(oid,KgUtil.toInt(status));
    }

    // range220005
    @RequestMapping("/getRangeList")
    @ResponseBody
    public KgResponseJson getRangeList(@Valid @Positive String page,@Valid @Positive String pageSize,String condition,String dataMode,String rangeID){
        return kgRangeService.getRangeList(KgUtil.toInt(page),KgUtil.toInt(pageSize),condition,dataMode,rangeID);
    }

    // range220006
    @RequestMapping("/getRangeListByRangeId")
    @ResponseBody
    public KgResponseJson getRange(@Valid @NotBlank String rangeId,@Valid @Pattern(regexp = "id|label") String sortBy){
        return kgRangeService.getRangeListByRangeId(rangeId,sortBy);
    }

    // range220007
    @RequestMapping("/getRangeListNotByRoleOID")
    @ResponseBody
    public KgResponseJson getRangeListNotByRoleOID(@Valid @Positive String page,@Valid @Positive String pageSize,String condition,@Valid @NotBlank String roleOID){
        return kgRangeService.getRangeListNotByRoleOID(KgUtil.toInt(page),KgUtil.toInt(pageSize),condition,roleOID);
    }

    // range220008
    @RequestMapping("/getRangeIds")
    @ResponseBody
    public KgResponseJson getRangeIds(){
        return kgRangeService.getRangeIds();
    }

    // range220009
    @RequestMapping("/getRangeIdsByDataMode")
    @ResponseBody
    public KgResponseJson getRangeIdsByDataMode(String dataMode){
        return kgRangeService.getRangeIdsByDataMode(dataMode);
    }

    // range220010
    @RequestMapping("/getRangeDataModes")
    @ResponseBody
    public KgResponseJson getRangeDataModes(){
        return kgRangeService.getRangeDataModes();
    }

    // range220011
    @RequestMapping("/exportRangeList")
    @ResponseBody
    public ResponseEntity exportRangeList(String condition,String dataMode,String rangeID){
        return kgRangeService.exportRangeList(condition,dataMode,rangeID);
    }
}
