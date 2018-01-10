package org.academiadecodigo.balboas;

import org.academiadecodigo.balboas.services.UserService;

/**
 * Created by Daniel Baeta on 23/11/17.
 */
public enum MessageProtocol {

    REGISTER("REG"),
    LOGIN("LOGIN"),
    SENDDATA("SENDDATA"),
    FIGHT("FIGHT");

    private String protocol;
    private static UserService userService;
    public static final String DELIMITER = "##";
    private static int playerNumber = 0;


    MessageProtocol(String protocol) {
        this.protocol = protocol;
    }

    public static String decode(String message) {

        System.out.println("Message decode from server " + message);
        String[] splittedMessage = message.split(DELIMITER);
        MessageProtocol protocol = MessageProtocol.valueOf(splittedMessage[0]);

        System.out.println("Protocol: " + protocol);

        if (protocol == null) {
            return null;
        }

        switch (protocol) {
            case LOGIN:
                if (userService.authenticate(splittedMessage[2], splittedMessage[3])) {
                    System.out.println("User ok!");
                    return encode(LOGIN, "done" + DELIMITER + splittedMessage[2]);
                }
                break;
            case REGISTER:
                if (userService.addUser(splittedMessage[2], splittedMessage[3], splittedMessage[4])) {
                    return encode(REGISTER, "done");
                }
                break;
            case SENDDATA:
                System.out.println("Data received from second view");
                userService.registerData(splittedMessage[1], splittedMessage[2], splittedMessage[3], splittedMessage[4], splittedMessage[5]);
                System.out.println("Sending data to player");
                return encode(SENDDATA, "done");
            case FIGHT:
                String life = userService.getLife(splittedMessage[1]);
                String strength = userService.getStrength(splittedMessage[1]);
                ++playerNumber;
                return encode(FIGHT, splittedMessage[1] + DELIMITER + "done" + DELIMITER + life + DELIMITER +
                        strength + DELIMITER + playerNumber);
        }
        return null;
    }

    public static String encode(MessageProtocol protocol, String message) {

        return new StringBuilder(protocol.name()).append(DELIMITER).append(message).toString();

    }

    public static void setService(UserService userService) {
        MessageProtocol.userService = userService;
    }

}
