package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class OrderDetailService implements IOrderDetailService{
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    @Override
    public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws Exception {
        //check is orderID , productID có tồn tại không ?
        Order order = orderRepository.findById(orderDetailDTO.getOrderID())
                .orElseThrow(()-> new DataNotFoundException(
                "Can not found Order with orderId = " + orderDetailDTO.getOrderID()));
        Product product = productRepository.findById(orderDetailDTO.getProductId()).orElseThrow(()-> new DataNotFoundException(
                "Can not found product with productId =" + orderDetailDTO.getProductId()));

        //tạo orderDetail
        OrderDetail orderDetail = OrderDetail.builder()
                .price(orderDetailDTO.getPrice())
                .color(orderDetailDTO.getColor())
                .totalMoney(orderDetailDTO.getTotalMoney())
                .numberOfProducts(orderDetailDTO.getNumberOfProducts())
                .order(order)
                .product(product)
                .build();
        //save
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail getOrderDetail(Long id) throws Exception {
        return orderDetailRepository.findById(id)
                .orElseThrow(()->new DataNotFoundException("Cant found orderDetail with id = " + id));
    }

    @Override
    public OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) throws Exception {
        //check is orderID , productID có tồn tại không ?
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(()-> new DataNotFoundException(
                        "Can not found OrderDetail with orderId = " + id));
        Order existingOrder = orderRepository.findById(orderDetailDTO.getOrderID())
                .orElseThrow(()-> new DataNotFoundException(
                        "Can not found Order with orderId = " + orderDetailDTO.getOrderID()));
        Product existingProduct = productRepository.findById(orderDetailDTO.getProductId()).orElseThrow(()-> new DataNotFoundException(
                "Can not found product with productId =" + orderDetailDTO.getProductId()));
        //update orderDetail
        existingOrderDetail.setPrice(orderDetailDTO.getPrice());
        existingOrderDetail.setColor(orderDetailDTO.getColor());
        existingOrderDetail.setPrice(orderDetailDTO.getPrice());
        existingOrderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProducts());
        existingOrderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        existingOrderDetail.setProduct(existingProduct);
        existingOrderDetail.setOrder(existingOrder);
        return orderDetailRepository.save(existingOrderDetail);
    }

    @Override
    public void deleteById(Long id) {
        orderDetailRepository.deleteById(id);
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}
