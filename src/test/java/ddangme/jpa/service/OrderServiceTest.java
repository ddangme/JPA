package ddangme.jpa.service;

import ddangme.jpa.domain.Address;
import ddangme.jpa.domain.Member;
import ddangme.jpa.domain.Order;
import ddangme.jpa.domain.OrderStatus;
import ddangme.jpa.domain.item.Book;
import ddangme.jpa.domain.item.Item;
import ddangme.jpa.exception.NotEnoughStockException;
import ddangme.jpa.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    void 상품주문() throws Exception {
        // Given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10_000, 10);
        int orderCount = 2;

        // When
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // Then
        Order getOrder = orderRepository.findOne(orderId);

        assertThat(getOrder.getOrderStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(getOrder.getOrderItems().size()).isEqualTo(1);
        assertThat(getOrder.getTotalPrice()).isEqualTo(10_000 * 2);
        assertThat(item.getStockQuantity()).isEqualTo(8);
    }

    @Test
    void 상품주문_재고수량_초과() throws Exception {
        // Given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10_000, 10);

        int orderCount = 11;

        // When & Then
        assertThatThrownBy(() -> {
            orderService.order(member.getId(), item.getId(), orderCount);
        })
                .isInstanceOf(NotEnoughStockException.class)
                .hasMessage("need more stock");

    }

    @Test
    void 주문_취소() {
        // Given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10_000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // When
        orderService.cancelOrder(orderId);

        // Then
        Order getOrder = orderRepository.findOne(orderId);

        assertThat(getOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(item.getStockQuantity()).isEqualTo(10);

    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123")); em.persist(member);
        return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setStockQuantity(stockQuantity);
        book.setPrice(price);
        em.persist(book);
        return book;
    }


}