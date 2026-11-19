package com.fisiofino;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fisiofino.model.MalaltiaPacient;
import com.fisiofino.model.Pacient;

/**
 * Llegeix un XML amb DOM i en mostra el contingut sense conèixer-ne l'estructura.
 *
 * Important: amb DOM, builder.parse() llegeix TOT el fitxer i construeix l'arbre
 * sencer en memòria. Només després el recorrem i el mostrem.
 */
public class LectorXML {
    static Pacient p;

    static MalaltiaPacient mp;

    public static List<Pacient> llegirPacients(Path entradaXML) throws IOException, ParserConfigurationException, SAXException {
            Document doc = obtenirDocument(entradaXML);
            Element arrel = doc.getDocumentElement();
            List<Pacient> pacients = new ArrayList<>();

            // Comencem el recorregut recursiu per l'arrel, al nivell 0
            p = null;
            mp = null;
            recorrer(arrel, 0,pacients,new ArrayList<>());
            return pacients;
    }

    public static List<MalaltiaPacient> llegirMalalties(Path entradaXML) throws IOException, ParserConfigurationException, SAXException {
            Document doc = obtenirDocument(entradaXML);
            Element arrel = doc.getDocumentElement();
            List<MalaltiaPacient> malalties = new ArrayList<>();

            // Comencem el recorregut recursiu per l'arrel, al nivell 0
            p = null;
            mp = null;
            recorrer(arrel, 0,new ArrayList<>(),malalties);
            return malalties;
    }

    /**
     * Carrega el fitxer XML (Path de NIO) i retorna l'arbre DOM.
     * parse() no accepta un Path, per això li passem un InputStream de NIO.
     */
    public static Document obtenirDocument(Path entradaXML)
            throws IOException, ParserConfigurationException, SAXException {

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        try (InputStream is = Files.newInputStream(entradaXML)) {
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();   // fusiona nodes de text adjacents
            return doc;
        }
    }
    /**
     * Un element és fulla si no té cap fill de tipus element (només text, o res).
     * Atenció: un element buit també es considera fulla.
     */
    static boolean esFulla(Node node) {
        NodeList fills = node.getChildNodes();
        for (int i = 0; i < fills.getLength(); i++) {
            if (fills.item(i).getNodeType() == Node.ELEMENT_NODE) return false;
        }
        return true;
        }

    private static MalaltiaPacient leerProblem(Node node, String dniPacient) {
        MalaltiaPacient malaltia = new MalaltiaPacient();
        malaltia.setDniPacient(dniPacient);

        Node codigo = primerDescendiente(node, "code");
        if (codigo != null) {
            Node valorCodigo = codigo.getAttributes().getNamedItem("code");
            if (valorCodigo != null) {
                malaltia.setCode(valorCodigo.getNodeValue());
            }
            malaltia.setName(codigo.getTextContent().trim());
        }

        Node estado = primerDescendiente(node, "status");
        if (estado != null) {
            malaltia.setStatus(estado.getTextContent().trim());
        }

        List<Node> tratamientosXml = new ArrayList<>();
        recogerDescendientes(node, "treatment", tratamientosXml);
        StringBuilder tratamientos = new StringBuilder();

        for (Node tratamientoXml : tratamientosXml) {
            Node medicacion = primerDescendiente(tratamientoXml, "medication");
            Node dosis = primerDescendiente(tratamientoXml, "dose");
            Node frecuencia = primerDescendiente(tratamientoXml, "frequency");
            StringBuilder detalle = new StringBuilder();

            if (medicacion != null && !medicacion.getTextContent().trim().isEmpty()) {
                detalle.append(medicacion.getTextContent().trim());
            }
            if (dosis != null) {
                Node valor = dosis.getAttributes().getNamedItem("value");
                Node unidad = dosis.getAttributes().getNamedItem("unit");
                if (valor != null) {
                    if (detalle.length() > 0) {
                        detalle.append(" ");
                    }
                    detalle.append(valor.getNodeValue());
                    if (unidad != null) {
                        detalle.append(" ").append(unidad.getNodeValue());
                    }
                }
            }
            if (frecuencia != null && !frecuencia.getTextContent().trim().isEmpty()) {
                if (detalle.length() > 0) {
                    detalle.append(", ");
                }
                detalle.append(frecuencia.getTextContent().trim());
            }

            if (detalle.length() > 0) {
                if (tratamientos.length() > 0) {
                    tratamientos.append("; ");
                }
                tratamientos.append(detalle);
            }
        }

        if (tratamientos.length() > 0) {
            malaltia.setTreatment(tratamientos.toString());
        }
        return malaltia;
    }

