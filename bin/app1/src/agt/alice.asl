//Goal
!start.

// Piano iniziale
@start[atomic]
+!start : true <- 
    .print("Inizia la missione di salvataggio");
    !get_extinguisher.

// Piano per recuperare l'estintore
@get_extinguisher[atomic]    
+!get_extinguisher : not has(alice,extinguisher) <-
    .print("In movimento verso l'estintore");
    move_towards(extinguisher);
    !get_extinguisher.

@has_extinguisher[atomic]    
+!get_extinguisher : has(alice,extinguisher) <-
    .print("Estintore acquisito");
    !handle_first_fire.

// Gestione ricezione posizione di Bob
+agent_position(bob, X, Y) : true <-
    .print("Posizione di Bob attuale: ", X, ", ", Y).

// Gestione del primo fuoco statico
@first_fire[atomic]
+!handle_first_fire : has(alice,extinguisher) & not at(alice,fire1) <-
    .print("In movimento verso il primo fuoco ");
    move_towards(fire1);
    !handle_first_fire.

@reach_first[atomic]
+!handle_first_fire : has(alice,extinguisher) & at(alice,fire1) <-
    .print("Raggiunto e spento il primo fuoco ");
    +first_fire_extinguished;  // Aggiungiamo questa belief
    !handle_periodic_fires.     // Nuovo nome del piano

// Gestione dei fuochi periodici
@handle_periodic[atomic]
+!handle_periodic_fires : has(alice,extinguisher) <-
    .print("Vedo se si trovano altre fiamme intorno... ");
    .wait(2000);
    .findall(pos(X,Y), periodic_fire(X,Y), Fires);
    .print("Inizio a spegnere i fuochi periodici");
    !visit_periodic_fire(Fires).

@visit_periodic[atomic]   
+!visit_periodic_fire([pos(X,Y)|T]) : has(alice,extinguisher) <-
    .print("Mi sposto verso il fuoco in (", X, ",", Y, ")");
    move_towards(pos(X,Y));
    .print("Fuoco spento in (", X, ",", Y, ")");
    if (not .empty(T)) {
        !visit_periodic_fire(T);
    } else {
        .print("Fuochi spenti");
        +mission_completed; // si aggiorna la percezione di Alice e capisce di aver completato la missione
    }.

// Gestione percezione di nuovi fuochi
+periodic_fire(X,Y) : has(alice,extinguisher) & not mission_completed <-
    .print("Nuovo fuoco rilevato in (", X, ",", Y, ")").

//Gestione segnalazioni da Bob
@handle_bob_fire[atomic]
+fire_found(X,Y)[source(bob)] : has(alice,extinguisher) <-
    .print("Bob mi ha segnalato un fuoco in posizione (", X, ", ", Y, ")");
    +discovered_fire(X,Y); //si aggiorna la percezione  di Alice
    .send(bob, tell, fire_confirmation(X,Y));
    !handle_discovered_fire(X,Y).

@handle_discovered[atomic]
+!handle_discovered_fire(X,Y) : has(alice,extinguisher) <-
    .print("Mi dirigo verso il fuoco segnalato da Bob in (", X, ", ", Y, ")");
    move_towards(pos(X,Y));
    .print("Fuoco segnalato da Bob spento in (", X, ", ", Y, ")");
    !handle_periodic_fires.





























