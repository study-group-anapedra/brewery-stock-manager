package com.anapedra.stock_manager;

import com.anapedra.stock_manager.domain.entities.*;
import com.anapedra.stock_manager.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class StockManagerApplication  {
//
//    private final BeerRepository beerRepository;
//    private final CategoryRepository categoryRepository;
//    private final UserRepository userRepository;
//    private final OrderItemRepository orderItemRepository;
//    private final OrderRepository orderRepository;
//    private final RoleRepository roleRepository;
//    private final PaymentRepository paymentRepository;
//
//    public StockManagerApplication(
//            BeerRepository beerRepository,
//            CategoryRepository categoryRepository,
//            UserRepository userRepository,
//            OrderItemRepository orderItemRepository,
//            OrderRepository orderRepository,
//            RoleRepository roleRepository,
//            PaymentRepository paymentRepository) {
//        this.beerRepository = beerRepository;
//        this.categoryRepository = categoryRepository;
//        this.userRepository = userRepository;
//        this.orderItemRepository = orderItemRepository;
//        this.orderRepository = orderRepository;
//        this.roleRepository = roleRepository;
//        this.paymentRepository = paymentRepository;
//    }


	public static void main(String[] args) {
		SpringApplication.run(StockManagerApplication.class, args);
	}
//
//    @Override
//    public void run(String... args) throws Exception {
//        List<Category> categories = new ArrayList<>();
//        List<Beer> beers = new ArrayList<>();
//        List<User> allUsers = new ArrayList<>();
//        List<Role> roles = new ArrayList<>();
//        List<OrderItem> orderItems = new ArrayList<>();
//        List<Order> orders = new ArrayList<>();
//        List<Payment> payments = new ArrayList<>();
//
//        Role role1 = new Role(null, "ROLE_ADMIN");
//        Role role2 = new Role(null, "ROLE_CLIENT");
//        roles.addAll(Arrays.asList(role1, role2));
//
//        User clAndAd1 = new User(null, "Luiza Brandão", "luiza@gmail.com", "8177906788", LocalDate.parse("2015-02-23"), "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG", Instant.now(), Instant.now(), "307.460.850-00");
//        User clAndAd2 = new User(null, "Mara Vascon", "vascon@gmail.com", "81999887766", LocalDate.parse("2019-02-23"), "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG", Instant.now(), Instant.now(), "488.298.100-10");
//        User cl3 = new User(null, "Lucas Marcone Silva", "lucas@gmail.com", "81988776655", LocalDate.parse("2015-05-22"), "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG", Instant.now(), Instant.now(), "598.566.190-20");
//        User cl4 = new User(null, "Ana Bragalha", "bragalha@gmail.com", "12366444755", LocalDate.parse("2019-09-22"), "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG", Instant.now(), Instant.now(), "407.242.380-30");
//        User cl5 = new User(null, "Mari Ferreira", "ferreira@gmail.com", "35988664588", LocalDate.parse("2011-02-12"), "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG", Instant.now(), Instant.now(), "499.367.000-40");
//        User cl6 = new User(null, "Sandra Megal Simão", "megal@gmail.com", "62933446855", LocalDate.parse("2017-01-20"), "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG", Instant.now(), Instant.now(), "515.504.430-50");
//        User cl7 = new User(null, "Helena Saldanha Filipa", "helena@gmail.com", "61996585744", LocalDate.parse("2018-02-21"), "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG", Instant.now(), Instant.now(), "353.586.090-60");
//        User cl8 = new User(null, "Mariana Prado", "mariana@gmail.com", "87999435555", LocalDate.parse("2011-10-18"), "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG", Instant.now(), Instant.now(), "918.324.580-70");
//        User cl9 = new User(null, "Socorro Ribeiro", "socorro@gmail.com", "61869389211", LocalDate.parse("2019-01-23"), "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG", Instant.now(), Instant.now(), "564.677.060-80");
//        User cl10 = new User(null, "Ana Prado Santana", "anaprado@gmail.com", "12236554788", LocalDate.parse("2017-06-02"), "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG", Instant.now(), Instant.now(), "948.320.260-90");
//        User cl11 = new User(null, "Sandra Marcodes", "saidra@gmail.com", "78889654211", LocalDate.parse("2013-02-28"), "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG", Instant.now(), Instant.now(), "861.322.570-00");
//        User cl12 = new User(null, "Ana Matias", "anamartins@gmail.com", "12936554788", LocalDate.parse("2018-09-20"), "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG", Instant.now(), Instant.now(), "371.032.700-11");
//        User cl13 = new User(null, "Mari Samaria", "mari@gmail.com", "45986987122", LocalDate.parse("2000-09-20"), "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG", Instant.now(), Instant.now(), "653.812.100-22");
//        User cl14 = new User(null, "Sandra Osca Sintra", "sanfraosca@gmail.com", "78789654211", LocalDate.parse("2017-10-21"), "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG", Instant.now(), Instant.now(), "779.187.500-33");
//
//        allUsers.addAll(Arrays.asList(clAndAd1, clAndAd2, cl3, cl4, cl5, cl6, cl7, cl8, cl9, cl10, cl11, cl12, cl13, cl14));
//
//
//        clAndAd1.getRoles().addAll(Arrays.asList(role1, role2));
//        clAndAd2.getRoles().addAll(Arrays.asList(role1, role2));
//        cl3.getRoles().add(role2);
//        cl4.getRoles().add(role2);
//        cl5.getRoles().add(role2);
//        cl6.getRoles().add(role2);
//        cl7.getRoles().add(role2);
//        cl8.getRoles().add(role2);
//        cl9.getRoles().add(role2);
//        cl10.getRoles().add(role2);
//        cl11.getRoles().add(role2);
//        cl12.getRoles().add(role2);
//        cl13.getRoles().add(role2);
//        cl14.getRoles().add(role2);
//
//
//        Category cat1 = new Category(null, "Lagers", "Pilsen e American Lager");
//        Category cat2 = new Category(null, "Ales", "Pale Ale, IPA");
//        Category cat3 = new Category(null, "Wheat Beers", "Witbier, Weizenbier");
//        Category cat4 = new Category(null, "Stouts & Porters", "Cervejas Escuras");
//        Category cat5 = new Category(null, "Sour Beers", "Cervejas Ácidas");
//        Category cat6 = new Category(null, "Belgian Styles", "Tripel e Dubbel");
//        Category cat7 = new Category(null, "High ABV", "Cervejas fortes");
//        Category cat8 = new Category(null, "Fruit Beers", "Cervejas com frutas");
//        Category cat9 = new Category(null, "Specialty", "Cervejas Especiais");
//        Category cat10 = new Category(null, "Low Carb", "Cervejas Leves");
//        categories.addAll(Arrays.asList(cat1, cat2, cat3, cat4, cat5, cat6, cat7, cat8, cat9, cat10));
//
//        Beer bk1 = new Beer(null, "Pilsen Leve", "imgUrl1.com/pilsen.jpg", 4.5, 7.50, LocalDate.now(), LocalDate.now().plusMonths(6));
//        Beer bk2 = new Beer(null, "IPA Artesanal", "imgUrl2.com/ipa.jpg", 6.2, 18.00, LocalDate.now(), LocalDate.now().plusMonths(9));
//        Beer bk3 = new Beer(null, "Weizen Tradicional", "imgUrl3.com/weizen.jpg", 5.5, 12.50, LocalDate.now(), LocalDate.now().plusMonths(4));
//        Beer bk4 = new Beer(null, "Stout de Café", "imgUrl4.com/stout.jpg", 7.0, 22.00, LocalDate.now(), LocalDate.now().plusMonths(12));
//        Beer bk5 = new Beer(null, "Gose Salgada", "imgUrl5.com/gose.jpg", 4.8, 15.00, LocalDate.now(), LocalDate.now().plusMonths(6));
//        Beer bk6 = new Beer(null, "Tripel Clássica", "imgUrl6.com/tripel.jpg", 9.0, 25.00, LocalDate.now(), LocalDate.now().plusMonths(18));
//        Beer bk7 = new Beer(null, "Double IPA", "imgUrl7.com/dipa.jpg", 8.5, 28.00, LocalDate.now(), LocalDate.now().plusMonths(9));
//        Beer bk8 = new Beer(null, "Witbier Cítrica", "imgUrl8.com/witbier.jpg", 4.9, 14.00, LocalDate.now(), LocalDate.now().plusMonths(4));
//        Beer bk9 = new Beer(null, "Porter Defumada", "imgUrl9.com/porter.jpg", 5.8, 19.50, LocalDate.now(), LocalDate.now().plusMonths(12));
//        Beer bk10 = new Beer(null, "Saison de Frutas", "imgUrl10.com/saison.jpg", 6.5, 17.00, LocalDate.now(), LocalDate.now().plusMonths(6));
//        beers.addAll(Arrays.asList(bk1, bk2, bk3, bk4, bk5, bk6, bk7, bk8, bk9, bk10));
//
//        bk1.getCategories().addAll(Arrays.asList(cat1, cat10));
//        bk2.getCategories().addAll(Arrays.asList(cat2));
//        bk3.getCategories().addAll(Arrays.asList(cat3));
//        bk4.getCategories().addAll(Arrays.asList(cat4));
//        bk5.getCategories().addAll(Arrays.asList(cat5));
//        bk6.getCategories().addAll(Arrays.asList(cat6, cat7));
//        bk7.getCategories().addAll(Arrays.asList(cat2, cat7));
//        bk8.getCategories().addAll(Arrays.asList(cat3, cat8));
//        bk9.getCategories().addAll(Arrays.asList(cat4));
//        bk10.getCategories().addAll(Arrays.asList(cat8, cat6));
//
//        Order o1 = new Order(null, Instant.parse("2021-01-01T10:00:00Z"), clAndAd1);
//        Order o2 = new Order(null, Instant.parse("2021-01-02T11:00:00Z"), clAndAd1);
//        Order o3 = new Order(null, Instant.parse("2021-01-03T12:00:00Z"), clAndAd1);
//        Order o4 = new Order(null, Instant.parse("2021-02-01T10:00:00Z"), clAndAd2);
//        Order o5 = new Order(null, Instant.parse("2021-02-02T11:00:00Z"), clAndAd2);
//        Order o6 = new Order(null, Instant.parse("2021-02-03T12:00:00Z"), clAndAd2);
//        Order o7 = new Order(null, Instant.parse("2021-03-01T10:00:00Z"), cl3);
//        Order o8 = new Order(null, Instant.parse("2021-03-02T11:00:00Z"), cl3);
//        Order o9 = new Order(null, Instant.parse("2021-03-03T12:00:00Z"), cl3);
//        Order o10 = new Order(null, Instant.parse("2021-04-01T10:00:00Z"), cl4);
//        Order o11 = new Order(null, Instant.parse("2021-04-02T11:00:00Z"), cl4);
//        Order o12 = new Order(null, Instant.parse("2021-05-01T10:00:00Z"), cl5);
//        Order o13 = new Order(null, Instant.parse("2021-05-02T11:00:00Z"), cl5);
//        Order o14 = new Order(null, Instant.parse("2021-06-01T10:00:00Z"), cl6);
//        Order o15 = new Order(null, Instant.parse("2021-06-02T11:00:00Z"), cl6);
//        Order o16 = new Order(null, Instant.parse("2021-07-01T10:00:00Z"), cl7);
//        Order o17 = new Order(null, Instant.parse("2021-07-02T11:00:00Z"), cl7);
//        Order o18 = new Order(null, Instant.parse("2021-08-01T10:00:00Z"), cl8);
//        Order o19 = new Order(null, Instant.parse("2021-08-02T11:00:00Z"), cl8);
//        Order o20 = new Order(null, Instant.parse("2021-09-01T10:00:00Z"), cl9);
//        Order o21 = new Order(null, Instant.parse("2021-09-02T11:00:00Z"), cl9);
//        Order o22 = new Order(null, Instant.parse("2021-10-01T10:00:00Z"), cl10);
//        Order o23 = new Order(null, Instant.parse("2021-10-02T11:00:00Z"), cl10);
//        Order o24 = new Order(null, Instant.parse("2021-11-01T10:00:00Z"), cl11);
//        Order o25 = new Order(null, Instant.parse("2021-11-02T11:00:00Z"), cl11);
//        Order o26 = new Order(null, Instant.parse("2021-12-01T10:00:00Z"), cl12);
//        Order o27 = new Order(null, Instant.parse("2021-12-02T11:00:00Z"), cl12);
//        Order o28 = new Order(null, Instant.parse("2022-01-01T10:00:00Z"), cl13);
//        Order o29 = new Order(null, Instant.parse("2022-01-02T11:00:00Z"), cl13);
//        Order o30 = new Order(null, Instant.parse("2022-02-01T10:00:00Z"), cl14);
//
//        orders.addAll(Arrays.asList(
//                o1, o2, o3, o4, o5, o6, o7, o8, o9, o10,
//                o11, o12, o13, o14, o15, o16, o17, o18, o19, o20,
//                o21, o22, o23, o24, o25, o26, o27, o28, o29, o30
//        ));
//
//        OrderItem oi1 = new OrderItem(o1, bk1, 2, bk1.getPrice());
//        OrderItem oi2 = new OrderItem(o1, bk2, 1, bk2.getPrice());
//        OrderItem oi3 = new OrderItem(o2, bk3, 3, bk3.getPrice());
//        OrderItem oi4 = new OrderItem(o2, bk4, 1, bk4.getPrice());
//        OrderItem oi5 = new OrderItem(o3, bk5, 2, bk5.getPrice());
//        OrderItem oi6 = new OrderItem(o3, bk6, 1, bk6.getPrice());
//        OrderItem oi7 = new OrderItem(o4, bk1, 1, bk1.getPrice());
//        OrderItem oi8 = new OrderItem(o4, bk7, 2, bk7.getPrice());
//        OrderItem oi9 = new OrderItem(o5, bk2, 3, bk2.getPrice());
//        OrderItem oi10 = new OrderItem(o5, bk3, 1, bk3.getPrice());
//        OrderItem oi11 = new OrderItem(o6, bk4, 2, bk4.getPrice());
//        OrderItem oi12 = new OrderItem(o6, bk5, 1, bk5.getPrice());
//        OrderItem oi13 = new OrderItem(o7, bk6, 1, bk6.getPrice());
//        OrderItem oi14 = new OrderItem(o7, bk7, 2, bk7.getPrice());
//        OrderItem oi15 = new OrderItem(o8, bk8, 2, bk8.getPrice());
//        OrderItem oi16 = new OrderItem(o8, bk9, 1, bk9.getPrice());
//        OrderItem oi17 = new OrderItem(o9, bk10, 2, bk10.getPrice());
//        OrderItem oi18 = new OrderItem(o9, bk1, 1, bk1.getPrice());
//        OrderItem oi19 = new OrderItem(o10, bk2, 2, bk2.getPrice());
//        OrderItem oi20 = new OrderItem(o11, bk3, 1, bk3.getPrice());
//        OrderItem oi21 = new OrderItem(o12, bk4, 2, bk4.getPrice());
//        OrderItem oi22 = new OrderItem(o13, bk5, 1, bk5.getPrice());
//        OrderItem oi23 = new OrderItem(o14, bk6, 2, bk6.getPrice());
//        OrderItem oi24 = new OrderItem(o15, bk7, 1, bk7.getPrice());
//        OrderItem oi25 = new OrderItem(o16, bk8, 2, bk8.getPrice());
//        OrderItem oi26 = new OrderItem(o17, bk9, 1, bk9.getPrice());
//        OrderItem oi27 = new OrderItem(o18, bk10, 2, bk10.getPrice());
//        OrderItem oi28 = new OrderItem(o19, bk1, 1, bk1.getPrice());
//        OrderItem oi29 = new OrderItem(o20, bk2, 2, bk2.getPrice());
//        OrderItem oi30 = new OrderItem(o21, bk3, 1, bk3.getPrice());
//        OrderItem oi31 = new OrderItem(o22, bk4, 2, bk4.getPrice());
//        OrderItem oi32 = new OrderItem(o23, bk5, 1, bk5.getPrice());
//        OrderItem oi33 = new OrderItem(o24, bk6, 2, bk6.getPrice());
//        OrderItem oi34 = new OrderItem(o25, bk7, 1, bk7.getPrice());
//        OrderItem oi35 = new OrderItem(o26, bk8, 2, bk8.getPrice());
//        OrderItem oi36 = new OrderItem(o27, bk9, 1, bk9.getPrice());
//        OrderItem oi37 = new OrderItem(o28, bk10, 2, bk10.getPrice());
//        OrderItem oi38 = new OrderItem(o29, bk1, 1, bk1.getPrice());
//        OrderItem oi39 = new OrderItem(o30, bk2, 2, bk2.getPrice());
//
//        orderItems.addAll(Arrays.asList(
//                oi1, oi2, oi3, oi4, oi5, oi6, oi7, oi8, oi9, oi10,
//                oi11, oi12, oi13, oi14, oi15, oi16, oi17, oi18, oi19, oi20,
//                oi21, oi22, oi23, oi24, oi25, oi26, oi27, oi28, oi29, oi30,
//                oi31, oi32, oi33, oi34, oi35, oi36, oi37, oi38, oi39
//        ));
//
//        Payment pay1 = new Payment(null, Instant.parse("2021-10-02T08:00:00Z"), o1);
//        Payment pay2 = new Payment(null, Instant.parse("2020-11-12T08:00:00Z"), o2);
//        Payment pay3 = new Payment(null, Instant.parse("2022-01-09T12:00:00Z"), o3);
//
//        payments.addAll(Arrays.asList(pay1, pay2, pay3));
//    }


}
