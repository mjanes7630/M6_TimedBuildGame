import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.*;

public class TimedBuildGame
{   
   
   
   public static void main(String[] args)
   {
      GameModel model = new GameModel();
      GameView view = new GameView();
      GameController controller = new GameController(model, view);
      GameTimer gameTimer = new GameTimer(controller);
      controller.init(gameTimer);
      gameTimer.start();
   }
}

class GameTimer extends Thread {
   private Timer gameTimer;
   GameController gameController;
   
   // Construct class with controller
   public GameTimer (GameController gameController) {
      this.gameController = gameController;
   }

   // Thread run() method override
   public void run() {
      gameTimer = new Timer(1000, new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            gameController.updateTimer();
            doNothing(10);
         }
      });
   }
   
   void startTimer() {
      gameTimer.start();
   }
   
   void stopTimer() {
      gameTimer.stop();
   }
   
   public void doNothing(int milliseconds) {
      try {
         Thread.sleep(milliseconds);
      }
      catch(InterruptedException e) {
         System.out.println("Unexpected interrupt");
      }
   }
}

// ********************** VIEW ************************************************
class GameView
{
   JLabel[] computerLabels = new JLabel[GameModel.NUM_CARDS_PER_HAND];
   JLabel[] humanLabels = new JLabel[GameModel.NUM_CARDS_PER_HAND];
   JLabel[] playedCardLabels = new JLabel[GameModel.NUM_PLAYERS];
   JLabel[] playLabelText = new JLabel[GameModel.NUM_PLAYERS];
   JLabel[] computerCardBacks= new JLabel[GameModel.NUM_CARDS_PER_HAND];
   JLabel[] spacerBackCards = new JLabel[GameModel.NUM_PLAYERS];
   GameTimer gameTimer;
   JLabel timer;
   
   JButton stopButton = new JButton("Stop");
   JButton startButton = new JButton("Start");
   
   public void addStartListener(ActionListener startListener)
   {
      startButton.addActionListener(startListener);
      
   }
   
   public void addStopListener(ActionListener stopListener)
   {
      stopButton.addActionListener(stopListener);
      
   }
   
   static int playerScore = 0;
   static int compScore = 0;
   static int counter = 0;
   int rounds =  GameModel.NUM_CARDS_PER_HAND;
   static boolean playFlag = false;
   
