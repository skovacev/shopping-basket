/**
 * 
 */
package com.consumption.rest.services;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.consumption.rest.interfaces.IProfileService;
import com.consumption.rest.pojos.Data;
import com.consumption.rest.pojos.Profile;
import com.consumption.rest.utils.BaseClass;
import com.consumption.rest.utils.CommonUtils;
import com.consumption.rest.utils.DataImportUtils;

/**
 * @author Sinisa Kovčević 29-01-2018. -- Initial implementation
 */
@Service
public class ProfileService extends BaseClass implements IProfileService {

    private Set<Profile> profileData = new LinkedHashSet<Profile>();
    String filePath = "";
    public static final String DEFAULT_PATH = "src/main/resources/ratios.csv";

    /**
     * Default Constructor. It loads predefined CSV file.
     */
    public ProfileService() {
        //loadData(null, false);
    }

    @Override
    public List<Profile> findAll() {
        getLogger().info("Find all profiles and ratio data in database");
        return new ArrayList<Profile>(profileData);
    }

    @Override
    public Profile findById(final String profileId) {
        Optional<Profile> optional = profileData.stream().filter(b -> b.getProfileId().equals(profileId)).findFirst();
        return CommonUtils.getOptionalValue(optional);

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Data> findByProfile(String profileName) {
        Stream<Profile> stream = profileData.stream().filter(a -> a.getProfileId().equals(profileName));
        return (List<Data>) stream;
    }

    @Override
    public Data findByMonth(String profileId, String month) {
        Profile profile = findById(profileId);
        if (profile != null) {
            return CommonUtils.getOptionalValue(
                    profile.getData().stream().filter(c -> c.getMonthName().equalsIgnoreCase(month)).findFirst());
        } else {
            return null;
        }
    }

    @Override
    public boolean saveData(Profile profile) {
        if (isDataExist(profile)) {
            getLogger().warn("Profile id " + profile.getProfileId() + " already exists");
            return false;
        }
        if (!profileData.contains(profile) && validateProfile(profile)) {
            profileData.add(profile);
            backupData();
            return DataImportUtils.saveRatioData(filePath, profileData);
        }
        return false;
    }

    @Override
    public boolean updateData(Profile profile) {
        if (!isDataExist(profile)) {
            getLogger().warn("Profile id " + profile.getProfileId() + " does not exists");
            return false;
        }

        if (profile != null) {
            profileData.removeIf(p -> p.getProfileId().equals(profile.getProfileId()));
            profileData.add(profile);
            backupData();
            return DataImportUtils.saveRatioData(filePath, profileData);
        }
        return false;
    }

    @Override
    public boolean isDataExist(Profile profile) {
        return isDataExist(profile.getProfileId());
    }

    @Override
    public boolean isDataExist(String profileId) {
        return findById(profileId) != null ? true : false;
    }

    @Override
    public boolean deleteDataById(String profileId) {
        if (StringUtils.isNotEmpty(profileId)) {
            if (isDataExist(profileId)) {
                backupData();
                boolean removeIf = profileData.removeIf(p -> p.getProfileId().equals(profileId));
                return removeIf && DataImportUtils.saveRatioData(filePath, profileData);
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
    public boolean deleteAllDatas(boolean isSave) {
        try {
            backupData();
            profileData.clear();
            boolean saveRatioData = true;
            if (isSave) {
                saveRatioData = DataImportUtils.saveRatioData(filePath, profileData);
            }
            return profileData.isEmpty() && saveRatioData;
        } catch (Exception e) {
            getLogger().error("Failed to cleanup meter reading data.", e);
        }
        return false;
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
        profileData = DataImportUtils.readRatioData(filePath);
        List<Profile> collect = profileData.stream().filter(p -> validateProfile(p))
                .collect(Collectors.toList());
        if (initData) {
            deleteAllDatas(false);
        }
        profileData.addAll(collect);
        DataImportUtils.saveRatioData(filePath, profileData);
    }

    /**
     * Backup data.
     */
    private void backupData() {
        String timeStamp = "_" + LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli() + ".";
        DataImportUtils.saveRatioData(filePath.replace(".", timeStamp), profileData);
    }

    /**
     * Validate profile and its ratio data.
     * 
     * @param profile {@link Profile} instance
     * @return true if validation successful
     */
    private boolean validateProfile(Profile profile) {
        List<Boolean> resultList = new ArrayList<>();
        double sum = profile.getData().stream().mapToDouble(p -> p.getDataValue()).sum();
        if (Math.round(sum) != 1) {
            getLogger().error("Data validation for profile ratio data " + profile.getProfileId() + " failed");
            resultList.add(false);
        } else {
            getLogger().info("Data validation for profile ratio data " + profile.getProfileId() + " passed");
        }

        if (profile.getData().size() != 12) {
            getLogger().error("Data validation for profile ratio data size " + profile.getProfileId() + " failed");
            resultList.add(false);
        } else {
            getLogger()
                    .info("Data validation for profile ratio data size " + profile.getProfileId() + " passed");
        }
        return !resultList.contains(false);
    }
}
