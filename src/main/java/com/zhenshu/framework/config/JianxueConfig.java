package com.zhenshu.framework.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/10/31 11:26
 * @desc
 */
@Component
@ConfigurationProperties(prefix = "jianxue")
public class JianxueConfig {

    /**
     * 签到每日领取的鉴学点
     */
    private BigDecimal signPoint;

    /**
     * 拥有的线索数上限
     */
    private Integer clueLimit;

    /**
     * 解锁线索需要的鉴学点数
     */
    private BigDecimal collectPoint;

    /**
     * 成为会员所需鉴学点
     */
    private BigDecimal memberAmount;

    /**
     * 团购活动配置
     */
    private GroupAct groupAct;

    /**
     * 分销活动配置
     */
    private DistributionAct distributionAct;

    /**
     * 直播录播购买配置
     */
    private Live live;

    public Live getLive() {
        return live;
    }

    public void setLive(Live live) {
        this.live = live;
    }

    public static class GroupAct {


        /**
         * 小于时的费率
         */
        private BigDecimal rate;

        /**
         * 最小课次数
         */
        private Integer timeNumLess;

        /**
         * 最大课次数
         */
        private Integer timeNumMore;

        /**
         * 最小团购价
         */
        private BigDecimal priceLess;

        /**
         * 最大团购价
         */
        private BigDecimal priceMore;

        public BigDecimal getRate() {
            return rate;
        }

        public void setRate(BigDecimal rate) {
            this.rate = rate;
        }

        public Integer getTimeNumLess() {
            return timeNumLess;
        }

        public void setTimeNumLess(Integer timeNumLess) {
            this.timeNumLess = timeNumLess;
        }

        public Integer getTimeNumMore() {
            return timeNumMore;
        }

        public void setTimeNumMore(Integer timeNumMore) {
            this.timeNumMore = timeNumMore;
        }

        public BigDecimal getPriceLess() {
            return priceLess;
        }

        public void setPriceLess(BigDecimal priceLess) {
            this.priceLess = priceLess;
        }

        public BigDecimal getPriceMore() {
            return priceMore;
        }

        public void setPriceMore(BigDecimal priceMore) {
            this.priceMore = priceMore;
        }
    }

    public static class DistributionAct {


        /**
         * 小于时的费率
         */
        private BigDecimal rate;

        /**
         * 最小课次数
         */
        private Integer timeNumLess;

        /**
         * 最大课次数
         */
        private Integer timeNumMore;

        /**
         * 最小团购价
         */
        private BigDecimal priceLess;

        /**
         * 最大团购价
         */
        private BigDecimal priceMore;


        public BigDecimal getRate() {
            return rate;
        }

        public void setRate(BigDecimal rate) {
            this.rate = rate;
        }

        public Integer getTimeNumLess() {
            return timeNumLess;
        }

        public void setTimeNumLess(Integer timeNumLess) {
            this.timeNumLess = timeNumLess;
        }

        public Integer getTimeNumMore() {
            return timeNumMore;
        }

        public void setTimeNumMore(Integer timeNumMore) {
            this.timeNumMore = timeNumMore;
        }

        public BigDecimal getPriceLess() {
            return priceLess;
        }

        public void setPriceLess(BigDecimal priceLess) {
            this.priceLess = priceLess;
        }

        public BigDecimal getPriceMore() {
            return priceMore;
        }

        public void setPriceMore(BigDecimal priceMore) {
            this.priceMore = priceMore;
        }
    }

    public BigDecimal getMemberAmount() {
        return memberAmount;
    }

    public void setMemberAmount(BigDecimal memberAmount) {
        this.memberAmount = memberAmount;
    }

    /**
     * 计算团购费率
     */
    public BigDecimal getGroupRate() {
        return groupAct.getRate();
    }

    /**
     * 计算分销费率
     */
    public BigDecimal getDistributionRate() {
        return distributionAct.getRate();
    }

    /**
     * 直播录播购买
     */
    public static class Live{

        private Integer num;

        private Price price;

        public Integer getNum() {
            return num;
        }

        public void setNum(Integer num) {
            this.num = num;
        }

        public Price getPrice() {
            return price;
        }

        public void setPrice(Price price) {
            this.price = price;
        }

        public static class Price {

            private BigDecimal discount;

            private BigDecimal normal;

            public BigDecimal getDiscount() {
                return discount;
            }

            public void setDiscount(BigDecimal discount) {
                this.discount = discount;
            }

            public BigDecimal getNormal() {
                return normal;
            }

            public void setNormal(BigDecimal normal) {
                this.normal = normal;
            }
        }
    }

    public DistributionAct getDistributionAct() {
        return distributionAct;
    }

    public void setDistributionAct(DistributionAct distributionAct) {
        this.distributionAct = distributionAct;
    }

    public GroupAct getGroupAct() {
        return groupAct;
    }

    public void setGroupAct(GroupAct groupAct) {
        this.groupAct = groupAct;
    }

    public BigDecimal getCollectPoint() {
        return collectPoint;
    }

    public void setCollectPoint(BigDecimal collectPoint) {
        this.collectPoint = collectPoint;
    }

    public Integer getClueLimit() {
        return clueLimit;
    }

    public void setClueLimit(Integer clueLimit) {
        this.clueLimit = clueLimit;
    }

    public BigDecimal getSignPoint() {
        return signPoint;
    }

    public void setSignPoint(BigDecimal signPoint) {
        this.signPoint = signPoint;
    }
}
