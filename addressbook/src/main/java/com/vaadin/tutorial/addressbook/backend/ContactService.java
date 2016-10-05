package com.vaadin.tutorial.addressbook.backend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Separate Java service class. Backend implementation for the address book
 * application, with "detached entities" simulating real world DAO. Typically
 * these something that the Java EE or Spring backend services provide.
 */
// Backend service class. This is just a typical Java backend implementation
// class and nothing Vaadin specific.
public class ContactService {

    // Create dummy data by randomly combining first and last names
    static String[] fnames = { "Peter", "Alice", "John", "Mike", "Olivia",
            "Nina", "Alex", "Rita", "Dan", "Umberto", "Henrik", "Rene", "Lisa",
            "Linda", "Timothy", "Daniel", "Brian", "George", "Scott",
            "Jennifer" };
    static String[] lnames = { "Smith", "Johnson", "Williams", "Jones", "Brown",
            "Davis", "Miller", "Wilson", "Moore", "Taylor", "Anderson",
            "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson",
            "Young", "King", "Robinson" };

    private static final ContactService INSTANCE = createDemoService();

    public static ContactService getDemoService() {
        return INSTANCE;
    }

    private static ContactService createDemoService() {
        final ContactService contactService = new ContactService();

        Random r = new Random(0);
        for (int i = 0; i < 100; i++) {
            Contact contact = new Contact();
            contact.setFirstName(fnames[r.nextInt(fnames.length)]);
            contact.setLastName(lnames[r.nextInt(fnames.length)]);
            contact.setEmail(contact.getFirstName().toLowerCase() + "@"
                    + contact.getLastName().toLowerCase() + ".com");
            contact.setPhone("+ 358 555 " + (100 + r.nextInt(900)));
            LocalDate birthday = LocalDate.of(1930 + r.nextInt(70),
                    1 + r.nextInt(11), 1 + r.nextInt(27));
            contact.setBirthDate(birthday);
            contact.setDoNotCall(r.nextBoolean());
            contactService.save(contact);
        }
        return contactService;
    }

    private HashMap<Long, Contact> contacts = new HashMap<>();
    private long nextId = 0;

    public synchronized List<Contact> findAll(String stringFilter) {
        ArrayList<Contact> arrayList = new ArrayList<>();
        for (Contact contact : contacts.values()) {
            try {
                boolean passesFilter = stringFilter == null
                        || stringFilter.isEmpty()
                        || contact.toString().toLowerCase()
                                .contains(stringFilter.toLowerCase());
                if (passesFilter) {
                    arrayList.add(contact.clone());
                }
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(ContactService.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
        Collections.sort(arrayList, new Comparator<Contact>() {

            @Override
            public int compare(Contact o1, Contact o2) {
                return (int) (o2.getId() - o1.getId());
            }
        });
        return arrayList;
    }

    public synchronized long count() {
        return contacts.size();
    }

    public synchronized void delete(Contact value) {
        contacts.remove(value.getId());
    }

    public synchronized void save(Contact entry) {
        if (entry.getId() == null) {
            entry.setId(nextId++);
        }
        try {
            entry = entry.clone();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        contacts.put(entry.getId(), entry);
    }

}
