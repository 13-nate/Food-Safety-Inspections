package ca.sfu.cmpt276projectaluminium;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RestaurantManager implements Iterable<Restaurant> {
    private List<Restaurant> restaurants = new ArrayList<>();

    public void add(Restaurant restaurant){
            restaurants.add(restaurant);
        }

    @Override
    public Iterator<Restaurant> iterator() {
            return restaurants.iterator();
        }

    public Restaurant myGet(int i) {
            return restaurants.get(i);
        }

    public int mySize(){
            return restaurants.size();
        }

    @Override
    public String toString() {
        return "LensManager{" +
                "lenses=" + restaurants +
                '}';
        }
    }

