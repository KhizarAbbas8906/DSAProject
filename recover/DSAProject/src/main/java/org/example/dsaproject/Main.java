package org.example.dsaproject;

import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private final String api_key = System.getenv("API_KEY");
    private final GeoApiContext apiContext = new GeoApiContext.Builder()
            .apiKey(api_key)
            .build();


    public String[][] findShortestPath(String[] places) throws PlaceNotFound,  IOException, InterruptedException, ApiException {

        String[] coordinates = findCords(places);
        List<String[][]> attributes  = distanceMatrix(coordinates);
        double[][] distance=new double[places.length][places.length];
        for(int i=0;i<attributes.get(0).length;i++){
            for(int j=i+1;j<attributes.get(0).length;j++){
                String dist = attributes.getFirst()[i][j];
                distance[i][j] = Double.parseDouble(dist.substring(0, dist.length()-3));
                distance[j][i]=distance[i][j];
            }
        }

        TSPSolver solver = new TSPSolver(distance, 0, places.length-1);
        TSPSolver.totalDistance =solver.findMinCost();
        int[] path = solver.printPath();

        return createPath(places,attributes,path);
    }
    private String[][] createPath(String[] places,List<String[][]> distance,int[] path){
        String[][] answer =new String[places.length-1][4];
        for(int i=0;i<places.length-1;i++) {
            answer[i][0] = places[path[i]];
            answer[i][1] = places[path[i+1]];
            answer[i][2] = String.valueOf(distance.getFirst()[path[i]][path[i+1]]);
            answer[i][3] = String.valueOf(distance.get(1)[path[i]][path[i+1]]);
            System.out.println(answer[i][3]);
        }
        return answer;
    }
    private String[] findCords(String[] places) throws PlaceNotFound, InterruptedException, ApiException, IOException {

        String[] coordinates = new String[places.length];
        int i = 0;
        for(String place : places) {
            PlacesSearchResponse response = PlacesApi.textSearchQuery(apiContext, place).await();
            if(response.results.length>0) {
                PlacesSearchResult result = response.results[0];
                String coords = result.geometry.location.lat + "," + result.geometry.location.lng;
                coordinates[i++] = coords;
            }else throw new PlaceNotFound("Place not found: "+place);
        }
        return coordinates;
    }
    private List<String[][]> distanceMatrix(String[] coordinates) throws IOException, InterruptedException, ApiException {
        List<String[][]> distances = new ArrayList<String[][]>();
        String[][] distanceMatrix = new String[coordinates.length][coordinates.length];
        String[][] timeMatrix = new String[coordinates.length][coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            for (int j = i+1; j < coordinates.length; j++) {
                DistanceMatrix result =  DistanceMatrixApi.newRequest(apiContext)
                        .origins(coordinates[i])
                        .destinations(coordinates[j])
                        .mode(TravelMode.DRIVING)
                        .await();

                String distance = result.rows[0].elements[0].distance.humanReadable;
                String time = result.rows[0].elements[0].duration.humanReadable;
                timeMatrix[i][j] = time;
                timeMatrix[j][i] = time;
                distanceMatrix[i][j] = distance;
                distanceMatrix[j][i] = distanceMatrix[i][j];
            }
        }
        distances.add(distanceMatrix);
        distances.add(timeMatrix);
        return distances;
    }
}


