package com.android.studyspot.models;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

public class Address {


   public static final String[] NAMES = new String[]{
           "AAAS Community Extension Center",
           "Ackerman Rd, 650",
"Ackerman Rd, 660",
"Ackerman Rd, 690 - Shelter House",
"Ackerman Rd, 700",
"Adriatico's",
"Adventure Recreation Center",
"Aerospace Research Center",
"Agricultural Administration",
"Agricultural Engineering Building",
"Airport Administration",
"Airport Dr, 2740",
"Airport Operations",
"Animal House Kinnear Research Center",
"Animal Science Building",
"Archer House",
"Aronoff Laboratory",
"Arps Hall",
"Atwell Hall",
"Baker Hall",
"Baker Systems Engineering",
"Barrett House",
"Bevis Hall",
"Bill Davis Baseball Stadium",
"Biocontainment Laboratory",
"Biological Sciences Building",
"Biological Sciences Greenhouses",
"Biomedical Research Tower",
"Blackburn House",
"Blackwell Inn",
"Blankenship Hall",
"Bloch Cancer Survivors Plaza",
"Bolz Hall",
"Bowen House",
"Bradley Hall",
"Brain and Spine Hospital",
"Bricker Hall",
"Browning Amphitheater",
"Bruegger's Bagels",
"Buckeye Field",
"Buckeye Village A - Cuyahoga Ct, 600-626",
"Buckeye Village Administration",
"Buckeye Village Community Center",
"Buckeye Village Recreation Hall",
"Busch House",
"Caldwell Laboratory",
"Campbell Hall",
"Campus Shop",
"CampusParcView CampusParc ",
"Canfield Hall",
"CBEC",
"Celeste Laboratory of Chemistry",
"Center for Integrative Medicine",
"Center of Science and Industry - COSI",
"Central Service Building",
"Chadwick ArboretumView Chadwick Arboretum",
"Child Care Center",
"Cockins Hall",
"Coffey Rd Sports Center",
"Comprehensive Cancer Center",
"Converse Hall",
"Cryogenic Laboratory",
"Cunz Hall",
"Curl Hall",
"Davis Heart and Lung Research Institute",
"Davis Medical Research Center",
"Davis Tower",
"Denney Hall",
"Derby Hall",
"Doan Hall",
"Dodd Hall",
"Dodridge St, 250 W",
"Doric on Lane(2563)",
"Drackett Tower",
"Drake Performance and Event Center",
"Dreese Laboratories",
"Drinko Hall",
"Dulles Hall",
"Edison Joining Technology Center",
"Eighteenth Ave, 209 W",
"Eighteenth Avenue Library",
"Electroscience Laboratory",
"ElectroScience Laboratory Complex",
"Eleventh Ave, 235-243 W",
"Eleventh Ave, 33 W",
"Eleventh Ave, 45 W",
"Eleventh Ave, 53 W",
"Enarson Classroom Building",
"Evans Hall",
"Evans Laboratory",
"Faculty Club",
"Fawcett Center for Tomorrow",
"Fechko Alumnae Scholarship House",
"Fisher Commons",
"Fisher CommonsView Fisher Commons Google map",
"Fisher Hall",
"Flight Laboratory",
"Fontana Laboratories",
"Fred Beekman Park",
"French Field House",
"Fry Hall",
"Galbreath Equine Center",
"Gateway A",
"Gateway B",
"Gateway C",
"Gateway D",
"Gateway F - North",
"Gateway F - South",
"Gerlach Hall",
"German House",
"Goss Laboratory",
"Graves Hall",
"Hagerty Hall",
"Hale Hall",
"Halloran House",
"Hamilton Hall",
"Hanley Alumnae Scholarship House",
"Harding Hospital",
"Haverfield House",
"Hayes Hall",
"Heffner Wetland Research and Education",
"Herrick Dr, 393",
"Highland St, 1615",
"Hitchcock Hall",
"Hopkins Hall",
"Houck House",
"Houston House",
"Howlett Greenhouses",
"Howlett Hall",
"Hughes Hall",
"Ice Rink",
"Independence Hall",
"Institute for Behavioral Medicine Research",
"Internal Medicine and Pediatrics at Hilliard",
"James Cancer Hospital",
"Jameson Crane Sports Medicine Institute",
"Jennings Hall",
"Jesse Owens Memorial Stadium",
"Jesse Owens Recreation Center North",
"Jesse Owens Recreation Center South",
"Jesse Owens Tennis Center West",
"Jesse Owens West ParkView Jesse Owens West Park Google map",
"Jones Tower",
"Journalism Building",
"Kappa Kappa Gamma House",
"Kennedy Commons",
"Kenny Rd, 1900",
"Kenny Rd, 2006-2030",
"Kinnear Rd Center B",
"Kinnear Rd Center C",
"Kinnear Rd Center D",
"Kinnear Rd Center E",
"Kinnear Rd, 1100",
"Kinnear Rd, 1165",
"Kinnear Rd, 1212-1218",
"Kinnear Rd, 1224",
"Kinnear Rd, 1245-1255",
"Kinnear Rd, 1260",
"Kinnear Rd, 1275-1305",
"Kinnear Rd, 1315",
"Kinnear Rd, 760",
"Kinnear Rd, 930",
"Kinnear Rd, 960",
"Knight House",
"Knowlton Hall",
"Kottman Hall",
"Kuhn Honors and Scholars House",
"Kunz-Brundige Extension Building",
"Lane Ave, 121 W",
"Lane Ave, 127 W",
"Lane Ave, 1480 W",
"Laundry Building",
"Lawrence Tower",
"Lazenby Hall",
"Library Book Depository",
"Lincoln Tower",
"Lincoln Tower ParkView Lincoln Tower Park Google map",
"Longaberger Alumni House",
"Mack Hall",
"MacQuigg Laboratory",
"Maintenance Building",
"Mason Hall",
"Mathematics Building",
"Mathematics Tower",
"McCampbell Hall",
"McCorkle Aquatic Pavilion",
"McCracken Power Plant",
"McPherson Chemical Laboratory",
"Meiling Hall",
"Mendenhall Laboratory",
"Mendoza House",
"Mershon Auditorium",
"Mershon Center",
"Metro High School",
"Mirror LakeView Mirror Lake Google map",
"Morehouse Medical Plaza - Concourse",
"Morehouse Medical Plaza - Pavilion",
"Morehouse Medical Plaza - Tower",
"Morrill Tower",
"Morrison Tower",
"Mount Hall",
"Neil Ave, 1656-1660",
"Newman and Wolfrom Laboratory of Chemistry",
"Newton Hall",
"Nicklaus Museum",
"Nineteenth Ave, 140 W",
"North Recreation Center",
"North Star Rd, 2470",
"Northwood-High Building",
"Norton House",
"Norwich Flats",
"Nosker House",
"Ohio Stadium",
"Ohio Union",
"Ornamental Plant Germplasm Center",
"Orton Hall",
"OSU Center for Human Resource Research",
"Oxley Hall",
"Page Hall",
"Park-Stradley Hall",
"Parker Food Science and Technology",
"Parks Hall",
"Paterson Hall",
"Pennsylvania Place",
"Pfahl Hall",
"Phi Kappa Tau Fraternity",
"Physical Activity and Education Services - PAES",
"Physics Research Building",
"Plumb Hall",
"Pomerene Alumnae Scholarship House",
"Pomerene Hall",
"Postle Hall",
"Pressey Hall",
"Printing Facility",
"Prior Hall",
"Psychology Building",
"Radiation Dosimetry Calibration Facility",
"Ramseyer Hall",
"Raney House",
"Recreation and Physical Activity Center",
"Research Administration Building",
"Research Center",
"Residence on Tenth",
"Rhodes Hall",
"Riffe Building",
"Rightmire Hall",
"Riverwatch Tower",
"Ross Heart Hospital",
"Sandefur Wetland Pavilion",
"Satellite Communications Facility",
"Schoenbaum Family Center",
"Schoenbaum Hall",
"Schottenstein Center",
"Schumaker Complex",
"Science Village",
"Scott Hall",
"Scott House",
"Scott Laboratory",
"Sherman Studio Art Center",
"Siebert Hall",
"Sisson Hall",
"Smith Laboratory",
"Smith-Steeb Hall",
"Spielman Comprehensive Breast Center",
"Starling Loving Hall",
"State of Ohio Computer Center",
"Steelwood Athletic Training Facility",
"Stillman Hall",
"Stores and Receiving",
"Student Academic Services",
"Sullivant Hall",
"Taylor Tower",
"Telecommunications Network Center",
"The Oval",
"Thompson Library",
"Torres House",
"Townshend Hall",
"Transmitter - WOSU",
"Turfgrass Foundation",
"Twelfth Ave, 395 W",
"Tzagournis Medical Research Facility",
"University Hall",
"University Plaza HotelView",
"Urban Arts Space",
"Veterinary Medical Center",
"Veterinary Medicine Academic",
"Watts Hall",
"Weigel Hall",
"Wetland Bike Shelter",
"Wexner Center for the Arts",
"Wilce Student Health Center",
"William Hall Complex - Neil Building",
"William Hall Complex - Scholars House East",
"William Hall Complex - Scholars House West",
"William Hall Complex - Worthington Building",
"Wiseman Hall",
"Woody Hayes Athletic Center",
"Younkin Success Center",
"Zoology Research Laboratory",
"4-H Center",
};

   private String name;
   private String address;

//    public Address(@NonNull View itemView) throws Exception{
//        super(itemView);
//        if (Arrays.asList(NAMES).contains(name)){
//            this.name = name;
//            this.address = address;
//        }else{
//            throw new Exception("Location" + name + "is not a valid location on OSU campus");
//        }
//    }

    //methods to perform on the Address object
}