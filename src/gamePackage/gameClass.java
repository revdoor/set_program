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

public class gameClass {
    public boolean checkCondition(Card card1, Card card2, Card card3, CardAttributeCheck cac){
        boolean isSame = (cac.isSame(card1, card2)) && (cac.isSame(card2, card3));
        boolean isDifferent = (!cac.isSame(card1, card2))
                && (!cac.isSame(card2, card3))
                && (!cac.isSame(card3, card1));

        return isSame || isDifferent;
    }

    public boolean isSet(Card card1, Card card2, Card card3){
        boolean colorSatisfied = checkCondition(card1, card2, card3, (a, b) -> a.color == b.color);
        boolean numberSatisfied = checkCondition(card1, card2, card3, (a, b) -> a.number == b.number);
        boolean shapeSatisfied = checkCondition(card1, card2, card3, (a, b) -> a.shape == b.shape);
        boolean shadowSatisfied = checkCondition(card1, card2, card3, (a, b) -> a.shadow == b.shadow);

        return colorSatisfied && numberSatisfied && shapeSatisfied && shadowSatisfied;
    }
}
