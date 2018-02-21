/**
 * 
 */
package com.consumption.rest.services;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.consumption.rest.interfaces.IMeterService;
import com.consumption.rest.pojos.Data;
import com.consumption.rest.pojos.MeterReadingData;
import com.consumption.rest.pojos.Profile;
import com.consumption.rest.utils.BaseClass;
import com.consumption.rest.utils.CommonUtils;
import com.consumption.rest.utils.DataImportUtils;

/**
 * @author Sinisa Kovčević 29-01-2018. -- Initial implementation
 */

@Service
public class MeterService extends BaseClass implements IMeterService {

    @Inject
    private ProfileService profileService;

    private Set<MeterReadingData> meterReadingDataSet = new LinkedHashSet<MeterReadingData>();
    String filePath = "";
    public static final String DEFAULT_PATH = "src/main/resources/meter.csv";

    @Inject
    public MeterService(ProfileService profileService) {
//        this.profileService = profileService;
//        loadData(null, false);
    }
    
    @Override
    public void loadData(String filePathToUse) {
        loadData(filePathToUse, true);
    }
    
    public void loadData(String filePathToUse, boolean initData) {
        if (StringUtils.isNotEmpty(filePathToUse)) {
            this.filePath = filePathToUse;
        } else {
            this.filePath = DEFAULT_PATH;
        }
        meterReadingDataSet = DataImportUtils.readMeterData(filePath);
        List<MeterReadingData> collect = meterReadingDataSet.stream().filter(p -> validateMeterData(p))
                .collect(Collectors.toList());
        if (initData) {
            deleteAllDatas(false);
        }
        meterReadingDataSet.addAll(collect);
        DataImportUtils.saveMeterData(filePath, meterReadingDataSet);
    }

    @Override
    public MeterReadingData findById(final String connectionId) {
        Optional<MeterReadingData> optional = meterReadingDataSet.stream()
                .filter(b -> b.getConnectionId().equals(connectionId)).findFirst();
        return CommonUtils.getOptionalValue(optional);

    }

    @Override
    public List<MeterReadingData> findAll() {
        getLogger().info("Find all meter readings with all profiles in database");
        return new ArrayList<MeterReadingData>(meterReadingDataSet);
    }

    @Override
    public Profile findByProfile(String connectionId, String profileId) {
        MeterReadingData meterReadingData = findById(connectionId);
        if (meterReadingData != null) {
            return CommonUtils.getOptionalValue(meterReadingData.getProfileList().stream()
                    .filter(c -> c.getProfileId().equalsIgnoreCase(profileId))
                    .findFirst());
        } else {
            return null;
        }
    }

    @Override
    public Data findByProfileMonth(String connectionId, String profileId, String month) {
        Profile profile = findByProfile(connectionId, profileId);
        if (profile != null) {
            return CommonUtils
                    .getOptionalValue(profile.getData().stream().filter(p -> p.getMonthName().equals(month)).findAny());
        } else {
            getLogger().debug("Connection id " + connectionId + " with profile " + profileId + " not found");
            return null;
        }
    }

    @Override
    public MeterReadingData findConsumptionByMonth(String connectionId, String month) {
        if (!CommonUtils.SHORT_MONTHS.contains(month)) {
            throw new IllegalArgumentException(
                    "Provided month name is not valid: " + month + ". Please use short month naming.");
        }

        MeterReadingData meterReadingData = findById(connectionId);
        return calculateConsumptionDataForMonth(meterReadingData, month);
    }

    @Override
    public MeterReadingData findByMonth(String connectionId, String month) {
        if (!CommonUtils.SHORT_MONTHS.contains(month)) {
            throw new IllegalArgumentException(
                    "Provided month name is not valid: " + month + ". Please use short month naming.");
        }

        MeterReadingData meterReading = findById(connectionId);
        if (meterReading != null) {
            MeterReadingData meterReadingCalc = new MeterReadingData();
            meterReadingCalc.setConnectionId(meterReading.getConnectionId());

            meterReading.getProfileList().stream().forEach(p -> {
                Data data = findByProfileMonth(connectionId, p.getProfileId(), month);
                meterReadingCalc.addProfileList(new Profile(p.getProfileId(), new HashSet<>(Arrays.asList(data))));
            });
            return meterReadingCalc;
        }
        return null;
    }