   // establish main frame in which program will run
   CardTable myCardTable =
         new CardTable("CardTable",
               GameModel.NUM_CARDS_PER_HAND, GameModel.NUM_PLAYERS);
   
   
   public void init( GameModel.CardGameFramework lowCardGame)
   {
      myCardTable.setSize(800, 600);
      myCardTable.setLocationRelativeTo(null);
      myCardTable.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      // create labels
      playLabelText[0] = new JLabel("Computer", JLabel.CENTER);
      playLabelText[1] = new JLabel("Player 1", JLabel.CENTER);
      
      
      timer = new JLabel("00:00",JLabel.CENTER);
      myCardTable.pnlTimer.add(timer);
      myCardTable.pnlTimer.add(startButton);
      myCardTable.pnlTimer.add(stopButton);
    
      // Start with the Player
      JButton playButton = new JButton();
      playButton.setText("Play");
      myCardTable.pnlHumanHand.add(playButton);
      for(int i = 0; i < GameModel.NUM_CARDS_PER_HAND; i++) 
      {
         // Instance of the CardGameFramework instead of LowCardGame 
         humanLabels[i] = new JLabel(GUICard.getIcon(lowCardGame.getHand(1)
               .inspectCard(i)));
         // add labels to panels
         myCardTable.pnlHumanHand.add(humanLabels[i]);
      }
      
      playButton.addActionListener(new ActionListener() 
      {
         public void actionPerformed(ActionEvent e) 
         {     
            rounds--;
            
            if(!playFlag) 
            {
               myCardTable.pnlPlayArea.remove(spacerBackCards[0]);
               myCardTable.pnlPlayArea.add(computerLabels[rounds]);
               myCardTable.pnlComputerHand.remove(computerCardBacks[rounds]);
               myCardTable.pnlPlayArea.remove(spacerBackCards[1]);
               myCardTable.pnlPlayArea.add(humanLabels[rounds]);
               myCardTable.pnlPlayArea.add(playLabelText[0]);
               myCardTable.pnlPlayArea.add(playLabelText[1]);
               myCardTable.pnlHumanHand.remove(humanLabels[rounds]);
            }   
            
            if(playFlag && rounds >= 0) 
            {
               myCardTable.pnlPlayArea.remove(computerLabels[rounds+1]);
               myCardTable.pnlPlayArea.add(computerLabels[rounds]);
               myCardTable.pnlComputerHand.remove(computerCardBacks[rounds]);
               myCardTable.pnlPlayArea.remove(humanLabels[rounds + 1]);
               myCardTable.pnlPlayArea.add(humanLabels[rounds]);
               myCardTable.pnlPlayArea.add(playLabelText[0]);
               myCardTable.pnlPlayArea.add(playLabelText[1]);
               myCardTable.pnlHumanHand.remove(humanLabels[rounds]);
            }  
            
            playFlag = true;
            
            if(rounds >= 0) {
          
               if(GUICard.valueAsInt(lowCardGame.getHand(1).
                     inspectCard(rounds)) >
               GUICard.valueAsInt(lowCardGame.getHand(0).
                     inspectCard(rounds))) 
               {
                  playerScore++;
               }
               
               if(GUICard.valueAsInt(lowCardGame.getHand(1).
                     inspectCard(rounds)) <
               GUICard.valueAsInt(lowCardGame.getHand(0).
                     inspectCard(rounds)))
               {
                  compScore++;
               }
            }
            
            if(rounds == 0)
            {
               playButton.setVisible(false);
               myCardTable.pnlPlayArea.remove(computerLabels[rounds+1]);
               myCardTable.pnlPlayArea.remove(humanLabels[rounds + 1]);
               if(compScore > playerScore) 
               {
                  myCardTable.pnlPlayArea.add(new JLabel(Integer.
                        toString(compScore) + " Points Scored: Winner", JLabel.CENTER));
                  myCardTable.pnlPlayArea.add(new JLabel(Integer.
                        toString(playerScore) + " Points Scored", JLabel.CENTER));
               }
               if(compScore < playerScore) 
               {
                  myCardTable.pnlPlayArea.add(new JLabel(Integer.
                        toString(compScore) + " Points Scored", JLabel.CENTER));
                  myCardTable.pnlPlayArea.add(new JLabel(Integer.
                        toString(playerScore) + " Points Scored: Winner", JLabel.CENTER));
               }
               if(compScore == playerScore)
               {
                  myCardTable.pnlPlayArea.add(new JLabel(Integer.
                        toString(compScore) + " Points Scored: Tie", JLabel.CENTER));
                  myCardTable.pnlPlayArea.add(new JLabel(Integer.
                        toString(playerScore) + " Points Scored: Tie", JLabel.CENTER));
               }
            }

            myCardTable.repaint();
            myCardTable.setVisible(true);
         } 
      });
      
      
      
      // Next is the Computer
      for (int i = 0; i < GameModel.NUM_CARDS_PER_HAND; i++) {
         computerLabels[i] = (new JLabel(GUICard.getIcon(lowCardGame.getHand(0).
                                                         inspectCard(i))));
         computerCardBacks[i] = new JLabel(GUICard.getBackCardIcon());
         myCardTable.pnlComputerHand.add(computerCardBacks[i]);
      }
      // Add two spacer card backs
      spacerBackCards[0] = new JLabel(GUICard.getBackCardIcon());
      spacerBackCards[1] = new JLabel(GUICard.getBackCardIcon());
      myCardTable.pnlPlayArea.add(spacerBackCards[0]);
      myCardTable.pnlPlayArea.add(spacerBackCards[1]);
      myCardTable.pnlPlayArea.add(playLabelText[0]);
      myCardTable.pnlPlayArea.add(playLabelText[1]);
      
      // show everything to the user
      myCardTable.setVisible(true);
   }
   
   // class GUICard ***************************
   static class GUICard {
      
      private static Icon[][] iconCards = new ImageIcon[14][4];
      private static Icon iconBack;
      static boolean iconsLoaded = false;
      
      static void loadCardIcons() {
         String imageDirectory = "images/";
         String imageExtension =".gif";
         
         // Check to see if this has been done already
         if(iconsLoaded) { return; }
         
         for(int suit = 0; suit < 4; suit++) {
            for(int value = 0; value < 14; value++) {
               String imageFile = imageDirectory.
                     concat(turnIntIntoCardValue(value)).
                     concat(turnIntIntoCardSuit(suit)).
                     concat(imageExtension);;
               iconCards[value][suit] = new ImageIcon(imageFile);
            }
         }
         
         // The card back is also an icon so we should load this as well.
         iconBack = new ImageIcon(imageDirectory.
                                  concat("BK").
                                  concat(imageExtension));
         // Set a verification that this has been run
         iconsLoaded = true;
      }
      
