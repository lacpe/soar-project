package ch.unil.doplab.recipe.domain;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.nio.file.attribute.UserPrincipal;
import java.util.*;

@ApplicationScoped
public class ApplicationState {
    // Maps that will hold all CRUD-able items.
    private Map<UUID, UserProfile> userProfiles;
    private Map<UUID, MealPlan> mealPlans;
    private Map<UUID, GroceryList> groceryLists;

    // Object to handle all interactions with the Spoonacular API.
    private APIHandler apiHandler;

    /* Bidirectional maps (from Google's Guava Collections API) to match users to meal plans, and meal
    plans to grocery lists, and vice versa. */
    private BiMap<UUID, UUID> usersMealPlans;
    private BiMap<UUID, UUID> mealPlansGroceryLists;

    // Map to match usernames to their UUID. This simplifies API requests concerning users.
    private Map<String, UUID> usernames;

    // To generate random users
    private Random RANDOM = new Random();

    private Lorem lorem = LoremIpsum.getInstance();

    // List of 1000 names
    private String[] names = {
            "Kairi Doyle",
            "Kashton Gilmore",
            "Chanel Reese",
            "Alijah Kelley",
            "Rosalie Stanton",
            "Zyair McGee",
            "Kayleigh Fischer",
            "Leonidas Barron",
            "Anya Gutierrez",
            "Luca Wilkerson",
            "Janiyah Freeman",
            "Jayce Marsh",
            "Adelina Jefferson",
            "Raylan Kelly",
            "Ruby Fernandez",
            "Bentley Lane",
            "Amy Paul",
            "Noel Velasquez",
            "Esme McBride",
            "Denver Ponce",
            "Aileen Hayes",
            "Legend Peralta",
            "Malayah Huerta",
            "Douglas Hamilton",
            "Mackenzie Brewer",
            "Cruz Golden",
            "Giuliana Shaffer",
            "Dexter Chavez",
            "Nevaeh Cordova",
            "Vicente Mendoza",
            "Cora Crawford",
            "Kevin Moran",
            "Celeste Bautista",
            "Raul Warner",
            "Wynter Adkins",
            "Kylo Benton",
            "Anais Bender",
            "Zavier Phillips",
            "Naomi Dalton",
            "Fletcher Peterson",
            "Caroline Brady",
            "Reed Pratt",
            "Ailani Walters",
            "Colson Schmitt",
            "Queen Pearson",
            "Gunner Suarez",
            "Jimena Russell",
            "Weston Parrish",
            "Tiana Potter",
            "Lucca Reeves",
            "Lana Valentine",
            "Demetrius Weber",
            "Alayah Bond",
            "Roger Benson",
            "Collins Schmitt",
            "Murphy Avila",
            "Amiyah Leonard",
            "Ricardo Branch",
            "Luisa Bradley",
            "Richard Conley",
            "Salem Cuevas",
            "Brecken Cline",
            "Lina Hanna",
            "Aydin Snyder",
            "Callie Christian",
            "Ledger Neal",
            "Talia Bruce",
            "Uriah Munoz",
            "Kehlani Ellison",
            "Kye Austin",
            "Alivia Kline",
            "Ramon Collins",
            "Kinsley Holland",
            "Brady Burns",
            "Emerson Watson",
            "Greyson Middleton",
            "Madalyn Holland",
            "Brady Zavala",
            "Liv McDonald",
            "Calvin Barnes",
            "Liliana Ray",
            "Arlo Collier",
            "Ivory Morales",
            "Aaron Flowers",
            "Ariya Bradshaw",
            "Emory Michael",
            "Aubriella Fuentes",
            "Bowen Kirk",
            "Ellis Saunders",
            "Kasen Crawford",
            "Aubree Schneider",
            "Raymond Wade",
            "Evie McCormick",
            "Jasiah Bauer",
            "Haley Bradley",
            "Richard Perez",
            "Eleanor Bond",
            "Roger Yates",
            "Charley Pineda",
            "Gerardo Ball",
            "Abby Friedman",
            "Darwin Newton",
            "Braelynn Austin",
            "Omar Combs",
            "Irene Reeves",
            "Clark Mejia",
            "Saylor Friedman",
            "Darwin Espinoza",
            "Lucille Giles",
            "Kole Shannon",
            "Harlee McKenzie",
            "Scott Shields",
            "Analia Kent",
            "Mekhi Moss",
            "Bianca Allison",
            "Dennis Higgins",
            "Leighton Rosario",
            "Jedidiah Pruitt",
            "Brylee Sullivan",
            "Evan Hart",
            "Gemma Molina",
            "Prince Watts",
            "Melissa Wall",
            "Issac Wagner",
            "Maeve Dunlap",
            "Aries Fuentes",
            "Madeleine Vega",
            "Aidan Perkins",
            "Sage Velazquez",
            "Drew Christensen",
            "Carmen Hutchinson",
            "Korbin Huff",
            "Karsyn Rasmussen",
            "Will Frank",
            "Dior Moore",
            "Levi Copeland",
            "Dayana Stanley",
            "Manuel Suarez",
            "Jimena Abbott",
            "Kohen Moran",
            "Celeste Reid",
            "Josue Jensen",
            "Jane Owens",
            "Adriel Melendez",
            "Bethany Tate",
            "Dalton Ayers",
            "Simone Guzman",
            "Jude Massey",
            "Clementine Gould",
            "Blaine Cantu",
            "Galilea Woodward",
            "Jeremias Moses",
            "Karter Moses",
            "Niklaus Franklin",
            "Angela Bradshaw",
            "Emory Lambert",
            "Nina Livingston",
            "Ambrose Tanner",
            "Harmoni Garza",
            "Judah McCarthy",
            "Kira Burch",
            "Gerald Everett",
            "Noah Erickson",
            "Johnny Whitney",
            "Madalynn Landry",
            "Jaxx Ramirez",
            "Grace Howell",
            "Bradley Berg",
            "Emmalyn Atkinson",
            "Duke Webb",
            "Ariella Avery",
            "Jakari Villegas",
            "Jessie Guerrero",
            "Bryce Schwartz",
            "Lilliana Perry",
            "Waylon Gray",
            "Sarah Carlson",
            "Paul Jaramillo",
            "Guadalupe Fitzpatrick",
            "Blaze Foley",
            "Zaylee Montoya",
            "Ford Allen",
            "Riley Maddox",
            "Lyric Spears",
            "Isabela Porter",
            "Rhett Cruz",
            "Claire Hernandez",
            "Mason McCormick",
            "Macie Barr",
            "Harley Kent",
            "Jazmine Francis",
            "Harvey Graham",
            "Alaia Gonzalez",
            "Ethan Larsen",
            "Xiomara Rowland",
            "Eliezer Watts",
            "Melissa Rhodes",
            "Titus Cervantes",
            "Aylin Freeman",
            "Jayce Atkins",
            "Mina Parker",
            "Caleb Bryan",
            "Meredith Reid",
            "Josue Marquez",
            "Milani Webster",
            "Shawn Lester",
            "Averi Reilly",
            "Alvaro Strong",
            "Margo Bentley",
            "Randy House",
            "Sariah Howell",
            "Bradley Gibson",
            "Eden Rivera",
            "Charles Velez",
            "Megan Blankenship",
            "Ernesto Stokes",
            "Miranda Matthews",
            "Preston McKee",
            "Kori Bryant",
            "Jonah Merritt",
            "Kaisley Knox",
            "Valentin Gilmore",
            "Chanel Russo",
            "Jamie Nunez",
            "Mya Ray",
            "Arlo Goodwin",
            "Shiloh Dunlap",
            "Aries Conner",
            "Alondra Montes",
            "Darren Charles",
            "Jenna McPherson",
            "Foster Dickson",
            "Emmalynn Hubbard",
            "Forrest Ryan",
            "Morgan McCall",
            "Kiaan Watkins",
            "Lola Rice",
            "Graham Klein",
            "Elianna Reyna",
            "Reginald Pugh",
            "Landry Leblanc",
            "Braden Buchanan",
            "Maryam Yoder",
            "Johan Simmons",
            "Reagan Abbott",
            "Kohen Monroe",
            "Carly Ortega",
            "Kobe Delacruz",
            "Celine Klein",
            "Marco McKenzie",
            "Briar Gonzales",
            "Brayden Villanueva",
            "Monroe Rios",
            "Israel Weiss",
            "Lennox Shields",
            "Devon Taylor",
            "Sofia Gillespie",
            "Forest Brown",
            "Charlotte Rojas",
            "Colin Palacios",
            "Bria Frost",
            "Dario Noble",
            "Hunter Shields",
            "Devon Ramirez",
            "Grace Schneider",
            "Raymond Crane",
            "Della Reyna",
            "Reginald Ballard",
            "Alejandra Buckley",
            "Aryan Orozco",
            "Renata Wilkins",
            "Yusuf Fitzpatrick",
            "Annabella Sawyer",
            "Jefferson Pham",
            "Raelyn Dennis",
            "Emanuel Armstrong",
            "Presley Hamilton",
            "Jason Reynolds",
            "Isabelle Glenn",
            "Zaid McDaniel",
            "Dahlia Donovan",
            "Brayan Neal",
            "Talia Patterson",
            "Amir Hahn",
            "Fallon Hale",
            "Ezequiel Collins",
            "Kinsley Gilbert",
            "Tobias McKinney",
            "Gwendolyn Sullivan",
            "Evan Mitchell",
            "Willow Flowers",
            "Saul Solomon",
            "Mylah McDowell",
            "Lachlan Byrd",
            "Giselle Bates",
            "Ellis Rosas",
            "Joelle Burke",
            "Jax Reilly",
            "Tori Mack",
            "Esteban Morales",
            "Skylar Barry",
            "Emery Conley",
            "Salem Cook",
            "Ezekiel White",
            "Layla Chandler",
            "Royal Friedman",
            "Aspyn McLean",
            "Crosby Newman",
            "Oaklynn Harris",
            "Samuel Cortez",
            "Haven Avery",
            "Jakari Robles",
            "Felicity Humphrey",
            "Krew Miles",
            "Alessandra Griffin",
            "Ayden Mitchell",
            "Willow Reyes",
            "Eli Olson",
            "Isabel Poole",
            "Quincy Baker",
            "Isla Delacruz",
            "Memphis Berg",
            "Emmalyn Decker",
            "Taylor Villanueva",
            "Monroe Fox",
            "Antonio Bruce",
            "Marilyn Shepard",
            "Damari Moyer",
            "Zola Norris",
            "Cairo Walker",
            "Hazel Walsh",
            "Bodhi Kelley",
            "Rosalie Pierce",
            "Nicolas Benjamin",
            "Jianna Corona",
            "Darian Montes",
            "Roselyn Miller",
            "Benjamin Boone",
            "Mariam McCall",
            "Kiaan Butler",
            "Athena Cabrera",
            "Cade Middleton",
            "Madalyn Yoder",
            "Johan Lane",
            "Amy Townsend",
            "Alexis Owens",
            "Amaya Macias",
            "Moshe Gray",
            "Sarah Hull",
            "Salem Guzman",
            "Ashley Graham",
            "Giovanni Park",
            "Lia Lewis",
            "Wyatt Dickson",
            "Emmalynn Blair",
            "Troy Foley",
            "Zaylee Lynn",
            "Zechariah Jensen",
            "Jane Kelley",
            "Eric Kane",
            "Ellianna Finley",
            "Calum Shepard",
            "Noor Morales",
            "Aaron Kirby",
            "Skyla Humphrey",
            "Krew Smith",
            "Olivia Mills",
            "Alex Cruz",
            "Claire Galvan",
            "Kingsley Mosley",
            "Zaniyah Paul",
            "Noel Doyle",
            "Annalise Proctor",
            "Vance Kaur",
            "Holland Osborne",
            "Augustus Campbell",
            "Addison Browning",
            "Rohan Salazar",
            "Freya Crane",
            "Fox Carrillo",
            "Kaylani Michael",
            "Bronson Allen",
            "Riley Everett",
            "Camilo James",
            "Quinn Wolf",
            "Jase McConnell",
            "Denise Garza",
            "Judah Compton",
            "Elina Newman",
            "Anderson Fitzpatrick",
            "Annabella Perez",
            "Owen Weaver",
            "Teagan Thompson",
            "Theodore Lewis",
            "Ellie Neal",
            "Kane Black",
            "Molly Marshall",
            "Kaiden Christensen",
            "Carmen Aguirre",
            "Andy Sampson",
            "Meilani Valdez",
            "Kyler Stone",
            "Catalina Copeland",
            "Axton Peralta",
            "Malayah Green",
            "Anthony Steele",
            "Rylie Anderson",
            "Jacob Waller",
            "Whitley Larson",
            "Rafael Bowman",
            "Fiona Villa",
            "Clay Stephenson",
            "Khaleesi Rich",
            "Miller Espinosa",
            "Braylee Arnold",
            "Abraham Chambers",
            "Makayla Wiggins",
            "Azariah Velazquez",
            "Jaliyah Beil",
            "Ariel Avery",
            "Meghan Farrell",
            "Ty Turner",
            "Brooklyn Underwood",
            "Reece Roberson",
            "Sasha Decker",
            "Taylor Rogers",
            "Madelyn Delarosa",
            "Osiris Blackwell",
            "Saoirse Davidson",
            "Dante Russell",
            "Raelynn Dickson",
            "Maxton Beasley",
            "Jaylah Khan",
            "Kendrick Morrow",
            "Reyna Underwood",
            "Reece Walsh",
            "Leia Diaz",
            "Nathan Clayton",
            "Saige Rosales",
            "Wilder Young",
            "Zoey Roman",
            "Kian Gill",
            "Jordan Ventura",
            "Branson Gonzales",
            "Hadley Osborne",
            "Augustus Walsh",
            "Leia Fox",
            "Antonio Miranda",
            "Amina Houston",
            "Sylas Wiley",
            "Lauryn Levy",
            "Harold Logan",
            "Kora Pugh",
            "Judson Poole",
            "Bonnie Merritt",
            "Colten Kramer",
            "Hanna Austin",
            "Omar Hunter",
            "Khloe Barnett",
            "Stephen Allen",
            "Riley Villegas",
            "Clyde David",
            "Haylee Zavala",
            "Dillon Sweeney",
            "Yara Costa",
            "Kenji Tyler",
            "Helena McFarland",
            "Dane Larson",
            "Alayna Montgomery",
            "Maximiliano Wood",
            "Natalia Palacios",
            "Thaddeus Nolan",
            "Itzayana Estrada",
            "Phoenix Owen",
            "Mikayla Roth",
            "Roy Lamb",
            "Amaia Becker",
            "Lawson Benjamin",
            "Jianna Wu",
            "Kyson Green",
            "Zoe Brandt",
            "Damir Salazar",
            "Freya Bautista",
            "Raul O’brien",
            "Joanna Reyna",
            "Reginald Roman",
            "Astrid Maxwell",
            "Eden Hahn",
            "Fallon Wilkins",
            "Yusuf Snow",
            "Alexia Pacheco",
            "Erik Archer",
            "Kadence Yates",
            "Braylon Ochoa",
            "Luciana Baldwin",
            "Jaiden Fields",
            "Annie Glass",
            "Allan Barron",
            "Anya Morrison",
            "Maximus Petersen",
            "Fernanda Christian",
            "Ledger O’Donnell",
            "Bellamy Bass",
            "Landen Gomez",
            "Natalie Carrillo",
            "Wade Rios",
            "Brooke Lucero",
            "Felipe Davila",
            "Rayne Garcia",
            "James Cantu",
            "Galilea Roth",
            "Roy Hoover",
            "Virginia Donaldson",
            "Canaan Beck",
            "Gia Arroyo",
            "Alberto Rocha",
            "Emmie Rosas",
            "Remi Blanchard",
            "Layne Wilcox",
            "Jerry Durham",
            "Tiffany Hail",
            "Hector Dickerson",
            "Opal Tran",
            "Braxton Huffman",
            "Hayley Strickland",
            "Keegan Kemp",
            "Anika Nunez",
            "Caden Frye",
            "Raya Parker",
            "Caleb Fischer",
            "Maci Liu",
            "Pedro Schroeder",
            "Cameron Duffy",
            "Kyng Mann",
            "Paislee Cunningham",
            "Alejandro Vance",
            "Maxine Jennings",
            "Corbin Hicks",
            "Alina Romero",
            "Bryson Bullock",
            "Winnie Richmond",
            "Mordechai Durham",
            "Tiffany Aguirre",
            "Andy Yang",
            "Angelina Mata",
            "Ray Correa",
            "Valery McCormick",
            "Jasiah Pugh",
            "Landry Colon",
            "Bruce Felix",
            "Paisleigh Gallagher",
            "Marcos Everett",
            "Noah Quintana",
            "Kelvin Avalos",
            "Paloma Sharp",
            "Royce Villegas",
            "Jessie Baldwin",
            "Jaiden Hale",
            "Brinley Farmer",
            "Jamison Hoffman",
            "Aspen O’Neal",
            "Eddie Cook",
            "Aaliyah Lim",
            "Cal Wilson",
            "Luna Rosales",
            "Wilder Wise",
            "Mira Cervantes",
            "Kamari Baxter",
            "Lara Rice",
            "Graham Hess",
            "Kaliyah Brock",
            "Julio Hammond",
            "Holly Durham",
            "Kellen Davenport",
            "Adrianna Charles",
            "Conrad Maxwell",
            "Kyla Trujillo",
            "Apollo Ferguson",
            "Juliana Marquez",
            "Malakai Cole",
            "Margaret Houston",
            "Sylas Curtis",
            "Alexis Navarro",
            "Reid Wolfe",
            "Hallie Burke",
            "Jax Walls",
            "Lilianna Green",
            "Anthony Lucas",
            "Phoenix Baxter",
            "Tomas Ashley",
            "Khalani Norton",
            "Callen Bowman",
            "Fiona Davis",
            "Lucas Campos",
            "Sutton Patel",
            "Parker Quintana",
            "Kenia Knight",
            "Beckett Costa",
            "Robin Matthews",
            "Preston Valdez",
            "Diana Santana",
            "Mohamed Rangel",
            "Gloria Gonzalez",
            "Ethan Byrd",
            "Giselle Contreras",
            "Emilio Calderon",
            "Serena Dean",
            "Ronan Walton",
            "Scarlet Branch",
            "Keenan Bowers",
            "Elisa Frye",
            "Franco Bryan",
            "Meredith Chen",
            "Emmanuel Vaughan",
            "Nancy Jacobs",
            "Bryan Weeks",
            "Karen Tran",
            "Braxton Richard",
            "Davina Bernard",
            "Jair Garrett",
            "Tessa McGuire",
            "Casey Bowers",
            "Elisa Zimmerman",
            "Sergio O’Connor",
            "Charli Tapia",
            "Samir Calderon",
            "Serena May",
            "Finley Gilbert",
            "Jocelyn Gallegos",
            "Jonas Alvarez",
            "Leilani Vincent",
            "Aarav Howe",
            "Persephone Castro",
            "Jasper Calhoun",
            "Thalia Kane",
            "Brock Walters",
            "Samara Arnold",
            "Abraham Parrish",
            "Tiana Reeves",
            "Clark Lane",
            "Amy Harper",
            "Hayes Waller",
            "Whitley Parrish",
            "Karsyn York",
            "Milan Alexander",
            "Kingston Allen",
            "Riley Norman",
            "Aziel Kirk",
            "Ellis Boyd",
            "Dean Maynard",
            "Carolyn Tang",
            "Rogelio Underwood",
            "Ensley Bonilla",
            "Aden Jimenez",
            "Adeline Villa",
            "Clay Wyatt",
            "Liberty Bass",
            "Landen Sloan",
            "Selene Mejia",
            "Atticus Holland",
            "Mariah Solomon",
            "Musa Rosales",
            "Kinley Jefferson",
            "Raylan Kim",
            "Gabriella Camacho",
            "Tatum Buck",
            "Livia Young",
            "Asher Hoover",
            "Virginia Mathis",
            "Gustavo Shelton",
            "Makenzie McLean",
            "Crosby McClain",
            "Marleigh Moran",
            "Tate Molina",
            "Alexandria Blevins",
            "Avi Nielsen",
            "Vienna McIntyre",
            "Eliseo Proctor",
            "Chandler Rasmussen",
            "Will Reilly",
            "Tori Cain",
            "Benson Thornton",
            "Haisley Reeves",
            "Clark Roach",
            "Lyanna Watson",
            "Greyson Perez",
            "Eleanor Erickson",
            "Johnny Vazquez",
            "Journee Esparza",
            "Carl Goodwin",
            "Shiloh Cox",
            "Connor Doyle",
            "Annalise Summers",
            "Darius Waller",
            "Whitley O’Donnell",
            "Lian Thomas",
            "Elizabeth Hines",
            "Uriel Cole",
            "Margaret Perry",
            "Waylon Garcia",
            "Amelia Foley",
            "Mohammad Larsen",
            "Xiomara Myers",
            "Adam Sullivan",
            "Melanie Robbins",
            "Finnegan Lara",
            "Heidi Banks",
            "Martin Duke",
            "Melani Velazquez",
            "Drew Tang",
            "Belle Frederick",
            "Kase Dillon",
            "Laurel Kelly",
            "Cooper Wang",
            "Kailani Adkins",
            "Kylo Pierce",
            "Arabella Roman",
            "Kian Faulkner",
            "Ansley Nunez",
            "Caden Warner",
            "Wynter Hines",
            "Uriel Taylor",
            "Sofia Lin",
            "Conor Munoz",
            "Kehlani Esparza",
            "Carl Hughes",
            "Samantha Li",
            "Jorge Evans",
            "Eliana Harding",
            "Brodie Jimenez",
            "Adeline Scott",
            "Leo Dalton",
            "Lilian McCarty",
            "Blaise Mendez",
            "Londyn Ventura",
            "Branson Floyd",
            "Yaretzi Cole",
            "Nathaniel York",
            "Milan Higgins",
            "Sterling Pennington",
            "Yareli Leblanc",
            "Braden McMahon",
            "Belen Simmons",
            "Harrison Brennan",
            "Elodie Mata",
            "Ray Webster",
            "Kensley Knight",
            "Beckett Spencer",
            "Alyssa Simmons",
            "Harrison Adams",
            "Stella Little",
            "Lennox McFarland",
            "Annika Grimes",
            "Harlan Maxwell",
            "Kyla Crosby",
            "Tristen Cortes",
            "Lea Marks",
            "Amos Huff",
            "Karsyn Cobb",
            "Raphael Cline",
            "Lina Castaneda",
            "Collin Morales",
            "Skylar Le",
            "Damien Randolph",
            "Kailey Stein",
            "Creed Hutchinson",
            "Jamie Houston",
            "Sylas Allison",
            "Chelsea Middleton",
            "Misael Medina",
            "Elliana Fry",
            "Jacoby Huber",
            "Raquel Beltran",
            "Ricky Huerta",
            "Dulce Gonzales",
            "Brayden Hayes",
            "Iris Khan",
            "Kendrick Ellison",
            "Raina Brock",
            "Julio Santos",
            "Alana Leonard",
            "Ricardo Foster",
            "Brielle Allison",
            "Dennis Fry",
            "Clarissa Reid",
            "Josue Cunningham",
            "Marley Maddox",
            "Lyric Dennis",
            "Maisie McLaughlin",
            "Ibrahim McConnell",
            "Denise Day",
            "Kayson Jones",
            "Sophia Freeman",
            "Jayce Vaughan",
            "Nancy Fields",
            "Clayton Rivas",
            "Averie Reed",
            "Easton Ryan",
            "Morgan Peralta",
            "Dangelo Ayala",
            "Blair Reed",
            "Easton Kirby",
            "Skyla Castillo",
            "Kai Rhodes",
            "Tatum Martin",
            "Mateo Gross",
            "Angel Holt",
            "Niko Mosley",
            "Zaniyah Allison",
            "Dennis Hoover",
            "Virginia Fleming",
            "Fernando Watkins",
            "Lola Reese",
            "Alijah Fisher",
            "Arya Fernandez",
            "Bentley McKee",
            "Kori Hoffman",
            "Steven Christian",
            "Anahi Meyers",
            "Julien Boyle",
            "Aliya Rich",
            "Miller Marin",
            "Celia Ramos",
            "Angel Archer",
            "Kadence Patel",
            "Parker Torres",
            "Violet Mann",
            "Nehemiah Munoz",
            "Kehlani Flynn",
            "Kannon Brooks",
            "Autumn Miranda",
            "Rory Santos",
            "Alana Yang",
            "Malcolm Bridges",
            "Elora Norman",
            "Aziel Lam",
            "Karina Price",
            "Brooks Chandler",
            "Viviana Wiley",
            "Mathew Lynn",
            "Samira Baxter",
            "Tomas Stokes",
            "Miranda Hunter",
            "Archer Krueger",
            "Kamari Dudley",
            "Colter Guerrero",
            "Margot Nixon",
            "Cory Rivers",
            "Kiana Wilson",
            "Daniel Bernard",
            "Barbara McCarthy",
            "Devin Sampson",
            "Meilani Trevino",
            "Jaime Taylor",
            "Sofia Foley",
            "Mohammad Donovan",
            "Azariah Holland",
            "Brady Fitzpatrick",
            "Annabella Gallagher",
            "Marcos Juarez",
            "Juliet Blackburn",
            "Zahir Freeman",
            "Norah Ware",
            "Tadeo White",
            "Layla Strong",
            "Axl Phillips",
            "Naomi Bowers",
            "Dorian Johnston",
            "Laila Valentine",
            "Demetrius Haynes",
            "Lexi Castillo",
            "Kai Wiley",
            "Lauryn Gross",
            "Quinn McConnell",
            "Denise Edwards",
            "Adrian Garza",
            "River Cantu",
            "Anakin Morales",
            "Skylar Becker",
            "Lawson Benitez",
            "Aliza Hayden",
            "Leroy Stephens",
            "Millie Herring",
            "Henrik Morris",
            "Genesis Lowe",
            "Julius Frost",
            "Paula Atkinson",
            "Duke Benton",
            "Anais Morgan",
            "Hunter Schneider",
            "Delaney Fitzpatrick",
            "Blaze Phan",
            "Elsa Beil",
            "Ariel Callahan",
            "Kimber Marsh",
            "Bo Dunlap",
            "Iliana Hampton",
            "Hank Espinosa",
            "Braylee Hernandez",
            "Mason Rivers",
            "Kiana Sandoval",
            "Brantley Lawrence",
            "Lauren Yoder",
            "Johan Schultz",
            "Briella Oliver",
            "Karson Morales",
            "Skylar Hendrix",
            "Korbyn Strickland",
            "Nia Allen",
            "Carter Brandt",
            "Loretta Banks",
            "Martin Golden",
            "Giuliana Buckley",
            "Aryan Osborne",
            "Shelby Caldwell",
            "Rylan Vaughn",
            "Reign Munoz",
            "Justin Waller",
            "Whitley Johns",
            "Joziah Baldwin",
            "Esmeralda Bond",
            "Roger Moran",
            "Celeste Booth",
            "Chaim Bush",
            "Everlee Truong",
            "Ayan Williamson",
            "Catherine Beck",
            "Eduardo Bernard",
            "Barbara Leal",
            "Cedric Conner",
            "Alondra Schmitt",
            "Murphy Turner",
            "Brooklyn Sanders",
            "Jose Christian",
            "Anahi Stevenson",
            "Callan Gilbert",
            "Jocelyn Roberts",
            "Josiah Hogan",
            "Kathryn Giles",
            "Kole Espinosa",
            "Braylee Xiong",
            "Azrael McIntyre",
            "Rebekah Boyd",
            "Dean Humphrey",
            "Journi Peck",
            "Yousef Jimenez",
            "Adeline Dalton",
            "Fletcher Ho",
            "Calliope Dodson",
            "Seven Blanchard",
            "Layne Kaur",
            "Augustine Watkins",
            "Lola Hansen",
            "Charlie Thornton",
            "Haisley Benton",
            "Jamal Delarosa",
            "Iyla Harrell",
            "Nelson Buckley",
            "Theodora Arroyo",
            "Alberto Peralta",
            "Malayah Underwood",
            "Reece Phelps",
            "Laney Hines",
            "Uriel Cain",
            "Kendra Castro",
            "Jasper Whitney",
            "Madalynn Bowen",
            "Trevor Torres",
            "Violet McPherson",
            "Foster Riley",
            "Kayla Hodges",
            "Alonzo Friedman",
            "Aspyn Owens",
            "Adriel Davila",
            "Rayne Winters",
            "Deandre Wyatt",
            "Liberty Newton",
            "Santino Chen",
            "Valeria Reid",
            "Josue Ferguson",
            "Juliana Trejo",
            "Wesson Shannon",
            "Harlee Dixon",
            "Camden Harrell",
            "Kara Castaneda",
            "Collin Wilkerson",
            "Janiyah Kirby",
            "Tony Potter",
            "Rory Calhoun",
            "Gary Kemp",
            "Anika Jarvis",
            "Marlon Ruiz",
            "Emery Ho",
            "Morgan McKinney",
            "Gwendolyn Weaver",
            "Tucker Wyatt",
            "Liberty Gallagher",
            "Marcos Combs",
            "Irene Dejesus",
            "Rio Mullins"
};

