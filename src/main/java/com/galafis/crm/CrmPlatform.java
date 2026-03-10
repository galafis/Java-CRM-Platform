package com.galafis.crm;

import java.util.*;
import java.util.concurrent.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Customer Relationship Management Platform
 * Full-featured CRM with customer management, interaction tracking,
 * sales pipeline, and analytics.
 *
 * @author Gabriel Demetrios Lafis
 * @version 2.0.0
 */
public class CrmPlatform {

    private static final Logger LOGGER = Logger.getLogger(CrmPlatform.class.getName());
    private final Map<String, Customer> customers = new ConcurrentHashMap<>();
    private final Map<String, Interaction> interactions = new ConcurrentHashMap<>();
    private final Map<String, Deal> deals = new ConcurrentHashMap<>();

    // ---- Customer ----

    public static class Customer {
        private final String id;
        private String name;
        private String email;
        private String company;
        private String segment;
        private final LocalDateTime createdAt;
        private double lifetimeValue;
        private final Map<String, String> customFields;

        public Customer(String name, String email, String company, String segment) {
            this.id = "CUST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            this.name = name;
            this.email = email;
            this.company = company;
            this.segment = segment;
            this.createdAt = LocalDateTime.now();
            this.lifetimeValue = 0;
            this.customFields = new ConcurrentHashMap<>();
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getCompany() { return company; }
        public String getSegment() { return segment; }
        public double getLifetimeValue() { return lifetimeValue; }
        public void addValue(double amount) { this.lifetimeValue += amount; }
    }

    // ---- Interaction ----

    public static class Interaction {
        public enum Type { EMAIL, CALL, MEETING, NOTE, SUPPORT_TICKET }

        private final String id;
        private final String customerId;
        private final Type type;
        private final String subject;
        private final String notes;
        private final LocalDateTime timestamp;

        public Interaction(String customerId, Type type, String subject, String notes) {
            this.id = "INT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            this.customerId = customerId;
            this.type = type;
            this.subject = subject;
            this.notes = notes;
            this.timestamp = LocalDateTime.now();
        }

        public String getId() { return id; }
        public String getCustomerId() { return customerId; }
        public Type getType() { return type; }
        public String getSubject() { return subject; }
    }

    // ---- Deal (Sales Pipeline) ----

    public static class Deal {
        public enum Stage { PROSPECTING, QUALIFICATION, PROPOSAL, NEGOTIATION, CLOSED_WON, CLOSED_LOST }

        private final String id;
        private final String customerId;
        private String title;
        private double value;
        private Stage stage;
        private double probability;
        private final LocalDateTime createdAt;

        public Deal(String customerId, String title, double value) {
            this.id = "DEAL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            this.customerId = customerId;
            this.title = title;
            this.value = value;
            this.stage = Stage.PROSPECTING;
            this.probability = 0.1;
            this.createdAt = LocalDateTime.now();
        }

        public String getId() { return id; }
        public String getCustomerId() { return customerId; }
        public String getTitle() { return title; }
        public double getValue() { return value; }
        public Stage getStage() { return stage; }
        public double getProbability() { return probability; }

        public void advanceStage() {
            switch (stage) {
                case PROSPECTING: stage = Stage.QUALIFICATION; probability = 0.25; break;
                case QUALIFICATION: stage = Stage.PROPOSAL; probability = 0.50; break;
                case PROPOSAL: stage = Stage.NEGOTIATION; probability = 0.75; break;
                case NEGOTIATION: stage = Stage.CLOSED_WON; probability = 1.0; break;
                default: break;
            }
        }

        public void markLost() {
            stage = Stage.CLOSED_LOST;
            probability = 0;
        }
    }

    // ---- CRM Operations ----

    public Customer addCustomer(String name, String email, String company, String segment) {
        Customer customer = new Customer(name, email, company, segment);
        customers.put(customer.getId(), customer);
        LOGGER.info("Customer added: " + customer.getName() + " (" + customer.getId() + ")");
        return customer;
    }

    public Interaction logInteraction(String customerId, Interaction.Type type, String subject, String notes) {
        if (!customers.containsKey(customerId)) {
            throw new IllegalArgumentException("Customer not found: " + customerId);
        }
        Interaction interaction = new Interaction(customerId, type, subject, notes);
        interactions.put(interaction.getId(), interaction);
        return interaction;
    }

    public Deal createDeal(String customerId, String title, double value) {
        if (!customers.containsKey(customerId)) {
            throw new IllegalArgumentException("Customer not found: " + customerId);
        }
        Deal deal = new Deal(customerId, title, value);
        deals.put(deal.getId(), deal);
        return deal;
    }

