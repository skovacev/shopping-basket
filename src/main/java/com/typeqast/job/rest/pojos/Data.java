/**
 * 
 */
package com.typeqast.job.rest.pojos;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author Sinisa Kovčević 29-01-2018. -- Initial implementation
 */
@JsonPropertyOrder({
        "monthName",
        "dataValue"
})
@ApiObject(name="Data", description="Data object to keep ratio or meter reading data.")
public class Data {

    @JsonProperty("monthName")
    @ApiObjectField(name="monthName", description = "Short month name (Jan, Feb,...)", required= true)
    private String mMonthName;
    
    @JsonProperty("dataValue")
    @ApiObjectField(name="dataValue", description = "Value for ratio or meter", required= true)
    private Double mValue;

    /**
     * Default Constructor
     */
    public Data() {
        super();
    }

    public Data(String monthName, Double value) {
        mMonthName = monthName;
        this.mValue = value;
    }

    /**
     * Get Month name.
     *
     * @return the Month name
     */
    public final String getMonthName() {
        return mMonthName;
    }

    /**
     * Set monthName.
     *
     * @param monthName the Month name to set
     */
    public final void setMonthName(String monthName) {
        this.mMonthName = monthName;
    }

    /**
     * Get value.
     *
     * @return the value
     */
    public final Double getDataValue() {
        return mValue;
    }

    /**
     * Set Ratio.
     *
     * @param mValue the Ratio to set
     */
    public final void setDataValue(Double ratio) {
        this.mValue = ratio;
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Data)) {
            return false;
        }

        Data ratioData = (Data) obj;
        return this.mMonthName.equals(ratioData.getMonthName()) && this.mValue.doubleValue() == ratioData.getDataValue().doubleValue();
    }
    
    @Override
    public int hashCode() {
        int hashno = 7;
        hashno = 13 * hashno + (mMonthName == null ? 0 : mMonthName.hashCode());
        return hashno;
    }
}
