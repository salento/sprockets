package net.sf.sprockets.google;

import com.google.common.base.Objects;
import com.google.gson.stream.JsonReader;

import java.io.IOException;


public class TravelDistance {

    /**
     * Indication of the success or failure of the routing
     */
    String mStatus;

    /**
     * id (=position in origin array) of the origin
     */
    int mOriginId;

    /**
     * origin address (determined by the DistanceMatrix API)
     */
    String mOriginAddress;

    /**
     * id (=position in destination array) of the destination
     */
    int mDestinationId;

    /**
     * destination address (determined by the DistanceMatrix API)
     */
    String mDestinationAddress;

    /**
     * duration of the journey in seconds
     */
    long mDuration;

    /**
     * duration of the journey as text string
     */
    String mDurationText;

    /**
     * distance of the journey in meters
     */
    long mDistance;

    /**
     * distance of the journey as text string
     */
    String mDistanceText;


    TravelDistance(int originId, int destinationId, JsonReader in) throws IOException {
        mOriginId = originId;
        mDestinationId = destinationId;

        in.beginObject();
        while (in.hasNext()) {
            DistanceMatrix.Response.Key key = DistanceMatrix.Response.Key.get(in.nextName());

            switch (key) {
                case UNKNOWN:
                    in.skipValue();
                    break;
                case status:
                    mStatus = in.nextString();
                    break;
                case duration:
                    in.beginObject();
                    while (in.hasNext()) {
                        switch (DistanceMatrix.Response.Key.get(in.nextName())) {
                            case value:
                                mDuration = in.nextLong();
                                break;
                            case text:
                                mDurationText = in.nextString();
                                break;
                            default:
                                in.skipValue();
                        }
                    }
                    in.endObject();

                    break;
                case distance:
                    in.beginObject();
                    while (in.hasNext()) {
                        switch (DistanceMatrix.Response.Key.get(in.nextName())) {
                            case value:
                                mDistance = in.nextLong();
                                break;
                            case text:
                                mDistanceText = in.nextString();
                                break;
                            default:
                                in.skipValue();
                        }
                    }
                    in.endObject();
                    break;
                default:
                    in.skipValue();
            }
        }
        in.endObject();
    }


    /**
     * Indication of the success or failure of the routing
     */
    public String getStatus() {
        return mStatus;
    }

    /**
     * id (=position in origin array) of the origin
     */
    public int getOriginId() {
        return mOriginId;
    }

    /**
     * origin address (determined by the DistanceMatrix API)
     */
    public String getOriginAddress() {
        return mOriginAddress;
    }

    /**
     * id (=position in destination array) of the destination
     */
    public int getDestinationId() {
        return mDestinationId;
    }

    /**
     * destination address (determined by the DistanceMatrix API)
     */
    public String getDestinationAddress() {
        return mDestinationAddress;
    }

    /**
     * duration of the journey in seconds
     */
    public long getDuration() {
        return mDuration;
    }

    /**
     * duration of the journey as text string
     */
    public String getDurationText() {
        return mDurationText;
    }

    /**
     * distance of the journey in meters
     */
    public long getDistance() {
        return mDistance;
    }

    /**
     * distance of the journey as text string
     */
    public String getDistanceText() {
        return mDistanceText;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mOriginId, mDestinationId, mDuration, mDistance);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            if (this == obj) {
                return true;
            } else if (obj instanceof TravelDistance) {
                TravelDistance o = (TravelDistance) obj;
                return Objects.equal(mOriginId, o.mOriginId) && Objects.equal(mDestinationId, o.mDestinationId)
                        && mDuration == o.mDuration && mDistance == o.mDistance;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("status", mStatus)
                .add("originId", mOriginId).add("originAddress", mOriginAddress)
                .add("destinationId", mDestinationId).add("destinationAddress", mDestinationAddress)
                .add("duration", mDuration).add("durationText", mDurationText)
                .add("distance", mDistance).add("distanceText", mDistanceText).toString();
    }

}
