package gamePackage;

import java.util.*;

/*
 * Classes about Set game
 * includes gamePackage.Card, gamePackage.CardDeck, Field, ...
 *
 * @author revdoor
 */

import static gamePackage.gameClass.isSet;
import static gamePackage.gameClass.toPos;

class Card implements Comparable<Card>{
    int color, number, shape, shadow;
    int status;

    Card(int color, int number, int shape, int shadow){
        this.color = color;
        this.number = number;
        this.shape = shape;
        this.shadow = shadow;
        this.status = IdentifierConstant.STATUS_UNUSED;
    }

    boolean isEmpty() {
        return false;
    }

    @Override
    public int compareTo(Card card) {
        return Integer.compare(this.status, card.status);
    }
}

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
            int shadow = i%3;
            this.deck[i] = new Card(color, number, shape, shadow);
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
                    if (isSet(this.deck[i], this.deck[j], this.deck[k])) {
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

    boolean isEmpty() {
        return true;
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

    int toIdx() {
        return this.row * 4 + this.col;
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
        int idx = pos.toIdx();
        this.cardOnField[idx] = card;
        card.status = IdentifierConstant.STATUS_ON_FIELD;
    }

    void removeCard(Pos pos) {
        int idx = pos.toIdx();
        this.cardOnField[idx].status = IdentifierConstant.STATUS_USED;
        this.cardOnField[idx] = this.emptyCards[idx];
    }

    boolean existSET() {
        for (int i = 0; i < 10; i++) {
            for (int j = i+1; j < 11; j++) {
                for (int k = j+1; k < 12; k++) {
                    if (isSet(this.cardOnField[i], this.cardOnField[j], this.cardOnField[k])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

class SetGame {
    CardDeck gameDeck;
    GameField field;
    Player[] players;
    boolean gameFinished;

    SetGame() {
        this.gameDeck = new CardDeck();
        this.field = new GameField();
        this.gameFinished = false;

        initializeField();
    }

    void initializeField() {
        this.gameDeck.shuffleDeck();

        for (int idx = 0; idx < 12; idx++){
            this.field.putCard(toPos(idx), this.gameDeck.deck[idx]);
        }
    }

    void SETDeclaration(int player_no, Pos pos1, Pos pos2, Pos pos3) {
        Card card1 = this.field.getCard(pos1.toIdx());
        Card card2 = this.field.getCard(pos2.toIdx());
        Card card3 = this.field.getCard(pos3.toIdx());

        if (card1.isEmpty() || card2.isEmpty() || card3.isEmpty()) {
            badSETDeclaration(player_no);
            return;
        }

        if (!isSet(card1, card2, card3)) {
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

                for (int i = 0; i < 3; i++) {
                    int idx = i + this.gameDeck.usedCardNo;
                    this.field.putCard(posArr[i], this.gameDeck.deck[idx]);
                }
            } while (!this.field.existSET());
        }
    }
}

class SetGameForTwo extends SetGame {
    SetGameForTwo(String name1, String name2) {
        this.players = new Player[]{new Player(name1), new Player(name2)};
    }

    int gameWinner(GameResultChecker grc) {
        return grc.winner(this.players[0], this.players[1]);
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
        if (card1.isEmpty() || card2.isEmpty() || card3.isEmpty()) {
            return false;
        }

        boolean colorSatisfied = checkCondition(card1, card2, card3, (a, b) -> a.color == b.color);
        boolean numberSatisfied = checkCondition(card1, card2, card3, (a, b) -> a.number == b.number);
        boolean shapeSatisfied = checkCondition(card1, card2, card3, (a, b) -> a.shape == b.shape);
        boolean shadowSatisfied = checkCondition(card1, card2, card3, (a, b) -> a.shadow == b.shadow);

        return colorSatisfied && numberSatisfied && shapeSatisfied && shadowSatisfied;
    }

    public static Pos toPos(int idx) {
        return new Pos(idx/4, idx%4);
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
