package Model.Players;

import Model.Game.Objects.Card;
import Model.Game.Objects.Hand;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un giocatore nel gioco di BlackJack.
 * Gestisce le mani di carte, il saldo e le scommesse.
 */
public class Player {
    protected String name;
    protected List<Hand> hands;
    private int balance;
    private int currentBet;
    private boolean hasInsurance;
    private int insuranceAmount;

    /**
     * Costruttore che inizializza un giocatore con un nome e un saldo iniziale.
     *
     * @param name Nome del giocatore
     * @param initialBalance Saldo iniziale
     */
    public Player(String name, int initialBalance) {
        this.name = name;
        this.hands = new ArrayList<>();
        this.hands.add(new Hand()); // Aggiungi una mano vuota di default
        this.balance = initialBalance;
        this.currentBet = 0;
        this.hasInsurance = false;
        this.insuranceAmount = 0;
    }

    /**
     * Aggiunge una carta alla prima mano del giocatore.
     *
     * @param card La carta da aggiungere
     */
    public void addCard(Card card) {
        if (!hands.isEmpty()) {
            hands.get(0).addCard(card);
        }
    }

    /**
     * Aggiunge una carta ad una mano specifica del giocatore.
     *
     * @param handIndex Indice della mano
     * @param card La carta da aggiungere
     */
    public void addCard(int handIndex, Card card) {
        if (handIndex >= 0 && handIndex < hands.size()) {
            hands.get(handIndex).addCard(card);
        }
    }

    /**
     * Restituisce il nome del giocatore.
     *
     * @return Nome del giocatore
     */
    public String getName() {
        return name;
    }

    /**
     * Calcola il valore della prima mano del giocatore.
     *
     * @return Valore della mano principale
     */
    public int getHandValue() {
        if (!hands.isEmpty()) {
            return hands.get(0).getValue();
        }
        return 0;
    }

    /**
     * Calcola il valore di una mano specifica del giocatore.
     *
     * @param handIndex Indice della mano
     * @return Valore della mano specificata
     */
    public int getHandValue(int handIndex) {
        if (handIndex >= 0 && handIndex < hands.size()) {
            return hands.get(handIndex).getValue();
        }
        return 0;
    }

    /**
     * Reimposta le mani del giocatore, rimuovendo tutte le carte.
     */
    public void resetHand() {
        hands.clear();
        hands.add(new Hand());
        hasInsurance = false;
        insuranceAmount = 0;
    }

    /**
     * Restituisce la prima mano del giocatore.
     *
     * @return Lista delle carte nella mano principale
     */
    public List<Card> getHand() {
        if (!hands.isEmpty()) {
            return hands.get(0).getCards();
        }
        return new ArrayList<>();
    }

    /**
     * Restituisce una mano specifica del giocatore.
     *
     * @param handIndex Indice della mano
     * @return Lista delle carte nella mano specificata
     */
    public List<Card> getHand(int handIndex) {
        if (handIndex >= 0 && handIndex < hands.size()) {
            return hands.get(handIndex).getCards();
        }
        return new ArrayList<>();
    }

    /**
     * Restituisce tutte le mani del giocatore.
     *
     * @return Lista di tutte le mani
     */
    public List<Hand> getHands() {
        return hands;
    }

    /**
     * Restituisce il saldo del giocatore.
     *
     * @return Saldo attuale
     */
    public int getBalance() {
        return balance;
    }

    /**
     * Imposta il saldo del giocatore.
     *
     * @param balance Nuovo saldo
     */
    public void setBalance(int balance) {
        this.balance = balance;
    }

    /**
     * Restituisce la scommessa corrente.
     *
     * @return Importo della scommessa corrente
     */
    public int getCurrentBet() {
        return currentBet;
    }

    /**
     * Imposta la scommessa corrente.
     *
     * @param currentBet Nuovo importo della scommessa
     */
    public void setCurrentBet(int currentBet) {
        this.currentBet = currentBet;

        // Aggiorna anche la scommessa nella mano principale
        if (!hands.isEmpty()) {
            hands.get(0).setBet(currentBet);
        }
    }

    /**
     * Effettua una scommessa detraendo l'importo dal saldo.
     *
     * @param amount Importo da scommettere
     * @return true se la scommessa è andata a buon fine, false altrimenti
     */
    public boolean placeBet(int amount) {
        if (amount <= 0 || amount > balance) {
            return false;
        }

        balance -= amount;
        setCurrentBet(amount);

        return true;
    }