    @Override
    public boolean saveData(MeterReadingData meterReading) {
        if (isDataExist(meterReading)) {
            getLogger().warn("Connection id " + meterReading.getConnectionId() + " already exists");
            return false;
        }
        if (!meterReadingDataSet.contains(meterReading) && validateMeterData(meterReading)) {
            meterReadingDataSet.add(meterReading);
            backupData();
            return DataImportUtils.saveMeterData(filePath, meterReadingDataSet);
        }
        return false;
    }

    @Override
    public boolean updateData(MeterReadingData meterReading) {
        if (!isDataExist(meterReading)) {
            getLogger().warn("Connection id " + meterReading.getConnectionId() + " does not exists");
            return false;
        }
        if (meterReadingDataSet != null && validateMeterData(meterReading)) {
            meterReadingDataSet.removeIf(p -> p.getConnectionId().equals(meterReading.getConnectionId()));
            meterReadingDataSet.add(meterReading);
            backupData();
            return DataImportUtils.saveMeterData(filePath, meterReadingDataSet);
        }
        return false;
    }

    @Override
    public boolean isDataExist(MeterReadingData meterReadingData) {
        return isDataExist(meterReadingData.getConnectionId());
    }

    @Override
    public boolean isDataExist(String connectionId) {
        return findById(connectionId) != null ? true : false;
    }

    @Override
    public boolean deleteDataById(String connectionId) {
        if (StringUtils.isNotEmpty(connectionId)) {
            if (isDataExist(connectionId)) {
                backupData();
                boolean removeIf = meterReadingDataSet.removeIf(p -> p.getConnectionId().equals(connectionId));
                return removeIf && DataImportUtils.saveMeterData(filePath, meterReadingDataSet);
            }
        }
        return false;
    }

    @Override
    public boolean deleteAllDatas() {
        return deleteAllDatas(true);
    }

    /**
     * Delete all data.
     * 
     * @param isSave flag to save to file or not
     * @return true if cleanup successful
     */
    private boolean deleteAllDatas(boolean isSave) {
        try {
            backupData();
            meterReadingDataSet.clear();
            boolean saveMeterData = true;
            if (isSave) {
                saveMeterData = DataImportUtils.saveMeterData(filePath, meterReadingDataSet);
            }
            return meterReadingDataSet.isEmpty() && saveMeterData;
        } catch (Exception e) {
            getLogger().error("Failed to cleanup meter reading data.", e);
        }
        return false;
    }

    /**
     * Backup data.
     */
    private void backupData() {
        String timeStamp = "_" + LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli() + ".";
        DataImportUtils.saveMeterData(filePath.replace(".", timeStamp), meterReadingDataSet);
    }

    /**
     * Validate meter data.
     * 
     * @param meterReading {@link MeterReadingData}
     * @return true if all validations successful
     */
    private boolean validateMeterData(MeterReadingData meterReading) {
        boolean isIncreasing = validateMeterMonthDataIncreasing(meterReading);
        boolean isProfileExist = validateMeterProfileData(meterReading);
        boolean isConsumptionValid = validateConsumptionDataYearly(meterReading);
        return isConsumptionValid && isProfileExist && isIncreasing;
    }

    /**
     * Validate meter month data increasing.
     * 
     * @param meterReading {@link MeterReadingData}
     * @return true if data valid
     */
    private boolean validateMeterMonthDataIncreasing(MeterReadingData meterReading) {
        getLogger().debug("A meter reading for a month should not be lower than the previous one");
        for (Profile profile : meterReading.getProfileList()) {
            List<Data> collect = profile.getData().stream().collect(Collectors.toList());
            Collections.sort(collect, DataImportUtils.DATA_COMPARE);
            calculateEstimatedMonth(profile.getProfileId(), collect);
            for (int i = 0, j = 1; j < collect.size(); i++, j++) {
                if (collect.get(j).getDataValue().intValue() < collect.get(i).getDataValue().intValue()) {
                    getLogger().error("Error meter reading data. Monthly meter readings are not increasing");
                    return false;
                }
            }
        }
        getLogger().debug("A meter reading data is increasing every month");
        return true;
    }

