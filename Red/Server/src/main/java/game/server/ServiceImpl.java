package game.server;
import game.model.Card;
import game.model.Game;
import game.persistence.ICardRepository;
import game.persistence.IGameRepository;
import game.persistence.IUserRepository;
import game.services.IObserver;
import game.services.IServices;
import java.util.*;

public class ServiceImpl implements IServices {
    private IUserRepository userRepository;
    private IGameRepository gameRepository;
    private ICardRepository cardRepository;
    private Map<String, IObserver> playerClients;
    private Map<String, IObserver> pendingClients;  // users who logged in
    private Map<String, IObserver> willingClients;  // users who logged in and clicked start
    private final int defaultThreadsNo = 5;
    private Game currentGame = null;
    private String[] cards = {"red.8", "red.9", "red.10", "black.8", "black.5", "black.2", "black.7", "red.7", "red.2"};
    private int round;
    private int sentThisRound;

    public ServiceImpl(IUserRepository userRepository, IGameRepository gameRepository, ICardRepository cardRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.cardRepository = cardRepository;
        playerClients = new HashMap<>();
        pendingClients = new HashMap<>();
        willingClients = new HashMap<>();
        //System.out.println(this.userRepository.findOne("a", "a"));
    }

    @Override
    public boolean login(String username, String password, IObserver client) throws Exception {
        boolean valid = userRepository.findOne(username, password);
        if (valid) {
            if (playerClients.get(username) != null || pendingClients.get(username) != null) {
                throw new Exception("User is already Logged in!");
            }
            pendingClients.put(username, client);
        }
        return valid;
    }

//    private void notifyNewGame(IObserver client, int[] numbers, int sum) {
//        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
//        executor.execute(() -> {
//            try {
//                System.out.println("notifying start game...");
//                client.newGame(numbers, sum);
//            } catch (Exception e) {
//                System.out.println("error notifying player...");
//            }
//        });
//        executor.shutdown();
//    }
//
//    private void notifyEndGame(IObserver client) {
//        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
//        executor.execute(() -> {
//            try {
//                System.out.println("notifying end game...");
//                client.endGame();
//            } catch (Exception e) {
//                System.out.println("error notifying player...");
//            }
//        });
//        executor.shutdown();
//    }

    @Override
    public void logout(String username, IObserver client) throws Exception {
        IObserver c = pendingClients.remove(username);
        if (c == null) {
            c = playerClients.remove(username);
            if (c == null)
                throw new Exception("User " + username + " was not logged in!");
        }
    }

    @Override
    public List<String> getAllPlayers() {
        return new ArrayList<>(playerClients.keySet());
    }

    @Override
    public void addParticipant(String username, IObserver client) throws Exception {
        willingClients.putIfAbsent(username, client);
        if (willingClients.size() == 3 && currentGame == null) {    // se poate incepe jocul
            currentGame = new Game();
            gameRepository.save(currentGame);
            currentGame.setInProgress(true);

            List<String> cardsCopy = Arrays.asList(cards);
            Collections.shuffle(cardsCopy);
            int i = 0;
            for (Map.Entry<String, IObserver> each : willingClients.entrySet()) {
                List<String> forPlayer = new ArrayList<>();
                forPlayer.add(cardsCopy.get(i));
                Card c1 = new Card(each.getKey(), cardsCopy.get(i), true, false, 0);
                forPlayer.add(cardsCopy.get(i+1));
                Card c2 = new Card(each.getKey(), cardsCopy.get(i+1), true, false, 0);
                forPlayer.add(cardsCopy.get(i+2));
                Card c3 = new Card(each.getKey(), cardsCopy.get(i+2), true, false, 0);
                cardRepository.create(c1);
                cardRepository.create(c2);
                cardRepository.create(c3);
                currentGame.addCard(c1);
                currentGame.addCard(c2);
                currentGame.addCard(c3);
                gameRepository.update(currentGame);
                i = i+3;
                each.getValue().startGame(new ArrayList<String>(willingClients.keySet()), c1, c2, c3);
            }
            playerClients.putAll(willingClients);
            willingClients.clear();

            round = 0;
            sentThisRound = 0;
        }
    }

