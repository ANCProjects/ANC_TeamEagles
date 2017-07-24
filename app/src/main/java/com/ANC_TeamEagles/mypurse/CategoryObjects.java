package com.ANC_TeamEagles.mypurse;

/**
 * Created by Administrator on 7/23/2017.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryObjects {

    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> entertainment = new ArrayList<String>();
        entertainment.add("Entertainment General");
        entertainment.add("Cinema and Theatre");
        entertainment.add("Bar");
        entertainment.add("Games");
        entertainment.add("Trips");
        entertainment.add("Vacation");

        List<String> amenities = new ArrayList<String>();
        amenities.add("Car");
        amenities.add("Mechanic");
        amenities.add("Equipments");

        List<String> Children = new ArrayList<String>();
        Children.add("Children General");
        Children.add("Toys");
        Children.add("Clothing");
        Children.add("School");
        Children.add("Pocket Money");

        List<String> home = new ArrayList<String>();
        home.add("Home General");
        home.add("Electricity");
        home.add("Water");
        home.add("Gas");
        home.add("Rent");
        home.add("Internet");
        home.add("Tv");
        home.add("Furniture");
        home.add("Repairs");
        home.add("Cleaning products");

        List<String> health = new ArrayList<String>();
        health.add("Health and Beauty General");
        health.add("Cosmetics");
        health.add("Perfume");
        health.add("HairDresser");
        health.add("Beautician");
        health.add("Solarium");
        health.add("Nutrients");
        health.add("Medicaments");
        health.add("Barber");
        health.add("Cleaning products");

        List<String> uncategorised = new ArrayList<String>();

        List<String> food = new ArrayList<String>();
        food.add("Food General");
        food.add("Supermarket");
        food.add("Restaurant");
        food.add("HairDresser");
        food.add("Everyday");

        List<String> clothing = new ArrayList<String>();
        clothing.add("Clothing General");
        clothing.add("Trousers");
        clothing.add("Shoes");
        clothing.add("Sweaters");
        clothing.add("Shirts");
        clothing.add("Jackets");
        clothing.add("T-Shirts");
        clothing.add("Jewellery");
        clothing.add("Underwear");


        expandableListDetail.put("Uncategorized", uncategorised);
        expandableListDetail.put("Amenities", amenities);
        expandableListDetail.put("Children", Children);
        expandableListDetail.put("Clothing", clothing);
        expandableListDetail.put("Entertainment", entertainment);
        expandableListDetail.put("Food", food);
        expandableListDetail.put("Health and Beauty", health);
        expandableListDetail.put("Home", home);

        return expandableListDetail;
    }
}
