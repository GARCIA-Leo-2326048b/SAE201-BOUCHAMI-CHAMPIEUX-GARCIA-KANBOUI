package com.example.sae201bouchamichampieuxgarciakanboui;

import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

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
    private MenuButton tournamentMenuButton;

    @FXML
    private MenuItem oneVsOneItem;

    @FXML
    private MenuItem oneVsAIItem;

    @FXML
    private MenuItem tournamentItem;

    @FXML
    private GridPane chessBoard;

    @FXML
    private Label turnLabel;

    @FXML
    private Button playButton;

    @FXML
    private Label labelHaut;

    @FXML
    private Label labelBas;

    @FXML
    private Label checkLabel;

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

    private String selectedTime = "10 min"; // Temps de partie par défaut
    private Timeline whiteTimerTimeline;
    private Timeline blackTimerTimeline;

    private int whiteTimeInSeconds;
    private int blackTimeInSeconds;

    private boolean dejaJoue = false;


    @FXML
    public void initialize() {
        if (dejaJoue){
            isWhiteTurn = true;
            whiteTimeInSeconds = 600;
            blackTimeInSeconds = 600;
            whiteKingMoved = false;
            blackKingMoved = false;
            whiteRooksMoved = new boolean[]{false, false};
            blackRooksMoved = new boolean[]{false, false};

            // Réinitialiser les labels et boutons
            turnLabel.setVisible(false);
            checkLabel.setVisible(false);
            playButton.setText("Rejouer");
            playButton.setDisable(false);
            timeMenuButton.setDisable(false);
            tournamentMenuButton.setDisable(false);

            // Réinitialiser le plateau sans vider les pièces
            stopTimer(); // Ajoute cette ligne pour s'assurer que les timers sont arrêtés
            timeSetter();
            setTimeForPlayers(selectedTime);

            playButton.setOnAction(event -> {
                clearBoard();
                initializeBoard();
                setupEventHandlers();
                updateTurnLabel();
                timeMenuButton.setDisable(true);
                playButton.setDisable(true);
                tournamentMenuButton.setDisable(true);
                startTimer();
                turnLabel.setVisible(true); // Rendre visible le label du tour
            });

        }
        else {
            dejaJoue = true;
            initializeBoard();
            timeSetter();
            setTimeForPlayers(selectedTime);

            playButton.setOnAction(event -> {
                clearBoard();
                initializeBoard();
                setupEventHandlers();
                updateTurnLabel();
                timeMenuButton.setDisable(true);
                playButton.setDisable(true);
                tournamentMenuButton.setDisable(true);
                startTimer();
                turnLabel.setVisible(true); // Rendre visible le label du tour
            });
            setupTournamentMenu();
        }
    }

    private void checkTime() {
        if (whiteTimeInSeconds <= 0 || blackTimeInSeconds <= 0) {
            if (selectedPiece != null) {
                deselectPiece();
            }
            String winner = whiteTimeInSeconds <= 0 ? "Noirs" : "Blancs";
            String styleClass = whiteTimeInSeconds <= 0 ? "check-label-black" : "check-label-white";
            updateCheckLabel("Victoire " + winner, styleClass);

            initialize();
        }
    }


    private void deselectPiece() {
        if (selectedPiece != null) {
            clearSelection();
            selectedPiece = null;
            selectedRow = -1;
            selectedCol = -1;
        }
    }



    private void timeSetter() {
        // Ajout des gestionnaires d'événements aux éléments du menu
        twentyMinItem.setOnAction(event -> handleMenuItemClick(twentyMinItem.getText()));
        tenMinItem.setOnAction(event -> handleMenuItemClick(tenMinItem.getText()));
        fiveMinItem.setOnAction(event -> handleMenuItemClick(fiveMinItem.getText()));
        oneMinItem.setOnAction(event -> handleMenuItemClick(oneMinItem.getText()));

    }

    private void setupTournamentMenu() {
        oneVsOneItem.setOnAction(event -> handleTournamentMenuItemClick(oneVsOneItem.getText()));
        oneVsAIItem.setOnAction(event -> handleTournamentMenuItemClick(oneVsAIItem.getText()));
        tournamentItem.setOnAction(event -> handleTournamentMenuItemClick(tournamentItem.getText()));
    }

    private void handleMenuItemClick(String time) {
        timeMenuButton.setText(time);
        setTimeForPlayers(time);
    }

    private void handleTournamentMenuItemClick(String text) {
        tournamentMenuButton.setText(text);
    }

    // Méthode pour définir le temps de partie pour chaque joueur
    private void setTimeForPlayers(String time) {
        labelBas.setText(time);
        labelHaut.setText(time);
        switch (time){
            case "10 min":
                whiteTimeInSeconds = 10;
                blackTimeInSeconds = 10;
                break;
            case "20 min":
                whiteTimeInSeconds = 20;
                blackTimeInSeconds = 20;
                break;
            case "5 min":
                whiteTimeInSeconds = 300;
                blackTimeInSeconds = 300;
                break;
            case "1 min":
                whiteTimeInSeconds = 60;
                blackTimeInSeconds = 60;
                break;
        }
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

    private void startTimer() {
        stopTimer(); // Ajoute cette ligne pour arrêter le timer en cours

        if (isWhiteTurn) {
            whiteTimerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                whiteTimeInSeconds--;
                updateTimerDisplay();
                checkTime();
            }));
            whiteTimerTimeline.setCycleCount(Timeline.INDEFINITE);
            whiteTimerTimeline.play();
        } else {
            blackTimerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                blackTimeInSeconds--;
                updateTimerDisplay();
                checkTime();
            }));
            blackTimerTimeline.setCycleCount(Timeline.INDEFINITE);
            blackTimerTimeline.play();
        }
    }


    private void stopTimer() {
        if (whiteTimerTimeline != null && whiteTimerTimeline.getStatus() == Timeline.Status.RUNNING) {
            whiteTimerTimeline.stop();
        }
        if (blackTimerTimeline != null && blackTimerTimeline.getStatus() == Timeline.Status.RUNNING) {
            blackTimerTimeline.stop();
        }
    }


    private void updateTimerDisplay() {
        int whiteMinutes = whiteTimeInSeconds / 60;
        int whiteSeconds = whiteTimeInSeconds % 60;
        labelBas.setText(String.format("%02d:%02d", whiteMinutes, whiteSeconds));

        int blackMinutes = blackTimeInSeconds / 60;
        int blackSeconds = blackTimeInSeconds % 60;
        labelHaut.setText(String.format("%02d:%02d", blackMinutes, blackSeconds));
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

                        } else {
                            updateCheckLabel("Échec", "check-label-black");
                        }
                    } else if (isInCheck("noir")) {
                        if (isCheckmate("noir")) {
                            updateCheckLabel("Échec et mat", "check-label-white");

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

    private void updateCheckLabel(String message, String styleClass) {
        checkLabel.setText(message);
        checkLabel.setVisible(true);
        checkLabel.getStyleClass().removeAll("check-label-white", "check-label-black");
        checkLabel.getStyleClass().add(styleClass);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isCorrectTurn(int row, int col) {
        String piece = board[row][col];
        return (isWhiteTurn && piece.startsWith("blanc")) || (!isWhiteTurn && piece.startsWith("noir"));
    }

    private void selectPiece(int row, int col) {
        selectedPiece = (ImageView) boardCells[row][col].getChildren().get(0);
        selectedRow = row;
        selectedCol = col;
        selectedCell = boardCells[row][col];
        selectedCell.getStyleClass().add("yellow-cell");
        showPossibleMoves(selectedRow, selectedCol);
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

    private void toggleTurn() {
        stopTimer(); // Ajoute cette ligne pour arrêter le timer en cours
        isWhiteTurn = !isWhiteTurn;
        updateTurnLabel();
        startTimer(); // Ajoute cette ligne pour démarrer le timer du joueur suivant
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


}