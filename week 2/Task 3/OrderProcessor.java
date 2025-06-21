import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderProcessor {
    private static final Logger logger = LoggerFactory.getLogger(OrderProcessor.class);
    private static final int LOYALTY_POINTS_PER_ORDER = 10;

    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final PaymentGateway paymentGateway;
    private final InventoryService inventoryService;
    private final UserService userService;

    public OrderProcessor(
            OrderRepository orderRepository,
            EmailService emailService,
            PaymentGateway paymentGateway,
            InventoryService inventoryService,
            UserService userService) {
        this.orderRepository = orderRepository;
        this.emailService = emailService;
        this.paymentGateway = paymentGateway;
        this.inventoryService = inventoryService;
        this.userService = userService;
    }

    @Transactional
    public CompletableFuture<OrderResult> processOrder(Order order, User user) {
        logger.info("Starting order processing for orderId: {}", order.getId());
        
        return validateOrder(order, user)
            .thenCompose(validatedOrder -> processPayment(validatedOrder))
            .thenCompose(paidOrder -> updateInventory(paidOrder))
            .thenCompose(processedOrder -> saveOrder(processedOrder))
            .thenCompose(savedOrder -> sendConfirmation(savedOrder, user))
            .thenCompose(confirmedOrder -> updateLoyaltyPoints(confirmedOrder, user))
            .exceptionally(throwable -> {
                logger.error("Order processing failed", throwable);
                return OrderResult.failed(throwable);
            });
    }

    private CompletableFuture<Order> validateOrder(Order order, User user) {
        return CompletableFuture.supplyAsync(() -> {
            if (order == null || user == null) {
                throw new OrderValidationException("Order or User cannot be null");
            }

            if (!user.isActive()) {
                throw new OrderValidationException("User is not active");
            }

            if (order.getItems().isEmpty()) {
                throw new OrderValidationException("Order must contain items");
            }

            order.getItems().forEach(item -> {
                if (item.getStock() <= 0) {
                    throw new OrderValidationException("Item out of stock: " + item.getName());
                }
            });

            if (order.getPayment() == null || order.getPayment().getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new OrderValidationException("Invalid payment information");
            }

            return order;
        });
    }

    private CompletableFuture<Order> processPayment(Order order) {
        return CompletableFuture.supplyAsync(() -> {
            boolean paymentSuccess = paymentGateway.process(order.getPayment());
            if (!paymentSuccess) {
                throw new PaymentProcessingException("Payment failed for order: " + order.getId());
            }
            return order;
        });
    }

    private CompletableFuture<Order> updateInventory(Order order) {
        return CompletableFuture.supplyAsync(() -> {
            order.getItems().forEach(item -> 
                inventoryService.updateStock(item.getId(), item.getStock() - 1));
            return order;
        });
    }

    private CompletableFuture<Order> saveOrder(Order order) {
        return CompletableFuture.supplyAsync(() -> {
            orderRepository.save(order);
            return order;
        });
    }

    private CompletableFuture<Order> sendConfirmation(Order order, User user) {
        return CompletableFuture.supplyAsync(() -> {
            String message = String.format("Hello %s, your order %s has been successfully placed.",
                user.getName(), order.getId());
            emailService.sendEmail(user.getEmail(), "Order Confirmation", message);
            return order;
        });
    }

    private CompletableFuture<OrderResult> updateLoyaltyPoints(Order order, User user) {
        return CompletableFuture.supplyAsync(() -> {
            userService.addLoyaltyPoints(user.getId(), LOYALTY_POINTS_PER_ORDER);
            return OrderResult.success(order);
        });
    }
} 