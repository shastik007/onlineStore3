package online.db.servise;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.db.model.Basket;
import online.db.model.Products;
import online.db.model.SecondCategory;
import online.db.model.dto.ChildOrderDto;
import online.db.model.dto.MessageResponse;
import online.db.model.dto.OrderDto;
import online.db.model.dto.ProductCard;
import online.db.repository.BasketRepository;
import online.db.repository.ProductRepository;
import online.db.repository.SecondCategoryRepository;
import online.db.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {

    ProductRepository productRepository;
    SecondCategoryRepository nextCategoryRepository;
    UserRepository userRepository;
    BasketRepository basketRepository;
    MailSenderToAdmin mailSender;

    /**
     * Admin
     */

    public Products saveProduct(Products products, Long id) {

        SecondCategory nextCategory = nextCategoryRepository.findById(id).get();
        Products entity = new Products();
        entity.setPrice(products.getPrice());
        entity.setImage(products.getImage());
        entity.setManufacturer(products.getManufacturer());
        entity.setModel(products.getModel());
        entity.setAbout(products.getAbout());
        entity.setWeight(products.getWeight());
        entity.setSecondCategory(nextCategory);

        return productRepository.save(entity);
    }

    @Transactional
    public Products updateProduct(Products products, Long id) {
        Products oldProduct = productRepository.findById(id).get();

        String oldName = oldProduct.getAbout();
        String newName = products.getAbout();
        if (!oldName.equals(newName)) {
            oldProduct.setAbout(newName);
        }

        String old = oldProduct.getManufacturer();
        String news = products.getManufacturer();
        if (!old.equals(news)) {
            oldProduct.setManufacturer(news);
        }

        Double oldPrice = oldProduct.getPrice();
        Double newPrice = products.getPrice();
        if (!oldPrice.equals(newPrice)) {
            oldProduct.setPrice(newPrice);
        }

        String oldModel = oldProduct.getModel();
        String newModel = products.getModel();
        if (!oldModel.equals(newModel)) {
            oldProduct.setModel(newModel);
        }

        int oldW = oldProduct.getWeight();
        int newW = products.getWeight();
        if (!Objects.equals(oldW, newW)) {
            oldProduct.setWeight(newW);
        }

        if (Objects.nonNull(products.getImage()))
            oldProduct.setImage(products.getImage());

        productRepository.save(oldProduct);

        return oldProduct;
    }

    public String deleteProductById(Long id) {
        productRepository.deleteById(id);
        return "Delete Product Successfully";
    }

    /**
     * Client
     */

    public List<Products> getAllProducts(Long nextId) {
        return productRepository.getAllByNextCategory(nextId);
    }

    public HashSet<Products> findProductByModel(String model) {
        List<Products> likeModel = productRepository.findAllByModel(model);
        List<Products> allProduct = productRepository.findAll();
        List<Products> toSearch = new ArrayList<>();

        for (Products products : allProduct) {
            if (products.getModel().equalsIgnoreCase(model))
                toSearch.add(products);
        }
        toSearch.addAll(likeModel);

        return new HashSet<>(toSearch);
    }

    public Products getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    throw new NotFoundException(
                            String.format("Product with id %s doesn't exist!", id)
                    );
                });
    }

    @Transactional
    public ResponseEntity<?> addBookToBasket(OrderDto order) {

//        if (basketRepository.checkIfAlreadyClientPutInBasket(
//                getUsersBasketId(username), orderId) > 0) {
//            throw new BadRequestException("You already put this book in your basket");
//        }

//        User user = userRepository.getUser(username).orElseThrow(() ->
//                new NotFoundException(String.format("User with username %s not found", username)));

        List<ProductCard> productCards = new ArrayList<>();
        order.getOrders().forEach(el -> {
            ProductCard productCard = new ProductCard();
            productCard.setProductId(productRepository.findById(el.getProductId()).orElse(null));
            productCard.setCount(el.getCount());
            productCards.add(productCard);
        });


        Basket basket = new Basket();
        basket.setFullName(order.getFullName());
        basket.setProductCards(productCards);
        basket.setNumber(order.getPhoneNumber());
        basket.setAddress(order.getAddress());
        basket.setComment(order.getComment());
        Basket save = basketRepository.save(basket);

        sendDataToAdmin(order);
        return ResponseEntity.ok(new MessageResponse(String.format("Order with id %s has been added to basket of user",
                save.getBasketId())));
    }

    private void sendDataToAdmin(OrderDto orderDto) {
        List<Products> products = findByProductsId(orderDto.getOrders());

        String comment = orderDto.getComment() != null ? orderDto.getComment() : "";
        String address = orderDto.getAddress() != null ? orderDto.getAddress() : "";
        StringBuilder basket = new StringBuilder();
        String userData = "ФИО: " + orderDto.getFullName() + ", Телефон: " + orderDto.getPhoneNumber() + ", Коммент: " + comment + ", Адрес: " + address;

        for (ChildOrderDto order : orderDto.getOrders()) {
            Products product = productRepository.findById(order.getProductId()).orElseThrow();
            basket.append("Модел: ").append(product.getModel()).append(", Количество: ").append(order.getCount()).append(", Цена: ").append(product.getPrice()).append("\n");
        }

        mailSender.sendEmailToAdmin(basket.toString(), userData);
    }

    private List<Products> findByProductsId(List<ChildOrderDto> orders) {
        List<Products> products = new ArrayList<>();

        for (ChildOrderDto order : orders) {
            products.add(productRepository.findById(order.getProductId()).orElseThrow());
        }

        return products;
    }


    public Long getUsersBasketId(String username) {
        return basketRepository.getUsersBasketId(username);
    }


}
