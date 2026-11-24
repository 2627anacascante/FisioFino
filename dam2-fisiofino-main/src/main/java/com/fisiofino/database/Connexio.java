package com.fisiofino.database;
import java.util.Properties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;


/**** AQUESTA CLASSE DEFINEIX LA CONNEXIÓ I EL DATASOURCE */

public class Connexio {

    private static HikariDataSource dataSource;
    //el datasource es Un DataSource es el objeto que proporciona conexiones a la base de datos. 
    //En tu proyecto, HikariDataSource además administra un pool: mantiene varias conexiones disponibles para reutilizarlas, en lugar de abrir una conexión nueva para cada operación y administra el pool.

    /**************
     * S'HA D'IMPLEMENTAR TOT INCLOSES LES FUNCIONS
     * DEFINIDES A SOTA
     ********************/

    /**
     * Inicia el pool de connexions amb el fitxer de configuració EXTERN (fora del jar).
     *
     * @param connPropertiesPath ruta del fitxer conn.properties (absoluta o relativa al directori de
     *                           treball). Conté DB_URL, DB_USER, DB_PASSWORD i DB_MAX_POOL; com que
     *                           inclou la contrasenya, no s'ha de pujar al repositori.
     */
    public static void iniciaConnexio(String connPropertiesPath) throws IOException, SQLException, PoolInitializationException {
        if (dataSource != null && !dataSource.isClosed()) {
            throw new IllegalStateException("El pool de conexiones ya está iniciado");
        }

        Properties prop = loadProperties(connPropertiesPath);
        HikariConfig hkcfg = new HikariConfig();
        hkcfg.setJdbcUrl(prop.getProperty("DB_URL"));
        hkcfg.setUsername(prop.getProperty("DB_USER"));
        hkcfg.setPassword(prop.getProperty("DB_PASSWORD"));
        hkcfg.setMaximumPoolSize(Integer.parseInt(prop.getProperty("DB_MAX_POOL")));
        dataSource = new HikariDataSource(hkcfg);
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("El pool de conexiones no está iniciado");
        }
        return dataSource.getConnection();
    }

    public static void tancaConnexio() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
        dataSource = null;
    }

    private static Properties loadProperties(String connPropertiesPath) throws IOException {
        Properties prop = new Properties();
        try (var in = Files.newInputStream(Path.of(connPropertiesPath))) {
            prop.load(in);
        }
        return prop;
    }

}