    /**
     * Gestisce una vittoria standard (pagamento 1:1).
     */
    public void winBet() {
        balance += currentBet * 2; // Restituisce la puntata originale + vincita
        currentBet = 0;
    }

    /**
     * Gestisce una vittoria con blackjack (pagamento 3:2).
     */
    public void winBlackjack() {
        balance += (int)(currentBet * 2.5); // Restituisce la puntata originale + vincita 3:2
        currentBet = 0;
    }

    /**
     * Gestisce un pareggio, restituendo la scommessa.
     */
    public void pushBet() {
        balance += currentBet; // Restituisce solo la puntata originale
        currentBet = 0;
    }

    /**
     * Gestisce una perdita, azzerando la scommessa corrente.
     */
    public void loseBet() {
        currentBet = 0; // La puntata è già stata sottratta dal saldo
    }

    /**
     * Verifica se il giocatore può fare double down sulla prima mano.
     *
     * @return true se può fare double down, false altrimenti
     */
    public boolean canDoubleDown() {
        if (hands.isEmpty()) return false;
        Hand hand = hands.get(0);
        return hand.size() == 2 && balance >= currentBet;
    }

    /**
     * Verifica se il giocatore può fare double down su una mano specifica.
     *
     * @param handIndex Indice della mano
     * @return true se può fare double down, false altrimenti
     */
    public boolean canDoubleDown(int handIndex) {
        if (handIndex < 0 || handIndex >= hands.size()) return false;
        Hand hand = hands.get(handIndex);
        return hand.size() == 2 && balance >= hand.getBet();
    }

    /**
     * Esegue un double down sulla mano principale, raddoppiando la scommessa
     * e aggiungendo una carta.
     *
     * @param card La carta da aggiungere
     * @return true se l'operazione è riuscita, false altrimenti
     */
    public boolean doubleDown(Card card) {
        if (!canDoubleDown()) return false;

        // Sottrai l'importo aggiuntivo dal saldo
        balance -= currentBet;

        // Raddoppia la scommessa sulla mano
        hands.get(0).doubleDown();
        currentBet *= 2;

        // Aggiungi una carta
        addCard(card);

        return true;
    }

    /**
     * Esegue un double down su una mano specifica.
     *
     * @param handIndex Indice della mano
     * @param card La carta da aggiungere
     * @return true se l'operazione è riuscita, false altrimenti
     */
    public boolean doubleDown(int handIndex, Card card) {
        if (!canDoubleDown(handIndex)) return false;

        Hand hand = hands.get(handIndex);
        int bet = hand.getBet();

        // Sottrai l'importo aggiuntivo dal saldo
        balance -= bet;

        // Raddoppia la scommessa sulla mano
        hand.doubleDown();

        // Aggiungi una carta
        addCard(handIndex, card);

        return true;
    }

    /**
     * Verifica se il giocatore può fare split sulla prima mano.
     *
     * @return true se può fare split, false altrimenti
     */
    public boolean canSplit() {
        if (hands.isEmpty()) return false;
        Hand hand = hands.get(0);
        return hand.canSplit() && balance >= currentBet && hands.size() < 4; // Limite di 4 mani
    }

    /**
     * Verifica se il giocatore può fare split su una mano specifica.
     *
     * @param handIndex Indice della mano
     * @return true se può fare split, false altrimenti
     */
    public boolean canSplit(int handIndex) {
        if (handIndex < 0 || handIndex >= hands.size()) return false;
        Hand hand = hands.get(handIndex);
        return hand.canSplit() && balance >= hand.getBet() && hands.size() < 4; // Limite di 4 mani
    }

    /**
     * Esegue uno split sulla mano principale.
     *
     * @param newCard1 Nuova carta per la prima mano
     * @param newCard2 Nuova carta per la seconda mano
     * @return true se l'operazione è riuscita, false altrimenti
     */
    public boolean splitHand(Card newCard1, Card newCard2) {
        if (!canSplit()) return false;

        Hand originalHand = hands.get(0);
        int bet = originalHand.getBet();

        // Crea una nuova mano con la seconda carta dell'originale
        Card secondCard = originalHand.splitSecondCard();
        if (secondCard == null) return false;

        // Crea la nuova mano con la stessa scommessa
        Hand newHand = new Hand(bet);
        newHand.addCard(secondCard);

        // Aggiungi le nuove carte
        originalHand.addCard(newCard1);
        newHand.addCard(newCard2);

        // Aggiungi la nuova mano alla lista
        hands.add(newHand);

        return true;
    }