      static public Icon getIcon(GameModel.Card card) {
         // This is an opportunity to load the card icons
         loadCardIcons();
         return iconCards[valueAsInt(card)][suitAsInt(card)];
      }
      
      static public Icon getBackCardIcon() {
         // This is an opportunity to load the card icons
         loadCardIcons();
         return iconBack;
      }
      
      static int suitAsInt(GameModel.Card card)
      {
         GameModel.Card.Suit suit = card.getSuit();
         
         switch(suit) {
         case clubs:
            return 0;
         case diamonds:
            return 1;
         case hearts:
            return 2;
         case spades:
            return 3;
         default:
            return -999; // The default case should never be met
         }
      }

      static int valueAsInt(GameModel.Card card)
      {
         char value = card.getValue();
         switch(value) {
         case 'A':
            return 0;
         case 'T':
            return 9;
         case 'J':
            return 10;
         case 'Q':
            return 11;
         case 'K':
            return 12;
         case 'X':
            return 13;
         default:
            return Integer.parseInt(String.valueOf(value));
         }
      }

      // Private Helper Methods
      // turns 0 - 13 into "A", "2", "3", ... "Q", "K", "X"
      private static String turnIntIntoCardValue(int valueInt)
      {
         // Need to increment all values up by one
         valueInt++;
         switch(valueInt) {
         case 1:
            return "A";
         case 10:
            return "T";
         case 11:
            return "J";
         case 12:
            return "Q";
         case 13:
            return "K";
         case 14:
            return "X";
         default:
            return Integer.toString(valueInt);
         }
      }
      
      // turns 0 - 3 into "C", "D", "H", "S"
      private static String turnIntIntoCardSuit(int suitInt)
      {
         switch(suitInt)
         {
         case 0:
            return "C";
         case 1:
            return "D";
         case 2:
            return "H";
         case 3:
            return "S";
         default:
            return null;
         }
      }      
   }
   
   // class CardTable *************************
   @SuppressWarnings("serial")
   static class CardTable extends JFrame {
      static int MAX_CARDS_PER_HAND = 56;
      static int MAX_PLAYERS = 2; 

      private int numCardsPerHand;
      private int numPlayers;

      public JPanel pnlComputerHand, pnlHumanHand, pnlPlayArea, 
                    pnlTimer, pnlControls;
      
      // Constructor
      CardTable(String title, int numCardsPerHand, int numPlayers) {
         /*
          * Instantiate the JFrame super class with its own parameterized
          * constructor passing in the title
          */
         super(title);
         
         /*
          * Filter inputs
          */
         if (numCardsPerHand < 1 || numCardsPerHand > MAX_CARDS_PER_HAND) {
            int randomInt = new Random().nextInt(MAX_CARDS_PER_HAND) + 1;
            // Pick some random amount of cards between 1 and MAX_CARDS_PER_HAND
            this.numCardsPerHand = randomInt;
         }
         else {
            this.numCardsPerHand = numCardsPerHand;
         }
         
         if (numPlayers < 2 || numPlayers > MAX_PLAYERS) {
            /*
             * We will assume the minimum amount of players is two but will not 
             * assume the max players will always be two. Thus, we will set the
             * filter to the minimum players of 2. JPanels will only display 2.
             */
            this.numPlayers = 2;
         }
         else {
            this.numPlayers = numPlayers;
         }
         // Setup a border layout
         setLayout(new BorderLayout());
         /*
          * Setup of the Public JPanels, give them a border with a title, then
          * add them to their appropriate boarder location
          */
         // Top Computer Hand 
         pnlComputerHand = new JPanel(new GridLayout(1,numCardsPerHand));
         pnlComputerHand.setBorder(new TitledBorder("Computer Hand"));
         pnlComputerHand.setPreferredSize(new Dimension(800,120));
         pnlComputerHand.setMinimumSize(new Dimension(800, 120));
         add(pnlComputerHand, BorderLayout.NORTH);
         
         // Middle Playing Area
         pnlPlayArea = new JPanel(new GridLayout(3,2));
         pnlPlayArea.setBorder(new TitledBorder("Playing Area"));
         pnlPlayArea.setPreferredSize(new Dimension(600, 600));
         pnlPlayArea.setMinimumSize(new Dimension(600, 600));
         add(pnlPlayArea, BorderLayout.CENTER);
         
         // Timer Section
         //pnlTimer = new JPanel(new GridLayout(3, 1));
         pnlTimer = new JPanel(new FlowLayout(FlowLayout.TRAILING));
         pnlTimer.setBorder(new TitledBorder("Game Clock"));
         pnlTimer.setPreferredSize(new Dimension(150, 600));
         pnlTimer.setMinimumSize(new Dimension(150, 600));
         add(pnlTimer, BorderLayout.EAST);
         
         
         JButton playButton = new JButton();
         playButton.setText("Play");
         // Bottom Human Player Hand
         //pnlHumanHand = new JPanel(new GridLayout(1,numCardsPerHand + 1));
         pnlHumanHand = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
         pnlHumanHand.setBorder(new TitledBorder("Your Hand"));
         pnlHumanHand.setPreferredSize(new Dimension(600,120));
         pnlHumanHand.setMinimumSize(new Dimension(600, 120));
         add(pnlHumanHand, BorderLayout.SOUTH);
         
      }

