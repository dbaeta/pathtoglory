package org.academiadecodigo.balboas.utils;

import org.academiadecodigo.balboas.Game;
import org.academiadecodigo.balboas.Server;
import org.academiadecodigo.balboas.game.Player;
import org.academiadecodigo.balboas.service.JdbcUserService;

import java.util.*;

/**
 * Created by Daniel Baeta on 23/11/17.
 */
public enum MessageProtocol {

    LOGIN("LOGIN"),
    MOVE("MOVE"),
    ATTACK("ATTACK"),
    GAMEOVER("OVER"),
    BEGIN("BEGIN"),
    STATS("STATS");

    private String protocolTag;
    public static final String DELIMITER = "##";
    private static Game game;
    private static JdbcUserService jdbcUserService;

    MessageProtocol(String protocolTag) {
        this.protocolTag = protocolTag;
    }

    public static void setGame(Game newGame) {
        game = newGame;
    }

    public static void setJdbcUserService(JdbcUserService service) {
        jdbcUserService = service;
    }

    public static String encode(MessageProtocol protocol, String username, String message) {
        return new StringBuilder(protocol.name()).append(DELIMITER).append(message).toString();
    }

    public static void decode(String message, Server.ServerWorker serverWorker) {

        String[] splitMessage = message.split(DELIMITER);
        MessageProtocol protocol = MessageProtocol.valueOf(splitMessage[0]);
        String username = splitMessage[1];

        //<protocol>##<username>##<username>##<password>

        switch (protocol) {
            case LOGIN:
                if (jdbcUserService.authenticate(splitMessage[2], splitMessage[3])) {
                    addPlayer(new Player(serverWorker), splitMessage[1]);
                    jdbcUserService.receiveStatus(game.getPlayersList().get(username), username);
                    game.checkGameReadyToStart();
                }
                //JDBC service
                break;
            case MOVE:
                sendMessageToOpponentPlayer(splitMessage[2], username);
                break;
            case ATTACK:
                //Send message in format <protocol>##<username>##<damage>
                getOpponentPlayer(username).sufferAttack(game.getPlayersList().get(username).getStrength());
                break;
            case STATS:
                //Get these from the database
                sendMessageToPlayer(jdbcUserService.sendStats(game.getPlayersList().get(username), username), username);
                break;
        }
    }

    private static void sendMessageToPlayer(String message, String username) {

        Collection<Player> setPlayers = game.getPlayersList().values();

        for (Player player : setPlayers) {
            if (player.getUsername().equals(username)) {
                player.sendMessage(message);
            }
        }
    }

    private static void sendMessageToOpponentPlayer(String message, String username) {

        Collection<Player> setPlayers = game.getPlayersList().values();

        for (Player player : setPlayers) {
            if (!player.getUsername().equals(username)) {
                player.sendMessage(message);
            }
        }
    }

    private static Player getOpponentPlayer(String userName) {

        for (Player player : game.getPlayersList().values()) {
            if (!player.getUsername().equals(userName)) {
                return player;
            }
        }
        return null;
    }

    public static void broadcastMessage(String message) {
        for (Player player : game.getPlayersList().values()) {
            player.sendMessage(message);
        }
    }

    public static void addPlayer(Player player, String name) {
        player.setUsername(name);
        game.addPlayer(player);
    }
}