    /**
     * Esegue uno split su una mano specifica.
     *
     * @param handIndex Indice della mano
     * @param newCard1 Nuova carta per la mano originale
     * @param newCard2 Nuova carta per la nuova mano
     * @return true se l'operazione è riuscita, false altrimenti
     */
    public boolean splitHand(int handIndex, Card newCard1, Card newCard2) {
        if (!canSplit(handIndex)) return false;

        Hand originalHand = hands.get(handIndex);
        int bet = originalHand.getBet();

        // Crea una nuova mano con la seconda carta dell'originale
        Card secondCard = originalHand.splitSecondCard();
        if (secondCard == null) return false;

        // Crea la nuova mano con la stessa scommessa
        Hand newHand = new Hand(bet);
        newHand.addCard(secondCard);

        // Aggiungi le nuove carte
        originalHand.addCard(newCard1);
        newHand.addCard(newCard2);

        // Aggiungi la nuova mano alla lista
        hands.add(newHand);

        return true;
    }

    /**
     * Prende un'assicurazione contro il blackjack del dealer.
     *
     * @return true se l'operazione è riuscita, false altrimenti
     */
    public boolean takeInsurance() {
        // Imposta l'assicurazione sulla mano principale
        if (!hands.isEmpty())
            hands.get(0).takeInsurance();

        return true;
    }

    /**
     * Verifica se il giocatore ha preso l'assicurazione.
     *
     * @return true se ha l'assicurazione, false altrimenti
     */
    public boolean hasInsurance() {
        return hasInsurance;
    }

    /**
     * Restituisce l'importo dell'assicurazione.
     *
     * @return Importo dell'assicurazione
     */
    public int getInsuranceAmount() {
        return insuranceAmount;
    }

    /**
     * Imposta lo stato di assicurazione.
     *
     * @param hasInsurance Se il giocatore ha assicurazione
     * @param amount Importo dell'assicurazione
     */
    public void setInsurance(boolean hasInsurance, int amount) {
        this.hasInsurance = hasInsurance;
        this.insuranceAmount = amount;
    }

    /**
     * Vince l'assicurazione (pagamento 2:1).
     */
    public void winInsurance() {
        if (hasInsurance) {
            balance += insuranceAmount * 3; // Restituisce l'assicurazione + il pagamento 2:1
            hasInsurance = false;
            insuranceAmount = 0;
        }
    }

    /**
     * Perde l'assicurazione.
     */
    public void loseInsurance() {
        if (hasInsurance) {
            hasInsurance = false;
            insuranceAmount = 0;
        }
    }

    /**
     * Verifica se la prima mano è un blackjack.
     *
     * @return true se è un blackjack, false altrimenti
     */
    public boolean hasBlackjack() {
        if (!hands.isEmpty()) {
            return hands.get(0).isBlackjack();
        }
        return false;
    }

    /**
     * Verifica se una mano specifica è un blackjack.
     *
     * @param handIndex Indice della mano
     * @return true se è un blackjack, false altrimenti
     */
    public boolean hasBlackjack(int handIndex) {
        if (handIndex >= 0 && handIndex < hands.size()) {
            return hands.get(handIndex).isBlackjack();
        }
        return false;
    }

    /**
     * Verifica se la prima mano è sballata.
     *
     * @return true se è sballata, false altrimenti
     */
    public boolean isBusted() {
        if (!hands.isEmpty()) {
            return hands.get(0).isBusted();
        }
        return false;
    }

    /**
     * Verifica se una mano specifica è sballata.
     *
     * @param handIndex Indice della mano
     * @return true se è sballata, false altrimenti
     */
    public boolean isBusted(int handIndex) {
        if (handIndex >= 0 && handIndex < hands.size()) {
            return hands.get(handIndex).isBusted();
        }
        return false;
    }

    /**
     * Restituisce il numero di mani del giocatore.
     *
     * @return Numero di mani
     */
    public int getHandCount() {
        return hands.size();
    }
}
