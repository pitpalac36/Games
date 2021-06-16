package barcute.services;

import barcute.model.Place;

public interface IServices {
    boolean login(int id, String password, IObserver client) throws Exception;
    void addParticipant(int id, IObserver client, Place first, Place second) throws Exception;
    void sendGuess(int finalL, int finalK, int id, IObserver client) throws Exception;
    void logout(int id, IObserver client) throws Exception;
}
