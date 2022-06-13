package com.kigooo.kgs.service.kgService;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import com.kigooo.kgs.component.kgResponseJson.KgResponseJson;
import com.kigooo.kgs.domain.kgDomain.KgUser;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Validated
public interface KgUserService {
    KgResponseJson createUser(KgUser kgUser, List<String> roleOIDList);
    KgResponseJson deleteUser(String userOID);
    KgResponseJson updateUser(KgUser kgUser,List<String> roleOIDList);
    KgResponseJson updateUserStatus(String userOID,int status);
    KgResponseJson updateUserPassword(String userOID,String password);
    KgResponseJson getUserDetail(String userOID);
    KgUser getUserByAccount(@Valid @NotBlank String account,@Valid @Positive int status);
    KgResponseJson getUserList(int page,int pageSize,String condition,int init);
    KgResponseJson getUserListNotByRoleOID(int page,int pageSize,String roleOID,String condition,int init);
    Boolean checkUserExist(String oid,int init);
    ResponseEntity exportUserList(String condition);
}
