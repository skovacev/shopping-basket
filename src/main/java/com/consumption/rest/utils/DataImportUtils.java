package com.consumption.rest.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.consumption.rest.pojos.Data;
import com.consumption.rest.pojos.MeterReadingData;
import com.consumption.rest.pojos.Profile;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

/**
 * @author Sinisa Kovčević 29-01-2018. -- Initial implementation
 */
public class DataImportUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(DataImportUtils.class);

    /**
     * Comparator to sort object containing month names.
     */
    public static final Comparator<Data> DATA_COMPARE = new Comparator<Data>() {

        public int compare(Data o1, Data o2) {

            Format formatter = new SimpleDateFormat("MMM", Locale.ENGLISH);
            Date s1 = null;
            Date s2 = null;
            try {
                s1 = (Date) formatter.parseObject(o1.getMonthName());
                s2 = (Date) formatter.parseObject(o2.getMonthName());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return s1.compareTo(s2);
        }
    };

    /**
     * Read profile and ratio data from external CSV file.
     * 
     * @param filePath path to file containing data.
     * @return Set<Profile> data
     */
    public static Set<Profile> readRatioData(final String filePath) {
        LOGGER.info("Read CSV file from path: " + filePath);

        Set<Profile> profileData = new LinkedHashSet<Profile>();
        Map<String, Set<Data>> rawDataMap = new LinkedHashMap<>();
        try {
            File file = new File(filePath);
            if (!file.exists() && !file.canRead()) {
                LOGGER.error("Failed to read input data. File on path does not exist: " + filePath);
                return profileData;
            }
            CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(filePath)));
            csvReader.readAll().stream()
                    .filter(p -> !p[0].toUpperCase().startsWith("MONTH")
                            && !p[1].toUpperCase().startsWith("PROFILE")
                            && !p[2].toUpperCase().startsWith("RATIO"))
                    .filter(p -> StringUtils.isNotEmpty(p[0]))
                    .forEach(p -> rawDataMap.computeIfAbsent(p[1], k -> new LinkedHashSet<Data>())
                            .add(new Data(p[0], Double.valueOf(p[2]))));
            csvReader.close();
        } catch (FileNotFoundException ex) {
            LOGGER.error("FileNotFoundException occurred", ex.getMessage());
        } catch (IOException ex) {
            LOGGER.error("IOException occurred", ex.getMessage());
        }

        rawDataMap.entrySet().stream().forEach(p -> profileData.add(new Profile(p.getKey(), p.getValue())));
        return profileData;
    }

    /**
     * Save profile data.
     * 
     * @param filePath file path
     * @param profileSet {@link Profile}
     * @return true if saving was successfully
     */
    public static boolean saveRatioData(final String filePath, Set<Profile> profileSet) {
        return writeRatioData(filePath, profileSet, false);
    }

    /**
     * Update profile data.
     * 
     * @param filePath file path
     * @param profileSet {@link Profile}
     * @return true if update was successfully
     */
    public static boolean updateRatioData(final String filePath, Set<Profile> profileSet) {
        return writeRatioData(filePath, profileSet, true);
    }

    /**
     * Read meter data from external CSV file.
     * 
     * @param filePath path to file containing data.
     * @return Set<Profile> data
     */
    public static Set<MeterReadingData> readMeterData(final String filePath) {
        LOGGER.info("Read CSV file from path: " + filePath);

        Set<MeterReadingData> meterReadingData = new LinkedHashSet<MeterReadingData>();
        Map<String, Map<String, Set<Data>>> rawDataMap = new LinkedHashMap<>();
        try {
            File file = new File(filePath);
            if (!file.exists() && !file.canRead()) {
                LOGGER.error("Failed to read input data. File on path does not exist: " + filePath);
                return meterReadingData;
            }
            CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(filePath)));
            csvReader.readAll().stream()
                    .filter(p -> !p[0].toUpperCase().startsWith("CONNECTIONID")
                            && !p[1].toUpperCase().startsWith("PROFILE")
                            && !p[2].toUpperCase().startsWith("MONTH"))
                    .filter(p -> StringUtils.isNotEmpty(p[0]))
                    .forEach(p -> rawDataMap.computeIfAbsent(p[0], k -> new LinkedHashMap<String, Set<Data>>())
                            .computeIfAbsent(p[1], j -> new LinkedHashSet<Data>())
                            .add(new Data(p[2], Double.valueOf(p[3]))));
            csvReader.close();
        } catch (FileNotFoundException ex) {
            LOGGER.error("FileNotFoundException occurred", ex.getMessage());
        } catch (IOException ex) {
            LOGGER.error("IOException occurred", ex.getMessage());
        }
        for (Entry<String, Map<String, Set<Data>>> rawDataEntry : rawDataMap.entrySet()) {
            Set<Profile> profileSet = new LinkedHashSet<>();
            for (Entry<String, Set<Data>> profileDataEntry : rawDataEntry.getValue().entrySet()) {
                profileSet.add(new Profile(profileDataEntry.getKey(), profileDataEntry.getValue()));
            }
            meterReadingData.add(new MeterReadingData(rawDataEntry.getKey(), profileSet));
        }
        return meterReadingData;
    }

    /**
     * Save meter reading data.
     * 
     * @param filePath file path
     * @param meterReadingData {@link MeterReadingData}
     * @return true if saving was successfully
     */
    public static boolean saveMeterData(final String filePath, Set<MeterReadingData> meterReadingData) {
        return writeMeterData(filePath, meterReadingData, false);
    }

    /**
     * Update meter reading data.
     * 
     * @param filePath file path
     * @param meterReadingData {@link MeterReadingData}
     * @return true if update was successfully
     */
    public static boolean updateMeterData(final String filePath, Set<MeterReadingData> meterReadingData) {
        return writeMeterData(filePath, meterReadingData, true);
    }

    /**
     * Save or update meter data.
     * 
     * @param filePath file path
     * @param meterReadingData content to save
     * @param isAppend append or override
     * @return true if operation was successful
     */
    private static boolean writeMeterData(final String filePath, Set<MeterReadingData> meterReadingData,
            boolean isAppend) {
        LOGGER.info("Write data to CSV file on path: " + filePath);

        final CSVWriter csvWriter;
        try {
            File file = new File(filePath);
            if (!file.exists() && !file.canRead()) {
                LOGGER.warn("Failed to read input data. File on path does not exist: " + filePath);
            }
            Writer writer = Files.newBufferedWriter(Paths.get(filePath));
            csvWriter = new CSVWriter(writer);
            List<String[]> data = meterToStringArray(meterReadingData, isAppend);

            if (isAppend) {
                data.stream().forEach(p -> csvWriter.writeNext(p));
            } else {
                Files.copy(Paths.get(filePath), Paths.get(filePath));
                csvWriter.writeAll(data, false);
            }

            csvWriter.close();
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to write data to CSV file on path " + filePath);
        }
        return false;
    }

    /**
     * Meter reading object data to String Array conversion.
     * 
     * @param metReadingData {@link MeterReadingData} instance
     * @param isAppend
     * @return List of String arrays
     */
    private static List<String[]> meterToStringArray(Set<MeterReadingData> metReadingData, boolean isAppend) {
        List<String[]> records = new ArrayList<String[]>();
        if (!isAppend) {
            records.add(new String[] { "ConnectionID", "Profile", "Month", "Meter Reading" });
        }

        for (MeterReadingData meterReading : metReadingData) {
            for (Profile profile : meterReading.getProfileList()) {
                for (Data data : profile.getData()) {
                    records.add(new String[] { meterReading.getConnectionId(), profile.getProfileId(),
                            data.getMonthName(), data.getDataValue().doubleValue() + "" });
                }
            }
        }
        return records;
    }

    /**
     * Profile object data to String Array conversion.
     * 
     * @param profileList {@link Profile} instance
     * @param isAppend
     * @return List of String arrays
     */
    private static List<String[]> profileToStringArray(Set<Profile> profileList, boolean isAppend) {
        List<String[]> records = new ArrayList<String[]>();
        if (!isAppend) {
            records.add(new String[] { "Month", "Profile", "Ratio" });
        }

        for (Profile profile : profileList) {
            for (Data data : profile.getData()) {
                records.add(new String[] { data.getMonthName(), profile.getProfileId(),
                        data.getDataValue().doubleValue() + "" });
            }
        }
        return records;
    }

    /**
     * Save or update meter data.
     * 
     * @param filePath file path
     * @param profileData content to save
     * @param isAppend append or override
     * @return true if operation was successful
     */
    private static boolean writeRatioData(final String filePath, Set<Profile> profileData,
            boolean isAppend) {
        LOGGER.info("Write data to CSV file on path: " + filePath);

        final CSVWriter csvWriter;
        try {
            File file = new File(filePath);
            if (!file.exists() && !file.canRead()) {
                LOGGER.warn("Failed to read input data. File on path does not exist: " + filePath);
            }
            Writer writer = Files.newBufferedWriter(Paths.get(filePath));
            csvWriter = new CSVWriter(writer);
            List<String[]> data = profileToStringArray(profileData, isAppend);

            if (isAppend) {
                data.stream().forEach(p -> csvWriter.writeNext(p));
            } else {
                //Files.copy(Paths.get(filePath), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                csvWriter.writeAll(data, false);
            }

            csvWriter.close();
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to write data to CSV file on path " + filePath, e);
        }
        return false;
    }
}