    /**
     * Validate if profile data with ratio exists for all meter reading data.
     * 
     * @param meterReading {@link MeterReadingData}
     * @return true if data valid
     */
    private boolean validateMeterProfileData(MeterReadingData meterReading) {
        getLogger().debug(
                "Data about ratios for profiles contained in the provided meter reading object should exist in database");
        long count = meterReading.getProfileList().stream().filter(p -> {
            Profile profile = profileService.findById(p.getProfileId());
            if (profile == null) {
                return false;
            }
            getLogger().debug("Profile " + profile.getProfileId() + " exists in database");
            if (profile.getData().size() != CommonUtils.NUMBER_OF_MONTHS) {
                getLogger().debug(
                        "Profile " + profile.getProfileId() + " does not have all entries for a year in database");
                return false;
            } else {
                getLogger().debug("Profile " + profile.getProfileId() + " contains all entries for a year in database");
                return true;
            }
        }).count();
        return count != meterReading.getProfileList().size() ? false : true;
    }

    /**
     * Validate consumption data yearly.
     * 
     * @param meterReading {@link MeterReadingData}
     * @return true if consumption is following estimation based on ratio
     */
    private boolean validateConsumptionDataYearly(MeterReadingData meterReading) {
        List<Boolean> resultList = new ArrayList<>();
        if (meterReading != null) {
            MeterReadingData meterReadingCalc = new MeterReadingData();
            meterReadingCalc.setConnectionId(meterReading.getConnectionId());

            meterReading.getProfileList().stream().forEach(profile -> {
                boolean isValid = validateConsumptionDataYearly(profile);
                resultList.add(isValid);
                if (isValid) {
                    getLogger().debug(
                            "Consumption data for profile " + profile.getProfileId() + " is valid");
                } else {
                    getLogger().debug(
                            "Consumption data for profile " + profile.getProfileId() + " is not valid");
                }
            });
        }
        return !resultList.contains(false);
    }

    /**
     * Validate consumption for one month.
     * 
     * @param profile {@link Profile}
     * @return true if consumption for whole year is valid
     */
    private boolean validateConsumptionDataYearly(Profile profile) {
        List<Boolean> resultList = new ArrayList<>();
        List<Data> collect = profile.getData().stream().collect(Collectors.toList());
        Collections.sort(collect, DataImportUtils.DATA_COMPARE);
        CommonUtils.SHORT_MONTHS.stream().forEach(month -> {
            boolean isValid = validateConsumptionDataMonthly(profile.getProfileId(), collect, month);
            resultList.add(isValid);
            if (isValid) {
                getLogger().debug(
                        "Consumption data for profile " + profile.getProfileId() + " month " + month + " is valid");
            } else {
                getLogger().debug(
                        "Consumption data for profile " + profile.getProfileId() + " month " + month + " is not valid");
            }
        });

        return !resultList.contains(false);
    }

    /**
     * Validate consumption for one month.
     * 
     * @param profileId {@link Profile} ID
     * @param meterReading {@link MeterReadingData}
     * @param month Short month name
     * @return true if consumption for whole year is valid
     */
    private boolean validateConsumptionDataMonthly(String profileId, List<Data> meterReading, String month) {
        return calculateConsumptionData(profileId, meterReading, month) != null;
    }

