/**
 * 
 */
package com.typeqast.job.rest.interfaces;

import java.util.List;

/**
 * @author Sinisa Kovčević 29-01-2018. -- Initial implementation
 */
public interface IGenericService<T> {

    /**
     * Load data from some location.
     * 
     * @param filePath file path
     */
    void loadData(String filePath);

    /**
     * Find data by ID.
     * 
     * @param id id
     * @return object instance containing data or null if missing
     */
    T findById(String id);

    /**
     * Find all data.
     * 
     * @return List<T> list of data or empty list
     */
    List<T> findAll();

    /**
     * Save data to database.
     * 
     * @param content that needs to be saved
     * @return true if saved successfully
     */
    boolean saveData(T content);

    /**
     * Update data to database.
     * 
     * @param content that needs to be updated
     * @return true if update was successfully
     */
    boolean updateData(T content);

    /**
     * Check if data exist searching by ID.
     * 
     * @param id ID of the data
     * @return true if data exists
     */
    boolean isDataExist(String id);

    /**
     * Check if data exist searching by object.
     * 
     * @param id ID of the data
     * @return true if data exists
     */
    boolean isDataExist(T id);

    /**
     * Delete data from database by ID.
     * 
     * @param id of data that needs to be deleted
     * @return true if data was deleted successfully
     */
    boolean deleteDataById(String id);

    /**
     * Delete all data from.
     * 
     * @return true if all data's have been deleted
     */
    boolean deleteAllDatas();

}
