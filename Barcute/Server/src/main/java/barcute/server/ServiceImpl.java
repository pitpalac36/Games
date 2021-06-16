package barcute.server;
import barcute.model.Game;
import barcute.model.Guess;
import barcute.model.Place;
import barcute.persistence.IGameRepository;
import barcute.persistence.IUserRepository;
import barcute.persistence.PlaceRepository;
import barcute.services.IObserver;
import barcute.services.IServices;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ServiceImpl implements IServices {
    private IUserRepository userRepository;
    private IGameRepository gameRepository;
    private PlaceRepository placeRepository;
    private Map<Integer, IObserver> playerClients;   // users already in the game
    private Map<Integer, IObserver> pendingClients;  // users who logged in
    private Map<Integer, IObserver> willingParticipants;    // users who clicked start button
    private Game currentGame = null;
    private Guess guessPlayer1 = new Guess();
    private Guess guessPlayer2 = new Guess();

    public ServiceImpl(IUserRepository userRepository, IGameRepository gameRepository, PlaceRepository placeRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.placeRepository = placeRepository;
        playerClients = new HashMap<>();
        pendingClients = new HashMap<>();
        willingParticipants = new HashMap<>();
    }

    @Override
    public boolean login(int id, String password, IObserver client) throws Exception {
        boolean valid = userRepository.findOne(id, password);
        if (valid) {
            if (playerClients.get(id) != null || pendingClients.get(id) != null || willingParticipants.get(id) != null) {
                throw new Exception("User is already Logged in!");
            }
            pendingClients.put(id, client);
        }
        return valid;
    }

    @Override
    public void addParticipant(int id, IObserver client, Place first, Place second) throws Exception {
        if (currentGame == null) {    // player 1 was not initialized
            currentGame = new Game();
            gameRepository.create(currentGame);
            currentGame.setPlayer1(id);
            currentGame.setPlace1player1(first);
            placeRepository.create(first);
            currentGame.setPlace2player1(second);
            placeRepository.create(second);
        } else {
            if (currentGame.getPlayer2() == 0) {    // player 1 was initialized but player 2 wasn't
                currentGame.setPlayer2(id);
                currentGame.setPlace1player2(first);
                placeRepository.create(first);
                currentGame.setPlace2player2(second);
                placeRepository.create(second);
                //gameRepository.update(currentGame);
            }
        }
        willingParticipants.put(id, client);
        if (willingParticipants.size() > 1) {
            willingParticipants.remove(id); // so that i won't chose the same value when calling random
            IObserver opponent = random(willingParticipants);
            willingParticipants.put(id, client);
            if (opponent != null) {
                int opponentId = getKey(willingParticipants, opponent);

                playerClients.put(id, client);
                playerClients.put(opponentId, opponent);

                willingParticipants.remove(id);
                willingParticipants.remove(opponentId);

                client.startGame(opponentId);
                opponent.startGame(id);
            }
        }
    }

    @Override
    public void sendGuess(int finalL, int finalK, int id, IObserver client) {
        try {
            if (currentGame.getPlayer1() != 0)   // game is not "null"
                client.disableBoard();
            int opponent;
            if (currentGame.getPlayer1() == id) {
                opponent = currentGame.getPlayer2();
                int attempts = currentGame.getAttemptsPlayer1() + 1;
                currentGame.setAttemptsPlayer1(attempts);   // increment attempts
                //gameRepository.update(currentGame);
                if (currentGame.getPlace1player2().getRow() == finalL && currentGame.getPlace1player2().getColumn() == finalK) {    // guessed!
                    guessPlayer1.setPlace1(true);
                    client.notifyGuess();
                    playerClients.get(opponent).notifyDanger(finalL, finalK);
                } else if (currentGame.getPlace2player2().getRow() == finalL && currentGame.getPlace2player2().getColumn() == finalK) { // guessed!
                    guessPlayer1.setPlace2(true);
                    client.notifyGuess();
                    playerClients.get(opponent).notifyDanger(finalL, finalK);
                }
                if (guessPlayer1.getPlace1() && guessPlayer1.getPlace2()) { // both were guessed
                    IObserver winner = playerClients.get(id);
                    currentGame.setWinner(id);
                    gameRepository.update(currentGame);
                    currentGame = new Game();
                    winner.endGame(true);
                    playerClients.get(opponent).endGame(false);
                }
            } else {
                opponent = currentGame.getPlayer1();
                int attempts = currentGame.getAttemptsPlayer2() + 1;
                currentGame.setAttemptsPlayer2(attempts);   // increment attempts
                //gameRepository.update(currentGame);
                if (currentGame.getPlace1player1().getRow() == finalL && currentGame.getPlace1player1().getColumn() == finalK) {    // guessed!
                    guessPlayer2.setPlace1(true);
                    client.notifyGuess();
                    playerClients.get(opponent).notifyDanger(finalL, finalK);
                } else if (currentGame.getPlace2player1().getRow() == finalL && currentGame.getPlace2player1().getColumn() == finalK) { // guessed!
                    guessPlayer2.setPlace2(true);
                    client.notifyGuess();
                    playerClients.get(opponent).notifyDanger(finalL, finalK);
                }
                if (guessPlayer2.getPlace1() && guessPlayer2.getPlace2()) { // both were guessed
                    IObserver winner = playerClients.get(id);
                    currentGame.setWinner(id);
                    System.out.println(currentGame);
                    gameRepository.update(currentGame);
                    winner.endGame(true);
                    playerClients.get(opponent).endGame(false);
                    pendingClients.putAll(playerClients);
                    playerClients.clear();
                    currentGame = null;
                }
            }
            if (currentGame != null)   // game is not "null"
                playerClients.get(opponent).enableBoard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logout(int id, IObserver client) throws Exception {
        IObserver localClient = pendingClients.remove(id);
        if (localClient == null)
            throw new Exception("User " + id + " is not logged in");
        System.out.println("Client " + id + " disconnected");
    }


    private static <K, V> K getKey(Map<K, V> map, V value)
    {
        for (Map.Entry<K, V> entry: map.entrySet())
        {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static IObserver random(Map<Integer, IObserver> map) {
        Random generator = new Random();
        Object[] values = map.values().toArray();
        return (IObserver) values[generator.nextInt(values.length)];
    }
}
