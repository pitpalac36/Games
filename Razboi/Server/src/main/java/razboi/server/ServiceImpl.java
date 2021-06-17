package razboi.server;
import razboi.persistence.IUserRepository;
import razboi.services.IObserver;
import razboi.services.IServices;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceImpl implements IServices {
    private IUserRepository userRepository;
    private Map<String, IObserver> playerClients;
    private Map<String, IObserver> pendingClients;  // users who logged in
    private String[] cards = new String[]{"6", "7", "8", "9", "J", "Q", "K", "A"};
    private Map<String, List<IObserver>> chosen;
    int sent;

    public ServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
        playerClients = new HashMap<>();
        pendingClients = new HashMap<>();
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
        pendingClients.putIfAbsent(username, client);
        if (pendingClients.size() >= 2) {
            playerClients.clear();
            playerClients.putAll(pendingClients);
            pendingClients.clear();

            chosen = new HashMap<>();
            Arrays.stream(cards).forEach(x -> chosen.put(x, new ArrayList<>()));

            for (IObserver each : playerClients.values()) {
                List<String> copiedArray = Arrays.asList(cards);
                String[] currentUserCards = (String[])getRandomElements(copiedArray, 4).toArray(new String[4]);
                notifyNewGame(each, currentUserCards);
            }

            sent = 0;

            //currentGame = new Game(true, new HashSet<>());
            //gameRepository.create(currentGame);
        }
    }

    private void notifyNewGame(IObserver client, String[] currentRoundCards) {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        executor.execute(() -> {
            try {
                System.out.println("notifying start game...");
                client.newGame(currentRoundCards);
            } catch (Exception e) {
                System.out.println("error notifying player...");
            }
        });
        executor.shutdown();
    }

    @Override
    public List<String> getAllPlayers() {
        return new ArrayList<>(playerClients.keySet());
    }

    @Override
    public void sendCard(String card, String username, IObserver client) {
        try {
            client.disableCards();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        sent++;
        System.out.println(chosen);
        chosen.get(card).add(client);
        System.out.println(chosen);
        System.out.println("=====================================================");
        if (sent == playerClients.size()) {
            for (int i = 7; i >= 0; i--) {
                if (chosen.get(cards[i]).size() > 1) break;
                if (chosen.get(cards[i]).size() == 1){     // winner
                    try {
                        List<String> forTheWinner = new ArrayList<>();
                        chosen.forEach((key, value) -> {
                            if (value.size() > 0) {
                                forTheWinner.add(key);
                            }
                        });
                        chosen.get(cards[i]).get(0).winCards(forTheWinner);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            chosen = new HashMap<>();
            Arrays.stream(cards).forEach(x -> chosen.put(x, new ArrayList<>()));
            sent = 0;

            for (IObserver each : playerClients.values()) {
                try {
                    each.enableCards();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private List<String> getRandomElements(List<String> list, int totalItems)
    {
        Collections.shuffle(list);
        List<String> newList = new ArrayList<>();
        for (int i = 0; i < totalItems; i++)
            newList.add(list.get(i));
        return newList;
    }
}
