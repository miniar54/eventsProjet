package tn.esprit.eventsproject;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.eventsproject.entities.Event;
import tn.esprit.eventsproject.entities.Logistics;
import tn.esprit.eventsproject.entities.Participant;
import tn.esprit.eventsproject.entities.Tache;
import tn.esprit.eventsproject.repositories.EventRepository;
import tn.esprit.eventsproject.repositories.LogisticsRepository;
import tn.esprit.eventsproject.repositories.ParticipantRepository;
import tn.esprit.eventsproject.services.EventServicesImpl;

import java.time.LocalDate;
import java.util.*;

public class ServiceTest {

    @InjectMocks
    private EventServicesImpl eventServices;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private LogisticsRepository logisticsRepository;

    private Participant participant;
    private Event event;
    private Logistics logistics;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialisation des objets pour les tests
        participant = new Participant();
        participant.setIdPart(1);
        participant.setEvents(new HashSet<>());

        event = new Event();
        event.setDescription("Test Event");
        event.setParticipants(new HashSet<>());

        logistics = new Logistics();
        logistics.setReserve(true);
        logistics.setPrixUnit(100);
        logistics.setQuantite(2);
    }

    @Test
    public void testAddParticipant() {
        when(participantRepository.save(participant)).thenReturn(participant);

        Participant result = eventServices.addParticipant(participant);

        assertEquals(participant, result);
        verify(participantRepository).save(participant);
    }

    @Test
    public void testAddAffectEvenParticipantWithId() {
        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));
        when(eventRepository.save(event)).thenReturn(event);

        Event result = eventServices.addAffectEvenParticipant(event, 1);

        assertEquals(event, result);
        assertTrue(participant.getEvents().contains(event));
        verify(eventRepository).save(event);
    }

    @Test
    public void testAddAffectEvenParticipantWithEvent() {
        participant.getEvents().add(event);
        event.getParticipants().add(participant);

        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));
        when(eventRepository.save(event)).thenReturn(event);

        Event result = eventServices.addAffectEvenParticipant(event);

        assertEquals(event, result);
        assertTrue(participant.getEvents().contains(event));
        verify(eventRepository).save(event);
    }

    @Test
    public void testAddAffectLog() {
        when(eventRepository.findByDescription("Test Event")).thenReturn(event);
        when(logisticsRepository.save(logistics)).thenReturn(logistics);

        Logistics result = eventServices.addAffectLog(logistics, "Test Event");

        assertEquals(logistics, result);
        assertTrue(event.getLogistics().contains(logistics));
        verify(eventRepository).save(event);
    }
    @Test
    public void testGetLogisticsDates() {
        // Configuration des mocks
        Set<Logistics> logisticsSet = new HashSet<>();
        logisticsSet.add(logistics);
        event.setLogistics(logisticsSet);

        // Utilisation d'Arrays.asList() pour la compatibilité avec les versions antérieures
        when(eventRepository.findByDateDebutBetween(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1)))
                .thenReturn(Arrays.asList(event));

        List<Logistics> result = eventServices.getLogisticsDates(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(logistics));
    }
    @Test
    public void testCalculCout() {
        // Configuration des mocks
        when(eventRepository.findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache("Tounsi", "Ahmed", Tache.ORGANISATEUR))
                .thenReturn(Arrays.asList(event)); // Utiliser Arrays.asList pour compatibilité

        // Ajout de la logistique à l'événement
        logistics.setReserve(true);  // Assurez-vous que la logistique est réservée
        logistics.setPrixUnit(100);
        logistics.setQuantite(2);

        // Utilisation de HashSet pour garantir la compatibilité
        Set<Logistics> logisticsSet = new HashSet<>();
        logisticsSet.add(logistics);
        event.setLogistics(logisticsSet);

        // Appel de la méthode
        eventServices.calculCout();

        // Vérification des interactions
        verify(eventRepository).save(event);

        // Vérifiez que le coût est bien calculé
        assertEquals(200, event.getCout());
    }

}