    private String[] domains = {"e-mail",
            "example",
            "unil",
            "test"
    };

    private String[] countryCodes = {
            "ch",
            "org",
            "com"
    };

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void init() {
        userProfiles = new TreeMap<>();
        mealPlans = new TreeMap<>();
        groceryLists = new TreeMap<>();

        apiHandler = new APIHandler();

        usersMealPlans = HashBiMap.create();
        mealPlansGroceryLists = HashBiMap.create();

        usernames = new TreeMap<>();

        var allUserProfiles = findAllUserProfiles();
        for (UserProfile userProfile : allUserProfiles) {
            userProfiles.put(userProfile.getUserId(), userProfile);
            usernames.put(userProfile.getUsername(), userProfile.getUserId());
        }
        var allMealPlans = findAllMealPlans();
        for (MealPlan mealPlan : allMealPlans) {
            mealPlans.put(mealPlan.getMealPlanId(), mealPlan);
            usersMealPlans.put(mealPlan.getUserId(), mealPlan.getMealPlanId());
        }
        var allGroceryLists = findAllGroceryLists();
        for (GroceryList groceryList : allGroceryLists) {
            groceryLists.put(groceryList.getGroceryListId(), groceryList);
            mealPlansGroceryLists.put(groceryList.getMealPlanId(), groceryList.getGroceryListId());
        }
    }

