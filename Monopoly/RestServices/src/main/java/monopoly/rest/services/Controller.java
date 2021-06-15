package monopoly.rest.services;
import monopoly.persistence.GameRepository;
import monopoly.persistence.IGameRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/games")
public class Controller {

    private ApplicationContext factory = new ClassPathXmlApplicationContext("classpath:spring-rest.xml");
    private IGameRepository gameRepository = (GameRepository) factory.getBean("gameRepository");

    @CrossOrigin
    @RequestMapping(value = "/{gameId}/{username}", method = RequestMethod.GET)
    public Map<String, List<Integer>> getDeals(@PathVariable Integer gameId, @PathVariable String username) {
        System.out.println("INSIDE CONTROLLER GET DEALS");
        return gameRepository.getDeals(gameId, username);
    }

    @CrossOrigin
    @RequestMapping(value = "/{gameId}", method = RequestMethod.GET)
    public Map<String, Integer> getDeals(@PathVariable Integer gameId) {
        System.out.println("INSIDE CONTROLLER GET RANKINGS");
        return gameRepository.getRankings(gameId);
    }
}