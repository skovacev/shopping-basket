/**
 * 
 */
package com.typeqast.job.rest.pojos;

import java.util.HashSet;
import java.util.Set;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author Sinisa Kovčević 29-01-2018. -- Initial implementation
 */
@JsonPropertyOrder({
        "profileId",
        "data"
})
@ApiObject(name = "Profile", description = "Profile to keep profile ID and data. It can be used for ratio or meter data.")
public class Profile {
    @JsonProperty("profileId")
    @ApiObjectField(name = "profileId", description = "Profile ID", required = true, order = 1)
    private String mProfileId;

    @JsonProperty("data")
    @ApiObjectField(name = "data", description = "Data list Set<Data>", required = true, order = 2)
    private Set<Data> mData = new HashSet<Data>();;

    /**
     * Default Constructor
     */
    public Profile() {
        super();
    }

    public Profile(String profileId) {
        this.mProfileId = profileId;
    }

    /**
     * Default Constructor
     *
     * @param profileId
     * @param data
     */
    public Profile(String profileId, Set<Data> data) {
        this.mProfileId = profileId;
        setData(data);
    }

    /**
     * Get {@link IData}.
     *
     * @return the {@link IData}
     */
    public Set<Data> getData() {
        return mData;
    }

    /**
     * Set {@link Data}.
     *
     * @param dataList the {@link Data} to set
     * @return
     */
    public Profile setData(Set<Data> dataList) {
        if (dataList != null) {
            mData.addAll(dataList);
        }
        return this;
    }
    //
    //    /**
    //     * Set {@link Data}.
    //     *
    //     * @param dataValue the {@link Data} to set
    //     * @return
    //     */
    //    public Profile addData(Data dataValue) {
    //        if (dataValue != null) {
    //            this.mData.add(dataValue);
    //        }
    //        return this;
    //    }

    //    /**
    //     * Default Constructor
    //     *
    //     */
    //    public Profile(String profileId) {
    //        mProfileId = profileId.trim();
    //    }

    /**
     * Get profileId.
     *
     * @return the profileId
     */
    public String getProfileId() {
        return mProfileId;
    }

    /**
     * Set profileId.
     *
     * @param profileId the profileId to set
     */
    public void setProfileId(String profileId) {
        this.mProfileId = profileId;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Profile)) {
            return false;
        }

        Profile profile = (Profile) obj;
        return this.mProfileId.equals(profile.getProfileId());
    }

    @Override
    public int hashCode() {
        int hashno = 7;
        hashno = 13 * hashno + (mProfileId == null ? 0 : mProfileId.hashCode());
        return hashno;
    }
}