    // Functions for UserProfileResource
    public UserProfile getUserProfile(UUID id) {return userProfiles.get(id);}

    public Map<UUID, UserProfile> getAllUserProfiles() {return userProfiles;}

    @Transactional
    public UserProfile addUserProfile(UserProfile userProfile) {
        if (userProfile.getUserId() != null) {
            return addUserProfile(userProfile.getUserId(), userProfile);
        }
        return addUserProfile(UUID.randomUUID(), userProfile);
    }

    @Transactional
    public UserProfile addUserProfile(UUID id, UserProfile userProfile) {
        if (userProfile.getUsername() == null) {
            throw new IllegalArgumentException("Username cannot be null.");
        }
        if (userProfiles.containsKey(id)) {
            throw new IllegalArgumentException("User with ID " + id + " already exists.");
        }
        if (usernames.containsKey(userProfile.getUsername())) {
            throw new IllegalArgumentException("Username " + userProfile.getUsername() + " already in use.");
        }
        userProfile.setUserId(id);
        userProfiles.put(id, userProfile);
        usernames.put(userProfile.getUsername(), id);
        em.persist(userProfile);
        return userProfile;
    }

    @Transactional
    public UserProfile setUserProfile(UUID id, UserProfile userProfile) {
        // This function changes the data to whatever userProfile is saved at UUID id to whatever data
        // is in userProfile
        UserProfile oldUserProfile = userProfiles.get(id);
        if (userProfile == null) {
            throw new IllegalArgumentException("User profile inputed is null.");
        }
        if (oldUserProfile == null) {
            throw new IllegalArgumentException("User profile does not currently exist, cannot be updated.");
        }
        // Translating the following condition : if the old & new user profiles have the same (non-null) username
        // but their UUIDs are different (i.e. you are trying to set the same username for two different users
        // then throw an exception
        if (oldUserProfile.getUsername().equals(userProfile.getUsername())
                && oldUserProfile.getUsername() != null
                && !usernames.get(userProfile.getUsername()).equals(id)) {
            throw new IllegalArgumentException("A user with username " + userProfile.getUsername() + " already exists.");
        }
        oldUserProfile.replaceWithUser(userProfile);
        em.merge(userProfile);
        return userProfiles.get(id);
    }

