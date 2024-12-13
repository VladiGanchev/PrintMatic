package com.example.printmatic.config;

import com.example.printmatic.dto.response.OrderDTO;
import com.example.printmatic.dto.response.UserOrderDTO;
import com.example.printmatic.model.OrderEntity;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Order;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Configuration
@AllArgsConstructor
public class BeanConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        ZoneId zoneId = ZoneId.of("Europe/Sofia");

        // Converter to adjust time zone
        Converter<LocalDateTime, LocalDateTime> adjustTimeZone = new Converter<>() {
            @Override
            public LocalDateTime convert(MappingContext<LocalDateTime, LocalDateTime> context) {
                if (context.getSource() == null) {
                    return null;
                }
                return context.getSource().atZone(zoneId).toLocalDateTime();
            }
        };

        // Add custom mapping for UserOrderDTO
        modelMapper.typeMap(OrderEntity.class, UserOrderDTO.class)
                .addMappings(mapper -> {
                    mapper.using(adjustTimeZone).map(OrderEntity::getCreatedAt, UserOrderDTO::setCreatedAt);
                    mapper.using(adjustTimeZone).map(OrderEntity::getDeadline, UserOrderDTO::setDeadline);
                });
        modelMapper.typeMap(OrderEntity.class, OrderDTO.class)
                .addMappings(mapper -> {
                    mapper.using(adjustTimeZone).map(OrderEntity::getCreatedAt, OrderDTO::setCreatedAt);
                    mapper.using(adjustTimeZone).map(OrderEntity::getDeadline, OrderDTO::setDeadline);
                });

        return modelMapper;
    }

}
