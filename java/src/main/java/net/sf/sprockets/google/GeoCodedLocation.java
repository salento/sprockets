package net.sf.sprockets.google;

import com.google.common.base.Objects;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A geocoded Location transformed from Google GeoCoding in either direction.
 */
public class GeoCodedLocation {
    /**
     * list of all address components
     */
    private Place.Address mAddress;

    /**
     * a string containing the human-readable address of this location
     */
    private String mFormattedAddress;

    /**
     * latitude of the location
     */
    private double mLatitude;

    /**
     * longitude of the location
     */
    private double mLongitude;

    /**
     * This array contains a set of zero or more tags identifying the type of feature returned in the result
     */
    private ArrayList<String> mTypes;


    private GeoCodedLocation() {
    }

    GeoCodedLocation(JsonReader in) throws IOException {
        in.beginObject();
        while (in.hasNext()) {
            GeoCoding.Response.Key key = GeoCoding.Response.Key.get(in.nextName());

            switch (key) {
                case UNKNOWN:
                    in.skipValue();
                    break;
                case address_components:
                    mAddress = new Place.Address(in);
                    break;
                case formatted_address:
                    mFormattedAddress = in.nextString();
                    break;
                case geometry:
                    in.beginObject();
                    while (in.hasNext()) {
                        if (in.nextName().equals("location")) {
                            in.beginObject();
                            while (in.hasNext()) {
                                switch (Places.Response.Key.get(in.nextName())) {
                                    case lat:
                                        mLatitude = in.nextDouble();
                                        break;
                                    case lng:
                                        mLongitude = in.nextDouble();
                                        break;
                                    default:
                                        in.skipValue();
                                }
                            }
                            in.endObject();
                        } else {
                            in.skipValue(); // "viewport"
                        }
                    }
                    in.endObject();
                    break;
                case types:
                    in.beginArray();
                    while (in.hasNext()) {
                        if (mTypes == null) {
                            mTypes = new ArrayList<String>();
                        }
                        mTypes.add(in.nextString());
                    }
                    in.endArray();
                    break;

                default:
                    in.skipValue();
            }
        }
        in.endObject();
    }

    public Place.Address getAddress() {
        return mAddress;
    }

    public String getFormattedAddress() {
        return mFormattedAddress;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public List<String> getTypes() {
        return Collections.unmodifiableList(mTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mAddress, mLatitude, mLongitude, mFormattedAddress, mTypes);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            if (this == obj) {
                return true;
            } else if (obj instanceof GeoCodedLocation) {
                GeoCodedLocation o = (GeoCodedLocation) obj;
                return Objects.equal(mAddress, o.mAddress)
                        && mLatitude == o.mLatitude && mLongitude == o.mLongitude
                        && Objects.equal(mFormattedAddress, o.mFormattedAddress)
                        && Objects.equal(mTypes, o.mTypes);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("address", mAddress).add("latitude", mLatitude)
                .add("longitude", mLongitude).add("formattedAddress", mFormattedAddress)
                .add("types", mTypes != null ? mTypes.size() : null)
                .omitNullValues().toString();
    }
}