    @Transactional
    public void removeUserProfile(String username){
        removeUserProfile(usernames.get(username));
    }

    @Transactional
    public boolean removeUserProfile(UUID userId) {
        UserProfile userProfile = userProfiles.get(userId);
        UUID mealPlanId = usersMealPlans.get(userId);
        UUID groceryListId = mealPlansGroceryLists.get(mealPlanId);
        if (userProfile == null) {
            return false;
        }
        // Removes associated meal plan & grocery list (does nothing if there are none)
        if (groceryListId != null) {
            groceryLists.remove(groceryListId);
        }
        if (mealPlanId != null) {
            mealPlansGroceryLists.remove(mealPlanId);
            mealPlans.remove(mealPlanId);
        }
        usersMealPlans.remove(userId);
        // Removes user
        usernames.remove(userProfile.getUsername());
        userProfiles.remove(userId);
        if (!em.contains(userProfile)) {
            userProfile = em.merge(userProfile);
        }
        em.remove(userProfile);
        return true;
    }

    // Functions for MealPlanResource

    public MealPlan getMealPlan(UUID id) {
        return mealPlans.get(id);
    }

    public Map<UUID, MealPlan> getAllMealPlans() {
        return mealPlans;
    }

    @Transactional
    public MealPlan addMealPlan(MealPlan mealPlan) {
        if (mealPlan.getMealPlanId() != null) {
            return addMealPlan(mealPlan.getMealPlanId(), mealPlan);
        }
        return addMealPlan(UUID.randomUUID(), mealPlan);
    }

