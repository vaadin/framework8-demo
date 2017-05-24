package org.vaadin.example.treegrid.jdbc;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.IconGenerator;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.UI;
import org.vaadin.example.treegrid.jdbc.pojo.Company;
import org.vaadin.example.treegrid.jdbc.pojo.Department;
import org.vaadin.example.treegrid.jdbc.pojo.NamedItem;
import org.vaadin.example.treegrid.jdbc.pojo.Person;

import javax.servlet.annotation.WebServlet;
import java.util.Objects;

/**
 * This UI is the application entry point.
 */
@SuppressWarnings("unused")
public class TreeUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final HorizontalLayout layout = new HorizontalLayout();
        layout.setMargin(true);
        layout.setSizeFull();
        TreeGrid<NamedItem> treeGrid = setupTreeGrid();
        treeGrid.setHeight("50%");

        Tree<NamedItem> tree = setupTree();
        Panel treePanel = new Panel(tree);
        treePanel.setHeight("50%");

        layout.addComponentsAndExpand(treeGrid, treePanel);

        treeGrid.addExpandListener(expandEvent -> tree.expand(expandEvent.getExpandedItem()));
        treeGrid.addCollapseListener(expandEvent -> tree.collapse(expandEvent.getCollapsedItem()));

        tree.addExpandListener(expandEvent -> treeGrid.expand(expandEvent.getExpandedItem()));
        tree.addCollapseListener(expandEvent -> treeGrid.collapse(expandEvent.getCollapsedItem()));

        setContent(layout);
    }

    private TreeGrid<NamedItem> setupTreeGrid() {
        TreeGrid<NamedItem> treeGrid = new TreeGrid<>();

        treeGrid.addColumn(NamedItem::getName).setId("name").setCaption("Name");
        treeGrid.setHierarchyColumn("name");

        treeGrid.addColumn(ofPerson(Person::getFirstName)).setCaption("First Name");
        treeGrid.addColumn(ofPerson(Person::getLastName)).setCaption("Last Name");
        treeGrid.addColumn(new EmailGenerator()).setCaption("e-mail");
        treeGrid.addColumn(ofPerson(Person::getGender)).setCaption("Gender");
        treeGrid.setDataProvider(new PeopleData());
        return treeGrid;
    }

    private Tree<NamedItem> setupTree() {
        Tree<NamedItem> tree = new Tree<>();
        tree.setDataProvider(new PeopleData());
        tree.setItemCaptionGenerator(NamedItem::getName);
        tree.setItemIconGenerator(new PeopleIconGenerator());
        return tree;
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = TreeUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }

    private static ValueProvider<NamedItem, String> ofPerson(ValueProvider<Person, String> personExtractor) {
        return (NamedItem item) -> {
            if (item instanceof Person) {
                return personExtractor.apply((Person) item);
            } else {
                return "--";
            }
        };
    }

    private static class PeopleIconGenerator implements IconGenerator<NamedItem> {
        @Override
        public Resource apply(NamedItem p) {
            if (p instanceof Person) {
                String gender = Objects.toString(((Person) p).getGender(), "");
                switch (gender.toUpperCase()) {
                    case "MALE":
                        return VaadinIcons.MALE;
                    case "FEMALE":
                        return VaadinIcons.FEMALE;
                    default:
                        return VaadinIcons.USER;
                }
            } else if (p instanceof Department) {
                return VaadinIcons.GROUP;
            } else {
                return VaadinIcons.OFFICE;
            }
        }
    }

    public static class EmailGenerator implements ValueProvider<NamedItem, String> {

        @Override
        public String apply(NamedItem namedItem) {
            if (namedItem instanceof Company) {
                String domainName = namedItem.getName().replaceAll("^[A-Za-z0-9.-]", "").toLowerCase();
                return "inbox@" + domainName + ".com";
            };
            if (namedItem instanceof Person) return ((Person) namedItem).getEmail();
            return "--";
        }
    }
}