    @Override
    public void sendCard(Card card, String username, IObserver client) throws Exception {
        client.disable();
        if (sentThisRound == 0) {   // primul care trimite => incrementez runda
            round++;
        }
        sentThisRound++;    // incrementez nr de playeri care au trimis
        card.setRound(round);
        cardRepository.update(card);
        if (sentThisRound == playerClients.size()) {    // au trimis toti deci pot procesa cartile
            List<Card> thisRound = cardRepository.getCards(currentGame, round);
            String player = verifyOneRed(thisRound);
            if (player.equals("")) { // nu a fost trimisa doar o carte rosie

                if (thisRound.get(0).getValue().contains("black") && thisRound.get(1).getValue().contains("black")
                        && thisRound.get(2).getValue().contains("black")) { // sunt toate negre
                    int min;
                    int c1value = Integer.parseInt(thisRound.get(0).getValue().split("\\.")[1]);
                    int c2value = Integer.parseInt(thisRound.get(1).getValue().split("\\.")[1]);
                    int c3value = Integer.parseInt(thisRound.get(2).getValue().split("\\.")[1]);

                    if (c1value < c2value) min = c1value;
                    else min = c2value;
                    if (c3value < min) min = c3value;

                    if (thisRound.get(0).getValue().contains(String.valueOf(min))) {
                        for (int i = 0; i <3; i++) {
                            Card nou = new Card(thisRound.get(0).getUsername(), thisRound.get(i).getValue(), false, true, round);
                            nou.setGame(currentGame);
                            cardRepository.create(nou);
                            currentGame.addCard(nou);
                        }
                        playerClients.get(thisRound.get(0).getUsername()).wonCards(thisRound);
                    }
                    if (thisRound.get(1).getValue().contains(String.valueOf(min))) {
                        for (int i = 0; i <3; i++) {
                            Card nou = new Card(thisRound.get(1).getUsername(), thisRound.get(i).getValue(), false, true, round);
                            nou.setGame(currentGame);
                            cardRepository.create(nou);
                            currentGame.addCard(nou);
                        }
                        playerClients.get(thisRound.get(1).getUsername()).wonCards(thisRound);
                    }
                    if (thisRound.get(2).getValue().contains(String.valueOf(min))) {
                        for (int i = 0; i <3; i++) {
                            Card nou = new Card(thisRound.get(2).getUsername(), thisRound.get(i).getValue(), false, true, round);
                            nou.setGame(currentGame);
                            cardRepository.create(nou);
                            currentGame.addCard(nou);
                        }
                        playerClients.get(thisRound.get(2).getUsername()).wonCards(thisRound);
                    }
                    for (IObserver each : playerClients.values())
                        each.enable();

                } else {    // au fost trimise 2 carti rosii
                    int max;
                    int c1value, c2value, c3value;
                    if (thisRound.get(0).getValue().contains("red")) {
                        c1value = Integer.parseInt(thisRound.get(0).getValue().split("\\.")[1]);
                    }

                    else c1value = -1;
                    if (thisRound.get(1).getValue().contains("red")){
                        c2value = Integer.parseInt(thisRound.get(1).getValue().split("\\.")[1]);
                    }

                    else c2value = -1;
                    if (thisRound.get(2).getValue().contains("red")) {
                        c3value = Integer.parseInt(thisRound.get(2).getValue().split("\\.")[1]);
                    }

                    else c3value = -1;

                    if (c1value > c2value) max = c1value;
                    else max = c2value;
                    if (c3value > max) max = c3value;

                    if (thisRound.get(0).getValue().contains(String.valueOf(max)) && c1value != -1) {
                        for (int i = 0; i < 3; i++) {
                            Card nou = new Card(thisRound.get(0).getUsername(), thisRound.get(i).getValue(), false, true, round);
                            nou.setGame(currentGame);
                            cardRepository.create(nou);
                            currentGame.addCard(nou);
                        }
                        playerClients.get(thisRound.get(0).getUsername()).wonCards(thisRound);
                    }
                    if (thisRound.get(1).getValue().contains(String.valueOf(max)) && c2value != -1) {
                        for (int i = 0; i <3; i++) {
                            Card nou = new Card(thisRound.get(1).getUsername(), thisRound.get(i).getValue(), false, true, round);
                            nou.setGame(currentGame);
                            cardRepository.create(nou);
                            currentGame.addCard(nou);
                        }
                        playerClients.get(thisRound.get(1).getUsername()).wonCards(thisRound);
                    }
                    if (thisRound.get(2).getValue().contains(String.valueOf(max)) && c3value != -1) {
                        for (int i = 0; i <3; i++) {
                            Card nou = new Card(thisRound.get(2).getUsername(), thisRound.get(i).getValue(), false, true, round);
                            nou.setGame(currentGame);
                            cardRepository.create(nou);
                            currentGame.addCard(nou);
                        }
                        playerClients.get(thisRound.get(2).getUsername()).wonCards(thisRound);
                    }
                    for (IObserver each : playerClients.values())
                        each.enable();
                }
            } else {    // a fost trimisa doar o carte rosie => cel care a trimis castiga toate cartile
                for (int i = 0; i <3; i++) {
                    Card nou = new Card(player, thisRound.get(i).getValue(), false, true, round);
                    nou.setGame(currentGame);
                    cardRepository.create(nou);
                    currentGame.addCard(nou);
                }
                for (Card each : thisRound) {
                    each.setWon(true);
                    cardRepository.update(each);
                }
                playerClients.get(player).wonCards(thisRound);
                for (IObserver each : playerClients.values())
                    each.enable();
            }
            gameRepository.update(currentGame);
            sentThisRound = 0;
            if (round == 3) {
                for (IObserver each : playerClients.values())
                    each.endGame();
                currentGame.setInProgress(false);
                gameRepository.update(currentGame);
            }
        }
    }

    @Override
    public void sendNumber(String username, IObserver client, int size) throws Exception {
        //
    }

    private String verifyOneRed(List<Card> thisRound) {
        int contor = 0;
        String user = "";
        for (Card each : thisRound) {
            if (each.getValue().contains("red")) {
                contor++;
                user = each.getUsername();
            }
            if (contor > 1) return "";
        }
        if (contor == 0) return "";
        return user;
    }

}