    @Transactional
    public MealPlan addMealPlan(UUID id, MealPlan mealPlan) {
        mealPlan.setMealPlanId(id);
        if (mealPlans.containsKey(id)) {
            throw new IllegalArgumentException("A meal plan with id " + id + " already exists.");
        }
        mealPlans.put(id, mealPlan);
        em.persist(mealPlan);
        return mealPlan;
    }

    @Transactional
    public MealPlan setMealPlan(UUID id, MealPlan mealPlan) {
        mealPlans.replace(id, mealPlan);
        em.merge(mealPlan);
        return mealPlans.get(id);
    }

    @Transactional
    public boolean removeMealPlan(UUID id) {
        MealPlan mealPlan = mealPlans.get(id);
        UUID groceryListId = mealPlansGroceryLists.get(id);
        if (mealPlan == null) {
            return false;
        }
        if (groceryListId != null) {
            // Removes associated grocery list, does nothing if there is none
            groceryLists.remove(groceryListId);
        }
        mealPlansGroceryLists.remove(id);
        // Removes meal plan
        mealPlans.remove(id);
        if (!em.contains(mealPlan)) {
            mealPlan = em.merge(mealPlan);
        }
        em.remove(mealPlan);
        return true;
    }

    // Functions for GroceryListResource
    public GroceryList getGroceryList(UUID id) {return groceryLists.get(id);}

