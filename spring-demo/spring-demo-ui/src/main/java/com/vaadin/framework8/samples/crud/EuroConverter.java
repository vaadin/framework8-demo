package com.vaadin.framework8.samples.crud;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.data.Result;
import com.vaadin.data.util.converter.StringToBigDecimalConverter;

/**
 * A converter that adds/removes the euro sign and formats currencies with two
 * decimal places.
 */
public class EuroConverter extends StringToBigDecimalConverter {

    public EuroConverter() {
        super("Cannot convert value to a number");
    }

    @Override
    public Result<BigDecimal> convertToModel(String value, Locale locale) {
        value = value.replaceAll("[€\\s]", "").trim();
        if ("".equals(value)) {
            value = "0";
        }
        return super.convertToModel(value, locale);
    }

    @Override
    public String convertToPresentation(BigDecimal value, Locale locale) {
        return super.convertToPresentation(value, locale) + " €";
    }

    @Override
    protected NumberFormat getFormat(Locale locale) {
        // Always display currency with two decimals
        NumberFormat format = super.getFormat(locale);
        if (format instanceof DecimalFormat) {
            ((DecimalFormat) format).setMaximumFractionDigits(2);
            ((DecimalFormat) format).setMinimumFractionDigits(2);
        }
        return format;
    }

}
