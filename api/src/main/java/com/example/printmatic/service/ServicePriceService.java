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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ServicePriceService {

    private final ServicePriceRepository servicePriceRepository;
    private final UserRepository userRepository;

    public ServicePriceService(ServicePriceRepository servicePriceRepository, UserRepository userRepository) {
        this.servicePriceRepository = servicePriceRepository;
        this.userRepository = userRepository;
    }

    public ServicePriceResultDTO updateServicePrice(Long servicePriceId, ServicePriceDTO servicePriceDTO, Principal principal) {
        // Verify user
        Optional<UserEntity> user = userRepository.findByEmail(principal.getName());
        if (user.isEmpty()) {
            return new ServicePriceResultDTO(-1L, servicePriceDTO.service(), servicePriceDTO.priceType(),
                    servicePriceDTO.price(), 404, "User not found.");
        }

        // Verify that all values are present
        if (Stream.of(servicePriceDTO.service(), servicePriceDTO.priceType(), servicePriceDTO.price()).anyMatch(Objects::isNull)) {
            return new ServicePriceResultDTO(-1L, servicePriceDTO.service(), servicePriceDTO.priceType(),
                    servicePriceDTO.price(), 400, "Empty fields in request.");
        }

        // Find existing service price by ID
        Optional<ServicePriceEntity> optionalServicePriceEntity = servicePriceRepository.findById(servicePriceId);
        if (optionalServicePriceEntity.isEmpty()) {
            return new ServicePriceResultDTO(-1L, servicePriceDTO.service(), servicePriceDTO.priceType(),
                    servicePriceDTO.price(), 404, "Service Price entity with this ID was not found.");
        }

        // Get service price entity
        ServicePriceEntity servicePriceEntity = optionalServicePriceEntity.get();

        // Update service price entity
        servicePriceEntity.setService(ServiceEnum.valueOf(servicePriceDTO.service()));
        servicePriceEntity.setPriceType(PriceType.valueOf(servicePriceDTO.priceType()));
        servicePriceEntity.setServicePrice(servicePriceDTO.price());

        // Update service price entity in the DB
        ServicePriceEntity savedServicePriceEntity = servicePriceRepository.save(servicePriceEntity);

        return new ServicePriceResultDTO(
                savedServicePriceEntity.getId(), savedServicePriceEntity.getService().name(),
                savedServicePriceEntity.getPriceType().name(), savedServicePriceEntity.getServicePrice(),
                200, "Successfully updated service price.");
    }

    public MultipleServicePriceResultDTO getAllServicePrices(Principal principal) {
        // Verify user
        Optional<UserEntity> user = userRepository.findByEmail(principal.getName());
        if (user.isEmpty()) {
            return new MultipleServicePriceResultDTO(Set.of(), 404, "User not found.");
        }

        // Find all service price entities
        List<ServicePriceEntity> servicePriceEntities = servicePriceRepository.findAll();
        if (servicePriceEntities.isEmpty()) {
            return new MultipleServicePriceResultDTO(Set.of(), 404, "No service price entities found.");
        }

        // Map all service price entities to MultipleServicePriceResultDTO.ServicePriceObject entities
        Set<MultipleServicePriceResultDTO.ServicePriceObject> servicePriceObjects = new HashSet<>();
        servicePriceEntities.forEach(servicePriceEntity -> servicePriceObjects.add(
                new MultipleServicePriceResultDTO.ServicePriceObject(
                        servicePriceEntity.getId(), servicePriceEntity.getService().name(),
                        servicePriceEntity.getPriceType().name(), servicePriceEntity.getServicePrice())));

        return new MultipleServicePriceResultDTO(servicePriceObjects, 200, "Found service price entities.");
    }

    public void seedServices() {
        if (servicePriceRepository.count() == 0) {
            // Create default services
            ServicePriceEntity A3 = new ServicePriceEntity(null, ServiceEnum.A3, PriceType.VALUE, 0.30);
            ServicePriceEntity A4 = new ServicePriceEntity(null, ServiceEnum.A4, PriceType.VALUE, 0.15);
            ServicePriceEntity A5 = new ServicePriceEntity(null, ServiceEnum.A5, PriceType.VALUE, 0.10);
            ServicePriceEntity grayscale = new ServicePriceEntity(null, ServiceEnum.GRAYSCALE, PriceType.MULTIPLIER, 1.0);
            ServicePriceEntity color = new ServicePriceEntity(null, ServiceEnum.COLOR, PriceType.MULTIPLIER, 4.0);
            ServicePriceEntity regularMatte = new ServicePriceEntity(null, ServiceEnum.REGULAR_MATE, PriceType.MULTIPLIER, 1.0);
            ServicePriceEntity glossy = new ServicePriceEntity(null, ServiceEnum.GLOSSY, PriceType.MULTIPLIER, 2.0);
            ServicePriceEntity brightWhite = new ServicePriceEntity(null, ServiceEnum.BRIGHT_WHITE, PriceType.MULTIPLIER, 1.5);
            ServicePriceEntity photo = new ServicePriceEntity(null, ServiceEnum.PHOTO, PriceType.MULTIPLIER, 12.0);
            ServicePriceEntity heavyweight = new ServicePriceEntity(null, ServiceEnum.HEAVYWEIGHT, PriceType.MULTIPLIER, 2.5);
            ServicePriceEntity oneHour = new ServicePriceEntity(null, ServiceEnum.ONE_HOUR, PriceType.MULTIPLIER, 1.2);
            ServicePriceEntity oneDay = new ServicePriceEntity(null, ServiceEnum.ONE_DAY, PriceType.MULTIPLIER, 1.1);
            ServicePriceEntity threeDays = new ServicePriceEntity(null, ServiceEnum.THREE_DAYS, PriceType.MULTIPLIER, 1.0);
            ServicePriceEntity oneWeek = new ServicePriceEntity(null, ServiceEnum.ONE_WEEK, PriceType.MULTIPLIER, 0.9);

            // Add service to DB
            servicePriceRepository.saveAll(List.of(A3, A4, A5));
            servicePriceRepository.saveAll(List.of(grayscale, color));
            servicePriceRepository.saveAll(List.of(regularMatte, glossy, brightWhite, photo, heavyweight));
            servicePriceRepository.saveAll(List.of(oneHour, oneDay, threeDays, oneWeek));
        }
    }

    public String generateCalculatedFormulaForPrice(
            OrderCreationDTO order, BigDecimal pricePerPage, Integer numberOfGrayscalePages,
            BigDecimal grayscaleMultiplier, Integer numberOfColorPages, BigDecimal colorMultiplier,
            BigDecimal paperTypeMultiplier, BigDecimal deadlineMultiplier, BigDecimal discount, Integer copies) {
        // Write total price formula
        // grayscalePagesPrice = pricePerPage * numberOfGrayscalePages * grayscaleMultiplier
        // colorPagesPrice = pricePerPage * numberOfColorPages * colorMultiplier
        // finalPrice = (grayscalePagesPrice + colorPagesPrice) * paperTypeMultiplier * deadlineMultiplier * copies * discount

        String pageSizeInBulgarian = ServiceEnum.valueOf(order.getPageSize().name()).bulgarianName();
        String paperTypeInBulgarian = ServiceEnum.valueOf(order.getPaperType().name()).bulgarianName();
        String deadlineInBulgarian = ServiceEnum.valueOf(order.getDeadline().name()).bulgarianName();

        return String.format(
                """
                        (Цена на %s страница * Брой черно-бели страници * Надценка за черно-бели страници
                        + Цена на %s страница * Брой цветни страници * Надценка за цветни страници)
                        * Надценка за тип хартия - %s * Надценка за срок - %s * Брой копия * Oтстъпка
                        (%.0f ст. * %d * %.2f + %.0f ст. * %d * %.2f) * %.2f * %.2f * %d * %.2f""",
                pageSizeInBulgarian, pageSizeInBulgarian,
                paperTypeInBulgarian, deadlineInBulgarian,
                toCents(pricePerPage), numberOfGrayscalePages, grayscaleMultiplier,
                toCents(pricePerPage), numberOfColorPages, colorMultiplier,
                paperTypeMultiplier, deadlineMultiplier, copies, discount
        );
    }

    private BigDecimal toCents(BigDecimal number) {
        return number.multiply(BigDecimal.valueOf(100));
    }

    public Pair<BigDecimal, String> calculateOrderPrice(OrderCreationDTO order, Integer copies) {
        // Get all services
        List<ServicePriceEntity> servicePriceEntities = servicePriceRepository.findAll();
        if (servicePriceEntities.isEmpty()) {
            throw new EntityNotFoundException("Service price entities not found.");
        }

        // Determine price per page
        BigDecimal pricePerPage = getPricePerPage(servicePriceEntities, order.getPageSize());

        // Determine grayscale multiplier
        BigDecimal grayscaleMultiplier = getMultiplierForPageColor(servicePriceEntities, PageColor.GRAYSCALE);

        // Determine colored page multiplier
        BigDecimal colorMultiplier = getMultiplierForPageColor(servicePriceEntities, PageColor.COLOR);

        // Determine paper type multiplier
        BigDecimal paperTypeMultiplier = getMultiplierForPaperType(servicePriceEntities, order.getPaperType());

        // Determine deadline multiplier
        BigDecimal deadlineMultiplier = getMultiplierForDeadline(servicePriceEntities, order.getDeadline());

        // Determine discounts - TODO: implement
        BigDecimal discount = BigDecimal.valueOf(1);

        // Calculate total price
        // grayscalePagesPrice = pricePerPage * numberOfGrayscalePages * grayscaleMultiplier
        // colorPagesPrice = pricePerPage * numberOfColorPages * colorMultiplier
        // finalPrice = (grayscalePagesPrice + colorPagesPrice) * paperTypeMultiplier * deadlineMultiplier * copies * discount

        BigDecimal grayscalePagesPrice = pricePerPage
                .multiply(BigDecimal.valueOf(order.getGrayscalePages()))
                .multiply(grayscaleMultiplier);
        BigDecimal colorPagesPrice = pricePerPage
                .multiply(BigDecimal.valueOf(order.getColorfulPages()))
                .multiply(colorMultiplier);
        BigDecimal allPagesPrice = grayscalePagesPrice.add(colorPagesPrice);

        BigDecimal finalPrice = allPagesPrice
                .multiply(paperTypeMultiplier)
                .multiply(deadlineMultiplier)
                .multiply(BigDecimal.valueOf(copies))
                .multiply(discount);

        String finalPriceFormula = generateCalculatedFormulaForPrice(
                order, pricePerPage, order.getGrayscalePages(), grayscaleMultiplier,
                order.getColorfulPages(), colorMultiplier, paperTypeMultiplier, deadlineMultiplier, discount, copies);

        return Pair.of(finalPrice, finalPriceFormula);
    }

    private BigDecimal getPricePerPage(List<ServicePriceEntity> servicePriceEntities, PageSize pageSize) {
        // Find page size service
        ServicePriceEntity service = findServiceThatEqualsTo(servicePriceEntities, pageSize.name());
        // Determine price
        return BigDecimal.valueOf(service.getServicePrice());
    }

    private BigDecimal getMultiplierForPageColor(List<ServicePriceEntity> servicePriceEntities, PageColor pageColor) {
        // Find page color service
        ServicePriceEntity service = findServiceThatEqualsTo(servicePriceEntities, pageColor.name());
        // Determine multiplier
        return BigDecimal.valueOf(service.getServicePrice());
    }

    private BigDecimal getMultiplierForPaperType(List<ServicePriceEntity> servicePriceEntities, PaperType paperType) {
        // Find paper type service
        ServicePriceEntity service = findServiceThatEqualsTo(servicePriceEntities, paperType.name());
        // Determine multiplier
        return BigDecimal.valueOf(service.getServicePrice());
    }

    private BigDecimal getMultiplierForDeadline(List<ServicePriceEntity> servicePriceEntities, DeadlineEnum deadline) {
        // Find deadline service
        ServicePriceEntity service = findServiceThatEqualsTo(servicePriceEntities, deadline.name());
        // Determine multiplier
        return BigDecimal.valueOf(service.getServicePrice());
    }

    private ServicePriceEntity findServiceThatEqualsTo(List<ServicePriceEntity> servicePriceEntities, String to) {
        // Find page size service
        Set<ServicePriceEntity> services = servicePriceEntities
                .stream()
                .filter(servicePriceEntity -> servicePriceEntity.getService().name().equals(to))
                .collect(Collectors.toSet());
        if (services.size() != 1) {
            throw new IllegalArgumentException("Only one service was expected.");
        }
        return services.iterator().next();
    }
}
