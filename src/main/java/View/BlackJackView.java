package View;

import Controller.BlackjackActionListener;
import Controller.BlackjackBettingListener;
import Controller.RoundEndListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista principale del gioco BlackJack che coordina tutte le componenti grafiche.
 * Gestisce il layout del tavolo da gioco, le aree dei giocatori, del dealer
 * e tutti i controlli dell'interfaccia utente.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class BlackJackView extends AnchorPane {
    private final DealerView dealerView;
    private final PlayerInfoView playerView;
    private final ControlPanelView controlPanelView;
    private final BettingView bettingView;
    private PlayerHandsView playerHandsView;
    private List<PlayerHandsView> aiHandsViews;
    private final HBox bottomControlsArea;
    private final HBox allHandsArea;
    private EndRoundPanel endRoundPanel;
    private BlackjackActionListener actionListener;
    private BlackjackBettingListener bettingListener;
    private RoundEndListener roundEndListener;

    /**
     * Costruisce la vista principale del gioco BlackJack.
     * 
     * @param cardBackDesign Il design del dorso delle carte selezionato
     * @param playerImagePath Il percorso dell'avatar del giocatore
     * @param numberOfPlayers Il numero di giocatori AI
     * @param playerName Il nome del giocatore umano
     * @param balance Il saldo iniziale del giocatore
     */
    public BlackJackView(String cardBackDesign, String playerImagePath, int numberOfPlayers, String playerName, int balance) {
        CardImageService.setCardBackDesign(cardBackDesign);

        setPrefSize(1155, 744);
        getStyleClass().add("blackjack-table");

        // Crea le componenti principali
        dealerView = new DealerView();
        playerView = new PlayerInfoView(playerName, playerImagePath, balance);

        controlPanelView = new ControlPanelView();
        bettingView = new BettingView(10, balance);

        // ===== SEZIONE DEALER =====
        dealerView.setPrefSize(400, 250);
        dealerView.getStyleClass().add("dealer-area");

        playerHandsView = new PlayerHandsView("Your Hands", false);
        aiHandsViews = new ArrayList<>();

        for (int i = 0; i < numberOfPlayers; i++)
            aiHandsViews.add(new PlayerHandsView("AI "+ (i+1), true));

        // Container per tutte le mani
        allHandsArea = new HBox();
        allHandsArea.setPrefSize(1324, 336);
        allHandsArea.getChildren().add(playerHandsView);
        allHandsArea.getChildren().addAll(aiHandsViews);
        allHandsArea.setAlignment(Pos.TOP_CENTER);
        allHandsArea.setSpacing(7);
        allHandsArea.getStyleClass().add("ai-player-area");

        // Container per tutti i controlli inferiori
        bottomControlsArea = new HBox();
        bottomControlsArea.getStyleClass().add("controls-panel");
        bottomControlsArea.setPrefSize(1355, 200);
        bottomControlsArea.getChildren().addAll(bettingView, controlPanelView, playerView);
        bottomControlsArea.setSpacing(20);

        endRoundPanel = new EndRoundPanel();
        endRoundPanel.toFront();

        AnchorPane.setTopAnchor(dealerView, 10.0);
        AnchorPane.setLeftAnchor(dealerView, 465.0);

        AnchorPane.setTopAnchor(allHandsArea, 270.0);
        AnchorPane.setLeftAnchor(allHandsArea, 5.0);

        AnchorPane.setTopAnchor(bottomControlsArea, 615.0);
        AnchorPane.setLeftAnchor(bottomControlsArea, 20.0);
        AnchorPane.setRightAnchor(bottomControlsArea, 20.0);

        AnchorPane.setTopAnchor(endRoundPanel, 300.0);
        AnchorPane.setRightAnchor(endRoundPanel, 0.0);

        getChildren().addAll(dealerView, allHandsArea, bottomControlsArea, endRoundPanel);
    }

    /**
     * Restituisce la vista delle informazioni del giocatore.
     * 
     * @return La vista PlayerInfoView
     */
    public PlayerInfoView getPlayerView() {
        return playerView;
    }

    /**
     * Restituisce la vista del dealer.
     * 
     * @return La vista DealerView
     */
    public DealerView getDealerView() {
        return dealerView;
    }

    /**
     * Restituisce la vista delle mani del giocatore umano.
     * 
     * @return La vista PlayerHandsView del giocatore umano
     */
    public PlayerHandsView getPlayerHands() {
        return playerHandsView;
    }

    /**
     * Restituisce la lista delle viste dei giocatori AI.
     * 
     * @return Lista delle viste PlayerHandsView dei giocatori AI
     */
    public List<PlayerHandsView> getAIPlayerViews() {
        return aiHandsViews;
    }

    /**
     * Restituisce la vista del pannello di controllo.
     * 
     * @return La vista ControlPanelView
     */
    public ControlPanelView getControlPanelView() {
        return controlPanelView;
    }

    /**
     * Restituisce la vista delle scommesse.
     * 
     * @return La vista BettingView
     */
    public BettingView getBettingView() {
        return bettingView;
    }

    /**
     * Imposta il listener per le azioni di gioco.
     * 
     * @param listener Il listener per le azioni del giocatore
     */
    public void setActionListener(BlackjackActionListener listener) {
        this.actionListener = listener;
        controlPanelView.setActionListener(listener);
    }

    /**
     * Imposta il listener per le scommesse.
     * 
     * @param listener Il listener per le scommesse del giocatore
     */
    public void setBettingListener(BlackjackBettingListener listener) {
        this.bettingListener = listener;
        bettingView.setBettingListener(listener);
    }

    /**
     * Imposta il listener per gli eventi di fine round.
     *
     * @param listener L'oggetto listener per la gestione della fine del round
     */
    public void setRoundEndListener(RoundEndListener listener) {
        this.roundEndListener = listener;
        endRoundPanel.setEndRoundListener(listener);
    }

    /**
     * Mostra il pannello di fine round basato sul saldo corrente.
     *
     * @param currentBalance Il saldo attuale del giocatore
     * @param minimumBet La scommessa minima richiesta per continuare
     */
    public void showEndRoundPanel(int currentBalance, int minimumBet) {
        if (currentBalance < minimumBet) {
            endRoundPanel.showForInsufficientFunds(currentBalance, minimumBet);
        } else {
            endRoundPanel.showForNewRound();
        }
    }

    /**
     * Resetta completamente la vista per preparare un nuovo round.
     * Ripulisce tutte le mani, resetta i controlli e prepara l'interfaccia
     * per una nuova partita.
     */
    public void resetViewForNewRound() {
        playerHandsView.resetForNewRound();
        dealerView.resetHandForNewRound();

        for (PlayerHandsView aiHandsView : aiHandsViews)
            aiHandsView.resetForNewRound();

        playerView.updateCurrentBet(0);
        controlPanelView.updateControls(false, false, false, false);
        bettingView.setVisible(true);
    }

}
