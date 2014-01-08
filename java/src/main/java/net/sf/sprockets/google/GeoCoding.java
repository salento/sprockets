/*
 * Copyright 2013 pushbit <pushbit@gmail.com>
 *
 * This file is part of Sprockets.
 *
 * Sprockets is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Sprockets is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Sprockets.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.sprockets.google;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ObjectArrays;
import com.google.common.io.Closeables;
import com.google.gson.stream.JsonReader;
import net.sf.sprockets.Sprockets;
import net.sf.sprockets.net.HttpClient;
import net.sf.sprockets.util.logging.Loggers;
import org.apache.commons.configuration.Configuration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;

/**
 * Methods for calling <a href="https://developers.google.com/maps/documentation/geocoding/" target="_blank">Google GeoCoding API</a>
 * services.
 */
public class GeoCoding {
    private static final Logger sLog = Loggers.get(GeoCoding.class);

    private GeoCoding() {
    }

    /**
     * Geocodes an address into coordinates and vice versa
     * <p>
     * Required one of the following params:
     * </p>
     * <ul>
     * <li>{@link Params#address(String) origin}</li>
     * <li>{@link Params#latlng(String) destinations} }</li>
     * </ul>
     * <p/>
     *
     * @throws java.io.IOException if there is a problem communicating with the Google GeoCoding API service
     */
    public static Response geocoding(Params params)
            throws IOException {

        JsonReader in = reader(params.format());
        try {
            return new Response(in);
        } finally {
            Closeables.close(in, true);
        }
    }

    /**
     * Get a reader for the URL.
     */
    private static JsonReader reader(String url) throws IOException {
        URLConnection con = HttpClient.openConnection(new URL(url));
        return new JsonReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
    }

    /**
     * <p>
     * Parameters for Google GeoCoding API services. All methods return their instance so that calls
     * can be chained. For example:
     * </p>
     * <p/>
     * <pre>{@code
     * Params p = new Params().address("Albertina in Vienna, Austria").language("de")
     * </pre>
     */
    public static class Params {
        private String mAddress;
        private String mLatLng;
        private String mBounds;
        private String mLanguage;
        private String mRegion;
        private String[] mComponents;


        /**
         * sets the address which should be geocoded. (removed any previously set address or latlng)
         */
        public Params address(String address) {
            mAddress = address;
            mLatLng = null;
            return this;
        }

        /**
         * sets the location which should be reverse geocoded. (removed any previously set address or latlng)
         */
        public Params latlng(double latitude, double longitude) {
            mLatLng = String.format(Locale.US, "%f,%f", latitude, longitude);
            mAddress = null;
            return this;
        }

        /**
         * Add Location (latitude/longitude values) as an origin.
         */
        public Params bounds(double south, double west, double north, double east) {
            mBounds = String.format("%f|%f|%f|%f", south, west, north, east);
            return this;
        }

        /**
         * Return results in this language, if possible. The value must be one of the supported
         * language codes.
         *
         * @see <a href="https://spreadsheets.google.com/pub?key=p9pdwsai2hDMsLkXsoM05KQ&gid=1"
         *      target="_blank">Supported Languages</a>
         */
        public Params language(String language) {
            mLanguage = language;
            return this;
        }

        /**
         * The region code, specified as a ccTLD ("top-level domain") two-character value. This parameter will
         * only influence, not fully restrict, results from the geocoder.
         *
         * @see <a href="http://en.wikipedia.org/wiki/CcTLD" target="_blank">ccTLD</a>
         */
        public Params region(String region) {
            mRegion = region;
            return this;
        }

        /**
         * Each component filter consists of a component:value pair and will fully restrict the
         * results from the geocoder.
         *
         * @see <a href="https://developers.google.com/maps/documentation/geocoding/#ComponentFiltering"
         * target="_blank">Component Filtering</a>
         */
        public Params components(String... components) {
            if (components != null) {
                if (mComponents == null) {
                    mComponents = components;
                } else {
                    mComponents = ObjectArrays.concat(mComponents, components, String.class);
                }
            } else {
                mComponents = null;
            }
            return this;
        }

        /**
         * Append the types param with pipe symbols between the values.
         */
        private static Joiner sPipes;

