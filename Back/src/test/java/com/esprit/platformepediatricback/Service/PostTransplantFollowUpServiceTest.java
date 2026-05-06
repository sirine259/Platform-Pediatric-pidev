package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.Repository.PostTransplantFollowUpRepository;
import com.esprit.platformepediatricback.entity.PostTransplantFollowUp;
import com.esprit.platformepediatricback.entity.KidneyTransplant;
import com.esprit.platformepediatricback.entity.Doctor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostTransplantFollowUpServiceTest {

    @Mock
    private PostTransplantFollowUpRepository followUpRepository;

    @Mock
    private KidneyTransplantDetailsService kidneyTransplantService;

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private PostTransplantFollowUpService postTransplantFollowUpService;

    private PostTransplantFollowUp testFollowUp;
    private KidneyTransplant testKidneyTransplant;
    private Doctor testDoctor;
    private PostTransplantFollowUp followUpWithId;

    @BeforeEach
    void setUp() {
        testDoctor = new Doctor();

        testKidneyTransplant = new KidneyTransplant();

        testFollowUp = new PostTransplantFollowUp();
        testFollowUp.setKidneyTransplant(testKidneyTransplant);
        testFollowUp.setDoctor(testDoctor);
        testFollowUp.setFollowUpDate(LocalDateTime.now());
        testFollowUp.setClinicalNotes("Initial follow-up notes");
        testFollowUp.setComplications("None");
        testFollowUp.setCreatinineLevel(1.2);
        testFollowUp.setGfr(85.0);
        testFollowUp.setBloodPressure("120/80");
        testFollowUp.setIsFollowUpComplete(false);
        testFollowUp.setPatientAttended(true);
        testFollowUp.setFollowUpType("ROUTINE");

        followUpWithId = new PostTransplantFollowUp();
        followUpWithId.setKidneyTransplant(testKidneyTransplant);
        followUpWithId.setDoctor(testDoctor);
        followUpWithId.setFollowUpDate(LocalDateTime.now());
    }

    // ==================== Create Tests ====================

    @Test
    void createFollowUp_ShouldSaveAndReturnFollowUp() {
        when(kidneyTransplantService.getKidneyTransplantDetailsById(any(Long.class))).thenReturn(Optional.of(testKidneyTransplant));
        when(doctorService.getDoctorById(any(Long.class))).thenReturn(Optional.of(testDoctor));
        when(followUpRepository.save(any(PostTransplantFollowUp.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostTransplantFollowUp result = postTransplantFollowUpService.createFollowUp(testFollowUp);

        assertNotNull(result);
        verify(followUpRepository, times(1)).save(any(PostTransplantFollowUp.class));
    }

    @Test
    void createFollowUp_ShouldThrowException_WhenKidneyTransplantIsNull() {
        testFollowUp.setKidneyTransplant(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postTransplantFollowUpService.createFollowUp(testFollowUp);
        });

        assertEquals("Kidney transplant is required", exception.getMessage());
        verify(followUpRepository, never()).save(any(PostTransplantFollowUp.class));
    }

    @Test
    void createFollowUp_ShouldThrowException_WhenDoctorIsNull() {
        testFollowUp.setDoctor(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postTransplantFollowUpService.createFollowUp(testFollowUp);
        });

        assertEquals("Doctor is required", exception.getMessage());
        verify(followUpRepository, never()).save(any(PostTransplantFollowUp.class));
    }

    // ==================== Read Tests ====================

    @Test
    void getFollowUpById_ShouldReturnFollowUp_WhenExists() {
        when(followUpRepository.findById(any(Long.class))).thenReturn(Optional.of(testFollowUp));

        Optional<PostTransplantFollowUp> result = postTransplantFollowUpService.getFollowUpById(1L);

        assertTrue(result.isPresent());
        verify(followUpRepository, times(1)).findById(any(Long.class));
    }

    @Test
    void getFollowUpById_ShouldReturnEmpty_WhenNotExists() {
        when(followUpRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        Optional<PostTransplantFollowUp> result = postTransplantFollowUpService.getFollowUpById(999L);

        assertFalse(result.isPresent());
        verify(followUpRepository, times(1)).findById(any(Long.class));
    }

    @Test
    void getAllFollowUps_ShouldReturnAllFollowUps() {
        List<PostTransplantFollowUp> followUps = Arrays.asList(testFollowUp);
        when(followUpRepository.findAll()).thenReturn(followUps);

        List<PostTransplantFollowUp> result = postTransplantFollowUpService.getAllFollowUps();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(followUpRepository, times(1)).findAll();
    }

    // ==================== Update Tests ====================

    @Test
    void updateFollowUp_ShouldUpdateAndReturnFollowUp_WhenExists() {
        PostTransplantFollowUp updatedDetails = new PostTransplantFollowUp();
        updatedDetails.setFollowUpDate(LocalDateTime.now().plusDays(7));
        updatedDetails.setClinicalNotes("Updated notes");
        updatedDetails.setComplications("Minor infection");
        updatedDetails.setCreatinineLevel(1.5);
        updatedDetails.setGfr(75.0);
        updatedDetails.setBloodPressure("130/85");
        updatedDetails.setMedicationAdjustments("Increased immunosuppressants");
        updatedDetails.setIsFollowUpComplete(true);
        updatedDetails.setPatientAttended(true);
        updatedDetails.setFollowUpType("FOLLOW_UP");

        when(followUpRepository.findById(any(Long.class))).thenReturn(Optional.of(testFollowUp));
        when(followUpRepository.save(any(PostTransplantFollowUp.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostTransplantFollowUp result = postTransplantFollowUpService.updateFollowUp(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("Updated notes", result.getClinicalNotes());
        verify(followUpRepository, times(1)).findById(any(Long.class));
        verify(followUpRepository, times(1)).save(any(PostTransplantFollowUp.class));
    }

    @Test
    void updateFollowUp_ShouldThrowException_WhenNotExists() {
        PostTransplantFollowUp updatedDetails = new PostTransplantFollowUp();
        when(followUpRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postTransplantFollowUpService.updateFollowUp(999L, updatedDetails);
        });

        assertEquals("Follow-up not found", exception.getMessage());
        verify(followUpRepository, never()).save(any(PostTransplantFollowUp.class));
    }

    // ==================== Delete Tests ====================

    @Test
    void deleteFollowUp_ShouldDeleteFollowUp_WhenExists() {
        when(followUpRepository.findById(any(Long.class))).thenReturn(Optional.of(testFollowUp));

        postTransplantFollowUpService.deleteFollowUp(1L);

        verify(followUpRepository, times(1)).findById(any(Long.class));
        verify(followUpRepository, times(1)).delete(testFollowUp);
    }

    @Test
    void deleteFollowUp_ShouldThrowException_WhenNotExists() {
        when(followUpRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postTransplantFollowUpService.deleteFollowUp(999L);
        });

        assertEquals("Follow-up not found", exception.getMessage());
        verify(followUpRepository, never()).delete(any(PostTransplantFollowUp.class));
    }

    // ==================== Business Logic Tests ====================

    @Test
    void getFollowUpsByKidneyTransplant_ShouldReturnFollowUps() {
        List<PostTransplantFollowUp> followUps = Arrays.asList(testFollowUp);
        when(followUpRepository.findByKidneyTransplant(any(KidneyTransplant.class))).thenReturn(followUps);

        List<PostTransplantFollowUp> result = postTransplantFollowUpService.getFollowUpsByKidneyTransplant(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(followUpRepository, times(1)).findByKidneyTransplant(any(KidneyTransplant.class));
    }

    @Test
    void getFollowUpsByDoctor_ShouldReturnFollowUps() {
        List<PostTransplantFollowUp> followUps = Arrays.asList(testFollowUp);
        when(followUpRepository.findByDoctor(any(Doctor.class))).thenReturn(followUps);

        List<PostTransplantFollowUp> result = postTransplantFollowUpService.getFollowUpsByDoctor(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(followUpRepository, times(1)).findByDoctor(any(Doctor.class));
    }

    @Test
    void getTodayFollowUps_ShouldReturnTodaysFollowUps() {
        List<PostTransplantFollowUp> followUps = Arrays.asList(testFollowUp);
        when(followUpRepository.findTodayFollowUps(any(LocalDateTime.class))).thenReturn(followUps);

        List<PostTransplantFollowUp> result = postTransplantFollowUpService.getTodayFollowUps();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(followUpRepository, times(1)).findTodayFollowUps(any(LocalDateTime.class));
    }

    @Test
    void getCompletedFollowUps_ShouldReturnCompletedFollowUps() {
        List<PostTransplantFollowUp> followUps = Arrays.asList(testFollowUp);
        when(followUpRepository.findByIsFollowUpComplete(true)).thenReturn(followUps);

        List<PostTransplantFollowUp> result = postTransplantFollowUpService.getCompletedFollowUps();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(followUpRepository, times(1)).findByIsFollowUpComplete(true);
    }

    @Test
    void getPendingFollowUps_ShouldReturnPendingFollowUps() {
        List<PostTransplantFollowUp> followUps = Arrays.asList(testFollowUp);
        when(followUpRepository.findByIsFollowUpComplete(false)).thenReturn(followUps);

        List<PostTransplantFollowUp> result = postTransplantFollowUpService.getPendingFollowUps();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(followUpRepository, times(1)).findByIsFollowUpComplete(false);
    }

    // ==================== Kidney Function Monitoring Tests ====================

    @Test
    void getFollowUpsWithHighCreatinine_ShouldReturnFollowUps() {
        List<PostTransplantFollowUp> followUps = Arrays.asList(testFollowUp);
        when(followUpRepository.findByHighCreatinineLevel(any(Double.class))).thenReturn(followUps);

        List<PostTransplantFollowUp> result = postTransplantFollowUpService.getFollowUpsWithHighCreatinine(2.0);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(followUpRepository, times(1)).findByHighCreatinineLevel(any(Double.class));
    }

    @Test
    void getFollowUpsWithLowGFR_ShouldReturnFollowUps() {
        List<PostTransplantFollowUp> followUps = Arrays.asList(testFollowUp);
        when(followUpRepository.findByLowGFR(any(Double.class))).thenReturn(followUps);

        List<PostTransplantFollowUp> result = postTransplantFollowUpService.getFollowUpsWithLowGFR(60.0);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(followUpRepository, times(1)).findByLowGFR(any(Double.class));
    }

    @Test
    void getFollowUpsWithComplications_ShouldReturnFollowUps() {
        List<PostTransplantFollowUp> followUps = Arrays.asList(testFollowUp);
        when(followUpRepository.findByComplications()).thenReturn(followUps);

        List<PostTransplantFollowUp> result = postTransplantFollowUpService.getFollowUpsWithComplications();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(followUpRepository, times(1)).findByComplications();
    }

    // ==================== Statistics Tests ====================

    @Test
    void countFollowUpsByKidneyTransplant_ShouldReturnCorrectCount() {
        when(followUpRepository.countByKidneyTransplant(any(KidneyTransplant.class))).thenReturn(5L);

        Long result = postTransplantFollowUpService.countFollowUpsByKidneyTransplant(1L);

        assertEquals(5L, result);
        verify(followUpRepository, times(1)).countByKidneyTransplant(any(KidneyTransplant.class));
    }

    @Test
    void countFollowUpsByDoctor_ShouldReturnCorrectCount() {
        when(followUpRepository.countByDoctor(any(Doctor.class))).thenReturn(10L);

        Long result = postTransplantFollowUpService.countFollowUpsByDoctor(1L);

        assertEquals(10L, result);
        verify(followUpRepository, times(1)).countByDoctor(any(Doctor.class));
    }

    @Test
    void countCompletedFollowUps_ShouldReturnCorrectCount() {
        when(followUpRepository.countCompletedFollowUps()).thenReturn(8L);

        Long result = postTransplantFollowUpService.countCompletedFollowUps();

        assertEquals(8L, result);
        verify(followUpRepository, times(1)).countCompletedFollowUps();
    }

    // ==================== Additional Service Method Tests ====================

    @Test
    void completeFollowUp_ShouldCompleteAndReturnFollowUp_WhenExists() {
        when(followUpRepository.findById(any(Long.class))).thenReturn(Optional.of(testFollowUp));
        when(followUpRepository.save(any(PostTransplantFollowUp.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostTransplantFollowUp result = postTransplantFollowUpService.completeFollowUp(1L, "All good", "Continue meds");

        assertNotNull(result);
        assertTrue(result.getIsFollowUpComplete());
        verify(followUpRepository, times(1)).save(any(PostTransplantFollowUp.class));
    }

    @Test
    void isKidneyFunctionNormal_ShouldReturnCorrectValue() {
        when(followUpRepository.findById(any(Long.class))).thenReturn(Optional.of(testFollowUp));

        boolean result = postTransplantFollowUpService.isKidneyFunctionNormal(1L);

        assertFalse(result); // isFollowUpComplete is false
    }
}
