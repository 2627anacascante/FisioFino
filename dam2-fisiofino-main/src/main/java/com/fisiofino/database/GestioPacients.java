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
            INSERT INTO malaltiaPacient
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
            stmt.setInt(6, mp.getDose());
            stmt.setString(7, mp.getUnit());
            stmt.setString(8, mp.getFrequency());

            if (stmt.executeUpdate() != 1) {
                throw new SQLException("No se pudo insertar la enfermedad en el paciente");
            }
            return true;
        }
    }    

    public boolean CreateMalaltia(MalaltiaPacient mp) throws SQLException {

        /**************** HAS DE COMPLETAR EL CODI *******************/
        String sql = """
        INSERT INTO malaltia
            (malaltia_name, code)
        VALUES (?, ?))
        """;

    try (Connection conn = Connexio.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, mp.getName());
            stmt.setString(2, mp.getCode());

            if (stmt.executeUpdate() != 1){
                throw new SQLException("no se pudo crear la enfermedad");
            }
            return true;
         }

    }    

//Apartat 2: Cerca de pacient per DNI
    public Pacient findBydniNie(String dni_nie) throws SQLException {
        String sql = """
            SELECT given_name, family_name, nif, birth_date, gender, address,
                   city, postal_code, country, telephone, email
            FROM patient
            WHERE nif = ?
            """;

        try (Connection con = Connexio.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, dni_nie);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Pacient p = new Pacient();
                p.setGivenName(rs.getString("given_name"));
                p.setFamilyName(rs.getString("family_name"));
                p.setDni(rs.getString("nif"));
                p.setBirthDate(rs.getObject("birth_date", java.time.LocalDate.class));
                p.setGender(rs.getString("gender"));
                p.setAddress(rs.getString("address"));
                p.setCity(rs.getString("city"));
                p.setPostalCode(rs.getString("postal_code"));
                p.setCountry(rs.getString("country"));
                p.setTelephone(rs.getString("telephone"));
                p.setEmail(rs.getString("email"));
                return p;
            }
        }
    }

//Apartat 3: Malalties del pacient
    public List<MalaltiaPacient> findDiseaseBydniNie(String dni_nie) throws SQLException {
        String sql = """
            SELECT treatment, dniPacient, malaltia_status, code, malaltia_name, dose, unit, frequency
            FROM malaltiaPacient
            WHERE dniPacient = ?
            """;

        List<MalaltiaPacient> malaltiespacient = new ArrayList<>();
        try (Connection con = Connexio.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, dni_nie);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MalaltiaPacient mp = new MalaltiaPacient();
                    mp.setTreatment(rs.getString("treatment"));
                    mp.setDniPacient(rs.getString("dniPacient"));
                    mp.setCode(rs.getString("code"));
                    mp.setDose(rs.getInt("dose"));
                    mp.setName(rs.getString("malaltia_name"));
                    mp.setStatus(rs.getString("malaltia_status"));
                    mp.setFrequency(rs.getString("frequency"));
                    mp.setUnit(rs.getString("unit"));
                    malaltiespacient.add(mp);
                }
            }
        }
        return malaltiespacient;
    }

//Apartat 4: Llistar tots els pacients
    public List<Pacient> findAllPacients () throws SQLException {
        List<Pacient> pacients = new ArrayList<>();
        String sql = """
            SELECT given_name, family_name, nif, birth_date, gender, address,
                   city, postal_code, country, telephone, email
            FROM patient
            """;

        try (Connection con = Connexio.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Pacient p = new Pacient();
                p.setGivenName(rs.getString("given_name"));
                p.setFamilyName(rs.getString("family_name"));
                p.setDni(rs.getString("nif"));
                p.setBirthDate(rs.getObject("birth_date", java.time.LocalDate.class));
                p.setGender(rs.getString("gender"));
                p.setAddress(rs.getString("address"));
                p.setCity(rs.getString("city"));
                p.setPostalCode(rs.getString("postal_code"));
                p.setCountry(rs.getString("country"));
                p.setTelephone(rs.getString("telephone"));
                p.setEmail(rs.getString("email"));
                pacients.add(p);
            }
        }
        
        return pacients;
    }

//Apartat 5: Modificar l'estat d'una malaltia d'un pacient
    public boolean updateStatus (int idPacient, String snomedCode, String status) throws SQLException {
        boolean modificat = false;

        /**************** HAS DE COMPLETAR EL CODI *******************/
        String sql = """
            SELECT treatment, dniPacient, malaltia_status, code, malaltia_name, dose, unit, frequency
            FROM malaltiaPacient
            WHERE dniPacient = ?
            """;

        try (Connection con = Connexio.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql)){
        stmt.setInt(1, idPacient);
        stmt.setString(2, snomedCode);
        stmt.setString(3, status);
        
        try (ResultSet rs = stmt.executeQuery()){
            while (!modificat) {
                MalaltiaPacient malaltiaPacient = new MalaltiaPacient();
                malaltiaPacient.setStatus(rs.getString("status"));
           }

        }
    }

        return  modificat;
    }

// funcions i mètodes accesoris i necessaris per saber si s'ha d'insertar o modificar    
    public boolean findMalaltia (String SnomedCode) throws SQLException {
        boolean esMalaltia = false;
        
        /**************** HAS DE COMPLETAR EL CODI *******************/
        String sql = """
            SELECT malaltia_name, code
            FROM malaltia
            WHERE code = ?
            """;

        try (Connection con = Connexio.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql)){
            stmt.setString(1, SnomedCode);

            try(ResultSet rs = stmt.executeQuery()){
                while (!esMalaltia) {
                    MalaltiaPacient mp = new MalaltiaPacient();
                    mp.setCode(rs.getString("code"));
                }
            }
        }
        
        return esMalaltia;
    }

    public boolean findMalaltiaPacient (String dniPacient, String SnomedCode, String status) throws SQLException {
        boolean esMalaltiaPacient = false;

        /**************** HAS DE COMPLETAR EL CODI *******************/
        String sql = """
        SELECT treatment, dniPacient, malaltia_status, code, malaltia_name, dose, unit, frequency
            FROM malaltiaPacient
            WHERE dniPacient = ?
        """;

        try (Connection con = Connexio.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql)){
            stmt.setString(1, dniPacient);
            stmt.setString(2, SnomedCode);
            stmt.setString(3, status);
        

            try (ResultSet rs = stmt.executeQuery()){
                while (!esMalaltiaPacient) {
                    MalaltiaPacient mp = new MalaltiaPacient();
                    mp.setTreatment(rs.getString("treatment"));
                    mp.setDniPacient(rs.getString("dniPacient"));
                    mp.setCode(rs.getString("code"));
                    mp.setDose(rs.getInt("dose"));
                    mp.setName(rs.getString("malaltia_name"));
                    mp.setStatus(rs.getString("malaltia_status"));
                    mp.setFrequency(rs.getString("frequency"));
                    mp.setUnit(rs.getString("unit"));
                    
                }
            }
        }
        return esMalaltiaPacient;
    }


}
