package anagrame.server;
import anagrame.domain.Game;
import anagrame.domain.Ranking;
import anagrame.domain.User;
import anagrame.repository.*;
import anagrame.services.IObserver;
import anagrame.services.IServices;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ServiceImpl implements IServices {
    private IUserRepository userRepository;
    private IGameRepository gameRepository;
    private IWordRepository wordRepository;
    private RankingRepository rankingRepository;
    private Map<String, IObserver> playerClients;
    private final int defaultThreadsNo = 5;
    private Map<String, IObserver> pendingClients;
    private List<String> finishedRoundClients;
    private Game currentGame = null;
    private String currentWord;
    private int currentRound = 0;
    private AtomicBoolean wasRun = new AtomicBoolean(false);
    private int finished;
    private int biggestScore;

    public ServiceImpl(IUserRepository userRepository, IGameRepository gameRepository, IWordRepository wordRepository, RankingRepository rankingRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.wordRepository = wordRepository;
        this.rankingRepository = rankingRepository;
        playerClients = new HashMap<>();
        pendingClients = new HashMap<>();
        finishedRoundClients = new ArrayList<>();
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

            if (currentGame == null) {
                if(pendingClients.size() == 2) {
                    for (String each : pendingClients.keySet())
                        notify(each);
                } else if (pendingClients.size() > 2)
                    notify(username);
            }
        }
        return valid;
    }

    private void notify(String username) {
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);

        executor.execute(() -> {
            try {
                if (pendingClients.size() >= 2) {
                    System.out.println("notifying enable start...");
                    pendingClients.get(username).enableStart();
                }
                else {
                    System.out.println("notifying disable start...");
                    pendingClients.get(username).disableStart();
                }
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
        if (pendingClients.size() == 1) {
            notify(pendingClients.keySet().iterator().next());
        }
    }

    @Override
    public void startGame() throws Exception {
        String fileName = System.getProperty("user.dir") + "\\Server\\src\\main\\resources\\gameID";
        System.out.println(fileName);
        FileInputStream fis=new FileInputStream(fileName);
        Scanner sc=new Scanner(fis);
        Integer id = Integer.parseInt(sc.nextLine()) +1;
        sc.close();
        FileOutputStream outputStream = new FileOutputStream(fileName);
        byte[] strToBytes = id.toString().getBytes();
        outputStream.write(strToBytes);
        outputStream.close();

        playerClients.clear();
        playerClients.putAll(pendingClients);
        pendingClients.clear();

        currentWord = wordRepository.chooseRandom().getWord();
        currentGame = new Game(id, currentWord, new HashSet<>(), true);
        gameRepository.create(currentGame);
        for (IObserver each : playerClients.values()) {
            each.setGame(currentGame);
            each.newRound(id, shuffle(currentWord), currentWord.length());
            each.disableStart();
        }
        currentRound++;
        finished = 0;
        biggestScore = 0;
    }

    public String shuffle(String text){
        List<Character> characters =  text.chars().mapToObj( c -> (char)c).collect(Collectors.toList());
        StringBuilder result = new StringBuilder();
        IntStream.range(0,text.length()).forEach((index) -> {
            int randomPosition = new Random().nextInt(characters.size());
            result.append(characters.get(randomPosition));
            characters.remove(randomPosition);
        });
        return result.toString();
    }

    @Override
    public void sendResponse(String username, Integer gameId, String word) throws Exception {
        wasRun.set(false);
        if (currentGame.getId() == gameId && currentGame.getInProgress()) {
            int points = 0;
            System.out.println(word);
            System.out.println(currentWord);
            int min = Math.min(word.length(), currentWord.length());
            for (int i = 0; i < min; i++) {
                if (word.charAt(i) == currentWord.charAt(i)) {
                    points++;
                }
            }
            System.out.println("HEEEY SUNT IN SEND RESPONSE SERVER");
            Ranking r = new Ranking(username, points, currentGame, currentRound);
            currentGame.setRanking(r);
            rankingRepository.create(r);
            finishedRoundClients.add(username);
            if (playerClients.size() == finishedRoundClients.size()) {
                for (Map.Entry<String, IObserver> each : playerClients.entrySet()) {
                    System.out.println("SEND RESULT TOOOOO obs");
                    each.getValue().resultReceived(currentWord, gameRepository.getPoints(each.getKey(), currentGame.getId(), currentRound));
                }
                gameRepository.update(currentGame);
                finishedRoundClients.clear();
            }
        }
    }

    @Override
    public List<String> getAllPlayers() {
        return new ArrayList<>(playerClients.keySet());
    }

    @Override
    public void requestRound() throws RemoteException {
        if (finishedRoundClients.size() == playerClients.size() && !wasRun.getAndSet(true)) {
            System.out.println("CURRENT ROOOOUND: " + currentRound);
            if (currentRound == 3) {
                currentGame.setInProgress(false);
                gameRepository.update(currentGame);

                for (Map.Entry<String, IObserver> each : playerClients.entrySet()) {
                    each.getValue().endGame(currentWord, gameRepository.getPoints(each.getKey(), currentGame.getId(), currentRound));
                }

                pendingClients.putAll(playerClients);
                playerClients.clear();

                if (pendingClients.size() >= 2) {
                    for (String each : pendingClients.keySet())
                        notify(each);
                }
            } else {
                currentWord = wordRepository.chooseRandom().getWord();
                currentGame.setCurrentWord(currentWord);
                for (IObserver each : playerClients.values()) {
                    System.out.println("new round for obs");
                    each.newRound(currentGame.getId(), shuffle(currentWord), currentWord.length());
                }
                currentRound++;
            }

        }
    }

    @Override
    public synchronized void announceEnded(User user, IObserver client) {
        finished++;
        int score = gameRepository.getFinalScore(user.getUsername(), currentGame);
        if (score > biggestScore)  {
            biggestScore = score;
        }
        System.out.println(finished + " " + biggestScore + " " + pendingClients.size());
        if (finished == pendingClients.size()) {
            for (Map.Entry<String, IObserver> each : pendingClients.entrySet()) {
                System.out.println("scor " + each.getKey() + ": " + gameRepository.getFinalScore(each.getKey(), currentGame));
                notifyScore(each.getValue(), gameRepository.getFinalScore(each.getKey(), currentGame), biggestScore);
            }
            currentGame = null;
        }
    }

    private void notifyScore(IObserver client, int finalScore, int biggestScore) {
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        executor.execute(() -> {
            try {
                System.out.println("notifying winner...");
                client.wonOrNot(finalScore, biggestScore);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("error notifying player...");
            }
        });
        executor.shutdown();
    }
}
