package example;

import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;
import java.util.Timer;
import java.util.TimerTask;

public class HouseModel extends GridWorldModel {
    //serve ad indicare le variabili uniche tra loro grazie alla presenza di bit diversi, indicando che saranno prsenti in 
    // celle diverse
    public static final int GSize = 7;
    public static final int PERSON = 16;
    public static final int FIRE1 = 32;
    public static final int FIRE2 = 64;
    public static final int FIRE_PERIODIC1 = 128;
    public static final int FIRE_PERIODIC2 = 256;
    public static final int FIRE_PERIODIC3 = 512;
    public static final int EXTINGUISHER = 1024;
    public static final int KIT = 2048;

    // Posizioni statiche
    Location lPerson = new Location(6, 6);
    Location lFire1 = new Location(1, 2);
    Location lFire2 = new Location(4, 4);
    Location lExtinguisher = new Location(3, 1);
    Location lKit = new Location(4, 6);

    // Posizioni fuochi dinamici, il fuoco che divampa
    Location[] periodicFireLocations = {
        new Location(3, 4),
        new Location(4, 3),
        new Location(3, 3)
    };

    private Timer fireGenerationTimer;
    private int currentFireIndex = 0;
    boolean[] activePeriodicFires = new boolean[3];

    public HouseModel() {
        //griglia 7 x 7 con 2 agenti presenti
        super(7, 7, 2);
        
        // Posizioni iniziali
        setAgPos(0, 3, 3); // Bob
        setAgPos(1, 0, 0); // Alice
        
        // Aggiungi oggetti statici
        add(PERSON, lPerson);
        add(FIRE1, lFire1);
        add(FIRE2, lFire2);
        add(EXTINGUISHER, lExtinguisher);
        add(KIT, lKit); 

        // Inizia la generazione dei fuochi
        startFireGeneration();
    }

    private void startFireGeneration() {
        fireGenerationTimer = new Timer(true);
        fireGenerationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                generateNextFire();
            }
        }, 0, 2000); // Genera un nuovo fuoco ogni 2 secondi
    }
    // generano le fiamme in una posizione predefinita nella griglia
    private synchronized void generateNextFire() {
        if (currentFireIndex < periodicFireLocations.length) {
            Location fireLoc = periodicFireLocations[currentFireIndex];
            int fireType = getFireType(currentFireIndex);
            add(fireType, fireLoc);
            activePeriodicFires[currentFireIndex] = true;
            currentFireIndex++;
            
            if (view != null) {
                view.repaint();
            }
            System.out.println("Nuovo fuoco generato in posizione: " + fireLoc);
        }
    }

    private int getFireType(int index) {
        switch(index) {
            case 0: return FIRE_PERIODIC1;
            case 1: return FIRE_PERIODIC2;
            case 2: return FIRE_PERIODIC3;
            default: return 0;
        }
    }

    public boolean moveTowards(Location dest, int agentId) {
        Location r1 = getAgPos(agentId);
        
        if (r1.x < dest.x) r1.x++;
        else if (r1.x > dest.x) r1.x--;
        
        if (r1.y < dest.y) r1.y++;
        else if (r1.y > dest.y) r1.y--;

        setAgPos(agentId, r1);
        
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }

    public void cleanup() {
        if (fireGenerationTimer != null) {
            fireGenerationTimer.cancel();
            fireGenerationTimer.purge();
        }
    }
}


