package mate.academy;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import mate.academy.dao.CinemaHallDao;
import mate.academy.dao.MovieDao;
import mate.academy.dao.MovieSessionDao;
import mate.academy.dao.ShoppingCartDao;
import mate.academy.dao.TicketDao;
import mate.academy.dao.UserDao;
import mate.academy.dao.impl.CinemaHallDaoImpl;
import mate.academy.dao.impl.MovieDaoImpl;
import mate.academy.dao.impl.MovieSessionDaoImpl;
import mate.academy.dao.impl.ShoppingCartDaoImpl;
import mate.academy.dao.impl.TicketDaoImpl;
import mate.academy.dao.impl.UserDaoImpl;
import mate.academy.model.CinemaHall;
import mate.academy.model.Movie;
import mate.academy.model.MovieSession;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import mate.academy.service.ShoppingCartService;
import mate.academy.service.impl.ShoppingCartServiceImpl;

public class Main {
    public static void main(String[] args) {
        final UserDao userDao = new UserDaoImpl();
        final ShoppingCartDao shoppingCartDao = new ShoppingCartDaoImpl();
        final TicketDao ticketDao = new TicketDaoImpl();
        final MovieDao movieDao = new MovieDaoImpl();
        final CinemaHallDao cinemaHallDao = new CinemaHallDaoImpl();
        final MovieSessionDao movieSessionDao = new MovieSessionDaoImpl();
        final ShoppingCartService shoppingCartService =
                new ShoppingCartServiceImpl(shoppingCartDao, ticketDao);

        final String uniqueEmail = "john.doe" + System.currentTimeMillis() + "@example.com";
        final User user = new User();
        user.setEmail(uniqueEmail);
        user.setPassword("1234");
        user.setSalt("someSalt".getBytes(StandardCharsets.UTF_8));
        userDao.add(user);

        final User persistedUser = userDao.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        shoppingCartService.registerNewShoppingCart(persistedUser);

        final Movie fastAndFurious = new Movie("Fast and Furious");
        fastAndFurious.setDescription("Action film about street racing, heists, and spies.");
        movieDao.add(fastAndFurious);

        final CinemaHall firstCinemaHall = new CinemaHall();
        firstCinemaHall.setCapacity(100);
        firstCinemaHall.setDescription("First hall with 100 seats");
        cinemaHallDao.add(firstCinemaHall);

        final MovieSession tomorrowSession = new MovieSession();
        tomorrowSession.setMovie(fastAndFurious);
        tomorrowSession.setCinemaHall(firstCinemaHall);
        tomorrowSession.setShowTime(LocalDateTime.now().plusDays(1));
        movieSessionDao.add(tomorrowSession);

        shoppingCartService.addSession(tomorrowSession, persistedUser);

        ShoppingCart cart = shoppingCartService.getByUser(persistedUser);
        System.out.println("Tickets in cart: " + cart.getTickets().size());

        final MovieSession secondSession = new MovieSession();
        secondSession.setMovie(fastAndFurious);
        secondSession.setCinemaHall(firstCinemaHall);
        secondSession.setShowTime(LocalDateTime.now().plusDays(2));
        movieSessionDao.add(secondSession);

        shoppingCartService.addSession(secondSession, persistedUser);

        cart = shoppingCartService.getByUser(persistedUser);
        System.out.println("Tickets in cart after second add: " + cart.getTickets().size());

        shoppingCartService.clear(cart);
        System.out.println("Tickets after clear: " + cart.getTickets().size());
    }
}
