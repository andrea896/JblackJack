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
    }

    /**
     * Effettua una scommessa detraendo l'importo dal saldo.
     *
     * @param amount Importo da scommettere
     * @return true se la scommessa è andata a buon fine, false altrimenti
     */
    public boolean placeBet(int amount, int currentHandIndex) {
        if (amount <= 0 || amount > balance) {
            return false;
        }

        balance -= amount;
        setCurrentBet(amount);
        hands.get(currentHandIndex).setBet(amount);

        return true;
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
     * Esegue un double down su una mano specifica.
     *
     * @param handIndex Indice della mano
     * @param card La carta da aggiungere
     * @return true se l'operazione è riuscita, false altrimenti
     */
    public boolean doubleDown(int handIndex, Card card) {
        if (!canDoubleDown(handIndex)) return false;

        Hand hand = hands.get(handIndex);
        // Raddoppia la scommessa sulla mano
        hand.doubleDown();

        // Aggiungi una carta
        addCard(handIndex, card);

        return true;
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
        currentBet += bet;

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
