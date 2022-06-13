package com.kigooo.kgs.service.kgService;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import com.kigooo.kgs.component.kgResponseJson.KgResponseJson;
import com.kigooo.kgs.domain.kgDomain.KgRelationship;
import org.springframework.validation.annotation.Validated;

@Validated
public interface KgRelService {
    //user2role
    KgResponseJson createUsers2Role(List<String> userOIDList,String roleOID);
    void createUser2Roles(@Valid @NotBlank String userOID,@Valid @NotEmpty List<String> roleOIDList);
    KgResponseJson deleteUser2Role(List<String> ridList);
    void deleteUser2RoleByUserOID(@Valid @NotBlank String userOID);
    void deleteUser2RoleByRoleOID(@Valid @NotBlank String roleOID);
    KgResponseJson getUser2RoleByUserOIDForReturn(String userOID);
    List<KgRelationship> getUser2RoleByUserOID(@Valid @NotBlank String userOID);
    KgResponseJson getUser2RoleByRoleOIDForReturn(String roleOID,int page,int pageSize,String condition);

    //role2command
    void createRoles2Command(@Valid @NotEmpty List<String> roleOIDList,@Valid @NotBlank String commandOID);
    void createRole2Commands(@Valid @NotBlank String roleOID,@Valid @NotEmpty List<String> commandOIDList);
    void deleteRole2Command(@Valid @NotEmpty List<String> ridList);
    void deleteRole2CommandByRoleOID(@Valid @NotBlank String roleOID);
    void deleteRole2CommandByCommandOID(@Valid @NotBlank String commandOID);
    List<KgRelationship> getRole2CommandByRoleOID(@Valid @NotBlank String roleOID);
    List<KgRelationship> getRole2CommandByCommandOID(@Valid @NotBlank String commandOID);
    
    //role2operate
    void createRole2Operates(@Valid @NotBlank String roleOID,@Valid @NotEmpty List<String> operateOIDList);
    void createRoles2Operate(@Valid @NotEmpty List<String> roleOIDList,@Valid @NotBlank String operateOID);
    void deleteRole2Operate(@Valid @NotEmpty List<String> ridList);
    void deleteRole2OperateByRoleOID(@Valid @NotBlank String roleOID);
    List<KgRelationship> getRole2OperateByRoleOID(@Valid @NotBlank String roleOID);
    List<KgRelationship> getRole2OperateByOperateOID(@Valid @NotBlank String operateOID);

    //role2range
    KgResponseJson createRole2Ranges(String roleOID,List<String> rangeOIDList);
    KgResponseJson deleteRole2Range(List<String> ridList);
    KgResponseJson getRole2RangeByRoleOID(String roleOID);
    
    //command2api
    void createCommand2Apis(@Valid @NotBlank String commandOID,@Valid @NotEmpty List<String> apiOIDList);
    void createCommands2Api(@Valid @NotEmpty List<String> commandOIDList,@Valid @NotBlank String apiOID);
    void deleteCommand2Api(@Valid @NotEmpty List<String> ridList);
    void deleteCommand2ApiByCommandOID(@Valid @NotBlank String commandOID);
	List<KgRelationship> getCommand2ApiByCommandOID(@Valid @NotBlank String commandOID);
    List<KgRelationship> getCommand2ApiByApiOID(@Valid @NotBlank String apiOID);

    //role2api
    void createRoles2Api(@Valid @NotEmpty List<String> roleOIDList,@Valid @NotBlank String apiOID);
    void createRoles2Apis(@Valid @NotEmpty List<String> roleOIDList,@Valid @NotEmpty List<String> apiOIDList);
    void deleteRole2ApiByRoleOIDs(@Valid @NotEmpty List<String> roleOIDList);
    void deleteRole2ApiByApiOIDs(@Valid @NotEmpty List<String> apiOIDList);
    
    //reset
    void resetRole2Api();
    void resetRole2ApiByRoleOIDs(@Valid @NotEmpty List<String> roleOIDList);
    void resetRole2ApiByCommandOIDs(@Valid @NotEmpty List<String> commandOIDList);
    void resetRole2ApiByOperatesOIDs(@Valid @NotEmpty List<String> operateOIDList);
    void resetRole2ApiByApiOIDs(@Valid @NotEmpty List<String> apiOIDList);
    
    //other
    Map<String,List> getRelationshipDiff(List<KgRelationship> relationshipList, List<String> comparedOIDList, @Valid @Pattern(regexp = "from|to") String comparedDirect);
}
