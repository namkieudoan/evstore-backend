package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderStatus;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @Override
    public Order createOrder(OrderDTO orderDTO) throws Exception {
        // check: is User_id exists ?
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(()-> new DataNotFoundException(
                        "Cannot find user with id: " + orderDTO.getUserId()));
        // convert OrderDTO -> Order
        //cách 2: dùng modelMapper
        //user modelMapper, tạo 1 luồng ánh xạ riêng đ kiểm soát việc ánh xạ
        modelMapper.typeMap(OrderDTO.class,Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        // cập nhật các trường của đơn hàng từ orderDTO
        Order order = new Order();
        modelMapper.map(orderDTO,order);
        order.setUser(user);
        order.setOrderDate(new Date()); // lấy time hiện tại
        order.setStatus(OrderStatus.PENDING);
        order.setActive(true);
        //check shippingDate phải >= ngày hôm nay
        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now(): orderDTO.getShippingDate();
        if(shippingDate.isBefore(LocalDate.now())){
            throw new DataNotFoundException("Date must be at least today !");
        }
        order.setShippingDate(shippingDate);
        orderRepository.save(order);
        // convert order -> orderResponse
        return order;
    }

    @Override
    public Order getOrderById(Long orderId) throws DataNotFoundException {
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new DataNotFoundException(
                "Can not found orderID"));
        return order;
    }
    @Override
    public List<Order> getOrdersByUserId(Long userId) throws DataNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(()-> new DataNotFoundException(
                "Cant not find user with userId: " + userId));
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders;
    }
    @Override
    public Order updateOrder(Long orderId, OrderDTO orderDTO) throws DataNotFoundException {
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new DataNotFoundException(
                "Cannot find order with id = " + orderId));
        User existingUser = userRepository.findById(orderDTO.getUserId()).orElseThrow(()-> new DataNotFoundException(
                "Cant find user with userID = " + orderDTO.getUserId()));
        // Tạo một luồng ánh xạ riêng để kiểm soát việc ánh xạ
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));

        // Update các trường của các đơn hàng của orderDTO
        modelMapper.map(orderDTO,order);
        order.setUser(existingUser);
        orderRepository.save(order);
        return order;
    };

    @Override
    public void deleteOrder(Long id){
        Order order = orderRepository.findById(id).orElse(null);
        // use soft-delete
        if(order != null){
            order.setActive(false);
            orderRepository.save(order);
        }
    }



}
