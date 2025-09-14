//Goal
!start. 

// Piano iniziale
+!start : true <- 
    !at(bob, person).

// Piano quando raggiunge la persona
+!at(bob, P) : at(bob, P) & P = person & not has(bob,kit) <- 
    .print("Ho raggiunto la persona, sta male! Vado a prendere il kit");
    !get_kit.

// Piano quando raggiunge la persona con il kit
+!at(bob, P) : at(bob, P) & P = person & has(bob,kit) <- 
    .print("Ho raggiunto la persona con il kit di pronto soccorso");
    .send(alice, tell, destination_reached(person));
    .send(alice, tell, where_are_you).

// Piano per movimento generico
+!at(bob, P) : not at(bob, P) <- 
    .print("Cerco la persona");
    move_towards(P);
    .print("Mi trovo dove è presente la persona da salvare");
    !at(bob,P).

// Piano per prendere il kit
+!get_kit : not has(bob,kit) <- 
    .print("Mi dirigo verso il kit di pronto soccorso");
    move_towards(kit);
    !get_kit.

+!get_kit : has(bob,kit) <- 
    .print("Ho preso il kit, torno dalla persona");
    !return_to_person.

// Piano per tornare dalla persona
+!return_to_person : has(bob,kit) <- 
    .print("Ritorno dalla persona con il kit");
    move_towards(person);
    !at(bob, person).

// Gestione posizione di Alice
+agent_position(alice, X, Y) : true <- 
    .print("Posizione di Alice attuale: ", X, ", ", Y).

// Gestione scoperta fuochi
+found_fire(X,Y) : true <- 
    .print("Ho trovato un fuoco in posizione: (", X, ", ", Y, ")");
    .send(alice, tell, fire_found(X,Y));
    .wait(fire_confirmation(X,Y)[source(alice)]);
    .print("Alice ha confermato di aver ricevuto la segnalazione del fuoco in (", X, ", ", Y, ")").

// Gestione conferma da Alice
+fire_confirmation(X,Y)[source(alice)] : true <-
    .print("Alice ha confermato che spegnerà il fuoco in (", X, ", ", Y, ")").
















     

     







