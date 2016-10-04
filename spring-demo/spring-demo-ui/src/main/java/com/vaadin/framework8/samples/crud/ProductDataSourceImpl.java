package com.vaadin.framework8.samples.crud;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;

import com.vaadin.framework8.samples.backend.data.Availability;
import com.vaadin.framework8.samples.backend.data.Category;
import com.vaadin.framework8.samples.backend.data.Product;
import com.vaadin.framework8.samples.backend.repository.CategoryRepository;
import com.vaadin.framework8.samples.backend.repository.ProductRepository;
import com.vaadin.server.data.AbstractDataSource;
import com.vaadin.server.data.Query;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.UI;

/**
 * DataSource implementation for managing {@code ProductRepository} and
 * filtering.
 * <p>
 * <strong>Note:</strong> This implementation can't be used between multiple
 * different components as the filtering is actually stateful and not stateless.
 */
@Component
// TODO: Use common data source for all UIs after backend filtering is done
@UIScope
@Transactional
public class ProductDataSourceImpl extends AbstractDataSource<Product>
        implements ProductDataSource {

    private static class PageQuery {
        Pageable pageable;
        int pageOffset;
    }

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    private String filterText;

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    public int size(Query t) {
        return (int) getItems(getPaging(t).pageable).count();
    }

    @Override
    public Stream<Product> fetch(Query t) {
        PageQuery pageQuery = getPaging(t);
        return getItems(pageQuery.pageable).skip(pageQuery.pageOffset)
                .limit(t.getLimit());
    }

    public void setFilterText(String filterText) {
        if (Objects.equals(this.filterText, filterText)) {
            return;
        }
        this.filterText = filterText;
        refreshAll();
    }

    public void save(Product product) {
        productRepo.save(product);
        refreshAll();
    }

    public void delete(Product product) {
        productRepo.delete(product);
        refreshAll();
    }

    private Collection<Category> getFilteredCategories(String string) {
        return categoryRepo.findAllByNameContainingIgnoreCase(string);
    }

    private List<Availability> getFilteredAvailabilities(String string) {
        Locale locale = UI.getCurrent().getLocale();
        return Arrays.stream(Availability.values())
                .filter(a -> a.name().toLowerCase(locale).contains(string))
                .collect(Collectors.toList());
    }

    private Stream<Product> getItems(Pageable page) {
        if (filterText == null || filterText.isEmpty()) {
            return StreamSupport.stream(productRepo.findAll(page).spliterator(),
                    false);
        }
        String filter = filterText.toLowerCase(UI.getCurrent().getLocale());
        return productRepo
                .findAllByProductNameContainingIgnoreCaseOrAvailabilityInOrCategoryIn(
                        filter, getFilteredAvailabilities(filter),
                        getFilteredCategories(filter), page)
                .stream();
    }

    /**
     * Return a PageQuery object containing page request and offset in page.
     * 
     * @param q
     *            the original query
     * @return paged query
     */
    private PageQuery getPaging(Query q) {
        final PageQuery p = new PageQuery();
        int start = q.getOffset();
        int end = q.getOffset() + q.getLimit();

        if (start < end - start) {
            p.pageable = getPageRequest(q, 0, end);
            p.pageOffset = q.getOffset();
        } else {
            // Calculate the page that fits the full requested index range
            int size = end - start;
            while (start / size != (end - 1) / size) {
                ++size;
            }
            p.pageable = getPageRequest(q, start / size, size);
            // Set the offset on page to filter out unneeded results
            p.pageOffset = start % size;
        }

        return p;
    }

    private PageRequest getPageRequest(Query q, int pageIndex, int pageLength) {
        if (!q.getSortOrders().isEmpty()) {
            return new PageRequest(pageIndex, pageLength, getSorting(q));
        } else {
            return new PageRequest(pageIndex, pageLength);
        }
    }

    private Sort getSorting(Query q) {
        return new Sort(q.getSortOrders().stream()
                .map(so -> new Order(
                        so.getDirection() == SortDirection.ASCENDING
                                ? Direction.ASC : Direction.DESC,
                        so.getSorted()))
                .collect(Collectors.toList()));
    }
}
