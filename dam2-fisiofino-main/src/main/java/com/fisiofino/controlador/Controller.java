package com.fisiofino.controlador;

import java.util.List;
import java.io.IOException;
import java.nio.file.Path;

import com.fisiofino.LectorXML;
import com.fisiofino.database.GestioPacients;
import com.fisiofino.model.MalaltiaPacient;
import com.fisiofino.model.Pacient;
import com.fisiofino.vista.MenuView;

public class Controller {

    private final MenuView vista;
    private final GestioPacients gestiopacients;

    public Controller(MenuView vista, GestioPacients gestiopacients) {
        this.vista = vista;
        this.gestiopacients = gestiopacients;
    }

    public void inicia() {
        vista.mostraBenvinguda();
        boolean sortir = false;

        while (!sortir) {
            vista.MenuOpcions();
            try {
                String opcio = vista.demanaOpcio();
                if (opcio == null) break;

                switch (opcio.toUpperCase()) {
                    case "1": /************** Crida als mètodes que fan la carregarXml(); ************/
                    /*************** HAS DE COMPLETAR EL CODI ******************/
                    carregarXml();
                        break;
                    case "2": /************** consultarInfoPersonal(); *******************************/ 
                    /*************** HAS DE COMPLETAR EL CODI ******************/
                    consultarInfoPersonal();
                        break;
                    case "3": /************** consultarInfoMedica(); *********************************/   
                    /*************** HAS DE COMPLETAR EL CODI ******************/
                    consultarInfoMedica();
                        break;
                    case "4": /************** consultarTots(); ***************************************/         
                    /*************** HAS DE COMPLETAR EL CODI ******************/
                    consultarTots();
                        break;
                    case "5": /************** modificarEstat(); **************************************/        
                    /*************** HAS DE COMPLETAR EL CODI ******************/
                    modificarEstat();
                        break;
                    case "X": sortir = true;                break;
                    default:  vista.mostraError("Opció no vàlida");
                }
            } catch (IOException e) {
                vista.mostraError("Error de lectura: " + e.getMessage());
                sortir = true;
            } catch (Exception e) {
                vista.mostraError(e.toString());
            }
        }
        
        vista.mostraMissatge("Fins aviat!");
    }

    private void consultarInfoPersonal() throws Exception{
        // Implementa la lògica per carregar l'XML
        vista.mostraMissatge("Carregant XML");

        String dni = vista.demana("DNI/NIE: ");
        if (dni == null || dni.isBlank()) return;

        Pacient pacient = gestiopacients.findBydniNie(dni);
        if (pacient == null ){
            vista.mostraMissatge("no s'ha trobat el pacient");
        }else{
            vista.mostraPacient(pacient);
        }

    }

    private void consultarInfoMedica() throws Exception {
        String dni = vista.demana("DNI/NIE: ");
        if (dni ==null || dni.isBlank()) return ;

        List<MalaltiaPacient> malalties = gestiopacients.findDiseaseBydniNie(dni);

        if (malalties.isEmpty()){
            vista.mostraMissatge("El pacient no té malalties registrades.");
        }else{
            vista.mostraMalaltiesPacient(malalties);
        }
    }

    private void consultarTots() throws Exception{
        List<Pacient> pacients = gestiopacients.findAllPacients();

        if(pacients.isEmpty()){
            vista.mostraMissatge("No hi ha pacients registrats");
        }else{
            vista.mostraPacients(pacients);
        }
    }

    private void modificarEstat() throws Exception{
        String idText = vista.demana("Id pacient");
        String codi = vista.demana("codi SNOMED");
        String nouEstat = vista.demana("Nou estat:");
        //pide estos valores porque se necesitan para identificar al paciente 
        //la enfermedad específica que se desea actualizar, 
        // así como el nuevo estado que se quiere asignar a esa enfermedad.

        if (idText == null || codi == null || nouEstat == null) return;

        int idPacient;
        try{
            idPacient = Integer.parseInt(idText);
//convierte el id del paciente de String a int para poder usarlo en la base de datos
        }catch(NumberFormatException nfe){
            vista.mostraError("L'id ha de ser un nº");
            return ;
        }
        boolean modificat = gestiopacients.updateStatus(idPacient, codi, nouEstat);

        if (modificat){
            vista.mostraMissatge("Estat actualitzada");
        }else{
            vista.mostraMissatge("no s'ha trobat aquesta malaltia per a el pacient");
        }
    }

    private void carregarXml() throws Exception{
    
        Path fitxer = Path.of("pacientes_bluebutton.xml");

    List<Pacient> pacients = LectorXML.llegirPacients(fitxer);

    List<MalaltiaPacient> malalties = LectorXML.llegirMalalties(fitxer);

    for (Pacient pacient : pacients) {
        if (gestiopacients.findBydniNie(pacient.getDni()) == null) {
            gestiopacients.CreatePatient(pacient);
        }
    }

    for (MalaltiaPacient malaltia : malalties) {
        if (!gestiopacients.findMalaltia(malaltia.getCode())) {
            gestiopacients.CreateMalaltia(malaltia);
        }

        if (!gestiopacients.findMalaltiaPacient(
                malaltia.getDniPacient(),
                malaltia.getCode(),
                malaltia.getStatus())) {
            gestiopacients.CreateMalaltiaPacient(malaltia);
        }
    }

    vista.mostraMissatge("Importació completada.");
    }

}