    public void closeDeal(String dealId) {
        Deal deal = deals.get(dealId);
        if (deal == null) throw new IllegalArgumentException("Deal not found");
        while (deal.getStage() != Deal.Stage.CLOSED_WON && deal.getStage() != Deal.Stage.CLOSED_LOST) {
            deal.advanceStage();
        }
        Customer customer = customers.get(deal.getCustomerId());
        if (customer != null && deal.getStage() == Deal.Stage.CLOSED_WON) {
            customer.addValue(deal.getValue());
        }
    }

    public List<Customer> searchCustomers(String query) {
        String q = query.toLowerCase();
        return customers.values().stream()
                .filter(c -> c.getName().toLowerCase().contains(q)
                        || c.getEmail().toLowerCase().contains(q)
                        || c.getCompany().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    // ---- Analytics ----

    public Map<String, Object> generateAnalytics() {
        Map<String, Object> analytics = new LinkedHashMap<>();

        analytics.put("totalCustomers", customers.size());
        analytics.put("totalInteractions", interactions.size());
        analytics.put("totalDeals", deals.size());

        // Pipeline value
        double pipelineValue = deals.values().stream()
                .filter(d -> d.getStage() != Deal.Stage.CLOSED_WON && d.getStage() != Deal.Stage.CLOSED_LOST)
                .mapToDouble(d -> d.getValue() * d.getProbability())
                .sum();
        analytics.put("weightedPipelineValue", Math.round(pipelineValue * 100.0) / 100.0);

        // Won revenue
        double wonRevenue = deals.values().stream()
                .filter(d -> d.getStage() == Deal.Stage.CLOSED_WON)
                .mapToDouble(Deal::getValue)
                .sum();
        analytics.put("closedWonRevenue", wonRevenue);

        // Segment breakdown
        Map<String, Long> segments = customers.values().stream()
                .collect(Collectors.groupingBy(Customer::getSegment, Collectors.counting()));
        analytics.put("customerSegments", segments);

        // Deal stages
        Map<String, Long> stageCount = deals.values().stream()
                .collect(Collectors.groupingBy(d -> d.getStage().name(), Collectors.counting()));
        analytics.put("dealsByStage", stageCount);

        return analytics;
    }

    // ---- Demo ----

    public static void main(String[] args) {
        System.out.println("=== Java CRM Platform ===\n");

        CrmPlatform crm = new CrmPlatform();

        // Add customers
        Customer c1 = crm.addCustomer("Alice Santos", "alice@techcorp.com", "TechCorp", "Enterprise");
        Customer c2 = crm.addCustomer("Bruno Lima", "bruno@startup.io", "StartupIO", "SMB");
        Customer c3 = crm.addCustomer("Carla Mendes", "carla@bigcorp.com", "BigCorp", "Enterprise");
        Customer c4 = crm.addCustomer("Daniel Costa", "daniel@agency.com", "DigitalAgency", "Mid-Market");

        // Log interactions
        crm.logInteraction(c1.getId(), Interaction.Type.EMAIL, "Product Demo Request", "Interested in enterprise plan");
        crm.logInteraction(c1.getId(), Interaction.Type.MEETING, "Discovery Call", "Discussed requirements");
        crm.logInteraction(c2.getId(), Interaction.Type.CALL, "Follow-up", "Sent proposal");
        crm.logInteraction(c3.getId(), Interaction.Type.SUPPORT_TICKET, "Integration Issue", "Resolved ticket #1234");

        // Create deals
        Deal d1 = crm.createDeal(c1.getId(), "TechCorp Enterprise License", 50000);
        Deal d2 = crm.createDeal(c2.getId(), "StartupIO Annual Plan", 12000);
        Deal d3 = crm.createDeal(c3.getId(), "BigCorp Platform Migration", 85000);

        // Advance deals
        d1.advanceStage(); // QUALIFICATION
        d1.advanceStage(); // PROPOSAL
        d2.advanceStage(); // QUALIFICATION
        crm.closeDeal(d3.getId()); // CLOSED_WON

        // Search
        System.out.println("Search 'corp': " + crm.searchCustomers("corp").size() + " results");

        // Analytics
        System.out.println("\n--- CRM Analytics ---");
        Map<String, Object> analytics = crm.generateAnalytics();
        analytics.forEach((key, value) -> System.out.println("  " + key + ": " + value));

        System.out.println("\nCRM Platform running successfully!");
    }
}
