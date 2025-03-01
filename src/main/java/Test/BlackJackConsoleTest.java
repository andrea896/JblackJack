package Test;

import Model.Game.GameModel;
import Model.Game.GameState;
import Model.Game.Objects.Card;
import Model.Players.Dealer;
import Model.Players.Player;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

/**
 * Classe per testare il gioco di Blackjack da console.
 * Implementa Observer per ricevere aggiornamenti dal GameModel.
 */
public class BlackJackConsoleTest implements Observer {
    private GameModel gameModel;
    private Scanner scanner;
    private boolean gameRunning;

    /**
     * Costruttore che inizializza il test.
     */
    public BlackJackConsoleTest() {
        scanner = new Scanner(System.in);
        gameRunning = true;
    }

    /**
     * Inizializza il gioco.
     */
    public void initGame() {
        System.out.println("=== BLACKJACK CONSOLE TEST ===");
        System.out.print("Inserisci il tuo nome: ");
        String playerName = scanner.nextLine();

        System.out.print("Inserisci il saldo iniziale: ");
        int initialBalance = Integer.parseInt(scanner.nextLine());

        System.out.print("Inserisci il numero di mazzi (1-8): ");
        int numDecks = Integer.parseInt(scanner.nextLine());

        System.out.print("Inserisci il numero di giocatori AI (0-3): ");
        int numAIPlayers = Integer.parseInt(scanner.nextLine());

        // Crea il modello di gioco
        gameModel = new GameModel(playerName, initialBalance, numDecks, numAIPlayers);
        gameModel.addObserver(this);

        System.out.println("\nGioco inizializzato! " + playerName + " inizia con $" + initialBalance);

        // Avvia il loop principale del gioco
        mainGameLoop();
    }

