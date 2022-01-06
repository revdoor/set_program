package gamePackage;

/*
 * Classes about Set game
 * includes gamePackage.Card, gamePackage.CardDeck, Field, ...
 *
 * @author revdoor
 */

class Card {
    int color, number, shape, shadow;
    int status;

    Card(int color, int number, int shape, int shadow){
        this.color = color;
        this.number = number;
        this.shape = shape;
        this.shadow = shadow;
        this.status = IdentifierConstant.STATUS_UNUSED;
    }
}

interface CardAttributeCheck{
    boolean isSame(Card card1, Card card2);
}

class CardDeck {
    Card[] deck = new Card[81];

    CardDeck(){
        for(int i = 0; i < 81; i++){
            int color = i/27;
            int number = i%27/9;
            int shape = i%9/3;
            int shadow = i%3;
            this.deck[i] = new Card(color, number, shape, shadow);
        }
    }
}

class EmptyCard extends Card {
    EmptyCard(){
        super(IdentifierConstant.EMPTY,
                IdentifierConstant.EMPTY,
                IdentifierConstant.EMPTY,
                IdentifierConstant.EMPTY);
        this.status = IdentifierConstant.EMPTY;
    }
}

class Player {
    String name;
    int score;
    int penalty;

    Player(String name){
        this.name = name;
        this.score = 0;
        this.penalty = 0;
    }
}

interface GameResultChecker{
    boolean p1Win(Player p1, Player p2);
    boolean p2Win(Player p1, Player p2);
    boolean draw(Player p1, Player p2);
}

class DefaultGameResultChecker implements GameResultChecker{
    @Override
    public boolean p1Win(Player p1, Player p2) {
        int score1 = p1.score - p1.penalty;
        int score2 = p2.score - p2.penalty;

        return score1 > score2;
    }

    public boolean p2Win(Player p1, Player p2) {
        return p1Win(p2, p1);
    }

    public boolean draw(Player p1, Player p2) {
        return !p1Win(p1, p2) && !p2Win(p1, p2);
    }
}

class GameField {
    Card[] cardOnField = new Card[12];
    EmptyCard[] emptyCards = new EmptyCard[12];

    GameField() {
        for(int i = 0; i < 12; i++){
            this.emptyCards[i] = new EmptyCard();
            this.cardOnField[i] = this.emptyCards[i];
        }
    }
}

class SetGame {
    CardDeck gameDeck;
    GameField field;
    Player[] players;

    SetGame() {
        this.gameDeck = new CardDeck();
    }
}

class SetGameForTwo extends SetGame {
    SetGameForTwo(String name1, String name2) {
        this.players = new Player[]{new Player(name1), new Player(name2)};
    }
}

public class gameClass {
    public static boolean checkCondition(Card card1, Card card2, Card card3, CardAttributeCheck cac){
        boolean isSame = (cac.isSame(card1, card2)) && (cac.isSame(card2, card3));
        boolean isDifferent = (!cac.isSame(card1, card2))
                && (!cac.isSame(card2, card3))
                && (!cac.isSame(card3, card1));

        return isSame || isDifferent;
    }

    public static boolean isSet(Card card1, Card card2, Card card3){
        boolean colorSatisfied = checkCondition(card1, card2, card3, (a, b) -> a.color == b.color);
        boolean numberSatisfied = checkCondition(card1, card2, card3, (a, b) -> a.number == b.number);
        boolean shapeSatisfied = checkCondition(card1, card2, card3, (a, b) -> a.shape == b.shape);
        boolean shadowSatisfied = checkCondition(card1, card2, card3, (a, b) -> a.shadow == b.shadow);

        return colorSatisfied && numberSatisfied && shapeSatisfied && shadowSatisfied;
    }

    public static void main(String[] args){
        SetGameForTwo game = new SetGameForTwo("Alice", "Bob");

        int color = game.gameDeck.deck[35].color;
        int number = game.gameDeck.deck[35].number;
        int shape = game.gameDeck.deck[35].shape;
        int shadow = game.gameDeck.deck[35].shadow;

        System.out.println(color);
        System.out.println(number);
        System.out.println(shape);
        System.out.println(shadow);
    }
}
