package utilities;

import com.spatial4j.core.context.jts.JtsSpatialContext;
import com.spatial4j.core.distance.DistanceUtils;
import com.spatial4j.core.shape.Point;
import com.spatial4j.core.shape.Rectangle;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

/**
 * This class is reference to the url: https://www.ibm.com/developerworks/library/j-coordconvert/
 *
 * It has good explanation about UTM/Latitude-Longitude conversion
 *
 * Created by sateesh on 18/1/17.
 */
public class GeoCoordinatesUtil {

    private static final double EARTH_RADIUS = 6371.01;
    private static final double MIN_LAT = Math.toRadians(-90d);  // -PI/2
    private static final double MAX_LAT = Math.toRadians(90d);   //  PI/2
    private static final double MIN_LON = Math.toRadians(-180d); // -PI
    private static final double MAX_LON = Math.toRadians(180d);  //  PI

    private JtsSpatialContext context = JtsSpatialContext.GEO;;

    public double[] utm2LatLon(String UTM) {
        UTM2LatLon c = new UTM2LatLon();
        return c.convertUTMToLatLong(UTM);
    }

    public String latLon2UTM(double latitude, double longitude) {
        LatLon2UTM c = new LatLon2UTM();
        return c.convertLatLonToUTM(latitude, longitude);

    }

