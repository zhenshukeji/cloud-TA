package com.zhenshu.framework.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author yuxi
 * @date 2020-11-09
 * @desc wx家长端小程序设置
 */
@Configuration
@ConfigurationProperties(prefix = "wx-config")
public class WxProperties {

    private String appId;

    private String appSecret;

    private Live live;

    private Record record;

    private Market market;

    private Subscribe subscribe;

    /**
     * 机构端扫码支付
     */
    private PayConfig payConfig;

    private Group group;

    private Distribution distribution;

    private Course course;

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Distribution getDistribution() {
        return distribution;
    }

    public void setDistribution(Distribution distribution) {
        this.distribution = distribution;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }


    public Subscribe getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Subscribe subscribe) {
        this.subscribe = subscribe;
    }

    /**
     * 团购页面配置
     */
    public static class Group {
        private String page;

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }
    }

    /**
     * 分销页面配置
     */
    public static class Distribution {
        private String page;

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }
    }

    /**
     * 线下页面配置
     */
    public static class Course {
        private String page;

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }
    }

    /**
     * 直播页面配置
     */
    public static class Live {
        private String page;

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }
    }

    /**
     * 公众号配置
     */
    public static class Subscribe {

        private String appId;

        private String appSecret;

        private Template template;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }

        public Template getTemplate() {
            return template;
        }

        public void setTemplate(Template template) {
            this.template = template;
        }

        /**
         * 模板
         */
        public static class Template {

            private Renewal renewal;

            private CheckWork checkWork;

            private ClassNotice classNotice;

            public Renewal getRenewal() {
                return renewal;
            }

            public void setRenewal(Renewal renewal) {
                this.renewal = renewal;
            }

            public CheckWork getCheckWork() {
                return checkWork;
            }

            public void setCheckWork(CheckWork checkWork) {
                this.checkWork = checkWork;
            }

            public ClassNotice getClassNotice() {
                return classNotice;
            }

            public void setClassNotice(ClassNotice classNotice) {
                this.classNotice = classNotice;
            }

            // 续费预警
            public static class Renewal{
                private String id;
                private String studentName;
                private String courseName;
                private String copyWrite;
                private String remark;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getStudentName() {
                    return studentName;
                }

                public void setStudentName(String studentName) {
                    this.studentName = studentName;
                }

                public String getCourseName() {
                    return courseName;
                }

                public void setCourseName(String courseName) {
                    this.courseName = courseName;
                }

                public String getCopyWrite() {
                    return copyWrite;
                }

                public void setCopyWrite(String copyWrite) {
                    this.copyWrite = copyWrite;
                }

                public String getRemark() {
                    return remark;
                }

                public void setRemark(String remark) {
                    this.remark = remark;
                }
            }

            // 考勤通知
            public static class CheckWork{
                private String id;
                private String copyWrite;
                private String type;
                private String courseName;
                private String classTime;
                private String className;
                private String teacherName;
                private String remark;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public String getCourseName() {
                    return courseName;
                }

                public void setCourseName(String courseName) {
                    this.courseName = courseName;
                }

                public String getCopyWrite() {
                    return copyWrite;
                }

                public void setCopyWrite(String copyWrite) {
                    this.copyWrite = copyWrite;
                }

                public String getClassTime() {
                    return classTime;
                }

                public void setClassTime(String classTime) {
                    this.classTime = classTime;
                }

                public String getRemark() {
                    return remark;
                }

                public void setRemark(String remark) {
                    this.remark = remark;
                }

                public String getClassName() {
                    return className;
                }

                public void setClassName(String className) {
                    this.className = className;
                }

                public String getTeacherName() {
                    return teacherName;
                }

                public void setTeacherName(String teacherName) {
                    this.teacherName = teacherName;
                }
            }

            public static class ClassNotice{
                private String id;
                private String copyWrite;
                private String courseName;
                private String classTime;
                private String classLocation;
                private String remark;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getCopyWrite() {
                    return copyWrite;
                }

                public void setCopyWrite(String copyWrite) {
                    this.copyWrite = copyWrite;
                }

                public String getCourseName() {
                    return courseName;
                }

                public void setCourseName(String courseName) {
                    this.courseName = courseName;
                }

                public String getClassTime() {
                    return classTime;
                }

                public void setClassTime(String classTime) {
                    this.classTime = classTime;
                }

                public String getClassLocation() {
                    return classLocation;
                }

                public void setClassLocation(String classLocation) {
                    this.classLocation = classLocation;
                }

                public String getRemark() {
                    return remark;
                }

                public void setRemark(String remark) {
                    this.remark = remark;
                }
            }
        }
    }

    /**
     * 录播页面
     */
    public static class Record {
        private String page;

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }
    }

    /**
     * 市场活动页面
     */
    public static class Market {
        private String page;

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }
    }

    /**
     * 支付配置
     */
    public static class PayConfig {

        private String appSecret;

        private String mchId;

        private String mchKey;

        private String notifyUrl;

        private String keyPath;

        private String tradeType;

        private String signType;

        public String getSignType() {
            return signType;
        }

        public void setSignType(String signType) {
            this.signType = signType;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }

        public String getMchId() {
            return mchId;
        }

        public void setMchId(String mchId) {
            this.mchId = mchId;
        }

        public String getMchKey() {
            return mchKey;
        }

        public void setMchKey(String mchKey) {
            this.mchKey = mchKey;
        }

        public String getNotifyUrl() {
            return notifyUrl;
        }

        public void setNotifyUrl(String notifyUrl) {
            this.notifyUrl = notifyUrl;
        }

        public String getKeyPath() {
            return keyPath;
        }

        public void setKeyPath(String keyPath) {
            this.keyPath = keyPath;
        }

        public String getTradeType() {
            return tradeType;
        }

        public void setTradeType(String tradeType) {
            this.tradeType = tradeType;
        }
    }

    public PayConfig getPayConfig() {
        return payConfig;
    }

    public void setPayConfig(PayConfig payConfig) {
        this.payConfig = payConfig;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public Live getLive() {
        return live;
    }

    public void setLive(Live live) {
        this.live = live;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

}