      // Accessors (Getters)
      /**
       * @return the number of cards per hand
       */
      int getNumCardsPerHand() { return numCardsPerHand; }

      /**
       * @return the number of players
       */
      int getNumPlayers() { return numPlayers; }
   }

   public void updateTimer() {
      counter++;
      timer.setText(String.format("%02d:%02d",counter / 60, (counter % (24*3600))%60));
   }
}

// ********************** MODEL ***********************************************
class GameModel
{
   static int NUM_CARDS_PER_HAND = 7;
   static int NUM_PLAYERS = 2;  
   
   // Low-Card Game **************************
   // Start with CardGameFramework per instructor
   int numPacksPerDeck = 1;
   int numJokersPerPack = 2;
   int numUnusedCardsPerPack = 0;
   Card[] unusedCardsPerPack = null;
   
   CardGameFramework lowCardGame = new CardGameFramework( 
         numPacksPerDeck, numJokersPerPack,  
         numUnusedCardsPerPack, unusedCardsPerPack, 
         NUM_PLAYERS, NUM_CARDS_PER_HAND);
   
   public CardGameFramework getFramework(){ return lowCardGame; }
   
   // class Card *****************************
   static class Card
   {
      // enum storing card suits (Method made static)
      public enum Suit
      {
         clubs, diamonds, hearts, spades
      }

      // Private Statics for Card Class
      private static final char DEFAULT_VALUE = 'A';
      private static final Suit DEFAULT_SUIT = Suit.spades;

      // Private Card class members
      private char value;
      private Suit suit;
      private boolean errorFlag;
      
      public static char[] valueRanks;
      
      

      /**
       * Card Constructor with no parameters
       */
      public Card() { this(DEFAULT_VALUE, DEFAULT_SUIT); }

      // Constructor with both parameters
      public Card(char value, Suit suit) { set(value, suit); }

      // Accessors and Mutators (getters and setters)
      public char getValue() { return value; }

      public Suit getSuit() { return suit; }

      public boolean getErrorFlag() { return errorFlag; }

      public boolean set(char value, Suit suit)
      {
         // Set value to upperCase
         char upperValue = Character.toUpperCase(value);

         // Check validity of the value and update members and errorFlag
         if (isValid(upperValue, suit))
         {
            this.value = upperValue;
            this.suit = suit;
            errorFlag = false;
            return true;
         }
         else
         {
            this.value = upperValue;
            this.suit = suit;
            errorFlag = true;
            return false;
         }
      }

      @Override
      public String toString()
      {
         if (errorFlag)
            return "** illegal **";
         else
            return value + " of " + suit;
      }

      // Check if passed card is equal to current card
      public boolean equals(Card card)
      {
         return getValue() == card.getValue() && getSuit() == card.getSuit();
      }

      // Private Methods
      private boolean isValid(char value, Suit suit)
      {
         // Although suit is passed it is not checked

         // Return true if value is a valid value else false (added 'Q')
         return ((value >= '2' && value <= '9') || value == 'A' || value == 'K'
               || value == 'Q' || value == 'J' || value == 'T' || value == 'X');
      }

