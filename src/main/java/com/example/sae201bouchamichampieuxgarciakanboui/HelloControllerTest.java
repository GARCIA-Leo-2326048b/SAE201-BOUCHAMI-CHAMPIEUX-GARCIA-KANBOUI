package com.example.sae201bouchamichampieuxgarciakanboui;

import org.junit.Test;

import static org.junit.Assert.*;

public class HelloControllerTest {

    @Test
    public void testPionNoirMove() {
        HelloController controller = new HelloController();
        controller.initializeBoard();
        assertTrue(controller.isValidMovePionNoir(1, 0, 3, 0));
        assertFalse(controller.isValidMovePionNoir(1, 0, 4, 0));
    }

    @Test
    public void testPionBlancMove() {
        HelloController controller = new HelloController();
        controller.initializeBoard();
        assertTrue(controller.isValidMovePionBlanc(6, 0, 4, 0));
        assertFalse(controller.isValidMovePionBlanc(6, 0, 3, 0));
    }

    @Test
    public void testCavalierMove() {
        HelloController controller = new HelloController();
        controller.initializeBoard();
        assertTrue(controller.isValidMoveCavalier(0, 1, 2, 2));
        assertFalse(controller.isValidMoveCavalier(0, 1, 2, 3));
    }

    @Test
    public void testTourMove() {
        HelloController controller = new HelloController();
        controller.initializeBoard();
        assertTrue(controller.isValidMoveTour(0, 0, 0, 7));
        assertFalse(controller.isValidMoveTour(0, 0, 1, 1));
    }

    @Test
    public void testFouMove() {
        HelloController controller = new HelloController();
        controller.initializeBoard();
        assertTrue(controller.isValidMoveFou(0, 2, 3, 5));
        assertFalse(controller.isValidMoveFou(0, 2, 4, 5));
    }

    @Test
    public void testReineMove() {
        HelloController controller = new HelloController();
        controller.initializeBoard();
        assertTrue(controller.isValidMoveReine(0, 3, 3, 6));
        assertTrue(controller.isValidMoveReine(0, 3, 0, 6));
        assertFalse(controller.isValidMoveReine(0, 3, 2, 5));
    }

    @Test
    public void testRoiMove() {
        HelloController controller = new HelloController();
        controller.initializeBoard();
        assertTrue(controller.isValidMoveRoi(0, 4, 1, 4));
        assertFalse(controller.isValidMoveRoi(0, 4, 2, 4));
    }

    @Test
    public void testRoque() {
        HelloController controller = new HelloController();
        controller.initializeBoard();
        assertTrue(controller.isValidMoveRoi(7, 4, 7, 6)); // Roque à droite blanc
        assertTrue(controller.isValidMoveRoi(7, 4, 7, 2)); // Roque à gauche blanc
        assertTrue(controller.isValidMoveRoi(0, 4, 0, 6)); // Roque à droite noir
        assertTrue(controller.isValidMoveRoi(0, 4, 0, 2)); // Roque à gauche noir
    }

    @Test
    public void testPromotionPionBlanc() {
        HelloController controller = new HelloController();
        controller.initializeBoard();
        controller.board[1][0] = "blanc/Pionblanc.png"; // Mettre un pion blanc en position de promotion
        controller.handleCellClick(1, 0);
        controller.handleCellClick(0, 0); // Déplacer le pion pour promotion
        assertEquals("blanc/Reineblanc.png", controller.board[0][0]);
    }

    @Test
    public void testPromotionPionNoir() {
        HelloController controller = new HelloController();
        controller.initializeBoard();
        controller.board[6][0] = "noir/Pionnoir.png"; // Mettre un pion noir en position de promotion
        controller.handleCellClick(6, 0);
        controller.handleCellClick(7, 0); // Déplacer le pion pour promotion
        assertEquals("noir/Reinenoir.png", controller.board[7][0]);
    }
}
