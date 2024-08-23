package com.zhenshu.framework.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaRedisConfigImpl;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.zhenshu.framework.config.properties.WxMecProperties;
import com.zhenshu.framework.config.properties.WxProperties;
import me.chanjar.weixin.common.redis.JedisWxRedisOps;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpRedisConfigImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/11/9 11:51
 * @desc
 */
@Configuration
public class WxConfig {

    @Resource
    private WxProperties wxProperties;

    @Resource
    private WxMecProperties wxMecProperties;

    @Resource
    private JedisPool jedisPool;


    @Bean(name = "wxMaConfig")
    public WxMaConfig wxMaConfig() {
        WxMaRedisConfigImpl config = new WxMaRedisConfigImpl(jedisPool);
        config.setAppid(wxProperties.getAppId());
        config.setSecret(wxProperties.getAppSecret());
        return config;
    }

    @Bean(name = "wxMaService")
    public WxMaService wxMaService(WxMaConfig wxMaConfig) {
        WxMaService service = new WxMaServiceImpl();
        service.setWxMaConfig(wxMaConfig);
        return service;
    }

    @Bean(name = "wxMecMaConfig")
    public WxMaConfig wxMecMaConfig() {
        WxMaRedisConfigImpl config = new WxMaRedisConfigImpl(jedisPool);
        config.setAppid(wxMecProperties.getAppId());
        config.setSecret(wxMecProperties.getAppSecret());
        return config;
    }

    @Bean(name = "wxMecMaService")
    public WxMaService wxMecMaService(WxMaConfig wxMecMaConfig) {
        WxMaService service = new WxMaServiceImpl();
        service.setWxMaConfig(wxMecMaConfig);
        return service;
    }

    @Bean
    public WxMpService wxMpService() {
        WxMpRedisConfigImpl configStorage;
        configStorage = new WxMpRedisConfigImpl(new JedisWxRedisOps(jedisPool), wxProperties.getSubscribe().getAppId());

        configStorage.setAppId(wxProperties.getSubscribe().getAppId());
        configStorage.setSecret(wxProperties.getSubscribe().getAppSecret());

        WxMpService service = new WxMpServiceImpl();
        service.setWxMpConfigStorage(configStorage);
        return service;
    }

    @Bean(name = "wxPayConfig")
    public WxPayConfig wxPayConfig() {
        WxPayConfig payConfig = new WxPayConfig();
        WxProperties.PayConfig config = wxProperties.getPayConfig();
        payConfig.setAppId(wxProperties.getAppId());
        payConfig.setMchId(config.getMchId());
        payConfig.setMchKey(config.getMchKey());
        payConfig.setNotifyUrl(config.getNotifyUrl());
        payConfig.setKeyPath(config.getKeyPath());
        payConfig.setTradeType(config.getTradeType());
        payConfig.setSignType(config.getSignType());
        return payConfig;
    }

    @Bean(name = "wxPayConfigByWxPay")
    public WxPayConfig wxPayConfigByWxPay() {
        WxPayConfig payConfig = new WxPayConfig();
        WxMecProperties.PayConfig config = wxMecProperties.getPayConfig();
        payConfig.setAppId(wxMecProperties.getAppId());
        payConfig.setMchId(config.getMchId());
        payConfig.setMchKey(config.getMchKey());
        payConfig.setNotifyUrl(config.getNotifyUrl());
        payConfig.setKeyPath(config.getKeyPath());
        payConfig.setTradeType(config.getTradeType());
        payConfig.setSignType(config.getSignType());
        return payConfig;
    }

    @Bean(name = "wxPayService")
    public WxPayService wxPayService(WxPayConfig wxPayConfig) {
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(wxPayConfig);
        return wxPayService;
    }

    @Bean(name = "wxPayServiceByWxPay")
    public WxPayService wxPayServiceByWxPay(WxPayConfig wxPayConfigByWxPay) {
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(wxPayConfigByWxPay);
        return wxPayService;
    }

}