      /**
       * sorts arraySize using bubble sort method
       * @param cards - array of cards to be sorted
       * @param ararySize - length of the array being sorted?
       */
      static void arraySort(Card[] cards, int arraySize) {
         
         for(int i = 0; i < arraySize - 1; i++){
            
            for(int j = 0; i < arraySize - i - 1; j++) {
               Card currentCard = cards[j];
               Card nextCard = cards[j+1];            
               
               // if next card is lower, swap
               if(Character
                     .compare(currentCard.getValue(),
                           nextCard.getValue()) > 1) {
                  // create a copy, reference will be overwritten
                  Card tempCard = 
                        new Card(currentCard.getValue(), currentCard.getSuit());
                  currentCard = new Card(nextCard.getValue(), nextCard.getSuit());
                  nextCard = new Card(tempCard.getValue(), nextCard.getSuit());
               }
               
            }// end inner j loop         
            
         }// end outer i loop
         
      }
      
   }// end Card class
   
   // class Deck *****************************
   static class Deck
   {   
      public final int MAX_CARDS = 6*(52+4);
      private static Card[] masterPack = new Card[52 + 4]; // max deck +4 jokers
      private Card[] cards;
      private int topCard;

      // Constructors
      public Deck(int numPacks)
      {
         cards = new Card[MAX_CARDS];
         allocateMasterPack();
         init(numPacks);
      }

      // Re-populates the cards[] with new cards. Resetting it to its original,
      // un-shuffled state.
      // Methods
      public void init(int numPacks)
      {
         // If numPacks is less than one initialize one pack
         if (numPacks < 1) {
            numPacks = 1;
         }
         
         for (int i = 0; i < numPacks; i++) {
            for (int j = 0; j < masterPack.length; j++) {
               cards[(i*masterPack.length)+j] = masterPack[j];
            }
         }
         topCard = numPacks * masterPack.length - 1;
      }

      // Shuffles the cards[] by iterating through the cards[] and placing the
      // card in each index in a random index.
      public void shuffle()
      {
         Random rand = new Random();
         int num;
         Card tempCard;

         for (int i = 0; i < topCard; i++)
         {
            num = rand.nextInt(52);
            tempCard = cards[num];
            cards[num] = cards[i];
            cards[i] = tempCard;
         }
      }

      // Returns the card at the 'topCard' position of the deck and simulates
      // the removal from the deck by incrementing topCard by 1. Also, returns
      // null if there are no more cards left.
      public Card dealCard() {
         
         if (topCard < 0) { return new Card('*', Card.Suit.hearts); }
         else { return cards[topCard--]; }      
      }

      public int getTopCard(){ return topCard; }

      // Returns the card in index k of the cards[]. If cards[k] is out of
      // bounds, then it returns a card with errorFlag set to true.
      public Card inspectCard(int k)
      {
         try { return cards[k]; }
         catch (Exception e) { return new Card('f', Card.Suit.spades); }
      }

