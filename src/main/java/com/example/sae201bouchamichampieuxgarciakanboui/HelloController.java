package com.example.sae201bouchamichampieuxgarciakanboui;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class HelloController {

    @FXML
    private MenuButton timeMenuButton;

    @FXML
    private MenuItem twentyMinItem;

    @FXML
    private MenuItem tenMinItem;

    @FXML
    private MenuItem fiveMinItem;

    @FXML
    private MenuItem oneMinItem;

    @FXML
    private GridPane chessBoard;

    private VBox[][] boardCells = new VBox[8][8];
    String[][] board = new String[8][8];
    private ImageView selectedPiece = null;
    private int selectedRow = -1;
    private int selectedCol = -1;

    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
    private boolean[] whiteRooksMoved = {false, false}; // left and right rooks
    private boolean[] blackRooksMoved = {false, false}; // left and right rooks

    @FXML
    public void initialize() {
        setUpTimeButton();
        initializeBoard();
        setupEventHandlers();
    }

    void initializeBoard() {
        // Initialiser les pièces noires
        board[0][0] = "noir/Tournoir.png";
        board[0][1] = "noir/Cavaliernoir.png";
        board[0][2] = "noir/Founoir.png";
        board[0][3] = "noir/Reinenoir.png";
        board[0][4] = "noir/Roinoir.png";
        board[0][5] = "noir/Founoir.png";
        board[0][6] = "noir/Cavaliernoir.png";
        board[0][7] = "noir/Tournoir.png";
        for (int col = 0; col < 8; col++) {
            board[1][col] = "noir/Pionnoir.png";
        }

        // Initialiser les pièces blanches
        board[7][0] = "blanc/Tourblanc.png";
        board[7][1] = "blanc/Cavalierblanc.png";
        board[7][2] = "blanc/Foublanc.png";
        board[7][3] = "blanc/Reineblanc.png";
        board[7][4] = "blanc/Roiblanc.png";
        board[7][5] = "blanc/Foublanc.png";
        board[7][6] = "blanc/Cavalierblanc.png";
        board[7][7] = "blanc/Tourblanc.png";
        for (int col = 0; col < 8; col++) {
            board[6][col] = "blanc/Pionblanc.png";
        }

        // Ajouter les pièces au GridPane
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                VBox vbox = new VBox();
                vbox.setAlignment(Pos.CENTER); // Centre le contenu du VBox
                if (board[row][col] != null) {
                    ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/com/example/sae201bouchamichampieuxgarciakanboui/img/" + board[row][col])));
                    imageView.setFitWidth(100);
                    imageView.setFitHeight(100);
                    vbox.getChildren().add(imageView);
                }
                chessBoard.add(vbox, col, row);
                boardCells[row][col] = vbox;
            }
        }
    }

    private void setupEventHandlers() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                final int r = row;
                final int c = col;
                boardCells[row][col].setOnMouseClicked(event -> handleCellClick(r, c));
            }
        }
    }

    void handleCellClick(int row, int col) {
        if (selectedPiece == null) {
            if (board[row][col] != null) {
                selectedPiece = (ImageView) boardCells[row][col].getChildren().get(0);
                selectedRow = row;
                selectedCol = col;
            }
        } else {
            if (isValidMove(selectedRow, selectedCol, row, col)) {
                // Gestion des roques
                if (board[selectedRow][selectedCol].equals("blanc/Roiblanc.png")) {
                    whiteKingMoved = true;
                    if (selectedCol == 4 && col == 6) { // Roque à droite
                        boardCells[7][7].getChildren().clear();
                        ImageView rook = new ImageView(new Image(getClass().getResourceAsStream("/com/example/sae201bouchamichampieuxgarciakanboui/img/blanc/Tourblanc.png")));
                        rook.setFitWidth(100);
                        rook.setFitHeight(100);
                        boardCells[7][5].getChildren().add(rook);
                        board[7][7] = null;
                        board[7][5] = "blanc/Tourblanc.png";
                    } else if (selectedCol == 4 && col == 2) { // Roque à gauche
                        boardCells[7][0].getChildren().clear();
                        ImageView rook = new ImageView(new Image(getClass().getResourceAsStream("/com/example/sae201bouchamichampieuxgarciakanboui/img/blanc/Tourblanc.png")));
                        rook.setFitWidth(100);
                        rook.setFitHeight(100);
                        boardCells[7][3].getChildren().add(rook);
                        board[7][0] = null;
                        board[7][3] = "blanc/Tourblanc.png";
                    }
                } else if (board[selectedRow][selectedCol].equals("noir/Roinoir.png")) {
                    blackKingMoved = true;
                    if (selectedCol == 4 && col == 6) { // Roque à droite
                        boardCells[0][7].getChildren().clear();
                        ImageView rook = new ImageView(new Image(getClass().getResourceAsStream("/com/example/sae201bouchamichampieuxgarciakanboui/img/noir/Tournoir.png")));
                        rook.setFitWidth(100);
                        rook.setFitHeight(100);
                        boardCells[0][5].getChildren().add(rook);
                        board[0][7] = null;
                        board[0][5] = "noir/Tournoir.png";
                    } else if (selectedCol == 4 && col == 2) { // Roque à gauche
                        boardCells[0][0].getChildren().clear();
                        ImageView rook = new ImageView(new Image(getClass().getResourceAsStream("/com/example/sae201bouchamichampieuxgarciakanboui/img/noir/Tournoir.png")));
                        rook.setFitWidth(100);
                        rook.setFitHeight(100);
                        boardCells[0][3].getChildren().add(rook);
                        board[0][0] = null;
                        board[0][3] = "noir/Tournoir.png";
                    }
                }

                // Gestion de la promotion des pions
                if (board[selectedRow][selectedCol].equals("blanc/Pionblanc.png") && row == 0) {
                    board[selectedRow][selectedCol] = "blanc/Reineblanc.png"; // Promotion en reine
                    selectedPiece.setImage(new Image(getClass().getResourceAsStream("/com/example/sae201bouchamichampieuxgarciakanboui/img/blanc/Reineblanc.png")));
                } else if (board[selectedRow][selectedCol].equals("noir/Pionnoir.png") && row == 7) {
                    board[selectedRow][selectedCol] = "noir/Reinenoir.png"; // Promotion en reine
                    selectedPiece.setImage(new Image(getClass().getResourceAsStream("/com/example/sae201bouchamichampieuxgarciakanboui/img/noir/Reinenoir.png")));
                }

                // Déplacement de la pièce
                boardCells[selectedRow][selectedCol].getChildren().clear();
                boardCells[row][col].getChildren().add(selectedPiece);

                board[row][col] = board[selectedRow][selectedCol];
                board[selectedRow][selectedCol] = null;

                selectedPiece = null;
                selectedRow = -1;
                selectedCol = -1;
            } else {
                showError("Mouvement invalide !");
            }
        }
    }

    private boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        String piece = board[fromRow][fromCol];
        String targetPiece = board[toRow][toCol];

        // Empêcher les pièces blanches de se déplacer sur d'autres pièces blanches et les pièces noires sur d'autres pièces noires
        if ((piece.startsWith("blanc") && targetPiece != null && targetPiece.startsWith("blanc")) ||
                (piece.startsWith("noir") && targetPiece != null && targetPiece.startsWith("noir"))) {
            return false;
        }

        switch (piece) {
            case "noir/Pionnoir.png":
                return isValidMovePionNoir(fromRow, fromCol, toRow, toCol);
            case "blanc/Pionblanc.png":
                return isValidMovePionBlanc(fromRow, fromCol, toRow, toCol);
            case "noir/Cavaliernoir.png":
            case "blanc/Cavalierblanc.png":
                return isValidMoveCavalier(fromRow, fromCol, toRow, toCol);
            case "noir/Tournoir.png":
            case "blanc/Tourblanc.png":
                return isValidMoveTour(fromRow, fromCol, toRow, toCol);
            case "noir/Founoir.png":
            case "blanc/Foublanc.png":
                return isValidMoveFou(fromRow, fromCol, toRow, toCol);
            case "noir/Reinenoir.png":
            case "blanc/Reineblanc.png":
                return isValidMoveReine(fromRow, fromCol, toRow, toCol);
            case "noir/Roinoir.png":
            case "blanc/Roiblanc.png":
                return isValidMoveRoi(fromRow, fromCol, toRow, toCol);
            default:
                return false;
        }
    }

    boolean isValidMovePionNoir(int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow == 1) { // Mouvement initial de deux cases pour un pion
            return (toRow == fromRow + 1 || toRow == fromRow + 2) && fromCol == toCol && board[toRow][toCol] == null;
        } else {
            return toRow == fromRow + 1 && fromCol == toCol && board[toRow][toCol] == null;
        }
    }

    boolean isValidMovePionBlanc(int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow == 6) { // Mouvement initial de deux cases pour un pion
            return (toRow == fromRow - 1 || toRow == fromRow - 2) && fromCol == toCol && board[toRow][toCol] == null;
        } else {
            return toRow == fromRow - 1 && fromCol == toCol && board[toRow][toCol] == null;
        }
    }

    boolean isValidMoveCavalier(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }

    boolean isValidMoveTour(int fromRow, int fromCol, int toRow, int toCol) {
        return fromRow == toRow || fromCol == toCol;
    }

    boolean isValidMoveFou(int fromRow, int fromCol, int toRow, int toCol) {
        return Math.abs(fromRow - toRow) == Math.abs(fromCol - toCol);
    }

    boolean isValidMoveReine(int fromRow, int fromCol, int toRow, int toCol) {
        return isValidMoveTour(fromRow, fromCol, toRow, toCol) || isValidMoveFou(fromRow, fromCol, toRow, toCol);
    }

    boolean isValidMoveRoi(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);
        if (rowDiff <= 1 && colDiff <= 1) {
            return true;
        }
        // Gestion du roque
        if (fromRow == 0 && fromCol == 4 && toRow == 0) { // Roi noir
            if (!blackKingMoved) {
                if (toCol == 6 && !blackRooksMoved[1] && board[0][5] == null && board[0][6] == null) {
                    return true; // Roque à droite
                } else if (toCol == 2 && !blackRooksMoved[0] && board[0][1] == null && board[0][2] == null && board[0][3] == null) {
                    return true; // Roque à gauche
                }
            }
        } else if (fromRow == 7 && fromCol == 4 && toRow == 7) { // Roi blanc
            if (!whiteKingMoved) {
                if (toCol == 6 && !whiteRooksMoved[1] && board[7][5] == null && board[7][6] == null) {
                    return true; // Roque à droite
                } else if (toCol == 2 && !whiteRooksMoved[0] && board[7][1] == null && board[7][2] == null && board[7][3] == null) {
                    return true; // Roque à gauche
                }
            }
        }
        return false;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setUpTimeButton() {
        twentyMinItem.setOnAction(event -> timeMenuButton.setText("20 min"));
        tenMinItem.setOnAction(event -> timeMenuButton.setText("10 min"));
        fiveMinItem.setOnAction(event -> timeMenuButton.setText("5 min"));
        oneMinItem.setOnAction(event -> timeMenuButton.setText("1 min"));
    }
}
