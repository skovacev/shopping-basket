/**
 * 
 */
package com.typeqast.job.rest.utils;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typeqast.job.rest.pojos.Profile;

/**
 * @author Sinisa Kovčević 29-01-2018. -- Initial implementation 
 *
 */
public class CommonUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);
    public static final List<String> SHORT_MONTHS; 
    public static final int NUMBER_OF_MONTHS = 12;
    
    static {
        SHORT_MONTHS = Arrays.asList(new DateFormatSymbols(Locale.ENGLISH).getShortMonths())
                .stream().filter(p -> !p.isEmpty()).collect(Collectors.toList());
    }
    
    /**
     * Get {@link Profile} value from stream.
     * 
     * @param optional Optional<Profile> value
     * @return {@link Profile} if found null otherwise
     */
    public static <T> T getOptionalValue(Optional<T> optional) {
        if (optional != null && optional.isPresent()) {
            return optional.get();
        } else {
            LOGGER.error("Failed to find data in stream.");
            return null;
        }
    }
}