    private static Node primerDescendiente(Node node, String nombre) {
        NodeList hijos = node.getChildNodes();
        for (int i = 0; i < hijos.getLength(); i++) {
            Node hijo = hijos.item(i);
            if (hijo.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (hijo.getNodeName().equals(nombre)) {
                return hijo;
            }
            Node encontrado = primerDescendiente(hijo, nombre);
            if (encontrado != null) {
                return encontrado;
            }
        }
        return null;
    }

    private static void recogerDescendientes(Node node, String nombre, List<Node> resultado) {
        NodeList hijos = node.getChildNodes();
        for (int i = 0; i < hijos.getLength(); i++) {
            Node hijo = hijos.item(i);
            if (hijo.getNodeType() == Node.ELEMENT_NODE) {
                if (hijo.getNodeName().equals(nombre)) {
                    resultado.add(hijo);
                }
                recogerDescendientes(hijo, nombre, resultado);
            }
        }
    }

    /**
     * Recorre recursivament l'arbre a partir d'un node.
     *  - Cas base 1: el node no és un element (text de la indentació) -> no fem res.
     *  - Cas base 2: element fulla (només conté text) -> el mostrem i tornem.
     *  - Cas recursiu: element pare -> el mostrem i explorem els seus fills.
     *
     * node:   node que estem visitant
     * nivell: profunditat dins l'arbre (0 = arrel, 1 = registres com <alumne>..., 
     *                                   2 = registres com <nom>)
     */
    public static void recorrer(Node node, int nivell, List<Pacient> pacients, List<MalaltiaPacient> malPacients) {
        
        // Cas base 1: només tractem elements; els nodes de text de la indentació s'ignoren
        if (node.getNodeType() != Node.ELEMENT_NODE) return;

        // Afegim els atributs de l'element (p. ex. id), sigui quin sigui el seu nom
        NamedNodeMap atributs = node.getAttributes();
        for (int i = 0; i < atributs.getLength(); i++) {
            Node at = atributs.item(i);
            if (node.getNodeName().equals("telecom")) {

// AQUÍ TENIU UN EXEMPLE DE COM EXTREURE INFORMACIÓ D'ATRIBUTS
                if (at.getTextContent().contains("tel")) {
                    p.setTelephone(at.getTextContent().substring(4));
                }
                if (at.getTextContent().contains("mailto")) {
                    p.setEmail(at.getTextContent().substring(7));
                }
            }
            /******************** HAS DE COMPLETAR EL CODI ***************/
        }

        if (node.getNodeName().equals("birthTime")) {
            String value = node.getAttributes().getNamedItem("value").getNodeValue();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            p.setBirthDate(LocalDate.parse(value, formatter));
        }

        if (node.getNodeName().equals("administrativeGenderCode")) {
            String value = node.getAttributes().getNamedItem("code").getNodeValue();
            p.setGender(value);
        }

        if (node.getNodeName().equals("streetAddressLine")) {
            p.setAddress(node.getTextContent());
        }

        if(node.getNodeName().equals("city")) {
            p.setCity(node.getTextContent());
        }

        if(node.getNodeName().equals("postalCode")) {
            p.setPostalCode(node.getTextContent());
        }

        if (node.getNodeName().equals("country")) {
            p.setCountry(node.getTextContent());
        }

        if (node.getNodeName().equals("dni")){
            p.setDni(node.getTextContent());
        }

        if (node.getNodeName().equals("family")){
            p.setFamilyName(node.getTextContent());
        }

        if (node.getNodeName().equals("problem")) {
            malPacients.add(leerProblem(node, p.getDni()));
            return;
        }
 
        // Cas base 2: element fulla (nom, cognoms...) -> mostrem nom i text a la mateixa línia
        if (esFulla(node)) {
// AQUÍ TENIU UN EXEMPLE DE COM EXTREURE EL TEXT DELS ELEMENTS/NODES
             if (node.getNodeName().equals("given")) {
                p.setGivenName(node.getTextContent());
            } 
            return;
        /****************** HAS DE COMPLETAR EL CODI ********************/
        }

        // Element pare significa que creem un nou pacient
        if (nivell == 1) {
            p = new Pacient();
        } 

        // Cas recursiu: explorem els fills un nivell més avall
        NodeList fills = node.getChildNodes();
        for (int i = 0; i < fills.getLength(); i++) {
            recorrer(fills.item(i), nivell + 1,pacients,malPacients);
        }
        //añadir paciente
        if (nivell == 1) {
            pacients.add(p);
            p = null;
        }
    
}
}

