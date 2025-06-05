package Model.Game;

import Model.Game.Objects.Hand;
import Model.Players.Player;

/**
 * Gestisce tutte le transazioni finanziarie nel gioco di BlackJack.
 * Centralizza la logica di scommesse, pagamenti e gestione del saldo.
 */
public class BankManager {

    private void resetBet(Player player, int handIndex){
        Hand hand = player.getHands().get(handIndex);
        hand.setBet(0);
        if (handIndex == 0)
            player.setCurrentBet(0);
    }

    /**
     * Gestisce una vittoria standard (pagamento 1:1).
     *
     * @param player Il giocatore vincente
     * @param handIndex Indice della mano vincente
     */
    public void payWin(Player player, int handIndex) {
        Hand hand = player.getHands().get(handIndex);
        int bet = hand.getBet();
        player.setBalance(player.getBalance() + bet * 2);
        resetBet(player, handIndex);
    }

    /**
     * Gestisce una vittoria con blackjack (pagamento 3:2).
     *
     * @param player Il giocatore con blackjack
     * @param handIndex Indice della mano con blackjack
     */
    public void payBlackjack(Player player, int handIndex) {
        Hand hand = player.getHands().get(handIndex);
        int bet = hand.getBet();
        player.setBalance(player.getBalance() + (int)(bet * 2.5));
        resetBet(player, handIndex);
    }

    /**
     * Gestisce un pareggio, restituendo la scommessa.
     *
     * @param player Il giocatore che pareggia
     * @param handIndex Indice della mano
     */
    public void payPush(Player player, int handIndex) {
        Hand hand = player.getHands().get(handIndex);
        int bet = hand.getBet();
        player.setBalance(player.getBalance() + bet);
        resetBet(player, handIndex);
    }

    /**
     * Gestisce una perdita, azzerando la scommessa.
     *
     * @param player Il giocatore perdente
     * @param handIndex Indice della mano perdente
     */
    public void handleLoss(Player player, int handIndex) {
        resetBet(player, handIndex);
    }

    /**
     * Gestisce il piazzamento dell'assicurazione.
     *
     * @param player Il giocatore che prende l'assicurazione
     * @return true se l'operazione è riuscita, false altrimenti
     */
    public boolean placeInsurance(Player player) {
        int insuranceAmount = player.getCurrentBet() / 2;

        if (player.getBalance() >= insuranceAmount) {
            player.setBalance(player.getBalance() - insuranceAmount);
            player.setInsurance(true, insuranceAmount);
            if (!player.getHands().isEmpty()) {
                player.getHands().get(0).takeInsurance();
            }

            return true;
        }

        return false;
    }

    /**
     * Gestisce l'assicurazione vincente (pagamento 2:1).
     *
     * @param player Il giocatore con assicurazione
     */
    public void payInsurance(Player player) {
        if (player.hasInsurance()) {
            int insuranceAmount = player.getInsuranceAmount();
            player.setBalance(player.getBalance() + insuranceAmount * 3);
            player.setInsurance(false, 0);
        }
    }

    /**
     * Gestisce l'assicurazione perdente.
     *
     * @param player Il giocatore con assicurazione
     */
    public void handleInsuranceLoss(Player player) {
        if (player.hasInsurance())
            player.setInsurance(false, 0);
    }

    /**
     * Gestisce un double down, raddoppiando la scommessa.
     *
     * @param player Il giocatore che fa double down
     * @param handIndex Indice della mano
     * @return true se l'operazione è riuscita, false altrimenti
     */
    public boolean handleDoubleDown(Player player, int handIndex) {
        Hand hand = player.getHands().get(handIndex);
        int bet = hand.getBet();

        if (player.getBalance() < bet)
            return false;

        hand.doubleDown();
        player.setBalance(player.getBalance() - bet);

        int totalBet = player.getHands().stream()
                .mapToInt(h -> h.getBet())
                .sum();

        player.setCurrentBet(totalBet);

        return true;
    }

    /**
     * Gestisce una divisione della mano (split).
     *
     * @param player Il giocatore che fa split
     * @param handIndex Indice della mano da dividere
     * @return true se l'operazione è riuscita, false altrimenti
     */
    public boolean handleSplit(Player player, int handIndex) {
        Hand hand = player.getHands().get(handIndex);
        int bet = hand.getBet();

        if (player.getBalance() < bet || !player.canSplit(handIndex))
            return false;

        player.setBalance(player.getBalance() - bet);
        player.setCurrentBet(bet);

        return true;
    }
}
