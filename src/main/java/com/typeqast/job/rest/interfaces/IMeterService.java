/**
 * 
 */
package com.typeqast.job.rest.interfaces;

import com.typeqast.job.rest.pojos.Data;
import com.typeqast.job.rest.pojos.MeterReadingData;
import com.typeqast.job.rest.pojos.Profile;

/**
 * @author Sinisa Kovčević 29-01-2018. -- Initial implementation
 */
public interface IMeterService extends IGenericService<MeterReadingData> {

    /**
     * Find meter {@link Profile} by applying filter on connection ID and month.
     * 
     * @param connectionId Connection ID
     * @param profileId Short month naming (ex. Jan, Feb...)
     * @return {@link Profile} with meter reading data
     */
    Profile findByProfile(String connectionId, String profileId);

    /**
     * Find {@link MeterReadingData} by applying filter on connection ID and month.
     * 
     * @param connectionId Connection ID
     * @param month Short month naming (ex. Jan, Feb...)
     * @return {@link MeterReadingData} meter reading data for month
     */
    MeterReadingData findByMonth(String connectionId, String month);

    /**
     * Find meter {@link Data} by applying filter on connection ID, profile and month.
     * 
     * @param connectionId Connection ID
     * @param profileId {@link Profile} ID
     * @param month Short month naming (ex. Jan, Feb...)
     * @return {@link Data} consumption data for month
     */
    Data findByProfileMonth(String connectionId, String profileId, String month);

    /**
     * Find consumption by month.
     * 
     * @param connectionId connection ID
     * @param month Short month naming (ex. Jan, Feb...)
     * @return
     */
    MeterReadingData findConsumptionByMonth(String connectionId, String month);

    //    /**
    //     * findById
    //     * @param MeterReadingData
    //     * @param month
    //     * @return
    //     */
    //    public MeterReadingData findById(String MeterReadingDataId);
    //
    //    /**
    //     * saveUser
    //     * @param MeterReadingData
    //     * @return 
    //     */
    //    public boolean saveMeterReadingData(MeterReadingData MeterReadingData);

    //    /**
    //     * isRatioDataExist
    //     * @param MeterReadingData
    //     * @return
    //     */
    //    public boolean isMeterReadingDataExist(MeterReadingData MeterReadingData);

    //
    //    /**
    //     * Update MeterReadingData
    //     * @param MeterReadingData
    //     */
    //    public void updateMeterReadingData(MeterReadingData MeterReadingData);

    /**
     * // * deleteUserById
     * // * @param MeterReadingDataId
     * //
     */
    //    public void deleteMeterReadingDataById(String MeterReadingDataId);
    //
    //    /**
    //     * isMeterReadingDataExist
    //     * @param MeterReadingDataId
    //     * @return
    //     */
    //    boolean isMeterReadingDataExist(String MeterReadingDataId);
    //
    //    /**
    //     * deleteAllMeterReadingDatas
    //     */
    //    public void deleteAllMeterReadingDatas();

}
