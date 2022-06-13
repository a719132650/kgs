package com.kigooo.kgs.component.kgResponseJson;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KgResponseJson implements Serializable {
    private static final long serialVersionUID = 1L;
    private String code = "0";
    private String msg = "Success";
    private Map<String,Object> data = new HashMap<>();

    class Paging{
        private int page;
        private int pageSize;
        private int pageTotal;
        private int total;

        public Paging(int page, int pageSize, int pageTotal, int total) {
            this.page = page;
            this.pageSize = pageSize;
            this.pageTotal = pageTotal;
            this.total = total;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getPageTotal() {
            return pageTotal;
        }

        public void setPageTotal(int pageTotal) {
            this.pageTotal = pageTotal;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }

    public KgResponseJson() {}

    public KgResponseJson(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public KgResponseJson(Map<String, Object> data) {
        this.data = data;
    }

    public KgResponseJson(String code, String msg, Map<String, Object> data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    /**
     * 放入键值对类型数据
     * @param key 键
     * @param value 值
     * @return void
     * @author
     * @date 2021/8/30 15:18
     */
    public void putData(String key,Object value){
        this.data.put(key,value);
    }

    /**
     * 放入List集合类型数据
     * @param list 数据集合
     * @return void
     * @author
     * @date 2021/8/30 15:18
     */
    public void putListData(List<?> list){
        putData("list",list);
    }

    /**
     * 放入页码信息
     * @param page 当前页
     * @param pageSize 每页数量
     * @param pageTotal 总页数
     * @param total 总记录数
     * @return void
     * @author
     * @date 2021/8/30 15:19
     */
    public void putPagingData(int page,int pageSize,int pageTotal,int total){
        putData("paging",new Paging(page,pageSize,pageTotal,total));
    }

    /**
     * 放入对象类型数据 对象属性转为key value格式
     * @param object 
     * @return void
     * @author
     * @date 2021/8/30 15:21
     */
    public void putObjectData(Object object){
        Field[] fields=object.getClass().getDeclaredFields();
        for(int i=0;i<fields.length;i++){
            String fieldName = fields[i].getName();
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            Character c = fieldName.charAt(1);
            if(Character.isUpperCase(c)){
                firstLetter = firstLetter.toLowerCase();
            }
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = null;
            try {
                method = object.getClass().getMethod(getter, new Class[] {});
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            Object value = null;
            try {
                value = method.invoke(object, new Object[] {});
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            this.putData(fieldName,value);
        }
    }
}
