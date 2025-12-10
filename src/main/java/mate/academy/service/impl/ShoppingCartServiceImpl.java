package mate.academy.service.impl;

import mate.academy.dao.ShoppingCartDao;
import mate.academy.dao.TicketDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.model.MovieSession;
import mate.academy.model.ShoppingCart;
import mate.academy.model.Ticket;
import mate.academy.model.User;
import mate.academy.service.ShoppingCartService;

public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartDao shoppingCartDao;
    private final TicketDao ticketDao;

    public ShoppingCartServiceImpl(ShoppingCartDao shoppingCartDao, TicketDao ticketDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.ticketDao = ticketDao;
    }

    @Override
    public void addSession(MovieSession movieSession, User user) {
        ShoppingCart cart = shoppingCartDao.getByUser(user)
                .orElseGet(() -> {
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUser(user);
                    return shoppingCartDao.add(newCart);
                });

        Ticket ticket = new Ticket();
        ticket.setMovieSession(movieSession);
        ticket.setUser(user);
        cart.getTickets().add(ticket);
        shoppingCartDao.update(cart);
    }

    @Override
    public ShoppingCart getByUser(User user) {
        return shoppingCartDao.getByUser(user)
                .orElseThrow(() ->
                        new DataProcessingException("ShoppingCart not found for user", null));
    }

    @Override
    public void registerNewShoppingCart(User user) {
        ShoppingCart cart = new ShoppingCart();
        cart.setUser(user);
        shoppingCartDao.add(cart);
    }

    @Override
    public void clear(ShoppingCart shoppingCart) {
        shoppingCart.getTickets().clear();
        shoppingCartDao.update(shoppingCart);
    }
}
