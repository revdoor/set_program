package gamePackage;

import java.util.*;

import static gamePackage.gameClass.*;

class objectPrinter {
    void printGameField(GameField field) {
        for(int row = 0; row < 3; row++) {
            for(int col = 0; col < 4; col++) {
                System.out.print(field.cardOnField[posToIdx(new Pos(row, col))].cardString());
                System.out.print(" ");
            }
            System.out.print("\n");
        }
    }

    void printPlayerInfo(Player[] players) {
        for (Player player : players) {
            System.out.println(player.playerString());
        }
    }

}

public class gameClassTest {
    public static void main(String[] args) {
        SETGameForTwo testGame = new SETGameForTwo("Alice", "Bob");
        objectPrinter printer = new objectPrinter();
        Scanner sc = new Scanner(System.in);
        String oper;
        int playerNo;
        Pos[] posList = new Pos[3];

        printer.printGameField(testGame.field);
        printer.printPlayerInfo(testGame.players);

        label:
        while (true) {
            oper = sc.next();

            switch (oper) {
                case "exit":
                    break label;
                case "setcall":
                    playerNo = sc.nextInt();

                    for (int i = 0; i < 3; i++) {
                        int row = sc.nextInt();
                        int col = sc.nextInt();
                        posList[i] = new Pos(row, col);
                    }

                    testGame.SETDeclaration(playerNo, posList[0], posList[1], posList[2]);
                    break;
                case "field":
                    printer.printGameField(testGame.field);
                    break;
                case "player":
                    printer.printPlayerInfo(testGame.players);
                    break;
            }
        }
    }
}