      // Populates the masterPack[] with cards.
      private static void allocateMasterPack()
      {
         // Checks to see if masterPack[] has already been filled
         if (masterPack[0] != null) { return; }

         // These four blocks fill the masterPack with Cards
         masterPack[0] = new Card('K', Card.Suit.spades);
         masterPack[1] = new Card('Q', Card.Suit.spades);
         masterPack[2] = new Card('J', Card.Suit.spades);
         masterPack[3] = new Card('T', Card.Suit.spades);
         masterPack[4] = new Card('9', Card.Suit.spades);
         masterPack[5] = new Card('8', Card.Suit.spades);
         masterPack[6] = new Card('7', Card.Suit.spades);
         masterPack[7] = new Card('6', Card.Suit.spades);
         masterPack[8] = new Card('5', Card.Suit.spades);
         masterPack[9] = new Card('4', Card.Suit.spades);
         masterPack[10] = new Card('3', Card.Suit.spades);
         masterPack[11] = new Card('2', Card.Suit.spades);
         masterPack[12] = new Card('A', Card.Suit.spades);
         masterPack[13] = new Card('X', Card.Suit.spades);

         masterPack[14] = new Card('K', Card.Suit.hearts);
         masterPack[15] = new Card('Q', Card.Suit.hearts);
         masterPack[16] = new Card('J', Card.Suit.hearts);
         masterPack[17] = new Card('T', Card.Suit.hearts);
         masterPack[18] = new Card('9', Card.Suit.hearts);
         masterPack[19] = new Card('8', Card.Suit.hearts);
         masterPack[20] = new Card('7', Card.Suit.hearts);
         masterPack[21] = new Card('6', Card.Suit.hearts);
         masterPack[22] = new Card('5', Card.Suit.hearts);
         masterPack[23] = new Card('4', Card.Suit.hearts);
         masterPack[24] = new Card('3', Card.Suit.hearts);
         masterPack[25] = new Card('2', Card.Suit.hearts);
         masterPack[26] = new Card('A', Card.Suit.hearts);
         masterPack[27] = new Card('X', Card.Suit.hearts);

         masterPack[28] = new Card('K', Card.Suit.diamonds);
         masterPack[29] = new Card('Q', Card.Suit.diamonds);
         masterPack[30] = new Card('J', Card.Suit.diamonds);
         masterPack[31] = new Card('T', Card.Suit.diamonds);
         masterPack[32] = new Card('9', Card.Suit.diamonds);
         masterPack[33] = new Card('8', Card.Suit.diamonds);
         masterPack[34] = new Card('7', Card.Suit.diamonds);
         masterPack[35] = new Card('6', Card.Suit.diamonds);
         masterPack[36] = new Card('5', Card.Suit.diamonds);
         masterPack[37] = new Card('4', Card.Suit.diamonds);
         masterPack[38] = new Card('3', Card.Suit.diamonds);
         masterPack[39] = new Card('2', Card.Suit.diamonds);
         masterPack[40] = new Card('A', Card.Suit.diamonds);
         masterPack[41] = new Card('X', Card.Suit.diamonds);

         masterPack[42] = new Card('K', Card.Suit.clubs);
         masterPack[43] = new Card('Q', Card.Suit.clubs);
         masterPack[44] = new Card('J', Card.Suit.clubs);
         masterPack[45] = new Card('T', Card.Suit.clubs);
         masterPack[46] = new Card('9', Card.Suit.clubs);
         masterPack[47] = new Card('8', Card.Suit.clubs);
         masterPack[48] = new Card('7', Card.Suit.clubs);
         masterPack[49] = new Card('6', Card.Suit.clubs);
         masterPack[50] = new Card('5', Card.Suit.clubs);
         masterPack[51] = new Card('4', Card.Suit.clubs);
         masterPack[52] = new Card('3', Card.Suit.clubs);
         masterPack[53] = new Card('2', Card.Suit.clubs);
         masterPack[54] = new Card('A', Card.Suit.clubs);
         masterPack[55] = new Card('X', Card.Suit.clubs);
      }

      /**
       * check if the card exists in the deck
       * if possible, put the card on top of the deck
       * return false if impossible or card exists
       * @param card - card to be added to the deck
       * @return - true if addCard was successful false otherwise
       */
      boolean addCard (Card card) {
         for(int i = 0; i < cards.length; i++) {
            if(Character.compare(cards[i].getValue(), card.getValue()) == 0 && 
                  cards[i].getSuit() == card.getSuit()) {
               return false; // card exists
            }
         }
         
         // add card to top, increment topCard
         cards[topCard] = new Card(card.getValue(), card.getSuit());
         topCard++;
         return true;
      }

      /**
       * remove a specific card from the deck
       * put the current top card into its place
       * return false if the card doesn't exist
       * @param card - card to be removed
       * @return - true if removed from the deck, flase otherwise
       */
      boolean removeCard(Card card) {
         for(int i = 0; i < cards.length; i++) {
            if(Character.compare(cards[i].getValue(), card.getValue()) == 0 
                  && cards[i].getSuit() == cards[i].getSuit()) {
               // card exists remove it, and put top card there.
               cards[i] = new Card(cards[topCard].getValue(),
                     cards[topCard].getSuit());
               cards[topCard] = null;
               topCard--;
               return true;
            }
         }
         
         // card doesn't exist
         return false;
      }
      
      /**
       * put all of the cards in the deck back into the right order
       */
      void sort() {
         Card.arraySort(masterPack, masterPack.length);
      }
      
      /**
       * return the number of cards remaining in the deck
       * @return
       */
      public int getNumCards() {
         return topCard;
      }
      
   }// end Deck Class
   
   // class Hand *****************************
   class Hand
   {
      // max number of cards in a deck, 1 person hand
      public static final int MAX_CARDS = 100; // add 4 jokers

      private Card[] myCards;
      private int numCards; 
      
      public int getNumCards() { return numCards; }

      public Hand() {
         myCards = new Card[MAX_CARDS];
         resetHand();
      }

