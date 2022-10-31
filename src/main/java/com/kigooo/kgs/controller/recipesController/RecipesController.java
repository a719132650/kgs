package com.kigooo.kgs.controller.recipesController;

import com.kigooo.kgs.component.kgResponseJson.KgResponseJson;
import com.kigooo.kgs.service.recipesService.RecipesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@Controller
@RequestMapping("/recipes")
public class RecipesController {

    @Autowired
    private RecipesService recipesService;

    @RequestMapping("/uploadPic")
    @ResponseBody
    public KgResponseJson uploadPic(@Valid @NotEmpty @RequestParam("fileList") MultipartFile[] fileList, HttpServletRequest httpServletRequest) {
        return recipesService.uploadPic(fileList,httpServletRequest);
    }
}
