package game.rest.services;
import game.model.Card;
import game.model.Game;
import game.model.User;
import game.persistence.*;
import org.graalvm.compiler.debug.CSVUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/games")
public class Controller {

    private ApplicationContext factory = new ClassPathXmlApplicationContext("classpath:spring-rest.xml");
    private IUserRepository userRepository = (UserRepository) factory.getBean("userRepository");
    private IGameRepository gameRepository = (GameRepository) factory.getBean("gameRepository");
    private ICardRepository cardRepository = (CardRepository) factory.getBean("cardRepository");

    @CrossOrigin
    @RequestMapping(value = "/{gameId}", method = RequestMethod.GET)
    public ResponseEntity<?> getInfo(@PathVariable int gameId) {
        System.out.println("INSIDE CONTROLLER GET INFO");
        try {
            Map<String, List<Card>> map = new HashMap<>();
            Game game = gameRepository.findOne(gameId);
            for (User each : userRepository.getAll()) {
                if (cardRepository.getCardsFromServer(game, each.getUsername()).size() > 0)
                    map.put(each.getUsername(), cardRepository.getCardsFromServer(game, each.getUsername()));
            }
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("Invalid request!", HttpStatus.BAD_REQUEST);
        }
    }
}