package com.fisiofino.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.postgresql.core.v3.SqlSerializationContext;

import com.fisiofino.model.MalaltiaPacient;
import com.fisiofino.model.Pacient;
import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class GestioPacients {

//Apartat 1: Insercions a partir del XML
    public int CreatePatient(Pacient p) throws SQLException, IOException, PoolInitializationException {
        String sql = """
            INSERT INTO patient
                (given_name, family_name, nif, birth_date, gender, address,
                 city, postal_code, country, telephone, email)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = Connexio.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, p.getGivenName());
            stmt.setString(2, p.getFamilyName());
            stmt.setString(3, p.getDni());
            stmt.setObject(4, p.getBirthDate());
            stmt.setString(5, p.getGender());
            stmt.setString(6, p.getAddress());
            stmt.setString(7, p.getCity());
            stmt.setString(8, p.getPostalCode());
            stmt.setString(9, p.getCountry());
            stmt.setString(10, p.getTelephone());
            stmt.setString(11, p.getEmail());

            if (stmt.executeUpdate() != 1) {
                throw new SQLException("No se pudo insertar el paciente");
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("La base de datos no devolvió el ID del paciente");
    }

    public boolean CreateMalaltiaPacient(MalaltiaPacient mp) throws SQLException {
        String sql = """
            INSERT INTO malaltia
                (treatment, dniPacient, malaltia_status, code, malaltia_name, dose, unit, frequency)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = Connexio.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, mp.getTreatment());
            stmt.setString(2, mp.getDniPacient());
            stmt.setString(3, mp.getStatus());
            stmt.setString(4, mp.getCode());
            stmt.setString(5, mp.getName());
            stmt.setDouble(6, mp.getDose());
            stmt.setString(7, mp.getUnit());
            stmt.setString(8, mp.getFrequency());

            if (stmt.executeUpdate() != 1) {
                throw new SQLException("No se pudo insertar la enfermedad");
            }
            return true;
        }
    }    

    public boolean CreateMalaltia(MalaltiaPacient mp) throws SQLException {

        boolean malaltiaPacientCreada = false;

        /**************** HAS DE COMPLETAR EL CODI *******************/
        
        malaltiaPacientCreada = true;
        return malaltiaPacientCreada;
    }    

//Apartat 2: Cerca de pacient per DNI
    public Pacient findBydniNie(String dni_nie) throws SQLException {
            
        Pacient p = null;

        /**************** HAS DE COMPLETAR EL CODI *******************/
        p.getDni(dni_nie);

        return  p;
    }

//Apartat 3: Malalties del pacient
    public List<MalaltiaPacient> findDiseaseBydniNie(String dni_nie) throws SQLException {
        List<MalaltiaPacient> malaltiespacient = new ArrayList<MalaltiaPacient>();

        /**************** HAS DE COMPLETAR EL CODI *******************/    

        return malaltiespacient;
    }

//Apartat 4: Llistar tots els pacients
    public List<Pacient> findAllPacients () throws SQLException {
        List<Pacient> pacients = new ArrayList<>();

        /**************** HAS DE COMPLETAR EL CODI *******************/
        
        return pacients;
    }

//Apartat 5: Modificar l'estat d'una malaltia d'un pacient
    public boolean updateStatus (int idPacient, String snomedCode, String status) throws SQLException {
        boolean modificat = false;

        /**************** HAS DE COMPLETAR EL CODI *******************/

        return  modificat;
    }

// funcions i mètodes accesoris i necessaris per saber si s'ha d'insertar o modificar    
    public boolean findMalaltia (String SnomedCode) throws SQLException {
        boolean esMalaltia = false;
        
        /**************** HAS DE COMPLETAR EL CODI *******************/
        
        return esMalaltia;
    }

    public boolean findMalaltiaPacient (String dniPacient, String SnomedCode, String status) throws SQLException {
        boolean esMalaltiaPacient = false;

        /**************** HAS DE COMPLETAR EL CODI *******************/
        
        return esMalaltiaPacient;
    }


}
