package com.csye6225.springapi.springmvcrest.Configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//        entityManagerFactoryRef = "userEntityManagerFactory",
//        transactionManagerRef = "userTransactionManager",
//        basePackages = {
//                "com.csye6225.springapi.springmvcrest.repositories"
//        }
//)
public class UserConfiguration {

    @Primary
    @Bean(name = "userDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource userDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "userEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("userDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("com.csye6225.springapi.springmvcrest.domain")
                .persistenceUnit("db1")
                .build();
    }

    @Primary
    @Bean(name = "userTransactionManager")
    public PlatformTransactionManager userTransactionManager(
            @Qualifier("userEntityManagerFactory") EntityManagerFactory userEntityManagerFactory
    ) {
        return new JpaTransactionManager(userEntityManagerFactory);
    }



    @Bean(name = "imageTransactionManager")
    public PlatformTransactionManager imageTransactionManager(
            @Qualifier("imageEntityManagerFactory") EntityManagerFactory imageEntityManagerFactory
    ) {
        return new JpaTransactionManager(imageEntityManagerFactory);
    }

    @Bean(name = "imageDataSource")
    @ConfigurationProperties(prefix = "spring.second-datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "imageEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    barEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("imageDataSource") DataSource dataSource
    ) {
        return
                builder
                        .dataSource(dataSource)
                        .packages("com.csye6225.springapi.springmvcrest.domain")
                        .persistenceUnit("db2")
                        .build();
    }
}