package com.anapedra.stock_manager.domein.entities;

import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.Stock;
import com.anapedra.stock_manager.domain.enums.StockStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.when;


public class StockUnitTest {

    // A Stock precisa de uma Beer para funcionar corretamente (especialmente checkAndClearIfExpired)
    @Mock
    private Beer mockBeer;
    
    private Stock stock;

    // Constante da regra de negócio (LOW_STOCK_LIMIT = 10)
    private static final int LOW_STOCK_LIMIT = 10;
    
    // --- Configuração Inicial ---

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa os mocks
        
        // Criamos o Stock com o mock da Beer. A quantidade inicial será definida nos testes.
        stock = new Stock(0, mockBeer); 
    }
    
    // --- Testes de Status ---
    
    @Test
    @DisplayName("Deve retornar OUT_OF_STOCK quando a quantidade for zero")
    void updateStatus_shouldSetOutOfStock_whenQuantityIsZero() {
        // Arrange
        stock.setQuantity(0);

        // Act & Assert
        Assertions.assertEquals(StockStatus.OUT_OF_STOCK, stock.getStatus());
    }

    @Test
    @DisplayName("Deve retornar LOW quando a quantidade estiver no limite (<= 10)")
    void updateStatus_shouldSetLow_whenQuantityIsAtLimit() {
        // Arrange
        stock.setQuantity(LOW_STOCK_LIMIT); // 10

        // Act & Assert
        Assertions.assertEquals(StockStatus.LOW, stock.getStatus());
    }

    @Test
    @DisplayName("Deve retornar AVAILABLE quando a quantidade for maior que o limite (> 10)")
    void updateStatus_shouldSetAvailable_whenQuantityIsAboveLimit() {
        // Arrange
        stock.setQuantity(LOW_STOCK_LIMIT + 1); // 11

        // Act & Assert
        Assertions.assertEquals(StockStatus.AVAILABLE, stock.getStatus());
    }

    // --- Testes de Diminuição (Decrease/Saída - Regras de Venda/Perda) ---

    @Test
    @DisplayName("Deve diminuir o estoque com sucesso")
    void decreaseQuantity_shouldReduceStock_whenSufficient() {
        // Arrange
        stock.setQuantity(50);
        int amountToDecrease = 20;

        // Act
        stock.decreaseQuantity(amountToDecrease);

        // Assert
        Assertions.assertEquals(30, stock.getQuantity());
        Assertions.assertEquals(StockStatus.AVAILABLE, stock.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção se a quantidade a diminuir for negativa")
    void decreaseQuantity_shouldThrowException_whenAmountIsNegative() {
        // Arrange
        stock.setQuantity(50);

        // Act & Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            stock.decreaseQuantity(-5);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção se o estoque for insuficiente")
    void decreaseQuantity_shouldThrowException_whenInsufficientStock() {
        // Arrange
        stock.setQuantity(10);
        // Mockar o nome da Beer para a mensagem de erro
        when(mockBeer.getName()).thenReturn("Cerveja Teste"); 

        // Act & Assert
        Assertions.assertThrows(IllegalStateException.class, () -> {
            stock.decreaseQuantity(11);
        }, "Estoque insuficiente para a cerveja: Cerveja Teste. Disponível: 10, Pedido: 11");
    }

    // --- Testes de Aumento (Increase/Entrada - Regras de Reposição) ---

    @Test
    @DisplayName("Deve aumentar o estoque com sucesso")
    void increaseQuantity_shouldAddStock() {
        // Arrange
        stock.setQuantity(5); // LOW_STOCK
        int amountToIncrease = 10;

        // Act
        stock.increaseQuantity(amountToIncrease);

        // Assert
        Assertions.assertEquals(15, stock.getQuantity()); // 5 + 10
        Assertions.assertEquals(StockStatus.AVAILABLE, stock.getStatus()); // Status deve mudar
    }

    @Test
    @DisplayName("Deve lançar exceção se a quantidade a aumentar for negativa")
    void increaseQuantity_shouldThrowException_whenAmountIsNegative() {
        // Arrange
        stock.setQuantity(50);

        // Act & Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            stock.increaseQuantity(-5);
        });
    }
    
    // --- Testes de Expiração (checkAndClearIfExpired) ---
    
    @Test
    @DisplayName("Deve zerar o estoque se a cerveja estiver vencida")
    void checkAndClearIfExpired_shouldClearStock_whenBeerIsExpired() {
        // Arrange
        stock.setQuantity(30);
        when(mockBeer.isExpired()).thenReturn(true);

        // Act
        int removedQuantity = stock.checkAndClearIfExpired();

        // Assert
        Assertions.assertEquals(30, removedQuantity); // Retorna a quantidade zerada
        Assertions.assertEquals(0, stock.getQuantity());
        Assertions.assertEquals(StockStatus.OUT_OF_STOCK, stock.getStatus());
    }

    @Test
    @DisplayName("Não deve alterar o estoque se a cerveja não estiver vencida")
    void checkAndClearIfExpired_shouldNotClearStock_whenBeerIsNotExpired() {
        // Arrange
        stock.setQuantity(30);
        when(mockBeer.isExpired()).thenReturn(false);

        // Act
        int removedQuantity = stock.checkAndClearIfExpired();

        // Assert
        Assertions.assertEquals(0, removedQuantity); // Retorna 0
        Assertions.assertEquals(30, stock.getQuantity());
        Assertions.assertEquals(StockStatus.AVAILABLE, stock.getStatus());
    }

    @Test
    @DisplayName("Deve retornar 0 se o estoque já estiver zerado e a cerveja vencida")
    void checkAndClearIfExpired_shouldReturnZero_whenStockIsAlreadyZeroAndExpired() {
        // Arrange
        stock.setQuantity(0);
        when(mockBeer.isExpired()).thenReturn(true);

        // Act
        int removedQuantity = stock.checkAndClearIfExpired();

        // Assert
        Assertions.assertEquals(0, removedQuantity); // Retorna 0
        Assertions.assertEquals(0, stock.getQuantity());
        Assertions.assertEquals(StockStatus.OUT_OF_STOCK, stock.getStatus());
    }
}