    public Map<UUID, GroceryList> getAllGroceryLists() {return groceryLists;}

    @Transactional
    public GroceryList addGroceryList(GroceryList groceryList) {
        if (groceryList.getGroceryListId() != null) {
            return addGroceryList(groceryList.getGroceryListId(), groceryList);
        }
        return addGroceryList(UUID.randomUUID(), groceryList);
    }

    @Transactional
    public GroceryList addGroceryList(UUID id, GroceryList groceryList) {
        groceryList.setGroceryListId(id);
        if (groceryLists.containsKey(id)) {
            throw new IllegalArgumentException("A grocery list with id " + id + " already exists.");
        }
        groceryLists.put(id, groceryList);
        em.persist(groceryList);
        return groceryList;
    }

    public GroceryList setGroceryList(UUID id, GroceryList groceryList) {
        groceryLists.replace(id, groceryList);
        em.merge(groceryList);
        return groceryList;
    }

    @Transactional
    public boolean removeGroceryList(UUID id) {
        GroceryList groceryList = groceryLists.get(id);
        if (groceryList == null) {
            return false;
        }
        mealPlansGroceryLists.inverse().remove(id);
        groceryLists.remove(id);
        mealPlans.remove(id);
        if (!em.contains(groceryList)) {
            groceryList = em.merge(groceryList);
        }
        em.remove(groceryList);
        return true;
    }

