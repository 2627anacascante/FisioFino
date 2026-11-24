package com.fisiofino.database;
import java.util.Properties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;


/**** AQUESTA CLASSE DEFINEIX LA CONNEXIÓ I EL DATASOURCE */

public class Connexio {

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
    public static void iniciaConnexio(String connPropertiesPath) throws IOException {
        /************* HAS DE COMPLETAR EL CODI ******************/
    Properties prop = loadProperties(connPropertiesPath);
    HikariConfig hkcfg = new HikariConfig();
    hkcfg.setJdbcUrl(prop.getProperty("DB_URL"));
    hkcfg.setUsername(prop.getProperty("DB_USER"));
    hkcfg.setPassword(prop.getProperty("DB_PASSWORD"));
    hkcfg.setMaximumPoolSize(Integer.parseInt(prop.getProperty("DB_MAX_POOL")));

    

    try(HikariDataSource hkds = new HikariDataSource(hkcfg);
        Connection cnn = hkds.getConnection()){
    }catch (PoolInitializationException pie){
        pie.printStackTrace();
    }catch(SQLException sqle){
        sqle.printStackTrace();
    }
}

    public static void tancaConnexio(Connection cnn) throws SQLException{
        /************* HAS DE COMPLETAR EL CODI ******************/
        cnn.close();
    }

    private static Properties loadProperties(String connPropertiesPath){
        Properties prop = new Properties();
        try (InputStream in = GestioPacients.class.getClassLoader().getResourceAsStream("conn.properties")){
            if ( in != null){
                prop.load(in);
            }else{
                prop.load(Files.newBufferedReader(Path.of(connPropertiesPath)));
            }

        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return prop;
    }

}
