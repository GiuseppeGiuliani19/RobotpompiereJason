package example;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;
import jason.environment.grid.Location;

public class HouseEnv extends Environment {
    // Literals esistenti, ovvero le percezioni e gli stati presenti nell'ambiente creato
    public static final Literal reachedPerson = Literal.parseLiteral("at(bob,person)");
    public static final Literal reachedFire1 = Literal.parseLiteral("at(alice,fire1)");
    public static final Literal reachedFire2 = Literal.parseLiteral("at(alice,fire2)");
    public static final Literal reachedExtinguisher = Literal.parseLiteral("at(alice,extinguisher)");
    public static final Literal hasExtinguisher = Literal.parseLiteral("has(alice,extinguisher)");
    
    // Literals per il kit medico
    public static final Literal reachedKit = Literal.parseLiteral("at(bob,kit)");
    public static final Literal hasKit = Literal.parseLiteral("has(bob,kit)");
    
    // Literals per i fuochi periodici
    public static final Literal periodicFire1 = Literal.parseLiteral("periodic_fire(3,4)");
    public static final Literal periodicFire2 = Literal.parseLiteral("periodic_fire(4,3)");
    public static final Literal periodicFire3 = Literal.parseLiteral("periodic_fire(3,3)");
    
    //tengono traccia dello stato dell'ambiente
    private boolean aliceHasExtinguisher = false;
    private boolean bobHasKit = false;
    private boolean fire1Handled = false;
    private boolean fire2Handled = false;
    HouseModel model;
    
    public void init(String[] args) {
        System.out.println("Ambiente di sviluppo creato e inizializzato");
        model = new HouseModel();
        HouseView view = new HouseView(model);
        model.setView(view);
        updatePercepts();
    }
    //servono per le percezioni dei robot, nel momento in cui prendono un materiale si aggiorna la loro percezione dell'ambiente
    void updatePercepts() {
        //per evitare conflitti nel momento in cui cambio percezione o cambia uno stato nell'ambiente, vengono pulite le percezioni così
        //da evitare conflitti
        clearPercepts("bob");
        clearPercepts("alice");
        
        Location bobLoc = model.getAgPos(0);
        Location aliceLoc = model.getAgPos(1);
        
        System.out.println("Luogo in cui si trova bob: " + String.valueOf(bobLoc));
        System.out.println("Luogo in cui si trova Alice: " + String.valueOf(aliceLoc));
        
        // Percezioni base
        addPercept("alice", Literal.parseLiteral("pos(alice," + aliceLoc.x + "," + aliceLoc.y + ")"));
        addPercept("bob", Literal.parseLiteral("pos(bob," + bobLoc.x + "," + bobLoc.y + ")"));
        
        // Gestione estintore
        if (aliceLoc.equals(model.lExtinguisher)) {
            aliceHasExtinguisher = true;
            addPercept("alice", reachedExtinguisher);
            addPercept("alice", hasExtinguisher);
            System.out.println("Alice ha preso l'estintore.");
        }
        
        if (aliceHasExtinguisher) {
            addPercept("alice", hasExtinguisher);
        }
        
        // Gestione kit medico
        if (bobLoc.equals(model.lKit)) {
            bobHasKit = true;
            addPercept("bob", reachedKit);
            addPercept("bob", hasKit);
            System.out.println("Bob ha preso il kit di pronto soccorso.");
        }
        
        if (bobHasKit) {
            addPercept("bob", hasKit);
        }
        
        // Gestione fuochi statici
        if (aliceLoc.equals(model.lFire1) && aliceHasExtinguisher) {
            addPercept("alice", reachedFire1);
            System.out.println("Alice ha raggiunto il primo fuoco.");
        }
        
        if (aliceLoc.equals(model.lFire2) && aliceHasExtinguisher) {
            addPercept("alice", reachedFire2);
            System.out.println("Alice ha raggiunto il secondo fuoco.");
        }
        
        // Gestione fuochi periodici
        if (model.hasObject(HouseModel.FIRE_PERIODIC1, 3, 4)) {
            addPercept("alice", periodicFire1);
            addPercept("bob", periodicFire1);
            System.out.println("Fuoco attivo in (3,4)");
        }
        
        if (model.hasObject(HouseModel.FIRE_PERIODIC2, 4, 3)) {
            addPercept("alice", periodicFire2);
            addPercept("bob", periodicFire2);
            System.out.println("Fuoco attivo in (4,3)");
        }
        
        if (model.hasObject(HouseModel.FIRE_PERIODIC3, 3, 3)) {
            addPercept("alice", periodicFire3);
            addPercept("bob", periodicFire3);
            System.out.println("Fuoco attivo in (3,3)");
        }
        
        // Gestione percezioni di Bob per fuochi statici
        if (model.hasObject(HouseModel.FIRE1, bobLoc.x, bobLoc.y)) {
            addPercept("bob", Literal.parseLiteral("found_fire(" + model.lFire1.x + "," + model.lFire1.y + ")"));
        }
        
        if (model.hasObject(HouseModel.FIRE2, bobLoc.x, bobLoc.y)) {
            addPercept("bob", Literal.parseLiteral("found_fire(" + model.lFire2.x + "," + model.lFire2.y + ")"));
        }
        
        // Gestione comunicazione tra agenti
        addPercept("bob", Literal.parseLiteral("agent_position(alice," + aliceLoc.x + "," + aliceLoc.y + ")"));
        addPercept("alice", Literal.parseLiteral("agent_position(bob," + bobLoc.x + "," + bobLoc.y + ")"));
        
        if (bobLoc.equals(model.lPerson)) {
            addPercept("bob", reachedPerson);
            System.out.println("Bob ha raggiunto la persona.");
        }
    }
    
    public boolean executeAction(String ag, Structure action) {
        boolean result = false;
        
        if (action.getFunctor().equals("move_towards")) {
            Location dest = null;
            int agentId = ag.equals("bob") ? 0 : 1;
            String l = action.getTerm(0).toString();
            
            if (l.equals("person") && agentId == 0) {
                dest = model.lPerson;
            } else if (l.equals("fire1") && agentId == 1) {
                dest = model.lFire1;
            } else if (l.equals("fire2") && agentId == 1) {
                dest = model.lFire2;
            } else if (l.equals("extinguisher") && agentId == 1) {
                dest = model.lExtinguisher;
            } else if (l.equals("kit") && agentId == 0) {
                dest = model.lKit;
            } else if (l.startsWith("pos")) {
                try {
                    int x = Integer.parseInt(l.substring(4, 5));
                    int y = Integer.parseInt(l.substring(6, 7));
                    dest = new Location(x, y);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            if (dest != null) {
                result = model.moveTowards(dest, agentId);
            }
        }
        
        if (result) {
            updatePercepts();
        }
        
        return result;
    }
}












