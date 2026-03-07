package refresh.acci.global.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class VectorDbConfig {

    @Bean(name = "vectorDataSource")
    @ConfigurationProperties(prefix = "vector-db")
    public DataSource dataSource(
            @Value("${vector-db.url}") String url,
            @Value("${vector-db.username}") String username,
            @Value("${vector-db.password}") String password,
            @Value("${vector-db.driver-class-name:org.postgresql.Driver}") String driver,
            @Value("${hikari.connection-init-sql:SET client_encoding TO 'UTF8'}") String connectionInitSql
    ) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName(driver);
        ds.setConnectionInitSql(connectionInitSql);

        return ds;
    }

    @Bean(name = "vectorDbJdbcTemplate")
    public JdbcTemplate vectorJdbcTemplate(@Qualifier("vectorDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
