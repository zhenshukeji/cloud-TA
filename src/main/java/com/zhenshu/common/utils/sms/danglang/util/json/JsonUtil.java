package com.zhenshu.common.utils.sms.danglang.util.json;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.JSONUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Json工具类
 */
public class JsonUtil {
    private static JsonConfig config = null;

    public static JsonConfig getJsonConfig() {
        if (config == null) {
            initProcessor();
        }
        return config;
    }

    /**
     * 初始化工作，使用JSON-LIB转换带有DATE类型的对象需要额外的一些设置
     *
     * @author 当郎
     */
    public static void initProcessor() {
        config = new JsonConfig();
        // 处理日期时返回 {"date":#,"day":#,"hours":#,"minutes":#,"month":#,"nanos":#,"seconds":#,"time":#,"timezoneOffset":#,"year":#} 格式
        // config.registerJsonBeanProcessor(java.sql.Date.class, new JsDateJsonBeanProcessor());
        // 处理日期时返回 yyyy-MM-dd HH:mm:ss 格式
        config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor());
        config.registerJsonValueProcessor(java.sql.Date.class, new DateJsonValueProcessor());
        config.registerJsonValueProcessor(java.sql.Timestamp.class, new DateJsonValueProcessor());
    }

    /**
     * 设置json日期到java日期的转换格式
     *
     * @author zengjun
     */
    public static void formatDate() {
        // 设定日期转换格式
        JSONUtils.getMorpherRegistry().registerMorpher(
                new DateMorpher(new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd"}));

    }


    /**
     * 把json格式转化为Java对象
     *
     * @param json    JSON格式字符串
     * @param beanCls 调用者(类)
     * @return Json的Java格式对象
     * @author 当郎
     */
    @SuppressWarnings("unchecked")
    public static <T> T Json2Obj(String json, Class<T> beanCls) {
        if (json == null || json.equals("")) {
            return null;
        }
        T jsonObj = null;
        formatDate();
        if (json.startsWith("{")) {
            if (beanCls != null) {
                jsonObj = (T) JSONObject.toBean(JSONObject.fromObject(json), beanCls);
            } else {
                jsonObj = (T) JSONObject.toBean(JSONObject.fromObject(json), JsonUtil.getJsonConfig());
            }
        }
        if (json.startsWith("[")) {
            jsonObj = (T) JSONArray.toArray(JSONArray.fromObject(json), JsonUtil.getJsonConfig());
        }
        return jsonObj;
    }

    /**
     * 把json格式转化为Java对象
     * 并且该Java对象中包含有其它的对象
     *
     * @param json    JSON格式字符串
     * @param beanCls 调用者(类)
     * @return Json的Java格式对象
     * @author 当郎
     */
    @SuppressWarnings("unchecked")
    public static <T> T Json2ObjMap(String json, Class<T> beanCls, Map<String, Class> map) {
        if (json == null || json.equals("")) {
            return null;
        }
        T jsonObj = null;
        formatDate();
        if (json.startsWith("{")) {
            if (beanCls != null) {
                jsonObj = (T) JSONObject.toBean(JSONObject.fromObject(json), beanCls, map);
            } else {
                jsonObj = (T) JSONObject.toBean(JSONObject.fromObject(json), JsonUtil.getJsonConfig());
            }
        }
        if (json.startsWith("[")) {
            jsonObj = (T) JSONArray.toArray(JSONArray.fromObject(json), JsonUtil.getJsonConfig());
        }
        return jsonObj;
    }

    /**
     * 把消息列表转为json格式
     *
     * @param msgList
     * @return
     * @author 当郎
     */
    public static <T> String msg2Json(List<T> msgList) {
        formatDate();
        JSONArray json = JSONArray.fromObject(msgList, JsonUtil.getJsonConfig());
        return json.toString();
    }


    /**
     * /**
     * 分页列表json格式（带button）。
     *
     * @param totalCount    总行数
     * @param dataListName  数据列表键
     * @param dataList      数据列表值
     * @param msgName       消息键
     * @param msgList       消息值
     * @param hidButtonName 需隐藏按钮键
     * @param hidButtonList 需隐藏按钮值
     * @return
     */
    public static <E> String getJsonString(int totalCount, String dataListName, List<E> dataList, String msgName,
                                           List<String> msgList, String hidButtonName, List<String> hidButtonList) {
        // return json string
        StringBuffer json = new StringBuffer("{");
        json.append("\"success\":");
        json.append(true);
        json.append(",");
        json.append("\"totalCount\":\"");
        json.append(totalCount);
        json.append("\",");
        json.append("\"" + dataListName + "\":");
        json.append(getJsonArray(dataList));
        json.append(",");
        json.append("\"" + msgName + "\":");
        json.append(msg2Json(msgList));
        if (hidButtonName != null && !"".equals(hidButtonName)) {
            json.append(",");
            json.append("\"" + hidButtonName + "\":");
            json.append(msg2Json(hidButtonList));

        }

        json.append("}");
        return json.toString();
    }

    /**
     * 对象json格式
     *
     * @param object
     * @return
     * @author 当郎
     */
    public static String getJsonObject(Object object) {
        formatDate();
        JSONObject json = JSONObject.fromObject(object, JsonUtil.getJsonConfig());
        return json.toString();
    }

    /**
     * 列表json格式
     *
     * @param list
     * @return
     * @author 当郎
     */
    public static <T> String getJsonArray(List<T> list) {
        formatDate();
        JSONArray json = JSONArray.fromObject(list, JsonUtil.getJsonConfig());
        return json.toString();
    }


}
