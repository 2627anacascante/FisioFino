package com.fisiofino;

import java.io.IOException;
import java.sql.SQLException;

import com.fisiofino.controlador.Controller;
import com.fisiofino.database.Connexio;
import com.fisiofino.database.GestioPacients;
import com.fisiofino.vista.MenuView;

public class Main {
    public static void main(String[] args) {
        try {
            Connexio.iniciaConnexio("conn.properties");
            MenuView vista = new MenuView();
            GestioPacients gestioPacients = new GestioPacients();
            Controller conT = new Controller(vista, gestioPacients);
            conT.inicia();
        } catch (IOException e) {
            System.out.println("No s'ha pogut carregar la configuració: " + e.getMessage());
        }catch(SQLException sqle){
        sqle.printStackTrace();
        
        } finally {
            Connexio.tancaConnexio();
        }
    }
}