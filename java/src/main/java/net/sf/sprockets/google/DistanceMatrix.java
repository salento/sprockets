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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.logging.Level.INFO;

/**
 * Methods for calling <a href="https://developers.google.com/maps/documentation/distancematrix/" target="_blank">Google Distance Matrix API</a>
 * services.
 */
public class DistanceMatrix {
	private static final Logger sLog = Loggers.get(DistanceMatrix.class);

	private DistanceMatrix() {
	}

	/**
	 * Get places that are near a location.
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#origins(String...) origin}</li>
     * <li>{@link Params#destinations(String...) destinations} }</li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#mode(String) mode}</li>
     * <li>{@link Params#language(String) language}</li>
	 * <li>{@link Params#avoid(String) avoid}</li>
	 * <li>{@link Params#units(String) unit system}</li>
	 * <li>{@link Params#departureTime(long) departure time} </li>
	 * </ul>
	 * <p>
	 *
	 * @throws java.io.IOException
	 *             if there is a problem communicating with the Google Places API service
	 */
	public static Response distances(Params params)
			throws IOException {

        JsonReader in = reader(params.format());
        try {
            return new Response(params.getOriginCount(), params.getDestinationCount(), in);
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
	 * Parameters for Google Distance Matrix API services. All methods return their instance so that calls
	 * can be chained. For example:
	 * </p>
	 *
	 * <pre>{@code
	 * Params p = new Params().origins("Albertina in Vienna, Austria").
     *      destinations("48.20274,16.368843", "2116039,16.37701").mode("walking")
	 * }</pre>
	 */
	public static class Params {
        private String[] mOrigins;
        private String[] mDestinations;
        private String mMode;
        private String mLanguage;
        private String mAvoid;
        private String mUnits;
        private long mDepartureTime;


		/**
		 * Add Location (an address or latitude/longitude values) as an origin. Calling this method multiple
		 * times will append to the list of types to match. Provide null to reset the list.
		 */
		public Params origins(String... origins) {
			if (origins != null) {
				if (mOrigins == null) {
                    mOrigins = origins;
				} else {
                    mOrigins = ObjectArrays.concat(mOrigins, origins, String.class);
				}
			} else {
                mOrigins = null;
			}
			return this;
		}

        /**
         * Add Location (latitude/longitude values) as an origin.
         */
        public Params origin(double latitude, double longitude) {
            if (mOrigins == null) {
                mOrigins = new String[]{latitude + "," + longitude};
            } else {
                mOrigins = ObjectArrays.concat(mOrigins, new String[]{latitude + "," + longitude}, String.class);
            }
            return this;
        }

        /**
         * Add Location (an address or latitude/longitude values) as a destination. Calling this method multiple
         * times will append to the list of types to match. Provide null to reset the list.
         */
        public Params destinations(String... destinations) {
            if (destinations != null) {
                if (mDestinations == null) {
                    mDestinations = destinations;
                } else {
                    mDestinations = ObjectArrays.concat(mDestinations, destinations, String.class);
                }
            } else {
                mDestinations = null;
            }
            return this;
        }

        /**
         * Add Location (latitude/longitude values) as a destination.
         */
        public Params destination(double latitude, double longitude) {
            if (mDestinations == null) {
                mDestinations = new String[]{latitude + "," + longitude};
            } else {
                mDestinations = ObjectArrays.concat(mDestinations, new String[]{latitude + "," + longitude}, String.class);
            }
            return this;
        }

        /**
         * specifies the mode of transport to use when calculating directions.
         *
         * Valid modes are: driving (default), walking, bicycling
         */
        public Params mode(String mode) {
            mMode = mode;
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
         * restrictions to the route. Only one restriction can be specified.
         *
         * Valid restrictions are: tolls, highways
         *
         * @see <a href="https://developers.google.com/maps/documentation/distancematrix/#Restrictions"
         *      target="_blank">Supported Languages</a>
         */
        public Params avoid(String avoid) {
            mAvoid = avoid;
            return this;
        }

        /**
         * Specifies the unit system to use when expressing distance as text.
         *
         * Valid unit systems are: metric (default), imperial
         *
         * @see <a href="https://developers.google.com/maps/documentation/distancematrix/#unit_systems"
         *      target="_blank">Supported Languages</a>
         */
        public Params units(String units) {
            mUnits = units;
            return this;
        }

        /**
         * Specifies the desired time of departure as seconds since midnight, January 1, 1970 UTC.
         * The value must be set to within a few minutes of the current time
         *
         */
        public Params departureTime(long departureTime) {
            mDepartureTime = departureTime;
            return this;
        }

        /** Append the types param with pipe symbols between the values. */
        private static Joiner sPipes;

        /**
		 * Get a URL formatted for the type of request.
		 *
		 * @since 1.0.0
		 */
		public String format() {

            final String baseURL = "https://maps.googleapis.com/maps/api/distancematrix/json?";
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
                if (mOrigins==null) throw new NullPointerException("origins must be set");
                s.append("&origins=");
                if (mOrigins.length>0) s.append(URLEncoder.encode(mOrigins[0], "UTF-8"));
                for (int i=1; i<mOrigins.length; i++) {
                    s.append("%7C").append(URLEncoder.encode(mOrigins[i], "UTF-8"));
                }

                if (mDestinations==null) throw new NullPointerException("destinations must be set");
                s.append("&destinations=");
                if (mDestinations.length>0) s.append(URLEncoder.encode(mDestinations[0], "UTF-8"));
                for (int i=1; i<mDestinations.length; i++) {
                    s.append("%7C").append(URLEncoder.encode(mDestinations[i], "UTF-8"));
                }

            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UTF-8 encoding isn't supported?!", e);
            }

            if (!Strings.isNullOrEmpty(mMode)) {
                s.append("&mode=").append(mMode);
            }
            s.append("&language=").append(!Strings.isNullOrEmpty(mLanguage) ? mLanguage : Locale.getDefault());
            if (!Strings.isNullOrEmpty(mAvoid)) {
                s.append("&avoid=").append(mAvoid);
            }
            if (!Strings.isNullOrEmpty(mUnits)) {
                s.append("&units=").append(mUnits);
            }
            if (mDepartureTime>0) {
                s.append("&departure_time=").append(mDepartureTime);
            }


			return s.toString();
		}

		/**
		 * Clear any set parameters so that this instance can be re-used for a new request.
		 *
		 * @since 1.0.0
		 */
		public Params clear() {
            mOrigins = null;
            mDestinations = null;
            mMode = null;
            mLanguage = null;
            mAvoid = null;
            mUnits = null;
            mDepartureTime = 0;

			return this;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(Arrays.hashCode(mOrigins), Arrays.hashCode(mDestinations),
                    mMode, mLanguage, mAvoid, mUnits, mDepartureTime);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj != null) {
				if (this == obj) {
					return true;
				} else if (obj instanceof Params) {
					Params o = (Params) obj;
					return Objects.equal(mOrigins, o.mOrigins)
                            && Objects.equal(mDestinations, o.mDestinations)
                            && Objects.equal(mMode, o.mMode)
                            && Objects.equal(mLanguage, o.mLanguage)
                            && Objects.equal(mAvoid, o.mAvoid)
                            && Objects.equal(mUnits, o.mUnits)
                            && mDepartureTime == o.mDepartureTime;
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return Objects.toStringHelper(this).add("origin", mOrigins != null ? Arrays.toString(mOrigins) : null)
                    .add("destinations", mDestinations != null ? Arrays.toString(mDestinations) : null)
                    .add("mode", mMode).add("language", mLanguage)
                    .add("avoid", mAvoid).add("units", mUnits)
                    .add("departureTime", mDepartureTime != 0 ? mDepartureTime : null)
					.omitNullValues().toString();
		}

        public int getOriginCount() {
            return mOrigins.length;
        }

        public int getDestinationCount() {
            return mDestinations.length;
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

            /** New status that hasn't been added here yet. */
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
            status, error_message, id, origin_addresses, destination_addresses,
            rows, elements, duration, distance, value, text,
            /** New key that hasn't been added here yet. */
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
        List<TravelDistance> mResult;
        String[] mOriginAddresses;
        String[] mDestinationAddresses;

        /**
		 * Read fields from a search response.
		 */
		private Response(int originCount, int destinationCount, JsonReader in) throws IOException {
            int row = 0, element = 0;
            mResult = new ArrayList<TravelDistance>(originCount * destinationCount);
            mOriginAddresses = new String[originCount];
            mDestinationAddresses = new String[destinationCount];

			in.beginObject();
			while (in.hasNext()) {
				switch (Key.get(in.nextName())) {
				    case status:
					    mStatus = Status.get(in.nextString());
					    break;
                    case error_message:
                        mErrorMessage = in.nextString();
                        break;
                    case origin_addresses:
                        in.beginArray();
                        for (int i=0; in.hasNext(); i++) {
                            mOriginAddresses[i] = in.nextString();
                        }
                        in.endArray();
                        break;
                    case destination_addresses:
                        in.beginArray();
                        for (int i=0; in.hasNext(); i++) {
                            mDestinationAddresses[i] = in.nextString();
                        }
                        in.endArray();
                        break;

                    case rows:
                        in.beginArray();
                        while (in.hasNext()) {
                            in.beginObject();
                            if (Key.get(in.nextName())==Key.elements) {
                                in.beginArray();
                                while (in.hasNext()) {
                                    mResult.add(new TravelDistance(row, element, in));
                                    element ++;
                                }
                                in.endArray();
                            } else {
                                in.skipValue(); //  should not be there
                            }

                            in.endObject();
                            row ++;
                            element = 0;
                        }
                        in.endArray();
                        break;
                    default:
                        in.skipValue();
				}
			}
			in.endObject();

            // copy address zu traveldistance objects
            for (TravelDistance td : mResult) {
                td.mOriginAddress = mOriginAddresses[td.mOriginId];
                td.mDestinationAddress = mDestinationAddresses[td.mDestinationId];
            }
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
        public List<TravelDistance> getResult() {
            return Collections.unmodifiableList(mResult);
        }

    }


}
