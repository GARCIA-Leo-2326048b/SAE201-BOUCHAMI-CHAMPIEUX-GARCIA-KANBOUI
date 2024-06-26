package com.example.sae201bouchamichampieuxgarciakanboui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChessController {


    @FXML
    private MenuButton tournamentMenuButton;

    @FXML
    private MenuItem oneVsOneItem;

    @FXML
    private MenuItem oneVsAIItem;

    //@FXML
   // private MenuItem tournamentItem;

    @FXML
    private GridPane chessBoard;

    @FXML
    private Label turnLabel;

    @FXML
    private Button playButton;

    @FXML
    private MenuButton partieTimeMenuButton;

    @FXML
    private MenuItem twentyMinItem;

    @FXML
    private MenuItem tenMinItem;

    @FXML
    private MenuItem fiveMinItem;

    @FXML
    private MenuItem oneMinItem;

    @FXML
    private Label tempsPartieHaut;

    @FXML
    private  Label tempsPartieBas;

    @FXML
    private Label checkLabel;

    @FXML
    private Button abandonBlanc;

    @FXML
    private Button abandonNoir;

    private String gameMode = "1 vs 1";

    private VBox[][] boardCells = new VBox[8][8];
    private String[][] board = new String[8][8];
    private ImageView selectedPiece = null;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private List<Circle> moveIndicators = new ArrayList<>();
    private List<VBox> redCells = new ArrayList<>();
    private VBox selectedCell = null;

    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
    private boolean[] whiteRooksMoved = {false, false}; // left and right rooks
    private boolean[] blackRooksMoved = {false, false}; // left and right rooks

    private boolean isWhiteTurn = true; // Indicateur de tour, commence par les blancs

    private Timeline whiteTimerTimeline;
    private Timeline blackTimerTimeline;
    private String selectedPartieTime = "10 min";
    private int whitePartieTimeInSeconds;
    private int blackPartieTimeInSeconds;

    private boolean dejaJoue = false;


    @FXML
    public void initialize() {
        if (dejaJoue){
            if (selectedPiece != null) {
                deselectPiece();
            }
            isWhiteTurn = true;
            whiteKingMoved = false;
            blackKingMoved = false;
            whiteRooksMoved = new boolean[]{false, false};
            blackRooksMoved = new boolean[]{false, false};

            // Réinitialiser les labels et boutons
            abandonBlanc.setVisible(false);
            abandonNoir.setVisible(false);
            playButton.setText("Rejouer");
            playButton.setDisable(false);
            partieTimeMenuButton.setDisable(false);
            partieTimeMenuButton.setText("10 min");
            tournamentMenuButton.setDisable(false);

            // Réinitialiser le plateau sans vider les pièces
            stopPartieTimer();
            partieTimeSetter();
            setPartieTime(selectedPartieTime);

            playButton.setOnAction(event -> {
                abandonBlanc.setVisible(true);
                abandonNoir.setVisible(true);
                turnLabel.setVisible(false);
                checkLabel.setVisible(false);
                clearBoard();
                initializeBoard();
                setupEventHandlers();
                updateTurnLabel();
                partieTimeMenuButton.setDisable(true);
                playButton.setDisable(true);
                tournamentMenuButton.setDisable(true);
                startPartieTimer();
                turnLabel.setVisible(true); // Rendre visible le label du tour
            });
            setupTournamentMenu();

        }
        else {
            dejaJoue = true;
            initializeBoard();
            partieTimeSetter();
            setPartieTime(selectedPartieTime);

            playButton.setOnAction(event -> {
                abandonBlanc.setVisible(true);
                abandonNoir.setVisible(true);
                clearBoard();
                initializeBoard();
                setupEventHandlers();
                updateTurnLabel();
                partieTimeMenuButton.setDisable(true);
                playButton.setDisable(true);
                tournamentMenuButton.setDisable(true);
                startPartieTimer();
                turnLabel.setVisible(true); // Rendre visible le label du tour
            });
            setupTournamentMenu();
        }

        // Gestionnaires pour les boutons d'abandon
        abandonBlanc.setOnAction(event -> handleAbandon("Blancs"));
        abandonNoir.setOnAction(event -> handleAbandon("Noirs"));
    }

    // Méthode pour gérer l'abandon
    private void handleAbandon(String player) {
        if (selectedPiece != null) {
            deselectPiece();
        }
        String winner = player.equals("Blancs") ? "Noirs" : "Blancs";
        String styleClass = player.equals("Blancs") ? "check-label-black" : "check-label-white";
        updateCheckLabel("Victoire " + winner, styleClass);
        disableUserInteraction();
        initialize();
    }

    private void checkPartieTime() {
        if (whitePartieTimeInSeconds <= 0 || blackPartieTimeInSeconds <= 0) {
            if (selectedPiece != null) {
                deselectPiece();
            }
            String winner = whitePartieTimeInSeconds <= 0 ? "Noirs" : "Blancs";
            String styleClass = whitePartieTimeInSeconds <= 0 ? "check-label-black" : "check-label-white";
            updateCheckLabel("Victoire " + winner, styleClass);
            disableUserInteraction();
            initialize();
        }
    }

    private void partieTimeSetter() {
        // Ajout des gestionnaires d'événements aux éléments du menu
        fiveMinItem.setOnAction(event -> setupToursTimer(fiveMinItem.getText()));
        tenMinItem.setOnAction(event -> setupToursTimer(tenMinItem.getText()));
        twentyMinItem.setOnAction(event -> setupToursTimer(twentyMinItem.getText()));
        oneMinItem.setOnAction(event -> setupToursTimer(oneMinItem.getText()));

    }

    // Méthode pour définir le temps de partie pour chaque joueur
    private void setPartieTime(String time) {
        tempsPartieBas.setText(time);
        tempsPartieHaut.setText(time);
        switch (time){
            case "10 min":
                whitePartieTimeInSeconds = 600;
                blackPartieTimeInSeconds = 600;
                break;
            case "20 min":
                whitePartieTimeInSeconds = 1200;
                blackPartieTimeInSeconds = 1200;
                break;
            case "5 min":
                whitePartieTimeInSeconds = 300;
                blackPartieTimeInSeconds = 300;
                break;
            case "1 min":
                whitePartieTimeInSeconds = 60;
                blackPartieTimeInSeconds = 60;
                break;
        }
    }

    private void startPartieTimer() {
        stopPartieTimer(); // Ajoute cette ligne pour arrêter le timer en cours

        if (isWhiteTurn) {
            whiteTimerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                whitePartieTimeInSeconds--;
                updatePartieTimerDisplay();
                checkPartieTime();
            }));
            whiteTimerTimeline.setCycleCount(Timeline.INDEFINITE);
            whiteTimerTimeline.play();
        } else {
            blackTimerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                blackPartieTimeInSeconds--;
                updatePartieTimerDisplay();
                checkPartieTime();
            }));
            blackTimerTimeline.setCycleCount(Timeline.INDEFINITE);
            blackTimerTimeline.play();
        }
    }

    private void stopPartieTimer() {
        if (whiteTimerTimeline != null && whiteTimerTimeline.getStatus() == Timeline.Status.RUNNING) {
            whiteTimerTimeline.stop();
        }
        if (blackTimerTimeline != null && blackTimerTimeline.getStatus() == Timeline.Status.RUNNING) {
            blackTimerTimeline.stop();
        }
    }

    //Afficher le temps sous la forme "minute:secondes"
    private void updatePartieTimerDisplay() {
        int whiteMinutes = whitePartieTimeInSeconds / 60;
        int whiteSeconds = whitePartieTimeInSeconds % 60;
        tempsPartieBas.setText(String.format("%02d:%02d", whiteMinutes, whiteSeconds));

        int blackMinutes = blackPartieTimeInSeconds / 60;
        int blackSeconds = blackPartieTimeInSeconds % 60;
        tempsPartieHaut.setText(String.format("%02d:%02d", blackMinutes, blackSeconds));
    }

    private void setupTournamentMenu() {
        oneVsOneItem.setOnAction(event -> handleTournamentMenuItemClick(oneVsOneItem.getText()));
        oneVsAIItem.setOnAction(event -> handleTournamentMenuItemClick(oneVsAIItem.getText()));
        //tournamentItem.setOnAction(event -> handleTournamentMenuItemClick(tournamentItem.getText()));
    }

    private void setupToursTimer(String time) {
        partieTimeMenuButton.setText(time);
        setPartieTime(time);
    }

    private void handleTournamentMenuItemClick(String text) {
        tournamentMenuButton.setText(text);
        gameMode = text;
    }

    // Méthode pour nettoyer le plateau
    private void clearBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boardCells[row][col].getChildren().clear();
                board[row][col] = null;
            }
        }
    }

    private void initializeBoard() {
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

    private void updateCheckLabel(String message, String styleClass) {
        checkLabel.setText(message);
        checkLabel.setVisible(true);
        checkLabel.getStyleClass().removeAll("check-label-white", "check-label-black");
        checkLabel.getStyleClass().add(styleClass);
    }

    private void deselectPiece() {
        if (selectedPiece != null) {
            clearSelection();
            selectedPiece = null;
            selectedRow = -1;
            selectedCol = -1;
        }
    }

    private void clearSelection() {
        if (selectedCell != null) {
            selectedCell.getStyleClass().remove("yellow-cell");
            selectedCell = null;
        }
        selectedPiece = null;
        selectedRow = -1;
        selectedCol = -1;
        clearMoveIndicators();
        resetRedCells();
    }

    private void clearMoveIndicators() {
        for (Circle circle : moveIndicators) {
            ((VBox) circle.getParent()).getChildren().remove(circle);
        }
        moveIndicators.clear();
    }

    private void resetRedCells() {
        for (VBox cell : redCells) {
            cell.getStyleClass().remove("red-cell");
        }
        redCells.clear();
    }

    private void handleCellClick(int row, int col) {
        if (selectedPiece == null) {
            if (board[row][col] != null && isCorrectTurn(row, col)) {
                selectPiece(row, col);
            }
        } else {
            if (board[row][col] != null && board[selectedRow][selectedCol].startsWith(board[row][col].substring(0, 5))) {
                clearSelection();
                selectPiece(row, col);
            } else {
                clearMoveIndicators();
                resetRedCells();
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
                    if (board[row][col] != null) {
                        // Enlève la pièce capturée
                        boardCells[row][col].getChildren().clear();
                    }
                    boardCells[row][col].getChildren().add(selectedPiece);

                    board[row][col] = board[selectedRow][selectedCol];
                    board[selectedRow][selectedCol] = null;

                    selectedPiece = null;
                    selectedRow = -1;
                    selectedCol = -1;
                    clearSelection();
                    toggleTurn();

                    // Vérification des échecs et échecs et mats
                    if (isInCheck("blanc")) {
                        if (isCheckmate("blanc")) {
                            updateCheckLabel("Échec et mat ", "check-label-black");
                            disableUserInteraction();
                            initialize();
                        } else {
                            updateCheckLabel("Échec", "check-label-black");
                        }
                    } else if (isInCheck("noir")) {
                        if (isCheckmate("noir")) {
                            updateCheckLabel("Échec et mat", "check-label-white");
                            disableUserInteraction();
                            initialize();
                        } else {
                            updateCheckLabel("Échec", "check-label-white");
                        }
                    } else {
                        checkLabel.setVisible(false);
                    }
                } else {
                    clearSelection();
                }
            }
        }
    }

    private void selectPiece(int row, int col) {
        selectedPiece = (ImageView) boardCells[row][col].getChildren().get(0);
        selectedRow = row;
        selectedCol = col;
        selectedCell = boardCells[row][col];
        selectedCell.getStyleClass().add("yellow-cell");
        showPossibleMoves(selectedRow, selectedCol);
    }

    private boolean isInCheck(String color) {
        int kingRow = -1;
        int kingCol = -1;

        // Trouver le roi
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] != null && board[row][col].equals(color + "/Roi" + color + ".png")) {
                    kingRow = row;
                    kingCol = col;
                    break;
                }
            }
        }

        // Vérifier si le roi est attaqué
        return isUnderAttack(kingRow, kingCol, color);
    }

    private boolean isUnderAttack(int kingRow, int kingCol, String color) {
        String opponentColor = color.equals("blanc") ? "noir" : "blanc";

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] != null && board[row][col].startsWith(opponentColor)) {
                    if (isValidMoveWithoutCheck(row, col, kingRow, kingCol)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isCheckmate(String color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] != null && board[row][col].startsWith(color)) {
                    for (int toRow = 0; toRow < 8; toRow++) {
                        for (int toCol = 0; toCol < 8; toCol++) {
                            if (isValidMoveWithoutCheck(row, col, toRow, toCol)) {
                                String tempPiece = board[toRow][toCol];
                                board[toRow][toCol] = board[row][col];
                                board[row][col] = null;
                                boolean inCheck = isInCheck(color);
                                board[row][col] = board[toRow][toCol];
                                board[toRow][toCol] = tempPiece;
                                if (!inCheck) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean isValidMoveWithoutCheck(int fromRow, int fromCol, int toRow, int toCol) {
        String piece = board[fromRow][fromCol];
        String destinationPiece = board[toRow][toCol];

        if (destinationPiece != null) {
            if ((piece.startsWith("blanc") && destinationPiece.startsWith("blanc")) ||
                    (piece.startsWith("noir") && destinationPiece.startsWith("noir"))) {
                return false;
            }
        }

        switch (piece) {
            case "noir/Pionnoir.png":
                return isValidMovePionNoir(fromRow, fromCol, toRow, toCol, destinationPiece);
            case "blanc/Pionblanc.png":
                return isValidMovePionBlanc(fromRow, fromCol, toRow, toCol, destinationPiece);
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

    private boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        String piece = board[fromRow][fromCol];
        String destinationPiece = board[toRow][toCol];

        // Vérifier si la pièce destination est de la même couleur
        if (destinationPiece != null) {
            if ((piece.startsWith("blanc") && destinationPiece.startsWith("blanc")) ||
                    (piece.startsWith("noir") && destinationPiece.startsWith("noir"))) {
                return false;
            }
        }

        if (isValidMoveWithoutCheck(fromRow, fromCol, toRow, toCol)) {
            // Simuler le mouvement pour vérifier s'il met le roi en échec
            String tempPiece = board[toRow][toCol];
            board[toRow][toCol] = board[fromRow][fromCol];
            board[fromRow][fromCol] = null;
            boolean isInCheck = isInCheck(piece.startsWith("blanc") ? "blanc" : "noir");
            board[fromRow][fromCol] = board[toRow][toCol];
            board[toRow][toCol] = tempPiece;
            return !isInCheck;
        }

        return false;
    }

    private boolean isValidMovePionNoir(int fromRow, int fromCol, int toRow, int toCol, String destinationPiece) {
        if (destinationPiece == null) {
            if (fromRow == 1) { // Mouvement initial de deux cases pour un pion
                if ((toRow == fromRow + 2) && fromCol == toCol) {
                    return board[fromRow + 1][fromCol] == null && board[toRow][toCol] == null;
                } else {
                    return toRow == fromRow + 1 && fromCol == toCol && board[toRow][toCol] == null;
                }
            } else {
                return toRow == fromRow + 1 && fromCol == toCol && board[toRow][toCol] == null;
            }
        } else {
            return toRow == fromRow + 1 && Math.abs(fromCol - toCol) == 1 && destinationPiece.startsWith("blanc");
        }
    }

    private boolean isValidMovePionBlanc(int fromRow, int fromCol, int toRow, int toCol, String destinationPiece) {
        if (destinationPiece == null) {
            if (fromRow == 6) { // Mouvement initial de deux cases pour un pion
                if ((toRow == fromRow - 2) && fromCol == toCol) {
                    return board[fromRow - 1][fromCol] == null && board[toRow][toCol] == null;
                } else {
                    return toRow == fromRow - 1 && fromCol == toCol && board[toRow][toCol] == null;
                }
            } else {
                return toRow == fromRow - 1 && fromCol == toCol && board[toRow][toCol] == null;
            }
        } else {
            return toRow == fromRow - 1 && Math.abs(fromCol - toCol) == 1 && destinationPiece.startsWith("noir");
        }
    }

    private boolean isValidMoveCavalier(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }

    private boolean isValidMoveTour(int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow != toRow && fromCol != toCol) {
            return false;
        }
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);
        int rowStep = (toRow - fromRow) / Math.max(1, rowDiff);
        int colStep = (toCol - fromCol) / Math.max(1, colDiff);

        for (int i = 1; i < Math.max(rowDiff, colDiff); i++) {
            if (board[fromRow + i * rowStep][fromCol + i * colStep] != null) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidMoveFou(int fromRow, int fromCol, int toRow, int toCol) {
        if (Math.abs(fromRow - toRow) != Math.abs(fromCol - toCol)) {
            return false;
        }
        int rowDiff = Math.abs(fromRow - toRow);
        int rowStep = (toRow - fromRow) / rowDiff;
        int colStep = (toCol - fromCol) / rowDiff;

        for (int i = 1; i < rowDiff; i++) {
            if (board[fromRow + i * rowStep][fromCol + i * colStep] != null) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidMoveReine(int fromRow, int fromCol, int toRow, int toCol) {
        return isValidMoveTour(fromRow, fromCol, toRow, toCol) || isValidMoveFou(fromRow, fromCol, toRow, toCol);
    }

    private boolean isValidMoveRoi(int fromRow, int fromCol, int toRow, int toCol) {
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

    private void showPossibleMoves(int fromRow, int fromCol) {
        String piece = board[fromRow][fromCol];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (isValidMove(fromRow, fromCol, row, col)) {
                    if (board[row][col] != null) {
                        boardCells[row][col].getStyleClass().add("red-cell");
                        redCells.add(boardCells[row][col]);
                    } else {
                        Circle circle = new Circle(15, Color.LIGHTGRAY);
                        boardCells[row][col].getChildren().add(circle);
                        moveIndicators.add(circle);
                    }
                }
            }
        }
    }

    // Fonction qui permet à l'IA de jouer un coup aléatoire
    private void makeRandomMoveForAI() {
        List<int[]> possibleMoves = new ArrayList<>();
        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                if (board[fromRow][fromCol] != null && board[fromRow][fromCol].startsWith("noir")) {
                    for (int toRow = 0; toRow < 8; toRow++) {
                        for (int toCol = 0; toCol < 8; toCol++) {
                            if (isValidMove(fromRow, fromCol, toRow, toCol)) {
                                possibleMoves.add(new int[]{fromRow, fromCol, toRow, toCol});
                            }
                        }
                    }
                }
            }
        }

        if (!possibleMoves.isEmpty()) {
            Random random = new Random();
            int[] move = possibleMoves.get(random.nextInt(possibleMoves.size()));
            handleCellClick(move[0], move[1]);
            handleCellClick(move[2], move[3]);
        }
        enableUserInteraction();
    }

    private void disableUserInteraction() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boardCells[row][col].setDisable(true);
            }
        }
    }

    private void enableUserInteraction() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boardCells[row][col].setDisable(false);
            }
        }
    }

    private boolean isCorrectTurn(int row, int col) {
        String piece = board[row][col];
        return (isWhiteTurn && piece.startsWith("blanc")) || (!isWhiteTurn && piece.startsWith("noir"));
    }

    private void toggleTurn() {
        stopPartieTimer();
        isWhiteTurn = !isWhiteTurn;
        updateTurnLabel();
        if (gameMode.equals("1 vs IA")) {
            if (isWhiteTurn) {
                enableUserInteraction();
            } else {
                disableUserInteraction();
                playMoveWithDelay();
            }
        }
        startPartieTimer();
    }

    private void playMoveWithDelay() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            makeRandomMoveForAI();
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    private void updateTurnLabel() {
        turnLabel.setText("Tour: " + (isWhiteTurn ? "Blancs" : "Noirs"));
        turnLabel.getStyleClass().removeAll("turn-label-white", "turn-label-black");
        if (isWhiteTurn) {
            turnLabel.getStyleClass().add("turn-label-white");
        } else {
            turnLabel.getStyleClass().add("turn-label-black");
        }
    }

}