package monopoly.server;
import javafx.util.Pair;
import monopoly.model.Deal;
import monopoly.model.Game;
import monopoly.persistence.DealRepository;
import monopoly.persistence.IGameRepository;
import monopoly.persistence.IUserRepository;
import monopoly.services.IObserver;
import monopoly.services.IServices;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceImpl implements IServices {
    private IUserRepository userRepository;
    private IGameRepository gameRepository;
    private DealRepository dealRepository;
    private Map<String, IObserver> playerClients;
    private Map<String, IObserver> pendingClients;  // users who logged in
    private Map<String, IObserver> willingPlayers;  // users who clicked start button
    private Map<Integer, Pair<Integer, IObserver>> board;    // key: position; value: Pair<cost, player>  (if there is a player)
    private int biggestScore = 0;
    private String currentWinner = "";
    private final int defaultThreadsNo = 5;
    private int[] numbers = new int[5];
    private int finished = 0;
    private Game currentGame = null;


    public ServiceImpl(IUserRepository userRepository, IGameRepository gameRepository, DealRepository dealRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.dealRepository = dealRepository;
        playerClients = new HashMap<>();
        pendingClients = new HashMap<>();
        willingPlayers = new HashMap<>();
        board = new HashMap<>();
        System.out.println(this.userRepository.findOne("a", "a"));
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

    private void notifyNewGame(IObserver client, int[] numbers, int sum) {
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        executor.execute(() -> {
            try {
                System.out.println("notifying start game...");
                client.newGame(numbers, sum);
            } catch (Exception e) {
                System.out.println("error notifying player...");
            }
        });
        executor.shutdown();
    }

    private void notifyEndGame(IObserver client) {
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        executor.execute(() -> {
            try {
                System.out.println("notifying end game...");
                client.endGame();
            } catch (Exception e) {
                System.out.println("error notifying player...");
            }
        });
        executor.shutdown();
    }

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
    public void addParticipant(String username, IObserver client) {
        willingPlayers.putIfAbsent(username, client);
        if (willingPlayers.size() == 3) {
            playerClients.clear();
            playerClients.putAll(pendingClients);
            willingPlayers.clear();
            pendingClients.clear();

            Random ran = new Random();
            for (int i = 0; i < 5; i++) {
                numbers[i] = ran.nextInt(50) + 1;
            }
            int sum = ran.nextInt(100) + 100;

            for (IObserver each : playerClients.values())
                notifyNewGame(each, numbers, sum);
            currentGame = new Game(true, new HashSet<>());
            gameRepository.create(currentGame);
        }
    }

    @Override
    public void movePawn(String username, int currentPosition, int generated, int currency, IObserver client) throws Exception {
        int position = currentPosition + generated;
        if (position > 5) {     // move to start
            position = 1;
            client.receive(100, "");
            Deal deal = new Deal(currentGame, username, 100);
            currentGame.setDeal(deal);
            deal.setGame(currentGame);
            dealRepository.create(deal);
            gameRepository.update(currentGame);
        }
        int cost = numbers[position - 1];

        // remove former pair from board
        board.remove(currentPosition);

        if (board.get(position) == null) {  // free; add pair to board
            playerClients.get(username).pay(cost/2, position, "");
            board.put(position, new Pair<>(cost, client));
            Deal deal = new Deal(currentGame, username, (-1) * cost/2);
            currentGame.setDeal(deal);
            deal.setGame(currentGame);
            dealRepository.create(deal);
            gameRepository.update(currentGame);
        } else {
            String receiver = getKey(playerClients, board.get(position).getValue());
            playerClients.get(username).pay(cost, position, receiver);
            board.get(position).getValue().receive(cost, username);

            Deal dealPayer = new Deal(currentGame, username, (-1) * cost);
            Deal dealReceiver = new Deal(currentGame, receiver, cost);
            currentGame.setDeal(dealPayer);
            dealPayer.setGame(currentGame);
            dealRepository.create(dealPayer);
            currentGame.setDeal(dealReceiver);
            dealReceiver.setGame(currentGame);
            dealRepository.create(dealReceiver);
            gameRepository.update(currentGame);
        }
    }

    @Override
    public List<String> getAllPlayers() {
        return new ArrayList<>(playerClients.keySet());
    }

    @Override
    public void announceFinish(IObserver client) {
        finished++;
        if (finished == playerClients.size()) {
            finished = 0;   // so i can use it in sendScore
            for (IObserver each : playerClients.values())
                notifyEndGame(each);
        }
        currentGame.setInProgress(false);
        gameRepository.update(currentGame);
    }

    @Override
    public synchronized void sendScore(int currency, String username, IObserver client) {
        finished++;
        if (currency >= biggestScore) {
            biggestScore = currency;
            currentWinner = username;
        }
        System.out.println(username + " sent score" + currency + ". finished = " + finished);
        System.out.println(playerClients.size());
        if (finished == playerClients.size()) {
            for (IObserver each : playerClients.values())
                sendWinner(each, currentWinner, biggestScore);

            pendingClients.putAll(playerClients);
            playerClients.clear();
        }
    }

    private void sendWinner(IObserver client, String currentWinner, int biggestScore) {
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        executor.execute(() -> {
            try {
                System.out.println("notifying winner...");
                client.wonOrNot(currentWinner, biggestScore);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("error notifying player...");
            }
        });
        executor.shutdown();
    }


    public static <K, V> K getKey(Map<K, V> map, V value)
    {
        for (Map.Entry<K, V> entry: map.entrySet())
        {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
