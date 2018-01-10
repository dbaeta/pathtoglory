package org.academiadecodigo.balboas.services;

import org.academiadecodigo.balboas.game.Player;

import java.util.List;

public class MockUserService implements UserService {

    private List<Player> playerList;

    @Override
    public boolean authenticate(String pass, String user) {
        return true;
    }

    @Override
    public boolean addUser(String username, String password, String email) {
        return true;
    }

    @Override
    public boolean findByName(String username) {
        return true;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void registerData(String name, String gym, String diet, String beer, String smoke) {
        playerList.add(new Player(name, gym, diet, beer, smoke));
    }

    @Override
    public String getLife(String name) {
        return Integer.toString(10);
    }

    @Override
    public String getStrength(String name) {
        return Integer.toString(10);
    }
}