      /**
       * removes all cards from the hand (simplest way possible)
       */
      public void resetHand() { numCards = 0; }

      /**
       * adds a card to the next available position in myCards provides object
       * copy not a reference copy
       * 
       * @param card - the card to be stored in the array
       * @return - t/f if card take was successful
       */
      public boolean takeCard(Card card) {
         // case for not adding a cards
         if (numCards >= MAX_CARDS || myCards[numCards] != null) {return false;}
         else {
            myCards[numCards] = new Card();
            myCards[numCards].set(card.getValue(), card.getSuit());
            numCards++;
            return true;
         }         
      }

      /**
       * returns & removes the top occupied position of the array (last element)
       * 
       * @return - the card that was removed from the myCards
       */
      public Card playCard()
      {
         // what if there are no cards to play, for now return new card with
         // error
         int length = myCards.length;
         if (length < 1)
         {
            return new Card('*', Card.Suit.spades);
         }

         int newLength = length - 1;
         Card playedCard = new Card(myCards[newLength].getValue(),
               myCards[newLength].getSuit());
         Card[] oldHand = myCards.clone();

         // remove the card from the array, recreate the array
         myCards = new Card[newLength];
         for (int i = 0; i < newLength; i++)
         {
            myCards[i] = new Card(oldHand[i].getValue(), oldHand[i].getSuit());
         }

         // decrement the hand count
         numCards--;

         return playedCard;
      }
      
      /**
       * Removes the card at index location and shifts cards down in the array
       * @param cardIndex - index of the card to be played
       * @return - the card that was played
       */
      public Card playCard(int cardIndex)
      {
         if ( numCards == 0 ) //error
         {
            //Creates a card that does not work
            return new Card('M', Card.Suit.spades);
         }
         //Decreases numCards.
         Card card = myCards[cardIndex];
         
         numCards--;
         for(int i = cardIndex; i < numCards; i++)
         {
            myCards[i] = myCards[i+1];
         }
         
         myCards[numCards] = null;
         
         return card;
       }

      /**
       * returns all cards in the hand as a string
       */
      @Override
      public String toString()
      {
         String hand = "Hand = ( ";

         for (int i = 0; i < numCards; i++)
         {
            hand += myCards[i].toString();
            if (i != numCards - 1)
            {
               hand += ", ";
            }
         }

         hand += " )";

         return hand;
      }

      /**
       * returns a copy of the card in myArray based on the parameter position
       * if the card is not found, return a new card with errorFlag = true
       * 
       * @param k - position in the array to search
       * @return - copy of the found card, or card with errorFlag
       */
      public Card inspectCard(int k)
      {
         Card errorCard = new Card('*', Card.Suit.diamonds);
         
         if(k < 0 || k >= numCards) {
            return errorCard;
         }
         else {
            return myCards[k];
         }
      }

      /**
       * sort the hand by calling arraySort() from Card class
       */
      void sort() {
         Card.arraySort(myCards, myCards.length);
      }
      
   }// end Hand class
   
   // class CardGameFramework ****************
   class CardGameFramework
   {
      private static final int MAX_PLAYERS = 50;

      private int numPlayers;
      private int numPacks;            // # standard 52-card packs per deck
                                      // ignoring jokers or unused cards
      private int numJokersPerPack;  // if 2 per pack & 3 packs per deck, get 6
      private int numUnusedCardsPerPack;  // # cards removed from each pack
      private int numCardsPerHand;        // # cards to deal each player
      private Deck deck;               // holds the initial full deck and gets
                                  // smaller (usually) during play
      private Hand[] hand;             // one Hand for each player
      private Card[] unusedCardsPerPack; // an array holding the cards not used
                                      // in the game.  e.g. pinochle does not
                                      // use cards 2-8 of any suit

