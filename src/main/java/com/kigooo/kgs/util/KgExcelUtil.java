package com.kigooo.kgs.util;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class KgExcelUtil {

    private static final Logger logger = LoggerFactory.getLogger("APP_LOG");

    /**
     * 
     * @param title
     * @param tableContent 
     * @return org.springframework.http.ResponseEntity<byte[]>
     * @author
     * @date 2022/3/18 16:44
     */
    public static ResponseEntity<byte[]> exportToExcelWithList(List<String> title, List<List<String>> tableContent) {
        try{
            SXSSFWorkbook workbook = new SXSSFWorkbook();
            Sheet sheet = workbook.createSheet("sheet");
            Row headerRow = sheet.createRow(0);
            for(int i = 0; i< title.size(); i++){
                Cell cell0 = headerRow.createCell(i);
                cell0.setCellValue(title.get(i));
            }
            for(int i = 0; i < tableContent.size(); i++){
                List<String> fieldList = tableContent.get(i);
                Row row = sheet.createRow(i + 1);
                for(int j = 0 ; j < fieldList.size(); j++){
                    row.createCell(j).setCellValue( fieldList.get(j));
                }
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            workbook.write(byteArrayOutputStream);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment",
                    new String("export.xlsx".getBytes("UTF-8"), "iso-8859-1"));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.CREATED);
        }catch (Exception e){
            logger.error(e.getMessage());
            return null;
        }
    }

    /**
     *
     * @param attributes
     * @param domainList 
     * @return org.springframework.http.ResponseEntity<byte[]>
     * @author
     * @date 2022/3/18 16:44
     */
    public static ResponseEntity<byte[]> exportToExcelWithDomainList(String [] [] attributes,List<?> domainList) {
        List<List<String>> tableContent = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        for(String [] attribute : attributes){
            try{
                titles.add(attribute[1]);
            }catch(Exception e){
                titles.add("");
            }
        }
        for(Object o : domainList){
            List<String> valueList = new ArrayList<>();
            for(String [] attribute : attributes){
                char[] cs=attribute[0].toCharArray();
                if(cs.length>1){
                    char sc = cs[1];
                    if((int)sc<65 || (int)sc>90){
                        cs[0]-=32;
                    }
                }else{
                    cs[0]-=32;
                }
                String funName =  "get"+String.valueOf(cs);
                String attrValue = "";
                try{
                    Method m = o.getClass().getMethod(funName);
                    attrValue = m.invoke(o).toString();
                }catch(Exception e){}
                valueList.add(attrValue);
            }
            tableContent.add(valueList);
        }
        return exportToExcelWithList(titles,tableContent);
    }


}
