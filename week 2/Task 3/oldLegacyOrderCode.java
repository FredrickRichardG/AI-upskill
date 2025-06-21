public class oldLegacyOrderCode {

    public void processOrder(Order order, User user) {
        Logger logger = Logger.getLogger("OrderLogger");
        logger.info("Starting order processing...");
    
        if (order == null || user == null) {
            logger.severe("Order or User is null");
            return;
        }
    
        try {
            if (!user.isActive()) {
                logger.warning("User is not active: " + user.getId());
                return;
            }
    
            logger.info("Validating items in order...");
            List<Item> items = order.getItems();
            if (items == null || items.size() == 0) {
                logger.warning("No items in the order.");
                return;
            }
    
            for (Item item : items) {
                if (item.getStock() <= 0) {
                    logger.warning("Item out of stock: " + item.getName());
                    return;
                }
            }
    
            logger.info("Checking payment info...");
            Payment payment = order.getPayment();
            if (payment == null || payment.getAmount() <= 0) {
                logger.severe("Invalid payment information.");
                return;
            }
    
            // Simulating inventory update
            for (Item item : items) {
                int newStock = item.getStock() - 1;
                item.setStock(newStock);
                logger.info("Updated stock for item " + item.getName() + ": " + newStock);
            }
    
            // Simulating payment processing
            boolean paymentSuccess = PaymentGateway.process(payment);
            if (!paymentSuccess) {
                logger.severe("Payment failed for order: " + order.getId());
                return;
            }
    
            // Save order
            OrderRepository repo = new OrderRepository();
            repo.save(order);
            logger.info("Order saved successfully.");
    
            // Simulate sending confirmation
            EmailService emailService = new EmailService();
            String message = "Hello " + user.getName() + ", your order " + order.getId() + " has been successfully placed.";
            emailService.sendEmail(user.getEmail(), "Order Confirmation", message);
    
            logger.info("Confirmation email sent.");
    
            // Update user points
            int points = user.getPoints();
            user.setPoints(points + 10);
            logger.info("Added loyalty points to user. Total points: " + user.getPoints());
    
        } catch (Exception e) {
            logger.severe("Exception while processing order: " + e.getMessage());
            e.printStackTrace();
        }
    
        logger.info("Order processing completed.");
    }
    
}
