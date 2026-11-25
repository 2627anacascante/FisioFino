package com.fisiofino.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import org.junit.jupiter.api.Test;

import com.fisiofino.database.GestioPacients;
import com.fisiofino.vista.MenuView;

class ControllerTest {

    @Test
    void opcioXFinalitzaElMenu() {
        FakeMenuView vista = new FakeMenuView("x");

        new Controller(vista, new GestioPacients()).inicia();

        assertEquals(1, vista.comptadorMenus);
        assertTrue(vista.errors.isEmpty());
        assertEquals(List.of("Fins aviat!"), vista.missatges);
    }

    @Test
    void opcioNoValidaMostraErrorIContinua() {
        FakeMenuView vista = new FakeMenuView("9", "X");

        new Controller(vista, new GestioPacients()).inicia();

        assertEquals(2, vista.comptadorMenus);
        assertEquals(List.of("Opció no vàlida"), vista.errors);
        assertEquals(List.of("Fins aviat!"), vista.missatges);
    }

    @Test
    void errorDeLecturaMostraErrorIFinalitza() {
        FakeMenuView vista = new FakeMenuView();
        vista.errorLectura = new IOException("fallada de lectura");

        new Controller(vista, new GestioPacients()).inicia();

        assertEquals(1, vista.comptadorMenus);
        assertEquals(List.of("Error de lectura: fallada de lectura"), vista.errors);
        assertEquals(List.of("Fins aviat!"), vista.missatges);
    }

    private static class FakeMenuView extends MenuView {
        private final Queue<String> opcions;
        private final List<String> errors = new ArrayList<>();
        private final List<String> missatges = new ArrayList<>();
        private int comptadorMenus;
        private IOException errorLectura;

        private FakeMenuView(String... opcions) {
            this.opcions = new ArrayDeque<>(Arrays.asList(opcions));
        }

        @Override
        public void mostraBenvinguda() {
        }

        @Override
        public void MenuOpcions() {
            comptadorMenus++;
        }

        @Override
        public String demanaOpcio() throws IOException {
            if (errorLectura != null) {
                throw errorLectura;
            }
            return opcions.poll();
        }

        @Override
        public void mostraError(String missatge) {
            errors.add(missatge);
        }

        @Override
        public void mostraMissatge(String missatge) {
            missatges.add(missatge);
        }
    }
}
