package com.vaadin.framework8.samples.crud;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
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
import org.springframework.stereotype.Service;

import com.vaadin.data.provider.AbstractDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.framework8.samples.backend.data.Availability;
import com.vaadin.framework8.samples.backend.data.Category;
import com.vaadin.framework8.samples.backend.data.Product;
import com.vaadin.framework8.samples.backend.repository.CategoryRepository;
import com.vaadin.framework8.samples.backend.repository.ProductRepository;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.UI;

/**
 * DataProvider implementation for managing {@code ProductRepository} and
 * filtering.
 */
@Service
public class ProductDataProviderImpl
        extends AbstractDataProvider<Product, Supplier<String>>
        implements ProductDataProvider {

    private static class PageQuery {
        Pageable pageable;
        int pageOffset;
    }

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    @Transactional
    public int size(Query<Product, Supplier<String>> t) {
        return (int) getItems(getPaging(t).pageable, getFilter(t)).count();
    }

    private String getFilter(Query<Product, Supplier<String>> t) {
        return t.getFilter().map(Supplier::get).orElse(null);
    }

    @Override
    @Transactional
    public Stream<Product> fetch(Query<Product, Supplier<String>> t) {
        PageQuery pageQuery = getPaging(t);
        return getItems(pageQuery.pageable, getFilter(t))
                .skip(pageQuery.pageOffset).limit(t.getLimit());
    }

    @Transactional
    @Override
    public void save(Product product) {
        productRepo.save(product);
        refreshAll();
    }

    @Transactional
    @Override
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

    private Stream<Product> getItems(Pageable page, String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            return StreamSupport.stream(productRepo.findAll(page).spliterator(),
                    false);
        }
        String filter = filterText.toLowerCase(UI.getCurrent().getLocale());
        return productRepo
                .findDistinctByProductNameContainingIgnoreCaseOrAvailabilityInOrCategoryIn(
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
    private PageQuery getPaging(Query<Product, Supplier<String>> q) {
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

    private PageRequest getPageRequest(Query<Product, Supplier<String>> q,
            int pageIndex, int pageLength) {
        if (!q.getSortOrders().isEmpty()) {
            return new PageRequest(pageIndex, pageLength, getSorting(q));
        } else {
            return new PageRequest(pageIndex, pageLength);
        }
    }

    private Sort getSorting(Query<Product, Supplier<String>> q) {
        return new Sort(q.getSortOrders().stream()
                .map(so -> new Order(
                        so.getDirection() == SortDirection.ASCENDING
                                ? Direction.ASC : Direction.DESC,
                        so.getSorted()))
                .collect(Collectors.toList()));
    }
}