        /**
         * Get a URL formatted for the type of request.
         *
         * @since 1.0.0
         */
        public String format() {

            final String baseURL = "http://maps.googleapis.com/maps/api/geocode/json?";
            if (sPipes == null) {
                sPipes = Joiner.on("%7C").skipNulls(); // URL encoded
            }

            StringBuilder s = new StringBuilder(baseURL.length() + 256);
            s.append(baseURL);
            Configuration config = Sprockets.getConfig();
//			String key = config.getString("google.api-key");
//			checkState(!Strings.isNullOrEmpty(key), "google.api-key not set");
//			s.append("key=").append(key);
            boolean sensor = config.getBoolean("hardware.location");
            s.append("&sensor=").append(sensor);

            try {
                if (mAddress != null) {
                    s.append("&address=").append(URLEncoder.encode(mAddress, "UTF-8"));
                } else if (mLatLng != null) {
                    s.append("&latlng=").append(mLatLng);
                } else {
                    throw new IllegalStateException("either address or latlng must be set");
                }

                if (!Strings.isNullOrEmpty(mBounds)) {
                    s.append("&bounds=").append(mBounds);
                }

                s.append("&language=").append(!Strings.isNullOrEmpty(mLanguage) ? mLanguage : Locale.getDefault());

                if (!Strings.isNullOrEmpty(mRegion)) {
                    s.append("&region=").append(mRegion);
                }

                if (mComponents != null && mComponents.length > 0) {
                    s.append("&components=");
                    s.append(mComponents[0]);
                    for (int i = 1; i < mComponents.length; i++) {
                        s.append("%7C").append(mComponents[i]);
                    }
                }


            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UTF-8 encoding isn't supported?!", e);
            }

            return s.toString();
        }

        /**
         * Clear any set parameters so that this instance can be re-used for a new request.
         */
        public Params clear() {
            mAddress = null;
            mLatLng = null;
            mBounds = null;
            mLanguage = null;
            mRegion = null;
            mComponents = null;

            return this;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(mAddress, mLatLng, mBounds, mLanguage, mRegion, Arrays.hashCode(mComponents));
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null) {
                if (this == obj) {
                    return true;
                } else if (obj instanceof Params) {
                    Params o = (Params) obj;
                    return Objects.equal(mAddress, o.mAddress)
                            && Objects.equal(mLatLng, o.mLatLng)
                            && Objects.equal(mBounds, o.mBounds)
                            && Objects.equal(mLanguage, o.mLanguage)
                            && Objects.equal(mRegion, o.mRegion)
                            && Objects.equal(mComponents, o.mComponents);
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this).add("address", mAddress).add("latlng", mLatLng)
                    .add("bound", mBounds).add("language", mLanguage).add("region", mRegion)
                    .add("components", mComponents != null ? Arrays.toString(mComponents) : null)
                    .omitNullValues().toString();
        }

    }


    /**
     * Place search results.
     */
    public static class Response {

        /**
         * Indications of the success or failure of the request.
         */
        public enum Status {
            /* general Status */
            OK, INVALID_REQUEST, MAX_ELEMENTS_EXCEEDED, OVER_QUERY_LIMIT, REQUEST_DENIED,

            /* element-level status */
            NOT_FOUND, ZERO_RESULTS,

            /**
             * New status that hasn't been added here yet.
             */
            UNKNOWN;

            /**
             * Get the matching Status or {@link #UNKNOWN} if one can't be found.
             */
            private static Status get(String status) {
                try {
                    return Status.valueOf(status);
                } catch (IllegalArgumentException e) {
                    String msg = "Unknown status code: {0}.  "
                            + "If this hasn''t already been reported, please create a new issue at "
                            + "https://github.com/salento/sprockets/issues";
                    sLog.log(INFO, msg, status);
                    return UNKNOWN;
                }
            }
        }


        /**
         * All known response field keys and {@link #UNKNOWN} for new keys not included here yet.
         */
        enum Key {
            status, error_message, results, address_components, long_name, short_name, types, formatted_address,
            geometry, location, lat, lng, location_type, viewport, northeast, southwest,

            /**
             * New key that hasn't been added here yet.
             */
            UNKNOWN;

            /**
             * Get the matching Key or {@link #UNKNOWN} if one can't be found.
             */
            static Key get(String key) {
                try {
                    return Key.valueOf(key);
                } catch (IllegalArgumentException e) {
                    String msg = "Unknown response key: {0}.  "
                            + "If this hasn''t already been reported, please create a new issue at "
                            + "https://github.com/salento/sprockets/issues";
                    sLog.log(INFO, msg, key);
                    return UNKNOWN;
                }
            }
        }

        Status mStatus;
        String mErrorMessage;
        List<GeoCodedLocation> mResult;

        /**
         * Set the {@link Status} from the string value.
         */
        void status(String status) {
            mStatus = Status.get(status);
        }


        /**
         * Read fields from a search response.
         */
        private Response(JsonReader in) throws IOException {
            mResult = new ArrayList<GeoCodedLocation>();

            in.beginObject();
            while (in.hasNext()) {
                switch (Key.get(in.nextName())) {
                    case status:
                        status(in.nextString());
                        break;
                    case results:
                        in.beginArray();
                        while (in.hasNext()) {
                            if (mResult == null) {
                                mResult = new ArrayList<GeoCodedLocation>();
                            }
                            mResult.add(new GeoCodedLocation(in));
                        }
                        in.endArray();
                        break;
                    default:
                        in.skipValue();
                }
            }
            in.endObject();

        }

        /**
         * Indication of the success or failure of the request.
         */
        public Status getStatus() {
            return mStatus;
        }

        /**
         * List of Travel Distances
         */
        public List<GeoCodedLocation> getResult() {
            return Collections.unmodifiableList(mResult);
        }

    }


}
