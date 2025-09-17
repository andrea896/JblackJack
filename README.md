# 🃏 JBlackJack

[![Java](https://img.shields.io/badge/Java-20-orange.svg)](https://openjdk.java.net/)
[![JavaFX](https://img.shields.io/badge/JavaFX-20.0.1-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.11.0-green.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Academic-yellow.svg)]()

Un'implementazione completa e moderna del gioco BlackJack sviluppata in Java con interfaccia grafica JavaFX. Progetto realizzato per il corso di **Metodologie di Programmazione** presso l'Università "La Sapienza" di Roma.

## Architettura

Il progetto implementa diversi **Design Pattern** fondamentali:

- **Model-View-Controller (MVC)**: Separazione netta tra logica di business, presentazione e controllo
- **Observer-Observable**: Comunicazione event-driven tra componenti
- **Strategy Pattern**: Strategie intercambiabili per i giocatori AI
- **Factory Pattern**: Creazione centralizzata di giocatori e oggetti di gioco
- **Singleton Pattern**: Gestione centralizzata di risorse condivise (AudioManager, CardImageService)

### 📁 Struttura del Progetto

```
src/main/java/
├── Controller/          # Logica di controllo e gestione eventi
│   ├── JBlackJack.java     # Classe principale
│   ├── MainController.java # Controller principale MVC
│   ├── ActionController.java # Gestione azioni di gioco
│   ├── AudioManager.java   # Sistema audio (Singleton)
│   └── ...
├── Model/              # Logica di business e dati
│   ├── Game/              # Logica del gioco
│   │   ├── GameModel.java
│   │   ├── TurnManager.java
│   │   └── Objects/       # Oggetti del dominio (Card, Deck, Hand)
│   ├── Players/           # Gestione giocatori
│   │   ├── Player.java
│   │   ├── AIPlayer.java
│   │   └── StrategyPlay/  # Strategie AI
│   └── Profile/           # Gestione profili utente
├── View/               # Interfaccia grafica
│   ├── BlackJackView.java  # Vista principale
│   ├── HandView.java       # Visualizzazione mani
│   ├── AnimationQueue.java # Sistema animazioni
│   └── ...
└── Utility/           # Utility e servizi
    └── LoggerUtility.java
```

## 🚀 Installazione e Avvio

### Prerequisiti

- **Java 20** o superiore
- **Maven 3.6+**
- **JavaFX 20** (incluso nelle dipendenze)

### Clonazione e Build

```bash
# Clona il repository
git clone https://github.com/andrea896/JBlackJack.git
cd JBlackJack

# Compila il progetto
mvn clean compile

# Avvia l'applicazione
mvn javafx:run
```

### Esecuzione Alternativa

```bash
# Genera il JAR eseguibile
mvn clean package

# Avvia con Java (assicurati di avere JavaFX nel module path)
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar target/JBlackJack-1.0-SNAPSHOT.jar
```

## 🎯 Come Giocare

1. **Crea/Carica Profilo**: Al primo avvio, crea un nuovo profilo o carica uno esistente
2. **Configura Partita**: Scegli numero di giocatori AI (1-3) e stile carte
3. **Piazza Scommessa**: Usa lo slider per selezionare l'importo
4. **Gioca**: Usa i pulsanti HIT, STAND, DOUBLE DOWN, SPLIT secondo la strategia
5. **Visualizza Risultati**: Controlla vincite/perdite e statistiche aggiornate

### 🎮 Controlli di Gioco

- **HIT**: Pesca una carta
- **STAND**: Ferma la mano corrente
- **DOUBLE DOWN**: Raddoppia la scommessa e pesca una sola carta
- **SPLIT**: Divide la mano se hai due carte uguali
- **INSURANCE**: Protezione contro il BlackJack del dealer (se disponibile)

## 🎨 Tecnologie Utilizzate

| Tecnologia | Versione | Utilizzo |
|------------|----------|----------|
| **Java** | 20 | Linguaggio principale |
| **JavaFX** | 20.0.1 | Framework GUI |
| **Maven** | 3.11.0 | Build system |
| **GSON** | 2.10.1 | Serializzazione JSON |
| **Java Sound API** | Built-in | Sistema audio |

## 📚 Documentazione

La documentazione JavaDoc completa è disponibile nella cartella `javadoc/`:

```bash
# Genera documentazione aggiornata
mvn javadoc:javadoc

# Apri la documentazione
open javadoc/index.html
```

## 📄 Licenza

Progetto accademico sviluppato per scopi educativi nell'ambito del corso di Metodologie di Programmazione presso l'Università "La Sapienza" di Roma.

## 👥 Autori

- **Andrea896** - *Sviluppatore principale* - Università "La Sapienza" di Roma

---
