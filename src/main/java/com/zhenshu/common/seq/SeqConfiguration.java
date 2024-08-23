package com.zhenshu.common.seq;

import com.zhenshu.common.seq.dao.SingleSequenceDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;

@Component
@Configuration
public class SeqConfiguration {

    @Bean(initMethod = "init")
    public SingleSequenceDao singleSequence(DataSource dataSource) {
        SingleSequenceDao singleSequenceDao = new SingleSequenceDao();
        singleSequenceDao.setDataSource(dataSource);
        singleSequenceDao.setInnerStep(1000);
        singleSequenceDao.setRetryTimes(5);
        singleSequenceDao.setAdjust(true);
        return singleSequenceDao;
    }

    @Bean(name = "seqBdStudent", initMethod = "init")
    public DefaultSequence seqBdStudent(SingleSequenceDao singleSequenceDao) throws SQLException {
        DefaultSequence defaultSequence = new DefaultSequence();
        defaultSequence.setName("seq_bd_student");
        defaultSequence.setSequenceDao(singleSequenceDao);
        return defaultSequence;
    }

    @Bean(name = "seqBdOrder", initMethod = "init")
    public DefaultSequence seqBdOrder(SingleSequenceDao singleSequenceDao) throws SQLException {
        DefaultSequence defaultSequence = new DefaultSequence();
        defaultSequence.setName("seq_bd_order");
        defaultSequence.setSequenceDao(singleSequenceDao);
        return defaultSequence;
    }

}