    /**
     * Calculate consumption data for single month.
     * 
     * @param meterReading {@link MeterReadingData}
     * @param month month name
     * @return {@link MeterReadingData} with consumption data
     */
    private MeterReadingData calculateConsumptionDataForMonth(MeterReadingData meterReading, String month) {
        if (meterReading != null) {
            MeterReadingData meterReadingCalc = new MeterReadingData();
            meterReadingCalc.setConnectionId(meterReading.getConnectionId());

            meterReading.getProfileList().stream().forEach(p -> {
                List<Data> sortedList = p.getData().stream().sorted(DataImportUtils.DATA_COMPARE)
                        .collect(Collectors.toList());
                Profile consumptionProfile = new Profile(p.getProfileId());

                Data consumptionData = calculateConsumptionData(p.getProfileId(), sortedList, month);
                Set<Data> dataSet = new LinkedHashSet<>();
                dataSet.add(consumptionData);
                consumptionProfile.setData(dataSet);

                meterReadingCalc.addProfileList(consumptionProfile);
            });
            boolean isIncreasing = validateMeterMonthDataIncreasing(meterReading);
            boolean isProfileDataValid = validateMeterProfileData(meterReading);
            if (isProfileDataValid && isIncreasing) {
                return meterReadingCalc;
            }
        }
        return null;
    }

    /**
     * Calculate consumption for one month.
     * 
     * @param profileId {@link Profile} ID
     * @param meterReading {@link MeterReadingData} meter reading data list
     * @param month short month name (Jan, Feb...)
     * @return calculated consumption in {@link Data}
     */
    private Data calculateConsumptionData(String profileId, List<Data> meterReading, String month) {
        int indexOf = CommonUtils.SHORT_MONTHS.indexOf(month);
        calculateEstimatedMonth(profileId, meterReading);

        Data currentMonthData = CommonUtils
                .getOptionalValue(meterReading.stream().filter(r -> r.getMonthName().equals(month)).findFirst());

        Data nextMonthData = meterReading.get(indexOf + 1);

        double currValue = currentMonthData.getDataValue().doubleValue();
        double nextValue = nextMonthData.getDataValue().doubleValue();

        final float tolerance = 0.25f;
        Double consumptionDiff = nextValue - currValue;
        if (Double.compare(consumptionDiff, 0.0) < 1) {
            getLogger().error("Consumption data for current month is less than the previous");
            throw new IllegalStateException("Consumption data for current month is less than the previous");
        }

        double yearlySum = 0.0f;
        for (int i = 0, j = 1; j < meterReading.size(); i++, j++) {
            yearlySum += (meterReading.get(j).getDataValue().doubleValue()
                    - meterReading.get(i).getDataValue().doubleValue());
        }

        Data ratioData = profileService.findByMonth(profileId, currentMonthData.getMonthName());
        double estConsumPerMonth = yearlySum * ratioData.getDataValue();

        double minValue = estConsumPerMonth - (estConsumPerMonth * tolerance);
        double maxValue = estConsumPerMonth + (estConsumPerMonth * tolerance);

        if (consumptionDiff >= minValue && consumptionDiff <= maxValue) {
            return new Data(currentMonthData.getMonthName(), consumptionDiff);
        } else {
            throw new IllegalStateException(
                    "Consumption for a month is not consistent with the ratio and tolerance of a 25%.");
        }
    }

    /**
     * Calculate month consumption for Jan after a New Year to be able to calculate monthly
     * consumption.
     * Meter readings are per month but to calculate consumption we need additional month data.
     * <br>
     * Consumption: (the difference between the meter reading of a month minus the previous one)
     * 
     * @param profileId {@link Profile} ID
     * @param data Meter reading data for year
     */
    private void calculateEstimatedMonth(String profileId, List<Data> data) {
        final String DEC = "Dec";

        double yearlySum = 0.0f;
        for (int i = 0, j = 1; j < data.size(); i++, j++) {
            yearlySum += (data.get(j).getDataValue().doubleValue() - data.get(i).getDataValue().doubleValue());
        }

        Data decMeterData = CommonUtils
                .getOptionalValue(data.stream().filter(r -> r.getMonthName().equals(DEC)).findFirst());
        Data decRatioData = profileService.findByMonth(profileId, DEC);
        Data estimatedMonthData = new Data("Dec",
                decMeterData.getDataValue() + (yearlySum * decRatioData.getDataValue()));
        data.add(estimatedMonthData);
    }
}
