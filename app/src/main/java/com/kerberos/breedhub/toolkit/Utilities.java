package com.kerberos.breedhub.toolkit;

import android.content.Context;

import androidx.fragment.app.FragmentManager;

import com.kerberos.breedhub.R;
import com.kerberos.breedhub.models.Locations;
import com.kerberos.breedhub.models.ServicesModel;
import com.kerberos.breedhub.views.NotificationDialog;
import com.maxkeppeler.sheets.calendar.CalendarMode;
import com.maxkeppeler.sheets.calendar.CalendarSheet;
import com.maxkeppeler.sheets.calendar.SelectionMode;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HexFormat;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public abstract class Utilities {
    public static String getGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 5 && hour < 12) {
            return "Good Morning";
        } else if (hour >= 12 && hour < 17) {
            return "Good Afternoon";
        } else if (hour >= 17 && hour < 21) {
            return "Good Evening";
        } else {
            return "Good Night";
        }
    }
    public static void getDate(String title, Context context, CalendarSheet calendarSheet){
        calendarSheet.setWindowContext(context);
        calendarSheet.title(title);
        calendarSheet.selectionMode(SelectionMode.DATE);
        calendarSheet.show();
    }
    public static String getAlphaColor(String hexColor, FragmentManager fragmentManager){
        double opacity = 40;
        if (!hexColor.startsWith("#") || (hexColor.length() != 7 && hexColor.length() != 9)) {
            new NotificationDialog("Invalid hex color format. Expected format: #RRGGBB or #AARRGGBB.").show(fragmentManager,"Utilities");
            return "#7564DD17";
        }

        // Remove the '#' character
        String color = hexColor.substring(1);

        // Calculate the alpha value based on the opacity
        int alpha = (int) Math.round(opacity * 255);

        // Ensure alpha is within valid range
        if (alpha < 0) {
            alpha = 0;
        } else if (alpha > 255) {
            alpha = 255;
        }

        // Convert alpha value to a two-digit hex string
        String alphaHex = String.format("%02X", alpha);

        // If the input color is in the format #AARRGGBB, remove the existing alpha component
        if (color.length() == 8) {
            color = color.substring(2);
        }

        // Return the new color with alpha
        return "#" + alphaHex + color;
    }

    public static ArrayList<ServicesModel> getServices(FragmentManager fragmentManager){
        ArrayList<ServicesModel> services = new ArrayList<>();

        ServicesModel servicesModel1 = new ServicesModel("Connections", getAlphaColor("#7564DD17",fragmentManager),R.drawable.breeding_image);
        ServicesModel servicesModel2 = new ServicesModel("Grooming", getAlphaColor("#751738DD",fragmentManager),R.drawable.grooming_image);
        ServicesModel servicesModel3 = new ServicesModel("Veterinary", getAlphaColor("#FF00B8D4",fragmentManager),R.drawable.vet_image);
        ServicesModel servicesModel4 = new ServicesModel("Adoption", getAlphaColor("#FFDD2C00",fragmentManager),R.drawable.adoption_image);

        services.add(servicesModel1);
        services.add(servicesModel2);
        services.add(servicesModel3);
        services.add(servicesModel4);

        return  services;
    }

    public static ArrayList<Locations> getLocations(){
        ArrayList<Locations> locations = new ArrayList<>();

        Locations servicesModel1 = new Locations("Kuala Adoptees Trust", "-30.754575", "135.684603", R.drawable.adoption_centee, "65 788 866 689", "A holistic approach to handling our pets and companions." ,Arrays.asList("adoption", "euthanization"));
        Locations servicesModel2 = new Locations("Kyst Agency", "-29.083984","126.434820",R.drawable.vet_place, "65 456 765 876", "Come get all your vet services from us with no extra cost cause we believe in you and your cat" ,Arrays.asList("vet", "breeding"));
        Locations servicesModel3 = new Locations("Cat centre", "52.190772", "-1.944578",R.drawable.grooming_place, "65 765 768 876", "A cat has nine lives, but reputation runs once so come one , come all to your cats appearance makeover" , Arrays.asList("grooming", "breeding"));
        Locations servicesModel4 = new Locations("Jonas Place", "27.663242", "85.384201",R.drawable.clinic, "65 764 450 097", "Want a partner but the dating pool is not for you, come and get a companion who will be with you through thick n thin as you journey on." , Arrays.asList("adoption", "vet", "grooming"));

        locations.add(servicesModel1);
        locations.add(servicesModel2);
        locations.add(servicesModel3);
        locations.add(servicesModel4);

        return  locations;
    }
}