      public CardGameFramework( int numPacks, int numJokersPerPack,
       int numUnusedCardsPerPack,  Card[] unusedCardsPerPack,
       int numPlayers, int numCardsPerHand)
      {
         int k;

         // filter bad values
         if (numPacks < 1 || numPacks > 6)
            numPacks = 1;
         if (numJokersPerPack < 0 || numJokersPerPack > 4)
            numJokersPerPack = 0;
         if (numUnusedCardsPerPack < 0 || numUnusedCardsPerPack > 50) //  > 1 card
            numUnusedCardsPerPack = 0;
         if (numPlayers < 1 || numPlayers > MAX_PLAYERS)
            numPlayers = 4;
         // one of many ways to assure at least one full deal to all players
         if  (numCardsPerHand < 1 ||
          numCardsPerHand >  numPacks * (52 - numUnusedCardsPerPack)
          / numPlayers )
            numCardsPerHand = numPacks * (52 - numUnusedCardsPerPack) / numPlayers;

         // allocate
         this.unusedCardsPerPack = new Card[numUnusedCardsPerPack];
         this.hand = new Hand[numPlayers];
         for (k = 0; k < numPlayers; k++)
            this.hand[k] = new Hand();
         deck = new Deck(numPacks);

         // assign to members
         this.numPacks = numPacks;
         this.numJokersPerPack = numJokersPerPack;
         this.numUnusedCardsPerPack = numUnusedCardsPerPack;
         this.numPlayers = numPlayers;
         this.numCardsPerHand = numCardsPerHand;
         for (k = 0; k < numUnusedCardsPerPack; k++)
            this.unusedCardsPerPack[k] = unusedCardsPerPack[k];

         // prepare deck and shuffle
         newGame();
      }

      // constructor overload/default for game like bridge
      public CardGameFramework()
      {
         this(1, 0, 0, null, 4, 13);
      }

      public Hand getHand(int k)
      {
         // hands start from 0 like arrays

         // on error return automatic empty hand
         if (k < 0 || k >= numPlayers)
            return new Hand();

         return hand[k];
      }

      public Card getCardFromDeck() { return deck.dealCard(); }

      public int getNumCardsRemainingInDeck() { return deck.getNumCards(); }

      public void newGame()
      {
         int k, j;

         // clear the hands
         for (k = 0; k < numPlayers; k++)
            hand[k].resetHand();

         // restock the deck
         deck.init(numPacks);

         // remove unused cards
         for (k = 0; k < numUnusedCardsPerPack; k++)
            deck.removeCard( unusedCardsPerPack[k] );

         // add jokers
         for (k = 0; k < numPacks; k++)
            for ( j = 0; j < numJokersPerPack; j++)
               deck.addCard( new Card('X', Card.Suit.values()[j]) );

         // shuffle the cards
         deck.shuffle();
      }

      public boolean deal()
      {
         // returns false if not enough cards, but deals what it can
         int k, j;
         boolean enoughCards;

         // clear all hands
         for (j = 0; j < numPlayers; j++)
            hand[j].resetHand();

         enoughCards = true;
         for (k = 0; k < numCardsPerHand && enoughCards ; k++)
         {
            for (j = 0; j < numPlayers; j++)
               if (deck.getNumCards() > 0)
                  hand[j].takeCard( deck.dealCard() );
               else
               {
                  enoughCards = false;
                  break;
               }
         }

         return enoughCards;
      }

      void sortHands()
      {
         int k;

         for (k = 0; k < numPlayers; k++)
            hand[k].sort();
      }

      Card playCard(int playerIndex, int cardIndex)
      {
         // returns bad card if either argument is bad
         if (playerIndex < 0 ||  playerIndex > numPlayers - 1 ||
               cardIndex < 0 || cardIndex > numCardsPerHand - 1)
         {
            //Creates a card that does not work
            return new Card('M', Card.Suit.spades);      
         } 
         // return the card played
         return hand[playerIndex].playCard(cardIndex); 
      }

      boolean takeCard(int playerIndex)
      {
         // returns false if either argument is bad
         if (playerIndex < 0 || playerIndex > numPlayers - 1)
            return false;
   
         // Are there enough Cards?
         if (deck.getNumCards() <= 0)
            return false;

         return hand[playerIndex].takeCard(deck.dealCard());
      }
   }
}

// ***************** CONTROLLER **********************************************
class GameController
{
   GameModel theModel;
   GameView theView;
   GameModel.CardGameFramework framework;
   GameTimer gameTimer;
   
   public GameController(GameModel model, GameView view)
   {
      theModel = model;
      theView = view;
      
      this.theView.addStartListener(new StartListener());
      this.theView.addStopListener(new StopListener());
   }
   
   class StartListener implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         gameTimer.startTimer();
      }
   }
   
   class StopListener implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         gameTimer.stopTimer();
      }
   }

   public void updateTimer() {
      theView.updateTimer();
   }

   public void init(GameTimer gameTimer)
   {
      this.gameTimer = gameTimer;
      framework = theModel.getFramework();
      framework.deal();
      theView.init(framework);
   }
}