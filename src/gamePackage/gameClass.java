package gamePackage;

import java.util.*;

/*
 * Classes about SET game
 * includes gamePackage.Card, gamePackage.CardDeck, Field, ...
 *
 * @author revdoor
 */

import static gamePackage.gameClass.*;

class Card implements Comparable<Card>{
    int color, number, shape, shading;
    int status;

    Card(int color, int number, int shape, int shading){
        this.color = color;
        this.number = number;
        this.shape = shape;
        this.shading = shading;
        this.status = IdentifierConstant.STATUS_UNUSED;
    }

    void statusChange(int status) {
        this.status = status;
    }

    @Override
    public int compareTo(Card card) {
        return Integer.compare(this.status, card.status);
    }
}

@FunctionalInterface
interface CardAttributeCheck{
    boolean isSame(Card card1, Card card2);
}

class CardDeck {
    Card[] deck = new Card[81];
    int usedCardNo;

    CardDeck(){
        for(int i = 0; i < 81; i++){
            int color = i/27;
            int number = i%27/9;
            int shape = i%9/3;
            int shading = i%3;
            this.deck[i] = new Card(color, number, shape, shading);
        }

        usedCardNo = 0;
    }

    void shuffleDeck() {
        List<Card> list = Arrays.asList(this.deck);
        Collections.shuffle(list);
        Collections.sort(list);
        list.toArray(this.deck);
    }

    boolean existSET() {
        if (this.usedCardNo == 81) {
            return false;
        }

        for(int i = usedCardNo; i < 79; i++) {
            for(int j = i+1; j < 80; j++) {
                for(int k = j+1; k < 81; k++) {
                    if (isSET(this.deck[i], this.deck[j], this.deck[k])) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    boolean remainsUnusedCard() {
        return this.usedCardNo + 12 < 81;
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

    default int winner(Player p1, Player p2) {
        if (p1Win(p1, p2)) return 1;
        if (p2Win(p1, p2)) return 2;
        return 0;
    }
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

class Pos {
    int row;
    int col;

    Pos(int row, int col) {
        this.row = row;
        this.col = col;
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

    Card getCard(int idx) {
        return this.cardOnField[idx];
    }

    void putCard(Pos pos, Card card) {
        int idx = posToIdx(pos);
        this.cardOnField[idx] = card;
    }

    void removeCard(Pos pos) {
        int idx = posToIdx(pos);
        this.cardOnField[idx].statusChange(IdentifierConstant.STATUS_USED);
        this.cardOnField[idx] = this.emptyCards[idx];
    }

    boolean existSET() {
        for (int i = 0; i < 10; i++) {
            for (int j = i+1; j < 11; j++) {
                for (int k = j+1; k < 12; k++) {
                    if (isSET(this.cardOnField[i], this.cardOnField[j], this.cardOnField[k])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

class SETGame {
    CardDeck gameDeck;
    GameField field;
    Player[] players;
    boolean gameFinished;

    SETGame() {
        this.gameDeck = new CardDeck();
        this.field = new GameField();
        this.gameFinished = false;

        initializeField();
    }

    void initializeField() {
        this.gameDeck.shuffleDeck();

        for (int idx = 0; idx < 12; idx++){
            this.field.putCard(idxToPos(idx), this.gameDeck.deck[idx]);
            this.gameDeck.deck[idx].statusChange(IdentifierConstant.STATUS_ON_FIELD);
        }
    }

    void SETDeclaration(int player_no, Pos pos1, Pos pos2, Pos pos3) {
        Card card1 = this.field.getCard(posToIdx(pos1));
        Card card2 = this.field.getCard(posToIdx(pos2));
        Card card3 = this.field.getCard(posToIdx(pos3));

        if (isEmpty(card1) || isEmpty(card2) || isEmpty(card3)) {
            badSETDeclaration(player_no);
            return;
        }

        if (!isSET(card1, card2, card3)) {
            badSETDeclaration(player_no);
            return;
        }

        goodSETDeclaration(player_no);
        if (this.gameDeck.existSET()) {
            removeCards(pos1, pos2, pos3);
            refillCards(pos1, pos2, pos3);
        }
        else {
            this.gameFinished = true;
        }
    }

    void badSETDeclaration(int player_no) {
        this.players[player_no].penalty += 1;
    }

    void goodSETDeclaration(int player_no) {
        this.players[player_no].score += 1;
    }

    void removeCards(Pos pos1, Pos pos2, Pos pos3) {
        this.field.removeCard(pos1);
        this.field.removeCard(pos2);
        this.field.removeCard(pos3);

        this.gameDeck.usedCardNo += 3;
    }

    void refillCards(Pos pos1, Pos pos2, Pos pos3) {
        Pos[] posArr = new Pos[]{pos1, pos2, pos3};
        if (this.gameDeck.remainsUnusedCard()) {
            do {
                this.gameDeck.shuffleDeck();

                for (int idx = 0; idx < 3; idx++) {
                    this.field.putCard(posArr[idx], this.gameDeck.deck[idx]);
                }
            } while (!this.field.existSET());

            for (int idx = 0; idx < 3; idx++)
                this.field.getCard(posToIdx(posArr[idx])).statusChange(IdentifierConstant.STATUS_ON_FIELD);
        }
    }

    boolean gameIsFinished() {
        return this.gameFinished;
    }
}

class SETGameForTwo extends SETGame {
    SETGameForTwo(String name1, String name2) {
        this.players = new Player[]{new Player(name1), new Player(name2)};
    }

    int gameWinner(GameResultChecker grc) {
        return grc.winner(this.players[0], this.players[1]);
    }
}

public class gameClass {
    public static boolean isEmpty(Card card) {
        return card.getClass().getName().equals("gamePackage.EmptyCard");
    }

    public static boolean checkCondition(Card card1, Card card2, Card card3, CardAttributeCheck cac){
        boolean isSame = (cac.isSame(card1, card2)) && (cac.isSame(card2, card3));
        boolean isDifferent = (!cac.isSame(card1, card2))
                && (!cac.isSame(card2, card3))
                && (!cac.isSame(card3, card1));

        return isSame || isDifferent;
    }

    public static boolean isSET(Card card1, Card card2, Card card3){
        if (isEmpty(card1) || isEmpty(card2) || isEmpty(card3)) {
            return false;
        }

        boolean colorSatisfied = checkCondition(card1, card2, card3, (a, b) -> a.color == b.color);
        boolean numberSatisfied = checkCondition(card1, card2, card3, (a, b) -> a.number == b.number);
        boolean shapeSatisfied = checkCondition(card1, card2, card3, (a, b) -> a.shape == b.shape);
        boolean shadingSatisfied = checkCondition(card1, card2, card3, (a, b) -> a.shading == b.shading);

        return colorSatisfied && numberSatisfied && shapeSatisfied && shadingSatisfied;
    }

    public static int posToIdx(Pos pos) {
        return pos.row * 4 + pos.col;
    }

    public static Pos idxToPos(int idx) {
        return new Pos(idx/4, idx%4);
    }

    public static void main(String[] args){
        SETGameForTwo game = new SETGameForTwo("Alice", "Bob");

        EmptyCard c = new EmptyCard();

        System.out.println(game.field.cardOnField[0].getClass().getName());
        System.out.println(c.getClass().getName());
        System.out.println(isEmpty(c));
    }
}