    //Functions for ServiceResource
    @Transactional
    public MealPlan generateMealPlan(UUID userId) {
        UserProfile userProfile = userProfiles.get(userId);
        if (userProfile == null) {
            throw new IllegalArgumentException("No user profile with ID " + userId);
        }
        if (usersMealPlans.containsKey(userId)) {
            throw new IllegalArgumentException("Another meal plan for user " + userProfile.getUsername() + " already exists.");
        }
        MealPlan mealPlan = apiHandler.generateMealPlan(userProfile);
        mealPlan.setUserId(userId);
        addMealPlan(mealPlan);
        usersMealPlans.put(userId, mealPlan.getMealPlanId());
        return mealPlan;
    }

    @Transactional
    public GroceryList generateGroceryList(UUID userId) {
        MealPlan mealPlan = mealPlans.get(usersMealPlans.get(userId));
        if (mealPlan == null) {
            throw new IllegalArgumentException("User with ID " + userId + " does not have a registered meal plan.");
        }
        GroceryList groceryList = apiHandler.generateConsolidatedShoppingList(mealPlan.getAllMeals());
        groceryList.setMealPlanId(mealPlan.getMealPlanId());
        addGroceryList(groceryList);
        mealPlansGroceryLists.put(mealPlan.getMealPlanId(), groceryList.getGroceryListId());
        return groceryList;
    }

