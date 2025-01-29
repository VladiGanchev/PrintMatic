package com.example.printmatic.service;

import com.example.printmatic.dto.request.OrderCreationDTO;
import com.example.printmatic.dto.request.ServicePriceDTO;
import com.example.printmatic.dto.response.MultipleServicePriceResultDTO;
import com.example.printmatic.dto.response.ServicePriceResultDTO;
import com.example.printmatic.enums.*;
import com.example.printmatic.model.ServicePriceEntity;
import com.example.printmatic.model.UserEntity;
import com.example.printmatic.repository.ServicePriceRepository;
import com.example.printmatic.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicePriceServiceTest {

    @InjectMocks
    private ServicePriceService servicePriceService;

    @Mock
    private ServicePriceRepository servicePriceRepository;

    @Mock
    private UserRepository userRepository;

    private OrderCreationDTO createOrderCreationDTO() {
        return new OrderCreationDTO("Test title", 1, false, PageSize.A4, PaperType.REGULAR_MATE,
                "Test additional info", DeadlineEnum.THREE_DAYS, "test_file.url", 10, 5, 5);
    }

    private static class PrincipalImpl implements Principal {
        @Override
        public String getName() {
            return "test@test.test";
        }
    }

    @Test
    void updateServicePrice_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        ServicePriceDTO dto = new ServicePriceDTO("A4", "VALUE", 0.15);

        ServicePriceResultDTO result = servicePriceService.updateServicePrice(1L, dto, new PrincipalImpl());

        assertEquals(404, result.getStatus());
        assertEquals("User not found.", result.getMessage());
    }

    @Test
    void updateServicePrice_ServiceNotFound() {
        UserEntity user = new UserEntity();
        ServicePriceDTO dto = new ServicePriceDTO("A4", "VALUE", 0.15);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(servicePriceRepository.findById(1L)).thenReturn(Optional.empty());

        ServicePriceResultDTO result = servicePriceService.updateServicePrice(1L, dto, new PrincipalImpl());

        assertEquals(404, result.getStatus());
        assertEquals("Service Price entity with this ID was not found.", result.getMessage());
    }

    @Test
    void updateServicePrice_InvalidInput() {
        UserEntity user = new UserEntity();
        ServicePriceDTO dto = new ServicePriceDTO(null, null, null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        ServicePriceResultDTO result = servicePriceService.updateServicePrice(1L, dto, new PrincipalImpl());

        assertEquals(400, result.getStatus());
        assertEquals("Empty fields in request.", result.getMessage());
    }

    @Test
    void updateServicePrice_Success() {
        UserEntity user = new UserEntity();
        ServicePriceEntity service = new ServicePriceEntity(1L, ServiceEnum.A4, PriceType.VALUE, 0.10);
        ServicePriceDTO dto = new ServicePriceDTO("A4", "VALUE", 0.15);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(servicePriceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(servicePriceRepository.save(any(ServicePriceEntity.class))).thenReturn(service);

        ServicePriceResultDTO result = servicePriceService.updateServicePrice(1L, dto, new PrincipalImpl());

        assertEquals(200, result.getStatus());
        assertEquals("Successfully updated service price.", result.getMessage());
    }

    @Test
    void getAllServicePrices_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        MultipleServicePriceResultDTO result = servicePriceService.getAllServicePrices(new PrincipalImpl());

        assertEquals(404, result.getStatus());
        assertEquals("User not found.", result.getMessage());
    }

    @Test
    void getAllServicePrices_NoServicesFound() {
        UserEntity user = new UserEntity();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(servicePriceRepository.findAll()).thenReturn(List.of());

        MultipleServicePriceResultDTO result = servicePriceService.getAllServicePrices(new PrincipalImpl());

        assertEquals(404, result.getStatus());
        assertEquals("No service price entities found.", result.getMessage());
    }

    @Test
    void getAllServicePrices_Success() {
        UserEntity user = new UserEntity();
        ServicePriceEntity service1 = new ServicePriceEntity(1L, ServiceEnum.A4, PriceType.VALUE, 0.15);
        ServicePriceEntity service2 = new ServicePriceEntity(2L, ServiceEnum.A3, PriceType.VALUE, 0.30);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(servicePriceRepository.findAll()).thenReturn(List.of(service1, service2));

        MultipleServicePriceResultDTO result = servicePriceService.getAllServicePrices(new PrincipalImpl());

        assertEquals(200, result.getStatus());
        assertEquals(2, result.getServicePriceSet().size());
    }

    @Test
    void seedServices_DataAlreadyPresent() {
        when(servicePriceRepository.count()).thenReturn(5L);

        servicePriceService.seedServices();

        verify(servicePriceRepository, never()).saveAll(anyList());
    }

    @Test
    void seedServices_NoDataPresent() {
        when(servicePriceRepository.count()).thenReturn(0L);

        servicePriceService.seedServices();

        verify(servicePriceRepository, atLeastOnce()).saveAll(anyList());
    }

    @Test
    void calculateOrderPrice_MissingServices() {
        OrderCreationDTO order = createOrderCreationDTO();

        when(servicePriceRepository.findAll()).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> servicePriceService.calculateOrderPrice(order, 2));
    }

    @Test
    void calculateOrderPrice_ZeroPages() {
        OrderCreationDTO order = createOrderCreationDTO();
        order.setTotalPages(0);
        order.setGrayscalePages(0);
        order.setColorfulPages(0);
        ServicePriceEntity pagePrice = new ServicePriceEntity(1L, ServiceEnum.A4, PriceType.VALUE, 0.15);
        ServicePriceEntity grayscaleMultiplier = new ServicePriceEntity(2L, ServiceEnum.GRAYSCALE, PriceType.MULTIPLIER, 1.0);
        ServicePriceEntity colorMultiplier = new ServicePriceEntity(3L, ServiceEnum.COLOR, PriceType.MULTIPLIER, 4.0);
        ServicePriceEntity paperMultiplier = new ServicePriceEntity(4L, ServiceEnum.REGULAR_MATE, PriceType.MULTIPLIER, 1.0);
        ServicePriceEntity deadlineMultiplier = new ServicePriceEntity(5L, ServiceEnum.THREE_DAYS, PriceType.MULTIPLIER, 1.2);

        when(servicePriceRepository.findAll()).thenReturn(List.of(pagePrice, grayscaleMultiplier, colorMultiplier, paperMultiplier, deadlineMultiplier));

        Pair<BigDecimal, String> result = servicePriceService.calculateOrderPrice(order, 2);

        assertEquals(BigDecimal.ZERO, result.getLeft().stripTrailingZeros());
        assertNotNull(result.getRight());
    }

    @Test
    void calculateOrderPrice_Success() {
        OrderCreationDTO order = createOrderCreationDTO();
        ServicePriceEntity pagePrice = new ServicePriceEntity(1L, ServiceEnum.A4, PriceType.VALUE, 0.15);
        ServicePriceEntity grayscaleMultiplier = new ServicePriceEntity(2L, ServiceEnum.GRAYSCALE, PriceType.MULTIPLIER, 1.0);
        ServicePriceEntity colorMultiplier = new ServicePriceEntity(3L, ServiceEnum.COLOR, PriceType.MULTIPLIER, 4.0);
        ServicePriceEntity paperMultiplier = new ServicePriceEntity(4L, ServiceEnum.REGULAR_MATE, PriceType.MULTIPLIER, 1.0);
        ServicePriceEntity deadlineMultiplier = new ServicePriceEntity(5L, ServiceEnum.THREE_DAYS, PriceType.MULTIPLIER, 1.2);

        when(servicePriceRepository.findAll()).thenReturn(List.of(pagePrice, grayscaleMultiplier, colorMultiplier, paperMultiplier, deadlineMultiplier));

        Pair<BigDecimal, String> result = servicePriceService.calculateOrderPrice(order, 2);

        assertNotNull(result);
        assertTrue(result.getLeft().compareTo(BigDecimal.ZERO) > 0);
        assertNotNull(result.getRight());
    }
}

