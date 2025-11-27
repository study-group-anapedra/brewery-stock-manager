package com.anapedra.stock_manager.controllers;

import com.anapedra.stock_manager.controllers.BeerController;
import com.anapedra.stock_manager.domain.dtos.BeerFilterDTO;
import com.anapedra.stock_manager.domain.dtos.BeerInsertDTO;
import com.anapedra.stock_manager.domain.dtos.StockInputDTO;
import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.Stock;
import com.anapedra.stock_manager.services.BeerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
public class BeerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BeerService beerService;

    private Beer beerEntity;
    private BeerFilterDTO beerFilterDTO;
    private BeerInsertDTO beerInsertDTO;
    private StockInputDTO stockDTO;
    private Page<BeerFilterDTO> beerPage;

    @BeforeEach
    void setUp() {

        beerEntity = new Beer();
        beerEntity.setId(1L);
        beerEntity.setName("IPA Teste");
        beerEntity.setUrlImg("url/img");
        beerEntity.setAlcoholContent(5.5);
        beerEntity.setPrice(12.0);
        beerEntity.setManufactureDate(LocalDate.of(2024, 1, 1));
        beerEntity.setExpirationDate(LocalDate.of(2025, 1, 1));

        Stock stock = new Stock();
        stock.setId(10L);
        stock.setQuantity(50);
        stock.setBeer(beerEntity);
        beerEntity.setStock(stock);

        beerFilterDTO = new BeerFilterDTO(beerEntity);

        stockDTO = new StockInputDTO(50);

        beerInsertDTO = new BeerInsertDTO(
                "IPA Teste",
                "url/img",
                5.5,
                12.0,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2025, 1, 1),
                stockDTO
        );
        beerInsertDTO.setId(1L);

        beerPage = new PageImpl<>(List.of(beerFilterDTO), PageRequest.of(0, 10), 1);

        Mockito.when(beerService.findById(anyLong()))
                .thenReturn(beerFilterDTO);

        Mockito.when(beerService.findAllBeer(any(), any(), any(), any(), any(), any()))
                .thenReturn(beerPage);

        Mockito.when(beerService.insert(any()))
                .thenReturn(beerInsertDTO);

        Mockito.when(beerService.update(anyLong(), any()))
                .thenReturn(beerInsertDTO);

        Mockito.doNothing().when(beerService).delete(anyLong());
    }


    @Test
    @WithMockUser(roles = "CLIENT")  
    void testFindById_ReturnsBeerFilterDTO() throws Exception {

        mockMvc.perform(get("/beers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("IPA Teste"))
                .andExpect(jsonPath("$.urlImg").value("url/img"))
                .andExpect(jsonPath("$.alcoholContent").value(5.5))
                .andExpect(jsonPath("$.price").value(12.0))
                .andExpect(jsonPath("$.stock").value(50));
    }
    @Test
    @WithMockUser(roles = "CLIENT") // AUTORIZAÇÃO
    void testFindAll_ReturnsPagedBeerFilterDTO() throws Exception {

        mockMvc.perform(get("/beers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("IPA Teste"))
                .andExpect(jsonPath("$.content[0].stock").value(50));
    }





}
