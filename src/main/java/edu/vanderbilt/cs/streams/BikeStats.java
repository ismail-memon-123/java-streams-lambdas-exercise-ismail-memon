package edu.vanderbilt.cs.streams;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.HashSet;

import edu.vanderbilt.cs.streams.BikeRide.LatLng;

public class BikeStats {

    private BikeRide ride;

    public BikeStats(BikeRide ride) {
        this.ride = ride;
    }

    /**
     * @ToDo:
     *
     * Create a stream of DataFrames representing the average of the
     * sliding windows generated from the given window size.
     *
     * For example, if a windowSize of 3 was provided, the BikeRide.DataFrames
     * would be fetched with the BikeRide.fusedFramesStream() method. These
     * frames would be divided into sliding windows of size 3 using the
     * StreamUtils.slidingWindow() method. Each sliding window would be a
     * list of 3 DataFrame objects. You would produce a new DataFrame for
     * each window by averaging the grade, altitude, velocity, and heart
     * rate for the 3 DataFrame objects.
     *
     * For each window, you should use the coordinate of the first DataFrame in the window
     * for the location.
     *
     * @param windowSize
     * @return
     */
    public Stream<BikeRide.DataFrame> averagedDataFrameStream(int windowSize){
        Stream<BikeRide.DataFrame> streamer = ride.fusedFramesStream();
        List<List<BikeRide.DataFrame>> listV = StreamUtils.slidingWindow(streamer.collect(Collectors.toList()), windowSize).collect(Collectors.toList());
        List<Double> dV = new ArrayList<Double>();
        List<Double> dH = new ArrayList<Double>();
        List<Double> dG = new ArrayList<Double>();
        List<Double> dA = new ArrayList<Double>();
        List<Double> dC_Lat = new ArrayList<Double>();
        List<Double> dC_Long = new ArrayList<Double>();
        BikeRide.DataFrame[] df = new BikeRide.DataFrame[listV.size()];
        for (int i = 0; i < listV.size(); i++) {
            dV.add(StreamUtils.averageOfProperty(BikeRide.DataFrame::getVelocity).apply(listV.get(i)));
            dH.add(StreamUtils.averageOfProperty(BikeRide.DataFrame::getHeartRate).apply(listV.get(i)));
            dG.add(StreamUtils.averageOfProperty(BikeRide.DataFrame::getGrade).apply(listV.get(i)));
            dA.add(StreamUtils.averageOfProperty(BikeRide.DataFrame::getAltitude).apply(listV.get(i)));
            dC_Lat.add((listV.get(i).get(0)).getLat());
            dC_Long.add((listV.get(i).get(0)).getLong());
        }
        for (int i = 0; i < dV.size(); i++) {
            double[] coordinates = {dC_Lat.get(i), dC_Long.get(i)};
            BikeRide.LatLng coord = new BikeRide.LatLng(coordinates);
            BikeRide.DataFrame data = new BikeRide.DataFrame(coord, dG.get(i), dA.get(i), dV.get(i), dH.get(i));
            df[i] = data;
        }
        return Arrays.stream(df);
    }

    // @ToDo:
    //
    // Determine the stream of unique locations that the
    // rider stopped. A location is unique if there are no
    // other stops at the same latitude / longitude.
    // The rider is stopped if velocity = 0.
    //
    // For the purposes of this assignment, you should use
    // LatLng.equals() to determine if two locations are
    // the same.
    //
    public Stream<LatLng> locationsOfStops() {
        Stream<BikeRide.DataFrame> streamer = ride.fusedFramesStream();
        List<BikeRide.DataFrame> allZeroV = streamer.filter(df -> df.getVelocity() == 0).collect(Collectors.toList());

        Set<LatLng> set = new HashSet<LatLng>();
        for (BikeRide.DataFrame dfer : allZeroV) {
            set.add(dfer.coordinate);    
        }
        return set.stream();
    }

}
