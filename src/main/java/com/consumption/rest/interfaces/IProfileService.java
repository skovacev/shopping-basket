/**
 * 
 */
package com.consumption.rest.interfaces;

import java.util.List;

import com.consumption.rest.pojos.Data;
import com.consumption.rest.pojos.Profile;

/**
 * @author Sinisa Kovčević 29-01-2018. -- Initial implementation
 */
public interface IProfileService extends IGenericService<Profile> {

    /**
     * Find ratio List<Data> by applying filter on profile name.
     * 
     * @param profileId {@link Profile} ID
     * @return List<Data> ratio data for all months
     */
    List<Data> findByProfile(String profileName);

    /**
     * Find ratio {@link Data} by applying filter on profile and month.
     * 
     * @param profileId {@link Profile} ID
     * @return {@link Data} ratio data for one month
     */
    Data findByMonth(String profile, String month);

    //    /**
    //     * findById
    //     * @param profile
    //     * @param month
    //     * @return
    //     */
    //    Profile findById(String profileId);

    //    /**
    //     * saveUser
    //     * @param profile
    //     */
    //    void saveProfile(Profile profile);

    //    /**
    //     * isRatioDataExist
    //     * @param profile
    //     * @return
    //     */
    //    boolean isProfileExist(Profile profile);

    //    /**
    //     * findAll
    //     * @return
    //     */
    //    List<Profile> findAll();

    //    /**
    //     * Update Profile
    //     * @param profile
    //     */
    //    void updateProfile(Profile profile);

}
