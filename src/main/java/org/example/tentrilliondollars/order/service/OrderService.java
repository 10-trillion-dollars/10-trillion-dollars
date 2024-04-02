package org.example.tentrilliondollars.order.service;

import lombok.RequiredArgsConstructor;
import org.example.tentrilliondollars.address.repository.AddressRepository;
import org.example.tentrilliondollars.global.security.UserDetailsImpl;
import org.example.tentrilliondollars.order.dto.OrderDetailResponseDto;
import org.example.tentrilliondollars.order.dto.OrderResponseDto;
import org.example.tentrilliondollars.order.entity.Order;
import org.example.tentrilliondollars.order.entity.OrderDetail;
import org.example.tentrilliondollars.order.entity.OrderState;
import org.example.tentrilliondollars.order.repository.OrderDetailRepository;
import org.example.tentrilliondollars.order.repository.OrderRepository;
import org.example.tentrilliondollars.product.entity.Product;
import org.example.tentrilliondollars.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;

    @Transactional
    public void createOrder(Map<Long,Long> basket,UserDetailsImpl userDetails,Long addressId) throws Exception {
        for(Long key:basket.keySet()){
            if(!checkStock(key,basket.get(key))){throw new Exception("id:"+key+" 수량부족");}
        }
        Order order = new Order(userDetails.getUser(),OrderState.PREPARING,addressRepository.getReferenceById(addressId));
        orderRepository.save(order);
        for(Long key:basket.keySet()){
            OrderDetail orderDetail= new OrderDetail(order,key,basket.get(key),productRepository.getReferenceById(key).getPrice(),productRepository.getReferenceById(key).getName());
            orderDetailRepository.save(orderDetail);
            updateStock(key,basket.get(key));
        }

    }
    public List<OrderDetailResponseDto> getOrderDetailList(Long orderId){
        List<OrderDetail> listOfOrderedProducts = orderDetailRepository.findOrderDetailsByOrder(orderRepository.getReferenceById(orderId));
        return listOfOrderedProducts.stream().map(OrderDetailResponseDto::new).toList();
    }
    @Transactional
    public void deleteOrder(Long orderId){
        orderDetailRepository.deleteAll(orderDetailRepository.findOrderDetailsByOrder(orderRepository.getReferenceById(orderId)));
        orderRepository.delete(orderRepository.getReferenceById(orderId));
    }

    public boolean checkUser(UserDetailsImpl userDetails,Long orderId){
        return Objects.equals(userDetails.getUser().getId(), orderRepository.getReferenceById(orderId).getUser().getId());
    }

    public void updateStock(Long productId,Long quantity){
        Product product =  productRepository.getReferenceById(productId);
        product.updateStockAfterOrder(quantity);

    }

    public boolean checkStock(Long productId,Long quantity){
        return productRepository.getReferenceById(productId).getStock() - quantity >= 0;
    }

    public List<OrderResponseDto> getOrderList(UserDetailsImpl userDetails){
        List<Order> orderList = orderRepository.findOrdersByUser(userDetails.getUser());
        return orderList.stream().map(OrderResponseDto::new).toList();
    }

}
