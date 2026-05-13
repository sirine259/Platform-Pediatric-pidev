package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.Repository.DonorRepository;
import com.esprit.platformepediatricback.Repository.RecipientRepository;
import com.esprit.platformepediatricback.Repository.TransplantRepository;
import com.esprit.platformepediatricback.entity.Donor;
import com.esprit.platformepediatricback.entity.Recipient;
import com.esprit.platformepediatricback.entity.Transplant;
import com.esprit.platformepediatricback.entity.Patient;
import com.esprit.platformepediatricback.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KidneyTransplantServiceTest {

    @Mock
    private DonorRepository donorRepository;

    @Mock
    private RecipientRepository recipientRepository;

    @Mock
    private TransplantRepository transplantRepository;

    @InjectMocks
    private KidneyTransplantService kidneyTransplantService;

    private Donor testDonor;
    private Recipient testRecipient;
    private Transplant testTransplant;
    private Patient testPatient;
    private User testSurgeon;

    @BeforeEach
    void setUp() {
        testSurgeon = new User();
        testSurgeon.setUsername("testdoctor");
        testSurgeon.setEmail("doctor@test.com");

        testPatient = new Patient();
        testPatient.setFirstName("John");
        testPatient.setLastName("Doe");

        testDonor = new Donor();
        testDonor.setFirstName("John");
        testDonor.setLastName("Doe");
        testDonor.setDateOfBirth(LocalDate.now().minusYears(30));
        testDonor.setBloodType("O+");
        testDonor.setPhoneNumber("123456789");
        testDonor.setEmail("donor@test.com");
        testDonor.setAddress("123 Test St");
        testDonor.setStatus(Donor.DonorStatus.PENDING);
        testDonor.setRegistrationDate(LocalDateTime.now());

        testRecipient = new Recipient();
        testRecipient.setPatient(testPatient);
        testRecipient.setDateOfBirth(LocalDate.now().minusYears(25));
        testRecipient.setBloodType("O+");
        testRecipient.setPhoneNumber("987654321");
        testRecipient.setEmail("recipient@test.com");
        testRecipient.setAddress("456 Test Ave");
        testRecipient.setStatus(Recipient.RecipientStatus.WAITING);
        testRecipient.setRegistrationDate(LocalDateTime.now());

        testTransplant = new Transplant();
        testTransplant.setDonor(testDonor);
        testTransplant.setRecipient(testRecipient);
        testTransplant.setSurgeon(testSurgeon);
        testTransplant.setStatus(Transplant.TransplantStatus.SCHEDULED);
        testTransplant.setCreatedAt(LocalDateTime.now());
        testTransplant.setUpdatedAt(LocalDateTime.now());
    }

    // ==================== Donor Tests ====================

    @Test
    void createDonor_ShouldSaveAndReturnDonor() {
        when(donorRepository.save(any(Donor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Donor result = kidneyTransplantService.createDonor(testDonor);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertNotNull(result.getRegistrationDate());
        verify(donorRepository, times(1)).save(any(Donor.class));
    }

    @Test
    void getDonorById_ShouldReturnDonor_WhenExists() {
        when(donorRepository.findById(1L)).thenReturn(Optional.of(testDonor));

        Optional<Donor> result = kidneyTransplantService.getDonorById(1L);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        verify(donorRepository, times(1)).findById(1L);
    }

    @Test
    void getDonorById_ShouldReturnEmpty_WhenNotExists() {
        when(donorRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Donor> result = kidneyTransplantService.getDonorById(999L);

        assertFalse(result.isPresent());
        verify(donorRepository, times(1)).findById(999L);
    }

    @Test
    void getAllDonors_ShouldReturnAllDonors() {
        List<Donor> donors = Arrays.asList(testDonor);
        when(donorRepository.findAll()).thenReturn(donors);

        List<Donor> result = kidneyTransplantService.getAllDonors();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(donorRepository, times(1)).findAll();
    }

    @Test
    void getDonorsByStatus_ShouldReturnDonorsWithStatus() {
        List<Donor> donors = Arrays.asList(testDonor);
        when(donorRepository.findByStatus(Donor.DonorStatus.PENDING)).thenReturn(donors);

        List<Donor> result = kidneyTransplantService.getDonorsByStatus(Donor.DonorStatus.PENDING);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(donorRepository, times(1)).findByStatus(Donor.DonorStatus.PENDING);
    }

    @Test
    void getDonorsByBloodType_ShouldReturnDonorsWithBloodType() {
        List<Donor> donors = Arrays.asList(testDonor);
        when(donorRepository.findByBloodType("O+")).thenReturn(donors);

        List<Donor> result = kidneyTransplantService.getDonorsByBloodType("O+");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(donorRepository, times(1)).findByBloodType("O+");
    }

    @Test
    void updateDonor_ShouldUpdateAndReturnDonor_WhenExists() {
        Donor updatedDetails = new Donor();
        updatedDetails.setFirstName("Jane");
        updatedDetails.setLastName("Smith");
        updatedDetails.setDateOfBirth(LocalDate.now().minusYears(35));
        updatedDetails.setBloodType("A+");
        updatedDetails.setPhoneNumber("111111111");
        updatedDetails.setEmail("jane@test.com");
        updatedDetails.setAddress("789 New St");
        updatedDetails.setStatus(Donor.DonorStatus.APPROVED);
        updatedDetails.setMedicalHistory("None");
        updatedDetails.setNotes("Updated notes");

        when(donorRepository.findById(1L)).thenReturn(Optional.of(testDonor));
        when(donorRepository.save(any(Donor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Donor result = kidneyTransplantService.updateDonor(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        verify(donorRepository, times(1)).findById(1L);
        verify(donorRepository, times(1)).save(any(Donor.class));
    }

    @Test
    void updateDonor_ShouldReturnNull_WhenNotExists() {
        Donor updatedDetails = new Donor();
        when(donorRepository.findById(999L)).thenReturn(Optional.empty());

        Donor result = kidneyTransplantService.updateDonor(999L, updatedDetails);

        assertNull(result);
        verify(donorRepository, times(1)).findById(999L);
        verify(donorRepository, never()).save(any(Donor.class));
    }

    @Test
    void deleteDonor_ShouldReturnTrue_WhenExists() {
        when(donorRepository.existsById(1L)).thenReturn(true);

        boolean result = kidneyTransplantService.deleteDonor(1L);

        assertTrue(result);
        verify(donorRepository, times(1)).existsById(1L);
        verify(donorRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteDonor_ShouldReturnFalse_WhenNotExists() {
        when(donorRepository.existsById(999L)).thenReturn(false);

        boolean result = kidneyTransplantService.deleteDonor(999L);

        assertFalse(result);
        verify(donorRepository, times(1)).existsById(999L);
        verify(donorRepository, never()).deleteById(any(Long.class));
    }

    @Test
    void searchDonorsByName_ShouldReturnMatchingDonors() {
        List<Donor> donors = Arrays.asList(testDonor);
        when(donorRepository.findByNameContaining("John", "Doe")).thenReturn(donors);

        List<Donor> result = kidneyTransplantService.searchDonorsByName("John", "Doe");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(donorRepository, times(1)).findByNameContaining("John", "Doe");
    }

    @Test
    void getApprovedDonors_ShouldReturnApprovedDonors() {
        List<Donor> donors = Arrays.asList(testDonor);
        when(donorRepository.findApprovedDonorsOrderByRegistrationDate()).thenReturn(donors);

        List<Donor> result = kidneyTransplantService.getApprovedDonors();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(donorRepository, times(1)).findApprovedDonorsOrderByRegistrationDate();
    }

    @Test
    void getDonorByEmail_ShouldReturnDonor() {
        when(donorRepository.findByEmail("donor@test.com")).thenReturn(testDonor);

        Donor result = kidneyTransplantService.getDonorByEmail("donor@test.com");

        assertNotNull(result);
        assertEquals("donor@test.com", result.getEmail());
        verify(donorRepository, times(1)).findByEmail("donor@test.com");
    }

    @Test
    void getDonorByPhoneNumber_ShouldReturnDonor() {
        when(donorRepository.findByPhoneNumber("123456789")).thenReturn(testDonor);

        Donor result = kidneyTransplantService.getDonorByPhoneNumber("123456789");

        assertNotNull(result);
        assertEquals("123456789", result.getPhoneNumber());
        verify(donorRepository, times(1)).findByPhoneNumber("123456789");
    }

    // ==================== Recipient Tests ====================

    @Test
    void createRecipient_ShouldSaveAndReturnRecipient() {
        when(recipientRepository.save(any(Recipient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recipient result = kidneyTransplantService.createRecipient(testRecipient);

        assertNotNull(result);
        assertNotNull(result.getRegistrationDate());
        verify(recipientRepository, times(1)).save(any(Recipient.class));
    }

    @Test
    void getRecipientById_ShouldReturnRecipient_WhenExists() {
        when(recipientRepository.findById(1L)).thenReturn(Optional.of(testRecipient));

        Optional<Recipient> result = kidneyTransplantService.getRecipientById(1L);

        assertTrue(result.isPresent());
        verify(recipientRepository, times(1)).findById(1L);
    }

    @Test
    void getRecipientById_ShouldReturnEmpty_WhenNotExists() {
        when(recipientRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Recipient> result = kidneyTransplantService.getRecipientById(999L);

        assertFalse(result.isPresent());
        verify(recipientRepository, times(1)).findById(999L);
    }

    @Test
    void getAllRecipients_ShouldReturnAllRecipients() {
        List<Recipient> recipients = Arrays.asList(testRecipient);
        when(recipientRepository.findAll()).thenReturn(recipients);

        List<Recipient> result = kidneyTransplantService.getAllRecipients();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(recipientRepository, times(1)).findAll();
    }

    @Test
    void getRecipientsByStatus_ShouldReturnRecipientsWithStatus() {
        List<Recipient> recipients = Arrays.asList(testRecipient);
        when(recipientRepository.findByStatus(Recipient.RecipientStatus.WAITING)).thenReturn(recipients);

        List<Recipient> result = kidneyTransplantService.getRecipientsByStatus(Recipient.RecipientStatus.WAITING);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(recipientRepository, times(1)).findByStatus(Recipient.RecipientStatus.WAITING);
    }

    @Test
    void getRecipientsByBloodType_ShouldReturnRecipientsWithBloodType() {
        List<Recipient> recipients = Arrays.asList(testRecipient);
        when(recipientRepository.findByBloodType("O+")).thenReturn(recipients);

        List<Recipient> result = kidneyTransplantService.getRecipientsByBloodType("O+");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(recipientRepository, times(1)).findByBloodType("O+");
    }

    @Test
    void getWaitingRecipients_ShouldReturnWaitingRecipients() {
        List<Recipient> recipients = Arrays.asList(testRecipient);
        when(recipientRepository.findWaitingRecipientsOrderByRegistrationDate()).thenReturn(recipients);

        List<Recipient> result = kidneyTransplantService.getWaitingRecipients();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(recipientRepository, times(1)).findWaitingRecipientsOrderByRegistrationDate();
    }

    @Test
    void updateRecipient_ShouldUpdateAndReturnRecipient_WhenExists() {
        Patient newPatient = new Patient();

        Recipient updatedDetails = new Recipient();
        updatedDetails.setPatient(newPatient);
        updatedDetails.setDateOfBirth(LocalDate.now().minusYears(30));
        updatedDetails.setBloodType("A+");
        updatedDetails.setPhoneNumber("999999999");
        updatedDetails.setEmail("updated@test.com");
        updatedDetails.setAddress("999 New Ave");
        updatedDetails.setStatus(Recipient.RecipientStatus.TRANSPLANTED);
        updatedDetails.setMedicalHistory("Updated history");
        updatedDetails.setNotes("Updated notes");
        updatedDetails.setTransplantDate(LocalDateTime.now());

        when(recipientRepository.findById(1L)).thenReturn(Optional.of(testRecipient));
        when(recipientRepository.save(any(Recipient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recipient result = kidneyTransplantService.updateRecipient(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("A+", result.getBloodType());
        assertEquals(Recipient.RecipientStatus.TRANSPLANTED, result.getStatus());
        verify(recipientRepository, times(1)).findById(1L);
        verify(recipientRepository, times(1)).save(any(Recipient.class));
    }

    @Test
    void updateRecipient_ShouldReturnNull_WhenNotExists() {
        Recipient updatedDetails = new Recipient();
        when(recipientRepository.findById(999L)).thenReturn(Optional.empty());

        Recipient result = kidneyTransplantService.updateRecipient(999L, updatedDetails);

        assertNull(result);
        verify(recipientRepository, times(1)).findById(999L);
        verify(recipientRepository, never()).save(any(Recipient.class));
    }

    @Test
    void deleteRecipient_ShouldReturnTrue_WhenExists() {
        when(recipientRepository.existsById(1L)).thenReturn(true);

        boolean result = kidneyTransplantService.deleteRecipient(1L);

        assertTrue(result);
        verify(recipientRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteRecipient_ShouldReturnFalse_WhenNotExists() {
        when(recipientRepository.existsById(999L)).thenReturn(false);

        boolean result = kidneyTransplantService.deleteRecipient(999L);

        assertFalse(result);
        verify(recipientRepository, never()).deleteById(any(Long.class));
    }

    @Test
    void searchRecipientsByName_ShouldReturnMatchingRecipients() {
        List<Recipient> recipients = Arrays.asList(testRecipient);
        when(recipientRepository.findByPatientNameContaining("John")).thenReturn(recipients);

        List<Recipient> result = kidneyTransplantService.searchRecipientsByName("John");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(recipientRepository, times(1)).findByPatientNameContaining("John");
    }

    // ==================== Transplant Tests ====================

    @Test
    void createTransplant_ShouldSaveAndReturnTransplant() {
        when(transplantRepository.save(any(Transplant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transplant result = kidneyTransplantService.createTransplant(testTransplant);

        assertNotNull(result);
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(transplantRepository, times(1)).save(any(Transplant.class));
    }

    @Test
    void getTransplantById_ShouldReturnTransplant_WhenExists() {
        when(transplantRepository.findById(1L)).thenReturn(Optional.of(testTransplant));

        Optional<Transplant> result = kidneyTransplantService.getTransplantById(1L);

        assertTrue(result.isPresent());
        verify(transplantRepository, times(1)).findById(1L);
    }

    @Test
    void getTransplantById_ShouldReturnEmpty_WhenNotExists() {
        when(transplantRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Transplant> result = kidneyTransplantService.getTransplantById(999L);

        assertFalse(result.isPresent());
        verify(transplantRepository, times(1)).findById(999L);
    }

    @Test
    void getAllTransplants_ShouldReturnAllTransplants() {
        List<Transplant> transplants = Arrays.asList(testTransplant);
        when(transplantRepository.findAll()).thenReturn(transplants);

        List<Transplant> result = kidneyTransplantService.getAllTransplants();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transplantRepository, times(1)).findAll();
    }

    @Test
    void getTransplantsByStatus_ShouldReturnTransplantsWithStatus() {
        List<Transplant> transplants = Arrays.asList(testTransplant);
        when(transplantRepository.findByStatus(Transplant.TransplantStatus.SCHEDULED)).thenReturn(transplants);

        List<Transplant> result = kidneyTransplantService.getTransplantsByStatus(Transplant.TransplantStatus.SCHEDULED);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transplantRepository, times(1)).findByStatus(Transplant.TransplantStatus.SCHEDULED);
    }

    @Test
    void getTransplantsByDonor_ShouldReturnTransplantsForDonor() {
        List<Transplant> transplants = Arrays.asList(testTransplant);
        when(transplantRepository.findByDonor(testDonor)).thenReturn(transplants);

        List<Transplant> result = kidneyTransplantService.getTransplantsByDonor(testDonor);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transplantRepository, times(1)).findByDonor(testDonor);
    }

    @Test
    void getTransplantsByRecipient_ShouldReturnTransplantsForRecipient() {
        List<Transplant> transplants = Arrays.asList(testTransplant);
        when(transplantRepository.findByRecipient(testRecipient)).thenReturn(transplants);

        List<Transplant> result = kidneyTransplantService.getTransplantsByRecipient(testRecipient);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transplantRepository, times(1)).findByRecipient(testRecipient);
    }

    @Test
    void getTransplantsBySurgeon_ShouldReturnTransplantsForSurgeon() {
        List<Transplant> transplants = Arrays.asList(testTransplant);
        when(transplantRepository.findBySurgeon(testSurgeon)).thenReturn(transplants);

        List<Transplant> result = kidneyTransplantService.getTransplantsBySurgeon(testSurgeon);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transplantRepository, times(1)).findBySurgeon(testSurgeon);
    }

    @Test
    void updateTransplant_ShouldUpdateAndReturnTransplant_WhenExists() {
        Transplant updatedDetails = new Transplant();
        updatedDetails.setDonor(testDonor);
        updatedDetails.setRecipient(testRecipient);
        updatedDetails.setScheduledDate(LocalDateTime.now().plusDays(7));
        updatedDetails.setStatus(Transplant.TransplantStatus.IN_PROGRESS);
        updatedDetails.setSurgeon(testSurgeon);
        updatedDetails.setHospital("General Hospital");
        updatedDetails.setPreOperativeNotes("Pre-op notes");
        updatedDetails.setPostOperativeNotes("Post-op notes");
        updatedDetails.setComplications("None");
        updatedDetails.setIsSuccessful(true);

        when(transplantRepository.findById(1L)).thenReturn(Optional.of(testTransplant));
        when(transplantRepository.save(any(Transplant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transplant result = kidneyTransplantService.updateTransplant(1L, updatedDetails);

        assertNotNull(result);
        assertEquals(Transplant.TransplantStatus.IN_PROGRESS, result.getStatus());
        assertNotNull(result.getUpdatedAt());
        verify(transplantRepository, times(1)).findById(1L);
        verify(transplantRepository, times(1)).save(any(Transplant.class));
    }

    @Test
    void updateTransplant_ShouldReturnNull_WhenNotExists() {
        Transplant updatedDetails = new Transplant();
        when(transplantRepository.findById(999L)).thenReturn(Optional.empty());

        Transplant result = kidneyTransplantService.updateTransplant(999L, updatedDetails);

        assertNull(result);
        verify(transplantRepository, times(1)).findById(999L);
        verify(transplantRepository, never()).save(any(Transplant.class));
    }

    @Test
    void deleteTransplant_ShouldReturnTrue_WhenExists() {
        when(transplantRepository.existsById(1L)).thenReturn(true);

        boolean result = kidneyTransplantService.deleteTransplant(1L);

        assertTrue(result);
        verify(transplantRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteTransplant_ShouldReturnFalse_WhenNotExists() {
        when(transplantRepository.existsById(999L)).thenReturn(false);

        boolean result = kidneyTransplantService.deleteTransplant(999L);

        assertFalse(result);
        verify(transplantRepository, never()).deleteById(any(Long.class));
    }

    @Test
    void getTransplantsByHospital_ShouldReturnTransplants() {
        List<Transplant> transplants = Arrays.asList(testTransplant);
        when(transplantRepository.findByHospital("General Hospital")).thenReturn(transplants);

        List<Transplant> result = kidneyTransplantService.getTransplantsByHospital("General Hospital");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transplantRepository, times(1)).findByHospital("General Hospital");
    }

    @Test
    void getScheduledTransplants_ShouldReturnScheduledTransplants() {
        List<Transplant> transplants = Arrays.asList(testTransplant);
        when(transplantRepository.findScheduledTransplantsOrderByDate()).thenReturn(transplants);

        List<Transplant> result = kidneyTransplantService.getScheduledTransplants();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transplantRepository, times(1)).findScheduledTransplantsOrderByDate();
    }

    @Test
    void getSuccessfulTransplants_ShouldReturnSuccessfulTransplants() {
        List<Transplant> transplants = Arrays.asList(testTransplant);
        when(transplantRepository.findByIsSuccessful(true)).thenReturn(transplants);

        List<Transplant> result = kidneyTransplantService.getSuccessfulTransplants();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transplantRepository, times(1)).findByIsSuccessful(true);
    }

    // ==================== Matching and Statistics Tests ====================

    @Test
    void findCompatibleDonors_ShouldReturnCompatibleDonors() {
        List<Donor> donors = Arrays.asList(testDonor);
        when(donorRepository.findByBloodTypeAndStatus("O+", Donor.DonorStatus.APPROVED)).thenReturn(donors);

        List<Donor> result = kidneyTransplantService.findCompatibleDonors("O+");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(donorRepository, times(1)).findByBloodTypeAndStatus("O+", Donor.DonorStatus.APPROVED);
    }

    @Test
    void findCompatibleRecipients_ShouldReturnCompatibleRecipients() {
        List<Recipient> recipients = Arrays.asList(testRecipient);
        when(recipientRepository.findByBloodTypeAndStatus("O+", Recipient.RecipientStatus.WAITING)).thenReturn(recipients);

        List<Recipient> result = kidneyTransplantService.findCompatibleRecipients("O+");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(recipientRepository, times(1)).findByBloodTypeAndStatus("O+", Recipient.RecipientStatus.WAITING);
    }

    @Test
    void getDonorCountByStatus_ShouldReturnCorrectCount() {
        when(donorRepository.countByStatus(Donor.DonorStatus.APPROVED)).thenReturn(5L);

        long result = kidneyTransplantService.getDonorCountByStatus(Donor.DonorStatus.APPROVED);

        assertEquals(5L, result);
        verify(donorRepository, times(1)).countByStatus(Donor.DonorStatus.APPROVED);
    }

    @Test
    void getRecipientCountByStatus_ShouldReturnCorrectCount() {
        when(recipientRepository.countByStatus(Recipient.RecipientStatus.WAITING)).thenReturn(3L);

        long result = kidneyTransplantService.getRecipientCountByStatus(Recipient.RecipientStatus.WAITING);

        assertEquals(3L, result);
        verify(recipientRepository, times(1)).countByStatus(Recipient.RecipientStatus.WAITING);
    }

    @Test
    void getTransplantCountByStatus_ShouldReturnCorrectCount() {
        when(transplantRepository.countByStatus(Transplant.TransplantStatus.COMPLETED)).thenReturn(10L);

        long result = kidneyTransplantService.getTransplantCountByStatus(Transplant.TransplantStatus.COMPLETED);

        assertEquals(10L, result);
        verify(transplantRepository, times(1)).countByStatus(Transplant.TransplantStatus.COMPLETED);
    }

    @Test
    void getSuccessfulTransplantCount_ShouldReturnCorrectCount() {
        when(transplantRepository.countSuccessfulTransplants()).thenReturn(8L);

        long result = kidneyTransplantService.getSuccessfulTransplantCount();

        assertEquals(8L, result);
        verify(transplantRepository, times(1)).countSuccessfulTransplants();
    }
}
