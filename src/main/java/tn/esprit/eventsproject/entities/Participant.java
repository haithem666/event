package tn.esprit.eventsproject.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.Set;


import javax.persistence.*;
import java.io.Serializable;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Participant implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int idPart;
    String nom;
    String prenom;
    @Enumerated(EnumType.STRING)
    Tache tache;
    @ManyToMany
    Set<Event> events;

}
