package barcute.rest.services;
import barcute.model.Game;
import barcute.persistence.GameRepository;
import barcute.persistence.IGameRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/games")
public class Controller {

    private ApplicationContext factory = new ClassPathXmlApplicationContext("classpath:spring-rest.xml");
    private IGameRepository gameRepository = (GameRepository) factory.getBean("gameRepository");

    @CrossOrigin
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getGames(@PathVariable String id) {
        System.out.println("INSIDE CONTROLLER GET GAMES " + id);
        try {
            return new ResponseEntity<List<Game>>(gameRepository.getAll(Integer.parseInt(id)), HttpStatus.OK);
        }
        catch (NumberFormatException e) {
            return new ResponseEntity<String>("id should be a number!", HttpStatus.BAD_REQUEST);
        }
    }
}