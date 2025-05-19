package Model.Game;

import Model.Game.Objects.Hand;
import Model.Players.Dealer;
import Model.Players.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calcola i risultati del gioco e determina vincite e perdite.
 */
public class ResultCalculator {
    private BankManager manager;

    public ResultCalculator(BankManager manager) {
        this.manager = manager;
    }
    /**
     * Calcola i risultati per tutti i giocatori.
     *
     * @param humanPlayer Giocatore umano
     * @param players Lista di tutti i giocatori AI
     * @param dealer Dealer
     */
    public Map<String, Integer> calculateResults(Player humanPlayer, List<Player> players, Dealer dealer) {
        int dealerValue = dealer.getHandValue(0);
        boolean dealerBusted = dealerValue > 21;

        Map<String, Integer> stats = new HashMap<>();
        stats.put("wonHands", 0);
        stats.put("lostHands", 0);
        stats.put("totalHands", 0);

        for (int i = 0; i < humanPlayer.getHandCount(); i++) {
            stats.put("totalHands", stats.get("totalHands") + 1);
            String result = processHandOutcome(humanPlayer, i, dealerValue, dealerBusted);

            if (result.equals("win"))
                stats.put("wonHands", stats.get("wonHands") + 1);
            else
                stats.put("lostHands", stats.get("lostHands") + 1);
        }
        stats.put("finalBalance", humanPlayer.getBalance());
        for (Player player : players)
            for (int i = 0; i < player.getHandCount(); i++) {
                String AIResult = processHandOutcome(player, i, dealerValue, dealerBusted);
            }

        return stats;
    }
    /**
     * Elabora i risultati dell'assicurazione.
     *
     * @param humanPlayer Giocatore umano
     * @param players Lista di tutti i giocatori
     * @param dealer Dealer
     * @param insurancePaid Flag per evitare pagamenti doppi
     */
    public void processInsuranceOutcomes(Player humanPlayer, List<Player> players, Dealer dealer, boolean insurancePaid) {
        if (insurancePaid) return;

        for (Player player : players) {
            if (player.hasInsurance())
                manager.payInsurance(player);
            else if (player != humanPlayer)
                manager.handleInsuranceLoss(player);

        }

        if (humanPlayer.hasInsurance())
            manager.payInsurance(humanPlayer);
        else
            manager.handleInsuranceLoss(humanPlayer);
    }

    /**
     * Cancella tutte le assicurazioni.
     *
     * @param humanPlayer Giocatore umano
     * @param players Lista di tutti i giocatori
     */
    public void clearInsurance(Player humanPlayer, List<Player> players) {
        for (Player player : players)
            if (player != humanPlayer)
                manager.handleInsuranceLoss(player);

        manager.handleInsuranceLoss(humanPlayer);
    }

    /**
     * Elabora il risultato per una mano specifica di un giocatore.
     *
     * @param player Giocatore
     * @param handIndex Indice della mano
     * @param dealerValue Valore della mano del dealer
     * @param dealerBusted Flag che indica se il dealer ha sballato
     */
    private String processHandOutcome(Player player, int handIndex, int dealerValue, boolean dealerBusted) {
        int handValue = player.getHandValue(handIndex);
        boolean handBusted = player.isBusted(handIndex);
        boolean handBlackjack = player.hasBlackjack(handIndex);

        if (handBusted) {
            manager.handleLoss(player, handIndex);
            return "loss";
        } else if (dealerBusted) {
            // Il dealer ha sballato, la mano vince (pagamento 1:1)
            manager.payWin(player, handIndex);
            return "win";
        } else if (handBlackjack && !dealerBusted && dealerValue != 21) {
            // La mano ha blackjack, il dealer no (pagamento 3:2)
            manager.payBlackjack(player, handIndex);
            return "win";
        } else if (handValue > dealerValue) {
            // La mano ha un valore più alto (pagamento 1:1)
            manager.payWin(player, handIndex);
            return "win";
        } else if (handValue < dealerValue) {
            // Il dealer ha un valore più alto
            manager.handleLoss(player, handIndex);
            return "loss";
        } else {
            // Stesso valore, pareggio (restituzione della scommessa)
            manager.payPush(player, handIndex);
            return "win";
        }
    }
}
