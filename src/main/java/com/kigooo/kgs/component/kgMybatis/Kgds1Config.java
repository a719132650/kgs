package com.kigooo.kgs.component.kgMybatis;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import com.github.pagehelper.PageInterceptor;

// 主数据源配置文件
// basePackages指定使用该数据源的Dao路径
@Configuration
@MapperScan(basePackages = {"com.kigooo.kgs.dao.kgDao"},sqlSessionTemplateRef = "kgds1SqlSessionTemplate")
public class Kgds1Config {
    /**
     * 负责Mybatis初始化并提供SqlSession对象
     * @param dataSource 
     * @return org.apache.ibatis.session.SqlSessionFactory
     * @author
     * @date 2021/8/30 13:16
     */
    @Primary
    @Bean("kgds1SqlSessionFactory")
    public SqlSessionFactory ds1SqlSessionFactory(@Qualifier("kgds1")DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        // 分页配置
        Properties properties = new Properties();
        Interceptor interceptor = new PageInterceptor();
        // 分页数据库方言
        properties.setProperty("helperDialect", "mysql");
        properties.setProperty("autoRuntimeDialect", "true");
        properties.setProperty("reasonable", "false");
        interceptor.setProperties(properties);
        sqlSessionFactoryBean.setPlugins(new Interceptor[] {interceptor});
        return sqlSessionFactoryBean.getObject();
    }

    /**
     * 事务管理配置 用于指定数据源回滚
     * @param dataSource 
     * @return org.springframework.jdbc.datasource.DataSourceTransactionManager
     * @author
     * @date 2021/8/30 13:16
     */
    @Primary
    @Bean("kgds1TransactionManager")
    public DataSourceTransactionManager ds1DataSourceTransactionManager(@Qualifier("kgds1") DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 创建SqlSessionTemplate管理SqlSession
     * @param sqlSessionFactory
     * @return org.mybatis.spring.SqlSessionTemplate
     * @author
     * @date 2021/8/30 13:16
     */
    @Primary
    @Bean("kgds1SqlSessionTemplate")
    public SqlSessionTemplate ds1SqlSessionTemplate(@Qualifier("kgds1SqlSessionFactory") SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
