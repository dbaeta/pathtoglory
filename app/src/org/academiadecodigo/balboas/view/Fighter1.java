package org.academiadecodigo.balboas.view;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.academiadecodigo.balboas.controller.FightController;
import org.academiadecodigo.balboas.model.Client;
import org.academiadecodigo.balboas.model.MessageProtocol;

/**
 * Created by Daniel Baeta on 24/11/17.
 */
public class Fighter1 implements Fighter {

    FightController controller;
    Client client;


    @Override
    public void moveLeft(ImageView player1, ImageView player2, String clientName) {

        player1.setImage(new Image("/fighter1Still.png"));
        player1.setX(player1.getX() - 20.0d);
        if (player1.getX() <= 0) {
            player1.setX(0.0d);
        }
    }

    @Override
    public void moveRight(ImageView player1, ImageView player2, String clientName) {

        player1.setImage(new Image("/fighter1Still.png"));
        player1.setX(player1.getX() + 20.0d);
        if (player1.getX() >= (player2.getX() - player1.getFitWidth())) {
            player1.setX(player2.getX() - player2.getFitWidth());
        }
        if (player1.getX() >= 320) {
            player1.setX(320.0d);
        }
    }

    @Override
    public void attack(ImageView player1, ImageView player2, String text, String clientName) {

        player1.setImage(new Image("/punching1.png"));
        if (player1.getX() >= (player2.getX() - player1.getFitWidth())) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    String message = MessageProtocol.encode(MessageProtocol.ATTACK, text, clientName);
                    System.out.println(message);
                    client.sendMessage(message);
                }
            });
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setController(FightController controller) {
        this.controller = controller;
    }
}
