package by.zserifan.SETHL;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

import java.io.*;
import java.util.HashMap;

public class HungerLevels extends JavaPlugin implements Listener, Runnable {

    private HashMap<String, Integer> foodMap = new HashMap<String, Integer>(); // maps food name to it's "feed value"
    private String dataLocation = "HungerLevels.dat"; // this is where we store our data (currently the folder with the jar)
    private int saveInterval = 60; // in seconds

    private String string_hungerlevels_save = "&9[HungerLevels] Data saved!";
    private String string_hungerlevels_usage = "&9Usage: /setfl <number>";
    private String string_hungerlevels_remove_usage = "&9Usage: /removefl <nume mancare>";
    private String string_hungerlevels_not_in_db = "&9Acea mancare nu este in baza de date!";
    private String string_hungerlevels_set_hunger_succesful = "&9Execution succesful!";
    private String string_hungerlevels_removal_succesful = "&9S-a eliminat cu succes aceasta mancare din baza de date!";

    public void onEnable() {
        File file = new File(dataLocation);
        boolean exists = file.exists(); // check if data is stored from previous session
        if(exists) {
            this.loadData(); // if exists load it
        }
        else {
            this.foodMap = new HashMap<>();
        }

        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this, saveInterval * 20, saveInterval * 20);
    } // on enable

    public void onDisable() {

    }

    @Override
    public void run() {
        try {
            this.saveData(); // save our data so we can load it the next time we start the server
        } catch(Exception e) {
            System.out.println("Clown fiesta");
        }
    } // run

    @EventHandler
    public void onFoodEaten(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if(item.getType() == Material.AIR) {
            event.setCancelled(true);
            return;
        }
        String displayName = item.getItemMeta().getDisplayName();
        if(displayName == null || displayName.equalsIgnoreCase("null")) {
            displayName = item.getType().toString();
        }

        if(!foodMap.containsKey(displayName)) return;
        Integer foodValue = foodMap.get(displayName);

        int originalHungerLevel;
        Material foodType = item.getType();
        if(foodType == Material.APPLE                 ) originalHungerLevel = 4; // apple
        else if(foodType == Material.BAKED_POTATO     ) originalHungerLevel = 5; // baked potato
        else if(foodType == Material.BEETROOT         )    originalHungerLevel = 1; // beetrot
        else if(foodType == Material.BEETROOT_SOUP    ) originalHungerLevel = 6; // beetrot soup
        else if(foodType == Material.BREAD            ) originalHungerLevel = 5; // bread
        else if(foodType == Material.CAKE             ) originalHungerLevel = 2;  // cake (slice)
        else if(foodType == Material.CAKE_BLOCK       ) originalHungerLevel = 14; // cake (whole)
        else if(foodType == Material.CARROT           ) originalHungerLevel = 3;  // carrot
        else if(foodType == Material.CHORUS_FRUIT     ) originalHungerLevel = 4;  // chorus fruit
        else if(foodType == Material.COOKED_CHICKEN   ) originalHungerLevel = 5; // cooked chicken
        else if(foodType == Material.COOKED_FISH      ) originalHungerLevel = 5; // cooked fish
        else if(foodType == Material.COOKED_MUTTON    ) originalHungerLevel = 6; // cooked mutton
        else if(foodType == Material.GRILLED_PORK     ) originalHungerLevel = 8; // cooked porkchop
        else if(foodType == Material.COOKED_RABBIT    ) originalHungerLevel = 5; // cooked rabbit
        else if(foodType == Material.COOKIE           ) originalHungerLevel = 2; // cookie
        else if(foodType == Material.GOLDEN_APPLE     ) originalHungerLevel = 4; // golden carrot
        else if(foodType == Material.CARROT           ) originalHungerLevel = 6; // golden carrot
        else if(foodType == Material.MELON            ) originalHungerLevel = 2;  // melon
        else if(foodType == Material.MUSHROOM_SOUP    ) originalHungerLevel = 6; // mushroom stew
        else if(foodType == Material.POISONOUS_POTATO ) originalHungerLevel = 2; // poisonous potato
        else if(foodType == Material.POTATO           ) originalHungerLevel = 1;
        else if(foodType == Material.PUMPKIN_PIE      ) originalHungerLevel = 8;
        else if(foodType == Material.RABBIT_STEW      ) originalHungerLevel = 10; // rabbit stew
        else if(foodType == Material.RAW_BEEF         ) originalHungerLevel = 3; // raw beef
        else if(foodType == Material.RAW_CHICKEN      ) originalHungerLevel = 2; // raw chicken
        else if(foodType == Material.RAW_FISH         ) originalHungerLevel = 2; // raw fish
        else if(foodType == Material.ROTTEN_FLESH     ) originalHungerLevel = 4; // rotten flesh
        else if(foodType == Material.SPIDER_EYE       ) originalHungerLevel = 2; // spider eye
        else if(foodType == Material.COOKED_BEEF      ) originalHungerLevel = 8;
        else originalHungerLevel = 2;

        int newFoodLevelGain = foodValue - originalHungerLevel;
        int foodLevel = event.getPlayer().getFoodLevel();
        event.getPlayer().setFoodLevel(foodLevel + newFoodLevelGain);
    } // on food eaten


    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player admin = event.getPlayer();
        if(admin.hasPermission("HungerLevels.setFoodLevel")) {
            if(event.getMessage().toUpperCase().startsWith("/SETFL ")) { // no setfl command executed
                String[] split = event.getMessage().split(" ");
                if(split.length != 2) {
                    admin.sendMessage(ChatColor.translateAlternateColorCodes('&', string_hungerlevels_usage));
                }
                String hungerGrantedString = split[1]; // get the number as string
                Integer hungerGranted = Integer.parseInt(hungerGrantedString); // convert the number string

                ItemStack item = admin.getItemInHand();
                String displayName = item.getItemMeta().getDisplayName();
                if(displayName == null || displayName.equalsIgnoreCase("null")) {
                    displayName = item.getType().toString();
                }

                if(foodMap.containsKey(displayName)) foodMap.remove(displayName);
                foodMap.put(displayName, hungerGranted);
                admin.sendMessage(ChatColor.translateAlternateColorCodes('&', string_hungerlevels_set_hunger_succesful));

                event.setCancelled(true);
                return;
            } // if setfl <number>

            /***** SAVE THE DATA ******/
            if(event.getMessage().equalsIgnoreCase("/savefl")) {
                try {
                    this.saveData();
                } catch(Exception e) {
                    // nothing to do
                }
                event.setCancelled(true);
                return;
            } // if savefl

            /****** SHOW THE PLAYER THE CURRENTLY MAPPED FOODS ******/
            if(event.getMessage().equalsIgnoreCase("/dumpfl")) {
                foodMap.keySet().stream().forEach(mappedFoodKey -> {
                    admin.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6" + mappedFoodKey) + ChatColor.translateAlternateColorCodes('&', "&6 => &9" + foodMap.get(mappedFoodKey).toString()));
                });
                event.setCancelled(true);
                return;
            }

            /****** DELETE SOME ENTRIES ***********/
            if(event.getMessage().equalsIgnoreCase("/removefl")) {
                ItemStack food = admin.getItemInHand();
                String display = food.getItemMeta().getDisplayName();
                if(foodMap.containsKey(display)) foodMap.remove(display);
                else admin.sendMessage(ChatColor.translateAlternateColorCodes('&', string_hungerlevels_not_in_db));

                event.setCancelled(true);
                return;
            }
            if(event.getMessage().toUpperCase().startsWith("/REMOVEFL ")) {
                String[] split = event.getMessage().split(" ");
                if(split.length != 2) {
                    admin.sendMessage(ChatColor.translateAlternateColorCodes('&', string_hungerlevels_remove_usage));
                }
                String food = split[0];
                if(foodMap.containsKey(food)) {
                    foodMap.remove(food);
                    admin.sendMessage(string_hungerlevels_removal_succesful);
                }
                else admin.sendMessage(ChatColor.translateAlternateColorCodes('&', string_hungerlevels_not_in_db));

                event.setCancelled(true);
                return;
            } // if remove fl
            if(event.getMessage().equalsIgnoreCase("/purgefldb")) {
                File file = new File(dataLocation);
                file.delete();
                this.foodMap.clear();
            }

        } // if has permission
    } // on command

    private void loadData() {
        try {
            FileInputStream fin = new FileInputStream(dataLocation);
            ObjectInputStream objectIn = new ObjectInputStream(fin);
            this.foodMap = (HashMap<String,Integer>) objectIn.readObject();
            objectIn.close();
        } catch(IOException e) {
            this.foodMap = new HashMap<>();
        } catch (ClassNotFoundException e) {
            System.out.println("[HungerLevels] THIS SHOULD'VE NOT HAPPENED");
        }
    }

    private void saveData() throws IOException {
        FileOutputStream fout = new FileOutputStream(dataLocation);
        ObjectOutputStream objectOut = new ObjectOutputStream(fout);
        objectOut.writeObject(this.foodMap);
        objectOut.flush();
        objectOut.close();

        System.out.println(string_hungerlevels_save);
    }

} // class