    /**
     * Loop principale del gioco.
     */
    private void mainGameLoop() {
        while (gameRunning) {
            if (gameModel.getGameState() == GameState.WAITING_FOR_PLAYERS) {
                startNewRound();
            } else if (gameModel.getGameState() == GameState.GAME_OVER) {
                askForNewRound();
            } else if (gameModel.getGameState() == GameState.PLAYER_TURN) {
                handlePlayerTurn();
            }

            try {
                Thread.sleep(300); // Piccola pausa per evitare un utilizzo eccessivo della CPU
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\nGrazie per aver giocato a Blackjack!");
        scanner.close();
    }

    /**
     * Avvia un nuovo round.
     */
    private void startNewRound() {
        Player player = gameModel.getHumanPlayer();
        System.out.println("\n=== NUOVO ROUND ===");
        System.out.println("Saldo attuale: $" + player.getBalance());

        if (player.getBalance() <= 0) {
            System.out.println("Hai finito i soldi! Game over.");
            gameRunning = false;
            return;
        }

        System.out.print("Quanto vuoi scommettere? (1-" + player.getBalance() + "): ");
        int bet = Integer.parseInt(scanner.nextLine());

        if (bet <= 0 || bet > player.getBalance()) {
            System.out.println("Scommessa non valida. Riprova.");
            return;
        }

        if (gameModel.startGame(bet)) {
            System.out.println("Round iniziato! Scommessa: $" + bet);
            displayGameState();

            if (gameModel.getGameState() == GameState.PLAYER_TURN) {
                handlePlayerTurn();
            }
        } else {
            System.out.println("Impossibile avviare il round. Riprova.");
        }
    }

    /**
     * Gestisce il turno del giocatore.
     */
    private void handlePlayerTurn() {
        Player player = gameModel.getHumanPlayer();
        boolean turnActive = true;

        while (turnActive && gameModel.getGameState() == GameState.PLAYER_TURN) {
            // Se il giocatore ha più mani, mostra quale mano sta giocando
            if (player.getHandCount() > 1) {
                System.out.println("\nMano corrente (" + (gameModel.getCurrentHandIndex() + 1) + "/" +
                        player.getHandCount() + ")");
            }

            System.out.println("\nCosa vuoi fare?");
            System.out.println("1. Hit (Chiedi carta)");
            System.out.println("2. Stand (Stai)");

            // Opzioni aggiuntive disponibili solo in alcuni casi
            if (player.getHandCount() == 1 || (player.getHandCount() > 1 &&
                    gameModel.getCurrentHandIndex() < player.getHandCount())) {

                int handIndex = player.getHandCount() > 1 ? gameModel.getCurrentHandIndex() : 0;

                if (player.canDoubleDown(handIndex)) {
                    System.out.println("3. Double Down (Raddoppia)");
                }

                if (player.canSplit(handIndex)) {
                    System.out.println("4. Split (Dividi)");
                }

                if (gameModel.getDealer().getHand().get(0).isAce() && !player.hasInsurance()) {
                    System.out.println("5. Insurance (Assicurazione)");
                }
            }

            System.out.print("Scelta: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    gameModel.playerHit();
                    displayGameState();
                    break;
                case "2":
                    gameModel.playerStand();
                    turnActive = false;
                    break;
                case "3":
                    if ((player.canDoubleDown(gameModel.getCurrentHandIndex()))) {
                        gameModel.doubleDown();
                        displayGameState();
                        turnActive = false;
                    } else {
                        System.out.println("Double Down non disponibile.");
                    }
                    break;
                case "4":
                    if (player.canSplit(gameModel.getCurrentHandIndex())) {
                        gameModel.splitHand();
                        displayGameState();
                    } else {
                        System.out.println("Split non disponibile.");
                    }
                    break;
                case "5":
                    if (gameModel.getDealer().getHand().get(0).isAce() && !player.hasInsurance()) {
                        gameModel.takeInsurance();
                        displayGameState();
                    } else {
                        System.out.println("Insurance non disponibile.");
                    }
                    break;
                default:
                    System.out.println("Scelta non valida. Riprova.");
                    break;
            }
        }
    }

    /**
     * Chiede al giocatore se vuole iniziare un nuovo round.
     */
    private void askForNewRound() {
        Player player = gameModel.getHumanPlayer();
        System.out.println("\nRound terminato!");
        System.out.println("Saldo attuale: $" + player.getBalance());

        System.out.print("Vuoi giocare un altro round? (s/n): ");
        String choice = scanner.nextLine().toLowerCase();

        if (choice.equals("s") || choice.equals("si") || choice.equals("sì")) {
            gameModel = new GameModel(player.getName(), player.getBalance(), 1, 1);
            gameModel.addObserver(this);
        } else {
            gameRunning = false;
        }
    }

    /**
     * Visualizza lo stato corrente del gioco.
     */
    private void displayGameState() {
        System.out.println("\n=== STATO DEL GIOCO ===");

        // Mostra le carte del dealer
        Dealer dealer = gameModel.getDealer();
        System.out.print("Dealer: ");

        if (gameModel.getGameState() == GameState.PLAYER_TURN) {
            // Durante il turno del giocatore, mostra solo la prima carta del dealer
            if (dealer.getHand() == null || dealer.getHand().isEmpty()) {
                System.out.println("[Nessuna carta]");
            } else if (gameModel.getGameState() == GameState.PLAYER_TURN) {
                // Durante il turno del giocatore, mostra solo la prima carta del dealer
                System.out.print(dealer.getHand().get(0) + ", [Carta coperta]");
                System.out.println(" (Valore mostrato: " + dealer.getHand().get(0).getValue() + ")");
            } else {
                // Dopo il turno del giocatore, mostra tutte le carte del dealer
                displayCards(dealer.getHand());
                System.out.println(" (Valore: " + dealer.getHandValue() + ")");
            }
        } else {
            // Dopo il turno del giocatore, mostra tutte le carte del dealer
            displayCards(dealer.getHand());
            System.out.println(" (Valore: " + dealer.getHandValue() + ")");
        }

        // Mostra le carte dei giocatori AI
        for (Player aiPlayer : gameModel.getPlayers()) {
            System.out.print(aiPlayer.getName() + ": ");

            for (int i = 0; i < aiPlayer.getHandCount(); i++) {
                if (aiPlayer.getHandCount() > 1) {
                    System.out.print("Mano " + (i + 1) + ": ");
                }

                displayCards(aiPlayer.getHand(i));
                System.out.print(" (Valore: " + aiPlayer.getHandValue(i) + ")");

                if (aiPlayer.isBusted(i)) {
                    System.out.print(" - SBALLATO!");
                } else if (aiPlayer.hasBlackjack(i)) {
                    System.out.print(" - BLACKJACK!");
                }

                if (i < aiPlayer.getHandCount() - 1) {
                    System.out.println();
                }
            }
            System.out.println();
        }

        // Mostra le carte del giocatore umano
        Player humanPlayer = gameModel.getHumanPlayer();

        for (int i = 0; i < humanPlayer.getHandCount(); i++) {
            if (humanPlayer.getHandCount() > 1) {
                System.out.print("La tua mano " + (i + 1) + ": ");

                // Evidenzia la mano corrente
                if (gameModel.getGameState() == GameState.PLAYER_TURN &&
                        i == gameModel.getCurrentHandIndex()) {
                    System.out.print("* ");
                }
            } else {
                System.out.print("La tua mano: ");
            }

            displayCards(humanPlayer.getHand(i));
            System.out.print(" (Valore: " + humanPlayer.getHandValue(i) + ")");

            if (humanPlayer.isBusted(i)) {
                System.out.print(" - SBALLATO!");
            } else if (humanPlayer.hasBlackjack(i)) {
                System.out.print(" - BLACKJACK!");
            }

            if (i < humanPlayer.getHandCount() - 1) {
                System.out.println();
            }
        }

        System.out.println("\nSaldo: $" + humanPlayer.getBalance() +
                ", Scommessa: $" + gameModel.getCurrentBet());

        if (humanPlayer.hasInsurance()) {
            System.out.println("Assicurazione attiva: $" + (gameModel.getCurrentBet() / 2));
        }

        // Mostra lo stato del gioco
        System.out.println("Stato del gioco: " + gameModel.getGameState());
    }

    /**
     * Visualizza le carte di una mano.
     *
     * @param cards Lista di carte da visualizzare
     */
    private void displayCards(List<Card> cards) {
        for (int i = 0; i < cards.size(); i++) {
            System.out.print(cards.get(i));
            if (i < cards.size() - 1) {
                System.out.print(", ");
            }
        }
    }

    /**
     * Riceve aggiornamenti dal GameModel.
     *
     * @param o Observable che ha notificato l'aggiornamento
     * @param arg Argomento dell'aggiornamento (non utilizzato)
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof GameModel) {
            GameState state = gameModel.getGameState();

            if (state == GameState.AI_PLAYER_TURN) {
                System.out.println("\nTurno dei giocatori AI in corso...");
            } else if (state == GameState.DEALER_TURN) {
                System.out.println("\nTurno del dealer in corso...");
                displayGameState();
            } else if (state == GameState.GAME_OVER) {
                System.out.println("\nRound terminato!");
                displayGameState();
                displayRoundResults();
            }
        }
    }

    /**
     * Visualizza i risultati del round, inclusi vittorie, sconfitte e pareggi.
     */
    private void displayRoundResults() {
        Player player = gameModel.getHumanPlayer();
        Dealer dealer = gameModel.getDealer();
        int dealerValue = dealer.getHandValue();
        boolean dealerBusted = dealerValue > 21;

        System.out.println("\n=== RISULTATI DEL ROUND ===");

        // Elabora ogni mano del giocatore
        for (int i = 0; i < player.getHandCount(); i++) {
            int handValue = player.getHandValue(i);
            boolean handBusted = player.isBusted(i);
            boolean handBlackjack = player.hasBlackjack(i);

            // Prefisso per distinguere le mani in caso di split
            String handPrefix = player.getHandCount() > 1 ? "Mano " + (i + 1) + ": " : "";

            if (handBusted) {
                System.out.println(handPrefix + "Hai sballato con " + handValue + ". Perdi $" + gameModel.getCurrentBet() + ".");
            } else if (dealerBusted) {
                System.out.println(handPrefix + "Il dealer ha sballato! Vinci $" + gameModel.getCurrentBet() + " con la tua mano di " + handValue + ".");
            } else if (handBlackjack && !dealer.hasBlackjack(0)) {
                int winnings = (int)(gameModel.getCurrentBet() * 1.5);
                System.out.println(handPrefix + "Blackjack! Vinci $" + winnings + " con un pagamento 3:2.");
            } else if (handValue > dealerValue) {
                System.out.println(handPrefix + "Il tuo " + handValue + " batte il " + dealerValue + " del dealer. Vinci $" + gameModel.getCurrentBet() + ".");
            } else if (handValue < dealerValue) {
                System.out.println(handPrefix + "Il tuo " + handValue + " è inferiore al " + dealerValue + " del dealer. Perdi $" + gameModel.getCurrentBet() + ".");
            } else {
                System.out.println(handPrefix + "Pareggio con " + handValue + ". La tua scommessa di $" + gameModel.getCurrentBet() + " ti viene restituita.");
            }
        }

        // Gestione dell'assicurazione
        if (player.hasInsurance()) {
            if (dealer.hasBlackjack(0)) {
                System.out.println("La tua assicurazione è stata pagata con un rapporto 2:1.");
            } else {
                System.out.println("La tua assicurazione è stata persa.");
            }
        }

        System.out.println("\nSaldo attuale: $" + player.getBalance());
    }

    /**
     * Metodo main per eseguire il test.
     *
     * @param args Argomenti da linea di comando (non utilizzati)
     */
    public static void main(String[] args) {
        BlackJackConsoleTest test = new BlackJackConsoleTest();
        test.initGame();
    }
}
