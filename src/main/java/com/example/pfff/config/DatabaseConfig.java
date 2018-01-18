
package com.example.pfff.config;

import org.h2.jdbcx.JdbcDataSource;
import org.postgresql.jdbc2.optional.SimpleDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DatabaseConfig {
    @Bean
    public DataSource dataSource() throws URISyntaxException {
        String database_url = System.getenv("DATABASE_URL");
        if (database_url == null) {
            JdbcDataSource dataSource = new JdbcDataSource();
            dataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
            dataSource.setUser("sa");
            dataSource.setPassword("sa");
            return dataSource;
        } else {
            URI dbUri = new URI(database_url);

            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath();

            SimpleDataSource dataSource = new SimpleDataSource();
            dataSource.setUrl(dbUrl);
            dataSource.setUser(username);
            dataSource.setPassword(password);

            return dataSource;
        }
    }

}