    public static double fetchDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344;
        if (!Double.isNaN(dist)) {
            return (dist);
        }
        return 0;
    }

    public Rectangle boundingBox(double latitude, double longitude, double distance) {
        double d = distance * DistanceUtils.KM_TO_DEG;
        Point point = getPoint(longitude, latitude);
        return context.getDistCalc().calcBoxByDistFromPt(point, d, context, null);
    }

    public Point getPoint(double latitude, double longitude) {
        return context.makePoint(longitude, latitude);
    }

    public boolean calculateDistance(Point minPoint, Point maxPoint, Point point, Double distance) {
        double d1 = context.getDistCalc().distance(point, minPoint);
        double d2 = context.getDistCalc().distance(point, maxPoint);

        double d = distance * DistanceUtils.KM_TO_DEG;
        return (d >= d1 || d >= d2);
    }

    //Can also be used
    public Double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        Point point1 = getPoint(latitude1, longitude1);
        Point point2 = getPoint(latitude2, longitude2);
        Double distance = context.getDistCalc().distance(point1, point2);
        distance = DistanceUtils.DEG_TO_KM * distance;
        return distance;
    }

    //To be used
    public double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return dist * 1.609344 * 1000;
    }

    public double[] boundingCoordinates(double latitude, double longitude, double distance) {

        if (distance < 0d)
            throw new IllegalArgumentException();

        // angular distance in radians on a great circle
        double radDist = distance / EARTH_RADIUS;

        double minLat = latitude - radDist;
        double maxLat = latitude + radDist;

        double minLon, maxLon;
        if (minLat > MIN_LAT && maxLat < MAX_LAT) {
            double deltaLon = Math.asin(Math.sin(radDist) /
                    Math.cos(latitude));
            minLon = longitude - deltaLon;
            if (minLon < MIN_LON) minLon += 2d * Math.PI;
            maxLon = longitude + deltaLon;
            if (maxLon > MAX_LON) maxLon -= 2d * Math.PI;
        } else {
            // a pole is within the distance
            minLat = Math.max(minLat, MIN_LAT);
            maxLat = Math.min(maxLat, MAX_LAT);
            minLon = MIN_LON;
            maxLon = MAX_LON;
        }



        double minLongitude = Math.toDegrees(longitude) - Math.toDegrees(distance/EARTH_RADIUS/Math.cos(latitude));
        double maxLongitude = Math.toDegrees(longitude) + Math.toDegrees(distance/EARTH_RADIUS/Math.cos(latitude));

        double minLatitude = Math.toDegrees(latitude) + Math.toDegrees(distance/EARTH_RADIUS);
        double maxLatitude = Math.toDegrees(latitude) - Math.toDegrees(distance/EARTH_RADIUS);
        return new double[]{Math.toRadians(minLatitude), Math.toRadians(minLongitude), Math.toRadians(maxLatitude), Math.toRadians(maxLongitude)};
        /*return new double[]{Math.toRadians(minLat), Math.toRadians(minLon), Math.toRadians(maxLat), Math.toRadians(maxLon)};*/
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    private void validate(double latitude, double longitude) {
        if (latitude < -90.0 || latitude > 90.0 || longitude < -180.0
                || longitude >= 180.0) {
            throw new IllegalArgumentException(
                    "Legal ranges: latitude [-90,90], longitude [-180,180).");
        }

    }

    public String latLon2MGRUTM(double latitude, double longitude) {
        LatLon2MGRUTM c = new LatLon2MGRUTM();
        return c.convertLatLonToMGRUTM(latitude, longitude);

    }

    public double[] mgrutm2LatLon(String MGRUTM) {
        MGRUTM2LatLon c = new MGRUTM2LatLon();
        return c.convertMGRUTMToLatLong(MGRUTM);
    }

    public double degreeToRadian(double degree) {
        return degree * Math.PI / 180;
    }

    public double radianToDegree(double radian) {
        return radian * 180 / Math.PI;
    }

    private double POW(double a, double b) {
        return Math.pow(a, b);
    }

    private double SIN(double value) {
        return Math.sin(value);
    }

    private double COS(double value) {
        return Math.cos(value);
    }

    private double TAN(double value) {
        return Math.tan(value);
    }

    private class LatLon2UTM {
        public String convertLatLonToUTM(double latitude, double longitude) {
            validate(latitude, longitude);
            String UTM = "";

            setVariables(latitude, longitude);

            String longZone = getLongZone(longitude);
            LatZones latZones = new LatZones();
            String latZone = latZones.getLatZone(latitude);

            double _easting = getEasting();
            double _northing = getNorthing(latitude);

            UTM = longZone + " " + latZone + " " + ((int) _easting) + " "
                    + ((int) _northing);
            // UTM = longZone + " " + latZone + " " + decimalFormat.format(_easting) +
            // " "+ decimalFormat.format(_northing);

            return UTM;

        }

        protected void setVariables(double latitude, double longitude) {
            latitude = degreeToRadian(latitude);
            rho = equatorialRadius * (1 - e * e)
                    / POW(1 - POW(e * SIN(latitude), 2), 3 / 2.0);

            nu = equatorialRadius / POW(1 - POW(e * SIN(latitude), 2), (1 / 2.0));

            double var1;
            if (longitude < 0.0) {
                var1 = ((int) ((180 + longitude) / 6.0)) + 1;
            } else {
                var1 = ((int) (longitude / 6)) + 31;
            }
            double var2 = (6 * var1) - 183;
            double var3 = longitude - var2;
            p = var3 * 3600 / 10000;

            S = A0 * latitude - B0 * SIN(2 * latitude) + C0 * SIN(4 * latitude) - D0
                    * SIN(6 * latitude) + E0 * SIN(8 * latitude);

            K1 = S * k0;
            K2 = nu * SIN(latitude) * COS(latitude) * POW(sin1, 2) * k0 * (100000000)
                    / 2;
            K3 = ((POW(sin1, 4) * nu * SIN(latitude) * Math.pow(COS(latitude), 3)) / 24)
                    * (5 - POW(TAN(latitude), 2) + 9 * e1sq * POW(COS(latitude), 2) + 4
                    * POW(e1sq, 2) * POW(COS(latitude), 4))
                    * k0
                    * (10000000000000000L);

            K4 = nu * COS(latitude) * sin1 * k0 * 10000;

            K5 = POW(sin1 * COS(latitude), 3) * (nu / 6)
                    * (1 - POW(TAN(latitude), 2) + e1sq * POW(COS(latitude), 2)) * k0
                    * 1000000000000L;

            A6 = (POW(p * sin1, 6) * nu * SIN(latitude) * POW(COS(latitude), 5) / 720)
                    * (61 - 58 * POW(TAN(latitude), 2) + POW(TAN(latitude), 4) + 270
                    * e1sq * POW(COS(latitude), 2) - 330 * e1sq
                    * POW(SIN(latitude), 2)) * k0 * (1E+24);

        }

        protected String getLongZone(double longitude) {
            double longZone = 0;
            if (longitude < 0.0) {
                longZone = ((180.0 + longitude) / 6) + 1;
            } else {
                longZone = (longitude / 6) + 31;
            }
            String val = String.valueOf((int) longZone);
            if (val.length() == 1) {
                val = "0" + val;
            }
            return val;
        }

        protected double getNorthing(double latitude) {
            double northing = K1 + K2 * p * p + K3 * POW(p, 4);
            if (latitude < 0.0) {
                northing = 10000000 + northing;
            }
            return northing;
        }

        protected double getEasting() {
            return 500000 + (K4 * p + K5 * POW(p, 3));
        }

        // Lat Lon to UTM variables

        // equatorial radius
        double equatorialRadius = 6378137;

        // polar radius
        double polarRadius = 6356752.314;

        // flattening
        double flattening = 0.00335281066474748;// (equatorialRadius-polarRadius)/equatorialRadius;

        // inverse flattening 1/flattening
        double inverseFlattening = 298.257223563;// 1/flattening;

        // Mean radius
        double rm = POW(equatorialRadius * polarRadius, 1 / 2.0);

        // scale factor
        double k0 = 0.9996;

        // eccentricity
        double e = Math.sqrt(1 - POW(polarRadius / equatorialRadius, 2));

        double e1sq = e * e / (1 - e * e);

        double n = (equatorialRadius - polarRadius)
                / (equatorialRadius + polarRadius);

        // r curv 1
        double rho = 6368573.744;

        // r curv 2
        double nu = 6389236.914;

        // Calculate Meridional Arc Length
        // Meridional Arc
        double S = 5103266.421;

        double A0 = 6367449.146;

        double B0 = 16038.42955;

        double C0 = 16.83261333;

        double D0 = 0.021984404;

        double E0 = 0.000312705;

        // Calculation Constants
        // Delta Long
        double p = -0.483084;

        double sin1 = 4.84814E-06;

        // Coefficients for UTM Coordinates
        double K1 = 5101225.115;

        double K2 = 3750.291596;

        double K3 = 1.397608151;

        double K4 = 214839.3105;

        double K5 = -2.995382942;

        double A6 = -1.00541E-07;

    }

    private class LatLon2MGRUTM extends LatLon2UTM {
        public String convertLatLonToMGRUTM(double latitude, double longitude) {
            validate(latitude, longitude);
            String mgrUTM = "";

            setVariables(latitude, longitude);

            String longZone = getLongZone(longitude);
            LatZones latZones = new LatZones();
            String latZone = latZones.getLatZone(latitude);

            double _easting = getEasting();
            double _northing = getNorthing(latitude);
            Digraphs digraphs = new Digraphs();
            String digraph1 = digraphs.getDigraph1(Integer.parseInt(longZone),
                    _easting);
            String digraph2 = digraphs.getDigraph2(Integer.parseInt(longZone),
                    _northing);

            String easting = String.valueOf((int) _easting);
            if (easting.length() < 5) {
                easting = "00000" + easting;
            }
            easting = easting.substring(easting.length() - 5);

            String northing;
            northing = String.valueOf((int) _northing);
            if (northing.length() < 5) {
                northing = "0000" + northing;
            }
            northing = northing.substring(northing.length() - 5);

            mgrUTM = longZone + latZone + digraph1 + digraph2 + easting + northing;
            return mgrUTM;
        }
    }

    private class MGRUTM2LatLon extends UTM2LatLon {
        public double[] convertMGRUTMToLatLong(String mgrutm) {
            double[] latlon = {0.0, 0.0};
            // 02CNR0634657742
            int zone = Integer.parseInt(mgrutm.substring(0, 2));
            String latZone = mgrutm.substring(2, 3);

            String digraph1 = mgrutm.substring(3, 4);
            String digraph2 = mgrutm.substring(4, 5);
            easting = Double.parseDouble(mgrutm.substring(5, 10));
            northing = Double.parseDouble(mgrutm.substring(10, 15));

            LatZones lz = new LatZones();
            double latZoneDegree = lz.getLatZoneDegree(latZone);

            double a1 = latZoneDegree * 40000000 / 360.0;
            double a2 = 2000000 * Math.floor(a1 / 2000000.0);

            Digraphs digraphs = new Digraphs();

            double digraph2Index = digraphs.getDigraph2Index(digraph2);

            double startindexEquator = 1;
            if ((1 + zone % 2) == 1) {
                startindexEquator = 6;
            }

            double a3 = a2 + (digraph2Index - startindexEquator) * 100000;
            if (a3 <= 0) {
                a3 = 10000000 + a3;
            }
            northing = a3 + northing;

            zoneCM = -183 + 6 * zone;
            double digraph1Index = digraphs.getDigraph1Index(digraph1);
            int a5 = 1 + zone % 3;
            double[] a6 = {16, 0, 8};
            double a7 = 100000 * (digraph1Index - a6[a5 - 1]);
            easting = easting + a7;

            setVariables();

            double latitude = 0;
            latitude = 180 * (phi1 - fact1 * (fact2 + fact3 + fact4)) / Math.PI;

            if (latZoneDegree < 0) {
                latitude = 90 - latitude;
            }

            double d = _a2 * 180 / Math.PI;
            double longitude = zoneCM - d;

            if (getHemisphere(latZone).equals("S")) {
                latitude = -latitude;
            }

            latlon[0] = latitude;
            latlon[1] = longitude;
            return latlon;
        }
    }

    private class UTM2LatLon {
        double easting;

        double northing;

        int zone;

        String southernHemisphere = "ACDEFGHJKLM";

        protected String getHemisphere(String latZone) {
            String hemisphere = "N";
            if (southernHemisphere.indexOf(latZone) > -1) {
                hemisphere = "S";
            }
            return hemisphere;
        }

        public double[] convertUTMToLatLong(String UTM) {
            double[] latlon = {0.0, 0.0};
            String[] utm = UTM.split(" ");
            zone = Integer.parseInt(utm[0]);
            String latZone = utm[1];
            easting = Double.parseDouble(utm[2]);
            northing = Double.parseDouble(utm[3]);
            String hemisphere = getHemisphere(latZone);
            double latitude = 0.0;
            double longitude = 0.0;

            if (hemisphere.equals("S")) {
                northing = 10000000 - northing;
            }
            setVariables();
            latitude = 180 * (phi1 - fact1 * (fact2 + fact3 + fact4)) / Math.PI;

            if (zone > 0) {
                zoneCM = 6 * zone - 183.0;
            } else {
                zoneCM = 3.0;

            }

            longitude = zoneCM - _a3;
            if (hemisphere.equals("S")) {
                latitude = -latitude;
            }

            latlon[0] = latitude;
            latlon[1] = longitude;
            return latlon;

        }

        protected void setVariables() {
            arc = northing / k0;
            mu = arc
                    / (a * (1 - POW(e, 2) / 4.0 - 3 * POW(e, 4) / 64.0 - 5 * POW(e, 6) / 256.0));

            ei = (1 - POW((1 - e * e), (1 / 2.0)))
                    / (1 + POW((1 - e * e), (1 / 2.0)));

            ca = 3 * ei / 2 - 27 * POW(ei, 3) / 32.0;

            cb = 21 * POW(ei, 2) / 16 - 55 * POW(ei, 4) / 32;
            cc = 151 * POW(ei, 3) / 96;
            cd = 1097 * POW(ei, 4) / 512;
            phi1 = mu + ca * SIN(2 * mu) + cb * SIN(4 * mu) + cc * SIN(6 * mu) + cd
                    * SIN(8 * mu);

            n0 = a / POW((1 - POW((e * SIN(phi1)), 2)), (1 / 2.0));

            r0 = a * (1 - e * e) / POW((1 - POW((e * SIN(phi1)), 2)), (3 / 2.0));
            fact1 = n0 * TAN(phi1) / r0;

            _a1 = 500000 - easting;
            dd0 = _a1 / (n0 * k0);
            fact2 = dd0 * dd0 / 2;

            t0 = POW(TAN(phi1), 2);
            Q0 = e1sq * POW(COS(phi1), 2);
            fact3 = (5 + 3 * t0 + 10 * Q0 - 4 * Q0 * Q0 - 9 * e1sq) * POW(dd0, 4)
                    / 24;

            fact4 = (61 + 90 * t0 + 298 * Q0 + 45 * t0 * t0 - 252 * e1sq - 3 * Q0
                    * Q0)
                    * POW(dd0, 6) / 720;

            //
            lof1 = _a1 / (n0 * k0);
            lof2 = (1 + 2 * t0 + Q0) * POW(dd0, 3) / 6.0;
            lof3 = (5 - 2 * Q0 + 28 * t0 - 3 * POW(Q0, 2) + 8 * e1sq + 24 * POW(t0, 2))
                    * POW(dd0, 5) / 120;
            _a2 = (lof1 - lof2 + lof3) / COS(phi1);
            _a3 = _a2 * 180 / Math.PI;

        }

        double arc;

        double mu;

        double ei;

        double ca;

        double cb;

        double cc;

        double cd;

        double n0;

        double r0;

        double _a1;

        double dd0;

        double t0;

        double Q0;

        double lof1;

        double lof2;

        double lof3;

        double _a2;

        double phi1;

        double fact1;

        double fact2;

        double fact3;

        double fact4;

        double zoneCM;

        double _a3;

        double b = 6356752.314;

        double a = 6378137;

        double e = 0.081819191;

        double e1sq = 0.006739497;

        double k0 = 0.9996;

    }

    private class Digraphs {
        private Map digraph1 = new Hashtable();

        private Map digraph2 = new Hashtable();

        private String[] digraph1Array = {"A", "B", "C", "D", "E", "F", "G", "H",
                "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
                "Y", "Z"};

        private String[] digraph2Array = {"V", "A", "B", "C", "D", "E", "F", "G",
                "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V"};

        public Digraphs() {
            digraph1.put(new Integer(1), "A");
            digraph1.put(new Integer(2), "B");
            digraph1.put(new Integer(3), "C");
            digraph1.put(new Integer(4), "D");
            digraph1.put(new Integer(5), "E");
            digraph1.put(new Integer(6), "F");
            digraph1.put(new Integer(7), "G");
            digraph1.put(new Integer(8), "H");
            digraph1.put(new Integer(9), "J");
            digraph1.put(new Integer(10), "K");
            digraph1.put(new Integer(11), "L");
            digraph1.put(new Integer(12), "M");
            digraph1.put(new Integer(13), "N");
            digraph1.put(new Integer(14), "P");
            digraph1.put(new Integer(15), "Q");
            digraph1.put(new Integer(16), "R");
            digraph1.put(new Integer(17), "S");
            digraph1.put(new Integer(18), "T");
            digraph1.put(new Integer(19), "U");
            digraph1.put(new Integer(20), "V");
            digraph1.put(new Integer(21), "W");
            digraph1.put(new Integer(22), "X");
            digraph1.put(new Integer(23), "Y");
            digraph1.put(new Integer(24), "Z");

            digraph2.put(new Integer(0), "V");
            digraph2.put(new Integer(1), "A");
            digraph2.put(new Integer(2), "B");
            digraph2.put(new Integer(3), "C");
            digraph2.put(new Integer(4), "D");
            digraph2.put(new Integer(5), "E");
            digraph2.put(new Integer(6), "F");
            digraph2.put(new Integer(7), "G");
            digraph2.put(new Integer(8), "H");
            digraph2.put(new Integer(9), "J");
            digraph2.put(new Integer(10), "K");
            digraph2.put(new Integer(11), "L");
            digraph2.put(new Integer(12), "M");
            digraph2.put(new Integer(13), "N");
            digraph2.put(new Integer(14), "P");
            digraph2.put(new Integer(15), "Q");
            digraph2.put(new Integer(16), "R");
            digraph2.put(new Integer(17), "S");
            digraph2.put(new Integer(18), "T");
            digraph2.put(new Integer(19), "U");
            digraph2.put(new Integer(20), "V");

        }

        public int getDigraph1Index(String letter) {
            for (int i = 0; i < digraph1Array.length; i++) {
                if (digraph1Array[i].equals(letter)) {
                    return i + 1;
                }
            }

            return -1;
        }

        public int getDigraph2Index(String letter) {
            for (int i = 0; i < digraph2Array.length; i++) {
                if (digraph2Array[i].equals(letter)) {
                    return i;
                }
            }

            return -1;
        }

        public String getDigraph1(int longZone, double easting) {
            int a1 = longZone;
            double a2 = 8 * ((a1 - 1) % 3) + 1;

            double a3 = easting;
            double a4 = a2 + ((int) (a3 / 100000)) - 1;
            return (String) digraph1.get(new Integer((int) Math.floor(a4)));
        }

        public String getDigraph2(int longZone, double northing) {
            int a1 = longZone;
            double a2 = 1 + 5 * ((a1 - 1) % 2);
            double a3 = northing;
            double a4 = (a2 + ((int) (a3 / 100000)));
            a4 = (a2 + ((int) (a3 / 100000.0))) % 20;
            a4 = Math.floor(a4);
            if (a4 < 0) {
                a4 = a4 + 19;
            }
            return (String) digraph2.get(new Integer((int) Math.floor(a4)));

        }

    }

    private class LatZones {
        private char[] letters = {'A', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
                'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Z'};

        private int[] degrees = {-90, -84, -72, -64, -56, -48, -40, -32, -24, -16,
                -8, 0, 8, 16, 24, 32, 40, 48, 56, 64, 72, 84};

        private char[] negLetters = {'A', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
                'L', 'M'};

        private int[] negDegrees = {-90, -84, -72, -64, -56, -48, -40, -32, -24,
                -16, -8};

        private char[] posLetters = {'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Z'};

        private int[] posDegrees = {0, 8, 16, 24, 32, 40, 48, 56, 64, 72, 84};

        private int arrayLength = 22;

        public LatZones() {
        }

        public int getLatZoneDegree(String letter) {
            char ltr = letter.charAt(0);
            for (int i = 0; i < arrayLength; i++) {
                if (letters[i] == ltr) {
                    return degrees[i];
                }
            }
            return -100;
        }

        public String getLatZone(double latitude) {
            int latIndex = -2;
            int lat = (int) latitude;

            if (lat >= 0) {
                int len = posLetters.length;
                for (int i = 0; i < len; i++) {
                    if (lat == posDegrees[i]) {
                        latIndex = i;
                        break;
                    }

                    if (lat > posDegrees[i]) {
                        continue;
                    } else {
                        latIndex = i - 1;
                        break;
                    }
                }
            } else {
                int len = negLetters.length;
                for (int i = 0; i < len; i++) {
                    if (lat == negDegrees[i]) {
                        latIndex = i;
                        break;
                    }

                    if (lat < negDegrees[i]) {
                        latIndex = i - 1;
                        break;
                    } else {
                        continue;
                    }

                }

            }

            if (latIndex == -1) {
                latIndex = 0;
            }
            if (lat >= 0) {
                if (latIndex == -2) {
                    latIndex = posLetters.length - 1;
                }
                return String.valueOf(posLetters[latIndex]);
            } else {
                if (latIndex == -2) {
                    latIndex = negLetters.length - 1;
                }
                return String.valueOf(negLetters[latIndex]);

            }
        }

    }
    public String generateUTM(final Integer projectId, final Double easting, final Double northing) {
        if (projectId == 2) {
            return String.format("43 R %.2f %.2f", easting, northing);
        } else if (projectId == 8) {
            return String.format("45 Q %.2f %.2f", easting, northing);
        } else if (projectId == 12) {
            return String.format("43 S %.2f %.2f", easting, northing);
        } else {
            return String.format("44 R %.2f %.2f", easting, northing);
        }
    }

    public final static void main(String[] args) {
        GeoCoordinatesUtil geoUtil = new GeoCoordinatesUtil();
        /*String utm = geoUtil.latLon2UTM(26.254706, 82.037258);
        System.out.println(utm);
        double[] latlng = geoUtil.utm2LatLon(utm);
        System.out.println(latlng);*/
        /*String utm = String.format("44 R %.2f %.2f", 647557.551d,	2863849.868d);
        double[] latLng1 = geoUtil.utm2LatLon(utm);
        System.out.println(latLng1[0] + "  " + latLng1[1]);*/

        double[] latlng1 = new double[] {17.3850440,78.4866710};


        /*String utm2 = String.format("44 R %.2f %.2f", 604432.798d, 2903777.621d);
        double[] latLng2 = geoUtil.utm2LatLon(utm2);


        double distance = fetchDistance(latLng1[0], latLng1[1], latLng2[0], latLng2[1]);
        System.out.println(distance);*/
        Rectangle rectangle = geoUtil.boundingBox(latlng1[0], latlng1[1], 0.099);
        System.out.println(rectangle.getMinX() + " " + rectangle.getMinY() + " " + rectangle.getMaxX() + " " + rectangle.getMaxY());

        double[] latlng2 = new double[] {rectangle.getMinX() , rectangle.getMinY()};
        double distance = geoUtil.calculateDistance(latlng1[0], latlng1[1], latlng2[0],latlng2[1]);
        System.out.println(distance);

        latlng2 = new double[] {rectangle.getMaxX() , rectangle.getMaxY()};
        distance = geoUtil.calculateDistance(latlng1[0], latlng1[1], latlng2[0],latlng2[1]);
        System.out.println(distance);

        double[] boundingCoordinates = geoUtil.boundingCoordinates(latlng1[0], latlng1[1], 8.071);
        System.out.println(boundingCoordinates[0] + "  " + boundingCoordinates[1] + "  " + boundingCoordinates[2] + "  " + boundingCoordinates[3]);

        distance = geoUtil.calculateDistance(latlng1[0], latlng1[1], boundingCoordinates[0],boundingCoordinates[1]);
        System.out.println(distance);

        distance = geoUtil.calculateDistance(17.423702, 78.458389, 17.518637,78.396253);
        System.out.println(distance);

        distance = geoUtil.distance(17.423702, 78.458389, 17.518637,78.396253);
        System.out.println(distance);


    }


    public double haversine(double lat1, double lon1, double lat2, double lon2) {
        // if any of the position is invalid
        if (lat1 == 0 || lon1 == 0 || lat2 == 0 || lon2 == 0) {
            return 0;
        }

        final int R = 6371; // Radious of the earth
        Double latDistance = toRad(lat2-lat1);
        Double lonDistance = toRad(lon2-lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double distance = R * c * 1000;

        return distance;
    }

    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }

    public static String getAddressByGpsCoordinates(String lat, String lng) {
        String formattedAddress = "";
        HttpURLConnection connection = null;
        try {

            URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng="
                    + lat + "," + lng + "&key=AIzaSyDiED9ifmB7ypljz18ov2aMDCZAAfDAOfI&sensor=true");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON_VALUE);

            if (connection.getResponseCode() == 200) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((connection.getInputStream())));

                String output;
                String result = "";
                while ((output = bufferedReader.readLine()) != null) {
                    result = result + output;
                }

                JSONParser parser = new JSONParser();
                JSONObject jsonObj = (JSONObject) parser.parse(result);
                JSONArray jsonArray = (JSONArray) jsonObj.get("results");
                if (jsonArray != null && !jsonArray.isEmpty()) {
                    JSONObject data = (JSONObject) jsonArray.get(0);
                    formattedAddress = (String) data.get("formatted_address");
                }
                bufferedReader.close();
            }

        } catch (MalformedURLException e) {
            System.out.println(String.format("Exception occurred while reading url in getAddressByGpsCoordinates - %s", e.getMessage()));
        } catch (ParseException e) {
            System.out.println(String.format("Exception occurred while parsing result in getAddressByGpsCoordinates - %s", e.getMessage()));
        } catch (Exception e) {
            System.out.println(String.format("Exception occurred in getAddressByGpsCoordinates - %s", e.getMessage()));
        } finally {
            connection.disconnect();
        }
        return formattedAddress;
    }
}
