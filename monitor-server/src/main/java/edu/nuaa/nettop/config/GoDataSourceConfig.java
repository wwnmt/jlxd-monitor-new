package edu.nuaa.nettop.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-07-23
 * Time: 15:23
 * 用于查询邓理的数据库
 */
@Configuration
@MapperScan(
        basePackages = "edu.nuaa.nettop.dao.go",
        sqlSessionFactoryRef = "goSqlSessionFactory",
        sqlSessionTemplateRef = "goSqlSessionTemplate"
)
public class GoDataSourceConfig {
    // 将这个对象放入Spring容器中
    @Bean(name = "goDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.go")
    public DataSource getDateSource2() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "goSqlSessionFactory")
    public SqlSessionFactory goSqlSessionFactory(@Qualifier("goDataSource") DataSource datasource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(datasource);
        bean.setMapperLocations(
                // 设置mybatis的xml所在位置
                new PathMatchingResourcePatternResolver().getResources("classpath*:mapping/go/*.xml"));
        return bean.getObject();
    }

    @Bean("goSqlSessionTemplate")
    public SqlSessionTemplate goSqlSessionTemplate(
            @Qualifier("goSqlSessionFactory") SqlSessionFactory sessionFactory) {
        return new SqlSessionTemplate(sessionFactory);
    }
}
