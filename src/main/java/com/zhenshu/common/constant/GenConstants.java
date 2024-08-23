package com.zhenshu.common.constant;

/**
 * 代码生成通用常量
 *
 * @author zhenshu
 */
public class GenConstants {
    /**
     * 单表（增删改查）
     */
    public static final String TPL_CRUD = "crud";

    /**
     * 树表（增删改查）
     */
    public static final String TPL_TREE = "tree";

    /**
     * 树编码字段
     */
    public static final String TREE_CODE = "treeCode";

    /**
     * 树父编码字段
     */
    public static final String TREE_PARENT_CODE = "treeParentCode";

    /**
     * 树名称字段
     */
    public static final String TREE_NAME = "treeName";

    /**
     * 数据库字符串类型
     */
    public static final String[] COLUMNTYPE_STR = {"char" , "varchar" , "narchar" , "varchar2" , "tinytext" , "text" ,
            "mediumtext" , "longtext"};

    /**
     * 数据库时间类型
     */
    public static final String[] COLUMNTYPE_TIME = {"datetime" , "time" , "date" , "timestamp"};

    /**
     * 数据库数字类型
     */
    public static final String[] COLUMNTYPE_NUMBER = {"tinyint" , "smallint" , "mediumint" , "int" , "number" , "integer" ,
            "bigint" , "float" , "double" , "decimal"};
    /**
     * 数据库tinyint
     */
    public static final String[] COLUMNTYPE_BOOLEAN_INT = {"tinyint"};

    /**
     * 数据库短整型类型
     */
    public static final String[] COLUMNTYPE_SHORT_INT = {"smallint" , "mediumint"};

    /**
     * 数据库整形
     */
    public static final String[] COLUMNTYPE_INT = {"int" , "number" , "integer"};

    /**
     * 数据库长整形
     */
    public static final String[] COLUMNTYPE_LONG = {"bigint"};

    /**
     * 数据库单精度
     */
    public static final String[] COLUMNTYPE_FLOAT = {"float"};
    /**
     * 数据库双精度
     */
    public static final String[] COLUMNTYPE_DOUBLE = {"double"};
    /**
     * 十进制小数
     */
    public static final String[] COLUMNTYPE_DECIMAL = {"decimal"};
    /**
     * 页面不需要编辑字段
     */
    public static final String[] COLUMNNAME_NOT_EDIT = {"id" , "create_by" , "create_time" , "del_flag"};

    /**
     * 页面不需要显示的列表字段
     */
    public static final String[] COLUMNNAME_NOT_LIST = {"id" , "create_by" , "create_time" , "del_flag" , "update_by" ,
            "update_time"};

    /**
     * 页面不需要查询字段
     */
    public static final String[] COLUMNNAME_NOT_QUERY = {"id" , "create_by" , "create_time" , "del_flag" , "update_by" ,
            "update_time" , "remark"};

    /**
     * Entity基类字段
     */
    public static final String[] BASE_ENTITY = {"createBy" , "createTime" , "updateBy" , "updateTime" , "remark"};

    /**
     * Tree基类字段
     */
    public static final String[] TREE_ENTITY = {"parentName" , "parentId" , "orderNum" , "ancestors" , "children"};

    /**
     * 文本框
     */
    public static final String HTML_INPUT = "input";

    /**
     * 文本域
     */
    public static final String HTML_TEXTAREA = "textarea";

    /**
     * 下拉框
     */
    public static final String HTML_SELECT = "select";

    /**
     * 单选框
     */
    public static final String HTML_RADIO = "radio";

    /**
     * 复选框
     */
    public static final String HTML_CHECKBOX = "checkbox";

    /**
     * 日期控件
     */
    public static final String HTML_DATETIME = "datetime";

    /**
     * 上传空间
     */
    public static final String HTML_UPLOAD = "upload";

    /**
     * 字符串类型
     */
    public static final String TYPE_STRING = "String";

    /**
     * 整型
     */
    public static final String TYPE_INTEGER = "Integer";

    /**
     * 长整型
     */
    public static final String TYPE_LONG = "Long";
    /**
     * 短整型
     */
    public static final String TYPE_SHORT = "Short";

    /**
     * 单精度浮点型
     */
    public static final String TYPE_FLOAT = "Float";

    /**
     * 双精度浮点型
     */
    public static final String TYPE_DOUBLE = "Double";

    /**
     * 布尔类型
     */
    public static final String TYPE_BOOLEAN = "Boolean";

    /**
     * 高精度计算类型
     */
    public static final String TYPE_BIGDECIMAL = "BigDecimal";

    /**
     * 时间类型
     */
    public static final String TYPE_DATE = "Date";

    /**
     * 模糊查询
     */
    public static final String QUERY_LIKE = "LIKE";

    /**
     * 需要
     */
    public static final String REQUIRE = "1";
}