    public boolean checkUserMealPlan(UUID userId) {
        return usersMealPlans.get(userId) != null;
    }

    public boolean checkUserGroceryList(UUID userId) {
        boolean exists = false;
        if (usersMealPlans.containsKey(userId)) {
            exists = mealPlansGroceryLists.containsKey(usersMealPlans.get(userId));
        }
        else {
            exists = false;
        }
        return exists;
    }

    public MealPlan getUserMealPlan(UUID userId) {
        // Returns a given user's current meal plan
        try {
            return mealPlans.get(usersMealPlans.get(userId));
        } catch (NullPointerException e) {
            return null;
        }
    }

    public GroceryList getUserGroceryList(UUID userId) {
        // Looks up a user's current meal plan, and returns the corresponding grocery list
        try {
            return groceryLists.get(mealPlansGroceryLists.get(usersMealPlans.get(userId)));
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    public UUID authenticateUser(String username, String password) {
        if (!usernames.containsKey(username)) {
            throw new IllegalArgumentException("No user with name " + username + " exists.");
        }
        UUID userId = usernames.get(username);
        UserProfile userProfile = userProfiles.get(userId);
        if (Utils.checkPassword(password, userProfile.getPassword())) {
            return userId;
        }
        else {
            return null;
        }
    }

    public List<UserProfile> findAllUserProfiles() {
        return em.createQuery("SELECT u FROM UserProfile u", UserProfile.class).getResultList();
    }

    public List<MealPlan> findAllMealPlans() {
        return em.createQuery("SELECT m FROM MealPlan m", MealPlan.class).getResultList();
    }

    public List<GroceryList> findAllGroceryLists() {
        return em.createQuery("SELECT m FROM GroceryList m", GroceryList.class).getResultList();
    }

    public void clearObjects() {
        userProfiles.clear();
        mealPlans.clear();
        groceryLists.clear();
        usersMealPlans.clear();
        mealPlansGroceryLists.clear();
        usernames.clear();
    }

    public void clearTables() {
        // Disable foreign key checks
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

        // Get all table names in the studybuddy schema
        List<String> tables = (List<String>) em
                .createNativeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = 'RecipE'")
                .getResultList();

        // Avoid removing the sequence table
        tables.remove("SEQUENCE");

        // Truncate each table
        for (String table : tables) {
            em.createNativeQuery("TRUNCATE TABLE " + table).executeUpdate();
        }

        // Re-enable foreign key checks
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }

    @Transactional
    public void clearDb() {
        clearTables();
        clearObjects();
    }


    @Transactional
    public void populateDb() {
        for (UserProfile user : userProfiles.values()) {
            em.persist(user);
        }
    }


    public Meal dummyMeal() {
        Ingredient ing1 = new Ingredient(lorem.getWords(1), RANDOM.nextDouble(), "grams");
        Ingredient ing2 = new Ingredient(lorem.getWords(1), RANDOM.nextDouble(), "ml");
        NutritionalInfo nutritionalInfo = new NutritionalInfo();
        nutritionalInfo.setCalories(600 + RANDOM.nextInt(100));
        nutritionalInfo.setIron(RANDOM.nextDouble());
        nutritionalInfo.setVitaminC(RANDOM.nextDouble());
        String instructions1 = lorem.getWords(10, 15);
        String instructions2 = lorem.getWords(10, 15);
        Meal meal = new Meal(RANDOM.nextInt(10), lorem.getWords(1), "", nutritionalInfo);
        meal.setIngredients(List.of(ing1, ing2));
        meal.setInstructions(List.of(instructions1, instructions2));
        return meal;
    }

    public DailyMealSet dummyDailyMealSet() {
        return new DailyMealSet(List.of(dummyMeal(), dummyMeal(), dummyMeal()));
    }

    public MealPlan dummyMealPlan() {
        Map<String, DailyMealSet> dailyMealSets = new HashMap<>();
        dailyMealSets.put("Monday", dummyDailyMealSet());
        return new MealPlan(dailyMealSets);
    }

    public GroceryList dummyGroceryList(MealPlan mealPlan) {
        Map<String, Aisle> aisles = new HashMap<>();
        for (Meal meal : mealPlan.getAllMeals()) {
            Aisle aisle = new Aisle(meal.getIngredients());
            aisles.put("Meal 1", aisle);
        }
        return new GroceryList(aisles);
    }

    @Transactional
    public void populateApplication() {
        // Creates 1000 fake user profiles
        for (String name : names) {
            UserProfile userProfile = new UserProfile();
            userProfile.setUsername(name.split(" ")[0] + "." + name.split(" ")[1] + "@" + domains[RANDOM.nextInt(domains.length)] + "." + countryCodes[RANDOM.nextInt(countryCodes.length)]);
            userProfile.setPassword(name.replace(" ", "").toLowerCase() + (1950 + RANDOM.nextInt(55)));
            userProfile.setDietType(Utils.getRandomDietType());
            userProfile.setDislikedIngredients(Set.of(""));
            userProfile.setAllergies(Set.of(""));
            userProfile.setDesiredServings(1);
            userProfile.setMealPlanPreference(UserProfile.MealPlanPreference.DAY);
            userProfile.setDailyCalorieTarget(1750 + RANDOM.nextInt(500));
            userProfile.setUserId(UUID.randomUUID());
            addUserProfile(userProfile);
            MealPlan mealPlan = dummyMealPlan();
            mealPlan.setUserId(userProfile.getUserId());
            addMealPlan(mealPlan);
            GroceryList groceryList = dummyGroceryList(mealPlan);
            groceryList.setMealPlanId(mealPlan.getMealPlanId());
            addGroceryList(groceryList);
        }
    }


}
